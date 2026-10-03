package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard board;
    private boolean whiteKingMoved;
    private boolean blackKingMoved;
    private boolean whiteQueensideRookMoved;
    private boolean whiteKingsideRookMoved;
    private boolean blackQueensideRookMoved;
    private boolean blackKingsideRookMoved;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK;

        public TeamColor opponent() {
            return this == WHITE ? BLACK : WHITE;
        }
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }

        Collection<ChessMove> moves = new ArrayList<>();
        for (ChessMove move : piece.pieceMoves(board, startPosition)) {
            if (!moveLeavesKingInCheck(piece.getTeamColor(), move)) {
                moves.add(move);
            }
        }
        addCastlingMoves(piece, startPosition, moves);
        return moves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        Collection<ChessMove> legalMoves = validMoves(move.getStartPosition());
        if (piece == null || piece.getTeamColor() != teamTurn || legalMoves == null || !legalMoves.contains(move)) {
            throw new InvalidMoveException("Invalid move");
        }

        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        applyMove(board, move);
        updateSpecialMoveState(piece, start, end);
        teamTurn = teamTurn.opponent();
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isInCheck(board, teamColor);
    }

    private boolean isInCheck(ChessBoard targetBoard, TeamColor teamColor) {
        ChessPosition kingPosition = findKing(targetBoard, teamColor);
        if (kingPosition == null) {
            return false;
        }

        TeamColor enemyColor = teamColor.opponent();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = targetBoard.getPiece(position);
                if (piece == null || piece.getTeamColor() != enemyColor) {
                    continue;
                }
                for (ChessMove move : piece.pieceMoves(targetBoard, position)) {
                    if (kingPosition.equals(move.getEndPosition())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private ChessPosition findKing(ChessBoard targetBoard, TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = targetBoard.getPiece(position);
                if (piece != null
                        && piece.getTeamColor() == teamColor
                        && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return position;
                }
            }
        }
        return null;
    }

    private boolean hasLegalMove(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece == null || piece.getTeamColor() != teamColor) {
                    continue;
                }
                Collection<ChessMove> moves = validMoves(position);
                if (moves != null && !moves.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void applyMove(ChessBoard target, ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = target.getPiece(start);
        boolean castle = piece.getPieceType() == ChessPiece.PieceType.KING
                && Math.abs(start.getColumn() - end.getColumn()) == 2;

        target.addPiece(start, null);
        if (move.getPromotionPiece() != null) {
            piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }
        target.addPiece(end, piece);

        if (castle) {
            int rookFromColumn = end.getColumn() > start.getColumn() ? 8 : 1;
            int rookToColumn = end.getColumn() > start.getColumn() ? end.getColumn() - 1 : end.getColumn() + 1;
            ChessPosition rookFrom = new ChessPosition(start.getRow(), rookFromColumn);
            ChessPiece rook = target.getPiece(rookFrom);
            target.addPiece(rookFrom, null);
            target.addPiece(new ChessPosition(start.getRow(), rookToColumn), rook);
        }
    }

    private void addCastlingMoves(ChessPiece piece, ChessPosition start, Collection<ChessMove> moves) {
        if (piece.getPieceType() != ChessPiece.PieceType.KING || isInCheck(piece.getTeamColor())) {
            return;
        }

        boolean kingMoved = piece.getTeamColor() == TeamColor.WHITE ? whiteKingMoved : blackKingMoved;
        int homeRow = piece.getTeamColor() == TeamColor.WHITE ? 1 : 8;
        if (kingMoved || start.getRow() != homeRow || start.getColumn() != 5) {
            return;
        }

        boolean queensideRookMoved = piece.getTeamColor() == TeamColor.WHITE
                ? whiteQueensideRookMoved : blackQueensideRookMoved;
        boolean kingsideRookMoved = piece.getTeamColor() == TeamColor.WHITE
                ? whiteKingsideRookMoved : blackKingsideRookMoved;
        tryAddCastle(piece, start, homeRow, 1, 3, 4, queensideRookMoved, moves);
        tryAddCastle(piece, start, homeRow, 8, 7, 6, kingsideRookMoved, moves);
    }

    private void tryAddCastle(ChessPiece king, ChessPosition kingPosition, int row, int rookColumn,
                              int kingDestColumn, int passColumn, boolean rookMoved, Collection<ChessMove> moves) {
        if (rookMoved) {
            return;
        }

        ChessPosition rookPosition = new ChessPosition(row, rookColumn);
        ChessPiece rook = board.getPiece(rookPosition);
        if (rook == null || rook.getPieceType() != ChessPiece.PieceType.ROOK
                || rook.getTeamColor() != king.getTeamColor()) {
            return;
        }

        for (int col = Math.min(rookColumn, kingPosition.getColumn()) + 1;
             col < Math.max(rookColumn, kingPosition.getColumn()); col++) {
            if (board.getPiece(new ChessPosition(row, col)) != null) {
                return;
            }
        }

        if (isSquareAttacked(board, new ChessPosition(row, passColumn), king.getTeamColor().opponent())) {
            return;
        }

        ChessMove castle = new ChessMove(kingPosition, new ChessPosition(row, kingDestColumn), null);
        if (!moveLeavesKingInCheck(king.getTeamColor(), castle)) {
            moves.add(castle);
        }
    }

    private boolean isSquareAttacked(ChessBoard targetBoard, ChessPosition square, TeamColor attacker) {
        int pawnRow = attacker == TeamColor.WHITE ? square.getRow() - 1 : square.getRow() + 1;
        for (int colOffset : new int[]{-1, 1}) {
            int col = square.getColumn() + colOffset;
            if (pawnRow < 1 || pawnRow > 8 || col < 1 || col > 8) {
                continue;
            }
            ChessPiece pawn = targetBoard.getPiece(new ChessPosition(pawnRow, col));
            if (pawn != null && pawn.getTeamColor() == attacker && pawn.getPieceType() == ChessPiece.PieceType.PAWN) {
                return true;
            }
        }

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = targetBoard.getPiece(position);
                if (piece == null || piece.getTeamColor() != attacker || piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                    continue;
                }
                for (ChessMove move : piece.pieceMoves(targetBoard, position)) {
                    if (square.equals(move.getEndPosition())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void updateSpecialMoveState(ChessPiece piece, ChessPosition start, ChessPosition end) {
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (piece.getTeamColor() == TeamColor.WHITE) {
                whiteKingMoved = true;
            } else {
                blackKingMoved = true;
            }
            if (Math.abs(start.getColumn() - end.getColumn()) == 2) {
                int rookColumn = end.getColumn() > start.getColumn() ? 8 : 1;
                markRookMoved(new ChessPosition(start.getRow(), rookColumn));
            }
        }
        if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            markRookMoved(start);
        }
        markRookMoved(end);
    }

    private void markRookMoved(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        if (row == 1 && col == 1) {
            whiteQueensideRookMoved = true;
        } else if (row == 1 && col == 8) {
            whiteKingsideRookMoved = true;
        } else if (row == 8 && col == 1) {
            blackQueensideRookMoved = true;
        } else if (row == 8 && col == 8) {
            blackKingsideRookMoved = true;
        }
    }

    private void resetSpecialMoveState() {
        whiteKingMoved = false;
        blackKingMoved = false;
        whiteQueensideRookMoved = false;
        whiteKingsideRookMoved = false;
        blackQueensideRookMoved = false;
        blackKingsideRookMoved = false;
    }

    private boolean moveLeavesKingInCheck(TeamColor teamColor, ChessMove move) {
        ChessBoard copy = copyBoard();
        applyMove(copy, move);
        return isInCheck(copy, teamColor);
    }

    private ChessBoard copyBoard() {
        ChessBoard copy = new ChessBoard();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null) {
                    copy.addPiece(position, piece);
                }
            }
        }
        return copy;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && !hasLegalMove(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && !hasLegalMove(teamColor);
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
        resetSpecialMoveState();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn
                && Objects.equals(board, chessGame.board)
                && whiteKingMoved == chessGame.whiteKingMoved
                && blackKingMoved == chessGame.blackKingMoved
                && whiteQueensideRookMoved == chessGame.whiteQueensideRookMoved
                && whiteKingsideRookMoved == chessGame.whiteKingsideRookMoved
                && blackQueensideRookMoved == chessGame.blackQueensideRookMoved
                && blackKingsideRookMoved == chessGame.blackKingsideRookMoved;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board, whiteKingMoved, blackKingMoved,
                whiteQueensideRookMoved, whiteKingsideRookMoved, blackQueensideRookMoved, blackKingsideRookMoved);
    }
}

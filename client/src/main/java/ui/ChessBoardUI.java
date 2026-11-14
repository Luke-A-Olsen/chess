package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class ChessBoardUI {
    private static final String LIGHT_SQUARE = SET_BG_COLOR_LIGHT_GREY;
    private static final String DARK_SQUARE = SET_BG_COLOR_DARK_GREY;
    private static final String WHITE_PIECE = SET_TEXT_COLOR_RED;
    private static final String BLACK_PIECE = SET_TEXT_COLOR_BLUE;

    public static String drawWhitePerspective(ChessBoard board) {
        StringBuilder output = new StringBuilder();
        output.append("\n");
        output.append(headerLetters(true));
        for (int row = 8; row >= 1; row--) {
            output.append(" ").append(row).append(" ");
            for (int col = 1; col <= 8; col++) {
                output.append(square(board, row, col));
            }
            output.append(RESET_BG_COLOR).append(RESET_TEXT_COLOR);
            output.append(" ").append(row).append("\n");
        }
        output.append(headerLetters(true));
        output.append(RESET_BG_COLOR).append(RESET_TEXT_COLOR);
        return output.toString();
    }

    private static String headerLetters(boolean whitePerspective) {
        StringBuilder letters = new StringBuilder("   ");
        if (whitePerspective) {
            for (char file = 'a'; file <= 'h'; file++) {
                letters.append(" ").append(file).append(" ");
            }
        }
        letters.append("\n");
        return letters.toString();
    }

    private static String square(ChessBoard board, int row, int col) {
        boolean light = (row + col) % 2 == 1;
        String background = light ? LIGHT_SQUARE : DARK_SQUARE;
        ChessPiece piece = board.getPiece(new ChessPosition(row, col));
        return background + pieceText(piece);
    }

    private static String pieceText(ChessPiece piece) {
        if (piece == null) {
            return EMPTY;
        }
        String color = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_PIECE : BLACK_PIECE;
        return color + unicodeFor(piece) + RESET_TEXT_COLOR;
    }

    private static String unicodeFor(ChessPiece piece) {
        boolean white = piece.getTeamColor() == ChessGame.TeamColor.WHITE;
        return switch (piece.getPieceType()) {
            case KING -> white ? WHITE_KING : BLACK_KING;
            case QUEEN -> white ? WHITE_QUEEN : BLACK_QUEEN;
            case BISHOP -> white ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT -> white ? WHITE_KNIGHT : BLACK_KNIGHT;
            case ROOK -> white ? WHITE_ROOK : BLACK_ROOK;
            case PAWN -> white ? WHITE_PAWN : BLACK_PAWN;
        };
    }
}

package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator
{
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition start)
    {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece me = board.getPiece(start);
        int direction = me.getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : -1;
        int startRow = me.getTeamColor() == ChessGame.TeamColor.WHITE ? 2 : 7;
        int promotionRow = me.getTeamColor() == ChessGame.TeamColor.WHITE ? 8 : 1;

        int forwardRow = start.getRow() + direction;
        int col = start.getColumn();

        if (forwardRow >= 1 && forwardRow <= 8)
        {
            ChessPosition oneForward = new ChessPosition(forwardRow, col);
            if (board.getPiece(oneForward) == null)
            {
                pawnPromotion(moves, start, oneForward, promotionRow);

                if (start.getRow() == startRow)
                {
                    ChessPosition twoForward = new ChessPosition(forwardRow + direction, col);
                    if (board.getPiece(twoForward) == null)
                    {
                        pawnPromotion(moves, start, twoForward, promotionRow);
                    }
                }
            }

            for (int captureCol : new int[]{col - 1, col + 1})
            {
                if (captureCol >= 1 && captureCol <= 8)
                {
                    ChessPosition capturePos = new ChessPosition(forwardRow, captureCol);
                    ChessPiece target = board.getPiece(capturePos);
                    if (target != null && target.getTeamColor() != me.getTeamColor())
                    {
                        pawnPromotion(moves, start, capturePos, promotionRow);
                    }
                }
            }
        }

        return moves;
    }

    private void pawnPromotion(Collection<ChessMove> moves, ChessPosition start, ChessPosition end, int promotionRow)
    {
        if (end.getRow() == promotionRow)
        {
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.KNIGHT));
        }
        else
        {
            moves.add(new ChessMove(start, end, null));
        }
    }
}

package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator
{
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition start)
    {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece me = board.getPiece(start);
        int[][] offsets = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};

        for (int[] offset : offsets)
        {
            int row = start.getRow() + offset[0];
            int col = start.getColumn() + offset[1];

            if (row >= 1 && row <= 8 && col >= 1 && col <= 8)
            {
                ChessPosition end = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(end);

                if (target == null || target.getTeamColor() != me.getTeamColor())
                {
                    moves.add(new ChessMove(start, end, null));
                }
            }
        }

        return moves;
    }
}

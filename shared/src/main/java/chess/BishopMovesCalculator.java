package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator
{
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition start)
    {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece me = board.getPiece(start);
        int[][] directions = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};

        for (int[] dir : directions)
        {
            int row = start.getRow() + dir[0];
            int col = start.getColumn() + dir[1];

            while (row >= 1 && row <= 8 && col >= 1 && col <= 8)
            {
                ChessPosition end = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(end);

                if (target == null)
                {
                    moves.add(new ChessMove(start, end, null));
                }
                else
                {
                    if (target.getTeamColor() != me.getTeamColor())
                    {
                        moves.add(new ChessMove(start, end, null));
                    }
                    break;
                }

                row += dir[0];
                col += dir[1];
            }
        }

        return moves;
    }
}

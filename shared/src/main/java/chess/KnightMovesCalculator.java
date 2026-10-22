package chess;

import java.util.Collection;

public class KnightMovesCalculator
{
    private static final int[][] OFFSETS = {
            {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
            {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
    };

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition start)
    {
        return DirectionMoves.step(board, start, OFFSETS);
    }
}

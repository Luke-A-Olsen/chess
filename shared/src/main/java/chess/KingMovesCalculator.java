package chess;

import java.util.Collection;

public class KingMovesCalculator
{
    private static final int[][] OFFSETS = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
    };

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition start)
    {
        return DirectionMoves.step(board, start, OFFSETS);
    }
}

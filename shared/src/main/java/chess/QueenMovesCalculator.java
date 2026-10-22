package chess;

import java.util.Collection;

public class QueenMovesCalculator
{
    private static final int[][] DIRECTIONS = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {-1, 1}, {1, -1}, {-1, -1}, {1, 1}
    };

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition start)
    {
        return DirectionMoves.slide(board, start, DIRECTIONS);
    }
}

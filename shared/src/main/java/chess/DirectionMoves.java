package chess;

import java.util.ArrayList;
import java.util.Collection;

public class DirectionMoves
{
    public static Collection<ChessMove> slide(ChessBoard board, ChessPosition start, int[][] directions)
    {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece movingPiece = board.getPiece(start);

        for (int[] direction : directions)
        {
            int row = start.getRow() + direction[0];
            int column = start.getColumn() + direction[1];

            while (onBoard(row, column))
            {
                boolean blocked = addMove(moves, board, start, movingPiece, row, column);
                if (blocked)
                {
                    break;
                }
                row += direction[0];
                column += direction[1];
            }
        }

        return moves;
    }

    public static Collection<ChessMove> step(ChessBoard board, ChessPosition start, int[][] offsets)
    {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece movingPiece = board.getPiece(start);

        for (int[] offset : offsets)
        {
            int row = start.getRow() + offset[0];
            int column = start.getColumn() + offset[1];
            if (onBoard(row, column))
            {
                addMove(moves, board, start, movingPiece, row, column);
            }
        }

        return moves;
    }

    private static boolean addMove(
            Collection<ChessMove> moves,
            ChessBoard board,
            ChessPosition start,
            ChessPiece movingPiece,
            int row,
            int column)
    {
        ChessPosition end = new ChessPosition(row, column);
        ChessPiece target = board.getPiece(end);
        if (target == null)
        {
            moves.add(new ChessMove(start, end, null));
            return false;
        }
        if (target.getTeamColor() != movingPiece.getTeamColor())
        {
            moves.add(new ChessMove(start, end, null));
        }
        return true;
    }

    private static boolean onBoard(int row, int column)
    {
        return row >= 1 && row <= 8 && column >= 1 && column <= 8;
    }
}

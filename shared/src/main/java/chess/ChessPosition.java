package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int theRow;
    private final int theCol;

    public ChessPosition(int row, int col) {
        theRow = row;
        theCol = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return theRow;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left column
     */
    public int getColumn() {
        return theCol;
    }

    @Override
    public String toString() {
        return String.format("[%d,%d]", theRow, theCol);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return theRow == that.theRow && theCol == that.theCol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(theRow, theCol);
    }
}

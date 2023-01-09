package org.gamecore;


/**
 * (0, 0) position is equal to a1
 * This class represents available position of checker on the checkerboard. It can't be unpleasant position.
 */
public class CheckerBoardPosition {
    static final private int BOARD_SIZE = RussianCheckers.BOARD_SIZE;

    private int row;
    private int column;

    public CheckerBoardPosition(int row, int column)
            throws OutOfBorderException, IllegalCheckerPosition {
        checkPosition(row, column);
        this.row = row;
        this.column = column;
    }

    public CheckerBoardPosition(String posNotation)
            throws IllegalArgumentException, OutOfBorderException, IllegalCheckerPosition {
        if (posNotation.length() > 2){
            throw new IllegalArgumentException("Incorrect format of board position notation " +
                    "(length of notation more than 2)." +
                    " It should be like <[a-hA-H]><1-8>.");
        }
        char columnCh = Character.toLowerCase(posNotation.charAt(0));
        if (columnCh < 'a' || 'h' < columnCh){
            throw new IllegalArgumentException("Incorrect format of board position notation." +
                    " Expected [a-hA-H] got " + Character.toString(columnCh) + "." +
                    " It should be like <[a-hA-H]><1-8>.");
        }
        int rowNum = posNotation.charAt(1) - '0'; // get real number value from char
        if (rowNum < 1 || 8 < rowNum){
            throw new IllegalArgumentException("Incorrect format of board position notation." +
                    " Expected [1-8] got " + Integer.toString(rowNum) + "." +
                    " It should be like <[a-hA-H]><1-8>.");
        }

        // value adaptation to board array index
        rowNum--;
        int columnNum = columnCh - 'a';


        row = rowNum;
        column = columnNum;
    }

    private void checkPosition(int row, int column)
            throws OutOfBorderException, IllegalCheckerPosition {
        if ((row < 0) || (row >= BOARD_SIZE)
                || (column < 0) || (column >= BOARD_SIZE))
            throw new OutOfBorderException("Checker position (" + row + ", " + column + ")" +
                    " can't be out of board");

        if (column % 2 != row % 2)
            throw new IllegalCheckerPosition("Checker position (" + row + ", " + column + ")" +
                    " is not available" );
    }

    public CheckerBoardPosition moveOn(int rowDiff, int columnDiff)
            throws OutOfBorderException, IllegalCheckerPosition {
        int newRowPos = row + rowDiff;
        int newColPos = column + columnDiff;

        checkPosition(newRowPos, newColPos);

        row = newRowPos;
        column = newColPos;
        return this;
    }

    public CheckerBoardPosition moveOn(CheckerBoardPosition pos)
            throws OutOfBorderException, IllegalCheckerPosition {
        return moveOn(pos.row, pos.column);
    }

    public CheckerBoardPosition moveLeftUp(int cells)
            throws OutOfBorderException, IllegalCheckerPosition {
        return moveOn(cells, -cells);
    }

    public CheckerBoardPosition moveLeftUp()
            throws OutOfBorderException, IllegalCheckerPosition {
        return moveOn(1, -1);
    }

    public CheckerBoardPosition moveRightUp(int cells)
            throws OutOfBorderException, IllegalCheckerPosition {
        return moveOn(cells,cells);
    }

    public CheckerBoardPosition moveRightUp()
            throws OutOfBorderException, IllegalCheckerPosition {
        return moveOn(1,1);
    }

    public CheckerBoardPosition moveLeftDown(int cells)
            throws OutOfBorderException, IllegalCheckerPosition {
        return moveOn(-cells, -cells);
    }

    public CheckerBoardPosition moveLeftDown()
            throws OutOfBorderException, IllegalCheckerPosition {
        return moveOn(-1, -1);
    }

    public CheckerBoardPosition moveRightDown(int cells)
            throws OutOfBorderException, IllegalCheckerPosition {
        return moveOn(-cells, cells);
    }

    public CheckerBoardPosition moveRightDown()
            throws OutOfBorderException, IllegalCheckerPosition {
        return moveOn(-1, 1);
    }

    public CheckerBoardPosition add(CheckerBoardPosition pos)
            throws OutOfBorderException, IllegalCheckerPosition {
        return this.copy().moveOn(pos);
    }

    public CheckerBoardPosition reduce(CheckerBoardPosition pos)
            throws OutOfBorderException, IllegalCheckerPosition {
        return this.copy().moveOn(-pos.row, -pos.column);
    }

    public CheckerBoardPosition copy()
            throws OutOfBorderException, IllegalCheckerPosition {
        return new CheckerBoardPosition(row, column);
    }

    public String getPosNotation(){
        return Character.toString('a' + column) + Integer.toString(row + 1);
    }

    public int getRow() {
        return row;
    }

    public int getColumn(){
        return column;
    }

    @Override
    public String toString(){
        return getPosNotation();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CheckerBoardPosition that = (CheckerBoardPosition) o;

        if (row != that.row) return false;
        return column == that.column;
    }

    @Override
    public int hashCode() {
        return (Integer.hashCode(row)*19 + Integer.hashCode(column)*17);
    }

    // Static fields

    public static String getPosNotation(int row, int column){
        return Character.toString('a' + column) + Integer.toString(row + 1);
    }

    public static boolean isPositionCorrect(int row, int column){
        if ((row < 0) || (row >= BOARD_SIZE)
                || (column < 0) || (column >= BOARD_SIZE))
            return false;

        return column % 2 == row % 2;
    }

    public static boolean isOnEdge(CheckerBoardPosition pos){
        return (pos.getRow() == 0 || pos.getRow() == BOARD_SIZE - 1)
        || (pos.getColumn() == 0 || pos.getColumn() == BOARD_SIZE - 1);
    }
}

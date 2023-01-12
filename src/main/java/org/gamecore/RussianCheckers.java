package org.gamecore;

import java.util.*;
import java.util.stream.Stream;

public class RussianCheckers {

    public class Checker{
        public enum CheckerType{
            Checker,
            Queen;

            @Override
            public String toString(){
                switch (this){
                    case Checker -> {return "C";}
                    case Queen -> {return  "Q";}
                }
                return "undefined checker type string transformation";
            }
        }
        public enum CheckerColor{
            Black,
            White;

            @Override
            public String toString(){
                switch (this){
                    case Black -> {return "b";}
                    case White -> {return "w";}
                }
                return "undefined checker color string transformation";
            }

            public CheckerColor getOpposite(){
                if (this == CheckerColor.White) {
                    return Black;
                }
                return White;
            }
        }
        private CheckerType type;
        final private CheckerColor color;
        private CheckerBoardPosition position;
        private boolean isStepPassed = false;
        private boolean isKilled = false;

        public Checker(CheckerType type, CheckerColor color, CheckerBoardPosition position){
            this.type = type;
            this.color = color;
            this.position = position;
            board[position.getRow()][position.getColumn()] = this;
        }

        public Checker(CheckerType type, CheckerColor color, int row, int column)
                throws OutOfBorderException, IllegalCheckerPosition {
            this(type, color, new CheckerBoardPosition(row, column));
        }

        public Checker(CheckerColor color, int row, int column)
                throws OutOfBorderException, IllegalCheckerPosition {
            this(CheckerType.Checker, color, new CheckerBoardPosition(row, column));
        }

        public String getCurrentPosNotation(){
            return position.getPosNotation();
        }

        public void killStub(){
            isKilled = true;
        }

        public void killUnStub(){
            isKilled = false;
        }

        public boolean isKilled(){
            return isKilled;
        }

        public void hide(){
            setCell(position, null);
        }

        public void show() throws GameStateError{
            if (getCheckerAt(position) != null) {
                throw new GameStateError("We can't show checker on occupied cell by other one");
            }

            setCell(position, this);
        }

        public CheckerBoardPosition getCurrentPosition(){
            return position;
        }

        public List<CheckerGameStep> getAvailableSteps()
                throws OutOfBorderException, IllegalCheckerPosition, GameStateError,
                CheckerGameStep.CheckerStepApplyException {

            if (turnOf != color)
                throw new IllegalStateException("Now is " + turnOf.name() + " turn" +
                        " but " + color.name() + " makes the step.");

            boolean isCheckerKilled = false;
            List<CheckerGameStep> steps = new ArrayList<>();
            hide();
            switch (type){
                case Checker -> {
                    // set forward direction
                    int forwardRow = (color == CheckerColor.White) ? 1 : -1;
                    int columnStep = 1;

                    for (int i = 0; i < 2; i++){
                        // iterate direction
                        if (i == 1)
                            columnStep = -columnStep;

                        // exceptions will be if position can't be existed
                        try {
                            var nextPos = position.copy();
                            nextPos.moveOn(forwardRow, columnStep);

                            Checker nextCellChecker = getCheckerAt(nextPos);

                            // if only step without kills
                            if (nextCellChecker == null && !isCheckerKilled) {
                                var step = new CheckerGameStep(this, position, nextPos);
                                if (color == CheckerColor.White && nextPos.getRow() == BOARD_SIZE-1)
                                    step.transformedToQueen();
                                else if (color == CheckerColor.Black && nextPos.getRow() == 0){
                                    step.transformedToQueen();
                                }
                                steps.add(step);
                            } else { // if we can kill someone
                                nextPos.moveOn(forwardRow, columnStep);
                                // if we can jump and someone is not our player
                                if (getCheckerAt(nextPos) == null && nextCellChecker != null
                                        && nextCellChecker.color != color) {
                                    var step = new CheckerGameStep(this, position, nextPos);
                                    step.setKilledChecker(nextCellChecker);
                                    isCheckerKilled = true;
                                    steps.add(step);
                                }
                            }
                        }
                        catch (Throwable ignored){}
                    }
                    // kill by back step
                    var backwardRow = -forwardRow;
                    for (int i = 0; i < 2; i++){
                        if (i == 1)
                            columnStep = -columnStep;

                        try {
                            var nextPos = position.copy();
                            nextPos.moveOn(backwardRow, columnStep);

                            Checker nextCellChecker = getCheckerAt(nextPos);

                            if (nextCellChecker != null && !nextCellChecker.color.equals(color)){
                                nextPos.moveOn(backwardRow, columnStep);
                                if (getCheckerAt(nextPos) == null) {
                                    var step = new CheckerGameStep(this, position, nextPos);
                                    step.setKilledChecker(nextCellChecker);
                                    isCheckerKilled = true;
                                    steps.add(step);
                                }
                            }
                        }
                        catch (Throwable ignored){}
                    }
                }
                case Queen -> {
                    for (int i = 0; i < 4; i++){
                        // iterate incrementer to go through the all 4 sides
                        int rowStep = switch (i){
                            case 0, 1 -> 1;
                            default -> -1;
                        };
                        int columnStep = switch (i){
                            case 0, 2 -> 1;
                            default -> -1;
                        };

                        isCheckerKilled = false;

                        Checker cell = null, killed = null;
                        for (int stepNumber = 1;
                             CheckerBoardPosition.isPositionCorrect(position.getRow() + rowStep * stepNumber,
                                     position.getColumn() + columnStep * stepNumber); stepNumber++) {
                            var pos = new CheckerBoardPosition(position.getRow() + rowStep*stepNumber,
                                    position.getColumn() + columnStep*stepNumber);
                            cell = getCheckerAt(pos);
                            if (cell == null) {
                                var step = new CheckerGameStep(this, position, pos);
                                step.setKilledChecker(killed);
                                steps.add(step);
                            } else {
                                if (killed == null){
                                    killed = cell;
                                    isCheckerKilled = true;
                                }
                                else
                                    break;
                            }
                        }
                    }
                }
            }
            if (isCheckerKilled)
                steps = steps.stream().filter(CheckerGameStep::isSomeCheckerKilled).toList();
            show();
            return steps;
        }

        private void pushLogGameStep(CheckerGameStep step) throws GameStateError {
            if (gameLog == null){
                gameLog = step;
            }
            else {
                gameLog.setNextStep(step);
            }
        }

        public CheckerColor getColor(){
            return color;
        }

        public CheckerType getType(){
            return type;
        }

        public void makeStep(CheckerBoardPosition pos)
                throws IllegalArgumentException, OutOfBorderException, IllegalCheckerPosition {
            if (color != turnOf)
                throw new IllegalStateException("Now is " + turnOf.name() + " turn" +
                        " but " + color.name() + " makes the step.");

            if (board[pos.getRow()][pos.getColumn()] != null){
                throw new IllegalArgumentException("Selected position under game unit." +
                        " You can't make step to this place by game rulers.");
            }
            else if (Math.abs(pos.getRow() - position.getRow()) != Math.abs(pos.getColumn() - position.getColumn())) // not diagonal step
            throw new IllegalArgumentException("Selected position is not placed on diagonal." +
                    " You can't make step to this place by one step by game rulers.");
            else if (Math.abs(pos.getRow() - position.getRow()) == 1){ // usual one step
                setCell(position, null);
                position = pos.copy();
                setCell(position, this);
                turnOf = turnOf.getOpposite();
            } else {
                int rowStep = pos.getRow() - position.getRow();
                int columnStep = pos.getColumn() - position.getColumn();

                if (Math.abs(rowStep) > 2 && type == CheckerType.Checker) {
                    throw new IllegalArgumentException(
                            "You can't make a move more then 2 cells usual checker by game rules");
                }

                rowStep = rowStep / Math.abs(rowStep);
                columnStep = columnStep / Math.abs(columnStep);

                CheckerBoardPosition killingCheckerPosition = null;

                // check all cells on jump distance
                for (CheckerBoardPosition iterPos = position.copy().moveOn(rowStep, columnStep);
                     iterPos.getRow() != pos.getRow();
                     iterPos.moveOn(rowStep, columnStep)){
                    if (getCheckerAt(iterPos) != null){
                        if (getCheckerAt(iterPos).color.equals(color))
                            throw new IllegalArgumentException("You can't eat yourself");
                        if (killingCheckerPosition == null) {
                            killingCheckerPosition = iterPos.copy();
                        } else {
                            throw new IllegalArgumentException(
                                    "You can't eat plural checkers by one step by game rules");
                        }
                    }
                }

                // eating the checker
                if (killingCheckerPosition != null) {
                    getCheckerAt(killingCheckerPosition).hide();
//                    pushLogGameStep(new CheckerGameStep(this, getCheckerAt(posBuf), position, pos));
                }


                if (color == CheckerColor.White && pos.getRow() == BOARD_SIZE-1)
                    this.type = CheckerType.Queen;
                else if (color == CheckerColor.Black && pos.getRow() == 0){
                    this.type = CheckerType.Queen;
                }

                setCell(position, null);
                position = pos.copy();
                setCell(position, this);

                try {
                    List<CheckerGameStep> steps = getAvailableSteps();
                    if (!(steps.size() != 0 && steps.get(0).isSomeCheckerKilled())){
                        turnOf = turnOf.getOpposite();
                    }
                } catch (Throwable ignore) {}
            }

            isStepPassed = true;
            checkWin();
        }

        // temporary solution of check
        private boolean isSomeKills(){
            return Arrays.stream(board).flatMap(Arrays::stream)
                    .filter(Objects::nonNull)
                    .filter(c -> c.color.equals(color))
                    .flatMap(checker -> {
                            try {
                                return checker.getAvailableSteps().stream();
                            } catch (Throwable ignore) {
                                return Stream.empty();
                            }
                        })
                    .filter(CheckerGameStep::isSomeCheckerKilled)
                    .limit(1) // for faster calculation
                    .toList().size() != 0;
        }

        public void moveChecker(CheckerBoardPosition pos){

        }

        private void checkWin(){
            CheckerColor opponentColor = color.getOpposite();
            List<Checker> colorizedCheckers = Arrays.stream(board).flatMap(Arrays::stream)
                    .filter(Objects::nonNull)
                    .filter(c -> c.color.equals(opponentColor))
                    .toList();
            if (colorizedCheckers.size() == 0){
                winSide = color;
            } else if (turnOf == opponentColor) {
                List<CheckerGameStep> availableSteps = colorizedCheckers.stream()
                        .flatMap(checker -> {
                            try {
                                return checker.getAvailableSteps().stream();
                            } catch (Throwable ignore) {
                                return Stream.empty();
                            }
                        })
//                    .limit(1) // for faster calculation
                        .toList();

                if (availableSteps.size() == 0)
                    winSide = color.getOpposite();
            }
        }

        public void changeType(CheckerGameStep step, Checker.CheckerType newType){
            if (step == null) // it needs to allow this actions only from steps decision
                return;

            if (step.getApplyOnChecker() != this)
                throw new IllegalArgumentException("Type of checker can be changed only with available step.");

            type = newType;
        }

        public void makeStep(String nextPosNotation)
                throws OutOfBorderException, IllegalCheckerPosition {
            makeStep(new CheckerBoardPosition(nextPosNotation));
        }

        @Override
        public String toString(){
            return color.toString() + type.toString();
        }
    }

    public static final int BOARD_SIZE = 8;
    // @board - checkers board indexing from left down corner
    private final Checker[][] board = new Checker[BOARD_SIZE][BOARD_SIZE];

    private CheckerGameStep gameLog = null;
    private Checker.CheckerColor turnOf = Checker.CheckerColor.White;

    private Checker.CheckerColor winSide = null;


    public RussianCheckers()
            throws OutOfBorderException, IllegalCheckerPosition {
        for (int row = 0; row < board.length; row++){
            // init white checker
            if (row <= 2){
                for (int column = 0; column < board.length; column++){
                    if ((row + column) % 2 == 0){
                        new Checker(Checker.CheckerColor.White, row, column);
                    }
                }
            }
            // init black checkers
            else if (row >=5 ) {
                for (int column = 0; column < board.length; column++) {
                    if ((row + column) % 2 == 0) {
                        new Checker(Checker.CheckerColor.Black, row, column);
                    }
                }
            }
        }
    }

    public Checker.CheckerColor getTurnSide(){
        return turnOf;
    }

    public Checker getCheckerAt(CheckerBoardPosition position){
        return board[position.getRow()][position.getColumn()];
    }

    public Checker getCheckerAt(String posNotation)
            throws OutOfBorderException, IllegalCheckerPosition {
        return getCheckerAt(new CheckerBoardPosition(posNotation));
    }

    public Checker getCheckerAt(int row, int column)
            throws OutOfBorderException, IllegalCheckerPosition {
        return getCheckerAt(new CheckerBoardPosition(row, column));
    }

    public void setCell(CheckerBoardPosition position, Checker cellValue){
        board[position.getRow()][position.getColumn()] = cellValue;
    }

    public String getBoardAsString(){
        StringBuilder stringBoard = new StringBuilder();
        for (int i = board.length - 1; i >= 0; i--){
            Checker[] checkersRow = board[i];
            for (Checker checker : checkersRow){
                if (checker != null)
                    stringBoard.append(checker.toString());
                else
                    stringBoard.append("[]");
            }
            stringBoard.append('\n');
        }
        return stringBoard.toString();
    }

    public boolean isGameEnd(){
        return winSide != null;
    }

    public Checker.CheckerColor getWinnerSide(){
        return winSide;
    }

    public List<Checker> getAllCheckers(){
        return Arrays.stream(board).flatMap(arr -> Arrays.stream(arr)).filter(Objects::nonNull).toList();
    }
}

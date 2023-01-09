package org.gamecore;


import java.util.HashSet;
import java.util.Set;

/**
 * @implNote  This class is command pattern to checker game
 * Inner chain of steps declares the one turn of one player
 */
public class CheckerGameStep {
    public static class CheckerStepApplyException extends Exception {
        CheckerStepApplyException() { super();}
        CheckerStepApplyException(String msg) { super();}
    }
    private RussianCheckers.Checker applyOnChecker;
    private RussianCheckers.Checker killedChecker = null;
    private boolean isTransformedToQueen = false;
    private final CheckerBoardPosition from;
    private final CheckerBoardPosition to;

    private CheckerGameStep nextStep = null;
    private CheckerGameStep previousStep = null;

    public CheckerGameStep(RussianCheckers.Checker checker, CheckerBoardPosition from, CheckerBoardPosition to){
        applyOnChecker = checker;
        this.from = from;
        this.to = to;
    }

    public CheckerGameStep(CheckerGameStep previousStep, CheckerBoardPosition nextPos) throws GameStateError {
        previousStep.setNextStep(this);
        applyOnChecker = previousStep.applyOnChecker;
        from = previousStep.to;
        to = nextPos;
    }

    public void setKilledChecker(RussianCheckers.Checker killedChecker) throws CheckerStepApplyException {
        if (this.killedChecker == null)
            this.killedChecker = killedChecker;
        else
            throw new CheckerStepApplyException("You can't kill plural checkers for one step." +
                    " You can do this by plural steps in one turn");

        if (nextStep == null)
            killedChecker.killStub();
    }

    public void unsetKilledChecker() throws GameStateError {
        if (killedChecker != null)
            killedChecker.show();

        killedChecker = null;
    }

    public void setNextStep(CheckerGameStep step) throws GameStateError {
        nextStep = step;
        step.previousStep = this;

        if (killedChecker != null)
            killedChecker.show();
    }

    public RussianCheckers.Checker getApplyOnChecker() {
        return applyOnChecker;
    }

    public CheckerBoardPosition getFrom() {
        return from;
    }

    public CheckerBoardPosition getTo() {
        return to;
    }

    public RussianCheckers.Checker getKilledChecker() {
        return killedChecker;
    }

    public Set<RussianCheckers.Checker> getAllKilledCheckers() {
        CheckerGameStep stepIter = this;

        while (stepIter.previousStep != null)
            stepIter = stepIter.previousStep;

        var killedCheckers = new HashSet<RussianCheckers.Checker>();

        for (;stepIter.nextStep != null; stepIter = stepIter.nextStep){
            killedCheckers.add(stepIter.killedChecker);
        }
        return killedCheckers;
    }

    public void setApplyOnChecker(RussianCheckers.Checker checker){
        applyOnChecker = checker;
    }

    public void setDependingStep(CheckerGameStep step) throws GameStateError {
        step.setNextStep(this);
    }

    public boolean isSomeCheckerKilled(){
        return killedChecker != null;
    }

    /**
     *
     * @return next step in the same turn
     * @throws CheckerStepApplyException - this exception exist when checker position
     * is not consolidate with current checker step protocol.
     */
    public CheckerGameStep apply() throws CheckerStepApplyException {
        if (! applyOnChecker.getCurrentPosition().equals(from))
            throw new CheckerStepApplyException("We can't apply step on checker because init position "
                    + from.toString() +
                    " is not agreed to real checker position "
                    + applyOnChecker.getCurrentPosNotation());
        applyOnChecker.moveChecker(to);

        if (killedChecker != null){
            killedChecker.killStub();
            killedChecker.hide();
        }

        if (isTransformedToQueen)
            applyOnChecker.changeType(this, RussianCheckers.Checker.CheckerType.Queen);

        return nextStep;
    }

    public void applyAll() throws CheckerStepApplyException{
        CheckerGameStep stepIter = this;
        while ((stepIter = stepIter.apply()) != null);
    }

    /**
     *
     * @return previous step in the same turn
     * @throws CheckerStepApplyException - this exception exist when checker position
     * is not consolidate with current checker step protocol.
     */
    public CheckerGameStep revert() throws CheckerStepApplyException, GameStateError {
        if (! applyOnChecker.getCurrentPosition().equals(from))
            throw new CheckerStepApplyException("We can't apply step reverting on checker because end position "
                    + to.toString() +
                    " is not agreed to real checker position "
                    + applyOnChecker.getCurrentPosNotation());
        applyOnChecker.moveChecker(from);
        if (killedChecker != null){
            killedChecker.show();
            killedChecker.killUnStub();
        }

        if (isTransformedToQueen)
            applyOnChecker.changeType(this, RussianCheckers.Checker.CheckerType.Checker);

        return previousStep;
    }

    public void transformedToQueen(){
        isTransformedToQueen = true;
    }

    public void revertAll() throws CheckerStepApplyException, GameStateError {
        CheckerGameStep stepIter = this;
        while ((stepIter = stepIter.revert()) != null);
    }

    public void realiseTurn() throws CheckerStepApplyException {
        CheckerGameStep stepIter = this;
        while (stepIter.previousStep != null)
            stepIter = stepIter.previousStep;

        stepIter.applyAll();
    }

    public void cancelTurn() throws CheckerStepApplyException, GameStateError {
        CheckerGameStep stepIter = this;
        while (stepIter.nextStep != null)
            stepIter = stepIter.nextStep;

        stepIter.revertAll();
    }
}

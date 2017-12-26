public class Move {

    private int examToMove; //the timeslot to be moved
    private int timeslotForbidden; //the timeslot forbidden

    public Move(int examToMove, int timeslotForbidden) {
        this.examToMove = examToMove;
        this.timeslotForbidden = timeslotForbidden;
    }

    public int getExamToMove() {
        return examToMove;
    }

    public void setExamToMove(int examToMove) {
        this.examToMove = examToMove;
    }

    public int getTimeslotForbidden() {
        return timeslotForbidden;
    }

    public void setTimeslotForbidden(int timeslotForbidden) {
        this.timeslotForbidden = timeslotForbidden;
    }
}

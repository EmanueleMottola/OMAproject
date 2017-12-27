public class Move {

    private int examToMove; //the exam to be moved
    private int timeslot; //the timeslot

    public Move(int examToMove, int timeslotForbidden) {
        this.examToMove = examToMove;
        this.timeslot = timeslotForbidden;
    }

    public int getExamToMove() {
        return examToMove;
    }

    public void setExamToMove(int examToMove) {
        this.examToMove = examToMove;
    }

    public int getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(int timeslotForbidden) {
        this.timeslot = timeslotForbidden;
    }
}

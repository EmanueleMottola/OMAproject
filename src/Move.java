public class Move {

    private int examToMove; //the exam to be moved
    private int timeslot_dest; //the timeslot where the exam is put
    private int timeslot_source; //the timeslot where the exam is taken

    public Move (){
        this.examToMove = -1;
        this.timeslot_source = -1;
        this.timeslot_dest = -1;
    }

    public Move(int examToMove, int timeslot_source, int timeslot_dest) {
        this.examToMove = examToMove;
        this.timeslot_source = timeslot_source;
        this.timeslot_dest = timeslot_dest;
    }

    public int getExamToMove() {
        return examToMove;
    }

    public void setExamToMove(int examToMove) {
        this.examToMove = examToMove;
    }

    public int getTimeslot_dest() {
        return timeslot_dest;
    }

    public void setTimeslot_dest(int timeslot_dest) {
        this.timeslot_dest = timeslot_dest;
    }

    public int getTimeslot_source() {
        return timeslot_source;
    }

    public void setTimeslot_source(int timeslot_source) {
        this.timeslot_source = timeslot_source;
    }

    @Override
    public String toString() {
        return "Move{" +
                "examToMove=" + examToMove +
                ", timeslot_source=" + timeslot_source +
                ", timeslot_dest=" + timeslot_dest +

                '}';
    }
}

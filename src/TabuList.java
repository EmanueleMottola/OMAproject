import java.util.LinkedList;
import java.util.List;

public class TabuList {

    private LinkedList<Move> moves;
    private int dim;
    private int dimMax;

    public TabuList(int dimMax) {
        this.moves = new LinkedList<>();
        this.dim = 0;
        this.dim = dimMax;
    }

    public boolean addTabuMove(Move move){

        Move m = new Move();

        /*System.out.println("Exam id:" + move.getExamToMove());
        System.out.println("t_source:" + move.getTimeslot_source());
        System.out.println("t_dest:" + move.getTimeslot_dest());*/
        m.setExamToMove(move.getExamToMove());
        m.setTimeslot_dest(move.getTimeslot_source());
        m.setTimeslot_source(move.getTimeslot_dest());

        if(dim == dimMax){
            this.moves.removeFirst();
            this.dim--;
        }
        if(this.moves.add(m)){
            this.dim++;
            return true;
        }

        return false;
    }

    public boolean checkTabuMove(Move e){
        for(Move et : this.moves){
            if(equal(et, e))
                return true;
        }
        return false;
    }

    private boolean equal(Move et, Move e){

        //controllo swap
        if(et.getExamToMove() == e.getExamToMove() && et.getTimeslot_source() == e.getTimeslot_source() &&
                et.getTimeslot_dest() == e.getTimeslot_dest() )
            return true;

        return false;
    }



}

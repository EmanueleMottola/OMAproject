import java.util.LinkedList;
import java.util.List;

public class TabuList {

    private LinkedList<Move> moves;
    private int dim;

    public TabuList(int dim) {
        this.moves = new LinkedList<>();
        this.dim = dim;
    }

    public boolean addTabuMove(int idExam, int idTimeslot){
        Move move = new Move(idExam, idTimeslot);
        if(dim == 7){
            this.moves.removeFirst();
            this.dim--;
        }
        if(this.moves.add(move)){
            this.dim++;
            return true;
        }

        return false;
    }

    public boolean checkTabuMove(Move e){
        return this.moves.contains(e);
    }





}

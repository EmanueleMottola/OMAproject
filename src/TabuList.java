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
        if(dim == dimMax){
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

import java.util.*;

public class Solution {

    /*
    Sono state inserite variabili per distinguere la soluzione corrente dalla migliore ottenuta
    finora---> Map bestSolution
    I vicini che generiamo vengono inseriti dentro la Map neighbours, pulita ogni nuova generazione
     */
    private Map<Integer, Timeslot> bestSolution;
    private double bestPenalty; //Penalty della best solution
    private Map<Integer, Timeslot> currentSolution;
    private double currentPenalty;
    private Map<Move, Map<Integer, Timeslot>> neighbours;
    private Queue<Exam> priorityExams;
    private Map<Integer, Exam> exams;
    private TabuList tabulist;
    private int[][] conflicts;

    public Solution() {
        this.bestSolution = new LinkedHashMap<>();
        this.currentSolution = new LinkedHashMap<>();
        this.neighbours = new LinkedHashMap<>();
        this.priorityExams = new PriorityQueue<>((a, b) -> b.getNumberOfConflicts() - a.getNumberOfConflicts());
        this.exams = new LinkedHashMap<>();
        this.tabulist = new TabuList(7);
        this.bestPenalty = Integer.MAX_VALUE;
    }

    public Map<Integer, Timeslot> getBestSolution() {
        return bestSolution;
    }

    public void setBestSolution(Map<Integer, Timeslot> bestSolution) {
        this.bestSolution = bestSolution;
    }

    public Map<Integer, Timeslot> getCurrentSolution() {
        return currentSolution;
    }

    public void setCurrentSolution(Map<Integer, Timeslot> currentSolution) {
        this.currentSolution = currentSolution;
    }

    public boolean addNeighbour(Move k, Map<Integer, Timeslot> n){
        for(Map.Entry <Move, Map<Integer, Timeslot>> entry : neighbours.entrySet() ){
            if(k.equals(entry.getKey())){
                return false;
            }
        }

        neighbours.put(k, n);
        return true;
    }

    public Map<Move, Map<Integer, Timeslot>> getNeighbours() {
        return neighbours;
    }

    public Queue<Exam> getPriorityExams() {
        return priorityExams;
    }

    public Map<Integer, Exam> getExams() {
        return exams;
    }

    public TabuList getTabulist() {
        return tabulist;
    }

    public double getBestPenalty() {
        return bestPenalty;
    }

    public void setBestPenalty(double bestPenalty) {
        this.bestPenalty = bestPenalty;
    }

    public double getCurrentPenalty() {
        return currentPenalty;
    }

    public void setCurrentPenalty(double currentPenalty) {
        this.currentPenalty = currentPenalty;
    }

    public int[][] getConflicts() {
        return conflicts;
    }

    //svuota e pulisce i neighbours
    public void clear(){
        this.neighbours.clear();
    }

    public void initializeConflictsMatrix() {

        int totExams = exams.size();
        conflicts = new int[totExams][totExams];
        for(int i=0; i<totExams; i++)
            for(int j=0; j<totExams; j++)
                conflicts[i][j] = 0;
    }

    public void fillConflictsMatrix(List<Integer> tmp) {
        int h, k;
        //System.out.println(tmp.size());
        if(tmp.size() > 1)
            for(int i=0; i<tmp.size()-1; i++)
                for(int j=i+1; j<tmp.size(); j++)
                {
                    h = tmp.get(i) - 1; // -1 because examIds start from 1 but the rowIndex of the matrix starts from 0
                    k = tmp.get(j) - 1; // -1 because examIds start from 1 but the columnIndex of the matrix starts from 0
                    conflicts[h][k]++;
                    conflicts[k][h]++;
                }
    }

    private boolean checkconflict(Exam e, Timeslot t){

        try{
            for(Exam et : t.getExamsOfTimeslot()){
                if(conflicts[et.getIdExam()-1][e.getIdExam()-1] != 0)
                    return true;
            }
            return false;
        }
        catch(NullPointerException exce){
            System.out.println(exce.getMessage());
            return true;
        }

    }


    /*this function creates n neighbours, the number is chosen arbitrary.
         We choose the move to do in the following way:
             I consider the timeslot with the higher PenaltyPerTimeslot.
             There, we choose the exam with the higher penalty_exam.
             That is the exam of which the position we want to change. The destination timeslot
            is chosen randomly, checking feasibility of the new solution
    */

    public void Neighbours(){

        Move mossa =null;
        int max;
        Exam e_selected=null;
        int t_source=0, t_destination=0;
        Random random = new Random();
        int i=0,k=1;


        while(neighbours.size() < 100) {

            System.out.println("Sto cercando un neighbor");
            Map<Integer, Timeslot> m = clone(currentSolution);
            max=Integer.MIN_VALUE;

            t_source = Math.abs(random.nextInt()) % m.size() +1;
            while(m.get(t_source).getExamsOfTimeslot().isEmpty()){
                t_source = Math.abs(random.nextInt()) % m.size() +1;
            }

            int e_sel = Math.abs(random.nextInt()) % m.get(t_source).getExamsOfTimeslot().size();
            e_selected = m.get(t_source).getExamsOfTimeslot().get(e_sel);

            m.get(t_source).removeExamFromTimeslot(e_selected);

            t_destination = (Math.abs(random.nextInt()) % m.size()) + 1;

            if((t_destination == t_source )|| checkconflict(e_selected, m.get(t_destination))){
                continue;
            }
            m.get(t_destination).addExamToTimeslot(e_selected);


            mossa = new Move(e_selected.getIdExam(), t_source, t_destination);
            e_selected.setTimeSlot(m.get(t_destination));

            if(!addNeighbour(mossa, m)){
                continue;
            }

            //System.out.println("(" + mossa.getExamToMove() + ", "+ mossa.getTimeslot_source()+", "+mossa.getTimeslot_dest()+")");
            //System.out.println("Print current solution: ");
            //print(currentSolution, conflicts);
            //System.out.println("Print neighbour: " + j);
            //print(m, conflicts);
        }


    }
    /*
    this function chooses which is the best neighbor and updates the current/best solution
     */

    public void move(Move moveDone, double smallerPenalty){
        //System.out.println( "Scegliamo" + moveDone.toString());

        //se il neighbor scelto è migliore del best aggiorno best e current
        //altrimenti aggiorno solo current
        if(smallerPenalty < bestPenalty) {
            bestPenalty = currentPenalty = smallerPenalty;
            bestSolution = currentSolution = neighbours.get(moveDone);
        }
        else{
            currentPenalty = smallerPenalty;
            currentSolution = neighbours.get(moveDone);
        }
    }


    private Map<Integer, Timeslot> clone(Map<Integer, Timeslot> map){

        Map<Integer, Timeslot> m = new LinkedHashMap<>();

        for(Map.Entry<Integer, Timeslot> entry : map.entrySet()){
            Timeslot t = new Timeslot(entry.getKey());
            for(Exam e : entry.getValue().getExamsOfTimeslot()) {
                Exam et = new Exam(e.getIdExam(), e.getEnrolledStudents());
                et.setTimeSlot(t);
                t.addExamToTimeslot(et);
            }
            m.put(entry.getKey(), t);

        }

        return m;
    }

}

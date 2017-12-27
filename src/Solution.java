import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Solution {

    private Map<Integer, Timeslot> bestSolution;
    private int bestPenalty;
    private Map<Integer, Timeslot> currentSolution;
    private Map<Move, Map<Integer, Timeslot>> neighbours;
    private Map<Integer,Exam> exams;
    private TabuList tabulist;

    public Solution() {
        this.bestSolution = new LinkedHashMap<>();
        this.currentSolution = new LinkedHashMap<>();
        this.neighbours = new LinkedHashMap<>();
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

    public void addNeighbour(Move k, Map<Integer, Timeslot> n){
        neighbours.put(k, n);
    }

    public Map<Move, Map<Integer, Timeslot>> getNeighbours() {
        return neighbours;
    }

    public Map<Integer, Exam> getExams() {
        return exams;
    }

    public void setExams(Map<Integer, Exam> exams) {
        this.exams = exams;
    }

    public TabuList getTabulist() {
        return tabulist;
    }

/*implementare qui una funzione che serva per scegliere il miglior neighbour e ritorni una mossa da inserire
    nella tabulist.
     */

    public void clear(){
        this.neighbours.clear();
    }

    //this function controls that the exam e we want to insert in timeslot t isn't in conflict with the other exams of t
    private boolean checkconflict(int[][] conflicts, Exam e, Timeslot t){

        for(Exam et : t.getExamsOfTimeslot()){
            if(conflicts[et.getIdExam()-1][e.getIdExam()-1] != 0)
                return true;
        }
        return false;
    }

    //this function assigns to each exam in each timeslot its contribution to the global penalty (see move function)
    public void computePenaltyExam(int [][] conflicts){

        int penalty=0, timeslotPenalty;

        for(Map.Entry<Integer, Timeslot> entry : this.getCurrentSolution().entrySet()){

            timeslotPenalty = 0;

            for(Exam e : entry.getValue().getExamsOfTimeslot()){

                penalty = computeSingleExamPenalty(entry.getKey(), e, conflicts);

                e.setPenalty_exam(penalty);
                timeslotPenalty += penalty;
            }

            entry.getValue().setPenaltyPerTimeslot(timeslotPenalty);
        }
    }

    /*
    for each exam it is computed which is the amount it influences the total penalty, it's then put in penalty_exam
     */
    private int computeSingleExamPenalty(int k, Exam e, int [][] conflicts){

        int[] power = new int[]{16, 8, 4, 2, 1};
        int p, penalty=0;

        for(int i=-5; i<6; i++) {

            p = k + i;

            if(p < 1 || i==0 || p > currentSolution.size())
                continue;

            for(Exam et : this.getCurrentSolution().get(p).getExamsOfTimeslot()){
                //System.out.println("i :" + Integer.toString(i-1));
                //System.out.println("conflict[" + w1 +"][" + w2 + "] = " + conflicts[w1][w2] );
                penalty += conflicts[e.getIdExam()-1][et.getIdExam()-1] * power[Math.abs(i)-1];

            }

        }

        return penalty;
    }

    /*this function creates 10 neighbours, the number is chosen arbitrary.
     We choose the move to do in the following way:
         I consider the timeslot with the higher PenaltyPerTimeslot.
         There, we choose the exam with the higher penalty_exam.
         That is the exam of which the position we want to change. The destination timeslot
         is chosen randomly, checking feasibility of the new solution
    */
    public Move Neighbours(int [][] conflicts, Move moveDone){

        Map<Integer, Timeslot> m = new LinkedHashMap<>(currentSolution);
        Move mossa = null;
        int max = Integer.MIN_VALUE;
        Exam e_selected=null;
        int t_source=0;

        for(int j=0; j<10; j++) {
            System.out.println("running");
            for (Map.Entry<Integer, Timeslot> entry : m.entrySet()) {

                if (entry.getValue().getPenaltyPerTimeslot() > max) {

                    max = entry.getValue().getPenaltyPerTimeslot();
                    t_source = entry.getKey();

                }

            }
            max = Integer.MIN_VALUE;

            for (Exam e : m.get(t_source).getExamsOfTimeslot()) {

                if (e.getPenalty_exam() > max) {
                    max = e.getPenalty_exam();
                    e_selected = e;

                }

            }

            int timeslotPenalty = m.get(t_source).getPenaltyPerTimeslot();
            timeslotPenalty -= e_selected.getPenalty_exam();
            m.get(t_source).setPenaltyPerTimeslot(timeslotPenalty);



            m.get(t_source).removeExamFromTimeslot(e_selected);

            int conta = 0;
            int t_destination = (int) ((Math.random() % m.size()) + 1);

            //qua cicla all'infinito, mettere a posto
            while ((t_destination == t_source) || (conta < m.size() * 2) || (checkconflict(conflicts, e_selected, m.get(t_destination)))) {

                t_destination = (int) ((Math.random() % m.size()) + 1);
                conta++;

            }

            m.get(t_destination).addExamToTimeslot(e_selected);
            timeslotPenalty = m.get(t_destination).getPenaltyPerTimeslot();
            timeslotPenalty += computeSingleExamPenalty(t_destination, e_selected, conflicts);
            m.get(t_destination).setPenaltyPerTimeslot(timeslotPenalty);

            mossa = new Move(e_selected.getIdExam(), t_destination);

            neighbours.put(mossa, m);
        }
        Move moveForbidden = new Move(e_selected.getIdExam(), t_source);

        move(moveDone);

        return moveForbidden;

    }

    /*
    this function chooses which is the best neighbor and updates the current/best solution
     */

    private void move(Move moveDone){

        int minPenalty = Integer.MAX_VALUE;
        int penalty=0;

        for(Map.Entry<Move, Map<Integer, Timeslot>> m : neighbours.entrySet()){
            penalty=0;
            for(Map.Entry<Integer, Timeslot> t : m.getValue().entrySet()){
                penalty += t.getValue().getPenaltyPerTimeslot();
            }
            if(penalty < minPenalty){
                if(tabulist.checkTabuMove(m.getKey())){
                    if(penalty <= bestPenalty){   // aspiration criteria
                        bestPenalty = minPenalty = penalty;
                        bestSolution = currentSolution = m.getValue();
                        moveDone = m.getKey();
                    }
                }
                else{
                    minPenalty = penalty;
                    currentSolution = m.getValue();
                    if(minPenalty < bestPenalty){
                        bestSolution = currentSolution;
                    }
                    moveDone = m.getKey();
                }

            }
        }

        return ;
    }


}

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Solution {

    private Map<Integer, Timeslot> bestSolution;
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

    private int computeSingleExamPenalty(int k, Exam e, int [][] conflicts){

        int[] power = new int[]{1, 2, 4 ,8 ,16};
        int p, penalty=0;

        for(int i=-5; i<6; i++) {

            p = k + i;

            if(p < 1)
                break;

            if(i==0)
                continue;

            for(Exam et : this.getCurrentSolution().get(p).getExamsOfTimeslot()){
                penalty += e.getPenalty_exam() + conflicts[e.getIdExam()-1][et.getIdExam()-1] * power[(Math.abs(i))];
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
    public void Neighbours(int [][] conflicts){

        Map<Integer, Timeslot> m = new LinkedHashMap<>(currentSolution);
        Move move = null;
        int max = Integer.MIN_VALUE;
        Exam e_selected=null;
        int t_source=0;

        for(int j=0; j<10; j++) {
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

            while ((t_destination == t_source) && (conta < m.size() * 2) && (checkconflict(conflicts, e_selected, m.get(t_destination)))) {

                t_destination = (int) ((Math.random() % m.size()) + 1);
                conta++;

            }

            m.get(t_destination).addExamToTimeslot(e_selected);
            timeslotPenalty = m.get(t_destination).getPenaltyPerTimeslot();
            timeslotPenalty += computeSingleExamPenalty(t_destination, e_selected, conflicts);
            m.get(t_destination).setPenaltyPerTimeslot(timeslotPenalty);

            move = new Move(e_selected.getIdExam(), t_destination);

            neighbours.put(move, m);
        }

    }

    /*
    this function chooses which is the best neighbor and updates the current/best solution
     */

    public Move move(){

    }
}

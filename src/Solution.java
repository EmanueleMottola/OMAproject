import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Solution {

    private Map<Integer, Timeslot> bestSolution;
    private Map<Integer, Timeslot> currentSolution;
    private List<Map<Integer, Timeslot>> neighbours;
    private Map<Integer,Exam> exams;
    private TabuList tabulist;

    public Solution() {
        this.bestSolution = new LinkedHashMap<>();
        this.currentSolution = new LinkedHashMap<>();
        this.neighbours = new ArrayList<>();
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

    public void addNeighbour(Map<Integer, Timeslot> n){
        neighbours.add(n);
    }

    public List<Map<Integer, Timeslot>> getNeighbours() {
        return neighbours;
    }

    public Map<Integer, Exam> getExams() {
        return exams;
    }

    public void setExams(Map<Integer, Exam> exams) {
        this.exams = exams;
    }


    public void Neighbours(int [][] conflicts){

        Map<Integer, Timeslot> m = new LinkedHashMap<>(currentSolution);
        int max = Integer.MIN_VALUE;
        Exam e_selected=null;
        int t_selected=0;

        for(int j=0; j<10; j++) {
            for (Map.Entry<Integer, Timeslot> entry : m.entrySet()) {

                if (entry.getValue().getPenaltyPerTimeslot() > max) {

                    max = entry.getValue().getPenaltyPerTimeslot();
                    t_selected = entry.getKey();

                }

            }
            max = Integer.MIN_VALUE;

            for (Exam e : currentSolution.get(t_selected).getExamsOfTimeslot()) {

                if (e.getPenalty_exam() > max) {
                    max = e.getPenalty_exam();
                    e_selected = e;

                }

            }

            m.get(t_selected).removeExamFromTimeslot(e_selected);

            int conta = 0;
            int r = (int) ((Math.random() % currentSolution.size()) + 1);

            while ((r == t_selected) && (conta < currentSolution.size() * 2) && (checkconflict(conflicts, e_selected, m.get(r)))) {

                r = (int) ((Math.random() % currentSolution.size()) + 1);
                conta++;

            }

            currentSolution.get(r).addExamToTimeslot(e_selected);
            neighbours.add(m);
        }

    }

    /*implementare qui una funzione che serva per scegliere il miglior neighbour e ritorni una mossa da inserire
    nella tabulist.
     */

    public void clear(){
        this.neighbours.clear();
    }

    private boolean checkconflict(int[][] conflicts, Exam e, Timeslot t){

        for(Exam et : t.getExamsOfTimeslot()){
            if(conflicts[et.getIdExam()-1][e.getIdExam()-1] != 0)
                return true;
        }
        return false;
    }

    public void computePenaltyExam(int [][] conflicts){

        int p, penalty=0, timeslotPenalty=0;
        int[] power = new int[]{1, 2, 4 ,8 ,16};

        for(Map.Entry<Integer, Timeslot> entry : this.getCurrentSolution().entrySet()){

            for(Exam e : entry.getValue().getExamsOfTimeslot()){

                penalty=0;

                for(int i=-5; i<6; i++) {

                    p = entry.getKey() + i;

                    if(p < 1)
                        break;

                    if(i==0)
                        continue;

                    for(Exam et : this.getCurrentSolution().get(p).getExamsOfTimeslot()){
                        penalty += e.getPenalty_exam() + conflicts[e.getIdExam()-1][et.getIdExam()-1] * power[(Math.abs(i))];
                    }

                }

                e.setPenalty_exam(penalty);
                timeslotPenalty += penalty;
            }

            entry.getValue().setPenaltyPerTimeslot(timeslotPenalty);
        }
    }
}

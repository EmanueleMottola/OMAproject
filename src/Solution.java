import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Solution {

    private Map<Integer, Timeslot> bestSolution;
    private Map<Integer, Timeslot> currentSolution;
    private List<Map<Integer, Timeslot>> neighbours;
    private Map<Integer,Exam> exams;

    public Solution() {
        this.bestSolution = new LinkedHashMap<>();
        this.currentSolution = new LinkedHashMap<>();
        this.neighbours = new ArrayList<>();
        this.exams = new LinkedHashMap<>();
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


    public Map<Integer, Timeslot> move(){

        Map<Integer, Timeslot> m = new LinkedHashMap<>(currentSolution);
        int max = Integer.MIN_VALUE;
        Exam e_selected=null;
        int t_selected=0;

        for(Map.Entry<Integer, Timeslot> entry : m.entrySet()){

            if(entry.getValue().getPenaltyPerTimeslot() > max){

                max = entry.getValue().getPenaltyPerTimeslot();
                t_selected = entry.getKey();

            }

        }
        max = Integer.MIN_VALUE;

        for(Exam e : currentSolution.get(t_selected).getExamsOfTimeslot() ){

            if(e.getPenalty_exam() > max){

                max = e.getPenalty_exam();
                e_selected = e;

            }

        }

        m.get(t_selected).removeExamFromTimeslot(e_selected);

        int conta=0;
        int r = (int) ((Math.random() % currentSolution.size()) + 1);
        while( (r == t_selected) && (conta < currentSolution.size() * 2) && (checkconflict(e_selected, m.get(r))) ){
            r = (int) ((Math.random() % currentSolution.size()) + 1);
            conta++;
        }

        currentSolution.get(r).addExamToTimeslot(e_selected);

        return m;
    }


}

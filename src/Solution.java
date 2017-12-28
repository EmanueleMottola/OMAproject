import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Solution {

    private Map<Integer, Timeslot> bestSolution;
    private double bestPenalty;
    private Map<Integer, Timeslot> currentSolution;
    private double currentPenalty;
    private Map<Move, Map<Integer, Timeslot>> neighbours;
    private Map<Integer, Exam> bestExam;
    private TabuList tabulist;

    public Solution() {
        this.bestSolution = new LinkedHashMap<>();
        this.currentSolution = new LinkedHashMap<>();
        this.neighbours = new LinkedHashMap<>();
        this.bestExam = new LinkedHashMap<>();
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
        return bestExam;
    }

    public void setExams(Map<Integer, Exam> exams) {
        this.bestExam = exams;
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

                penalty = computeSingleExamPenalty(entry.getKey(), e.getIdExam(), conflicts);

                e.setPenalty_exam(penalty);
                timeslotPenalty += penalty;
            }

            entry.getValue().setPenaltyPerTimeslot(timeslotPenalty);
        }
    }

    /*
    for each exam it is computed which is the amount it influences the total penalty, it's then put in penalty_exam
     */
    public int computeSingleExamPenalty(int timeslot_id, int e_id, int [][] conflicts){

        int[] power = new int[]{16, 8, 4, 2, 1};
        int p, penalty=0;

        for(int i=-5; i<6; i++) {

            p = timeslot_id + i;

            if(p < 1 || i==0 || p > currentSolution.size())
                continue;

            for(Exam et : this.getCurrentSolution().get(p).getExamsOfTimeslot()){
                //System.out.println("i :" + Integer.toString(i-1));
                //System.out.println("conflict[" + w1 +"][" + w2 + "] = " + conflicts[w1][w2] );
                penalty += conflicts[e_id-1][et.getIdExam()-1] * power[Math.abs(i)-1];

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

        Move mossa = null;
        int max = Integer.MIN_VALUE;
        Exam e_selected=null;
        int t_source=0, t_destination=0;
        Random random = new Random();

        for(int j=0; j<currentSolution.size(); j++) {
            Map<Integer, Timeslot> m = clone(currentSolution);
            max=Integer.MIN_VALUE;

            t_source = Math.abs(random.nextInt()) % m.size() +1;
            while(m.get(t_source).getExamsOfTimeslot().isEmpty()){
                t_source = Math.abs(random.nextInt()) % m.size() +1;
            }

            for (Exam e : m.get(t_source).getExamsOfTimeslot()) {
                if (e.getPenalty_exam() > max) {
                    max = e.getPenalty_exam();
                    e_selected = new Exam(e.getIdExam() , e.getEnrolledStudents());
                }
            }

            m.get(t_source).removeExamFromTimeslot(e_selected);

            t_destination = (Math.abs(random.nextInt()) % m.size()) + 1;

            //qua cicla all'infinito, mettere a posto
            while ( (t_destination == t_source || checkconflict(conflicts, e_selected, m.get(t_destination)))) {
                int r = random.nextInt();
                t_destination = (Math.abs(r) % m.size()) + 1;
            }

            m.get(t_destination).addExamToTimeslot(e_selected);


            mossa = new Move(e_selected.getIdExam(),t_source, t_destination);
            e_selected.setTimeSlot(m.get(t_destination));

            addNeighbour(mossa, m);

            //System.out.println("Print current solution: ");
            //print(currentSolution, conflicts);
            //System.out.println("Print neighbour: " + j);
            //print(m, conflicts);
        }

        return;

    }

    /*
    this function chooses which is the best neighbor and updates the current/best solution
     */

    public void move(Move moveDone, double smallerPenalty, int[][] conflicts){
        System.out.println( "Siamo denttro move" + moveDone.toString());

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



    public void print(Map<Integer, Timeslot> map, int [][] conflicts){
        List<Exam> lista_c = new ArrayList<>();

        for(Map.Entry<Integer, Timeslot> entry : map.entrySet()){
            System.out.println(entry.getValue().getExamsOfTimeslot().toString());
            for(Exam e1 : entry.getValue().getExamsOfTimeslot()){
                for(Exam e2 : entry.getValue().getExamsOfTimeslot()){
                    if(!e1.equals(e2)){
                        if(conflicts[e1.getIdExam()-1][e2.getIdExam()-1]!=0){
                            System.out.println("Studente/i in comune tra gli esami " + e1.getIdExam() + " "+
                                    e2.getIdExam()+ " (valore conflicts: "+conflicts[e1.getIdExam()-1][e2.getIdExam()-1]+") in " +entry.getValue().toString());
                        }
                    }
                }
            }
        }

        for(Map.Entry<Integer, Exam> entry1 : bestExam.entrySet()){
            boolean p = false;
            for(Map.Entry<Integer, Timeslot> entry2 : map.entrySet()){
                if(entry2.getValue().getExamsOfTimeslot().contains(entry1.getValue())){
                    if(p){
                        System.out.println(entry1.getValue() + "gia presente");
                    }
                    p = true;
                }
            }
            if(!p){
                lista_c.add(entry1.getValue());
            }
        }

        System.out.println("lista esami non inseriti nei timeslots: " +lista_c.toString());



    }

    private Map<Integer, Timeslot> clone(Map<Integer, Timeslot> map){

        Map<Integer, Timeslot> m = new LinkedHashMap<>();

        for(Map.Entry<Integer, Timeslot> entry : map.entrySet()){
            Timeslot t = new Timeslot(entry.getKey());
            t.setPenaltyPerTimeslot(entry.getValue().getPenaltyPerTimeslot());
            for(Exam e : entry.getValue().getExamsOfTimeslot()) {
                Exam et = new Exam(e.getIdExam(), e.getEnrolledStudents());
                et.setTimeSlot(t);
                et.setPenalty_exam(e.getPenalty_exam());
                t.addExamToTimeslot(et);
            }
            m.put(entry.getKey(), t);

        }

        return m;
    }

}

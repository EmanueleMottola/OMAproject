

import java.io.*;
import java.sql.Time;
import java.util.*;

import com.sun.istack.internal.NotNull;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

public class ExaminationTimetabling {

    private Solution solution;
	private int totalTimeslots;
	private int numberOfStudent;
	// indexex start from 0, so for example if you want to know if exam 4 and 7 are in conflict you have to access the cell [3,6]
	public ExaminationTimetabling() {
		solution = new Solution();
	}
	// Constructor



	// This public function simply call the private methods that read files and put data in their structures 
	public void fillData(String fileExm, String fileSlo, String fileStu) {
		readFileExm(fileExm);
		solution.initializeConflictsMatrix();
		readFileSlo(fileSlo);
		readFileStu(fileStu);
		/* Print the Map of exams
		 * for (Map.Entry<Integer, Exam> entry : exams.entrySet())
		    System.out.println(entry.getKey() + " " + entry.getValue().getEnrolledStudents());
		}*/
		// Print the total number of timeslots
		/* System.out.println(timeslots); */
		// Print the conflict matrix
		/* for(int i=0; i < exams.keySet().size(); i++)
		{
			for(int j=0; j< exams.keySet().size(); j++)
				System.out.print(conflicts[i][j] + " ");
			System.out.println("\n");
		} */
		// Print the Map of Timeslots
		/* for (Map.Entry<Integer, Timeslot> entry : timeslots.entrySet())
		    System.out.println(entry.getKey() + " " + entry.getValue().getIdTimeSlot()); */ 
	}
	
	private void readFileExm(String filename) {

		try (BufferedReader in = new BufferedReader(new FileReader(filename)))
		{
			String line;
			int idExam, enStudents;
			while (!(line = in.readLine()).isEmpty())
			{
				Scanner s = new Scanner(line);
				idExam = s.nextInt();
				enStudents = s.nextInt();
				Exam e = new Exam(idExam, enStudents);
				solution.getExams().put(idExam, e);
				s.close();
			}
		} catch(IOException e) {System.out.println(e.getMessage());}

		fillExam();

	}
	
	private void readFileSlo(String filename) {
		try (BufferedReader in = new BufferedReader(new FileReader(filename)))
		{
			String line = in.readLine();
			totalTimeslots = Integer.parseInt(line);
		} catch(IOException e) {System.out.println(e.getMessage());}
		// I fill the Map with the timeslots
		for(int i=1; i<=totalTimeslots; i++)
		{
			Timeslot t = new Timeslot(i);
			solution.getCurrentSolution().put(i, t);
		}
	}
	
	private void readFileStu(String filename) {
		try (BufferedReader in = new BufferedReader(new FileReader(filename)))
		{
			numberOfStudent = 0;
			List<Integer> tempList = new ArrayList<Integer>();
			String line, lastStudent, currentStudent;
			lastStudent = "";
			while ((line = in.readLine()) != null)
			{
				Scanner s = new Scanner(line);
				currentStudent = s.next();
				if(currentStudent.equals(lastStudent) || lastStudent.equals(""))
				{
					tempList.add(s.nextInt());
					lastStudent = currentStudent;
				}
				else
				{
					solution.fillConflictsMatrix(tempList);
					tempList.clear();
					numberOfStudent++;
					tempList.add(s.nextInt());
					lastStudent = currentStudent;
				}
				s.close();
			}
			solution.fillConflictsMatrix(tempList); // called once out the while to execute the last student information
		} catch(IOException e) {System.out.println(e.getMessage());}
	}

	private void fillExam(){

		int numberOfConflicts;

		for(Map.Entry<Integer, Exam> e : solution.getExams().entrySet()){
			numberOfConflicts=0;
			for(int i=0; i<solution.getExams().size(); i++ ){
				if(e.getKey() != i)
					numberOfConflicts++;
			}
			e.getValue().setNumberOfConflicts(numberOfConflicts);
			solution.getPriorityExams().add(e.getValue());
		}
	}

	//create first feasible solution
	public void firstSolution(){


	}

	private void CreateFileTxt(int [][] conflicts){
		try{
			FileOutputStream file = new FileOutputStream("conflicts.txt");
			PrintStream scrivi = new PrintStream(file);
			for(int i=0; i<conflicts.length; i++){
				String buffer = Integer.toString(i) + ":";
				for(int j=0; j<conflicts.length; j++){
					if(conflicts[i][j] != 0)
						buffer += " " + Integer.toString(j);
				}
				scrivi.println(buffer);
			}
		}
		catch (IOException e){System.out.println(e.getMessage());}
	}

	public void TabuSearch(){

	    boolean stoppingCriteria = true;
	    Move moveDone = new Move();
	    double penalty_ne=0, smallerPenalty=Integer.MAX_VALUE;
	    int stop=0;

	    double totalPenalty = TruePenalty(solution.getCurrentSolution());

	    //inizialmente la nostra current solution e la nostra best coincidono!
	    solution.setBestSolution(solution.getCurrentSolution());
	    solution.setCurrentPenalty(totalPenalty);
	    solution.setBestPenalty(totalPenalty);

	    //tempo di esecuzione del tabusearch
		long t= System.currentTimeMillis();
		long end = t+600000;

		//TABU SEARCH
		while(System.currentTimeMillis() < end){
			//puliamo i neighbor e svuotiamo la mossa
	        solution.clear();
	        moveDone = null;
	        smallerPenalty = Integer.MAX_VALUE;

	        //generiamo i neighbor
	        solution.Neighbours();

	        //per ogni neighbor generato
	        for(Map.Entry<Move, Map<Integer, Timeslot>> entry : solution.getNeighbours().entrySet()) {

				penalty_ne=TruePenalty(entry.getValue());

				//valutiamo se la mossa genera il neighbor migliore tra quelli generati
				if (penalty_ne < smallerPenalty){
					//controlliamo se la mossa è tabu
					if (solution.getTabulist().checkTabuMove(entry.getKey())) {
						//controlliamo aspiration criteria
						if (penalty_ne < solution.getBestPenalty()) {
							smallerPenalty = penalty_ne;
							moveDone = entry.getKey();
						}

					}else {
						//assegnamo la mossa alla mossa migliore e aggiorniamo penalty
						smallerPenalty = penalty_ne;
						moveDone = entry.getKey();
					}

				}
			}
			//se non prende nessuna mossa tra quelle generate (perché tutte nella tabu list)
			if(moveDone == null)
				continue;

	        //faccio la mossa e aggiorno tabulist
			solution.move(moveDone, smallerPenalty);
			solution.getTabulist().addTabuMove(moveDone);
			System.out.println("stop:" + stop++);
			//System.out.println(moveDone.toString());
			//fino a qui dovrebbe essere giusto

			//solution.print(solution.getCurrentSolution(), conflicts);
            //System.out.println("with penalty: " + solution.getCurrentPenalty()/(2*numberOfStudent));
			//System.out.println("The best penalty is: " + solution.getBestPenalty()/(2*numberOfStudent));
        }
        //System.out.println("The best solution is: " );
        //solution.print(solution.getBestSolution(), conflicts);
        //System.out.println("with penalty: " + solution.getBestPenalty());

		System.out.println("TRUE PENALTY!!!: " + solution.getBestPenalty());
    }

    private double TruePenalty(Map<Integer, Timeslot> sol){

		//	CALCOLO DELLA PENALTY SENZA CALCOLARE DUE VOLTE PER OGNI COPPIA DI ESAMI
		int i, j;
		double penalty=0;
		int[] power = new int[]{1, 2, 4, 8, 16};
		int[][] conflicts = solution.getConflicts();

		for (Map.Entry<Integer, Timeslot> entry1 : sol.entrySet()){
			for(Exam e : entry1.getValue().getExamsOfTimeslot()){
				for(Map.Entry<Integer, Timeslot> entry2 : sol.entrySet()){
					if(entry2.getKey() <= entry1.getKey() || entry2.getKey()-entry1.getKey()>5)
						continue;
					else{
						for(Exam e2 : entry2.getValue().getExamsOfTimeslot()){
							penalty+=power[5-(entry2.getKey()-entry1.getKey())] * conflicts[e.getIdExam()-1][e2.getIdExam()-1];
							//System.out.println("penalty - true :" + penalty);
						}
					}
				}
			}
		}

		//System.out.println("TRUE PENALTY!!!: " + penalty/(numberOfStudent));
		return penalty/numberOfStudent;

	}

	private void print() {
		List<Exam> lista_c = new ArrayList<>();
		int[][] conflicts = solution.getConflicts();

		for(Map.Entry<Integer, Timeslot> entry : solution.getCurrentSolution().entrySet()){
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

	}


}




























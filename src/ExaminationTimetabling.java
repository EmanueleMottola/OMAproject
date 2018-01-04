import java.io.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

public class ExaminationTimetabling {
	private Map<Integer,Exam> exams;
	private Map<Integer, Timeslot> timeslots;
	private int totalTimeslots;
	private int[][] conflicts; // indices start from 0, so for example if you want to know if exam 4 and 7 are in conflict you have to access the cell [3,6]
	private int numberOfStudent;
	private int[] examsWithNoConflicts;
	private double objValueBestSolution;	
	private DirectedWeightedMultigraph<Timeslot, DefaultWeightedEdge> grafo;
	private int time;
	
	// Constructor
	public ExaminationTimetabling() {
		
		objValueBestSolution = Double.MAX_VALUE;
		exams = new LinkedHashMap<Integer, Exam>();
		timeslots = new LinkedHashMap<Integer, Timeslot>();
		grafo = new DirectedWeightedMultigraph<Timeslot, DefaultWeightedEdge>(DefaultWeightedEdge.class);
	}
	
	public void tabuSearch(String filename) {
		Map<Integer,Exam> examsCurrent = new LinkedHashMap<Integer, Exam>();
		Map<Integer, Timeslot> timeslotsCurrent = new LinkedHashMap<Integer, Timeslot>();
		LimitedQueue<Move> tabuList = new LimitedQueue<>(80); // 
		LimitedQueue<Move> movesDoneList = new LimitedQueue<>(120);
		Move bestNeighborMove;
		Move reverseMove;
		double objValueCurrentSolution;
		long timeMillis= System.currentTimeMillis();
		long end = timeMillis + this.time;
		
		// Initialization
		for(Exam e : exams.values())
		{
			Exam eNew;
			examsCurrent.put(e.getIdExam(), eNew=new Exam(e.getIdExam(), e.getEnrolledStudents()));
			eNew.setTimeSlot(timeslots.get(e.getTimeSlot().getIdTimeSlot()));
		}
		for(Timeslot t : timeslots.values())
		{
			timeslotsCurrent.put(t.getIdTimeSlot(), new Timeslot(t.getIdTimeSlot()));
			for(Exam e : t.getExamsOfTimeslot())
				timeslotsCurrent.get(t.getIdTimeSlot()).addExamToTimeslot(examsCurrent.get(e.getIdExam()));
		}

		// tabu search algorithm
		objValueBestSolution = TruePenalty(timeslots);
		while(System.currentTimeMillis() < end) {
			for(Timeslot tOld : timeslotsCurrent.values())
			{
				bestNeighborMove = createAndEvaluateNeighborhood(examsCurrent, timeslotsCurrent, tabuList, objValueBestSolution, tOld, movesDoneList);
				if(bestNeighborMove != null)
				{
					generateNeighborSolution(bestNeighborMove); // set new best solution
					reverseMove = createReverseMove(bestNeighborMove);
					tabuList.add(reverseMove); // put the move in tabu list
					movesDoneList.add(bestNeighborMove);					
					if((objValueCurrentSolution = TruePenalty(timeslotsCurrent)) < objValueBestSolution) // check if the best solution found so far
					{
						preTS(0, timeslotsCurrent);	
						updateBestSolution(examsCurrent, timeslotsCurrent);
						objValueCurrentSolution = TruePenalty(timeslotsCurrent);
						objValueBestSolution = objValueCurrentSolution;
						System.out.println("Migliorato! --->" + objValueBestSolution/this.numberOfStudent);
						writeSolutionOnFile(filename);
					}
				}
			}
		}
	}
	
	private void updateBestSolution(Map<Integer,Exam> examsCurrent, Map<Integer, Timeslot> timeslotsCurrent) {
		for(Exam e : examsCurrent.values())
		{
			int idExam = e.getIdExam();
			exams.get(idExam).setTimeSlot(timeslots.get(e.getTimeSlot().getIdTimeSlot()));
		}
		for(Timeslot t : timeslotsCurrent.values())
		{
			Timeslot tx = timeslots.get(t.getIdTimeSlot());
			tx.getExamsOfTimeslot().clear();
			for(Exam e : t.getExamsOfTimeslot())
			{
				Exam ex = exams.get(e.getIdExam());
				tx.addExamToTimeslot(ex);
			}
		}
	}
	
	private Move createAndEvaluateNeighborhood(Map<Integer,Exam> examsCurrent, Map<Integer, Timeslot> timeslotsCurrent, LimitedQueue<Move> tabuList, double objValueBestSolution, Timeslot tOld, LimitedQueue<Move> movesDoneList) {
		List<Move> movesList = new ArrayList<>();
		int indexBestMove = -1;
		double objValueBestMove = Double.MAX_VALUE;
		double objValueCurrentNeighbor;
		
		int idTimeslotOld = tOld.getIdTimeSlot();
		for(Exam em : tOld.getExamsOfTimeslot())
		{
			int idExamM = em.getIdExam();
			if(examsWithNoConflicts[idExamM-1] != 0) // check if exam has at least one conflict
			{
				for(int i=1; i <= timeslotsCurrent.size(); i++)
				{
					if(i != idTimeslotOld)
					{
						int conflict = 0;
						Timeslot tNew = timeslotsCurrent.get(i);
						for(int j=0; j<tNew.getExamsOfTimeslot().size() && conflict==0; j++) // check if the new Timeslot is available for the Exam em
						{
							int idExamT = tNew.getExamsOfTimeslot().get(j).getIdExam();
							if(conflicts[idExamM-1][idExamT-1] != 0)
								conflict = 1;
						}
						if(conflict == 0) // the move is not conflicting
						{
							Move m = new Move(tOld, tNew, em);
							movesList.add(m);
						}
					}
				}
			}
		}

		for(int i=0; i<movesList.size(); i++)
		{
			Move m = movesList.get(i);
			generateNeighborSolution(m);
			objValueCurrentNeighbor = TruePenalty(timeslotsCurrent);
			if(objValueCurrentNeighbor < objValueBestMove)
			{
				if(!isTabuMove(tabuList, movesDoneList, m))
				{
					indexBestMove = i; // is not a tabu move so i take it
					objValueBestMove = objValueCurrentNeighbor;
				}
				else 
				{
					if(satisfiesAspirationCriterion(objValueCurrentNeighbor, objValueBestSolution)) // is  a tabu move so i consider the aspiration criteria
					{
						System.out.println("Aspiration criterion applied!");
						indexBestMove = i;
						objValueBestMove = objValueCurrentNeighbor;
					}
				}
			}
			returnToCurrentSolution(m);
		}
		
		if(indexBestMove == -1)
			return null;

		return movesList.get(indexBestMove);
	}
	
	private boolean isTabuMove(LimitedQueue<Move> tabuList, LimitedQueue<Move> movesDoneList, Move m) {
		int present;
		Move mTl;
		
		present = 0;
		if(tabuList.size() > 0)
		{
			for(int j=0; j<tabuList.size() && present==0; j++)
			{
				mTl = tabuList.get(j);
				if(!m.isNotEqual(mTl))
					present = 1;
			}
		}
		if(present != 0)
			return true;
		
		present = 0;
		if(movesDoneList.size() > 0)
		{
			for(int j=0; j<movesDoneList.size() && present==0; j++)
			{
				mTl = movesDoneList.get(j);
				if(!m.isNotEqual(mTl))
					present = 1;
			}
		}
		if(present != 0)
			return true;
		
		return false;
	}
	
	private boolean satisfiesAspirationCriterion(double objValueCurrentNeighbor, double objValueBestSolution) {
		if(objValueCurrentNeighbor < objValueBestSolution)
			return true;
		return false;
	}
	
	private Move createReverseMove(Move m) {
		Move rm = new Move(m.getNewTimeslot(), m.getOldTimeslot(), m.getExam());
		return rm;
	}
	
	private void generateNeighborSolution(Move m) {
		Exam e = m.getExam();
		
	    m.getOldTimeslot().removeExamFromTimeslot(e);
	    m.getNewTimeslot().addExamToTimeslot(e);
	    e.setTimeSlot(m.getNewTimeslot());
	}
	
	private void returnToCurrentSolution(Move m) {
		Exam e = m.getExam();
		
		m.getNewTimeslot().removeExamFromTimeslot(e);
		m.getOldTimeslot().addExamToTimeslot(e);
		e.setTimeSlot(m.getOldTimeslot());
	}	
	
	public double TruePenalty(Map<Integer, Timeslot> sol) {

		//	CALCOLO DELLA PENALTY SENZA CALCOLARE DUE VOLTE PER OGNI COPPIA DI ESAMI
		double penalty=0;
		int[] power = new int[]{1, 2, 4, 8, 16};

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

		return penalty;
	}	
	
	private void fillVectorOfExamsWithNoConflicts() {
		int conflict;
		int totExams = exams.keySet().size();
		examsWithNoConflicts = new int[totExams];

		for(int i=0; i<totExams; i++)
		{
			conflict = 0;
			for(int j=0; j<totExams && conflict == 0; j++)
				if(conflicts[i][j] != 0)
					conflict = 1;
			examsWithNoConflicts[i] = conflict;
		}			
	}

    public void preACP(){
		
    	int min0;
	
    	for (Exam e : exams.values())
		{
			min0 = Integer.MAX_VALUE;
			for(Timeslot tg : timeslots.values()){
				
				grafo.removeAllEdges(grafo.edgesOf(tg));
			}
			
			int conflict_i = 0;
			int t0 = -1;
			for(Timeslot t : timeslots.values()){
					
				conflict_i = 0;
				for(Exam ets : t.getExamsOfTimeslot()){
						
				    if(conflicts[e.getIdExam()-1][ets.getIdExam()-1]!=0)
				    {
					   conflict_i++;
				    }
				}
				if(conflict_i<2){
					t0 = t.getIdTimeSlot();
					break;
				}
				if(conflict_i<min0){
					t0 = t.getIdTimeSlot();
				}
			}
				ACP(e, timeslots.get(t0));	
		}
	}

	//colour
	private void ACP(Exam e, Timeslot t){
		
			int min = Integer.MAX_VALUE;
			Exam exam_spostato = null;
			Timeslot time_spostato = null;
			//controllo gli esami del timeslot
			for(Exam et : t.getExamsOfTimeslot())
			{
				//System.out.println(et);
				//se c'? un esame con cui ? in conflitto
				//System.out.println(grafo.containsEdge(e, et));
				//System.out.println(conflicts[e.getIdExam()-1][et.getIdExam()-1]);
				if(conflicts[e.getIdExam()-1][et.getIdExam()-1]!=0)
				{
					//verifico tutte le possibilit? per i due esami
					for(Timeslot ts : timeslots.values())
					{
						if(ts != t){
						int conflict_e = 0;
						int conflict_et = 0;
						for(Exam ets : ts.getExamsOfTimeslot()){
							
							if(conflicts[e.getIdExam()-1][ets.getIdExam()-1]!=0)
							{
								conflict_e++;
							}
							if(conflicts[et.getIdExam()-1][ets.getIdExam()-1]!=0)
							{
								conflict_et++;
							}
							
						}
						//se ? minore il numero di esami in conflitto e lo spostamento non contrasta con TL lo faccio
						if(conflict_e < min && controlloTL(e,t,ts))
						{
							min = conflict_e;
							exam_spostato = exams.get(e.getIdExam());
							time_spostato = timeslots.get(ts.getIdTimeSlot());
							
							//	System.out.println(exam_spostato.toString());
							//	System.out.println(time_spostato.toString());
						}
						//se ? minore il numero di esami in conflitto e lo spostamento non contrasta con TL lo faccio
						if(conflict_et < min && controlloTL(et,t,ts))
						{
							min = conflict_et;
							exam_spostato = exams.get(et.getIdExam());
							time_spostato = timeslots.get(ts.getIdTimeSlot());
							
							   // System.out.println(exam_spostato.toString());
							   // System.out.println(time_spostato.toString());
						}
						}	
					}
				}
			}
							
			t.addExamToTimeslot(e);
			e.setTimeSlot(t);
			
			if(time_spostato != null)
			{
				//System.out.println("spostato");
				//System.out.println(exam_spostato);
				//System.out.println(time_spostato);
				//System.out.println(min);
				//System.out.println(t.toString() + " " + time_spostato.toString() + " " + exam_spostato.getIdExam());
				Graphs.addEdgeWithVertices(grafo, t, time_spostato, (double)exam_spostato.getIdExam());
				//System.out.println(grafo.getEdge(t, time_spostato).toString());
				t.removeExamFromTimeslot(exam_spostato);
					
					if(min == 0){
						
						exam_spostato.setTimeSlot(time_spostato);
						time_spostato.addExamToTimeslot(exam_spostato);
						
						controllo_conflitti();
					}
					else{
						ACP(exam_spostato, time_spostato);
					}
				
			}
		
	}
	

	private void controllo_conflitti() {
		
		Exam e = null;
		Timeslot te = null;
		for (Timeslot t : timeslots.values()){
			
			for(Exam e1 : t.getExamsOfTimeslot()){
				for(Exam e2 : t.getExamsOfTimeslot()){
					if(!e1.equals(e2) && conflicts[e1.getIdExam()-1][e2.getIdExam()-1] != 0){
						e = exams.get(e1.getIdExam());
						te = timeslots.get(t.getIdTimeSlot());
						break;
						
					}
				}
			}
		}
		
		if (e != null){
			
			te.removeExamFromTimeslot(e);
			ACP(e, te);
		}
		
	}

	private boolean controlloTL(Exam e, Timeslot t, Timeslot ts){
		
        if(grafo.containsEdge(t, ts)){
			
			for(DefaultWeightedEdge d :grafo.getAllEdges(t, ts)){
				
				if(grafo.getEdgeWeight(d)==(double)e.getIdExam()){
					return false;
				}
			}
		}
  
		//System.out.println("controllo true");
		return true;
	}	
	
	// This public function simply call the private methods that read files and put data in their structures 
	public void fillData(String fileExm, String fileSlo, String fileStu, int time) {
		readFileExm(fileExm);
		initializeConflictsMatrix();
		readFileSlo(fileSlo);
		readFileStu(fileStu);
		fillVectorOfExamsWithNoConflicts();
		this.time = time*1000;
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
				exams.put(idExam, e);
				s.close();
			}
		} catch(IOException e) {System.out.println(e.getMessage());}
		
	}
	
	private void initializeConflictsMatrix() {
		int totExams = exams.keySet().size();
		conflicts = new int[totExams][totExams];
		for(int i=0; i<totExams; i++)
			for(int j=0; j<totExams; j++)
				conflicts[i][j] = 0;
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
			 timeslots.put(i, t);
		}
		
		Graphs.addAllVertices(grafo, timeslots.values());
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
					fillConflictsMatrix(tempList);
					tempList.clear();
					numberOfStudent++;
					tempList.add(s.nextInt());
					lastStudent = currentStudent;
				}
				s.close();
			}
			fillConflictsMatrix(tempList); // called once out the while to execute the last student information 
			numberOfStudent++;
		} catch(IOException e) {System.out.println(e.getMessage());}
	}
	
	private void fillConflictsMatrix(List<Integer> tmp) {
		int h, k;
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

	public void print() {
		List<Exam> lista_c = new ArrayList<>();
		
		for (Timeslot t : timeslots.values()){
			System.out.println(t.getExamsOfTimeslot().toString());
			for(Exam e1: t.getExamsOfTimeslot()){
				for(Exam e2: t.getExamsOfTimeslot()){
					if(!e1.equals(e2)){
						if(conflicts[e1.getIdExam()-1][e2.getIdExam()-1]!=0){
							System.out.println("Studente/i in comune tra gli esami " + e1.getIdExam() + " "+ e2.getIdExam()+ " (valore conflicts: "+conflicts[e1.getIdExam()-1][e2.getIdExam()-1]+") in " +t.toString());
						}
					}
				}
			}
		}
		for(Exam e : exams.values()){
			boolean p = false;
			for (Timeslot t: timeslots.values()){
				if(t.getExamsOfTimeslot().contains(e)){
					if(p){
						System.out.println(e.toString()+" gi? presente");
					}
					p = true;
				}
			}
			if(!p){
				lista_c.add(e);
			}
		}
		System.out.println("lista esami non inseriti nei timeslots: " +lista_c.toString());
		
	}

	public int ObjFuncFirstSolution() {
		int totalUnfeasibility=0;
		int idE1, idE2;
		List<Exam> listClone;
		
		for(Timeslot t : timeslots.values())
		{
			listClone = t.getExamsOfTimeslot();
			for(int i=0; i<listClone.size()-1; i++)
			{
				for(int j=i+1; j<listClone.size(); j++)
				{
					idE1 = listClone.get(i).getIdExam();
					idE2 = listClone.get(j).getIdExam();
					if(conflicts[idE1-1][idE2-1] != 0)
					{
						totalUnfeasibility++;
					}
				}
			}
		}
		
		return totalUnfeasibility;
	}
	
	public void preTS(int step, Map<Integer, Timeslot> timeslots){
		
		List<Exam> spostamento = new ArrayList<>();
		boolean migliorato = false;
		int ts1 = 0;
		int ts2 = 0;
		double newP = 0;
		
		
		for(Timeslot t1 : timeslots.values()){
			
			for(Timeslot t2 : timeslots.values()){
				
				spostamento.clear();
				
				if(!t2.equals(t1)){
					
					
					spostamento.addAll(t1.getExamsOfTimeslot());
					t1.getExamsOfTimeslot().clear();
					//System.out.println(spostamento.toString());
					//System.out.println(t1.getExamsOfTimeslot().toString());
					t1.getExamsOfTimeslot().addAll(t2.getExamsOfTimeslot());
					//System.out.println(spostamento.toString());
					//System.out.println(t1.getExamsOfTimeslot().toString());
					t2.getExamsOfTimeslot().clear();
					//System.out.println(spostamento.toString());
					//System.out.println(t2.getExamsOfTimeslot().toString());
					t2.getExamsOfTimeslot().addAll(spostamento);
					//System.out.println(spostamento.toString());
					//System.out.println(t2.getExamsOfTimeslot().toString());
					newP = TruePenalty(timeslots);
					//System.out.println(t1.toString()+ " "+t2.toString() );
					//print();
					
					if(newP < this.objValueBestSolution){
						this.objValueBestSolution = newP;
						System.out.println("Scambio " +	newP/this.numberOfStudent );

						ts1 = t1.getIdTimeSlot();
						ts2 = t2.getIdTimeSlot();
						//print();
						
					}
					
					spostamento.clear();	
					spostamento.addAll(t1.getExamsOfTimeslot());
					t1.getExamsOfTimeslot().clear();
					t1.getExamsOfTimeslot().addAll(t2.getExamsOfTimeslot());
					t2.getExamsOfTimeslot().clear();
					t2.getExamsOfTimeslot().addAll(spostamento);
					//print();
					
				}
				
			}
		}
		if(ObjFuncFirstSolution() != 0){
			System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAaARGH!!");
		}
		//System.out.println("fine preTS");
		//print();
		if(ts1 != 0){
			
			Timeslot t1 = timeslots.get(ts1);
			Timeslot t2 = timeslots.get(ts2);
			migliorato = true;
			spostamento.clear();	
			spostamento.addAll(t1.getExamsOfTimeslot());
			t1.getExamsOfTimeslot().clear();
			t1.getExamsOfTimeslot().addAll(t2.getExamsOfTimeslot());
			t2.getExamsOfTimeslot().clear();
			t2.getExamsOfTimeslot().addAll(spostamento);
			
			for(Exam e : t1.getExamsOfTimeslot()){
				e.setTimeSlot(t1);
			}
			for(Exam e : t2.getExamsOfTimeslot()){
				e.setTimeSlot(t2);
			}
		}
		//System.out.println(TruePenalty(timeslots));
		if(migliorato){
			preTS(step+1, timeslots);
		}
		
		
	}
	
	
	private void writeSolutionOnFile(String filename) {
		try (PrintWriter out = new PrintWriter(new FileWriter(filename)))
		{
			String line;
			
			for(Exam e : exams.values())
			{
				line = e.getIdExam() + " " + e.getTimeSlot().getIdTimeSlot();
				out.println(line);
			}

		} catch(IOException e) {System.out.println(e.getMessage());}
	}

	/**
	 * @return the timeslots
	 */
	public Map<Integer, Timeslot> getTimeslots() {
		return timeslots;
	}
	
	
}

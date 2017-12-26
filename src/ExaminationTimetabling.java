

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
	private int[][] conflicts; 
	private int numberOfStudent;
    private DirectedWeightedMultigraph<Timeslot, DefaultWeightedEdge> grafo;

	double LocaltotalPenalty = 0;
	double totalPenalty = 0;
	// indexex start from 0, so for example if you want to know if exam 4 and 7 are in conflict you have to access the cell [3,6]
	

	// Constructor
	public ExaminationTimetabling() {
		/*listaE = new ArrayList<>();
		exams = new LinkedHashMap<Integer, Exam>();
		timeslots = new LinkedHashMap<Integer, Timeslot>();*/
		grafo = new DirectedWeightedMultigraph<Timeslot, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		solution = new Solution();
	}
	
	/* PUT THE ALGORITHM FUNCTION'S HERE*/
	/* PUT THE ALGORITHM FUNCTION'S HERE*/



	public double CalculatePenalty(){
		int t1=0, t2=0, n;
	    double penalty;
	    int[] power = {1, 2, 4, 8, 16};

	    this.totalPenalty = 0;

	    for(Map.Entry<Integer, Exam> e1 : solution.getExams().entrySet()){
	        for(Map.Entry<Integer, Exam> e2 : solution.getExams().entrySet()){
                if( !e1.getValue().equals(e2.getValue()) ){
                    if(conflicts[e1.getValue().getIdExam()-1][e2.getValue().getIdExam()-1] != 0){
                        t1 = e1.getValue().getTimeSlot().getIdTimeSlot();
                        t2 = e2.getValue().getTimeSlot().getIdTimeSlot();

                        if(t1 > t2){
                            n = t1 - t2;
                        }
                        else{
                            n = t2 - t1;
                        }
                        //System.out.println(n);

                        if ( n > 5){
                            penalty = 0;
                        }
                        else{
                            penalty = conflicts[e1.getValue().getIdExam()-1][e2.getValue().getIdExam()-1] * power[5-n];
                            //System.out.println(penalty);
                        }

                        this.totalPenalty += penalty;
                    }
                }
            }
        }

        System.out.println("Total penalty is :" + totalPenalty/numberOfStudent);
        this.LocaltotalPenalty = totalPenalty;
        return this.totalPenalty;
    }

	
    public void preACP(){
		
    	int min0;

    	for(Map.Entry<Integer, Exam> e : solution.getExams().entrySet()){
            min0 = Integer.MAX_VALUE;
            for(Map.Entry<Integer, Timeslot> t : solution.getCurrentSolution().entrySet()){
                grafo.removeAllEdges(grafo.edgesOf(t.getValue()));
            }

            int conflict_i = 0;
            int t0 = -1;
            for(Map.Entry<Integer, Timeslot> t : solution.getCurrentSolution().entrySet()){

                conflict_i = 0;
                for(Exam ets : t.getValue().getExamsOfTimeslot()){

                    if(conflicts[e.getValue().getIdExam()-1][ets.getIdExam()-1]!=0)
                    {
                        conflict_i++;
                    }
                }
                if(conflict_i<2){
                    t0 = t.getValue().getIdTimeSlot();
                    break;
                }
                if(conflict_i<min0){
                    t0 = t.getValue().getIdTimeSlot();
                }
            }
            ACP(e.getValue(), solution.getCurrentSolution().get(t0), solution.getCurrentSolution());
        }
	}

	//colour
	private void ACP(Exam e, Timeslot t, Map<Integer, Timeslot> listaT){
		
			int min = Integer.MAX_VALUE;
			Exam exam_spostato = null;
			Timeslot time_spostato = null;
			//controllo gli esami del timeslot
			for(Exam et : t.getExamsOfTimeslot())
			{
				//System.out.println(et);
				//se c'� un esame con cui � in conflitto
				//System.out.println(grafo.containsEdge(e, et));
				//System.out.println(conflicts[e.getIdExam()-1][et.getIdExam()-1]);
				if(conflicts[e.getIdExam()-1][et.getIdExam()-1]!=0)
				{
					//verifico tutte le possibilit� per i due esami
					for(Map.Entry<Integer, Timeslot> ts : listaT.entrySet())
					{
						if(ts.getValue() != t){
						int conflict_e = 0;
						int conflict_et = 0;
						for(Exam ets : ts.getValue().getExamsOfTimeslot()){
							
							if(conflicts[e.getIdExam()-1][ets.getIdExam()-1]!=0)
							{
								conflict_e++;
							}
							if(conflicts[et.getIdExam()-1][ets.getIdExam()-1]!=0)
							{
								conflict_et++;
							}
							
						}
						//se � minore il numero di esami in conflitto e lo spostamento non contrasta con TL lo faccio
						if(conflict_e < min && controlloTL(e,t,ts.getValue()))
						{
							min = conflict_e;
							exam_spostato = solution.getExams().get(e.getIdExam());
							time_spostato = solution.getCurrentSolution().get(ts.getValue().getIdTimeSlot());
							
							//	System.out.println(exam_spostato.toString());
							//	System.out.println(time_spostato.toString());
						}
						//se � minore il numero di esami in conflitto e lo spostamento non contrasta con TL lo faccio
						if(conflict_et < min && controlloTL(et,t,ts.getValue()))
						{
							min = conflict_et;
							exam_spostato = solution.getExams().get(et.getIdExam());
							time_spostato = solution.getCurrentSolution().get(ts.getValue().getIdTimeSlot());
							
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
						ACP(exam_spostato, time_spostato, listaT);
					}
				
			}
		
	}
	

	private void controllo_conflitti() {
		
		Exam e = null;
		Timeslot te = null;
		for (Map.Entry<Integer, Timeslot> t: solution.getCurrentSolution().entrySet()){
			
			for(Exam e1 : t.getValue().getExamsOfTimeslot()){
				for(Exam e2 : t.getValue().getExamsOfTimeslot()){
					if(!e1.equals(e2) && conflicts[e1.getIdExam()-1][e2.getIdExam()-1] != 0){
						e = solution.getExams().get(e1.getIdExam());
						te = solution.getCurrentSolution().get(t.getValue().getIdTimeSlot());
						break;
						
					}
				}
			}
		}
		
		if (e != null){
			
			te.removeExamFromTimeslot(e);
			ACP(e, te, solution.getCurrentSolution());
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
	public void fillData(String fileExm, String fileSlo, String fileStu) {
		readFileExm(fileExm);
		initializeConflictsMatrix();
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
				//exams.put(idExam, e);
				//listaE.add(e);
				s.close();
			}
		} catch(IOException e) {System.out.println(e.getMessage());}
		
	}
	
	private void initializeConflictsMatrix() {
		int totExams = solution.getExams().keySet().size();
		conflicts = new int[totExams][totExams];
		for(int i=0; i<totExams; i++)
			for(int j=0; j<totExams; j++)
				conflicts[i][j] = 0;
	}
	
	private void readFileSlo(String filename) {

        List<Timeslot> listaT = new ArrayList<>();

		try (BufferedReader in = new BufferedReader(new FileReader(filename)))
		{
			String line = in.readLine();
			totalTimeslots = Integer.parseInt(line);
		} catch(IOException e) {System.out.println(e.getMessage());}
		// I fill the Map with the timeslots
		//listaT = new ArrayList<>();
		for(int i=1; i<=totalTimeslots; i++)
		{
			Timeslot t = new Timeslot(i);
			solution.getCurrentSolution().put(i, t);
			//timeslots.put(i, t);
			listaT.add(t);
		}
		
		Graphs.addAllVertices(grafo, listaT);
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
		} catch(IOException e) {System.out.println(e.getMessage());}
	}
	
	private void fillConflictsMatrix(List<Integer> tmp) {
		int h, k;
		System.out.println(tmp.size());
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
		
		/*for (Timeslot t : listaT){
			System.out.println(t.getExamsOfTimeslot().toString());
			for(Exam e1: t.getExamsOfTimeslot()){
				for(Exam e2: t.getExamsOfTimeslot()){
					if(!e1.equals(e2)){
						if(conflicts[e1.getIdExam()-1][e2.getIdExam()-1]!=0){
							System.out.println("Studente/i in comune tra gli esami " + e1.getIdExam() + " "+
                                    e2.getIdExam()+ " (valore conflicts: "+conflicts[e1.getIdExam()-1][e2.getIdExam()-1]+") in " +t.toString());
						}
					}
				}
			}
		}*/

		for(Map.Entry<Integer, Exam> entry1 : solution.getExams().entrySet()){
		    boolean p = false;
		    for(Map.Entry<Integer, Timeslot> entry2 : solution.getCurrentSolution().entrySet()){
		        if(entry2.getValue().getExamsOfTimeslot().contains(entry1.getValue())){
		            if(p){
		                System.out.println(entry1.getValue() + "già presente");
                    }
                    p = true;
                }
            }
            if(!p){
		        lista_c.add(entry1.getValue());
            }
        }

        System.out.println("lista esami non inseriti nei timeslots: " +lista_c.toString());

		/*for(Exam e : listaE){
			boolean p = false;
			for (Timeslot t: listaT){
				if(t.getExamsOfTimeslot().contains(e)){
					if(p){
						System.out.println(e.toString()+" gi� presente");
					}
					p = true;
				}
			}
			if(!p){
				lista_c.add(e);
			}
		}
		System.out.println("lista esami non inseriti nei timeslots: " +lista_c.toString());*/
		
	}

	public int ObjFuncFirstSolution() {
		int totalUnfeasibility=0;
		int idE1, idE2;
		List<Exam> listClone;

		for(Map.Entry<Integer, Timeslot> entry : solution.getCurrentSolution().entrySet()){
		    listClone = entry.getValue().getExamsOfTimeslot();
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
		
		/*for(Timeslot t : timeslots.values())
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
		}*/
		
		return totalUnfeasibility;
	}
	
	public void preTS(){

	    int iterazioni = solution.getCurrentSolution().size();
		//int iterazioni = listaT.size();

        for(Map.Entry<Integer, Timeslot> entry : solution.getCurrentSolution().entrySet()){
            for(DefaultWeightedEdge d : grafo.edgesOf(entry.getValue())){
                grafo.removeEdge(d);
            }
        }

		/*for(Timeslot t : listaT){
			for(DefaultWeightedEdge d : grafo.edgesOf(t)){
				grafo.removeEdge(d);
			}
		}*/
		
		TS(iterazioni);
	}
	
	private void TS(int iterazioni){
		
		double minPenalty = Double.MAX_VALUE;
		double Penalty = this.LocaltotalPenalty;
		Exam exam_spostato = null;
		Timeslot time_spostato = null;
		boolean p = true;
		boolean noTL = false;
		
		if(iterazioni==0){
			System.out.println("Finito");
		}
		else{
		    for(Map.Entry<Integer, Exam> e : solution.getExams().entrySet()){
		        for(Map.Entry<Integer, Timeslot> t : solution.getCurrentSolution().entrySet()){
		            p = true;
		            if(!t.getValue().getExamsOfTimeslot().contains(e.getValue())){
		                for(Exam ep : t.getValue().getExamsOfTimeslot()){
                            if(conflicts[ep.getIdExam()-1][e.getValue().getIdExam()-1] != 0){
                                p = false;
                                break;
                            }
		                }
                    }
                    else{
		                p = false;
                    }
                    if(p){
                        Penalty = Penalty - CalculateSinglePenalty(e.getValue(), e.getValue().getTimeSlot());
                        Penalty = Penalty + CalculateSinglePenalty(e.getValue(), t.getValue());
                        //System.out.println(Penalty);
                        if(Penalty < minPenalty && controlloTS(e.getValue(), e.getValue().getTimeSlot(),t.getValue())){

                            minPenalty = Penalty;
                            exam_spostato = solution.getExams().get(e.getValue().getIdExam());
                            time_spostato = solution.getCurrentSolution().get(t.getValue().getIdTimeSlot());
                            noTL = false;
                        }
                        else{
                            if (Penalty < minPenalty && Penalty < this.totalPenalty){

                                minPenalty = Penalty;
                                exam_spostato = solution.getExams().get(e.getValue().getIdExam());
                                time_spostato = solution.getCurrentSolution().get(t.getValue().getIdTimeSlot());
                                noTL = true;
                            }
                        }
                    }
                }
            }
            if(time_spostato != null){

                //System.out.println(grafo.toString());
                //System.out.println(exam_spostato.getTimeSlot().toString());
                //System.out.println(time_spostato.toString());
                //System.out.println(exam_spostato.toString());
                if(!noTL){
                    Graphs.addEdge(grafo, exam_spostato.getTimeSlot(), time_spostato, (double)exam_spostato.getIdExam());
                }

                exam_spostato.getTimeSlot().removeExamFromTimeslot(exam_spostato);
                time_spostato.addExamToTimeslot(exam_spostato);
                exam_spostato.setTimeSlot(time_spostato);
                LocaltotalPenalty = LocaltotalPenalty + minPenalty;

                if(ObjFuncFirstSolution()==0){

                    if(LocaltotalPenalty < totalPenalty){

                        //System.out.println("Spostato "+ exam_spostato.toString());
                        //printare il file
                        totalPenalty = LocaltotalPenalty;

                    }
                    //System.out.println(iterazioni);
                    //iterazioni--;
                    TS(iterazioni);

                }
            }
	    }
	}
	
	private boolean controlloTS(Exam e, Timeslot ts, Timeslot t) {
		 
		if(grafo.containsEdge(t, ts)){
				
				for(DefaultWeightedEdge d :grafo.getAllEdges(t, ts)){
					
					if(grafo.getEdgeWeight(d)==(double)e.getIdExam()){
						return false;
					}
				}
			}
		
		return true;
	}

	private double CalculateSinglePenalty(Exam e, Timeslot t) {
		
		 int t2=0, n;
		 double total = 0, penalty;
		 int[] power = {1, 2, 4, 8, 16};

		 for(Map.Entry<Integer, Exam> e2 : solution.getExams().entrySet()){
             if( !e.equals(e2.getValue()) ){
                 if(conflicts[e.getIdExam()-1][e2.getValue().getIdExam()-1] != 0){

                     t2 = e2.getValue().getTimeSlot().getIdTimeSlot();

                     if(t.getIdTimeSlot() > t2) {
                         n = t.getIdTimeSlot() - t2;
                     }
                     else{
                         n = t2 - t.getIdTimeSlot();
                     }
                     //System.out.println(n);

                     if ( n > 5){
                         penalty = 0;
                     }
                     else{
                         penalty = conflicts[e.getIdExam()-1][e2.getValue().getIdExam()-1] * power[5-n];
                         //System.out.println(penalty);
                     }

                     total += penalty;
                 }
             }
         }
		 return total;
	}

	public void TabuSearch(){

	    boolean stoppingCriteria = true;

	    solution.computePenaltyExam(conflicts);
	    solution.setBestSolution(solution.getCurrentSolution());

	    while(stoppingCriteria){
	        solution.clear();
	        solution.move(conflicts);

        }
    }

}




























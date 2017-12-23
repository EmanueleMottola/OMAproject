package assignment;

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
	private List<Exam> listaE;
	private Map<Integer, Timeslot> timeslots;
	private List<Timeslot> listaT;
	private int totalTimeslots;
	private int[][] conflicts; 
	private int numberOfStudent;
	
	double LocaltotalPenalty = 0;
	double totalPenalty = 0;
	// indexex start from 0, so for example if you want to know if exam 4 and 7 are in conflict you have to access the cell [3,6]
	
	private DirectedWeightedMultigraph<Timeslot, DefaultWeightedEdge> grafo;
	// Constructor
	public ExaminationTimetabling() {
		listaE = new ArrayList<>();
		exams = new LinkedHashMap<Integer, Exam>();
		timeslots = new LinkedHashMap<Integer, Timeslot>();
		grafo = new DirectedWeightedMultigraph<Timeslot, DefaultWeightedEdge>(DefaultWeightedEdge.class);
	}
	
	/* PUT THE ALGORITHM FUNCTION'S HERE*/
	/* PUT THE ALGORITHM FUNCTION'S HERE*/

	public double CalculatePenalty(){
	    int t1=0, t2=0, n;
	    double penalty;
	    int[] power = {1, 2, 4, 8, 16};

	    this.totalPenalty = 0;
	    for(Exam e1 : listaE){
	        for(Exam e2 : listaE){
	            if( !e1.equals(e2) ){
	            	 if(conflicts[e1.getIdExam()-1][e2.getIdExam()-1] != 0){
	            		 
	            		 t1 = e1.getTimeSlot().getIdTimeSlot();
	            		 t2 = e2.getTimeSlot().getIdTimeSlot();
	            		 
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
                            penalty = conflicts[e1.getIdExam()-1][e2.getIdExam()-1] * power[5-n];
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
	
    	for (Exam e : listaE)
		{
			min0 = Integer.MAX_VALUE;
			for(Timeslot tg : listaT){
				
				grafo.removeAllEdges(grafo.edgesOf(tg));
			}
			
			int conflict_i = 0;
			int t0 = -1;
			for(Timeslot t : listaT){
					
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
				ACP(e, timeslots.get(t0), listaT);	
		}
	}

	//colour
	private void ACP(Exam e, Timeslot t, List<Timeslot> listaT){
		
			int min = Integer.MAX_VALUE;
			Exam exam_spostato = null;
			Timeslot time_spostato = null;
			//controllo gli esami del timeslot
			for(Exam et : t.getExamsOfTimeslot())
			{
				//System.out.println(et);
				//se c'è un esame con cui è in conflitto
				//System.out.println(grafo.containsEdge(e, et));
				//System.out.println(conflicts[e.getIdExam()-1][et.getIdExam()-1]);
				if(conflicts[e.getIdExam()-1][et.getIdExam()-1]!=0)
				{
					//verifico tutte le possibilità per i due esami
					for(Timeslot ts : listaT)
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
						//se è minore il numero di esami in conflitto e lo spostamento non contrasta con TL lo faccio
						if(conflict_e < min && controlloTL(e,t,ts))
						{
							min = conflict_e;
							exam_spostato = exams.get(e.getIdExam());
							time_spostato = timeslots.get(ts.getIdTimeSlot());
							
							//	System.out.println(exam_spostato.toString());
							//	System.out.println(time_spostato.toString());
						}
						//se è minore il numero di esami in conflitto e lo spostamento non contrasta con TL lo faccio
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
						ACP(exam_spostato, time_spostato, listaT);
					}
				
			}
		
	}
	

	private void controllo_conflitti() {
		
		Exam e = null;
		Timeslot te = null;
		for (Timeslot t : listaT){
			
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
			ACP(e, te, listaT);
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
				exams.put(idExam, e);
				listaE.add(e);
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
		listaT = new ArrayList<>();
		for(int i=1; i<=totalTimeslots; i++)
		{
			Timeslot t = new Timeslot(i);
			 timeslots.put(i, t);
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
		
		for (Timeslot t : listaT){
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
		for(Exam e : listaE){
			boolean p = false;
			for (Timeslot t: listaT){
				if(t.getExamsOfTimeslot().contains(e)){
					if(p){
						System.out.println(e.toString()+" già presente");
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
	
	public void preTS(){
		
		int iterazioni = listaT.size();
		
		for(Timeslot t : listaT){
			for(DefaultWeightedEdge d : grafo.edgesOf(t)){
				grafo.removeEdge(d);
			}
		}
		
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
		for (Exam e : listaE){
			for(Timeslot t: listaT){
				p = true;
				if(!t.getExamsOfTimeslot().contains(e)){
					for(Exam ep : t.getExamsOfTimeslot()){
						
						if(conflicts[ep.getIdExam()-1][e.getIdExam()-1] != 0){
							p = false;
							break;
						}
					
					}
				}
				else{
					p = false;
				}
				if(p ){

					Penalty = Penalty - CalculateSinglePenalty(e, e.getTimeSlot());
					Penalty = Penalty + CalculateSinglePenalty(e, t);
					//System.out.println(Penalty);
					if(Penalty < minPenalty && controlloTS(e, e.getTimeSlot(),t)){
						
						minPenalty = Penalty;
						exam_spostato = exams.get(e.getIdExam());
						time_spostato = timeslots.get(t.getIdTimeSlot());
						noTL = false;
					}
					else{
						if (Penalty < minPenalty && Penalty < this.totalPenalty){
							
							minPenalty = Penalty;
							exam_spostato = exams.get(e.getIdExam());
							time_spostato = timeslots.get(t.getIdTimeSlot());
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

		     for(Exam e2 : listaE){
		         if( !e.equals(e2) ){
		            	 if(conflicts[e.getIdExam()-1][e2.getIdExam()-1] != 0){
		            		 
		            		 t2 = e2.getTimeSlot().getIdTimeSlot();
		            		 
		            		 if(t.getIdTimeSlot() > t2){
		            			 
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
	                            penalty = conflicts[e.getIdExam()-1][e2.getIdExam()-1] * power[5-n];
	                            //System.out.println(penalty);
	                        }
	                       
	                        total += penalty;
	                    }
	            }

	        }
	        return total;
		
	}
	
}

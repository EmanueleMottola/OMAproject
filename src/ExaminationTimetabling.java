import java.io.*;
import java.util.*;
import org.coinor.opents.*;

public class ExaminationTimetabling {
	private Map<Integer,Exam> exams;
	//private List<Exam> listaE;
	private Map<Integer, Timeslot> timeslots;
	//private List<Timeslot> listaT;
	private int totalTimeslots;
	private int[][] conflicts;
	private int numberOfExams;
	private int numberOfStudent;
	// indexex start from 0, so for example if you want to know if exam 4 and 7 are in conflict you have to access the cell [3,6]
	
	// Constructor
	public ExaminationTimetabling() {
		exams = new LinkedHashMap<Integer, Exam>();
		timeslots = new LinkedHashMap<Integer, Timeslot>();
	}
	
	/* PUT THE ALGORITHM FUNCTION'S HERE*/


	/*public void TS(){
		ObjectiveFunction objFunc = new MyObjectiveFunction( timeslots );
		Solution initialSolution  = new MyGreedyStartSolution( timeslots, exams, conflicts );
		MoveManager   moveManager = new MyMoveManager();
		TabuList         tabuList = new SimpleTabuList( 7 ); // In OpenTS package

		// Create Tabu Search object
		TabuSearch tabuSearch = new SingleThreadedTabuSearch(
				initialSolution,
				moveManager,
				objFunc,
				tabuList,
				new BestEverAspirationCriteria(), // In OpenTS package
				false ); // maximizing = yes/no; false means minimizing

		// Start solving
		tabuSearch.setIterationsToGo( 100 );
		tabuSearch.startSolving();

		// Show solution
		MySolution best = (MySolution)tabuSearch.getBestSolution();
		System.out.println( "Best Solution:\n" + best );
	}*/



	public double ObjFunc(Map<Integer,Timeslot> tclone, Map<Integer, Exam> eclone){
	    int t1=0, t2=0, n;
	    double totalPenalty = 0, penalty=0;
	    int[] power = new int[]{1, 2, 4 ,8 ,16};

	    for(int i=0; i < eclone.size(); i++){
	        for(int j=i; j < eclone.size(); j++){
	            if( i != j ){
                    if(conflicts[i][j] != 0){

                        System.out.println(eclone.get(i+1).toString());
                        t1 = eclone.get(i+1).getTimeSlot().getIdTimeSlot();
                        t2 = eclone.get(j+1).getTimeSlot().getIdTimeSlot();


                        if(t1 > t2){
                            n = t1- t2;
                        }
                        else
                            n = t2 - t1;

                        if ( n > 5)
                            penalty = 0;
                        else
                            penalty = conflicts[i][j] * power[(5-n)] / numberOfStudent;


                        totalPenalty += penalty;
                    }
                }

            }
        }
        System.out.println("Total penalty is :" + totalPenalty);
        return totalPenalty;
    }

    public void tabusearch(){
	    double penalty=0;
        double oldpenalty=0;

        //inserire ciclo

	    //clone
        Map<Integer,Timeslot> tclone = new HashMap<Integer, Timeslot>(timeslots);
        Map<Integer,Exam> eclone = new HashMap<Integer, Exam>(exams);
        //generation(check feasibility)
        neighborGen(tclone,eclone);

        //evaluate su tclone
        penalty=ObjFunc(tclone,eclone);
        //swap con tclone se ? migliore
        //se swap -> inseriamo tabu list


    }



    public void neighborGen(Map<Integer, Timeslot> tclone, Map<Integer, Exam> eclone) {
        int feasible = 0;
        Random RandomNum=new Random(totalTimeslots);
        int rand_timeslot1 = 0;
        int rand_timeslot2 = 0;
        int rand_exam1 = 0;
        int rand_exam2 = 0;
        Exam e1 = new Exam(0, 0);
        Exam e2 = new Exam(0, 0);

        //generation con swap
        while (feasible == 0) {

            rand_timeslot1 = 0;
            rand_timeslot2 = 0;
            while (rand_timeslot1 == rand_timeslot2) {
                //we take two random timeslots
                rand_timeslot1 = RandomNum.nextInt(totalTimeslots);
                rand_timeslot2 = RandomNum.nextInt(totalTimeslots);

            }
            //we choose randomly two exams of the timeslots selected before
            rand_exam1 = RandomNum.nextInt(tclone.get(rand_timeslot1).getExamsOfTimeslot().size());
            rand_exam2 = RandomNum.nextInt(tclone.get(rand_timeslot2).getExamsOfTimeslot().size());

            //before swapping on the clone structure we loook at the feasibility
            feasible = checkfeasibility(rand_timeslot1, rand_timeslot2, rand_exam1, rand_exam2);

            if (feasible == 1) {
                //UPDATE OF THE TIMESLOTS' MAP
                //we take the two exams from the two timestlots using random values
                e1 = tclone.get(rand_timeslot1).getExamsOfTimeslot().get(rand_exam1);
                e2 = tclone.get(rand_timeslot2).getExamsOfTimeslot().get(rand_exam2);

                //we remove from the timeslots the exams we want to swap
                tclone.get(rand_timeslot1).removeExamFromTimeslot(e1);
                tclone.get(rand_timeslot2).removeExamFromTimeslot(e2);

                //we update the new exams' timeslots field value
                e1.setTimeSlot(tclone.get(rand_timeslot2));
                e2.setTimeSlot(tclone.get(rand_timeslot1));

                //now we add the exams to their new timeslots
                tclone.get(rand_timeslot1).addExamToTimeslot(e2);
                tclone.get(rand_timeslot2).addExamToTimeslot(e1);

                //---------------------------------------------------------------------------

                //we update the exams' map
                eclone.get(e1.getIdExam()).setTimeSlot(tclone.get(rand_timeslot2));
                eclone.get(e2.getIdExam()).setTimeSlot(tclone.get(rand_timeslot1));

            }

        }
    }

    private int checkfeasibility(int rndt1, int rndt2, int rnde1, int rnde2){
        int idExam=0;

        for(Exam e : timeslots.get(rndt1).getExamsOfTimeslot()){
            idExam = e.getIdExam();

            if(conflicts[rnde2-1][idExam-1]!=0){
                return 0;
            }
        }

        for(Exam e : timeslots.get(rndt2).getExamsOfTimeslot()){
            idExam = e.getIdExam();

            if(conflicts[rnde1-1][idExam-1]!=0){
                return 0;
            }
        }


        return 1;
    }

	public void preACP(){

		List<Timeslot> listaTimeslots = new ArrayList<>(timeslots.values());

		for (Map.Entry<Integer, Exam> entry : exams.entrySet())
		{
			ACP(entry.getValue(), (Timeslot) timeslots.values().toArray()[0], listaTimeslots);
		}
	}
	//colour
	private void ACP(Exam e, Timeslot t, List<Timeslot> listaT){
		
			int min = Integer.MAX_VALUE;
			Exam exam_spostato = null;
			Timeslot time_spostato = null;
			
			//controllo gli esami del timeslot
			for(Exam et : t.getExamsOfTimeslot() )
			{
				//System.out.println(et);
				//se c'? un esame con cui ? in conflitto
				//System.out.println(grafo.containsEdge(e, et));
				//System.out.println(conflicts[e.getIdExam()-1][et.getIdExam()-1]);
				if(conflicts[e.getIdExam()-1][et.getIdExam()-1]!=0)
				{
					//verifico tutte le possibilit? per i due esami
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
							//se ? minore il numero di esami in conflitto e lo spostamento non contrasta con TL lo faccio
							if(conflict_e < min && controlloTL(e,t,ts))
							{
								min = conflict_e;
								exam_spostato = e;
								time_spostato = ts;
							}
							//se ? minore il numero di esami in conflitto e lo spostamento non contrasta con TL lo faccio
							if(conflict_et < min && controlloTL(et,t,ts))
							{
								min = conflict_et;
								exam_spostato = et;
								time_spostato = ts;
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
				t.removeExamFromTimeslot(exam_spostato);
				if(min>0){
					
					ACP(exam_spostato, time_spostato, listaT);
				}
				else{
					
					exam_spostato.setTimeSlot(time_spostato);
					time_spostato.addExamToTimeslot(exam_spostato);
				}
				
			}
		
	}
	

	private static boolean controlloTL(Exam e, Timeslot t, Timeslot ts){
		
		if(e.getTime().containsEdge(ts, t))
		{
			//System.out.println("controllo false");
			return false;
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
				s.close();
			}
		} catch(IOException e) {System.out.println(e.getMessage());}
		//listaE = new ArrayList<>(exams.values());
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
		//listaT = new ArrayList<>();
		for(int i=1; i<=totalTimeslots; i++)
		{
			Timeslot t = new Timeslot(i);
			 timeslots.put(i, t);
			// listaT.add(t);
		}
		
		for (Map.Entry<Integer, Exam> entry : exams.entrySet())
	    {
	    	entry.getValue().addGrafo(new ArrayList<>(timeslots.values()));
	    }
	}

	private void readFileStu(String filename) {
		try (BufferedReader in = new BufferedReader(new FileReader(filename)))
		{
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
				    numberOfStudent++;
					fillConflictsMatrix(tempList);
					tempList.clear();
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
		// TODO Auto-generated method stub
		for (Map.Entry<Integer, Timeslot> entry : timeslots.entrySet())
		{
			System.out.println(entry.getValue().getExamsOfTimeslot().toString());
		}

	}
    public void printExamMap(){
	    System.out.println("Exam map:");
        for(Map.Entry<Integer, Exam> entry : exams.entrySet()){
            System.out.println(entry.getValue().toString());
        }
        System.out.println("List map:");
		for (Map.Entry<Integer, Exam> entry : exams.entrySet())
		{
			System.out.println(entry.getValue().toString());
		}

    }
    public void printTimeSlotMap(){
        for(Map.Entry<Integer, Timeslot> entry : timeslots.entrySet()){
            System.out.println(entry.getValue().toString());
        }
    }
}



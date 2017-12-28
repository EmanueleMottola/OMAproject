
public class EtpMain {

	public static void main(String[] args) {
		ExaminationTimetabling et = new ExaminationTimetabling();
		//et.fillData("instance01.exm", "instance01.slo", "instance01.stu");
		et.fillData("esami.txt", "timeslots.txt", "studenti.txt");
		et.preACP();
		
		et.print();
		et.CalculatePenalty();
		//et.TruePenalty(et.getSolution().getCurrentSolution());
		
		et.preTS();
		
		et.print();
		et.CalculatePenalty();
		//et.TruePenalty(et.getSolution().getCurrentSolution());

		et.TabuSearch();
	}

}
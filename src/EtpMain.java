package assignment;

public class EtpMain {

	public static void main(String[] args) {
		ExaminationTimetabling et = new ExaminationTimetabling();
		et.fillData("instance07.exm", "instance07.slo", "instance07.stu");
		//et.fillData("esami.txt", "timeslots.txt", "studenti.txt");
		et.preACP();
		
		et.print();
		et.CalculatePenalty();
		
		et.preTS();
		
		et.print();
		et.CalculatePenalty();
	}

}
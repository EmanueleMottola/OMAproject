import org.coinor.opents.*;

public class Main {

	public static void main(String[] args) {
		ExaminationTimetabling et = new ExaminationTimetabling();
		//et.fillData("instance01.exm", "instance01.slo", "instance01.stu");
		et.fillData("esami.txt", "timeslots.txt", "studenti.txt");
		et.TS();
		et.print();
		//et.printExamMap();
		//et.printTimeSlotMap();
        //et.tabusearch();

	}

}

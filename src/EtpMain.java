
public class EtpMain {

	public static void main(String[] args) {
		String fileExm, fileSlo,fileStu, fileSol;
		int time;
		ExaminationTimetabling et = new ExaminationTimetabling();
		
		fileExm = args[0] + ".exm";
		fileSlo = args[0] + ".slo";
		fileStu = args[0] + ".stu";
		fileSol = args[0] + "_OMAMZ_group14.sol";
		time = Integer.parseInt(args[1]);
		et.fillData(fileExm, fileSlo, fileStu, time);
		et.preACP();
		et.preTS(0, et.getTimeslots());
		et.tabuSearch(fileSol);
	}
}

import java.util.ArrayList;
import java.util.List;

public class Timeslot {
	private int idTimeslot;
	private List<Exam> examsTimeslot;
	
	// Constructor
	public Timeslot(int idTimeSlot) {
		this.idTimeslot = idTimeSlot;
		examsTimeslot = new ArrayList<Exam>();
	}
	
	public int getIdTimeSlot() {
		return idTimeslot;
	}
	
	public List<Exam> getExamsOfTimeslot() {
		return examsTimeslot;
	}
	
	public void addExamToTimeslot(Exam e) {
		examsTimeslot.add(e);
	}
	
	public void removeExamFromTimeslot(Exam e) {
		examsTimeslot.remove(e);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idTimeslot;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Timeslot other = (Timeslot) obj;
		if (idTimeslot != other.idTimeslot)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Timeslot{" +
				"idTimeslot=" + idTimeslot +
				", examsTimeslot=" + examsTimeslot +
				'}';
	}
}

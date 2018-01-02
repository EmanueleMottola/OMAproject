

public class Exam {
	private int idExam;
	private int enrolledStudents;
	private Timeslot assignedTimeSlot;
	private int numberOfConflicts;

	public Exam(int idExam, int enrolledStudents) {
		this.idExam = idExam;
		this.enrolledStudents = enrolledStudents;
		this.assignedTimeSlot = null;
		this.numberOfConflicts = 0;

	}
	
	public int getIdExam() {
		return idExam;
	}
	
	public int getEnrolledStudents() {
		return enrolledStudents;
	}
	
	public Timeslot getTimeSlot() {
		return assignedTimeSlot;
	}
	
	public void setTimeSlot(Timeslot timeslot) {
		assignedTimeSlot = timeslot;
	}

	public int getNumberOfConflicts() {
		return numberOfConflicts;
	}

	public void setNumberOfConflicts(int numberOfConflicts) {
		this.numberOfConflicts = numberOfConflicts;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idExam;
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
		Exam other = (Exam) obj;
		if (idExam != other.idExam)
			return false;
		return true;
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Exam " + idExam;
	}

	public int compare(Exam e1, Exam e2){
		if(e1.numberOfConflicts < e2.numberOfConflicts)
			return -1;
		if(e1.numberOfConflicts > e2.numberOfConflicts)
			return 1;
		return 0;
	}

}

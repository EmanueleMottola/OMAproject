

public class Exam {
	private int idExam;
	private int enrolledStudents;
	private Timeslot assignedTimeSlot;
	private int penalty_exam;



	public Exam(int idExam, int enrolledStudents) {
		this.idExam = idExam;
		this.enrolledStudents = enrolledStudents;
		this.assignedTimeSlot = null;
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

	public int getPenalty_exam() {
		return penalty_exam;
	}

	public void setPenalty_exam(int penalty_exam) {
		this.penalty_exam = penalty_exam;
	}

}

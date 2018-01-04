
public class Move {
	private Timeslot oldT;
	private Timeslot newT;
	private Exam e;
	
	public Move(Timeslot oldT, Timeslot newT, Exam e) {
		this.oldT = oldT;
		this.newT = newT;
		this.e = e;
	}
	
	public Timeslot getOldTimeslot() {
		return oldT;
	}
	
	public Timeslot getNewTimeslot() {
		return newT;
	}
	
	public Exam getExam() {
		return e;
	}

	public boolean isNotEqual(Move m) {
		if(oldT.getIdTimeSlot() != m.getOldTimeslot().getIdTimeSlot())
			return true;
		if(newT.getIdTimeSlot() != m.getNewTimeslot().getIdTimeSlot())
			return true;
		if(e.getIdExam() != m.getExam().getIdExam())
			return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((e == null) ? 0 : e.hashCode());
		result = prime * result + ((newT == null) ? 0 : newT.hashCode());
		result = prime * result + ((oldT == null) ? 0 : oldT.hashCode());
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
		Move other = (Move) obj;
		if (e == null) {
			if (other.e != null)
				return false;
		} else if (!e.equals(other.e))
			return false;
		if (newT == null) {
			if (other.newT != null)
				return false;
		} else if (!newT.equals(other.newT))
			return false;
		if (oldT == null) {
			if (other.oldT != null)
				return false;
		} else if (!oldT.equals(other.oldT))
			return false;
		return true;
	}
	
	
}

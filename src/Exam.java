import java.util.List;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

public class Exam {
	private int idExam;
	private int enrolledStudents;
	private Timeslot assignedTimeSlot;
	
	private SimpleDirectedGraph<Timeslot, DefaultEdge> time;
	
	public Exam(int idExam, int enrolledStudents) {
		this.idExam = idExam;
		this.enrolledStudents = enrolledStudents;
		this.assignedTimeSlot = null;
		this.time =  new SimpleDirectedGraph<Timeslot, DefaultEdge>(DefaultEdge.class);
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
	
	/**
	 * @return the tot_timeslot
	 */
	public void addGrafo(List<Timeslot> slot) {
		// TODO Auto-generated method stub
		Graphs.addAllVertices(time, slot);
	}
	/**
	 * @return the time
	 */
	public SimpleDirectedGraph<Timeslot, DefaultEdge> getTime() {
		return time;
	}
	public void setEdge(Timeslot t1, Timeslot t2) {
		// TODO Auto-generated method stub
		this.time.addEdge(t1, t2);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Exam " + idExam;
	}

}

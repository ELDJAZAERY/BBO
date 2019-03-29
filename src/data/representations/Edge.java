package data.representations;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Edge implements Comparable<Edge> {

	private Vertex one, two;
	private ArrayList<Vertex> vertices ;
	private float weight;

	public Edge(Vertex one, Vertex two) {
		this(one, two, 1);
	}

	public Edge(Vertex one, Vertex two, float weight) {
		this.one = (one.getLabel().compareTo(two.getLabel()) <= 0) ? one : two;
		this.two = (this.one == one) ? two : one;
		this.weight = weight;

		vertices = new ArrayList<>();
		vertices.add(one);
		vertices.add(two);
	}

	public Vertex getNeighbor(Vertex current) {
		if (!(current.equals(one) || current.equals(two))) {
			return null;
		}
		return (current.equals(one)) ? two : one;
	}

	public ArrayList<Vertex> getVertices(){return vertices;}

	public boolean isDomin(List<Vertex> DominVertices){
        return (DominVertices.contains(one) && DominVertices.contains(two));
    }

	public Vertex getOne() {
		return this.one;
	}

	public Vertex getTwo() {
		return this.two;
	}

	public float getWeight() {
		return this.weight;
	}

	public int compareTo(Edge other) {
		return ((int)this.weight - (int)other.weight);
	}

	public String toString() {
		return "({" + one + ", " + two + "}, " + weight + ")";
	}

	public int hashCode() {
		int min = Math.min(one.index , two.index);
		int max = Math.max(one.index , two.index);
		return (min+ "," + max).hashCode();
	}

	public boolean equals(Object other) {
		return hashCode() == other.hashCode();
	}


	public boolean contains_vertex(LinkedList<Vertex> verticesDT){
		if(verticesDT.contains(one) &&
                verticesDT.contains(two))
			return true;
		return false;
	}

}

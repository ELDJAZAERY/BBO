package data.representations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;


public class Vertex {

    private String label;
    private HashSet<Vertex> neighbors;
	private ArrayList<Edge> neighborhood;
    public int index ;

	public Vertex(String label) {
		this.label = label;
		this.index = Integer.valueOf(label);
		this.neighborhood = new ArrayList<>();
		neighbors = new HashSet<>();
	}

	public void addNeighbor(Edge edge) {
		if (this.neighborhood.contains(edge)) {
			return;
		}
		this.neighborhood.add(edge);
	}

    public void addNeighbor(Vertex neighbor) {
        neighbors.add(neighbor);
    }

    public boolean containsNeighbor(Edge other) {
		return this.neighborhood.contains(other);
	}

	public Edge getNeighbor(int index) {
		return this.neighborhood.get(index);
	}

    public void removeNeighbor(Edge e) {
        this.neighborhood.remove(e);
    }

	public int getNeighborCount() {
		return this.neighborhood.size();
	}

	public String getLabel() {
		return this.label;
	}

	public String toString() {
		return " " + label;
	}

	public int hashCode() {
		return this.label.hashCode();
	}

	public boolean equals(Object other) {
		if (!(other instanceof Vertex)) {
			return false;
		}

		Vertex v = (Vertex) other;
		return this.label.equals(v.label);
	}


    public HashSet<Edge> getDominNeighbors(List<Vertex> dominVertices) {
        HashSet<Edge> dominNeighbors = new HashSet<>();
        for(Edge edge:neighborhood){
            if(edge.isDomin(dominVertices))
                dominNeighbors.add(edge);
        }
        return dominNeighbors;
    }

    public HashSet<Vertex> getDominNeighborsv(List<Vertex> dominVertices) {
        HashSet<Vertex> dominNeighbors = new HashSet<>();
        for(Vertex v : neighbors)
            if(dominVertices.contains(v))
                dominNeighbors.add(v);

        return dominNeighbors;
    }



    public boolean isNeighbor(Vertex other){
	    return neighbors.contains(other);
    }

    public boolean isNeighbor(HashSet<Vertex> vertices){
        for(Vertex v:vertices)
            if(isNeighbor(v)) return true;
        return false;
    }

    public boolean isPurninable(HashSet<Vertex> verticeDT){
	    if(nbDominNeighbors(verticeDT)>1) return false;

	    for(Vertex neighbor:neighbors)
            if(!neighbor.haveAnotherDominNeighbor(this,verticeDT))
                return false;
	    return true;
    }

    public HashSet<Vertex> getNeighbors() {
        return neighbors;
    }

    public int nbDominNeighbors(HashSet<Vertex> verticeDT){
	    int cpt = 0;
	    for(Vertex v:verticeDT){
	        if(neighbors.contains(v))
	            cpt++;
        }
        return cpt;
    }

    public boolean haveAnotherDominNeighbor(Vertex vertex , HashSet<Vertex> verticeDT){
        for(Vertex neighbor : neighbors){
            if(verticeDT.contains(neighbor) &&
                    !neighbor.equals(vertex))
                return true;
        }
        return false;
    }


}

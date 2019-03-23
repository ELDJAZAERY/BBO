package data.representations;

import java.util.*;


public class Graph {
    
    public HashMap<String, Vertex> vertices;

    public HashMap<Integer, Edge> edges;
    
    
    public Graph() {
        this.vertices = new HashMap<>();
        this.edges = new HashMap<>();
    }


    public boolean addEdge(Vertex one, Vertex two, float weight) {
        if(one.equals(two)) {
            return false;   
        }
        
        //ensures the Edge is not in the Graph
        Edge e = new Edge(one, two, weight);
        if(edges.containsKey(e.hashCode())) {
            return false;
        }
        
        //and that the Edge isn't already incident to one of the vertices
        else if(one.containsNeighbor(e) || two.containsNeighbor(e)) {
            return false;
        }
            
        edges.put(e.hashCode(), e);
        one.addNeighbor(e);
        two.addNeighbor(e);
        return true;
    }
    

    public boolean containsEdge(Edge e){
        if(e.getOne() == null || e.getTwo() == null){
            return false;
        }
        return this.edges.containsKey(e.hashCode());
    }
    

    public float containsEdgeWeight(Edge e)
    {
        return this.edges.get(e.hashCode()).getWeight();
    }
    

    public Edge removeEdge(Edge e) {
       e.getOne().removeNeighbor(e);
       e.getTwo().removeNeighbor(e);
       return this.edges.remove(e.hashCode());
    }
    

    public Vertex getVertex(String label)
    {
        return vertices.get(label);
    }
    

    public boolean addVertex(Vertex vertex, boolean overwriteExisting) {
        Vertex current = this.vertices.get(vertex.getLabel());
        if(current != null)
        {
            if(!overwriteExisting)
            {
                return false;
            }
            
            while(current.getNeighborCount() > 0)
            {
                this.removeEdge(current.getNeighbor(0));
            }
        }
        vertices.put(vertex.getLabel(), vertex);
        return true;
    }


    public Vertex removeVertex(String label) {
        Vertex v = vertices.remove(label);
        while(v.getNeighborCount() > 0)
        {
            this.removeEdge(v.getNeighbor((0)));
        }
        return v;
    }


}

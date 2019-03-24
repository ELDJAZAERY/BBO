package data.representations;

import data.reader.Instances;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;


public class Solution {

    private static LinkedList<Integer> permutationInitial = new LinkedList<>();
    private LinkedList<Vertex> vertices;

    protected LinkedList<Integer> permutation;
    protected LinkedList<Vertex> verticesDT;

    protected float fitness;
    protected int nbEvaluations;

    protected HashSet<Edge> path;


    public Solution(Instances instances) {
        if(permutationInitial.size() != instances.NbNodes ){
            permutationInitial.clear();
            for (int j = 0; j < instances.NbNodes ; j++) {
                permutationInitial.add(j);
            }
        }

        vertices = instances.vertices;
        permutation = new LinkedList<>(permutationInitial);

        verticesDT = DominatingSet();
        connect();
        MST();
    }

    public Solution(Instances instances , LinkedList<Integer> CurrentSol) {
        vertices = instances.vertices;

        permutation = new LinkedList<>(CurrentSol);

        verticesDT = DominatingSet();
        connect();
        MST();
    }


    private LinkedList<Vertex> DominatingSet() {
        LinkedList<Vertex> dominating_set = new LinkedList<>();

        int[] temp = new int[permutation.size()];
        int computer = 0, j = 0;

        while (j < permutation.size() && computer != permutation.size()) {
            if (temp[permutation.get(j)] == 0) {
                temp[permutation.get(j)] = 1;
                computer++;
            }

            dominating_set.add(new Vertex(permutation.get(j)));
            Vertex V = vertices.get(permutation.get(j));
            for (int k = 0; k < V.getNeighborCount(); k++) {
                int v = Integer.parseInt(V.getNeighbor(k).getNeighbor(V).getLabel());

                if (temp[v] == 0) {
                    temp[v] = 1;
                    computer++;
                }
            }
            j++;

        }
        return dominating_set;
    }


    private boolean isConnected(){
        ArrayList<Vertex> vertices = new ArrayList<>(verticesDT);
        HashSet<Vertex> tempTree = new HashSet<>();

        tempTree.add(vertices.get(0));
        vertices.remove(0);

        for(int i=0;i<vertices.size();i++){
            if(vertices.get(i).isNeighbor(tempTree)){
                tempTree.add(vertices.get(i));
                vertices.remove(i);
                i=-1;
            }
        }

        return vertices.isEmpty();
    }


    public void MST(){

        path = new HashSet<>();

        Vertex nextNode;
        ArrayList<Vertex> nodes = new ArrayList<>();
        ArrayList<Edge>  ququeArcs = new ArrayList<>();

        nextNode = verticesDT.get(0);
        nodes.add(nextNode);
        ququeArcs.addAll(nextNode.getNeighbors());

        while(!nodes.containsAll(verticesDT)){
            Collections.sort(ququeArcs);
            for(Edge arc:ququeArcs) {
                if(!arc.contains(verticesDT)) continue;
                if (!nodes.contains(arc.getTwo())){
                    path.add(arc);
                    nodes.add(arc.getTwo());
                    ququeArcs.addAll(arc.getTwo().getNeighbors());
                    break;
                }else if(!nodes.contains(arc.getOne())){
                    path.add(arc);
                    nodes.add(arc.getOne());
                    ququeArcs.addAll(arc.getOne().getNeighbors());
                    break;
                }
            }
        }

        MAJ_Fitness();
    }


    private void connect(){
        while(!isConnected()){
            verticesDT.add(new Vertex(permutation.get(verticesDT.size())));
        }
    }


    public void MAJ_Fitness(){
        fitness = 0;
        if(verticesDT.size() == 0 || path.size() == 0) return;
        for(Edge edge:path){
            fitness += edge.getWeight();
        }
        nbEvaluations++;
    }

}

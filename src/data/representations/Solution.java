package data.representations;

import data.reader.Instances;

import java.util.*;


public class Solution {

    private static LinkedList<Integer> permutationInitial = new LinkedList<>();
    private LinkedList<Vertex> vertices;

    public LinkedList<Integer> permutation;
    public LinkedList<Vertex> verticesDT;

    public float fitness;
    public int nbEvaluations;

    public HashSet<Edge> path;


    public Solution(Instances instances) {
        if(permutationInitial.size() != instances.NbNodes ){
            permutationInitial.clear();
            for (int j = 0; j < instances.NbNodes ; j++) {
                permutationInitial.add(j);
            }
        }

        vertices = instances.vertices;
        permutation = new LinkedList<>(permutationInitial);
        DT();
    }

    public Solution(Instances instances , LinkedList<Integer> CurrentSol) {
        vertices = instances.vertices;
        permutation = new LinkedList<>(CurrentSol);
        DT();
    }


    public void DT(){
        verticesDT = DominatingSet();
        connect();
        pruning();
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

            dominating_set.add(vertices.get(permutation.get(j)));
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

    private void connect(){
        while(!isConnected()){
            verticesDT.add(vertices.get(permutation.get(verticesDT.size())));
        }
    }

    private boolean isConnected(){

        if(verticesDT.isEmpty()) return false;

        ArrayList<Vertex> vertices = new ArrayList<>(verticesDT);
        HashSet<Vertex> tempTree = new HashSet<>();

        tempTree.add(vertices.get(0));
        vertices.remove(0);

        Vertex tempV;
        for(int i=0;i<vertices.size();i++) {
            tempV = vertices.get(i);

            if (tempV.isNeighbor(tempTree)){
                tempTree.add(tempV);
                vertices.remove(i);
                i = -1;
            }
        }

        return vertices.isEmpty();
    }


    public void MST(){

        path = new HashSet<>();

        Vertex nextNode;
        HashSet<Vertex> nodes = new HashSet<>();
        ArrayList<Edge> ququeArcs = new ArrayList<>();

        nextNode = verticesDT.get(0);
        nodes.add(nextNode);
        ququeArcs.addAll(nextNode.getDominNeighbors(verticesDT));

        while(!nodes.containsAll(verticesDT)){
            Collections.sort(ququeArcs);
            for(Edge arc:ququeArcs) {
                if(!arc.contains_vertex(verticesDT)) continue;
                if (!nodes.contains(arc.getTwo())){
                    path.add(arc);
                    nodes.add(arc.getTwo());
                    ququeArcs.addAll(arc.getTwo().getDominNeighbors(verticesDT));
                    break;
                }else if(!nodes.contains(arc.getOne())){
                    path.add(arc);
                    nodes.add(arc.getOne());
                    ququeArcs.addAll(arc.getOne().getDominNeighbors(verticesDT));
                    break;
                }
            }
        }

        MAJ_Fitness();
    }


    public void MAJ_Fitness(){
        fitness = 0;
        if(verticesDT.size() == 0 || path.size() == 0) return;
        for(Edge edge:path){
            fitness += edge.getWeight();
        }
        nbEvaluations++;
    }

    
    private void pruning(){
        HashSet<Vertex> afterPruning = new HashSet<>(verticesDT);
        for(Vertex v : verticesDT){
            if(v.isPurninable(afterPruning))
                afterPruning.remove(v);
        }
        verticesDT = new LinkedList<>(afterPruning);
    }



    /** Alternative connect mthd **/

    public void Connect(){

        ArrayList<Vertex> DominateSet = new ArrayList<>(verticesDT);
        HashSet<Vertex> newDominateSet = new HashSet<>();

        HashSet<Vertex> path0 , path1 , path2 ;


        Vertex maxNode = getHaveMaxDominNeighbr(DominateSet);
        path0 = maxNode.getDominNeighborsv(DominateSet);

        newDominateSet.add(maxNode);
        newDominateSet.addAll(path0);

        DominateSet.remove(maxNode);
        DominateSet.removeAll(path0);


        while(!DominateSet.isEmpty()){

            maxNode = getHaveMaxDominNeighbr(DominateSet);

            path0 = getPath0(newDominateSet,DominateSet,maxNode,false);

            if(!path0.isEmpty()){
                newDominateSet.addAll(path0);
                DominateSet.removeAll(path0);
                continue;
            }


            path1 = getPath1(newDominateSet,DominateSet,maxNode,false);

            if(!path1.isEmpty()){
                newDominateSet.addAll(path1);
                DominateSet.removeAll(path1);
                continue;
            }



            path2 = getPath2(newDominateSet,DominateSet,maxNode,false);

            if(!path2.isEmpty()){
                newDominateSet.addAll(path2);
                DominateSet.removeAll(path2);
                continue;
            }

        }

        verticesDT = new LinkedList<>(newDominateSet);
    }


    public HashSet<Vertex> getPath0(HashSet<Vertex> newDominateSet , ArrayList<Vertex> DominateSet , Vertex MaxNode , boolean onlyForMAxNode){

        HashSet<Vertex> path0 = new HashSet<>();

        HashSet<Vertex>  MaxNodeDN  , tempNodeDN ;

        if(MaxNode != null && MaxNode.isNeighbor(newDominateSet)){
            MaxNodeDN = MaxNode.getDominNeighborsv(DominateSet);

            path0.add(MaxNode);
            path0.addAll(MaxNodeDN);

            return path0;
        }

        if(onlyForMAxNode) return path0;

        for(Vertex node:DominateSet){
            if(node.isNeighbor(newDominateSet)){
                tempNodeDN = node.getDominNeighborsv(DominateSet);

                path0.add(node);
                path0.addAll(tempNodeDN);

                return path0;
            }
        }

        return path0;
    }


    public HashSet<Vertex> getPath1(HashSet<Vertex> newDominateSet , ArrayList<Vertex> DominateSet , Vertex MaxNode ,boolean onlyForMAxNode){

        HashSet<Vertex> path1 = new HashSet<>();

        if(MaxNode != null){
            for(Vertex node :MaxNode.getNeighbors()){
                path1 = getPath0(newDominateSet,DominateSet,node,true);
                if(!path1.isEmpty()) {
                    path1.add(MaxNode);
                    path1.addAll(MaxNode.getDominNeighborsv(DominateSet));
                    return path1;
                }
            }
        }

        if(onlyForMAxNode) return path1;


        for(Vertex node:DominateSet){
            for(Vertex node2 : node.getNeighbors()){
                path1 = getPath0(newDominateSet,DominateSet,node2,true);
                if(!path1.isEmpty()) {
                    path1.add(node);
                    path1.addAll(node.getDominNeighborsv(DominateSet));
                    return path1;
                }
            }
        }

        return path1;
    }


    public HashSet<Vertex> getPath2(HashSet<Vertex> newDominateSet , ArrayList<Vertex> DominateSet ,Vertex MaxNode,boolean onlyForMAxNode){

        HashSet<Vertex> path2 = new HashSet<>();

        if(MaxNode != null){
            for(Vertex node :MaxNode.getNeighbors()){
                path2 = getPath0(newDominateSet,DominateSet,node,true);
                if(!path2.isEmpty()) {
                    path2.add(MaxNode);
                    path2.addAll(MaxNode.getDominNeighborsv(DominateSet));
                    return path2;
                }
            }
        }

        if(onlyForMAxNode) return path2;


        for(Vertex node:DominateSet){
            for(Vertex node2 : node.getNeighbors()){
                path2 = getPath1(newDominateSet,DominateSet,node2,true);
                if(!path2.isEmpty()) {
                    path2.add(node);
                    path2.addAll(node.getDominNeighborsv(DominateSet));
                    return path2;
                }
            }
        }

        return path2;
    }


    public Vertex getHaveMaxDominNeighbr(List<Vertex> dominSet){

        int max = 0;
        Vertex maxNode = dominSet.get(0);

        for(Vertex node:dominSet){
            int nbDN = node.getDominNeighbors(dominSet).size();
            if( nbDN > max){
                maxNode = node;
                max = nbDN;
            }
        }

        return maxNode;
    }

    
}

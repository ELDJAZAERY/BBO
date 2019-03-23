package metas.BBO;


import data.representations.Graph;
import data.representations.Vertex;

import java.util.Collections;
import java.util.LinkedList;

public class Individual {

	static int nbEval = 0 ;

	Solution sol;
	float SpeciesCount;
	float cost;
	int index;
	int eval;


	public Individual(Graph graph, LinkedList<Vertex> vertices,LinkedList<Integer> CurrentSol, int NbNodes , int index , boolean shuffle){
        Solution sol = new Solution(CurrentSol, NbNodes);
        if(shuffle)
            Collections.shuffle(sol.permutation);
        sol.DT_P(graph, vertices);
        this.sol = sol;
        this.cost = sol.fitness;
        this.index = index;
        this.eval = ++nbEval;
    }

    public Individual(Graph graph, LinkedList<Vertex> vertices, int NbNodes , int index , boolean shuffle){
        Solution sol = new Solution(NbNodes);
        if(shuffle)
            Collections.shuffle(sol.permutation);
        sol.DT_P(graph, vertices);
        this.sol = sol;
        this.cost = sol.fitness;
        this.index = index;
        this.eval = ++nbEval;
    }

    public int compareTo(Object other) {
        return (int)(cost - ((Individual) other).cost);
    }

}

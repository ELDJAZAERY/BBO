package metas.BBO;


import data.representations.Solution;

import data.reader.Instances;

import java.util.Collections;
import java.util.LinkedList;


public class Individual {

	static int nbEval = 0 ;

	Solution sol;
	float SpeciesCount;
	float cost;
	int index;
	int eval;


	public Individual(Instances instances,LinkedList<Integer> CurrentSol,int index , boolean shuffle){
        Solution sol = new Solution(instances , CurrentSol);
        if(shuffle){
            Collections.shuffle(sol.permutation);
            sol.DT();
        }
        this.sol = sol;
        this.cost = sol.fitness;
        this.index = index;
        this.eval = ++nbEval;
    }

    public Individual( Instances instances, int index , boolean shuffle){
        Solution sol = new Solution(instances);
        if(shuffle){
            Collections.shuffle(sol.permutation);
            sol.DT();
        }
        this.sol = sol;
        this.cost = sol.fitness;
        this.index = index;
        this.eval = ++nbEval;
    }

    public int compareTo(Object other) {
        return (int)(cost - ((Individual) other).cost);
    }

}

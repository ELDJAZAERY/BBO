package metas.BBO;


import data.representations.Solution;

import data.reader.Instances;

import java.util.Collections;
import java.util.LinkedList;


public class Individual implements Comparable {

	static int nbEval = 0 ;

	Solution sol;
	float SpeciesCount;
	float cost;
	int index;
	int eval;


	public Individual(LinkedList<Integer> CurrentSol,int index , boolean shuffle){
        Solution sol = new Solution(CurrentSol);
        if(shuffle){
            Collections.shuffle(sol.permutation);
            sol.Correction();
        }
        this.sol = sol;
        this.cost = sol.fitness;
        this.index = index;
        this.eval = ++nbEval;
    }

    public Individual(int index , boolean shuffle){
        Solution sol = new Solution();
        if(shuffle){
            Collections.shuffle(sol.permutation);
            sol.Correction();
        }
        this.sol = sol;
        this.cost = sol.fitness;
        this.index = index;
        this.eval = ++nbEval;
    }



    @Override
    public int compareTo(Object other) {
        return ((int)sol.fitness - (int)((Individual) other).sol.fitness);
    }

}

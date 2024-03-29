package metas.BBO;


import data.representations.Solutions.Solution;

import java.util.Collections;
import java.util.LinkedList;


public class Individual implements Comparable , Cloneable {

	static int nbEval = 0 ;

	Solution sol;
	float cost;
	int eval;

	public Individual(LinkedList<Integer> CurrentSol){
        Solution sol = new Solution(CurrentSol);

        this.sol = sol;
        this.cost = sol.fitness;

        this.eval = ++nbEval;
    }

    public Individual(){
        Solution sol = new Solution();

        this.sol = sol;
        this.cost = sol.fitness;

        this.eval = ++nbEval;
    }

    private Individual(Individual clone){
	    this.sol = (Solution) clone.sol.clone();
        this.cost = sol.fitness;
    }

    @Override
    public int compareTo(Object other) {
        return ((int)sol.fitness - (int)((Individual) other).sol.fitness);
    }

    @Override
    protected Object clone(){
        return new Individual(this);
    }
}

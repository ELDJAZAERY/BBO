package dtp_2;

public class Individual {
	
	Solution sol;
	float SpeciesCount;
	float cost;
	int index;
	int eval;
	float dist;
	
	public Individual (Solution sol, int index, int eval)
	{
		this.sol = sol;
		this.cost = sol.fitness;
		this.index = index;
		this.eval = eval;
		this.dist = 0;
	}
	
	public Individual (Solution sol, int index, float dist)
	{
		this.sol = sol;
		this.cost = sol.fitness;
		this.index = index;
		this.dist = dist;
		
		
	}
	
	public int compareTo(Object other) 
	{ 
	   	 float nombre1 = ((Individual) other).cost; 
	   	 float nombre2 = this.cost; 
	     if (nombre1 > nombre2)  return -1; 
	     else if(nombre1 == nombre2) return 0; 
	     else return 1; 
	}
	
}

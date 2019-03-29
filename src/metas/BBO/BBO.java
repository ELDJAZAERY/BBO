package metas.BBO;


import data.reader.Instances;
import data.representations.Graph;
import data.representations.Vertex;
import data.representations.Solution;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;


public class BBO {

    private int NbNodes;
    private int MaxNbGenerations; // max number of generation

    private int populationSize; // max species count, for each island
    private LinkedList<Individual> population; // the species count probability of each

    private LinkedList<Float> mu; // emigration rate

    private LinkedList<Float> lambda; // immigration rate


    private float PMutate; // initial mutation probability
    private LinkedList<Float> prob; // the species count probability of each habitat


    private Graph graph;
    private LinkedList<Vertex> vertices;
    private Instances instances;

    private Best best;

    private int random(int range){
        range = (range == 0) ? 1 : range;
        return ThreadLocalRandom.current().nextInt(0,range);
    }

	public BBO(Instances instances) {

        this.instances = instances;
	    graph = instances.graph;
        vertices = instances.vertices;
        NbNodes = instances.NbNodes;

        population = new LinkedList<>();

        prob = new LinkedList<>();
        lambda = new LinkedList<>();
        mu = new LinkedList<>();

        MaxNbGenerations = 300;
        populationSize = 30;

		PMutate = (float) 0.005;
    }


	public void BBO_Exec() {

	    // Start Time
        long startTime = System.currentTimeMillis();

	    // initialize the population
		InitializePopulation();

        best = new Best();
        best.update(population.get(0),0,0);
        best.display();


        /**\// ## Updating Population Loop ## \//**/
		for (int i = 0; i < MaxNbGenerations; i++) {
		    
			LinkedList<Individual> elitism = new LinkedList<>();
			elitism.add(population.get(0));
			elitism.add(population.get(1));
			

			for (int j = 0; j < populationSize; j++) {
				LinkedList<Integer> currentSolutions = new LinkedList<>(population.get(j).sol.permutation);
				HashMap<Integer, String> permutations = new HashMap<>();

				for (int n = 0; n < currentSolutions.size(); n++) {
					permutations.put(currentSolutions.get(n), String.valueOf(n));
				}

				// Random 0% --> 100%
				if (random(100) < lambda.get(j)) {
				    /** Permutations **/
				    _Permutation(currentSolutions,permutations,j);
				}else{
				    /** Mutations **/
                    _Mutation(currentSolutions,permutations,j);
                }

				Individual I = new Individual(instances,currentSolutions,j,false);
				population.set(j, I);
			}

			/** Local Search Multi Thread **/
            _LocalSearch();


            /** Elitism with the worst **/
            Collections.sort(population, (r1, r2) -> r1.compareTo(r2));
			population.set(populationSize - 1, elitism.get(0));
			population.set(populationSize - 2, elitism.get(1));


            /** evaluate Population **/
			Collections.sort(population, (r1, r2) -> r1.compareTo(r2));

			if (best.cost > population.get(0).cost) {
                best.update(population.get(0),i,startTime);
                best.display();
			} else {
				best.div++;
			}

			if (best.div >= 25) {
			    /** Diversification **/
			    _Diversity(elitism);
			}

			UpdatePopulations();
		}

        best.display();
        System.out.println(" --------- BBO FIN ----------");
	}


	public void InitializePopulation() {
		for (int i = 0; i < populationSize; i++) {
			Individual In = new Individual(instances,i,true);
			population.add(In);

			population.get(i).SpeciesCount = populationSize - i;
			// lambda(i) is the immigration rate for habitat i
			lambda.add(1 - (population.get(i).SpeciesCount / populationSize));
			// mu(i) is the emigration rate for habitat i
			mu.add( population.get(i).SpeciesCount / populationSize);
		}
        updateProb();
	}


	public void UpdatePopulations() {
		for (int i = 0; i < population.size(); i++) {
			population.get(i).SpeciesCount = populationSize - i;
			// lambda(i) is the immigration rate for habitat i
			lambda.set(i, 1 - (population.get(i).SpeciesCount / populationSize));
			// mu(i) is the emigration rate for habitat i
			mu.set(i, population.get(i).SpeciesCount / populationSize);
		}
        updateProb();
	}


    public void updateProb() {

	    prob.clear();

        float sommeLT = 0;
        float sommeMT = 0;
        float sommeT ;

        for (int k = 0; k < populationSize; k++) {
            sommeLT = sommeLT + lambda.get(k);
            sommeMT = sommeMT + mu.get(k);
        }
        sommeT = sommeLT / sommeMT;

        for (int k = 0; k < populationSize; k++) {
            prob.add((float) 1 / (1 + sommeT));
        }
    }



    private void _Permutation(LinkedList<Integer> currentSolutions ,HashMap<Integer, String> permutations , int j){
        for (int l = 0; l < populationSize && l != j; l++) {

            if (random(100) < mu.get(l)) {

                int rand = random(population.get(l).sol.verticesDT.size() - 1 );

                int tempNode = currentSolutions.get(rand);
                int NewNode = population.get(l).sol.permutation.get(rand);
                String pos = permutations.get(NewNode);

                currentSolutions.set(rand, NewNode);
                currentSolutions.set(Integer.parseInt(pos), tempNode);
                permutations.put(NewNode, String.valueOf(rand));
                permutations.put(tempNode, pos);
            }
        }
    }


    private void _Mutation(LinkedList<Integer> currentSolutions ,HashMap<Integer, String> permutations , int j){
        // Mutation
        float mi;
        mi = PMutate * ( (1 - (prob.get(j))) / Collections.max(prob));
        for (int i = 0; i < NbNodes; i++) {

            if (random(100) < mi) {

                int rand;
                while((rand = random(NbNodes - 1)) ==
                        currentSolutions.get(currentSolutions.get(i)));

                int tempNode = currentSolutions.get(i);
                String pos   = permutations.get(rand);
                currentSolutions.set(i, rand);
                currentSolutions.set(Integer.parseInt(pos), tempNode);
                permutations.put(rand, String.valueOf(i));
                permutations.put(tempNode, pos);
            }
        }

    }


    private void _Diversity(LinkedList<Individual> elitism){
        System.out.println("Jumping Out");
        for (int k = 0; k < populationSize; k++) {
            Individual I = new Individual(instances,k,true);
            population.set(k, I);
        }
        best.div = 0;

        // Replace the worst with the previous generation's ELITES.
        population.set(populationSize - 1, elitism.get(0));
        population.set(populationSize - 2, elitism.get(1));
        population.set(populationSize - 3, best.I);

        // Rank habitats
        Collections.sort(population,(r1,r2) -> r1.compareTo(r2));
    }


    /** Local Search Multi Thread **/
    private void _LocalSearch(){
        /** <Local Search> **/
        ArrayList<Callable<Void>> taskList = new ArrayList<>();
        for (int j = 0; j < populationSize; j++) {
            final int th = j;
            Callable<Void> callable = () -> {
                _VNS_Function_I(th);
                return null;
            };
            taskList.add(callable);
        }

        ExecutorService executor = Executors.newFixedThreadPool(populationSize);
        try {
            executor.invokeAll(taskList);
        } catch (InterruptedException ie) {
        }
        /** </Local Search> **/

    }

    /**  Local search individual **/
	private void _VNS_Function_I(int i) {

		Individual I_p = population.get(i);
		boolean stop = false;

		while (!stop) {
			stop = true;
			for (int t = 0; t < I_p.sol.verticesDT.size(); t++) {
				boolean find = false;
				Individual I_pc = I_p;
				for (int v = I_p.sol.verticesDT.size(); v < NbNodes && !find; v++) {
					LinkedList<Integer> permutation_pp = new LinkedList<>(I_p.sol.permutation);
					int temp = permutation_pp.get(t);
					permutation_pp.set(t, permutation_pp.get(v));
					permutation_pp.set(v, temp);

					Individual I_pp = new Individual(instances,permutation_pp,0,false);

					if (I_pp.cost < I_pc.cost) {
						I_pc = I_pp;
					}
				}
				I_p = I_pc;
			}
			// End of Local Search
			if (population.get(i).cost > I_p.cost) {
				population.set(i, I_p);
			}
		}
	}

}

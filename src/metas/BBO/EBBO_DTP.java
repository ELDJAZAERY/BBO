package metas.BBO;


import data.reader.Instances;
import data.representations.Graph;
import data.representations.Vertex;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EBBO_DTP {

    private int NbNodes;
    private int MaxNbGenerations; // max number of generation

    private int P; // max species count, for each island
    private int populationSize; // max species count, for each island
    private LinkedList<Individual> population; // the species count probability of each

    private float E; // max emigration rate, for each island
    private LinkedList<Float> mu; // extinction (emigration) rate


    private float I; // max immigration rate for each island
    private LinkedList<Float> lambda; // immigration rate


    private float PMutate; // initial mutation probability
    private LinkedList<Float> prob; // the species count probability of each habitat


    private Graph graph;
    private LinkedList<Vertex> vertices;
    private LinkedList<Integer> L;


    private float r;
    private Random rand;
    private int nbEvals;

    private Best best;

	public EBBO_DTP(Instances instances) {

	    graph = instances.graph;
        vertices = instances.vertices;
        NbNodes = instances.NbNodes;
        // Initialize the species count probability of each habitat
        L = new LinkedList<>();
        for (int j = 0; j < NbNodes; j++) {
            L.add(j);
        }

        population = new LinkedList<>();

        prob = new LinkedList<>();
        lambda = new LinkedList<>();
        mu = new LinkedList<>();

        rand = new Random();

        MaxNbGenerations = 300;
        populationSize = 20;

        P = populationSize;
		PMutate = (float) 0.005;
		I = (float) 1;
		E = (float) 1;
		nbEvals = 0;

    }


	public void BBO_S() {

	    // Start Time
        long startTime = System.currentTimeMillis();

	    // initialize the population
		InitializePopulation();

        best = new Best();
        best.update(population.get(0),0,0);

        /**\// ## Updating Population Loop ## \//**/
		for (int i = 0; i < MaxNbGenerations; i++) {
		    
			LinkedList<Individual> elitism = new LinkedList<>();
			elitism.add(population.get(0));
			elitism.add(population.get(1));
			

			for (int j = 0; j < populationSize; j++) {
				LinkedList<Integer> solutionTemp = new LinkedList<>(population.get(j).sol.permutation);
				HashMap<Integer, String> assigned = new HashMap<>();

				for (int n = 0; n < population.get(j).sol.permutation.size(); n++) {
					assigned.put(population.get(j).sol.permutation.get(n), String.valueOf(n));
				}

				// Random 0% --> 100%
				r = (float) rand.nextInt(101) / 100;
				if (r < lambda.get(j)) {
				    /** Permutations **/
				    _Permutation(solutionTemp,assigned,j);
				}else{
				    /** Mutations **/
                    _Mutation(solutionTemp,assigned,j);
                }

				nbEvals++;
				Solution individual = new Solution(solutionTemp, NbNodes);
				individual.DT_P(graph, vertices);
				Individual I = new Individual(individual, j, nbEvals);
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
			Solution individual = new Solution(L, NbNodes);
			Collections.shuffle(individual.permutation);
			individual.DT(graph, vertices);
			Individual In = new Individual(individual, i, 0);
			population.add(In);
			nbEvals++;

			population.get(i).SpeciesCount = P - i;
			// lambda(i) is the immigration rate for habitat i
			lambda.add(I * (1 - (population.get(i).SpeciesCount / P)));
			// mu(i) is the emigration rate for habitat i
			mu.add( E * population.get(i).SpeciesCount / P);
		}

        updateProb();
	}


	public void UpdatePopulations() {
		for (int i = 0; i < population.size(); i++) {
			population.get(i).SpeciesCount = P - i;
			// lambda(i) is the immigration rate for habitat i
			lambda.set(i, I * (1 - (population.get(i).SpeciesCount / P)));
			// mu(i) is the emigration rate for habitat i
			mu.set(i, E * population.get(i).SpeciesCount / P);
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



    private void _Permutation(LinkedList<Integer> solutionTemp ,HashMap<Integer, String> assigned , int j){
        for (int l = 0; l < populationSize && l != j; l++) {
            r = (float) rand.nextInt(101) / 100;
            if (r < mu.get(l)) {

                r = (float) rand.nextInt(101) / 100;
                int d = rand.nextInt(population.get(l).sol.verticesDT.size() - 1) + 0;

                int tempNode = solutionTemp.get(d);
                int NewNode = population.get(l).sol.permutation.get(d);
                String pos = assigned.get(NewNode);

                solutionTemp.set(d, NewNode);
                solutionTemp.set(Integer.parseInt(pos), tempNode);
                assigned.put(NewNode, String.valueOf(d));
                assigned.put(tempNode, pos);
            }
        }
    }


    private void _Mutation(LinkedList<Integer> solutionTemp ,HashMap<Integer, String> assigned , int j){
        // Mutation
        float mi;
        mi = PMutate * ( (1 - (prob.get(j))) / Collections.max(prob));
        for (int d = 0; d < NbNodes; d++) {

            r = (float) rand.nextInt(101) / 100;
            int n = 0;
            if (r < mi) {

                boolean stop = false;
                while (!stop) {
                    n = rand.nextInt(NbNodes - 1) + 0;
                    if (n != solutionTemp.get(solutionTemp.get(d)))
                        stop = true;
                }

                int tempNode = solutionTemp.get(d);
                String pos   = assigned.get(n);
                solutionTemp.set(d, n);
                solutionTemp.set(Integer.parseInt(pos), tempNode);
                assigned.put(n, String.valueOf(d));
                assigned.put(tempNode, pos);
            }
        }

    }


    private void _Diversity(LinkedList<Individual> elitism){
        System.out.println("Jumping Out");
        for (int k = 0; k < populationSize; k++) {
            Solution individual = new Solution(L, NbNodes);
            Collections.shuffle(individual.permutation);
            individual.DT(graph, vertices);
            Individual I = new Individual(individual, k, population.get(k).eval);
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
					nbEvals++;
					Solution individual = new Solution(permutation_pp, NbNodes);
					individual.DT(graph, vertices);

					Individual I_pp = new Individual(individual, 0, nbEvals);

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

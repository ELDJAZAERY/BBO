package metas.BBO;


import data.reader.Instances;
import data.representations.Graph;
import data.representations.Vertex;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EBBO_DTP {

    int NbNodes;
    int MaxNbGenerations; // max number of generation
    LinkedList<Float> distances;
    Hashtable<String, String> key;

    int P; // max species count, for each island
    int popsize; // max species count, for each island
    LinkedList<Individual> population; // the species count probability of each


    float E; // max emigration rate, for each island
    LinkedList<Float> mu; // extinction (emigration) rate


    float I; // max immigration rate for each island
	LinkedList<Float> lambda; // immigration rate


    float PMutate; // initial mutation probability
    LinkedList<Float> prob; // the species count probability of each habitat


    Graph graph;
    LinkedList<Vertex> vertices;
    LinkedList<Integer> L;


	float r;
    Random rand;
    int nbEvals;

    Best best;

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
        distances = new LinkedList<>();
        key = new Hashtable<>();

        prob = new LinkedList<>();
        lambda = new LinkedList<>();
        mu = new LinkedList<>();

        rand = new Random();

        MaxNbGenerations = 300;
        popsize = 20;

        P = popsize;
		PMutate = (float) 0.005;
		I = (float) 1;
		E = (float) 1;
		nbEvals = 0;

    }


	public void BBO_S() {

		Init_p_l_m_s();
		Prob(false);

		long startTime = System.currentTimeMillis();

        best = new Best();
        best.update(population.get(0),0,startTime);

        // Begin the optimization loop
		for (int i = 0; i < MaxNbGenerations; i++) {


			LinkedList<Individual> populationKeep = new LinkedList<>();
			populationKeep.add(population.get(0));
			populationKeep.add(population.get(1));

			for (int j = 0; j < popsize; j++) {
				LinkedList<Integer> permutationTemp = new LinkedList<>(population.get(j).sol.permutation);
				HashMap<Integer, String> permutationTemp_2 = new HashMap<>();

				for (int n = 0; n < population.get(j).sol.permutation.size(); n++) {
					permutationTemp_2.put(population.get(j).sol.permutation.get(n), String.valueOf(n));
				}

				// Random 0% --> 100%
				r = (float) rand.nextInt(101) / 100;
				if (r < lambda.get(j)) {
				    /** Permutations **/
				    _Permutation(permutationTemp,permutationTemp_2,j);
				}else{
				    /** Mutations **/
                    _Mutation(permutationTemp,permutationTemp_2,j);
                }

				nbEvals++;
				Solution individual = new Solution(permutationTemp, NbNodes);
				individual.DT_P(graph, vertices);
				Individual I = new Individual(individual, j, nbEvals);
				population.set(j, I);
			}

			/** Local Search Multi Thread **/
            _LocalSearch();

			// Rank habitats -----------------------------------------------;
			Collections.sort(population, (r1, r2) -> r1.compareTo(r2));

			// Replace the worst with the previous generation's ELITES.
			population.set(popsize - 3, populationKeep.get(0));
			population.set(popsize - 4, populationKeep.get(1));

			// Rank habitats -----------------------------------------------;
			Collections.sort(population, (r1, r2) -> r1.compareTo(r2));

			if (best.cost > population.get(0).cost) {
                best.update(population.get(0),i,startTime);
                best.display();
			} else {
				best.div++;
			}

			if (best.div >= 25) {
			    /** Diversification **/
			    _Diversity(populationKeep);
			}

			Update_l_m_s_();
			Prob(true);
		}

        best.display();
        System.out.println(" --------- BBO FIN ----------");
	}


	public void Init_p_l_m_s() {

		for (int i = 0; i < popsize; i++) {
			Solution individual = new Solution(L, NbNodes);
			Collections.shuffle(individual.permutation);
			individual.DT(graph, vertices);
			Individual In = new Individual(individual, i, 0);
			population.add(In);
			distances.add((float) 0);
			nbEvals++;

			population.get(i).SpeciesCount = P - i;
			// lambda(i) is the immigration rate for habitat i
			lambda.add(I * (1 - (population.get(i).SpeciesCount / P)));
			// mu(i) is the emigration rate for habitat i
			mu.add( E * population.get(i).SpeciesCount / P);
		}
	}


	public void Update_l_m_s_() {
		for (int i = 0; i < population.size(); i++) {
			population.get(i).SpeciesCount = P - i;
			// lambda(i) is the immigration rate for habitat i
			lambda.set(i, I * (1 - (population.get(i).SpeciesCount / P)));
			// mu(i) is the emigration rate for habitat i
			mu.set(i, E * population.get(i).SpeciesCount / P);
		}
	}


    public void Prob(Boolean set) {
        float sommeLT = 0;
        float sommeMT = 0;
        float sommeT ;

        for (int k = 0; k < popsize; k++) {
            sommeLT = sommeLT + lambda.get(k);
            sommeMT = sommeMT + mu.get(k);
        }

        sommeT = sommeLT / sommeMT;
        for (int k = 0; k < popsize; k++) {
            if (k == 0) {
                if(set)
                    prob.add(1,(float) 1 / (1 + sommeT));
                else
                    prob.add((float) 1 / (1 + sommeT));
            }

            else {
                float sommeL = 0;
                float sommeM = 0;
                for (int i = 0; i < k; i++) {
                    sommeL = sommeL + lambda.get(i);
                    sommeM = sommeM + mu.get(i);
                }

                if(set)
                    prob.add(1,(float) 1 / (1 + sommeT));
                else
                    prob.add((float) 1 / (1 + sommeT));
            }
        }
    }



    private void _Permutation(LinkedList<Integer> permutationTemp ,HashMap<Integer, String> permutationTemp_2 , int j){
        for (int l = 0; l < popsize && l != j; l++) {
            r = (float) rand.nextInt(101) / 100;
            if (r < mu.get(l)) {

                r = (float) rand.nextInt(101) / 100;
                int d = rand.nextInt(population.get(l).sol.verticesDT.size() - 1) + 0;

                int tempNode = permutationTemp.get(d);
                int NewNode = population.get(l).sol.permutation.get(d);
                String pos = permutationTemp_2.get(NewNode);

                permutationTemp.set(d, NewNode);
                permutationTemp.set(Integer.parseInt(pos), tempNode);
                permutationTemp_2.put(NewNode, String.valueOf(d));
                permutationTemp_2.put(tempNode, pos);

            }
        }
    }


    private void _Mutation(LinkedList<Integer> permutationTemp ,HashMap<Integer, String> permutationTemp_2 , int j){
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
                    if (n != permutationTemp.get(permutationTemp.get(d)))
                        stop = true;
                }

                int tempNode = permutationTemp.get(d);
                String pos = permutationTemp_2.get(n);
                permutationTemp.set(d, n);
                permutationTemp.set(Integer.parseInt(pos), tempNode);
                permutationTemp_2.put(n, String.valueOf(d));
                permutationTemp_2.put(tempNode, pos);

            }

        }
    }


    private void _Diversity(LinkedList<Individual> populationKeep){
        System.out.println("Jumping Out");
        for (int k = 0; k < popsize; k++) {
            Solution individual = new Solution(L, NbNodes);
            Collections.shuffle(individual.permutation);
            individual.DT(graph, vertices);
            Individual I = new Individual(individual, k, population.get(k).eval);
            population.set(k, I);
        }
        best.div = 0;

        // Replace the worst with the previous generation's ELITES.
        population.set(popsize - 3, populationKeep.get(0));
        population.set(popsize - 4, populationKeep.get(1));
        population.set(popsize - 5, best.I);

        // Rank habitats
        Collections.sort(population,(r1,r2) -> r1.compareTo(r2));
    }


    /** Local Search Multi Thread **/
    private void _LocalSearch(){
        /** <Local Search> **/
        ArrayList<Callable<Void>> taskList = new ArrayList<>();
        for (int j = 0; j < popsize; j++) {
            final int th = j;
            Callable<Void> callable = () -> {
                _VNS_Function_I(th);
                return null;
            };
            taskList.add(callable);
        }

        ExecutorService executor = Executors.newFixedThreadPool(popsize);
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

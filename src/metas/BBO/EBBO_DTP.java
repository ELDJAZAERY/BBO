package metas.BBO;

import data.representations.Graph;
import data.representations.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;

public class EBBO_DTP {

	float Pmutate; // initial mutation probability
	float I; // max immigration rate for each island
	float E; // max emigration rate, for each island
	int popsize; // max species count, for each island
	int P; // max species count, for each island
	int Maxgen; // max number of generation
	LinkedList<Float> prob; // the species count probability of each habitat
	LinkedList<Individual> population; // the species count probability of each

	LinkedList<Float> lamda; // immigration rate
	LinkedList<Float> mu; // extinction (emigration) rate
	Random rand;
	float r;
	int NbNodes;
	LinkedList<Float> distances;
	int nbev;
	Hashtable<String, String> key;

	public EBBO_DTP(int N) {
		Pmutate = (float) 0.005;
		I = (float) 1;
		E = (float) 1;
		popsize = 20;
		P = popsize;
		NbNodes = N;
		Maxgen = 300;
		prob = new LinkedList<Float>();
		lamda = new LinkedList<Float>();
		mu = new LinkedList<Float>();
		population = new LinkedList<Individual>();
		rand = new Random();
		distances = new LinkedList<Float>();
		nbev = 0;
		key = new Hashtable<String, String>();
	}

	public void BBO_S(LinkedList<Vertex> vertices, Graph graph) {

		// Initialize the species count probability of each habitat
		LinkedList<Integer> L = new LinkedList<Integer>();
		for (int j = 0; j < NbNodes; j++) {
			L.add(j);
		}

		Init_p_l_m_s(vertices, graph, L);
		Prob();

		Best best = new Best();
		best.cost = population.get(0).cost;
		best.NbN = population.get(0).sol.verticesDT.size();

		long startTime = System.nanoTime();
		// Begin the optimization loop

		for (int i = 0; i < Maxgen; i++) {
			LinkedList<Individual> populationKeep = new LinkedList<Individual>();
			populationKeep.add(population.get(0));
			populationKeep.add(population.get(1));

			for (int j = 0; j < popsize; j++) {
				LinkedList<Integer> permutationTemp = new LinkedList<Integer>();
				permutationTemp = Copy_L(population.get(j).sol.permutation);

				HashMap<Integer, String> permutationTemp_2 = new HashMap<Integer, String>();
				for (int n = 0; n < population.get(j).sol.permutation.size(); n++) {
					permutationTemp_2.put(population.get(j).sol.permutation.get(n), String.valueOf(n));
				}

				r = (float) rand.nextInt(101) / 100;
				if (r < lamda.get(j)) {
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

				// Mutation
				float mi;
				mi = Pmutate * ((float) (1 - (prob.get(j))) / Collections.max(prob));
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

				nbev++;
				Solution individual = new Solution(permutationTemp, NbNodes);
				individual.DT_P(graph, vertices);
				Individual I = new Individual(individual, j, nbev);
				population.set(j, I);

			}

			// Local Search
			ArrayList<Callable<Void>> taskList = new ArrayList<Callable<Void>>();
			for (int j = 0; j < popsize; j++) {
				final int th = j;
				Callable<Void> callable = new Callable<Void>() {
					public Void call() throws Exception {
						VNS_Function_I(vertices, graph, th);
						return null;
					}
				};
				taskList.add(callable);
			}

			ExecutorService executor = Executors.newFixedThreadPool(popsize);
			try {
				executor.invokeAll(taskList);
			} catch (InterruptedException ie) {
			}

			// Rank habitats -----------------------------------------------;
			Collections.sort(population, new Comparator<Individual>() {
				public int compare(Individual r1, Individual r2) {
					return r1.compareTo(r2);
				}
			});

			// Replace the worst with the previous generation's ELITES.
			population.set(popsize - 3, populationKeep.get(0));
			population.set(popsize - 4, populationKeep.get(1));

			// Rank habitats -----------------------------------------------;
			Collections.sort(population, new Comparator<Individual>() {
				public int compare(Individual r1, Individual r2) {
					return r1.compareTo(r2);
				}
			});

			if (best.cost > population.get(0).cost) {
				best.cost = population.get(0).cost;
				long endTime_best = System.nanoTime();
				best.t = (endTime_best - startTime);
				best.NbIt = i + 1;
				best.div = 0;
				best.I = population.get(0);
				best.NbN = population.get(0).sol.verticesDT.size();
				best.verticesDT = new LinkedList<Vertex>(population.get(0).sol.verticesDT);
				best.tree = population.get(0).sol.tree;

				System.out.println(
						best.cost + "," + best.NbN + "," + best.t / 1000000000.0 + "," + best.NbIt + "," + nbev);

			} else {
				best.div++;
			}

			if (best.div >= 25) {
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
				// -----------------------------------------------;
				Collections.sort(population, new Comparator<Individual>() {
					public int compare(Individual r1, Individual r2) {
						return r1.compareTo(r2);
					}
				});
			}

			Update_l_m_s_();
			Prob_set();
		}

		System.out
				.println(best.cost + "," + best.NbN + "," + best.t / 1000000000.0 + "," + best.NbIt + "   |   " + nbev);
		System.out.println("---------------------------------------------");

	}

	/*
	 * -----------------------------------------------------------Functions-----
	 * -------------------------------------------------------------------------
	 */

	public void Init_p_l_m_s(LinkedList<Vertex> vertices, Graph graph, LinkedList<Integer> L) {
		for (int i = 0; i < popsize; i++) {

			Solution individual = new Solution(L, NbNodes);
			Collections.shuffle(individual.permutation);
			individual.DT(graph, vertices);
			Individual In = new Individual(individual, i, 0);
			population.add(In);
			distances.add((float) 0);
			nbev++;

			population.get(i).SpeciesCount = P - i;
			// lambda(i) is the immigration rate for habitat i
			lamda.add(I * (1 - ((float) population.get(i).SpeciesCount / P)));
			// mu(i) is the emigration rate for habitat i
			mu.add((float) E * population.get(i).SpeciesCount / P);
		}
	}

	public void Update_l_m_s_() {
		for (int i = 0; i < population.size(); i++) {
			population.get(i).SpeciesCount = P - i;
			// lambda(i) is the immigration rate for habitat i
			lamda.set(i, I * (1 - ((float) population.get(i).SpeciesCount / P)));
			// mu(i) is the emigration rate for habitat i
			mu.set(i, (float) E * population.get(i).SpeciesCount / P);
		}
	}

	public void Sinu_Update_l_m_s() {
		for (int i = 0; i < population.size(); i++) {
			population.get(i).SpeciesCount = P - i;
			// lambda(i) is the immigration rate for habitat i
			lamda.add((float) ((I / 2) * (1 + Math.cos(population.get(i).SpeciesCount * Math.PI / P))));
			// mu(i) is the emigration rate for habitat i
			mu.add((float) ((E / 2) * (1 - Math.cos(population.get(i).SpeciesCount * Math.PI / P))));
		}
	}

	public void Sinu_Update_l_m_s_() {
		for (int i = 0; i < population.size(); i++) {
			population.get(i).SpeciesCount = P - i;
			// lambda(i) is the immigration rate for habitat i
			lamda.set(i, (float) ((I / 2) * (1 + Math.cos(population.get(i).SpeciesCount * Math.PI / P))));
			// mu(i) is the emigration rate for habitat i
			mu.set(i, (float) ((E / 2) * (1 - Math.cos(population.get(i).SpeciesCount * Math.PI / P))));
		}
	}

	public void Prob_set() {
		float sommeLT = 0;
		float sommeMT = 0;
		float sommeT = 0;

		for (int k = 0; k < popsize; k++) {
			sommeLT = sommeLT + lamda.get(k);
			sommeMT = sommeMT + mu.get(k);
		}

		sommeT = (float) sommeLT / sommeMT;
		for (int k = 0; k < popsize; k++) {
			if (k == 0) {
				prob.set(1, (float) 1 / (1 + sommeT));
			}

			else {
				float sommeL = 0;
				float sommeM = 0;
				for (int i = 0; i < k; i++) {
					sommeL = sommeL + lamda.get(i);
					sommeM = sommeM + mu.get(i);
				}

				prob.set(1, (float) sommeL / (sommeM * (1 + sommeT)));
			}
		}
	}

	public void Prob_DS() {
		// Prob
		float ProbMinus;
		float ProbPlus;
		float lambdaMinus;
		float muPlus;
		LinkedList<Float> ProbDot = new LinkedList<Float>();

		for (int i = 0; i < popsize; i++) {
			// Compute lambda for one less than the species count of habitat i.
			lambdaMinus = I * (1 - (population.get(i).SpeciesCount - 1) / P);
			// Compute mu for one more than the species count of habitat i.
			muPlus = E * (population.get(i).SpeciesCount + 1) / P;
			if (i < popsize - 1)
				ProbMinus = prob.get(i + 1);
			else
				ProbMinus = 0;

			if (i > 0)
				ProbPlus = prob.get(i - 1);
			else
				ProbPlus = 0;

			ProbDot.add(-(lamda.get(i) + mu.get(i)) * prob.get(i) + lambdaMinus * ProbMinus + muPlus * ProbPlus);
		}

		// Compute the new probabilities for each species count.
		for (int i = 0; i < prob.size(); i++) {
			float P = prob.get(i) + ProbDot.get(i) * 1; // DT
			if (P < 0) {
				P = 0;
			}
			prob.set(i, P);
		}

		float somme = 0;
		for (int i = 0; i < prob.size(); i++) {
			somme = somme + prob.get(i);
		}

		for (int i = 0; i < prob.size(); i++) {
			prob.set(i, prob.get(i) / somme);
		}
	}

	public void Prob() {
		float sommeLT = 0;
		float sommeMT = 0;
		float sommeT = 0;

		for (int k = 0; k < popsize; k++) {
			sommeLT = sommeLT + lamda.get(k);
			sommeMT = sommeMT + mu.get(k);
		}

		sommeT = (float) sommeLT / sommeMT;
		for (int k = 0; k < popsize; k++) {
			if (k == 0) {
				prob.add((float) 1 / (1 + sommeT));
			}

			else {
				float sommeL = 0;
				float sommeM = 0;
				for (int i = 0; i < k; i++) {
					sommeL = sommeL + lamda.get(i);
					sommeM = sommeM + mu.get(i);
				}

				prob.add((float) sommeL / (sommeM * (1 + sommeT)));
			}

		}
	}

	public void DistanceHamming(LinkedList<Float> distances) {
		float di;
		int somme;
		int somme2;

		for (int i = 0; i < popsize; i++) {
			somme2 = 0;
			for (int j = 0; j < popsize; j++) {
				somme = 0;
				if (i != j) {
					for (int k = 0; k < NbNodes; k++) {
						if (population.get(i).sol.permutation.get(k) != population.get(j).sol.permutation.get(k)) {
							somme++;
						}
					}
				}
				somme2 = somme2 + somme;
			}
			di = (float) somme2 / (popsize - 1);
			distances.set(i, di);
		}
	}

	public LinkedList<Integer> Copy_L(LinkedList<Integer> Liste) {

		LinkedList<Integer> L = new LinkedList<Integer>();
		for (int i = 0; i < Liste.size(); i++) {
			L.add(Liste.get(i));
		}
		return L;
	}

	public void VNS_Function_I(LinkedList<Vertex> vertices, Graph graph, int i) {

		Individual I_p = population.get(i);
		boolean stop = false;

		while (!stop) {
			stop = true;
			for (int t = 0; t < I_p.sol.verticesDT.size(); t++) {
				boolean find = false;
				Individual I_pc = I_p;
				for (int v = I_p.sol.verticesDT.size(); v < NbNodes && !find; v++) {
					LinkedList<Integer> permutation_pp = new LinkedList<Integer>(I_p.sol.permutation);
					int temp = permutation_pp.get(t);
					permutation_pp.set(t, permutation_pp.get(v));
					permutation_pp.set(v, temp);
					nbev++;
					Solution individual = new Solution(permutation_pp, NbNodes);
					individual.DT(graph, vertices);

					Individual I_pp = new Individual(individual, 0, nbev);

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

	public void DistanceHamming() {
		float di;
		int somme;
		int somme2;

		for (int i = 0; i < popsize; i++) {
			somme2 = 0;
			for (int j = 0; j < popsize; j++) {
				somme = 0;
				if (i != j) {
					for (int k = 0; k < NbNodes; k++) {
						if (population.get(i).sol.SB[k] != population.get(j).sol.SB[k]) {
							somme++;
						}
					}
				}
				somme2 = somme2 + somme;
			}
			di = (float) somme2 / (popsize - 1);
			distances.set(i, di);
		}
	}

	public void Reference_set_update() {

		LinkedList<Individual> Pop_New = new LinkedList<Individual>();
		// Rank habitats -----------------------------------------------;
		Collections.sort(population, new Comparator<Individual>() {
			public int compare(Individual r1, Individual r2) {
				return r1.compareTo(r2);
			}
		});

		int b1 = popsize / 2;
		int b2 = popsize / 2;

		for (int i = 0; i < b1; i++) {
			Pop_New.add(population.getFirst());
			population.removeFirst();
			distances.add((float) 0);
		}

		LinkedList<obj> Distance_min = new LinkedList<obj>();

		for (int i = 0; i < population.size(); i++) {
			int min = Integer.MAX_VALUE;

			for (int j = 0; j < Pop_New.size(); j++) {
				int D = 0;
				for (int d = 0; d < NbNodes; d++) {
					if (population.get(i).sol.SB[d] != Pop_New.get(j).sol.SB[d]) {
						D++;
					}
				}
				if (D < min)
					min = D;
			}
			obj o = new obj();
			o.pop = i;
			o.distance = min;

			Distance_min.add(o);
		}

		Collections.sort(Distance_min, new Comparator<obj>() {
			public int compare(obj r1, obj r2) {
				return r1.compareTo(r2);
			}
		});

		for (int i = 0; i < b2; i++) {
			Individual I = new Individual(population.get(Distance_min.get(i).pop).sol, i, Distance_min.get(i).distance);
			Pop_New.addFirst(I);
			distances.add((float) 0);
		}
		population = new LinkedList<Individual>(Pop_New);

		// Rank habitats -----------------------------------------------;
		Collections.sort(population, new Comparator<Individual>() {
			public int compare(Individual r1, Individual r2) {
				return r1.compareTo(r2);
			}
		});
	}

	public void Hill_Climbing_Adaptation(LinkedList<Vertex> vertices, Graph graph) {

	}
}

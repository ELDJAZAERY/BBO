package metas.BBO;

import data.representations.GraphProblems;
import data.representations.Graph;
import data.representations.Vertex;

import java.util.LinkedList;
import java.util.Random;

public class Solution {

	static LinkedList<Integer> permutationInitial = new LinkedList<>();

	LinkedList<Integer> permutation;
	LinkedList<Vertex> verticesDT;
	Graph tree;
	Random rand;
	int[] SB;

	/* ------------------- */
	LinkedList<Vertex> B2;
	Graph G2;
	/* ------------------- */

	public float fitness;

	public Solution(LinkedList<Integer> CurrentSol, int NbNodes) {
		permutation = new LinkedList<>(CurrentSol);
		verticesDT = new LinkedList<>();
		tree = new Graph();
		rand = new Random();
		SB = new int[NbNodes];

		B2 = new LinkedList<>();
		G2 = new Graph();
	}

	public Solution(int NbNodes) {

	    if(permutationInitial.size() == 0){
            for (int j = 0; j < NbNodes; j++) {
                permutationInitial.add(j);
            }
        }

        permutation = new LinkedList<>(permutationInitial);
		verticesDT = new LinkedList<>();
		tree = new Graph();
		rand = new Random();
		SB = new int[NbNodes];

		B2 = new LinkedList<>();
		G2 = new Graph();
	}


	public void DT(Graph graph, LinkedList<Vertex> vertices) {

		/* ------------------------------------------------------------------ */
		GraphProblems undirectedConnectivity = new GraphProblems();
		LinkedList<Integer> solution = undirectedConnectivity.DominatingSet(vertices, permutation);
		/* ------------------------------------------------------------------ */

		Graph G = new Graph();
		LinkedList<Vertex> B = new LinkedList<Vertex>();
		G = undirectedConnectivity.graph(graph, B, solution);
		
		boolean stop = false;
		while (!stop) {
			if (undirectedConnectivity.Connectivity_BFS(G, B)) {
				Prims_MST prims = new Prims_MST(B.size());
				prims.primsAlgorithm(G, B);
				fitness = prims.DominatingTree_(verticesDT, G, solution, tree);
				/** Binary representation **/
//				for (int i = 0; i < solution.size(); i++) {
//					SB[solution.get(i)] = 1;
//				}
				stop = true;
			} else {
				int index = solution.size();
				solution.add(permutation.get(index));
				undirectedConnectivity.AddToGraph(graph, G, B, solution);
			}
		}
	}


	public void DT_P(Graph graph, LinkedList<Vertex> vertices) {

		GraphProblems undirectedConnectivity = new GraphProblems();
		LinkedList<Integer> solution = new LinkedList<Integer>();
		solution = undirectedConnectivity.DominatingSet(vertices, permutation);

		Graph G = new Graph();
		LinkedList<Vertex> B = new LinkedList<Vertex>();
		G = undirectedConnectivity.graph(graph, B, solution);

		boolean stop = false;
		while (!stop) {
			if (undirectedConnectivity.Connectivity_BFS(G, B)) {
				Prims_MST prims = new Prims_MST(B.size());

				prims.primsAlgorithm(G, B);
				fitness = prims.DominatingTree(B2, G, solution, G2, graph, vertices, permutation);

				Prims_MST prims2 = new Prims_MST(B2.size());
				prims2.primsAlgorithm(G2, B2);
				LinkedList<Integer> SolutionNew = new LinkedList<Integer>();
				for (int i = 0; i < B2.size(); i++) {
					SolutionNew.add(Integer.parseInt(B2.get(i).getLabel()));
				}
				solution = new LinkedList<Integer>(SolutionNew);
				fitness = prims2.DominatingTree_(verticesDT, G2, solution, tree);

				/** Binary representation **/
				// for (int i = 0; i < solution.size(); i++) {
				// SB[solution.get(i)] = 1;
				// }

				stop = true;

			} else {
				int index = solution.size();
				solution.add(permutation.get(index));
				undirectedConnectivity.AddToGraph(graph, G, B, solution);
			}

		}
	}


}

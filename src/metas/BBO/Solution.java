package metas.BBO;

import data.representations.GraphProblems;
import data.representations.Graph;
import data.representations.Vertex;
import java.util.LinkedList;


public class Solution {

	private static LinkedList<Integer> permutationInitial = new LinkedList<>();

	LinkedList<Integer> permutation;
	LinkedList<Vertex> verticesDT;
	Graph tree;

	private LinkedList<Vertex> B2;
	private Graph G2;

	public float fitness;

	public Solution(LinkedList<Integer> CurrentSol) {
		permutation = new LinkedList<>(CurrentSol);
		verticesDT = new LinkedList<>();
		tree = new Graph();

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

		B2 = new LinkedList<>();
		G2 = new Graph();
	}


	public void DT_P(Graph graph, LinkedList<Vertex> vertices) {

		GraphProblems TG = new GraphProblems();
		LinkedList<Integer> solution ;
		solution = TG.DominatingSet(vertices, permutation);

		Graph G ;
		LinkedList<Vertex> B = new LinkedList<>();
		G = TG.graph(graph, B, solution);

		boolean stop = false;
		while (!stop) {
			if (TG.Connectivity_BFS(G, B)) {
				Prims_MST prims = new Prims_MST(B.size());

				prims.primsAlgorithm(G, B);
				fitness = prims.DominatingTree(B2, G, solution, G2, graph, vertices, permutation);

				Prims_MST prims2 = new Prims_MST(B2.size());
				prims2.primsAlgorithm(G2, B2);
				LinkedList<Integer> SolutionNew = new LinkedList<>();
				for (int i = 0; i < B2.size(); i++) {
					SolutionNew.add(Integer.parseInt(B2.get(i).getLabel()));
				}
				solution = new LinkedList<>(SolutionNew);
				fitness = prims2.DominatingTree_(verticesDT, G2, solution, tree);

				stop = true;

			} else {
				int index = solution.size();
				solution.add(permutation.get(index));
				TG.AddToGraph(graph, G, B, solution);
			}

		}
	}


}

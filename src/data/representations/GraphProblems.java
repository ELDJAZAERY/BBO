package data.representations;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class GraphProblems {

	private Queue<String> queue;

	public GraphProblems() {
		queue = new LinkedList<>();
	}

	// Check the Connectivity of Graph Using BFS
	public boolean Connectivity_BFS(Graph graph, LinkedList<Vertex> vertices) {

		String source = vertices.get(0).getLabel();
		HashMap<String, String> Visited = new HashMap<>();

		String element;
		Visited.put(source, "");
		queue.add(source);
		int c = 1;

		while (!queue.isEmpty()) {
			element = queue.remove();
			Vertex V = graph.getVertex((element));
			for (int j = 0; j < V.getNeighborCount(); j++) {
				String v = V.getNeighbor(j).getNeighbor(V).getLabel();
				if (Visited.get(v) == null) {
					queue.add(v);
					Visited.put(v, "");
					c++;
				}
			}
		}

		if (c == vertices.size())
			return true;
		else
			return false;
	}

	public LinkedList<Integer> DominatingSet(LinkedList<Vertex> vertices, LinkedList<Integer> permutation) {
		LinkedList<Integer> dominating_set = new LinkedList<>();

		int[] temp = new int[permutation.size()];
		int computer = 0, j = 0;

		while (j < permutation.size() && computer != permutation.size()) {
			if (temp[permutation.get(j)] == 0) {
				temp[permutation.get(j)] = 1;
				computer++;
			}

			dominating_set.add(permutation.get(j));
			Vertex V = vertices.get(permutation.get(j));
			for (int k = 0; k < V.getNeighborCount(); k++) {
				int v = Integer.parseInt(V.getNeighbor(k).getNeighbor(V).getLabel());

				if (temp[v] == 0) {
					temp[v] = 1;
					computer++;
				}
			}
			j++;

		}
		return dominating_set;
	}

	public Graph graph(Graph graph, LinkedList<Vertex> vertices,LinkedList<Integer> DominatingVertices) {
		Graph Newgraph = new Graph();

		for (int i = 0; i < DominatingVertices.size(); i++) {
			vertices.add(new Vertex("" + DominatingVertices.get(i)));
			Newgraph.addVertex(vertices.get(i), true);
		}

		for (int i = 0; i < vertices.size() - 1; i++) {
			for (int j = i + 1; j < vertices.size(); j++) {
				Edge e = new Edge(vertices.get(i),vertices.get(j));
				if (graph.containsEdge(e)) {
					Newgraph.addEdge(vertices.get(i), vertices.get(j), graph.containsEdgeWeight(e));
				}
			}
		}
		return Newgraph;
	}

	public void AddToGraph(Graph graph, Graph Newgraph, LinkedList<Vertex> verticesG,LinkedList<Integer> DominatingVertices) {

		int index = DominatingVertices.size() - 1;
		verticesG.add(new Vertex("" + DominatingVertices.get(index)));
		Newgraph.addVertex(verticesG.get(index), true);

		for (int k = 0; k < verticesG.size() - 1; k++) {
			Edge e = new Edge(verticesG.get(index), verticesG.get(k));
			if (graph.containsEdge(e)) {
				Newgraph.addEdge(verticesG.get(index), verticesG.get(k), graph.containsEdgeWeight(e));
			}
		}
	}

}

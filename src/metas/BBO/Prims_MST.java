package metas.BBO;

import data.representations.Edge;
import data.representations.Graph;
import data.representations.Vertex;

import java.util.LinkedList;

public class Prims_MST {

	boolean unsettled[];
	boolean settled[];
	float key[];
	public static final int INFINITE = 999;
	int parent[];

	public Prims_MST(int numberofvertices) {
		unsettled = new boolean[numberofvertices + 1];
		settled = new boolean[numberofvertices + 1];
		key = new float[numberofvertices + 1];
		parent = new int[numberofvertices + 1];
	}

	public int getUnsettledCount(boolean unsettled[]) {
		int count = 0;
		for (int index = 0; index < unsettled.length; index++) {
			if (unsettled[index]) {
				count++;
			}
		}
		return count;
	}

	public void primsAlgorithm(Graph graph, LinkedList<Vertex> vertices) {
		int evaluationVertex;
		for (int index = 0; index < vertices.size(); index++) {
			key[index] = INFINITE;
		}
		key[0] = 0;
		unsettled[0] = true;
		parent[0] = 0;

		while (getUnsettledCount(unsettled) != 0) {
			evaluationVertex = getMimumKeyVertexFromUnsettled(unsettled, graph, vertices);
			unsettled[evaluationVertex] = false;
			settled[evaluationVertex] = true;
			evaluateNeighbours(evaluationVertex, graph, vertices);
		}
	}

	private int getMimumKeyVertexFromUnsettled(boolean[] unsettled2, Graph graph, LinkedList<Vertex> vertices) {
		float min = Integer.MAX_VALUE;

		int node = 0;
		for (int vertex = 0; vertex < vertices.size(); vertex++) {
			if (unsettled[vertex] == true && key[vertex] < min) {
				node = vertex;
				min = key[vertex];
			}
		}
		return node;
	}

	public void evaluateNeighbours(int evaluationVertex, Graph graph, LinkedList<Vertex> vertices) {
		for (int destinationvertex = 0; destinationvertex < vertices.size(); destinationvertex++) {
			if (settled[destinationvertex] == false) {
				Edge e = new Edge(vertices.get(evaluationVertex), vertices.get(destinationvertex));
				if (graph.containsEdge(e)) {
					if (graph.containsEdgeWeight(e) < key[destinationvertex]) {
						key[destinationvertex] = graph.containsEdgeWeight(e);
						parent[destinationvertex] = evaluationVertex;
					}
					unsettled[destinationvertex] = true;
				}
			}
		}
	}


	public float DominatingTree(LinkedList<Vertex> vertices, Graph graph, LinkedList<Integer> DominatingVertices,
			Graph DT, Graph graphh, LinkedList<Vertex> verticess, LinkedList<Integer> permutation) {
		float weight = 0;
		for (int i = 0; i < DominatingVertices.size(); i++) {
			vertices.add(new Vertex("" + DominatingVertices.get(i)));
			DT.addVertex(vertices.get(i), true);
		}
		// Construction of the dominating tree
		for (int vertex = 1; vertex < vertices.size(); vertex++) {
			Edge E = new Edge(vertices.get(parent[vertex]), vertices.get(vertex));
			DT.addEdge(vertices.get(parent[vertex]), vertices.get(vertex), graph.containsEdgeWeight(E));
			weight = weight + graph.containsEdgeWeight(E);
		}

		int compteur = 0;
		int pos;
		Edge e;
		float f = 0;
		for (int i = 0; i < DominatingVertices.size(); i++) {
			pos = i - compteur;
			if (vertices.get(pos).getNeighborCount() == 1) {
				Vertex Vx = vertices.get(pos);
				for (int w = 0; w < Vx.getNeighborCount(); w++) {
					e = Vx.getNeighbor(w);
					f = graphh.containsEdgeWeight(e);
				}

				LinkedList<String> L = new LinkedList<String>();
				Vertex V = verticess.get(DominatingVertices.get(i));
				for (int j = 0; j < V.getNeighborCount(); j++) {
					String v = (V.getNeighbor(j).getNeighbor(V).getLabel());
					if (DT.getVertex(v) == null) {
						L.add(v);
					}
				}

				int dlt = 0;
				boolean stop = false;
				for (int j = 0; j < L.size(); j++) {
					stop = false;
					V = verticess.get(Integer.parseInt(L.get(j)));
					for (int k = 0; k < V.getNeighborCount(); k++) {
						String v = (V.getNeighbor(k).getNeighbor(V).getLabel());

						if (DT.getVertex(v) != null && Integer.parseInt(v) != DominatingVertices.get(i)) {
							stop = true;
						}
						if (stop == true) {
							dlt++;
							break;
						}
					}
				}

				if (dlt == L.size()) {
					DT.removeVertex(Vx.getLabel());
					vertices.remove(pos);

					compteur++;
					weight = weight - f;
					int n = permutation.get(pos);
					permutation.remove(pos);
					permutation.add(n);
				}

			}
		}
		return weight;
	}

	public float DominatingTree_(LinkedList<Vertex> vertices, Graph graph, LinkedList<Integer> DominatingVertices, Graph DT) {
		float weight = 0;
		for (int i = 0; i < DominatingVertices.size(); i++) {
			vertices.add(new Vertex("" + DominatingVertices.get(i)));
			DT.addVertex(vertices.get(i), true);
		}
		// Construction of the dominating tree
		for (int vertex = 1; vertex < vertices.size(); vertex++) {
			Edge E = new Edge(vertices.get(parent[vertex]), vertices.get(vertex));
			DT.addEdge(vertices.get(parent[vertex]), vertices.get(vertex), graph.containsEdgeWeight(E));
			weight = weight + graph.containsEdgeWeight(E);
		}
		return weight;
	}

	public void printMST(LinkedList<Vertex> vertices) {
		for (int vertex = 1; vertex < vertices.size(); vertex++) {
			System.out
					.print(vertices.get(parent[vertex]).getLabel() + "\t:\t" + vertices.get(vertex).getLabel() + "\n");
		}
	}


}

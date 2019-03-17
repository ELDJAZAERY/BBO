package metas;

import data.representations.Graph;
import data.reader.Instances;
import data.representations.Vertex;
import metas.BBO.EBBO_DTP;

import java.util.LinkedList;

public class Main_test {


	public static void main(String[] args) {
		
		String range = "100";
		String Node = "50";
        String file = "1";

		int NbNodes = Integer.parseInt(Node);


		LinkedList<Vertex> vertices = new LinkedList<>();
		
		Graph graph = new Graph();
        Instances e2 = new Instances(graph, vertices,NbNodes, range, file, Node);


		System.out.println("Bismilah <3 Nouveau code");
				
		EBBO_DTP bbo = new EBBO_DTP(NbNodes);

		bbo.BBO_S(vertices, graph);
		//bbo.testPerf(vertices,graph);
	}

	
}	
	

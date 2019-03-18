package metas;

import data.representations.Graph;
import data.reader.Instances;
import data.representations.Vertex;
import metas.BBO.EBBO_DTP;

import java.util.LinkedList;

public class Main_test {


	public static void main(String[] args) {
		
		String range = "150";
		String Node = "50";
        String file = "2";

		int NbNodes = Integer.parseInt(Node);


		LinkedList<Vertex> vertices = new LinkedList<>();
		
		Graph graph = new Graph();
        Instances instances = new Instances(graph, vertices,NbNodes, range, file, Node);


		System.out.println("Bismilah <3 Nouveau code");
				
		EBBO_DTP bbo = new EBBO_DTP(instances);

		bbo.BBO_S();
	}

	
}	
	

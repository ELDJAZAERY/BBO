package metas;

import data.representations.Graph;
import data.reader.Instances;
import data.representations.Vertex;
import metas.BBO.BBO;

import java.util.LinkedList;

public class Main_test {


	public static void main(String[] args) {
		
		String range = "100";
		String Node = "50";
        String file = "1";

		int NbNodes = Integer.parseInt(Node);


		LinkedList<Vertex> vertices = new LinkedList<>();
		
		Graph graph = new Graph();
        Instances instances = new Instances(graph, vertices,NbNodes, range, file, Node);


		BBO bbo = new BBO(instances);

		bbo.BBO_Exec();
	}

	
}	
	

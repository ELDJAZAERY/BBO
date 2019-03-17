package metas;

import data.representations.Graph;
import data.representations.Instances;
import data.representations.Vertex;

import metas.BBO2.EBBO_DTP_2;


import java.io.IOException;
import java.util.LinkedList;



public class Main_test
{

	public static void main(String[] args) throws IOException  {
		
		String range = "100";
		String Node = "50";
		int NbNodes = Integer.parseInt(Node);

		String file = "1";
		
		LinkedList<Vertex> vertices = new LinkedList<>();
		
		Graph graph = new Graph();
		try 
		{															
			@SuppressWarnings("unused")
			Instances e2 = new Instances(graph, vertices,NbNodes, range, file, Node);
		} 	
		catch (IOException e2) { e2.printStackTrace();}

		System.out.println("Bismilah <3 Nouveau code");
				
		EBBO_DTP_2 bbo = new EBBO_DTP_2(NbNodes);

		bbo.BBO_S(vertices, graph);
		//bbo.testPerf(vertices,graph);
	}

	
}	
	

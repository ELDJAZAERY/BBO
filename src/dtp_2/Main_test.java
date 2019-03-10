package dtp_2;

import java.io.IOException;
import java.util.LinkedList;



public class Main_test
{

	public static void main(String[] args) throws IOException  {
		
		String range = "100";
		String Node = "500";
		int NbNodes = Integer.parseInt(Node);

		String file = "1";
		
		LinkedList<Vertex> vertices = new LinkedList<Vertex>();
		
		Graph graph = new Graph();
		try 
		{															
			@SuppressWarnings("unused")
			Instances e2 = new Instances(graph, vertices,NbNodes, range, file, Node);									
		} 	
		catch (IOException e2) { e2.printStackTrace();}

		System.out.println("Bismilah <3 Nouveau code");
				
		EBBO_DTP bbo = new EBBO_DTP(NbNodes);
		bbo.BBO_S(vertices, graph);
		
	}

	
}	
	

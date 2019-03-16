package data.representations;

import java.io.*;
import java.util.LinkedList;

public class Instances {

	private BufferedReader br;
	
	public Instances(Graph graph, LinkedList<Vertex> vertices, int size, String range, String file, String node) throws IOException
	{
		//initialize some vertices and add them to the graph
		for(int i = 0; i < size; i++)
	    {
			vertices.add(new Vertex("" + i));
			
			graph.addVertex(vertices.get(i), true);
	    }
		
		InputStream ips=new FileInputStream("Instances\\"+range+"\\"+node+"_"+file+".txt"); 
		InputStreamReader ipsr=new InputStreamReader(ips);
		br = new BufferedReader(ipsr);
		String [] temp;
		String ligne;
		while((ligne=br.readLine())!=null)
		{
			if(ligne.length()!=0)
			{
				temp=ligne.split("[|]");
				graph.addEdge(vertices.get(Integer.parseInt(temp[0])), vertices.get(Integer.parseInt(temp[1])), Float.parseFloat(temp[2]));
			}
		}
														
	}
}

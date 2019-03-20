package data.reader;

import data.representations.Graph;
import data.representations.Vertex;

import java.io.*;
import java.util.LinkedList;

public class Instances {

	private BufferedReader br;

	public Graph graph;
	public LinkedList<Vertex> vertices;
    public int NbNodes;

	public Instances(Graph graph, LinkedList<Vertex> vertices, int NbNodes, String range, String file, String node)  {
	    try {

			//initialize some vertices and add them to the graph
			for(int i = 0; i < NbNodes; i++) {
				vertices.add(new Vertex("" + i));
				graph.addVertex(vertices.get(i), true);
			}

			InputStream ips = new FileInputStream("Instances\\"+range+"\\"+node+"_"+file+".txt");
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
		}catch (Exception e){
			e.printStackTrace();
		}finally {
	    	this.graph    = graph;
	    	this.vertices = vertices;
	    	this.NbNodes  = NbNodes;
		}

	}

}

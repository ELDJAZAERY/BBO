package data.reader;

import data.representations.Graph;
import data.representations.Vertex;

import java.io.*;
import java.util.LinkedList;

public class Instances {

	public Graph graph;
	public LinkedList<Vertex> vertices;
    public int NbNodes;
    public int NbArcs;

    public Instances(String path)  {

        graph = new Graph();
        vertices = new LinkedList<>();

        String[] lines = readFile(path);

        String[] info = lines[0].split(" ");

        String[] row;

        NbNodes = Integer.valueOf(info[0].trim());
        NbArcs  = Integer.valueOf(info[1].trim());

        initVertices();

        // init Edges
        for(int i = 1 ; i <= NbArcs ; i++) {
            row = lines[i].split(" ");
            graph.addEdge(
                    vertices.get(Integer.parseInt(row[0].trim())),
                    vertices.get(Integer.parseInt(row[1].trim())),
                    Float.parseFloat(row[2].trim())
            );
        }
    }

    private void initVertices(){
        for(int i = 0; i < NbNodes; i++) {
            vertices.add(new Vertex("" +i));
            graph.addVertex(vertices.get(i), true);
        }
    }

    private static String[] readFile(String path) {
        String content = null;
        File file = new File(path); // For example, foo.txt
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            char[] chars = new char[(int) file.length()];
            reader.read(chars);
            content = new String(chars);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(reader != null){
                try{reader.close();}catch(Exception e){}
            }
        }

        return content.split("\n");
    }

}

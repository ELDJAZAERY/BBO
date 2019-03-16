package metas.BBO;

import data.representations.Graph;
import data.representations.Vertex;

import java.util.LinkedList;

public class Best {
	
	float t;
	float cost;
	int NbIt;
	int NbN;
	int div;
	Individual I;
	LinkedList<Vertex> verticesDT;
	Graph tree;
	int clones;
	int nbev;
	
	public Best()
	{
		t = 0;
		cost = 0;
		NbIt = 0;
		div = 0;
		NbN = 0;
		clones = 0;
		nbev = 0;		
	}
}

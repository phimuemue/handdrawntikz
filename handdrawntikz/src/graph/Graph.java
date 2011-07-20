package graph;

import java.util.Iterator;
import java.util.LinkedList;

public class Graph {
	
	private LinkedList<Node> nodes = new LinkedList<Node>();
	private LinkedList<Edge> edges = new LinkedList<Edge>();
	
	public Graph(){
		
	}
	
	public void AddNode(Node n){
		nodes.add(n);
	}
	
	public void AddEdge(Edge e){
		edges.add(e);
	}
	
	public Iterator<Node> NodeIterator(){
		return nodes.iterator();
	}
	
	public Iterator<Edge> EdgeIterator(){
		return edges.iterator();
	}
}

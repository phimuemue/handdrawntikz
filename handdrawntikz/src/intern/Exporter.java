package intern;

import graph.Graph;

public abstract class Exporter {
	Graph graph = null;
	public Exporter(Graph g){
		this.graph = g;
	}
	
	public abstract void export(String path);
}

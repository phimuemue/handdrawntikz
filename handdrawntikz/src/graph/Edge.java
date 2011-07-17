package graph;

public class Edge {
	// start node v1 and end node v2
	private Node v1;
	private Node v2;
	
	public Edge(Node v1, Node v2) {
		super();
		this.v1 = v1;
		this.v2 = v2;
	}

	public Node getV1() {
		return v1;
	}

	public void setV1(Node v1) {
		this.v1 = v1;
	}

	public Node getV2() {
		return v2;
	}

	public void setV2(Node v2) {
		this.v2 = v2;
	}
	
}

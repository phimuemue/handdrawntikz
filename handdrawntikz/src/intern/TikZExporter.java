package intern;

import java.util.Iterator;

import graph.Edge;
import graph.Graph;
import graph.Node;

public class TikZExporter extends Exporter {

	public TikZExporter(Graph g) {
		super(g);
	}

	@Override
	public void export(String path) {
		StringBuilder sb = new StringBuilder();
		sb.append("\\begin{tikzpicture}\n");
		for (Iterator<Node> nodeIterator = graph.NodeIterator(); nodeIterator.hasNext();) {
			Node n = (Node) nodeIterator.next();
			sb.append("\\node at (" + n.getX() + ", " + n.getY() + ")"); // position, general
			sb.append("[circle, draw]"); // formatting
			sb.append("{}"); // internal description
			sb.append(";\n"); // closing the TikZ-statement, newline
		}
		for (Iterator<Edge> edgeIterator = graph.EdgeIterator(); edgeIterator.hasNext();) {
			Edge e = (Edge) edgeIterator.next();
			// TODO: edges
			sb.append("%" + e); // currently, only as comment
		}
		sb.append("\\end{tikzpicture}\n");
		System.out.println(sb);
	}

}

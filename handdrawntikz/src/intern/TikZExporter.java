package intern;

import java.io.FileWriter;
import java.io.IOException;
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
		FileWriter out;
		try {
			out = new FileWriter(path);
			out.write(generateOutput());
			out.close();
		} catch (IOException e) {
			System.out.print("Something went wrong during export to " + path);
			e.printStackTrace();
		}
	}

	private String generateOutput() {
		// TODO: make export parametric in some way
		StringBuilder sb = new StringBuilder();
		sb.append("\\begin{tikzpicture}\n");
		for (Iterator<Node> nodeIterator = graph.NodeIterator(); nodeIterator.hasNext();) {
			Node n = (Node) nodeIterator.next();
			sb.append("\\node at (" + n.getX() + ", " + n.getY() + ")"); // position, general
			sb.append("[circle, draw]"); // formatting
			sb.append("{" + n + "}"); // internal description
			sb.append(";\n"); // closing the TikZ-statement, newline
		}
		for (Iterator<Edge> edgeIterator = graph.EdgeIterator(); edgeIterator.hasNext();) {
			Edge e = (Edge) edgeIterator.next();
			sb.append("\\draw [->] (" + e.getV1() + ") -- (" + e.getV2() + ");\n");
		}
		sb.append("\\end{tikzpicture}\n");
		return sb.toString();
	}

}

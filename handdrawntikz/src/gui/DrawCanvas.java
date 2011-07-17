package gui;

import graph.Edge;
import graph.Node;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;
import java.util.ListIterator;

public class DrawCanvas extends Panel {
	
	private static final long serialVersionUID = 1L;
	private LinkedList<Point> points = new LinkedList<Point>();
	
	private LinkedList<Node> nodes = new LinkedList<Node>();
	private LinkedList<Edge> edges = new LinkedList<Edge>();
	
	private Node activeNode = null; // node under cursor
	private Node startingNode = null; // node to start edge at
	
	private boolean autoAdjustSizes = true; // automatically adjust node size
	private int nodeSize = 50;
	
	public Node getStartingNode() {
		return startingNode;
	}

	public void setStartingNode(Node startingNode) {
		this.startingNode = startingNode;
	}

	public Node getEndingNode() {
		return endingNode;
	}

	public void setEndingNode(Node endingNode) {
		this.endingNode = endingNode;
	}

	private Node endingNode = null; // node to complete edge
		
	public Node getActiveNode() {
		return activeNode;
	}

	public void setActiveNode(Node activeNode) {
		this.activeNode = activeNode;
		this.repaint();
	}

	public DrawCanvas() {
		super();
		MouseDrawingListener dl = new MouseDrawingListener(this);
		this.addMouseMotionListener(dl);
		this.addMouseListener(dl);
		System.out.println("MouseDrawingListener added");
	}

	public DrawCanvas(LayoutManager layout) {
		super(layout);
	}

	public Node getNodeAtPoint(int x, int y){
		for (Node n : nodes) {
			if (n.pointInNode(x, y)){
				return n;
			}
		}
		return null;
	}
	
	@Override
	public void paint(Graphics g) {
		g = (Graphics2D)g;
		// draw nodes
		((Graphics2D)(g)).setStroke(new BasicStroke(1));
		g.setColor(new Color(0, 0, 0));
		for (Node n : nodes) {
			g.drawRect(n.getX(), n.getY(), n.getWidth(), n.getHeight());
		}
		((Graphics2D)(g)).setStroke(new BasicStroke(2));
		g.setColor(new Color(0, 255, 0));
		if (activeNode != null){
			g.drawRect(activeNode.getX(), activeNode.getY(), activeNode.getWidth(), activeNode.getHeight());
		}
		((Graphics2D)(g)).setStroke(new BasicStroke(1));
		g.setColor(new Color(0, 0, 0));
		// draw edges
		for (Edge e : edges) {
			g.drawLine(e.getV1().getX(), e.getV1().getY(), e.getV2().getX(), e.getV2().getY());
		}
		// draw current line
		if (points.size() < 2) {
			return;
		}
		Point last = points.peek();
		g.setColor(new Color(0, 0, 0));
		for (Point p : points) {
			g.drawLine(last.x, last.y, p.x, p.y);
			last = p;
		}
		g.setColor(new Color(255, 0, 0));
		for (Point p : points) {
			g.drawLine(p.x - 5, p.y - 5, p.x + 5, p.y + 5);
			g.drawLine(p.x + 5, p.y - 5, p.x - 5, p.y + 5);
		}
	}

	public void addPoint(int x, int y) {
		points.add(new Point(x, y));
		this.paint(getGraphics());
	}

	public void removeAllPoints() {
		points.clear();
	}

	public void createNode(){
		if (points.size()==5){
			nodes.add(nodeFromPoints());
		}	
	}
	
	private Node nodeFromPoints() {
		int minx=Integer.MAX_VALUE, miny=Integer.MAX_VALUE, maxx=-1, maxy=-1;
		for (Point p : points) {
			minx = Math.min(minx, p.x);
			miny = Math.min(miny, p.y);
			maxx = Math.max(maxx, p.x);
			maxy = Math.max(maxy, p.y);
		}
		int avgX = (minx + maxx) / 2;
		int avgY = (miny + maxy) / 2;
		int t = nodeSize / 2;
		return new Node(avgX - t, avgY - t, nodeSize, nodeSize);
	}

	public void simplifyPoints() {
		// the following three yield a nice result for lines with strict corners
		eliminateNearNeighbours();
		adjustAngles();	
		rectify2();
		
		paintAll(getGraphics());
		this.repaint();
	}

	private void eliminateNearNeighbours() {
		double epsilon = 2.0;
		Point last = new Point(-1, -1);
		for (ListIterator<Point> it = (ListIterator<Point>) points.iterator(); it.hasNext();) {
			Point point = (Point) it.next();
			if (point.distance(last) <= epsilon) {
				it.remove();
			} 
			else {
				last = point;
			}
		}
	}

	private void adjustAngles() {
		double lastangle = Math.atan((double)(points.get(1).y - points.get(0).y)/(points.get(1).x-points.get(0).x));
		double angleepsilon = Math.PI;
		for (int i = 1; i < points.size()-1; i++) {
			double angle = Math.atan((double)((double)points.get(i).y - (double)points.get(i-1).y)/((double)points.get(i).x-(double)points.get(i-1).x));
			if (Math.abs(angle - lastangle) < angleepsilon){
				points.remove(i);
			}
			else {
				lastangle = angle;
			}
		}
	}
	
	private double pointLineDistance(Point a, Point b, Point c){
		// compute projection point d on the line ab (such that cd is perpendicular to ab)
		double v1 = b.x -a.x;
		double v2 = b.y -a.y;
		double alpha = (v1 * (c.x - a.x) + v2 * (c.y -a.y)) / (v1 * v1 + v2 * v2);
		Point d = new Point((int)(a.x + alpha * v1), (int)(a.y + alpha * v2));
		return d.distance(c);
	}
	
	private void rectify(){
		for (int i = 0; i < points.size()-2; i++) {
			Point p = points.get(i+1);
			p.x += (points.get(i).x + points.get(i+2).x)/2;
			p.x /= 2;
			p.y += (points.get(i).y + points.get(i+2).y)/2;
			p.y /= 2;
		}
	}

	private void rectify2(){
		for (int k=0; k<3; k++){
			for (int i = 0; i < points.size()-2; i++) {
				if (pointLineDistance(points.get(i), points.get(i+2), points.get(i+1)) < points.get(i).distance(points.get(i+2))/4.0){
					points.remove(i+1);
				}
			}
		}
	}
	
	public void createEdge() {
		assert (startingNode != null);
		assert (endingNode  !=  null);
		edges.add(new Edge(startingNode, endingNode));
	}

	public void relaseNodeBindings() {
		startingNode = null;
		endingNode = null;
		activeNode = null;
	}
	
	// this beast implements the whole logic for drawing stuff
	private class MouseDrawingListener implements MouseMotionListener,
			MouseListener {
		private boolean drawing = false;
		private DrawCanvas canvas = null;
		private CanvasMode mode = CanvasMode.Node; // default: drawing nodes
		
		public MouseDrawingListener(DrawCanvas p) {
			canvas = p;
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			canvas.addPoint(e.getX(), e.getY());
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			canvas.setActiveNode(null);
			int x = e.getX();
			int y = e.getY();
			for (Node n : canvas.nodes) {
				if (x >= n.getX() && 
						x <= n.getX() + n.getWidth() && 
						y >= n.getY() && 
						y <= n.getY()+n.getHeight()){
					canvas.setActiveNode(n);
					return;
				}
			}
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			canvas.removeAllPoints();
			// are we creating an edge?
			if (canvas.getActiveNode() != null){
				System.out.println("Drawing an edge");
				mode = CanvasMode.Edge;
				canvas.setStartingNode(canvas.getActiveNode());
			}
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			canvas.simplifyPoints();
			switch (mode) {
			case Node:
				canvas.createNode();
				break;
			case Edge:
				Node tmp = canvas.getNodeAtPoint(arg0.getX(), arg0.getY());
				if (tmp != null){
					canvas.setEndingNode(tmp);
					canvas.createEdge();
					canvas.relaseNodeBindings();
				}
			default:
				break;
			}
			mode = CanvasMode.Node;
		}
	}

}

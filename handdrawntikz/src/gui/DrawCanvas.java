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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.jws.WebParam.Mode;

public class DrawCanvas extends Panel {
	
	private static final long serialVersionUID = 1L;
	private LinkedList<Point> points = new LinkedList<Point>();
	
	LinkedList<Node> nodes = new LinkedList<Node>();
	private LinkedList<Edge> edges = new LinkedList<Edge>();
	
	private Node activeNode = null; // node under cursor
	private Node startingNode = null; // node to start edge at
	
	private boolean autoAdjustSizes = true; // automatically adjust node size
	private int nodeSize = 50;
	
	private CanvasMenu canvasMenu = new CanvasMenu(this);
	
	// ugly: Canvas and its MouseDrawingListener share this thing so that
	// each of them knows in which mode it is.
	
	public CanvasMenu getCanvasMenu() {
		return canvasMenu;
	}

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
		drawEdges(g);
		drawNodes(g);
		drawBrushLine(g);
	}

	private void drawBrushLine(Graphics g) {
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

	private void drawEdges(Graphics g) {
		for (Edge e : edges) {
			g.drawLine(e.getV1().getMidPoint().x, e.getV1().getMidPoint().y, e.getV2().getMidPoint().x, e.getV2().getMidPoint().y);
		}
	}

	private void drawNodes(Graphics g) {
		((Graphics2D)(g)).setStroke(new BasicStroke(1));
		for (Node n : nodes) {
			g.setColor(new Color(255,255,255));
			g.fillRect(n.getX(), n.getY(), n.getWidth(), n.getHeight());
			g.setColor(new Color(0, 0, 0));
			g.drawRect(n.getX(), n.getY(), n.getWidth(), n.getHeight());
		}
		((Graphics2D)(g)).setStroke(new BasicStroke(2));
		g.setColor(new Color(0, 255, 0));
		if (activeNode != null){
			g.drawRect(activeNode.getX(), activeNode.getY(), activeNode.getWidth(), activeNode.getHeight());
		}
		((Graphics2D)(g)).setStroke(new BasicStroke(1));
		g.setColor(new Color(0, 0, 0));
	}

	public void addPoint(int x, int y) {
		points.add(new Point(x, y));
		this.paint(getGraphics());
	}

	public void removeAllPoints() {
		points.clear();
	}

	public void recognizeShape(){
		if (points.size()==5){
			createNode(nodeFromPoints());
		}	
	}
	
	public void createNode(Node n){
		nodes.add(n);
		repaint();
	}
	
	public void deleteActiveNode(){
		deleteNode(getActiveNode());
	}
		
	public void deleteNode(Node n){
		nodes.remove(n);
		for (Iterator<Edge> i = edges.iterator(); i.hasNext();) {
			Edge e = (Edge) (i.next());
			if (e.getV1() == n || e.getV2() == n){
				i.remove();
			}
		}
		repaint();
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
		if (autoAdjustSizes){
			return new Node(avgX - t, avgY - t, nodeSize, nodeSize);
		}
		return new Node (minx, miny, maxx-minx, maxy-miny);
	}

	public void simplifyPoints() {
		if (points.size() <= 2){
			return;
		}
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

}

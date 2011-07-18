package graph;

import java.awt.Point;

public class Node {
	private int x, y, width, height;

	public Node(int x, int y, int width, int height) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public boolean pointInNode(int px, int py){
		return x <= px && px <= x + width && y <= py && py <= y+ height;
	}
	
	public boolean pointInNode(Point p){
		return pointInNode(p.x, p.y);
	}
	
	public Point getMidPoint(){
		return new Point(x + width/2, y+height/2);
	}
	
}

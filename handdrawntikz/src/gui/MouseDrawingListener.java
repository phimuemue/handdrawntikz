package gui;

import graph.Node;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

// this beast implements the whole logic for drawing stuff
class MouseDrawingListener implements MouseMotionListener,
		MouseListener {
	private DrawCanvas canvas = null;
	private CanvasMode mode = CanvasMode.Node; // default: drawing nodes

	private int moveStartX, moveStartY;
	
	public MouseDrawingListener(DrawCanvas p) {
		canvas = p;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (mode == CanvasMode.Move){
			canvas.getActiveNode().setX(e.getX() - moveStartX);
			canvas.getActiveNode().setY(e.getY() - moveStartY);
			canvas.repaint();
		}
		else {
			canvas.addPoint(e.getX(), e.getY());
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// if currently busy, just skip this stuff
		if (mode == CanvasMode.Move){
			return;
		}
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
		if (arg0.getButton() == MouseEvent.BUTTON3){
			canvas.getCanvasMenu().show(canvas, arg0.getX(), arg0.getY());
		}
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
			if (arg0.getButton() == MouseEvent.BUTTON1){
				System.out.println("Drawing an edge");
				mode = CanvasMode.Edge;
				canvas.setStartingNode(canvas.getActiveNode());
			}
			if (arg0.getButton() == MouseEvent.BUTTON2){
				System.out.println("Moving mode");
				moveStartX = arg0.getX() - canvas.getActiveNode().getX();
				moveStartY = arg0.getY() - canvas.getActiveNode().getY();
				mode = CanvasMode.Move;
			}
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
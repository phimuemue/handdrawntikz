package gui;

import graph.Node;

import intern.TikZExporter;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;

public class CanvasMenu extends JPopupMenu {
	
	private DrawCanvas canvas;
	
	public CanvasMenu(final DrawCanvas _canvas){
		canvas = _canvas;
		this.add(new CanvasMenuAction("Split vertex") {
			@Override
			public void action(ActionEvent arg0) {
				Node activeNode = canvas.getActiveNode();
				if (activeNode != null){
					canvas.createNode(new Node(activeNode.getX() + 70, activeNode.getY(), activeNode.getWidth(), activeNode.getHeight()));
				}
			}
		});
		
		this.add(new CanvasMenuAction("Delete Vertex") {
			@Override
			public void action(ActionEvent e) {
				canvas.deleteActiveNode();
			}
		});
		
		this.add(new CanvasMenuAction("Export to TikZ") {
			@Override
			public void action(ActionEvent e) {
				TikZExporter te = new TikZExporter(canvas.getGraph());
				te.export("testpath");
			}
		});
	}
	
	private abstract class CanvasMenuAction extends AbstractAction {

		public CanvasMenuAction(String s){
			super(s);
		}
		
		@Override
		final public void actionPerformed(ActionEvent e) {
			action(e);
			// cleanup stuff after menu has been closed
			canvas.repaint();
			canvas.setActiveNode(null);
		}
		
		abstract public void action(ActionEvent e);
	}
	
}

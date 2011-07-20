package gui;

import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class MainWindow extends Frame {

	public MainWindow(){
		this.setSize(800, 600);
		DrawCanvas dc = new DrawCanvas();
		this.add(dc);
		//dc.initialize();
		this.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent arg0) {
				
			}
			
			@Override
			public void windowIconified(WindowEvent arg0) {
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent arg0) {
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent arg0) {
				
			}
			
			@Override
			public void windowClosing(WindowEvent arg0) {
				System.exit(0);
			}
			
			@Override
			public void windowClosed(WindowEvent arg0) {
								
			}
			
			@Override
			public void windowActivated(WindowEvent arg0) {
				
			}
		});
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Initializing main window");
		MainWindow mw = new MainWindow();
		mw.setVisible(true);
	}

}

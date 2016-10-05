package org.first.team2485.scoutingform;

import java.awt.Dimension;

import javax.swing.JPanel;

public class GamblingPanel extends JPanel {
	
	protected GamblingPanel() {
		this.setPreferredSize(new Dimension(300, 600));
		
		new Thread(() -> updateWindow()).start();
	}
	
	private void updateWindow() {
		
		while (true) {
			
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}

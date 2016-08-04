package org.first.team2485.scoutingserver;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * 
 * @author Troy Appel
 *
 */
//test
@SuppressWarnings("serial")
public class QuitButton extends JButton {
	
	private  JFrame comp;
		
	public QuitButton(JFrame frame) {
		super("Quit");
				
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				showConfirmation();
			}
		});
		comp = frame;

		this.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showConfirmation();
			}
		});
		
	}
	private void showConfirmation () {
		
		String msg = "";

		msg = msg + "Are you sure you want to quit?";
		
		int status = JOptionPane.showConfirmDialog(comp, msg, "Confirmation", JOptionPane.YES_NO_OPTION);
		
		if (status == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
		else {
			return;
		}
	}


}

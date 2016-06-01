package org.first.team2485.scoutingform;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.first.team2485.scoutingform.formIO.FormIO;

public class SetupButton extends JButton implements ActionListener {

	private ScoutingForm form;
	
	private SetupButton me;

	public SetupButton(ScoutingForm form) {

		super();

		this.form = form;
		this.addActionListener(this);
		
		me = this;
		
		new Timer(false).schedule(new TimerTask() {
			
			@Override
			public void run() {
				Process p = FormIO.getInstance().getProcess();
				
				if (p != null) {
					if (p.isAlive()) {
						me.setText("Bluetooth Working");
						me.setEnabled(false);
						me.setBackground(Color.LIGHT_GRAY);
						me.setToolTipText("The Underlying Bluetooth System is Working Properly");
					} else {
						me.setText("RESTART BLUETOOTH");
						me.setEnabled(true);
						me.setBackground(Color.ORANGE);
						me.setToolTipText("The Scouting Server Could Not Be Found. Click To Try Again");
					}
				}
			}
		}, 0, 1000);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		FormIO.getInstance().startScript();
	}
}

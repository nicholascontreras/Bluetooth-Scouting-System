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

						if ((System.currentTimeMillis() / 500) % 2 == 0) {
							me.setBackground(Color.ORANGE);
						} else {
							me.setBackground(Color.RED);
						}
						me.setToolTipText("The Underlying Bluetooth System has Crashed. Click To Try Again");
					}
				}
			}
		}, 0, 100);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		FormIO.getInstance().startScript();
	}
}

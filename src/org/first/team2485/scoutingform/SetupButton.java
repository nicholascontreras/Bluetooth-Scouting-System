package org.first.team2485.scoutingform;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.first.team2485.scoutingform.formIO.FormIO;

public class SetupButton extends JButton implements ActionListener {

	private ScoutingForm form;

	public SetupButton(ScoutingForm form) {

		super("Setup My Computer");

		this.form = form;
		this.addActionListener(this);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JOptionPane.showMessageDialog(null, new FormIO().setup());
	}
}

package org.first.team2485.scoutingform;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.first.team2485.scoutingform.formIO.FormIO;

/**
 * 
 * @author Jeremy McCulloch
 *
 */
@SuppressWarnings("serial")
public class SubmitButton extends JButton implements ActionListener {

	private ScoutingForm form;

	public SubmitButton(ScoutingForm form) {

		super("Submit");

		this.form = form;
		this.addActionListener(this);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String dataToSend = form.submit();

		FormIO f = new FormIO();

		String setupResult = f.setup();

		if (!setupResult.equals("Setup Sucessful")) {
			JOptionPane.showMessageDialog(null, setupResult);
			return;
		}

		String result = f.saveAndSendData(dataToSend, true, false);

		if (result.equals("Unsent data already exists")) {
			int sendFirst = JOptionPane.showConfirmDialog(null, result + "\nDo you want to send it first?", "Warning",
					JOptionPane.YES_NO_OPTION);
			System.out.println(sendFirst);

			if (sendFirst == JOptionPane.YES_OPTION) {
				result = f.saveAndSendData(null, false, false);
				
				if (!result.equals("Sucessfuly sent scouting data")) {
					JOptionPane.showMessageDialog(null, result + " (while sending old data)");
					return;
				}
			}
			result = f.saveAndSendData(dataToSend, true, true);
		}

		JOptionPane.showMessageDialog(null, result);

		if (!result.equals("Sucessfuly sent scouting data")) {
			return;
		}

		form.reset();
	}
}

package org.first.team2485.scoutingform.questions;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner.DefaultEditor;

/**
 * 
 * @author Jeremy McCulloch
 *
 */
@SuppressWarnings("serial")
public class SpinnerQuestion extends Question{
		
	JLabel promptLabel;
	ButtonGroup optionButtonGroup;
	JSpinner spinner;
	
	public SpinnerQuestion(String prompt) {
		
		promptLabel = new JLabel(prompt);
		this.add(promptLabel);
		
		spinner = new JSpinner(new SpinnerNumberModel());
		((DefaultEditor) spinner.getEditor()).getTextField().setColumns(5);
		this.add(spinner);
	
	}
	
	public String getData() {
		return (int) spinner.getValue() + ",";
	}
	
	public void clear() {
		spinner.setValue(0);
	}
}

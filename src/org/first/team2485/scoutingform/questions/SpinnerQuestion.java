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
		
	private JLabel promptLabel;
	private ButtonGroup optionButtonGroup;
	private JSpinner spinner;
	
	private String internalName;
	
	public SpinnerQuestion(String prompt, String internalName) {
		
		promptLabel = new JLabel(prompt);
		this.add(promptLabel);
		
		spinner = new JSpinner(new SpinnerNumberModel());
		((DefaultEditor) spinner.getEditor()).getTextField().setColumns(5);
		this.add(spinner);
		
		this.internalName = internalName;
	
	}
	
	public String getData() {
		return internalName + "," + (int) spinner.getValue() + ",";
	}
	
	public void clear() {
		spinner.setValue(0);
	}
}

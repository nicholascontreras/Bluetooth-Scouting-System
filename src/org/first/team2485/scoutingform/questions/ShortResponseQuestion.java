package org.first.team2485.scoutingform.questions;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * 
 * @author Nicholas Contreras
 *
 */
@SuppressWarnings("serial")
public class ShortResponseQuestion extends Question {
		
	JLabel promptLabel;
	ButtonGroup optionButtonGroup;
	JTextField textField;
	
	boolean canClear;
	
	public ShortResponseQuestion(String prompt, boolean canClear) {
		promptLabel = new JLabel(prompt);
		this.add(promptLabel);
		
		textField = new JTextField(15);
		this.add(textField);
		
		this.canClear = canClear;
	}
	
	public String getData() {
		return "\"" + textField.getText() + "\",";
	}
	
	
	public void clear() {
		if (canClear) {
			this.textField.setText("");
		}
	}
}

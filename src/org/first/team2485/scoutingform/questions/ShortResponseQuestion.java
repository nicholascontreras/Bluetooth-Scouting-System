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
		
	private JLabel promptLabel;
	private ButtonGroup optionButtonGroup;
	private JTextField textField;
	
	private boolean canClear;
	
	private String internalName;
	
	public ShortResponseQuestion(String prompt, String internalName, boolean canClear) {
		promptLabel = new JLabel(prompt);
		this.add(promptLabel);
		
		textField = new JTextField(15);
		this.add(textField);
		
		this.canClear = canClear;
		
		this.internalName = internalName;
	}
	
	public String getData() {
		return internalName + "=\"" + textField.getText() + "\",";
	}
	
	
	public void clear() {
		if (canClear) {
			this.textField.setText("");
		}
	}
}

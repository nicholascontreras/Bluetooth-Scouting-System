package org.first.team2485.scoutingform.questions;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

/**
 * 
 * @author Jeremy McCulloch
 *
 */
@SuppressWarnings("serial")
public class MultipleChoiceQuestion extends Question {
	
	private JLabel promptLabel;
	private ButtonGroup optionButtonGroup;
	private JRadioButton[] optionButtons;
	
	private String internalName;
	
	public MultipleChoiceQuestion(String prompt, String internalName, String... options) {
		promptLabel = new JLabel(prompt);
		this.add(promptLabel);
		
		optionButtonGroup = new ButtonGroup();
		
		optionButtons = new JRadioButton[options.length];
		for (int i = 0; i < options.length; i++) {
			optionButtons[i] = new JRadioButton(options[i]);		
			this.add(optionButtons[i]);
			optionButtonGroup.add(optionButtons[i]);
		}	
		this.internalName = internalName;
	}
	
	public String getData() {
		for (int i = 0; i < optionButtons.length; i++) {
			if (optionButtons[i].isSelected()) {
				return internalName + "," + i + ",";
			}
		}
		return internalName + ",-1,";
	}
	public void clear() {
		optionButtonGroup.clearSelection();
	}
	
}

package org.first.team2485.scoutingform.questions;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JTextArea;

/**
 * 
 * @author Jeremy McCulloch
 *
 */
@SuppressWarnings("serial")
public class FreeResponseQuestion extends Question {
		
	private JLabel promptLabel;
	private ButtonGroup optionButtonGroup;
	private JTextArea area;
	
	private String internalName;
	
	public FreeResponseQuestion(String prompt, String internalName) {
		promptLabel = new JLabel(prompt);
		this.add(promptLabel);
		
		area = new JTextArea(5, 50);
		this.add(area);
		
		this.internalName = internalName;
	}
	
	public String getData() {
		
		String text = area.getText().replace(',', ';');
	
		return internalName + "," + text + ",";
	}
	public void clear() {
		this.area.setText("");
	}
}

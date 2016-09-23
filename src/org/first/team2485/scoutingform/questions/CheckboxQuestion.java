package org.first.team2485.scoutingform.questions;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

/**
 * 
 * @author Jeremy McCulloch
 *
 */
@SuppressWarnings("serial")
public class CheckboxQuestion extends Question {
		
	private JLabel promptLabel;
	private JCheckBox[] checkboxes;
	
	private String internalName;
	
	public CheckboxQuestion(String prompt, String internalName, String... options) {
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		promptLabel = new JLabel(prompt);
		this.add(promptLabel);
				
		checkboxes = new JCheckBox[options.length];
		for (int i = 0; i < options.length; i++) {
			checkboxes[i] = new JCheckBox(options[i]);		
			this.add(checkboxes[i]);
		}
		
		this.internalName = internalName;
	}
	
	public String getData() {
		String data = internalName + "=";
		for (int i = 0; i < checkboxes.length; i++) {
			data += (checkboxes[i].isSelected() ? "1^" : "0^");
		}
		
		data = data.substring(0, data.length() - 1) + ",";
		
		return data;
	}
	
	public void clear() {
		for(int i = checkboxes.length - 1; i >= 0; i--) {
			checkboxes[i].setSelected(false);
		}
	}
}

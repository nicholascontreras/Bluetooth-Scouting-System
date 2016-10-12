package org.first.team2485.scoutingform;
import java.awt.Component;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.first.team2485.scoutingform.questions.Question;

/**
 * 
 * @author Jeremy McCulloch
 *
 */
@SuppressWarnings("serial")
public class ScoutingFormTab extends JPanel {
	
	private Question[] questions;
	private String name;

	public ScoutingFormTab(String name, Question... questions) {
		
		this.name = name;
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		this.questions = questions;
		for (Question question : questions) {
			this.add(question);
		}
		
	}
	
	public String getName() {
		
		return name;
		
	}
	
	public String getData() {
		
		String output = "";

		for (Question question : questions) {
			output += question.getData();
		}

		return output;
		
	}
	
	public void clear() {
		for (int i = 0; i < questions.length; i++) {
			questions[i].clear();
		}
	}
	
	
	
}

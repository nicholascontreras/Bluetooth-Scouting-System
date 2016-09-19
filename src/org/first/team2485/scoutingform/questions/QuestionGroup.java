package org.first.team2485.scoutingform.questions;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;

import org.jdesktop.swingx.JXCollapsiblePane;

/**
 * 
 * @author Jeremy McCulloch
 *
 */
@SuppressWarnings("serial")
public class QuestionGroup extends Question {
	 
	private JXCollapsiblePane pane;
	private JCheckBox checkbox;
	private Question[] questions;

	public QuestionGroup(String title, Question... questions) {
		
		this.setLayout(new BorderLayout());
		
		checkbox = new JCheckBox(title);
		checkbox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				update();
			}
		});
		this.add(checkbox, BorderLayout.CENTER);
		
		pane = new JXCollapsiblePane();
		for (Question question : questions) {
			pane.add(question);
		}
		pane.setCollapsed(true);
		pane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.add(pane, BorderLayout.SOUTH);
		this.questions = questions;
	}
	
	public void update() {
		pane.setCollapsed(!checkbox.isSelected());
	}
	
	@Override
	public String getData() {
		return "";
	}
	
	@Override
	public void clear() {
		System.out.println(questions.length);
		for(int i = 0; i < questions.length; i++) {
			System.out.println(questions[i].getData());
			questions[i].clear();
		}
		this.pane.setCollapsed(true);
		this.checkbox.setSelected(false);
	}
}

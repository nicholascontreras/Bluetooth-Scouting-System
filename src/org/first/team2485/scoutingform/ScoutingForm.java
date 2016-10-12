package org.first.team2485.scoutingform;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.first.team2485.scoutingform.questions.CheckboxQuestion;
import org.first.team2485.scoutingform.questions.FreeResponseQuestion;
import org.first.team2485.scoutingform.questions.MultipleChoiceQuestion;
import org.first.team2485.scoutingform.questions.QuestionGroup;
import org.first.team2485.scoutingform.questions.ShortResponseQuestion;
import org.first.team2485.scoutingform.questions.SpinnerQuestion;

/**
 * 
 * @author Jeremy McCulloch
 * @author Troy Appel
 *
 */
@SuppressWarnings("serial")
public class ScoutingForm extends JPanel {

	private JFrame frame;
	private ScoutingFormTab[] tabs;
	private JTabbedPane tabbedPane;
	public static String name;

	public ScoutingForm(ScoutingFormTab... tabs) {

		ClientPythonInterface.getInstance().startScript();

		frame = new JFrame();
		JPanel wrapperPanel = new JPanel(new BorderLayout());
		wrapperPanel.add(this, BorderLayout.CENTER);
		this.setPreferredSize(new Dimension(600, 400));
		wrapperPanel.add(new ChatWindows(), BorderLayout.EAST);
		wrapperPanel.add(new GamblingPanel(), BorderLayout.WEST);

		frame.add(wrapperPanel);

		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		this.tabbedPane = new JTabbedPane();

		this.tabs = tabs;
		for (ScoutingFormTab tab : tabs) {
			tabbedPane.add(tab.getName(), new JScrollPane(tab));
		}

		this.add(tabbedPane);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout());
		buttonPane.add(new SubmitButton(this));
		buttonPane.add(new QuitButton(this.frame));// this handles all quitting
													// logic
		buttonPane.add(new ClearButton(tabs, this.frame));
		buttonPane.add(new SetupButton(this));
		this.add(buttonPane);

		frame.pack();
		frame.setVisible(true);
		this.repaint();

	}

	public String submit() {

		String output = "";

		for (ScoutingFormTab tab : tabs) {
			output += tab.getData();
		}
		return output;
	}

	public JFrame getFrame() {
		return frame;
	}

	public void reset() {
		for (ScoutingFormTab tab : tabs) {
			tab.clear();
		}

		tabbedPane.setSelectedIndex(0);
	}

	public static void main(String[] args) {

		name = JOptionPane.showInputDialog("Scout's Name:");
		
		if (name == null || name.isEmpty()) {
			System.exit(0);
		}

		//@formatter:off
		
		ScoutingFormTab prematch = new ScoutingFormTab("Prematch", 
			new SpinnerQuestion("Team number:", "teamnumber"),
			new SpinnerQuestion("Match Number:", "matchnumber"),
			new CheckboxQuestion("No Show", "noShow", "Did their robot not show up?")
		);
		
		ScoutingFormTab autonomous = new ScoutingFormTab("Autonomous", 
			new MultipleChoiceQuestion("Which defence did they line up in front of?", "autoDefence", "CDF", "Moat", "Ramparts", "Rock Wall", "Rough Terrain", "Low Bar", "None(Spy)"),
			new CheckboxQuestion("Did they...", "autoDefenceAction", "Approach a Defense", "Cross a Defense"), 
			new SpinnerQuestion("How many high goals did they make?", "autoHighGoals"),
			new SpinnerQuestion("How many high goals did they miss?", "autoHighMiss"),
			new SpinnerQuestion("How many low goals did they make?", "autoLowGoals"),
			new SpinnerQuestion("How many low goals did they miss?", "autoLowMiss")

		);
		
		ScoutingFormTab teleop = new ScoutingFormTab("Teleop", 
			new QuestionGroup("CDF", 
				new MultipleChoiceQuestion("How long did it take to cross on avg (seconds)?", "CDFTime", "0 - 2", "3 - 4", "5 - 6", "7 - 8", "8+", "Failed"),
				new SpinnerQuestion("How many times did they cross it?", "CDFCount")
			),
			new QuestionGroup("Moat", 
				new MultipleChoiceQuestion("How long did it take to cross on avg (seconds)?", "moatTime", "0 - 2", "3 - 4", "5 - 6", "7 - 8", "8+", "Failed"),
				new SpinnerQuestion("How many times did they cross it?", "moatCount")
			),
			new QuestionGroup("Ramparts", 
				new MultipleChoiceQuestion("How long did it take to cross on avg (seconds)?", "rampartsTime", "0 - 2", "3 - 4", "5 - 6", "7 - 8", "8+", "Failed"),
				new SpinnerQuestion("How many times did they cross it?", "rampartsCount")
			),
			new QuestionGroup("Rock Wall", 
				new MultipleChoiceQuestion("How long did it take to cross on avg (seconds)?", "rockWallTime","0 - 2", "3 - 4", "5 - 6", "7 - 8", "8+", "Failed"),
				new SpinnerQuestion("How many times did they cross it?", "rockWallCount")
			),
			new QuestionGroup("Rough Terrain", 
					new MultipleChoiceQuestion("How long did it take to cross on avg (seconds)?", "roughTerrainTime", "0 - 2", "3 - 4", "5 - 6", "7 - 8", "8+", "Failed"),
					new SpinnerQuestion("How many times did they cross it?", "roughTerraincount")
			),
			new QuestionGroup("Low Bar", 
				new MultipleChoiceQuestion("How long did it take to cross on avg (seconds)?", "lowBarTime", "0 - 2", "3 - 4", "5 - 6", "7 - 8", "8+", "Failed"),
				new SpinnerQuestion("How many times did they cross it?", "lowBarCount")
			),
			new SpinnerQuestion("How many high goals did they make?", "highMade"),
			new SpinnerQuestion("How many high goals did they miss?", "highMiss"),
			new SpinnerQuestion("How many low goals did they make?", "lowMade"),
			new SpinnerQuestion("How many low goals did they miss?", "lowMiss"),
			new MultipleChoiceQuestion("Did they...", "endGameState", "Challenge", "Scale", "Neither")
		);//<--- sad winky face 
		
		ScoutingFormTab ratings = new ScoutingFormTab("Ratings", 
			new MultipleChoiceQuestion("Intake Speed:", "speed", "N/A", "1", "2", "3", "4", "5"),
			new MultipleChoiceQuestion("Manueverability", "manueverability", "1", "2", "3" , "4", "5"),
			new MultipleChoiceQuestion("Defense", "defense", "N/A", "1", "2", "3", "4", "5"), 
			new MultipleChoiceQuestion("Driver Skill", "driverSkill", "1", "2", "3", "4", "5")
			
		);
		
		ScoutingFormTab misc = new ScoutingFormTab("Miscellaneous", 
			new SpinnerQuestion("How many fouls?", "otherFouls"),
			new MultipleChoiceQuestion("Did they break down?", "breakdown", "Yes", "No"),
			new FreeResponseQuestion("Comments:", "comments")
		);
		
		
		//@formatter:on

		new ScoutingForm(prematch, autonomous, teleop, ratings, misc);

	}
}

package org.first.team2485.scoutingform;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.first.team2485.common.Message;
import org.first.team2485.common.Message.MessageType;
import org.first.team2485.scoutingform.questions.MultipleChoiceQuestion;
import org.first.team2485.scoutingform.questions.SpinnerQuestion;

public class GamblingPanel extends JPanel {

	private ArrayList<GamblingScout> gamblingScouts;
	private JPanel panel;

	protected GamblingPanel() {
		this.setPreferredSize(new Dimension(500, 600));

		this.setLayout(new BorderLayout());
		
		panel = new JPanel();
		
		panel.setBorder(BorderFactory.createTitledBorder("Gambling"));

		gamblingScouts = new ArrayList<GamblingScout>();
		
		JTextArea winOrLose = new JTextArea();
		SpinnerQuestion amountBet = new SpinnerQuestion ("Amount Bet", "amountbet");
		MultipleChoiceQuestion winningTeam = new MultipleChoiceQuestion("Which alliance do you think will win?", "WinningAlliance", "Red Alliance", "Blue Alliance");
		SpinnerQuestion winningScore = new SpinnerQuestion ("How much do you think the winning team will score?", "winningscore");
		JButton sendButton = new JButton("Submit Bet");
		
		
		panel.add(winOrLose);
		panel.add(winningTeam);
		panel.add(winningScore);
		panel.add(amountBet);
		panel.add(sendButton);
		
		

		for (int i = 0; i < 10; i++) {
			GamblingScout newScout = new GamblingScout(ScoutingForm.name, 100); //get Name?

			gamblingScouts.add(newScout);

			this.add(newScout, BorderLayout.SOUTH);
		}
		
		if (amountBet.getData() != null){
			String betQuestionResult = amountBet.getData();
			int curBet = Integer.parseInt(betQuestionResult.substring(betQuestionResult.indexOf(",") + 1, betQuestionResult.length()-1));

			if (curBet > gamblingScouts.get(gamblingScouts.size()-1).getMoney()){
				curBet = gamblingScouts.get(gamblingScouts.size()-1).getMoney();
			}
		}

		this.add(panel, BorderLayout.CENTER);
		
		new Thread(() -> updateWindow()).start();
	}

	private void updateWindow() {

		while (true) {

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			ArrayList<Message> unhandledMessages = ClientPythonInterface.getInstance().unhandledMessages;

			if (!unhandledMessages.isEmpty()) {

				Message curMessage = unhandledMessages.get(0);

				if (curMessage.getMessageType() == MessageType.BET_PAYOUT) {

					ClientPythonInterface.getInstance().unhandledMessages.remove(0);

					String message = curMessage.getMessage();

					while (true) {

						String curScout = message.substring(0, message.indexOf(","));

						String curName = curScout.substring(0, curScout.indexOf("="));
						int curMoney = Integer.parseInt(curScout.substring(curScout.indexOf(",") + 1));

						boolean exists = false;

						for (GamblingScout cur : gamblingScouts) {
							if (cur.name.equals(curName)) {
								cur.money = curMoney;
								cur.update();
								exists = true;
								break;
							}
						}

						if (!exists) {

							GamblingScout newScout = new GamblingScout(curName, curMoney);

							gamblingScouts.add(newScout);

							if (gamblingScouts.size() % 3 == 0) {
								this.add(newScout, BorderLayout.SOUTH);
							} else if (gamblingScouts.size() % 3 == 1) {
								this.add(newScout, BorderLayout.EAST);
							} else {
								this.add(newScout, BorderLayout.WEST);
							}
						}

						int index = message.indexOf(",");

						if (index == -1) {
							break;
						}

						message = message.substring(index + 1);
					}
				}
			}
		}
	}

	private class GamblingScout extends JPanel {

		private String name;
		private int money;

		private JLabel nameLabel;
		private JLabel moneyLabel;

		private GamblingScout(String name, int money) {

			this.name = name;
			this.money = money;

			this.setLayout(new GridLayout(2, 1));

			nameLabel = new JLabel(name);
			nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
			this.add(nameLabel);

			moneyLabel = new JLabel("$" + money);
			moneyLabel.setHorizontalAlignment(SwingConstants.CENTER);
			this.add(moneyLabel);
			
			this.setBorder(new EmptyBorder(5, 5, 5, 5));
		}

		public int getMoney(){
			return money;
		}
		private void update() {
			moneyLabel.setText("$" + money);
		}
	}

	
}

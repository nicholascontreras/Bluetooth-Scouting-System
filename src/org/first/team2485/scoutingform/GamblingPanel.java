package org.first.team2485.scoutingform;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
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

public class GamblingPanel extends JPanel implements ActionListener {

	private ArrayList<GamblingScout> gamblingScouts;
	private JPanel panel;
	private JPanel scoutContainer;
	private JTextArea winOrLose;
	private SpinnerQuestion amountBet;
	private MultipleChoiceQuestion winningTeam;
	private SpinnerQuestion winningDifference;
	private JButton sendButton;
	private boolean gamblingOpen = true;
	private int curMoneyBet;
	private JTextArea currentMoneyBet;

	protected GamblingPanel() {
		this.setPreferredSize(new Dimension(500, 600));

		this.setLayout(new BorderLayout());

		panel = new JPanel();

		panel.setBorder(BorderFactory.createTitledBorder("Gambling"));
		panel.setPreferredSize(new Dimension(500, 450));

		gamblingScouts = new ArrayList<GamblingScout>();
		
		for (GamblingScout g : gamblingScouts){
			curMoneyBet = g.getMoney();
		}

		winOrLose = new JTextArea();
		amountBet = new SpinnerQuestion("Amount Bet", "amountbet");
		winningTeam = new MultipleChoiceQuestion("Which alliance do you think will win?",
				"WinningAlliance", "Red Alliance", "Blue Alliance");
		winningDifference = new SpinnerQuestion("What do you think the point difference will be between the winning and losing alliance?",
				"winningDifference");
		sendButton = new JButton("Submit Bet");
		sendButton.setActionCommand("SendButton");
		sendButton.addActionListener(this);
		currentMoneyBet = new  JTextArea("Current money bet on this match: $" + curMoneyBet);
		currentMoneyBet.setFont(currentMoneyBet.getFont().deriveFont(14f));

		panel.add(winOrLose);
		panel.add(winningTeam);
		panel.add(winningDifference);
		panel.add(amountBet);
		panel.add(sendButton);
		panel.add(currentMoneyBet);

		scoutContainer = new JPanel();
		scoutContainer.setLayout(new FlowLayout());
		scoutContainer.setPreferredSize(new Dimension(500, 150));
		this.add(scoutContainer, BorderLayout.SOUTH);

		for (int i = 0; i < 10; i++) {
			GamblingScout newScout = new GamblingScout(i + ":", 100); // get
																		// Name?

			gamblingScouts.add(newScout);

			scoutContainer.add(newScout);
		}

		if (amountBet.getData() != null) {
			String betQuestionResult = amountBet.getData();
			int curBet = Integer.parseInt(
					betQuestionResult.substring(betQuestionResult.indexOf(",") + 1, betQuestionResult.length() - 1));

			if (curBet > gamblingScouts.get(gamblingScouts.size() - 1).getMoney()) {
				curBet = gamblingScouts.get(gamblingScouts.size() - 1).getMoney();
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
							scoutContainer.add(newScout);
						}

						int index = message.indexOf(",");

						if (index == -1) {
							break;
						}

						message = message.substring(index + 1);
					}
				} else if (curMessage.getMessageType() == MessageType.GAMBLING_STATUS) {
					ClientPythonInterface.getInstance().unhandledMessages.remove(0);
					
					if (curMessage.getMessage().equals("OPEN")) {
						gamblingOpen = true;
					} else if (curMessage.getMessage().equals("CLOSED")) {
						gamblingOpen = false;
					}
				} else if (curMessage.getMessageType() == MessageType.BET_CONFIRM) {
					ClientPythonInterface.getInstance().unhandledMessages.remove(0);
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

		public int getMoney() {
			return money;
		}

		private void update() {
			moneyLabel.setText("$" + money);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("SendButton")) {
			if (gamblingOpen){
				String getAmountBet = amountBet.getData().substring(amountBet.getData().indexOf(",") + 1);
				String getWinningTeam = winningTeam.getData().substring(winningTeam.getData().indexOf(",") + 1);
				String getWinningDifference = winningDifference.getData().substring(winningDifference.getData().indexOf(","), winningDifference.getData().length()-1);
				new Message("BetPlaced" + getAmountBet + "," + getWinningTeam + "," + getWinningDifference, "SERVER", ScoutingForm.name, MessageType.BET_PLACE);
			}
			else {
				new Message("Gambling is closed, try again after this match ends", ScoutingForm.name, "SERVER", MessageType.CHAT);
			}
		} 
	} 
}

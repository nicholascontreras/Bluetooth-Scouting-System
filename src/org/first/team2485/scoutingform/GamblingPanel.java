package org.first.team2485.scoutingform;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.first.team2485.common.Message;
import org.first.team2485.common.Message.MessageType;

public class GamblingPanel extends JPanel {

	private ArrayList<GamblingScout> gamblingScouts;

	protected GamblingPanel() {
		this.setPreferredSize(new Dimension(300, 600));

		this.setLayout(new BorderLayout());

		gamblingScouts = new ArrayList<GamblingScout>();

		for (int i = 0; i < 10; i++) {
			GamblingScout newScout = new GamblingScout(i + "", 100);

			gamblingScouts.add(newScout);

			this.add(newScout, BorderLayout.SOUTH);
		}

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

		private void update() {
			moneyLabel.setText("$" + money);
		}
	}
}

package org.first.team2485.scoutingform;

import org.first.team2485.common.Message;
import org.first.team2485.common.Message.MessageType;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatWindows extends JPanel implements ActionListener {

	private JTabbedPane tabbedPane;

	private ArrayList<JTextArea> textAreas;
	private ArrayList<JTextField> textFields;

	public ChatWindows() {

		this.setPreferredSize(new Dimension(600, 400));

		this.setLayout(new BorderLayout());

		tabbedPane = new JTabbedPane();

		textAreas = new ArrayList<JTextArea>();
		textFields = new ArrayList<JTextField>();

		tabbedPane.addTab("Broadcasts", createTab("Broadcasts"));
		tabbedPane.addTab("DM to Server", createTab("DM to Server"));
		tabbedPane.addTab("Debug Console", createTab("Debug Console"));

		this.add(tabbedPane, BorderLayout.CENTER);

		new Thread(() -> updateChatWindows()).start();
	}

	private JPanel createTab(String tabName) {
		JPanel chatTab = new JPanel(new BorderLayout());

		JTextArea chatWindow = new JTextArea();
		textAreas.add(chatWindow);
		chatWindow.setEditable(false);
		JTextField enterChatWindow = new JTextField();
		textFields.add(enterChatWindow);
		enterChatWindow.setEditable(true);
		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(this);
		sendButton.setActionCommand(tabName + " Button");

		JPanel bottomContainer = new JPanel(new BorderLayout());
		bottomContainer.add(enterChatWindow, BorderLayout.CENTER);
		bottomContainer.add(sendButton, BorderLayout.LINE_END);

		chatTab.add(chatWindow, BorderLayout.CENTER);
		chatTab.add(bottomContainer, BorderLayout.PAGE_END);

		return chatTab;
	}

	private void updateChatWindows() {

		while (true) {

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			ArrayList<Message> unhandledMessages = ClientPythonInterface.getInstance().unhandledMessages;

			if (!unhandledMessages.isEmpty()) {

				Message curMessage = ClientPythonInterface.getInstance().unhandledMessages.get(0);

				if (curMessage.getMessageType() == MessageType.CHAT) {
					
					ClientPythonInterface.getInstance().unhandledMessages.remove(0);

					SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
					String formatedTime = sdf.format(new Date(curMessage.getTimeSent()));

					String formattedMessage = curMessage.getSender() + " @ " + formatedTime + ": "
							+ curMessage.getMessage();

					if (curMessage.getReciever().equals(ScoutingForm.name)
							&& curMessage.getReciever().equals("SERVER") || curMessage.getReciever().equals("SERVER")
							&& curMessage.getReciever().equals(ScoutingForm.name)) {
						
						JTextArea serverDMWindow = textAreas.get(1);
						serverDMWindow.setText(serverDMWindow.getText() + "\n" + formattedMessage);
					} else if (curMessage.getReciever().equals("BROADCAST")) {
						JTextArea broadcastWindow = textAreas.get(0);
						broadcastWindow.setText(broadcastWindow.getText() + "\n" + formattedMessage);
					}
				}
				JTextArea debugWindow = textAreas.get(2);
				debugWindow.setText(debugWindow.getText() + "\n" + curMessage.getMessage());
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		String actionCommand = arg0.getActionCommand();

		Message msg = null;

		if (actionCommand.equals("Broadcasts Button")) {
			msg = new Message(textFields.get(0).getText(), "BROADCAST", ScoutingForm.name, MessageType.CHAT);
			textFields.get(0).setText("");
		} else if (actionCommand.equals("DM to Server Button")) {
			msg = new Message(textFields.get(0).getText(), "SERVER", ScoutingForm.name, MessageType.CHAT);
			textFields.get(1).setText("");
		}

		if (msg != null) {
			ClientPythonInterface.getInstance().sendStringToPython(msg.getSendableForm());
		}
	}
}

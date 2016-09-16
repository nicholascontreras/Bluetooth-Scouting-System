package org.first.team2485.scoutingform;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.first.team2485.scoutingform.formIO.FormIO;

import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;

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

			if (!FormIO.getInstance().unhandledMessages.isEmpty()) {
				JTextArea broadcastWindow = textAreas.get(0);

				broadcastWindow
						.setText(broadcastWindow.getText() + "\n" + FormIO.getInstance().unhandledMessages.remove(0));
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		String actionCommand = arg0.getActionCommand();
		
		String messageToSend = ScoutingForm.name + "*";
		
		String userInput = null;
		
		if (actionCommand.equals("Broadcasts Button")) {		
			userInput = textFields.get(0).getText();
		} else if (actionCommand.equals("DM to Server")) {
			userInput = textFields.get(1).getText();
		}
		
		if (FormIO.getInstance().containsReservedPatterns(userInput)) {
			return;
		}
		
		messageToSend += userInput + "^";
		
		FormIO.getInstance().sendStringToPython(messageToSend);
		
		if (actionCommand.equals("Broadcasts Button")) {		
			textFields.get(0).setText("");			
		} else if (actionCommand.equals("DM to Server")) {
			textFields.get(1).setText("");
		}
	}
}

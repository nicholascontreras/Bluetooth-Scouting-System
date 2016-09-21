package org.first.team2485.scoutingserver;

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

@SuppressWarnings("serial")
public class ServerChatWindows extends JPanel implements ActionListener {

	private JTabbedPane tabbedPane;

	private ArrayList<JTextArea> textAreas;
	private ArrayList<JTextField> textFields;
	private ArrayList<JButton> sendButtons;

	public ServerChatWindows() {

		this.setPreferredSize(new Dimension(600, 400));

		this.setLayout(new BorderLayout());

		tabbedPane = new JTabbedPane();

		textAreas = new ArrayList<JTextArea>();
		textFields = new ArrayList<JTextField>();

		tabbedPane.addTab("Broadcasts", createTab("Broadcasts"));
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
		sendButtons.add(sendButton);
		sendButton.addActionListener(this);
		sendButton.setActionCommand(tabName + " Button");

		JPanel bottomContainer = new JPanel(new BorderLayout());
		bottomContainer.add(enterChatWindow, BorderLayout.CENTER);
		bottomContainer.add(sendButton, BorderLayout.LINE_END);

		chatTab.add(chatWindow, BorderLayout.CENTER);
		chatTab.add(bottomContainer, BorderLayout.PAGE_END);

		return chatTab;
	}
	
	protected void addChatWindowForScout(String scoutName) {
		
	}

	private void updateChatWindows() {

		while (true) {

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			ArrayList<Message> unhandledMessages = ServerPythonInterface.getInstance().unhandledMessages;

			if (!unhandledMessages.isEmpty()) {

				Message curMessage = ServerPythonInterface.getInstance().unhandledMessages.remove(0);

				if (curMessage.getMessageType() == MessageType.CHAT) {

					SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
					String formatedTime = sdf.format(new Date(curMessage.getTimeSent()));

					String formattedMessage = curMessage.getSender() + " @ " + formatedTime + ": "
							+ curMessage.getMessage();
					
					if (curMessage.getReciever().equals("BROADCAST")) {
						JTextArea broadcastWindow = textAreas.get(0);
						broadcastWindow.setText(broadcastWindow.getText() + "\n" + formattedMessage);
					} else if (curMessage.getReciever().equals("SERVER")) {
						for (int i = 1; i < tabbedPane.getTabCount() - 1; i++) {
							
							if (tabbedPane.getTitleAt(i).equals("DM with " + curMessage.getSender())) {
								
								JTextArea dmWindow = textAreas.get(i);
								dmWindow.setText(dmWindow.getText() + "\n" + formattedMessage);
								break;
							}	
						}
					}
				} else if (curMessage.getMessageType() == MessageType.RAW_DATA) {
					if (curMessage.getMessage().equals("NEW_SCOUT")) {
						tabbedPane.insertTab("DM with " + curMessage.getSender(), null, createTab("DM with " + curMessage.getSender()), null, tabbedPane.getTabCount()- 1);
					} else if (curMessage.getMessage().equals("LOST_SCOUT")) {
						
						for (int i = 1; i < tabbedPane.getTabCount() - 1; i++) {
							
							if (tabbedPane.getTitleAt(i).equals("DM with " + curMessage.getSender())) {
								
								textFields.get(i).setEnabled(false);
								sendButtons.get(i).setText("Remove Window");
								sendButtons.get(i).setActionCommand(sendButtons.get(i).getActionCommand() + " DEAD");
							}
						}	
					}
				}
				JTextArea debugWindow = textAreas.get(textAreas.size() - 1);
				debugWindow.setText(debugWindow.getText() + "\n" + curMessage.getMessage());
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		String actionCommand = arg0.getActionCommand();

		Message msg = null;

		if (actionCommand.equals("Broadcasts Button")) {
			msg = new Message(textFields.get(0).getText(), "BROADCAST", "SERVER", MessageType.CHAT);
			textFields.get(0).setText("");
		} else if (actionCommand.startsWith("DM with ") && actionCommand.endsWith("Button")) {		
			for (int i = 1; i < textFields.size() - 1; i++) {
			
				if (actionCommand.startsWith(tabbedPane.getTitleAt(i))) {
					msg = new Message(textFields.get(i).getText(), tabbedPane.getTitleAt(i).substring("DM with ".length() + 1), "SERVER", MessageType.CHAT);
					textFields.get(i).setText("");
					break;
				}
			}	
		} else if (actionCommand.endsWith("DEAD")) {
			for (int i = 1; i < textFields.size() - 1; i++) {
				
				if (actionCommand.startsWith(tabbedPane.getTitleAt(i))) {
					textAreas.remove(i);
					textFields.remove(i);
					sendButtons.remove(i);
					
					tabbedPane.removeTabAt(i);
					
					break;
				}
			}	
		}

		if (msg != null) {
			ServerPythonInterface.getInstance().sendStringToPython(msg.getSendableForm());
		}
	}
}

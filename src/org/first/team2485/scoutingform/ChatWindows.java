package org.first.team2485.scoutingform;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatWindows extends JPanel {
	
	private JTabbedPane chatWindows;
	
	public ChatWindows() {
		
		this.setPreferredSize(new Dimension(600, 400));
		
		this.setLayout(new BorderLayout());
		
		chatWindows = new JTabbedPane();
		
		chatWindows.addTab("Broadcasts", createTab());
		chatWindows.addTab("DM to Server", createTab());
		
		this.add(chatWindows, BorderLayout.CENTER);
	}
	
	private JPanel createTab() {
		JPanel chatTab = new JPanel(new BorderLayout());
		
		JTextArea chatWindow = new JTextArea();
		chatWindow.setEditable(false);
		JTextField enterChatWindow = new JTextField();
		enterChatWindow.setEditable(true);
		JButton sendButton = new JButton("Send");
		
		JPanel bottomContainer = new JPanel(new BorderLayout());
		bottomContainer.add(enterChatWindow, BorderLayout.CENTER);
		bottomContainer.add(sendButton, BorderLayout.LINE_END);
		
		chatTab.add(chatWindow, BorderLayout.CENTER);
		chatTab.add(bottomContainer, BorderLayout.PAGE_END);
		
		return chatTab;
	}
}

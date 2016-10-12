package org.first.team2485.scoutingserver;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.JTextComponent;

import org.first.team2485.common.Message;
import org.first.team2485.common.Message.MessageType;
import org.first.team2485.scoutingform.ScoutingForm;

import com.sun.glass.ui.Timer;

public class ScoutingServer extends JFrame implements ActionListener {

	protected static ServerSettings serverSettings;

	private static final String scriptURL = "https://script.google.com/macros/s/AKfycbziuQ3X1xpEckNH8ZPezjQBHesIQRWhYQym9TMUatrcmL6rmRmo/exec?data=";

	private GamblingSystem gamblingSystem;
	
	private JButton deployUpdateButton;
	
	private JButton turnOffGambling;

	public static void main(String[] args) {
		new ScoutingServer();
	}

	private ScoutingServer() {

		submitScoutingDataToSheet(
				new Message("FooBar,67,TeamNumber,4,AutoHigh,7", "SERVER", "Fredrick", MessageType.SCOUTING_DATA));

		serverSettings = new ServerSettings();

		gamblingSystem = new GamblingSystem();

		this.setLayout(new BorderLayout());

		JPanel chatWindows = new JPanel();

		chatWindows.add(new ServerChatWindows(this));

		add(chatWindows, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel(new FlowLayout());
		
		add(buttonPanel, BorderLayout.SOUTH);
		
		deployUpdateButton = new JButton("Deploy Update");
		deployUpdateButton.addActionListener(this);
		
		turnOffGambling = new JButton("Turn off Gambling");
		turnOffGambling.setActionCommand("gamblingOff");
		
		
		
		buttonPanel.add(deployUpdateButton);
		buttonPanel.add(turnOffGambling);

		this.pack();

		this.setVisible(true);

	}

	public static String loadFormVersion(File jarLoc) throws IOException {

		byte[] data = Files.readAllBytes(jarLoc.toPath());

		System.out.println("Buffer");
		
		char[] charData = new char[data.length];
		
		for (int i = 0; i < data.length; i++) {
			charData[i] = (char) data[i];
		}
		
		System.out.println("String");
		
		String s = String.valueOf(charData);
		
		System.out.println("Update size: " + s.length() + " bytes");
		
		return s;
	}

	protected void submitScoutingDataToSheet(Message scoutingData) {

		String formData = scoutingData.getMessage(); // <-- this String is the
														// CSV from the client's
														// form

		String timestampData = "Timestamp," + scoutingData.getTimeSent();
		String scoutNameData = "Scout," + scoutingData.getSender();

		String fullData = timestampData + "," + scoutNameData + "," + formData;

		System.out.println("Sumbitting: " + fullData);

		try {
			URL submitURL = new URL(scriptURL + fullData);
			submitURL.openStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void processNewBet(Message newBetMessage) {

		String better = newBetMessage.getSender();
		String contents = newBetMessage.getMessage();
		String alliance = contents.substring(0, contents.indexOf(","));
		contents = contents.substring(contents.indexOf("," + 1));
		int predictedScore = Integer.parseInt(contents.substring(0, contents.indexOf(",")));
		contents = contents.substring(contents.indexOf("," + 1));
		int betAmount = Integer.parseInt(contents);
		gamblingSystem.placeNewBet(better, alliance, predictedScore, betAmount);

		ServerPythonInterface.getInstance().sendStringToPython(new Message(
				"ACCEPTED," + newBetMessage.getSender() + "," + alliance + "," + predictedScore + "," + betAmount,
				"BROADCAST", "SERVER", MessageType.BET_CONFIRM).getSendableForm());
	}

	class ServerSettings {

		protected String scoutingFormSaveFile;
		protected String serverPythonLoc;
		protected int secondsAfterMatchForBet;
		protected String compID;

		private ServerSettings() {
			loadServerSettings();
		}

		private void loadServerSettings() {

			try {
				FileReader fileReader = new FileReader(
						ServerPythonInterface.getInstance().containingFolder.getPath() + "/serverSettings.txt");

				BufferedReader reader = new BufferedReader(fileReader);

				scoutingFormSaveFile = reader.readLine();
				serverPythonLoc = reader.readLine();
				secondsAfterMatchForBet = Integer.parseInt(reader.readLine());
				compID = reader.readLine();

				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource().equals(deployUpdateButton)) {
			JFileChooser fileChooser = new JFileChooser();
			
			int result = fileChooser.showOpenDialog(null);
			
			if (result == JFileChooser.APPROVE_OPTION) {
				
				try {
					String data = loadFormVersion(fileChooser.getSelectedFile());
					
					ServerPythonInterface.getInstance().sendStringToPython(new Message(data, "BROADCAST", "SERVER", MessageType.FORM_UPDATE).getSendableForm());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (arg0.getActionCommand().equals("gamblingOff")) {
			gamblingSystem.turnBetsOff();
		} 
	}
	
	
}

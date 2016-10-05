package org.first.team2485.scoutingserver;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
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

import com.sun.glass.ui.Timer;

public class ScoutingServer extends JFrame {
	
	protected static ServerSettings serverSettings;
	
	private static final String scriptURL = "https://script.google.com/macros/s/AKfycbziuQ3X1xpEckNH8ZPezjQBHesIQRWhYQym9TMUatrcmL6rmRmo/exec?data=";
	
	private GamblingSystem gamblingSystem;
	
	public static void main(String[] args) {
		new ScoutingServer();
	}

	private ScoutingServer() {
		
		submitScoutingDataToSheet(new Message("FooBar,67,TeamNumber,4,AutoHigh,7", "SERVER", "Fredrick", MessageType.SCOUTING_DATA));
		
		serverSettings = new ServerSettings();
		
		gamblingSystem = new GamblingSystem();

		JPanel centerPanel = new JPanel();
		
		this.setLayout(new BorderLayout());
		
		add(centerPanel, BorderLayout.CENTER);
		
		JPanel chatWindows = new JPanel();
		
		chatWindows.add(new ServerChatWindows(this));
		
		add(chatWindows, BorderLayout.LINE_END);
		
		this.pack();
		
		this.setVisible(true);
		
	}
	

	public static String loadFormVersion(File jarLoc) throws IOException {

		FileReader fileReader = new FileReader(jarLoc/* "C:/Users/Nicholas/Desktop/More Stuff/SpamKill.jar" */);

		System.out.println("file");

		BufferedReader bufferedReader = new BufferedReader(fileReader);

		System.out.println("Buffer");

		String data = "";

		String curLine = bufferedReader.readLine();

		System.out.println("read");

		while (curLine != null) {
			data += "!@#$%^&*()" + curLine;
			curLine = bufferedReader.readLine();
		}

		bufferedReader.close();

		System.out.println("print");

		System.out.println(data);
		return data;
	}
	
	protected void submitScoutingDataToSheet(Message scoutingData) {
		
		String formData = scoutingData.getMessage(); // <-- this String is the CSV from the client's form
		
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
		
		if (gamblingSystem.canPlaceBets()) {
			
			String better = newBetMessage.getSender();
			String contents = newBetMessage.getMessage();
			String alliance = contents.substring(0, contents.indexOf(","));
			contents = contents.substring(contents.indexOf("," + 1));
			int predictedScore = Integer.parseInt(contents.substring(0, contents.indexOf(",")));
			contents = contents.substring(contents.indexOf("," + 1));
			int betAmount = Integer.parseInt(contents);
			gamblingSystem.placeNewBet(better, alliance, predictedScore, betAmount);
			
			ServerPythonInterface.getInstance().sendStringToPython(new Message("ACCEPTED," + alliance + "," + "", newBetMessage.getSender(), "SERVER", MessageType.BET_CONFIRM).getSendableForm());
		} else {
			ServerPythonInterface.getInstance().sendStringToPython(new Message("REJECTED", newBetMessage.getSender(), "SERVER", MessageType.BET_CONFIRM).getSendableForm());
		}
	}
	
	
	
	class ServerSettings {
		
		protected String scoutingFormSaveFile;
		protected String serverPythonLoc;
		protected int secondsAfterMatchForBet;
		
		private ServerSettings() {
			loadServerSettings();
		}
		
		private void loadServerSettings() {
			
			try {
				FileReader fileReader = new FileReader(ServerPythonInterface.getInstance().containingFolder.getPath() + "/serverSettings.txt");
				
				BufferedReader reader = new BufferedReader(fileReader);
				
				scoutingFormSaveFile = reader.readLine();
				serverPythonLoc = reader.readLine();
				secondsAfterMatchForBet = Integer.parseInt(reader.readLine());
				
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}	
	}
}

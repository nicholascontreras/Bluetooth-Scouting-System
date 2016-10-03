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

import com.sun.glass.ui.Timer;

public class ScoutingServer extends JFrame {
	
	protected static ServerSettings serverSettings;
	

	public static void main(String[] args) {
		new ScoutingServer();
	}

	private ScoutingServer() {
		
		serverSettings = new ServerSettings();

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
	
	protected void handleScoutingData(Message scoutingData) {
		
		String formData = scoutingData.getMessage(); // <-- this String is the CSV from the client's form
		
		// Upload to G-Sheets
	}
	
	protected void processNewBet(Message newBetMessage) {
		// Process new bet
	}
	
	class ServerSettings {
		
		protected String scoutingFormSaveFile;
		protected String serverPythonLoc;
		
		private ServerSettings() {
			loadServerSettings();
		}
		
		private void loadServerSettings() {
			
			try {
				FileReader fileReader = new FileReader(ServerPythonInterface.getInstance().containingFolder.getPath() + "/serverSettings.txt");
				
				BufferedReader reader = new BufferedReader(fileReader);
				
				scoutingFormSaveFile = reader.readLine();
				serverPythonLoc = reader.readLine();
				
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
		
	}
}

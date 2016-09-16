package org.first.team2485.scoutingform.formIO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
//import java.nio.file.Files;
//import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import org.first.team2485.scoutingform.ScoutingForm;

public class FormIO {
	
	private static final String[] RESERVED_PATTERNS = new String[] {"*", "^", "BROADCAST", "SendToServer"};

	private static FormIO instance;

	private static File containingFolder;

	private Process pythonProcess; // the process of the python script

	public ArrayList<String> messageHistory;
	public ArrayList<String> unhandledMessages;

	private BufferedReader pythonOutput;
	private BufferedWriter pythonInput;

	public static FormIO getInstance() { // only allow one instance
		if (instance == null) {
			instance = new FormIO();
			try {
				File f = new File(instance.getClass().getResource("").toURI());

				System.out.println(f);

				while (!f.getName().equals("org")) {
					f = f.getParentFile();
				}

				containingFolder = f.getParentFile().getParentFile().getParentFile();

			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return instance;
	}

	private FormIO() {
		messageHistory = new ArrayList<String>();
		unhandledMessages = new ArrayList<String>();
	}

	public Process getProcess() {
		return pythonProcess;
	}

	public void startScript() {

		if (pythonProcess != null) {
			pythonProcess.destroyForcibly();
		}

		File scriptPath = new File(containingFolder, "client.py");

		System.out.println("Script path: " + scriptPath);

		try {
			pythonProcess = cmd("py " + scriptPath.getCanonicalPath(), false);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("started cmd");

		pythonInput = new BufferedWriter(new OutputStreamWriter(pythonProcess.getOutputStream()));
		pythonOutput = new BufferedReader(new InputStreamReader(pythonProcess.getInputStream()));

		new Thread(() -> {
			try {
				readInputFromPython();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	private void readInputFromPython() throws IOException {

		System.out.println("started thread");

		while (pythonProcess.isAlive() || pythonOutput.ready()) {

			System.out.println("Alive");

			if (pythonOutput.ready()) {

				String curMessage = null;
				try {
					curMessage = pythonOutput.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (curMessage != null) {

					System.out.println("read message: " + curMessage);

					if (curMessage.equals("SEND NAME")) {
						sendStringToPython(ScoutingForm.name);
					}

					messageHistory.add(curMessage);
					unhandledMessages.add(curMessage);
				} else {
					System.out.println("Input was null");
				}
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void sendStringToPython(String s) {

		byte[] byteMessage = s.getBytes();

		try {
			pythonProcess.getOutputStream().write(byteMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Process cmd(String cmd, boolean block) {
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			if (block) {
				p.waitFor();
			}
			return p;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String saveData(String data, boolean forceWrite) { // method
																// messy,
																// fix

		File file = new File(containingFolder, "scoutingData.csv");

		if (file.exists() && !forceWrite) {
			return "Unsent data already exists";
		}

		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(file);

			fileWriter.write(data);

			fileWriter.flush();

			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
			return "Failed to write scouting data to file";
		}
		return "Successfully saved the scouting data";

	}

	public String sendData() {

		try {
			pythonInput.write("");
		} catch (IOException e) {
			e.printStackTrace();
			return "An error occurred sending the data";
		}
		
		try {
			if (pythonOutput.readLine().equals("Scouting Data Received")) {
				return "Sucessfully sent scouting data";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "An error occurred sending the data";
	}
	
	public boolean containsReservedPatterns(String s) {
		
		for (String cur : RESERVED_PATTERNS) {
			if (s.contains(cur)) {
				return true;
			}
		}
		return false;
	}

	public void saveFormVersion(String newFormVersion) throws IOException {

		String[] dataToWrite = newFormVersion.split(Pattern.quote("!@#$%^&*()"));

		FileWriter fileWriter = new FileWriter(
				new File(containingFolder, "Bluetooth Scouting Client" + System.currentTimeMillis() + ".jar"));

		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

		for (String s : dataToWrite) {
			bufferedWriter.write(s);
			bufferedWriter.newLine();
		}

		bufferedWriter.close();
	}
}

package org.first.team2485.scoutingform;

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

import org.first.team2485.common.Message;

public class ClientPythonInterface {

	private static ClientPythonInterface instance;

	private static File containingFolder;

	private Process pythonProcess; // the process of the python script

	public ArrayList<Message> messageHistory;
	public ArrayList<Message> unhandledMessages;

	private BufferedReader pythonOutput;
	private BufferedWriter pythonInput;

	public static ClientPythonInterface getInstance() { // only allow one
														// instance
		if (instance == null) {
			instance = new ClientPythonInterface();
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

	private ClientPythonInterface() {
		messageHistory = new ArrayList<Message>();
		unhandledMessages = new ArrayList<Message>();
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
			pythonProcess = cmd("python -u " + scriptPath.getCanonicalPath(), false);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("started cmd. Is alive? " + pythonProcess.isAlive());

		pythonInput = new BufferedWriter(new OutputStreamWriter(pythonProcess.getOutputStream()));

		pythonOutput = new BufferedReader(new InputStreamReader(pythonProcess.getInputStream()));

		// try {
		// pythonOutput = new BufferedReader(new FileReader(new
		// File(containingFolder, "pythonOutput.txt")));
		// } catch (FileNotFoundException e1) {
		// e1.printStackTrace();
		// }

		new Thread(() -> {
			try {
				readInputFromPython();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	private void readInputFromPython() throws IOException {

		System.out.println("started thread Alive? " + pythonProcess.isAlive());

		while (pythonProcess.isAlive() || pythonOutput.ready()) {

			System.out.println("Ready: " + pythonOutput.ready());

			if (pythonOutput.ready()) {

				System.out.println("ready, reading line...");

				String curMessage = null;
				try {
					curMessage = pythonOutput.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (curMessage != null) {

					System.out.println("read message: " + curMessage);

					if (curMessage.startsWith("MESSAGE")) {

						Message message = new Message(curMessage.substring("MESSAGE".length()));

						messageHistory.add(message);
						unhandledMessages.add(message);
					} else {
						if (curMessage.equals("SEND NAME")) {
							System.out.println("Sending name: " + ScoutingForm.name);
							sendStringToPython(ScoutingForm.name);
						}
					}
				}
			} else {
				System.out.println("Input was null");
			}
		}

		System.out.println("Python alive(pre)? " + pythonProcess.isAlive());

		pythonInput.newLine();
		pythonInput.flush();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Python alive? " + pythonProcess.isAlive());

		// BufferedReader err = new BufferedReader(new
		// InputStreamReader(pythonProcess.getErrorStream()));
		//
		// String error = err.readLine();
		//
		// while (error != null) {
		// System.out.println(error);
		// error = err.readLine();
		// }
	}

	public void sendStringToPython(String s) {
		try {
			pythonInput.write(s);
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

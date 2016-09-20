package org.first.team2485.scoutingform;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.first.team2485.common.Message;
import org.first.team2485.common.Message.MessageType;

public class ClientPythonInterface {

	private static ClientPythonInterface instance;

	private static File containingFolder;

	private Process pythonProcess; // the process of the python script

	protected ArrayList<Message> messageHistory;
	protected ArrayList<Message> unhandledMessages;

	private BufferedReader pythonOutput;
	private BufferedWriter pythonInput;

	protected static ClientPythonInterface getInstance() { // only allow one
															// instance
		if (instance == null) {
			instance = new ClientPythonInterface();
			File f = new File(instance.getClass().getResource("").getPath().substring(5));

			System.out.println(f);

			while (!f.getName().equals("org")) {
				f = f.getParentFile();
			}

			containingFolder = f.getParentFile().getParentFile();

		}
		return instance;
	}

	private ClientPythonInterface() {
		messageHistory = new ArrayList<Message>();
		unhandledMessages = new ArrayList<Message>();
	}

	protected Process getProcess() {
		return pythonProcess;
	}

	protected void startScript() {

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

		new Thread(() -> {
			try {
				readInputFromPython();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	private void readInputFromPython() throws IOException {

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		pythonInput.write(ScoutingForm.name);
		pythonInput.newLine();
		pythonInput.flush();

		while (pythonProcess.isAlive() || pythonOutput.ready()) {

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (pythonOutput.ready()) {

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

						if (message.getMessageType() == MessageType.FORM_UPDATE) {
							saveFormVersion(message.getMessage());
						}

						System.out.println("added message");
						messageHistory.add(message);
						unhandledMessages.add(message);
					} else {
						Message debugMessage = new Message(curMessage, ScoutingForm.name, "DEBUG", MessageType.CHAT);
						
						messageHistory.add(debugMessage);
						unhandledMessages.add(debugMessage);
					}
				}
			} else {
				System.out.println("Input was null");
			}

			sendStringToPython("READ_ONLY");
			pythonInput.newLine();
			pythonInput.flush();
		}
	}

	protected void sendStringToPython(String s) {
		try {
			pythonInput.write(s + "^");
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

	protected String saveData(String data, boolean forceWrite) { // method
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

	protected String sendData() {

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

	private void saveFormVersion(String newFormVersion) throws IOException {

		String[] dataToWrite = newFormVersion.split(Pattern.quote("!@#$%^&*()"));

		FileWriter fileWriter = new FileWriter(new File(containingFolder, "Bluetooth Scouting Client (UPDATE).jar"));

		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

		for (String s : dataToWrite) {
			bufferedWriter.write(s);
			bufferedWriter.newLine();
		}

		bufferedWriter.close();
	}

	protected boolean checkForUpdate() {

		for (File f : containingFolder.listFiles()) {
			if (f.getName().contains("(UPDATE)")) {
				return true;
			}
		}
		return false;
	}

	protected void switchToUpdatedVersion() {

		for (File f : containingFolder.listFiles()) {
			if (f.getName().contains("Bluetooth Scouting Client.jar")) {
				f.renameTo(new File(containingFolder,
						"Bluetooth Scouting Client (OLD:" + System.currentTimeMillis() + ").jar"));
			}
		}

		for (File f : containingFolder.listFiles()) {
			if (f.getName().contains("(UPDATE)")) {
				f.renameTo(new File(containingFolder, "Bluetooth Scouting Client.jar"));
			}
		}
		
	}
}

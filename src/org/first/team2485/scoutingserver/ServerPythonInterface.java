package org.first.team2485.scoutingserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;

import org.first.team2485.common.Message;
import org.first.team2485.scoutingform.ScoutingForm;

public class ServerPythonInterface {
	protected ArrayList<Message> messageHistory;
	protected ArrayList<Message> unhandledMessages;
	
	private static ServerPythonInterface instance;
	
	private Process pythonProcess;
	
	private BufferedWriter pythonInput;
	private BufferedReader pythonOutput;
	
	private File serverScriptPath;

	protected static ServerPythonInterface getInstance() { // only allow one instance
		if (instance == null) {
			instance = new ServerPythonInterface();
		}
		return instance;
	}
	
	private ServerPythonInterface() {
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

		System.out.println(containingFolder);

		try {
			pythonProcess = cmd("python -u " + scriptPath.getCanonicalPath(), false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		pythonInput = new BufferedWriter(new OutputStreamWriter(pythonProcess.getOutputStream()));
		pythonOutput = new BufferedReader(new InputStreamReader(pythonProcess.getInputStream()));
		
		new Thread(() -> readInputFromPython()).start();
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
	
	private void readInputFromPython() throws IOException {

		while (pythonProcess.isAlive() || pythonOutput.ready()) {
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

						messageHistory.add(message);
						unhandledMessages.add(message);
					} else {
					}
				}
			} else {
				System.out.println("Input was null");
			}
		}


		sendStringToPython("READ_ONLY");
		pythonInput.newLine();
		pythonInput.flush();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	protected void sendStringToPython(String s) {
		try {
			pythonInput.write(s + "^");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
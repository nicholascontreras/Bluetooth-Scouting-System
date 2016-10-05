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
import java.util.regex.Pattern;

import org.first.team2485.common.Message;
import org.first.team2485.common.Message.MessageType;
import org.first.team2485.scoutingform.ScoutingForm;

public class ServerPythonInterface {
	protected ArrayList<Message> messageHistory;
	protected ArrayList<Message> unhandledMessages;

	protected File containingFolder;

	private static ServerPythonInterface instance;

	private Process pythonProcess;

	private BufferedWriter pythonInput;
	private BufferedReader pythonOutput;

	protected static ServerPythonInterface getInstance() { // only allow one
															// instance
		if (instance == null) {
			instance = new ServerPythonInterface();
		}

		return instance;
	}

	private ServerPythonInterface() {
		messageHistory = new ArrayList<Message>();
		unhandledMessages = new ArrayList<Message>();

		String path = this.getClass().getResource("").getPath();

		System.out.println("Starting path: " + path);

		path = path.replaceAll(Pattern.quote("%20"), " ");
		path = path.replaceAll(Pattern.quote(".jar!"), ".jar");

		System.out.println("Finalzied Path: " + path);

		File f = new File(path);

		System.out.println("Path Object:" + f);

		while (!f.getName().equals("org")) {
			f = f.getParentFile();
		}

		containingFolder = f.getParentFile().getParentFile();
	}

	protected Process getProcess() {
		return pythonProcess;
	}

	protected void startScript() {

		if (pythonProcess != null) {
			pythonProcess.destroyForcibly();
		}

		File scriptPath = new File(ScoutingServer.serverSettings.serverPythonLoc);

		System.out.println(containingFolder);

		try {
			pythonProcess = cmd("python -u " + scriptPath.getCanonicalPath(), false);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
						if (curMessage.startsWith("NEW_SCOUT")) {
							Message newScout = new Message("NEW_SCOUT", "SERVER",
									curMessage.substring(curMessage.indexOf(":") + 1), MessageType.RAW_DATA);

							messageHistory.add(newScout);
							unhandledMessages.add(newScout);
						} else if (curMessage.startsWith("LOST_SCOUT")) {
							Message lostScout = new Message("LOST_SCOUT", "SERVER",
									curMessage.substring(curMessage.indexOf(":") + 1), MessageType.RAW_DATA);

							messageHistory.add(lostScout);
							unhandledMessages.add(lostScout);
						}
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

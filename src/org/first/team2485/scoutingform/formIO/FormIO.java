package org.first.team2485.scoutingform.formIO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class FormIO {

	private static FormIO instance;
	
	private File savePath;
	private File scriptPath;
	
	private Process pythonProcess;
	
	private static boolean isMac; // gets set in findDesktop
	
	public static FormIO getInstance() {
		if (instance == null) {
			instance = new FormIO();
		}
		return instance;
	}
	
	public Process getProcess() {
		return pythonProcess;
	}
	
	public String setup() {

		if (!setupFiles()) {
			return "Failed to setup file path";
		}

		if (!(isMac ? setupPythonMac() : setupPythonWindows())) {
			return "Failed to setup Python";
		}

		return "Setup Sucessful";
	}
	
	public void startScript() {
		
		if (pythonProcess != null) {
			pythonProcess.destroyForcibly();
		}
		
		try {
			pythonProcess = cmd("py " + scriptPath.getCanonicalPath(), false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private boolean setupFiles() {

		File desktop = findDesktop();

		System.out.println("Desktop: " + desktop);

		if (desktop == null) {
			return false;
		}

		File scoutingFolder = new File(desktop, "scoutingData");

		File newFormFolder = new File(scoutingFolder, "unsentData");

		if (!newFormFolder.exists()) {
			if (!newFormFolder.mkdirs()) {
				System.out.println("Mkdirs failed");
				return false;
			}
		}

		try {
			File pythonScript = new File(getClass().getResource("client.py").toURI());

			File newLoc = new File(scoutingFolder, "client.py");

			System.out.println("New Loc: " + newLoc);

			Files.copy(pythonScript.toPath(), newLoc.toPath(), StandardCopyOption.REPLACE_EXISTING);

			scriptPath = pythonScript;
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}

		savePath = newFormFolder;
		return true;
	}

	private boolean setupPythonWindows() {

		Process pyVersion;

		while (true) {

			pyVersion = cmd("python -V", true);

			if (pyVersion != null) {
				break;
			}

			System.out.println("No Python");

			JTextArea message = new JTextArea(
					"You do not have Python, download it here:   \nhttps://www.python.org/downloads/");
			message.setEditable(false);
			JOptionPane.showMessageDialog(null, message);
		}

		String versionResult = convertStreamToString(pyVersion.getInputStream());
		System.out.println("Python check: " + versionResult);

		String version = versionResult.substring(versionResult.indexOf(" ") + 1);
		System.out.println(version);

		int[] minVersion = { 2, 7, 1 };

		for (int i = 0; i < minVersion.length; i++) {

			System.out.println("Check: " + version.charAt(0) + " : " + minVersion[i]);

			if (Integer.parseInt(version.charAt(0) + "") < minVersion[i]) {
				JTextArea message = new JTextArea(
						"Your Python version is too outdated, update it here:   \nhttps://www.python.org/downloads/");
				message.setEditable(false);
				JOptionPane.showMessageDialog(null, message);
				return false;
			}

			if (Integer.parseInt(version.charAt(0) + "") > minVersion[i]) {
				System.out.println("Vastly ahead, breaking");
				break;
			}

			version = version.substring(version.indexOf(".") + 1);
		}

		cmd("pip install pybluez", true);

		cmd("pip install --upgrade pybluez", true);

		return true;
	}

	private boolean testPythonMac() {
		try {
			Process p = Runtime.getRuntime().exec(new String[] {"ls",  "/Library/Python/2.7/site-packages/"});
			p.waitFor();
			String s = convertStreamToString(p.getInputStream());
			return s.matches("[\\S\\s]+lightblue.+egg[\\S\\s]+");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean setupPythonMac() {
		while (!testPythonMac()) {
			String command = "cd ~/Downloads;";
			command += "git clone https://github.com/jmcculloch2018/lightblue-0.4.git;";
			command += "cd lightblue*;";
			command += "sudo python setup.py install;";
			String args[] = { "osascript",  "-e",  "tell application \"Terminal\" to do script \"" + command +  "\"" };
			try {
				Runtime.getRuntime().exec(args);
			} catch (IOException e) {
				e.printStackTrace();
			}

			JOptionPane.showMessageDialog(null, "Click on terminal, type your password where prompted, then press OK.");
		}
		return true;
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

	private static String convertStreamToString(InputStream is) {
		Scanner s = new Scanner(is).useDelimiter("\\A");
		String toReturn = s.hasNext() ? s.next() : "";
		s.close();
		return toReturn;
	}

	private static File findDesktop() {
		ArrayList<File> curFiles = new ArrayList<File>();

		curFiles.add(new File("C:/")); // Windows base dir
		isMac = false;
		if (!curFiles.get(0).exists()) {
			curFiles.clear();
			curFiles.add(new File("/Users/")); // MAC base dir
			isMac = true;
		}

		while (curFiles.size() > 0) {

			ArrayList<File> nextFiles = new ArrayList<File>();

			for (File f : curFiles) {
				File[] newFiles = f.listFiles();

				if (newFiles != null) {

					for (File cur : newFiles) {
						if (cur.canWrite() && !cur.isHidden()) {
							if (cur.getName().equals("Desktop")) {
								return cur;
							} else {
								nextFiles.add(cur);
							}
						}
					}
				}
			}
			curFiles = nextFiles;
		}
		return null;
	}

	public String saveAndSendData(String data, boolean saveFirst, boolean forceWrite) {

		if (saveFirst) {

			File file = new File(savePath, "scoutingData.csv");

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
		}

		try {
			pythonProcess.getOutputStream().write(1);
		} catch (IOException e) {
			e.printStackTrace();
			return "An error occurred sending the data";
		}
		return "Sent data sucessfully";
	}
}
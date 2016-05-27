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

	private File savePath;
	private File scriptPath;

	public String setup() {

		if (!setupFiles()) {
			return "Failed to setup file path";
		}

		if (!setupPython()) {
			return "Failed to setup Python";
		}

		return "Setup Sucessful";
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

	private boolean setupPython() {
		
		Process pyVersion;

		while (true) {

			pyVersion = cmd("python -V", true);

			if (pyVersion != null) {
				break;
			}
			
			System.out.println("No Python");
			
			JTextArea message = new JTextArea("You do not have Python, download it here:   \nhttps://www.python.org/downloads/");
			message.setEditable(false);
			JOptionPane.showMessageDialog(null, message);
		}

		String versionResult = convertStreamToString(pyVersion.getInputStream());
		System.out.println("Python check: " + versionResult);

		String version = versionResult.substring(versionResult.indexOf(" ") + 1);
		System.out.println(version);

		int[] minVersion = { 3, 5, 1 };

		for (int i = 0; i < minVersion.length; i++) {

			System.out.println("Check: " + version.charAt(0) + " : " + minVersion[i]);

			if (Integer.parseInt(version.charAt(0) + "") < minVersion[i]) {
				System.out.println("Failed, installing");
				cmd("msiexec /i python-3.5.1.msi", true);
				break;
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

	public static void main(String[] args) {

		FormIO f = new FormIO();

		System.out.println(f.setup());

		System.out.println(f.saveAndSendData("45,23,egdfsd,,"));
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

		if (!curFiles.get(0).exists()) {
			curFiles.clear();
			curFiles.add(new File("/")); // MAC base dir
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

	public String saveAndSendData(String data) {

		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(new File(savePath, "scoutingData.csv"));

			fileWriter.write(data);

			fileWriter.flush();

			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
			return "Failed to write scouting data to file";
		}

		try {
			Process p = cmd("py " + scriptPath.getCanonicalPath(), true);

			if (p.exitValue() == 1) {
				return "Unable to connect to Scouting Server";
			} else {
				return "Sucessfuly sent scouting data";
			}
		} catch (IOException e) {
			e.printStackTrace();
			return "An error occurred sending the data";
		}
	}
}

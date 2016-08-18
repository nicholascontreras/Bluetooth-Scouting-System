package org.first.team2485.scoutingform.formIO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
//import java.nio.file.Files;
//import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import org.first.team2485.scoutingform.ScoutingForm;

public class FormIO {

	private static FormIO instance;
	
	private static File containingFolder;

	private Process pythonProcess; // the process of the python script					// processing

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

	public Process getProcess() {
		return pythonProcess;
	}

	public void startScript() {

		if (pythonProcess != null) {
			pythonProcess.destroyForcibly();
		}
		
		File scriptPath = new File(containingFolder, "client.py");
		
		System.out.println(containingFolder);

		try {
			pythonProcess = cmd("py " + scriptPath.getCanonicalPath(), false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (convertStreamToString(pythonProcess.getInputStream()).equals("GETNAME")) {
			try {
				pythonProcess.getOutputStream().write(ScoutingForm.name.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
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

	private static String convertStreamToString(InputStream is) {
		Scanner s = new Scanner(is).useDelimiter("\\A");
		String toReturn = s.hasNext() ? s.next() : "";
		s.close();
		return toReturn;
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
			pythonProcess.getOutputStream().write(1); // find new key - not one
		} catch (IOException e) {
			e.printStackTrace();
			return "An error occurred sending the data";
		}
		if (convertStreamToString(pythonProcess.getInputStream()).equals("Scouting Data Received")) {
			return "Sucessfully sent scouting data";
		} else {
			return "An error occurred sending the data";
		}

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

package org.first.team2485.scoutingserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.first.team2485.scoutingform.ScoutingForm;

public class ServerIO {
	protected ArrayList<String> messages;
	private static ServerIO instance;
	
	private Process pythonProcess;

	public static ServerIO getInstance() { // only allow one instance
		if (instance == null) {
			instance = new ServerIO();
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

}

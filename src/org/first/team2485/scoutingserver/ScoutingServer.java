package org.first.team2485.scoutingserver;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class ScoutingServer {

	public static void main(String[] args) {
		new ScoutingServer();
	}

	private ScoutingServer() {
		
		JFileChooser scriptFinder = new JFileChooser();
		
		scriptFinder.showOpenDialog(null);
		
		File file = scriptFinder.getSelectedFile();

		Process pythonServer = null;
		try {
			pythonServer = cmd("py " + file.getCanonicalPath(), false);
		} catch (IOException e) {
			e.printStackTrace();
		}

		byte[] ignoreTag = "IGNORE".getBytes();

		while (pythonServer.isAlive()) {
			
			try {
				if (pythonServer.getInputStream().read() != -1) {
					pythonServer.getOutputStream().write(ignoreTag);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		JOptionPane.showMessageDialog(null, "Python script stopped");
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
}

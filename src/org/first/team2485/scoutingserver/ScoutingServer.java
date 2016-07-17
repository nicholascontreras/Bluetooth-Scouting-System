package org.first.team2485.scoutingserver;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
		
		
		try {
			System.out.println(pythonServer.isAlive());
			Thread.sleep(20);
			System.out.println(pythonServer.isAlive());
			Thread.sleep(20);
			System.out.println(pythonServer.isAlive());
			Thread.sleep(20);
			System.out.println(pythonServer.isAlive());
			Thread.sleep(20);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		while (pythonServer.isAlive()) {
			
			try {
				if (pythonServer.getInputStream().read() != -1 || pythonServer.getErrorStream().read() != -1) {
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
	
	public static String loadFormVersion(File jarLoc) throws IOException {
		
		FileReader fileReader = new FileReader(jarLoc/*"C:/Users/Nicholas/Desktop/More Stuff/SpamKill.jar"*/);
		
		System.out.println("file");
		
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		
		System.out.println("Buffer");
		
		String data = "";
		
		String curLine = bufferedReader.readLine();
		
		System.out.println("read");
		
		while (curLine != null) {
			data += "!@#$%^&*()" + curLine;
			curLine = bufferedReader.readLine();
		}
		
		bufferedReader.close();
		
		System.out.println("print");
		
		System.out.println(data);
		return data;
	}
}

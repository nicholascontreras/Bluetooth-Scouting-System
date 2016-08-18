package org.first.team2485.scoutingserver;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import javafx.scene.input.KeyCode;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Date;

public class DMTab extends JPanel{
	
	private JTextArea output;
	private JTextArea input;
	private Process script;
	
	private String clientName;
	
	public DMTab (String clientName, Process pythonScript) {
		
		output = new JTextArea();
		output.setEditable(false);
		
		input = new JTextArea();
		input.setEditable(true);
		
		this.script = pythonScript;
		
		input.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					String inputText = input.getText();
					
					
					try {
						script.getOutputStream().write(inputText.getBytes());
						output.append("\n[Me]: " + inputText + "(" + new Date().toString() + ")");
						input.setText("");
						
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						output.append("\nAn error occurred. Try again later.");
					}
				}
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		this.add(new JScrollPane(output), BorderLayout.NORTH);
		
		this.add(new JScrollPane(input), BorderLayout.SOUTH);
		
		this.clientName = clientName;
		
	} 
	
	public void printText(String text) {
		String toPrint = "\n[" + clientName + "]: " + text + "(" + new Date().toString() + ")";
		
		output.append(toPrint);
	}
	
}

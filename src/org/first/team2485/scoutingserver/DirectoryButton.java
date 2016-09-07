package org.first.team2485.scoutingserver;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class DirectoryButton extends JButton{
	
	private File file = null;
	
	private DirectoryButton myself;
	
	public DirectoryButton (JFrame parent) {
		super();
		
		this.setText("No File Selected");
		
		this.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				JFileChooser chooser = new JFileChooser();
				
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				
				chooser.showOpenDialog(parent);
				
				file = chooser.getSelectedFile();
				
				System.out.println(file.getName());
				
				JButton button = (JButton) e.getSource();
				
				button.setText(file.getName());
				
				
			}
		});
	}
	
	public File getSelectedFile() {
		return file;
	}
	
}

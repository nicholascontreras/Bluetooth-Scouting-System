package org.first.team2485.scoutingserver;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;

public class UploadButton extends JButton {

	String scriptURL = "https://script.google.com/macros/s/AKfycbxOPJrgTvxLylo6DISWV5yGS4bnowYX-pKyUkle7fkm5Ktacfqs/exec";

	ArrayList<String> filesDone = new ArrayList<String>();

	public UploadButton(DirectoryButton dataDirButton) {
		super("Upload");
		
		File file = dataDirButton.getSelectedFile();

		this.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				File[] files = file.listFiles();
				
				ArrayList<String> filesProcessed = new ArrayList<String>();

				ArrayList<String> headers = new ArrayList<String>();
				ArrayList<String> params = new ArrayList<String>();
				
				int counter = 1;

				for (int i = 0; i < files.length; i++) {

					if (filesDone.contains(files[i].getName())) {
						continue;
					}

					String line = "";

					BufferedReader br;

					try {

						System.out.println("Loading File " + files[i].getName());

						br = new BufferedReader(new FileReader(files[i]));

						// Since one line, no need to worry about reading
						// multiple lines
						line = br.readLine();

						String[] data = line.split(",");

						for (int j = 0; j < data.length; j += 2) {
							headers.add(data[j] + counter);
							params.add(data[j + 1]);
						}
						
						System.out.println("File parsed successfully");
						
						counter++;
						
						br.close();
						
						

					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
				
				headers.add("size");
				params.add(counter + "");
				

				String[] headersArray = new String[headers.size()];
				String[] paramsArray = new String[params.size()];

				headers.toArray(headersArray);
				params.toArray(paramsArray);

				System.out.println("Sending data...");

				try {
					HTTPUtils.sendPost(scriptURL, headersArray, paramsArray);
					
					System.out.println("Sent successfully\n");
					
					filesDone.addAll(filesProcessed);

				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				
			}
			
			
		});
	}

}

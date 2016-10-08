package org.first.team2485.scoutingserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class BlueAllianceSystem {

	private ScoutingServer scoutingServer;
	
	private HashMap<String, Match> matches;

	protected BlueAllianceSystem(ScoutingServer scoutingServer) {
		this.scoutingServer = scoutingServer;
		matches = new HashMap<String, Match>();
	}

	protected static void getBlueAllianceData() {
		try {

			URLConnection connection = new URL("https://www.thebluealliance.com/api/v2/event/2016cabb/matches").openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			connection.setRequestProperty("X-TBA-App-Id", "frc2485:scouting-system:1");
			connection.connect();

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			String cur = bufferedReader.readLine();

			while (cur != null) {
				System.out.println(cur);
				cur = bufferedReader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		getBlueAllianceData();
	}
	
	private class Match {
		
		private String type;
		int matchNumber;
		int[] blueTeams;
		int blueScore;
		int[] redTeams;
		int redScore;
		
		private Match(String type, int matchNumber, int[] blueTeams, int blueScore, int[] redTeams, int redScore) {
			
		}
		
		
	}
}

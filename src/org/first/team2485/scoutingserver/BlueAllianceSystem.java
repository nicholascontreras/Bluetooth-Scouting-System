package org.first.team2485.scoutingserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

public class BlueAllianceSystem {

	private ScoutingServer scoutingServer;
	
	private ArrayList<Match> matches;

	protected BlueAllianceSystem(ScoutingServer scoutingServer) {
		this.scoutingServer = scoutingServer;
		matches = new ArrayList<Match>();
		
		updateMatchData(getBlueAllianceData());
	}

	protected static String getBlueAllianceData() {
		try {

			URLConnection connection = new URL("https://www.thebluealliance.com/api/v2/event/2016cabb/matches").openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			connection.setRequestProperty("X-TBA-App-Id", "frc2485:scouting-system:1");
			connection.connect();

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			String data = bufferedReader.readLine();
			
			return data;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void updateMatchData(String rawData) {
		
		String compID = "2016cabb";
		
		rawData = rawData.substring(1, rawData.length() - 1);
		
		while (true) {
			
			int index = rawData.indexOf(", \"event_key\": \"2016cabb\"},");
			
			if (index == -1) {
				break;
			}
			
			String curMatchString = rawData.substring(0, index + ", \"event_key\": \"2016cabb\"},".length());
			
			System.out.println(curMatchString);
			
			String matchType = getValueFromString(curMatchString, "comp_level", true);
			
			int matchNumber = Integer.parseInt(getValueFromString(curMatchString, "match_number", false));
			
			String rawBlueAllianceString = getValueFromString(curMatchString, "blue", false);
			rawBlueAllianceString = rawBlueAllianceString.substring(1, rawBlueAllianceString.length() - 1);
			int blueScore = Integer.parseInt(getValueFromString(rawBlueAllianceString, "score", false));
			String blueTeamsAsString = getValueFromString(rawBlueAllianceString, "teams", false);
			blueTeamsAsString = blueTeamsAsString.substring(1, blueTeamsAsString.length() - 1);
			blueTeamsAsString = blueTeamsAsString.replace("\"", "");
			blueTeamsAsString = blueTeamsAsString.re
		}
		
	}
	
	private static String getValueFromString(String data, String value, boolean hasQuotes) {
		
		int index = data.indexOf(value) + value.length() + 3;
		
		if (hasQuotes) {
			index++;
		}
		
		String part = data.substring(index);
		
		int endIndex = 0;
		int bracketsDeep = 0;
		
		for (int i = 0; i < part.length(); i++) {
			if (part.charAt(i) == ',' && bracketsDeep == 0) {
				endIndex = i;
				break;
			}
			
			if (part.charAt(i) == '{' || part.charAt(i) == '[') {
				bracketsDeep++;
			}
			if (part.charAt(i) == '}' || part.charAt(i) == ']') {
				bracketsDeep--;
			}
		}
		
		if (hasQuotes) {
			endIndex--;
		}
		
		return part.substring(0, endIndex);
	}
	
	public static void main(String[] args) {
		new BlueAllianceSystem(null);
	}
	
	private class Match {
		
		private String type;
		private int matchNumber;
		private int[] blueTeams;
		private int blueScore;
		private int[] redTeams;
		private int redScore;
		
		private Match(String type, int matchNumber, int[] blueTeams, int blueScore, int[] redTeams, int redScore) {
			
		}	
	}
}

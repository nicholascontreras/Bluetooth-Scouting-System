package org.first.team2485.scoutingserver;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.first.team2485.common.Message;
import org.first.team2485.common.Message.MessageType;

public class GamblingSystem {
	
	private HashSet<GamblingScout> gamblingScouts;
	
	private HashSet<PlacedBet> placedBets;
	
	protected GamblingSystem() {
	}
	
	protected void addScout(String name) {
		gamblingScouts.add(new GamblingScout(name, 100));
	}
	
	protected void removeScout(String name) {
		
		GamblingScout toRemove = null;
		
		for (GamblingScout cur : gamblingScouts) {
			if (cur.name.equals(name)) {
				toRemove = cur;
			}
		}
		
		if (toRemove != null) {
			gamblingScouts.remove(toRemove);
		}
	}
	
	protected void placeNewBet(String placer, String alliance, int predictedScore, int betAmount) {
		
		
		
	}
	
	protected Message enterMatchScoreAndPayout(int redScore, int blueScore) { //TODO: Troy do bet math

		ServerPythonInterface.getInstance().sendStringToPython(new Message("OPEN", "BROADCAST", "SERVER", MessageType.GAMBLING_STATUS).getSendableForm());
		
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				ServerPythonInterface.getInstance().sendStringToPython(new Message("CLOSE", "BROADCAST", "SERVER", MessageType.GAMBLING_STATUS).getSendableForm());
			}
		}, ScoutingServer.serverSettings.secondsAfterMatchForBet * 1000);
		
		return null;
	}
	
	private class GamblingScout {
		
		private String name;
		private int money;
		
		private GamblingScout(String name, int money) {
			
			this.name = name;
			this.money = money;
			
		}	
	}
	
	private class PlacedBet {
		
		
		
	}
}

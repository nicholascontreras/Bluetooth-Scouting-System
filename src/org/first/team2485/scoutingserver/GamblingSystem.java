package org.first.team2485.scoutingserver;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.first.team2485.common.Message;

public class GamblingSystem {
	
	private HashSet<PlacedBet> placedBets;
	
	private boolean canPlaceBets;
	
	protected GamblingSystem() {
		canPlaceBets = true;
	}
	
	protected void placeNewBet(String placer, String alliance, int predictedScore, int betAmount) {
		
		
		
	}
	
	protected boolean canPlaceBets() {
		return canPlaceBets;
	}
	
	protected Set<Message> enterMatchScoreAndPayout(int redScore, int blueScore) { //TODO: Troy do bet math
		
		HashSet<Message> payouts = new HashSet<>();
		
		for (PlacedBet curBet : placedBets) {
			
		}

		canPlaceBets = true;
		
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				canPlaceBets = false;
			}
		}, ScoutingServer.serverSettings.secondsAfterMatchForBet * 1000);
		
		return payouts;
	}
	
	private class PlacedBet {
		
		
		
	}
}

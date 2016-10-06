package org.first.team2485.scoutingserver;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.first.team2485.common.Message;

public class GamblingSystem {
	
	private HashSet<GamblingScout> gamblingScouts;
	
	private HashSet<PlacedBet> placedBets;
	
	private boolean canPlaceBets;
	
	protected GamblingSystem() {
		canPlaceBets = true;
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
	
	protected boolean canPlaceBets() {
		return canPlaceBets;
	}
	
	protected Message enterMatchScoreAndPayout(int redScore, int blueScore) { //TODO: Troy do bet math

		canPlaceBets = true;
		
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				canPlaceBets = false;
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

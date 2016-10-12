package org.first.team2485.scoutingserver;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.first.team2485.common.Message;

public class GamblingSystem {
	
	private static final int ERROR_OFFSET = 15;

	private HashSet<GamblingScout> gamblingScouts;
	
	private HashSet<PlacedBet> placedBets;
	
	private boolean canPlaceBets;
	
	private int pool;
	
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
		
		//TODO: don't place bet that is more than the money they have
		
	}
	
	protected boolean canPlaceBets() {
		return canPlaceBets;
	}
	
	protected Message enterMatchScoreAndPayout(int redScore, int blueScore) { //TODO: Troy do bet math

		canPlaceBets = true;
		
		//Calculate pool
		for(PlacedBet bet : placedBets) {
			pool += bet.value;
		}
		
		//If tie, no money for nobody; add money to next round's pool
		if (redScore == blueScore) {
			
			placedBets.clear();
			
			return null; //TODO: ask about message format
		
		}
		Iterator<PlacedBet> i = placedBets.iterator();
		
		while (i.hasNext()) {
			PlacedBet bet = i.next();
			//If they lose take away money and delete their bet
			if (!(bet.winner == "Blue" && blueScore > redScore )  || !(bet.winner == "Red" && blueScore < redScore ) ) {
				
				for (GamblingScout scout : gamblingScouts) {
					if (scout.name == bet.name) {
						scout.money -= bet.value;
						i.remove();
					}
				}
				
			}
		}
		
		//Assumption: all next are winners
		
		int[] error = new int[placedBets.size()];
		
		//find "error" for each person, keep track of sum
		int total = 0;
		
		int counter = 0;
		
		for (PlacedBet bet : placedBets) {
			error[counter] = Math.abs(bet.points - Math.max(blueScore, redScore)) + ERROR_OFFSET;
			counter++;
		}
		
		
		
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
		
		private String name;
		private String winner;
		private int points;
		private int value;
		
		private PlacedBet(String name, String winner, int points, int value) {
			this.name = name;
			this.winner = winner;
			this.points = points;
			this.value = value;
		}
		
		
	}
}

package com.tribaltech.android.entities;

import java.util.ArrayList;

public class GameHistoryDetails {

	public ArrayList<String> squareScore;

	public ArrayList<String> frameScore;
	
	public ArrayList<String> standingPins;

	public String challengeName;
	public String pointsWon;
	public String Names;
	public Boolean isCompleted;
	public Boolean isPosted;
	public Boolean enteredLive;
	public String livePoints;
	public String bowlingGameId;
	public String scoringType;
	

	public GameHistoryDetails() {
	}

	public GameHistoryDetails(ArrayList<String> standingPins, ArrayList<String> squareScore,
                              ArrayList<String> frameScore, String Names, String challengeName,
                              String pointsWon, Boolean isCompleted, Boolean isPosted,
                              Boolean enteredLive, String livePoints, String bowlingGameId, String scoringType) {
		this.squareScore = squareScore;
		this.frameScore = frameScore;
        this.standingPins=standingPins;
		this.Names = Names;
		this.challengeName = challengeName;
		this.pointsWon = pointsWon;
		this.isCompleted = isCompleted;
		this.isPosted = isPosted;

		this.enteredLive = enteredLive;
		this.livePoints = livePoints;
		this.bowlingGameId = bowlingGameId;
		this.scoringType=scoringType;
	}

	public void setIsPosted(Boolean bool) {
		this.isPosted = bool;
	}

}
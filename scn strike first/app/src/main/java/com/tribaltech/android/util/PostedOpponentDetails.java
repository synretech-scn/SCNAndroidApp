package com.tribaltech.android.util;

import java.io.Serializable;

public class PostedOpponentDetails implements Serializable{

	public String screenName;

	public String bowlingGameId;

	public String competitionId;

	public String userAverage;

	public String userHandicap;

	public String userRegion;

	public String opponentScore;

	public String opponentHandicapScore;

	public PostedOpponentDetails(String screenName, String bowlingGameId,
                                 String competitionId, String userAverage, String userHandicap,
                                 String userRegion, String opponentScore,
                                 String opponentHandicapScore) {
		super();
		this.screenName = screenName;
		this.bowlingGameId = bowlingGameId;
		this.competitionId = competitionId;
		this.userAverage = userAverage;
		this.userHandicap = userHandicap;
		this.userRegion = userRegion;
		this.opponentScore = opponentScore;
		this.opponentHandicapScore = opponentHandicapScore;
	}

}

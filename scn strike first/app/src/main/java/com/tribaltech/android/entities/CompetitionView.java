package com.tribaltech.android.entities;

import com.tribaltech.android.util.CommonUtil;

public class CompetitionView {

	public String creatorUserName;

	public String creatorRegion;

	public int creatorAverage;

	public int creatorHandicap;

	public String competitionName;

	public String id;

	public String expirationTime;

	public int creditWager;

	public String maxRewards;

	// For Posted
	public CompetitionView(String creatorUserName, String creatorRegion,
                           int creatorAverage, int creatorHandicap, String competitionName,
                           String id, String expirationTime) {
		super();
		this.creatorUserName = creatorUserName;
		this.creatorRegion = creatorRegion;
		this.creatorAverage = creatorAverage;
		this.creatorHandicap = creatorHandicap;
		this.competitionName = competitionName;
		this.id = id;
		this.expirationTime = CommonUtil
				.getExpirationTimeTodisplay(expirationTime);
	}

	// For Live
	public CompetitionView(String creatorUserName, String creatorRegion,
                           int creatorAverage, String competitionName, String id,
                           int creditWager, String maxRewards) {
		super();
		this.creatorUserName = creatorUserName;
		this.creatorRegion = creatorRegion;
		this.creatorAverage = creatorAverage;
		this.competitionName = competitionName;
		this.id = id;
		this.creditWager = creditWager;
		this.maxRewards = maxRewards;
	}
}

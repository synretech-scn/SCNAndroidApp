package com.tribaltech.android.entities;

public class Xbowler {

	public String screenName;

	public String averageScore;

	public String regionShortName;

	public String regionLongName;

	public String countryDisplayName;

	public String countryCode;

	public String friendId;

	public String userId;

	public String isFriend;

	public Xbowler(String screenName, String averageScore,
                   String regionShortName, String regionLongName,
                   String countryDisplayName, String countryCode, String friendId,
                   String userId, String isFriend) {
		this.screenName = screenName;
		this.averageScore = averageScore;
		this.regionShortName = regionShortName;

		this.regionLongName = regionLongName;
		this.countryDisplayName = countryDisplayName;
		this.countryCode = countryCode;

		this.friendId = friendId;
		this.userId = userId;
		this.isFriend = isFriend;
	}

}
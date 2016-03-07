package com.tribaltech.android.entities;

/**
 * Created by cl-99 on 1/12/2015.
 */
public class LeaderboardFilterItem {

    public String leaderboardType = "alltimescore";

    public String leaderboardTypeName = "All Time Score";

    public String pointsText = "Score";

    public String countryId = "0";

    public String stateId = "0";

    public String venueId = "0";

    public String countryName = "All Countries";

    public String stateName = "All States";

    public String venueName = "All Centres";

    public boolean allBowlers = true;

    public LeaderboardFilterItem() {
    }

    public LeaderboardFilterItem(String venueId, String venueName) {
        this.venueId = venueId;
        this.venueName = venueName;
    }

    public LeaderboardFilterItem(String leaderboardType, String leaderboardTypeName, String pointsText, String venueId, String venueName) {
        this.leaderboardType = leaderboardType;
        this.leaderboardTypeName = leaderboardTypeName;
        this.pointsText = pointsText;
        this.venueId = venueId;
        this.venueName = venueName;
    }

    public LeaderboardFilterItem(String leaderboardType, String leaderboardTypeName, String pointsText,
                                 boolean allBowlers, String countryId, String stateId, String venueId,
                                 String country, String state, String venue) {
        this.leaderboardTypeName = leaderboardTypeName;
        this.leaderboardType = leaderboardType;
        this.pointsText = pointsText;
        this.allBowlers = allBowlers;
        this.countryId = countryId;
        this.stateId = stateId;
        this.venueId = venueId;
        this.countryName = country;
        this.stateName = state;
        this.venueName = venue;
    }
}

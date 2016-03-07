package com.tribaltech.android.entities;

import java.io.Serializable;

public class Game implements Serializable{

    public String userName="";

    public String checkoutId="";

    public String centerType="";

    public String gameId="";

    public String laneNumber="";

    public String centerName="";

    public String liveGameId="";

    public String screenName="";

    public int venueId;

    public String patternName="";

    public String patternLength="";

    public String compType="";

    public Game(String checkoutId, String centerType, String gameId, String liveGameId) {
        this.checkoutId = checkoutId;
        this.centerType = centerType;
        this.gameId = gameId;
        this.liveGameId = liveGameId;
    }

    public Game(String userName, String checkoutId, String centerType,
                String gameId, String laneNumber, String centerName,
                String liveGameId, String screenName, int venueId,
                String patternName, String patternLength, String compType) {
        super();
        this.userName = userName;
        this.checkoutId = checkoutId;
        this.centerType = centerType;
        this.gameId = gameId;
        this.laneNumber = laneNumber;
        this.centerName = centerName;
        this.liveGameId = liveGameId;
        this.screenName = screenName;
        this.venueId = venueId;
        this.patternName = patternName;
        this.patternLength = patternLength;
        this.compType = compType;
    }


}

package com.tribaltech.android.util;

public class liveLanes {

    public String laneNumber;

    public String numberOfPlayers;

    public String venueId;
    public String Names;

    public liveLanes() {
    }

    public liveLanes(String laneNumber, String numberOfPlayers, String venueId,
                     String Names) {
        this.laneNumber = laneNumber;
        this.numberOfPlayers = numberOfPlayers;
        this.venueId = venueId;
        this.Names = Names;
    }

}
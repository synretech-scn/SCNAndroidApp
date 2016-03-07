package com.tribaltech.android.util;

import java.util.ArrayList;

public class liveLaneDetails {

    public ArrayList<String> squareScore;

    public ArrayList<String> frameScore;
    public ArrayList<String> standingPins;

    public String Names;

    public liveLaneDetails() {
    }

    public liveLaneDetails(
            ArrayList<String> standingPins,

            ArrayList<String> squareScore,
            ArrayList<String> frameScore, String Names) {
        this.squareScore = squareScore;
        this.frameScore = frameScore;
        this.standingPins = standingPins;
        this.Names = Names;
    }

}
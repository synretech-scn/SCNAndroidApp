package com.tribaltech.android.util;

/**
 * Created by cl-99 on 1/8/2015.
 */
public class CoachViewItem {

    public String playerName;

    public String score;

    public String[] squareScores = new String[22];

    public int[] pinFall = new int[22];

    public String[] frameScore = new String[11];

    public String rowKey;

    public CoachViewItem(String playerName, String score, String[] squareScores, int[] pinFall, String rowKey) {
        this.playerName = playerName;
        this.score = score;
        this.squareScores = squareScores;
        this.pinFall = pinFall;
        this.rowKey = rowKey;
    }

    public CoachViewItem(String playerName, String rowKey) {
        this.playerName = playerName;
        this.rowKey = rowKey;
    }
}

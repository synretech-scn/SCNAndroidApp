package com.tribaltech.android.util;

public class LiveScorePlayerData {

    public String playerName;

    public int frameCount;

    public String scores;

    public String rowKey;

    public LiveScorePlayerData(String playerName, int frameCount,
                               String scores, String rowKey) {
        super();
        this.playerName = playerName;
        this.frameCount = frameCount;
        this.scores = scores;
        this.rowKey = rowKey;
    }


}

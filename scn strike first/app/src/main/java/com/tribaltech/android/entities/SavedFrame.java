package com.tribaltech.android.entities;

/**
 * Created by cl-99 on 2/7/2015.
 */
public class SavedFrame {

    public int frameNumber;

    public int pinFall[];

    public String squareScores[];

    public SavedFrame(int frameNumber,int[] pinFall,String[] squareScores){
        this.frameNumber = frameNumber;
        this.pinFall = pinFall;
        this.squareScores = squareScores;
    }
}

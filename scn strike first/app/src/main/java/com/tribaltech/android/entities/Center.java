package com.tribaltech.android.entities;

import java.io.Serializable;

public class Center implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -3211251663488901008L;

    public int id;

    public String name = "";

    public String scoringType = "";

    public String contact="";

    public int laneCount;

    public Center() {
    }

    public Center(String name) {
        this.name = name;
    }

    public Center(String name, int id, String scoringType) {
        this.name = name;
        this.id = id;
        this.scoringType = scoringType;
    }

    public Center(String name, int id, String scoringType, String contact, int laneCount) {
        this.name = name;
        this.id = id;
        this.scoringType = scoringType;
        this.contact = contact;
        this.laneCount = laneCount;
    }


    @Override
    public String toString() {
        return name;
    }
}
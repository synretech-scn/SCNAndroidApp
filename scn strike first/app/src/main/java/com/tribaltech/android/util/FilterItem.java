package com.tribaltech.android.util;

import java.io.Serializable;

public class FilterItem implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 6618091661494209525L;

    public String timeDuration = "";

    public String locationId = "0";

    public String oilPatternId = "0";

    public String gameTypeId = "0";

    public String patternLengthId = "0";

    public String country = "";

    public String state = "";

    public String center = "";

    public String tag = "";

    public FilterItem() {
    }

    public FilterItem(String timeDuration, String locationId,
                      String oilPatternId, String gameTypeId, String patternLengthId) {
        super();
        this.timeDuration = timeDuration;
        this.locationId = locationId;
        this.oilPatternId = oilPatternId;
        this.gameTypeId = gameTypeId;
        this.patternLengthId = patternLengthId;
    }

    public FilterItem(String timeDuration, String locationId,
                      String oilPatternId, String gameTypeId, String patternLengthId,
                      String country, String state, String center, String tag) {
        super();
        this.timeDuration = timeDuration;
        this.locationId = locationId;
        this.oilPatternId = oilPatternId;
        this.gameTypeId = gameTypeId;
        this.patternLengthId = patternLengthId;
        this.country = country;
        this.state = state;
        this.center = center;
        this.tag = tag;
    }

}

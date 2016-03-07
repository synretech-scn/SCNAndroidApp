package com.tribaltech.android.util;

import java.io.Serializable;

public class FilterData implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -291914075015234068L;

    int locationId;

    int oilPatternId;

    int gameType;

    String timeDuration;

    public FilterData(int locationId, int oilPatternId, int gameType,
                      String timeDuration) {
        super();
        this.locationId = locationId;
        this.oilPatternId = oilPatternId;
        this.gameType = gameType;
        this.timeDuration = timeDuration;
    }

}

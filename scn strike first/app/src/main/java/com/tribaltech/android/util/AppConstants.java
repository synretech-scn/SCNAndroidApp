package com.tribaltech.android.util;

import android.graphics.Color;

public interface AppConstants {

    int SCREEN_WIDTH = 720;
    int SCREEN_HEIGHT = 1134;
    public static final int H2H_LIVE = 1;
    public static final int H2H_POSTED = 2;


    int MIN_PASSWORD_LENGTH = 6;
    //Frame States
    int BOWLED = 1;
    int SELECTED = 2;
    int PENDING = 0;

    //Frame Text Colors
    int SELECTED_TEXT_COLOR = Color.WHITE;
    int PENDING_TEXT_COLOR = Color.WHITE;
    int BOWLED_TEXT_COLOR = Color.BLACK;

    String SHARED_PREF_NAME = "demo_twitter_oauth";
    String SHARED_PREF_KEY_SECRET = "demo_oauth_token_secret";
    String SHARED_PREF_KEY_TOKEN = "demo_oauth_token";

    String TWITTER_CALLBACK_URL = "http://www.google.com/";

    String IEXTRA_AUTH_URL = "auth_url";
    String IEXTRA_OAUTH_VERIFIER = "oauth_verifier";
    String IEXTRA_OAUTH_TOKEN = "oauth_token";

    //Graphs
    int AVERAGE_SCORE = 0;
    int HIGH_SCORE = 1;
    int STRIKE_SPARE = 2;
    int SCORE_DISTRIBUTION = 3;
    int MULTIPIN_SPARE = 4;
    int SPLIT_SPARE = 5;
    int OIL_PATTERN = 6;
    int STRIKE_SPARE_FILTER = 7;
    int SINGLE_PIN = 8;
    int AVERAGE_BALL_TYPE = 9;

}

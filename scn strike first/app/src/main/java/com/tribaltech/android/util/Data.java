package com.tribaltech.android.util;

import android.content.Context;

import com.tribaltech.android.entities.Game;
import com.tribaltech.android.entities.LeaderboardFilterItem;

import java.text.NumberFormat;

public class Data {
    // public static boolean CMAGame = false;
    // http://2t8c-hydh.accessdomain.com:3000/user/
    // public static String baseUrl = "http://67.202.34.113:2000/user/";
    // public static String baseUrl = "http://192.168.1.127:107/";
    // public static String baseUrl = "http://api.staging.xbowling.com/";
    public static String baseUrl = "http://api.xbowling.com/";
    // public static String apiKey = "9E54C359216B4A51B34A7E83068E210C";
    public static String apiKey = "158478DC73FA498DB5D29BF13E9033F5";
    //    public static String AccessToken = "MDEwMDE25A7FeKyyiru5VcYUiq3K/3xntGZRGN1OVDuFg7z5U40gmf4kfnVY54B1kxc%2BuiIonqyFPdgwUxNCq568VHlvOq3XOEIViZp85c6NRj62CtatQ9iBNoaiFoxNI5MXBpqNblkSy/72o27ThqlZpad%2B0g==";
    public static String AccessToken = "";
    public static String fbID = "669711796407982";
    public static String userName = "";
    public static String email = "";
    public static String firstName = "";
    public static String lastName = "";
    // public static String cmaTeamId = "";
    // public static Bitmap userBitmap;
    public static int credits;
    public static String userImageUrl = "http://api.staging.xbowling.com/";
    public static String CumulativeScoreEventGuid = "40552BE9-270A-4C3F-BF34-B6467E2D0D8A";
    public static String regid = "";
    // public static int CMARemainingGames = 0;
    public static int twitterExceptionCode = 0;
    // public static String CMATEAM = "Team";
    public static int CMAgamesPurchased = 0;
    public static NumberFormat numberFormat = NumberFormat.getInstance();
    public static String bannerAd = "";
    public static String bannerAdUrl = "";
    public static boolean fromCashContest;

    public static String ballId = "";
    public static String patternNameId = "";
    public static String patternLengthId = "";
    public static String compTypeId = "";

    public static boolean userStatsSubscribed = false;

    public static boolean trialPurchased;

    public static boolean statsStatusChecked;

    public static FilterItem filter;

    public static Context currentContext;

    public static LeaderboardFilterItem leaderboardFilter;

    public static boolean postedEntered;

    public static int lastChallengeVisited;

    public static Game gameData;

    public static PostedOpponentDetails lastPostedOpp;

    public static int centerID;

    public static String center;

    public static String country = "";

    public static int notificationCount;

    public static String fbAccessToken = "";

    public static String userPoints = "";
}

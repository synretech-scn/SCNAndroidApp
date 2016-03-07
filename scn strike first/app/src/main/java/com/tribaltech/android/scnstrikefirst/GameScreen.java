package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tribaltech.android.entities.Game;
import com.tribaltech.android.entities.SavedFrame;
import com.tribaltech.android.util.AppConstants;
import com.tribaltech.android.util.AppStatus;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;
import com.tribaltech.android.util.DbController;
import com.tribaltech.android.util.HttpDeleteWithBody;
import com.tribaltech.android.util.StatsAdapter;
import com.tribaltech.android.util.UserStatsAdapter;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import rmn.androidscreenlibrary.ASSL;

public class GameScreen extends MenuIntent {

    private static final int TAG_REQUEST = 110;

    int[] frameStates = new int[11];
    Button[] pins = new Button[11];
    int currentFrame = 1;
    int maxFrameBowled;
    int ballNumber;

    Map<Integer, Integer> pinFallMap = new ConcurrentHashMap<Integer, Integer>();
    Map<Integer, Integer> pinFallPreviousState = new ConcurrentHashMap<Integer, Integer>();
    Map<Integer, Integer> pocketBrookMap = new HashMap<Integer, Integer>();
    static Map<Integer, String> ballNameMap;

    private LayoutInflater inflater;
    View frames;
    private ViewGroup tenFrameParent;
    String laneCheckoutId;
    String scoringType;
    String bowlingGameId;
    int venueId;
    String centerName;
    private String laneNumber;
    public static String liveCompetitionId = "";
    protected int latestFrameNumber;
    protected boolean gameCompleted;
    protected int lastSquare;
    protected int maxSquareBowled;
    private Handler handler;

    private TextView throw1;
    private TextView throw2;
    private TextView throw3;
    Button throwLabel;
    Button previousThrow;
    Button nextThrow;
    Button previousFrame;
    Button nextFrame;
    Button done;
    Button strike;
    Button spare;
    private String bowlerName;
    private Runnable runnable;
    private TextView center;
    private TextView totalScore;
    boolean updatePreviousThrows;
    private boolean updateScore;

    int orientation;
    protected LinearLayout summaryParent;
    private RelativeLayout scoringParent;
    private String rowKey;
    Handler liveScores;
    private Runnable liveScoreRunnable;
    private RelativeLayout challengeParent;
    private RelativeLayout bowlAgainParent;
    Button gameSummary;
    int diameter;
    float verticalMargin;
    float horizontalMargin;
    RelativeLayout firstBallParent;
    RelativeLayout secondBallParent;
    RelativeLayout thirdBallParent;
    public static List<String> tags = new ArrayList<>();
    //    OrientationEventListener mOrientationListener;
    static SavedFrame savedFrame;
    boolean trackBallType;
    boolean trackPocket;
    Spinner ballName;
    int lastFrameStates = 1;
    // 1: All Independent
    // 2: 1st & 2nd Dependent & 3rd active
    // 3: 2nd & 3rd Dependent
    // 4: 1st & 2nd Dependent & 3rd inactive
    String currentBallId = "0";
    Button pocket;
    Button brook;
    UserStatsAdapter bowlNameAdapter;

    static {
        ballNameMap = new HashMap<Integer, String>();
        for (int i = 1; i <= 10; i++) {
            ballNameMap.put(i, "0");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.orientation = getResources().getConfiguration().orientation;
        setContentView(R.layout.activity_main);
        pocket = (Button) findViewById(R.id.pocket);
        brook = (Button) findViewById(R.id.brooklyn);
        ballName = (Spinner) findViewById(R.id.ballType);
        liveCompetitionId = "";

        inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            new ASSL(GameScreen.this, (ViewGroup) findViewById(R.id.root),
                    AppConstants.SCREEN_HEIGHT, AppConstants.SCREEN_WIDTH,
                    false);
            diameter = (int) (getResources().getDimension(R.dimen.pin_diameter) * ASSL
                    .Xscale());
            verticalMargin = getResources().getDimension(
                    R.dimen.pin_vertical_margin)
                    * ASSL.Xscale();
            horizontalMargin = getResources().getDimension(
                    R.dimen.pin_horizontal_margin)
                    * ASSL.Xscale();
            frames = inflater.inflate(R.layout.frame, null);
            frames.setLayoutParams(new android.view.ViewGroup.LayoutParams(714, 150));
        } else {

            new ASSL(GameScreen.this, (ViewGroup) findViewById(R.id.root),
                    AppConstants.SCREEN_WIDTH, 1196,
                    false);
            diameter = (int) (getResources().getDimension(R.dimen.pin_diameter_land) * ASSL
                    .Yscale());
            verticalMargin = getResources().getDimension(
                    R.dimen.pin_vertical_margin_land)
                    * ASSL.Xscale();
            horizontalMargin = getResources().getDimension(
                    R.dimen.pin_horizontal_margin_land)
                    * ASSL.Xscale();
            frames = inflater.inflate(R.layout.frame_land, null);
            frames.setLayoutParams(new android.view.ViewGroup.LayoutParams(1160, 100));
        }

        ASSL.DoMagic(frames);
        tenFrameParent = (LinearLayout) findViewById(R.id.frameView);
        tenFrameParent.addView(frames);

        Intent intent = getIntent();
        laneCheckoutId = Data.gameData.checkoutId;
        venueId = Data.gameData.venueId;
        scoringType = Data.gameData.centerType;
        bowlingGameId = Data.gameData.gameId;
        liveCompetitionId = Data.gameData.liveGameId;
        centerName = Data.gameData.centerName;
        laneNumber = Data.gameData.laneNumber;
        bowlerName = Data.gameData.screenName;
        tags.clear();
//        if (intent.getBooleanExtra("newGame", true)) {
//            savedFrame = null;
//        }
//        DbController controller = new DbController(getApplicationContext());
//        controller.open();
//        Data.gameData = controller.getGameData(CommonUtil
//                .getScreenName(getApplicationContext()));
//        controller.close();

        handler = new Handler();
        liveScores = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                automaticGameView();
                handler.postDelayed(this, 20000);
            }
        };

        liveScoreRunnable = new Runnable() {
            @Override
            public void run() {
                liveScoreUpdate(false);
                liveScores.postDelayed(this, 20000);
            }
        };

        firstBallParent = (RelativeLayout) findViewById(R.id.first_ball_layout);
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            secondBallParent = (RelativeLayout) findViewById(R.id.second_ball_layout);
            thirdBallParent = (RelativeLayout) findViewById(R.id.third_ball_layout);
        }
        if (getScoringType().equalsIgnoreCase("Manual")) {
            CustomButtonListener listener = new CustomButtonListener(
                    diameter, horizontalMargin, verticalMargin);
            firstBallParent.setOnTouchListener(listener);
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                secondBallParent.setOnTouchListener(listener);
                thirdBallParent.setOnTouchListener(listener);
            }
        }

        summaryParent = (LinearLayout) findViewById(R.id.summaryParent);
        scoringParent = (RelativeLayout) findViewById(R.id.scoringParent);
        challengeParent = (RelativeLayout) findViewById(R.id.challengeParent);
        bowlAgainParent = (RelativeLayout) findViewById(R.id.bowlAgainParent);

        gameSummary = (Button) findViewById(R.id.gameSummary);

        throwLabel = (Button) findViewById(R.id.throwLabel);
        previousThrow = (Button) findViewById(R.id.previousThrow);
        nextThrow = (Button) findViewById(R.id.nextThrow);
        previousFrame = (Button) findViewById(R.id.previousFrame);
        nextFrame = (Button) findViewById(R.id.nextFrame);

        done = (Button) findViewById(R.id.done);
        totalScore = (TextView) findViewById(R.id.totalScore);
        center = (TextView) findViewById(R.id.centerName);
        center.setText(centerName);
        spareStrikeNormal();
        ((TextView) findViewById(R.id.bowlerName)).setText(bowlerName);

        for (int i = 1; i <= 10; i++) {
            pins[i] = ((Button) findViewById(CommonUtil.getIdFromName(
                    "pin" + i, R.id.class)));
        }

        if (getScoringType().equalsIgnoreCase("History")) {
            initilizePins();
            findViewById(R.id.strikeSpareParent).setVisibility(View.GONE);
            findViewById(R.id.strikeSpareParentPro).setVisibility(View.GONE);
            findViewById(R.id.challengeParent).setVisibility(View.GONE);
            findViewById(R.id.strikeSpareParent).setVisibility(View.GONE);
            findViewById(R.id.sideMenu).setVisibility(View.GONE);
            findViewById(R.id.menu).setVisibility(View.GONE);
            findViewById(R.id.back).setVisibility(View.VISIBLE);
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                findViewById(R.id.ballStrikeParent).setVisibility(View.GONE);
            }
            center.setText(getIntent().getStringExtra("gameName"));
            try {
                gameView(true, new JSONObject(getIntent().getStringExtra("response")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            getTags(true);
//            getChallengers();
//            fetchCommonLists();
        } else {
            initilizePins();
            if (true || intent.hasExtra("newGame")) {
                changeFrame(findViewById(CommonUtil.getIdFromName("frame1", R.id.class)));
            }
            getTags(false);
            getChallengers();
            fetchCommonLists();
        }

        if (intent.hasExtra("newGame")) {
            walletCall(this, venueId);
        }

    }

    public static void walletCall(Context ctx, int venueId) {
        JSONObject obj = null;
        StringEntity entity = null;
        try {
            obj = new JSONObject();
            obj.put("Points", 0);
            obj.put("Notes", "");
            obj.put("IsRedeemable", "");
            obj.put("VenueId", venueId);
            obj.put("BusinessBuilderItemID", 10000);
            entity = new StringEntity(obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (obj == null) return;

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.post(
                ctx,
                Data.baseUrl
                        + "venue/userpoint/0?token="
                        + CommonUtil.getAccessToken(ctx)
                        .replaceAll("[+]", "%2B") + "&apiKey="
                        + Data.apiKey, entity, "application/json",
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                    }

                    @Override
                    public void onFailure(Throwable e) {

                    }
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("SlidingActivityHelper.open", false);
        outState.putBoolean("SlidingActivityHelper.secondary", false);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }

    private void liveScoreView() {
        findViewById(R.id.strikeSpareParentPro).setVisibility(View.GONE);
        findViewById(R.id.strikeSpareParent).setVisibility(View.GONE);
        findViewById(R.id.challengeParent).setVisibility(View.GONE);
        findViewById(R.id.sideMenu).setVisibility(View.GONE);
        findViewById(R.id.menu).setVisibility(View.GONE);
        findViewById(R.id.back).setVisibility(View.VISIBLE);
        findViewById(R.id.coachView).setVisibility(View.VISIBLE);
        liveScoreUpdate(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getScoringType().equals("Manual")) {
            gameView(true);
        } else if (getScoringType().equals("Live")) {
            liveScores.removeCallbacksAndMessages(null);
            liveScores.post(liveScoreRunnable);
        } else if (!getScoringType().equalsIgnoreCase("History")) {
            handler.removeCallbacksAndMessages(null);
            handler.post(runnable);
        }

        if (!getScoringType().equalsIgnoreCase("History") && orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (Data.postedEntered || !Data.gameData.liveGameId.isEmpty()) {
                findViewById(R.id.backgroundLayout).setBackgroundResource(R.drawable.challenge_screen_bg);
                findViewById(R.id.h2hView).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.challengeText)).setText("View Challenges");
            } else {
                findViewById(R.id.h2hView).setVisibility(View.GONE);
                findViewById(R.id.backgroundLayout).setBackgroundResource(R.drawable.bg);
                ((TextView) findViewById(R.id.challengeText)).setText("Enter Challenges");
            }
        }

        if (Data.postedEntered || !Data.gameData.liveGameId.isEmpty()) {
            ((TextView) (challengeParent.getChildAt(0))).setText((Data.postedEntered || !Data.gameData.liveGameId.isEmpty()
                    ? "View" : "Enter") + " Challenges");
        }

        if (!getScoringType().equalsIgnoreCase("History")) {
            //Tags
            checkTags();
        }
    }

    private void checkTags() {
        boolean tagsUpdated = false;
        if (Data.postedEntered) {
            if (!tags.contains("H2H Posted")) {
                tags.add("H2H Posted");
                tagsUpdated = true;
            }
        }

        if (!Data.gameData.liveGameId.isEmpty()) {
            if (!tags.contains("H2H Live")) {
                tags.add("H2H Live");
                tagsUpdated = true;
            }
        }

        if (tagsUpdated) {
            Tags.addTags(GameScreen.this, bowlingGameId, tags, false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
        liveScores.removeCallbacksAndMessages(null);
    }

    public void changeFrame(View view) {

        ViewGroup frameParent = (ViewGroup) view;
        int frameNo = Integer.parseInt(frameParent.getChildAt(1).getTag()
                .toString());

        if (frameNo > maxFrameBowled + 1) {
            return;
        } else if (frameNo > maxFrameBowled) {
            maxFrameBowled = frameNo;
        }

        if (frameStates[currentFrame] == AppConstants.SELECTED) {
            frameStates[currentFrame] = AppConstants.BOWLED;
            // Change to Bowled
            toggleFrameState((ViewGroup) frames.findViewById(CommonUtil
                            .getIdFromName("frame" + currentFrame, R.id.class)),
                    orientation == Configuration.ORIENTATION_LANDSCAPE ? R.drawable.bowled_background_round :
                            R.drawable.bowled_background, R.drawable.bowled_background,
                    orientation == Configuration.ORIENTATION_LANDSCAPE ? R.drawable.bowled_background_bottom_round :
                            R.drawable.bowled_background,
                    AppConstants.BOWLED_TEXT_COLOR);
        }

        if (getScoringType().equalsIgnoreCase("Manual")) {
            if (currentFrame != 10) {
                checkPreviousState(currentFrame, getPocketCode());
            }
            throw1 = (TextView) findViewById(CommonUtil.getIdFromName("throw"
                    + (frameNo * 2 - 1), R.id.class));
            throw2 = (TextView) findViewById(CommonUtil.getIdFromName("throw"
                    + (frameNo * 2), R.id.class));
            if (frameNo == 10) {
                throw3 = (TextView) findViewById(CommonUtil.getIdFromName(
                        "throw21", R.id.class));
            } else {
                throw3 = null;
            }
        }

        currentFrame = frameNo;
        ballNumber = 1;
        throwLabel.setText("1st Throw");

        int squareNumber = 2 * currentFrame - 1;
        if (pinFallMap.get(squareNumber) == 0 && currentFrame != 10) {
            nextThrow.setVisibility(View.GONE);
            nextFrame.setVisibility(View.VISIBLE);
        } else {
            nextThrow.setVisibility(View.VISIBLE);
            nextFrame.setVisibility(View.GONE);
        }

        previousThrow.setVisibility(View.GONE);
        done.setVisibility(View.GONE);
        if (spare != null && strike != null) {
            spare.setVisibility(View.GONE);// Spare is tied to done and next frame
            strike.setVisibility(View.VISIBLE);
        }
        if (currentFrame == 1) {
            previousFrame.setVisibility(View.GONE);
        } else {
            previousFrame.setVisibility(View.VISIBLE);
        }

        frameStates[currentFrame] = AppConstants.SELECTED;
        // Change to selected
        toggleFrameState(frameParent, orientation == Configuration.ORIENTATION_LANDSCAPE ? R.drawable.selected_background_round :
                        R.drawable.selected_background, R.drawable.selected_background,
                orientation == Configuration.ORIENTATION_LANDSCAPE ? R.drawable.selected_background_bottom_round :
                        R.drawable.selected_background,
                AppConstants.SELECTED_TEXT_COLOR);
        renderPins(firstBallParent, currentFrame * 2 - 1);
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            renderPins(secondBallParent, currentFrame * 2);
            findViewById(R.id.firstBallParent).setVisibility(View.VISIBLE);
            findViewById(R.id.thirdBallParent).setVisibility(View.GONE);
            findViewById(R.id.doneLand).setVisibility(View.GONE);
            if (currentFrame == 10) {
                findViewById(R.id.lastFrameRight).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.lastFrameRight).setVisibility(View.GONE);
                findViewById(R.id.lastFrameLeft).setVisibility(View.GONE);
            }
        }

//        if (Data.userStatsSubscribed) {
//            if (ballName.getSelectedItem() != null) {
//                currentBallId = ((String[]) ballName.getSelectedItem())[0];
//            }
//        }

        if (Data.userStatsSubscribed) {
            int frame = currentFrame;
            setpocketBrook(frame);

            if (ballNameMap.get(currentFrame) != null
                    && bowlNameAdapter != null) {
                if (!ballNameMap.get(currentFrame).isEmpty()) {
                    int index = bowlNameAdapter.getIndexFromId(ballNameMap
                            .get(currentFrame));
                    if (index != -1) {
                        ballName.setSelection(index);
                    }
                }
            }
        }
    }

    private void setpocketBrook(int frame) {
        if (pocketBrookMap.get(frame) == 1) {
            pocket.setTag("off");
            pocket.setBackgroundResource(R.drawable.black_round);
            brook.setTag("on");
            brook.setBackgroundResource(R.drawable.blue_normal);
        } else if (pocketBrookMap.get(frame) == 2) {
            pocket.setTag("on");
            pocket.setBackgroundResource(R.drawable.blue_normal);
            brook.setTag("off");
            brook.setBackgroundResource(R.drawable.black_round);
        } else {
            pocket.setTag("off");
            pocket.setBackgroundResource(R.drawable.black_round);
            brook.setTag("off");
            brook.setBackgroundResource(R.drawable.black_round);
        }
    }

    public void renderPins(ViewGroup parent, int throwNum) {
        int pinState = pinFallMap.get(throwNum);
        for (int i = 1; i <= 10; i++) {
            if ((pinState & (int) Math.pow(2, i - 1)) > 0) {
                parent.findViewById(CommonUtil.getIdFromName("pin" + i, R.id.class))
                        .setBackgroundResource(R.drawable.bowling_pin);
            } else {
                parent.findViewById(CommonUtil.getIdFromName("pin" + i, R.id.class))
                        .setBackgroundResource(R.drawable.bowling_pin_down);
            }
        }
    }

    public void toggleFrameState(ViewGroup parent, int topBackground, int middleBackground,
                                 int bottomBackground, int textColor) {
//        for (int i = 0; i < parent.getChildCount(); i++) {
        ViewGroup view = parent;
        TextView top = (TextView) view.getChildAt(0);
        top.setBackgroundResource(topBackground);
        top.setTextColor(textColor);
        TextView middleLeft = (TextView) ((ViewGroup) view.getChildAt(1)).getChildAt(0);
        middleLeft.setBackgroundResource(middleBackground);
        middleLeft.setTextColor(textColor);
        TextView middleRight = (TextView) ((ViewGroup) view.getChildAt(1)).getChildAt(1);
        middleRight.setBackgroundResource(middleBackground);
        middleRight.setTextColor(textColor);
        TextView middleLast = (TextView) ((ViewGroup) view.getChildAt(1)).getChildAt(2);
        if (middleLast != null) {
            middleLast.setBackgroundResource(middleBackground);
            middleLast.setTextColor(textColor);
        }

        TextView bottom = (TextView) view.getChildAt(2);
        bottom.setBackgroundResource(bottomBackground);
        bottom.setTextColor(textColor);

//            if (view instanceof ViewGroup) {
//                toggleFrameState((ViewGroup) view, background, textColor);
//            } else if (view instanceof TextView) {
//                view.setBackgroundResource(background);
//                ((TextView) view).setTextColor(textColor);
//            }
    }
//    }

    public void toggle(View v) {
        toggle();
    }

    private void liveScoreUpdate(boolean showLoading) {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String to = df.format(c.getTime());

        String from = df.format(CommonUtil.minusTime(-90).getTime());

        if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                getApplicationContext())) {
            return;
        }

        if (showLoading && !CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(GameScreen.this, "Please Wait...");
        }
        RequestParams rv = new RequestParams();
        rv.put("from", from);
        rv.put("to", to);
        rv.put("apiKey", Data.apiKey);
        rv.put("token", CommonUtil.getAccessToken(getApplicationContext()));
        rv.put("rowKey", rowKey);
        rv.put("laneId", laneNumber);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(Data.baseUrl + "venue/" + venueId + "/GamePerPlayer", rv,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        Log.d("Live Score", "Live Score Updating");
                        CommonUtil.loading_box_stop();
                        try {
                            updateScore(new JSONObject(response), frames, false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.loading_box_stop();
                    }
                });
    }

    private void automaticGameView() {
        Log.v("func", "automaticGameView");
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.get(
                Data.baseUrl
                        + "lanecheckout/"
                        + laneCheckoutId
                        + "/bowlinggameview?token="
                        + CommonUtil.getAccessToken(getApplicationContext())
                        .replaceAll("[+]", "%2B") + "&apiKey="
                        + Data.apiKey, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONObject bowlingGame = json
                                    .getJSONObject("bowlingGame");
                            updateScore(bowlingGame, frames, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {

                    }
                });
    }

    private void updateScore(JSONObject bowlingGame, View frames,
                             boolean checkGameCompletion) {

        try {
            for (int i = 1; i <= 21; i++) {
//                TextView throwScore = (TextView) frames.findViewById(CommonUtil
//                        .getIdFromName("throw" + i, R.id.class));
//                throwScore.setText(bowlingGame.getString("squareScore" + i));
                int pins = bowlingGame.getInt("standingPins"
                        + (i < 10 ? "0" : "") + i);
                if (pins == 0) {
                    if (!bowlingGame.getString("squareScore" + i)
                            .equalsIgnoreCase("X")
                            && !bowlingGame.getString("squareScore" + i)
                            .equalsIgnoreCase("/")) {
                        pins = 1023;
                    }
                }
                pinFallMap.put(i, pins);
            }


            for (int i = 1; i <= 21; i++) {
                TextView throwScore = (TextView) frames.findViewById(CommonUtil
                        .getIdFromName("throw" + i, R.id.class));
                throwScore.setText(bowlingGame.getString("squareScore" + i));
//                int pins = bowlingGame.getInt("standingPins"
//                        + (i < 10 ? "0" : "") + i);
//                if (pins == 0) {
//                    if (!bowlingGame.getString("squareScore" + i)
//                            .equalsIgnoreCase("X")
//                            && !bowlingGame.getString("squareScore" + i)
//                            .equalsIgnoreCase("/")) {
//                        pins = 1023;
//                    }
//                }
//                pinFallMap.put(i, pins);
                // standingPins[i] = bowlingGame.getInt("standingPins"
                // + (i < 10 ? "0" : "") + i);

                if (!bowlingGame.getString("squareScore" + i).isEmpty()
                        && i > maxSquareBowled) {
                    maxSquareBowled = i;

                    if (maxSquareBowled > maxFrameBowled * 2) {
                        int temp = maxFrameBowled + 1;
                        if (temp > 10) {
                            temp = 10;
                        }
                        changeFrame(findViewById(CommonUtil.getIdFromName(
                                "frame" + temp, R.id.class)));
                    } else if (maxSquareBowled == maxFrameBowled * 2) {
                        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            renderPins(firstBallParent, currentFrame * 2 - 1);
                            renderPins(secondBallParent, currentFrame * 2);
                        }
                    }
                }
            }


            for (int i = 1; i <= 10; i++) {
                TextView frameScore = (TextView) frames.findViewById(CommonUtil
                        .getIdFromName("frameScore" + i, R.id.class));

                frameScore.setText(bowlingGame.getString("frameScore" + i));
                if (checkGameCompletion) {
                    if (!bowlingGame.getString("frameScore" + i).isEmpty()
                            && i > maxSquareBowled) {
                        maxSquareBowled = i;
                    }
                }
            }
            totalScore.setText(bowlingGame.getString("finalScore"));
            if (checkGameCompletion) {
                if (bowlingGame.getBoolean("isComplete")) {
                    if (!gameCompleted) {
                        gameCompleted = true;
                        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                            fetchStats((ListView) findViewById(R.id.summaryList));
                        } else {
                            walletCall(this,venueId);
                            new AlertDialog.Builder(GameScreen.this)
                                    .setTitle("Game Completed")
                                    .setCancelable(false)
                                    .setMessage(
                                            "Congratulations.You have scored "
                                                    + bowlingGame
                                                    .getString("finalScore")
                                                    + ".")
                                    .setPositiveButton(android.R.string.yes,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    dialog.dismiss();
                                                }
                                            }).show();
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[] calculateScore(int frame, int firstPins, int secondPins,
                                    int thirdPins) {
        int firstScore = 0;
        int secondScore = 0;
        for (int i = 1; i <= 10; i++) {
            if ((firstPins & (int) Math.pow(2, i - 1)) == 0) {
                ++firstScore;
            }
            if ((secondPins & (int) Math.pow(2, i - 1)) == 0) {
                ++secondScore;
            }
        }

        int thirdScore = 0;

        if (thirdPins != -1) {
            for (int i = 1; i <= 10; i++) {
                if ((thirdPins & (int) Math.pow(2, i - 1)) == 0) {
                    ++thirdScore;
                }
            }
        }

        String first = "";
        String second = "";
        String third = "";

        if (frame == 10) {
            if (firstScore == 10) {
                first = "X";
                second = secondScore + "";
                if (secondScore == 10) {
                    second = "X";
                    third = thirdScore + "";
                    if (thirdScore == 10) {
                        third = "X";
                    }
                } else {
                    if (thirdScore == 10) {
                        third = "/";
                    } else {
                        third = (thirdScore - secondScore) + "";
                    }
                }
            } else {
                first = firstScore + "";
                if (secondScore == 10) {
                    second = "/";
                    if (thirdScore == 10) {
                        third = "X";
                    } else {
                        third = thirdScore + "";
                    }
                } else {
                    second = (secondScore - firstScore) + "";
                }
            }

        } else {
            if (firstScore == 10) {
                first = "X";
                second = "";
            } else if (secondScore == 10) {
                second = "/";
                first = firstScore + "";
            } else {
                second = (secondScore - firstScore) + "";
                first = firstScore + "";
            }
        }

        return new String[]{first, second, third};
    }

    private boolean checkPreviousState(final int frameToDisplay, int code) {

        if (!getScoringType().equals("Manual")) {
            createThrow(frameToDisplay, code);
            return false;
        }

        if (!updateScore) {
            return false;
        }

        boolean stateChanged = false;
        if (!pinFallMap.get(frameToDisplay * 2 - 1).equals(
                pinFallPreviousState.get(frameToDisplay * 2 - 1))) {
            stateChanged = true;
            pinFallPreviousState.put(frameToDisplay * 2 - 1,
                    pinFallMap.get(frameToDisplay * 2 - 1));
        }

        if (!pinFallMap.get(frameToDisplay * 2).equals(
                pinFallPreviousState.get(frameToDisplay * 2))) {
            stateChanged = true;
            pinFallPreviousState.put(frameToDisplay * 2,
                    pinFallMap.get(frameToDisplay * 2));
        }

        if (frameToDisplay == 10) {
            if (!pinFallMap.get(frameToDisplay * 2 + 1).equals(
                    pinFallPreviousState.get(frameToDisplay * 2 + 1))) {
                stateChanged = true;
                pinFallPreviousState.put(frameToDisplay * 2 + 1,
                        pinFallMap.get(frameToDisplay * 2 + 1));
            }
        }

        if (true || stateChanged) {
            String[] scores = calculateScore(
                    frameToDisplay,
                    pinFallMap.get(frameToDisplay * 2 - 1),
                    pinFallMap.get(frameToDisplay * 2),
                    frameToDisplay == 10
                            && (pinFallMap.get(19) == 0 || pinFallMap.get(20) == 0) ? pinFallMap
                            .get(frameToDisplay * 2 + 1) : -1);

            TextView throw1 = (TextView) findViewById(CommonUtil.getIdFromName(
                    "throw" + (frameToDisplay * 2 - 1), R.id.class));
            TextView throw2 = (TextView) findViewById(CommonUtil.getIdFromName(
                    "throw" + (frameToDisplay * 2), R.id.class));
            throw1.setText(scores[0]);
            throw2.setText(scores[1]);

            if (frameToDisplay == 10) {
                TextView throw3 = (TextView) findViewById(CommonUtil
                        .getIdFromName("throw" + (frameToDisplay * 2 + 1),
                                R.id.class));
                throw3.setText(scores[2]);
            }
            enterScore(frameToDisplay, scores[0], scores[1], scores[2], code);

            return true;
        }

        // else {
        // createThrow(frameToDisplay, code);
        // }

        return false;
    }

    private String getScoringType() {
        if (scoringType == null || scoringType.isEmpty()) {
            return Data.gameData.centerType;
        }
        return scoringType;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.h2hSummary:

                if (((Button) view).getText().toString().equalsIgnoreCase("User Stats")) {
                    ((Button) view).setText("Challenges");
                    findViewById(R.id.summaryList).setVisibility(View.VISIBLE);
                    findViewById(R.id.h2HChallengeParent).setVisibility(View.GONE);
                } else {
                    ((Button) view).setText("User Stats");
                    findViewById(R.id.summaryList).setVisibility(View.GONE);
                    findViewById(R.id.h2HChallengeParent).setVisibility(View.VISIBLE);
                }
                break;

            case R.id.h2hView: {
                Intent intent;
                if (Data.lastChallengeVisited == AppConstants.H2H_LIVE) {
                    intent = new Intent(this, ChallengeView.class);
                } else {
                    if (Data.lastPostedOpp == null) {
                        intent = new Intent(this, H2HPostedMain.class);
                    } else {
                        intent = new Intent(this, ChallengeView.class);
                        intent.putExtra("postedOpp", Data.lastPostedOpp);
                    }
                }
                startActivity(intent);
                finish();
            }
            break;

            case R.id.challengeParent: {
                Intent intent = new Intent(this, Challenges.class);
//                intent.putExtra("gameData", new Game(laneCheckoutId, getScoringType(), bowlingGameId, liveCompetitionId));
                startActivity(intent);
                finish();
                break;
            }

            case R.id.nextThrow:
                done.setVisibility(View.GONE);
                previousFrame.setVisibility(View.GONE);
                previousThrow.setVisibility(View.VISIBLE);
                spare.setVisibility(View.GONE);
                strike.setVisibility(View.GONE);

                ballNumber++;
                if (currentFrame == 10) {
                    if (ballNumber == 3
                            || !(pinFallMap.get(19) == 0 || pinFallMap.get(20) == 0)) {
                        nextThrow.setVisibility(View.GONE);
                        if (getScoringType().equalsIgnoreCase("Manual")) {
                            done.setVisibility(View.VISIBLE);
                        }
                    }
                    strikeSpareToggle();
                    int frame = 10;
                    if (ballNumber == 2) {
                        if (pinFallMap.get(19) == 0) {
                            frame = 11;
                        } else {
                            frame = 10;
                        }
                    } else if (ballNumber == 3) {
                        if (pinFallMap.get(20) == 0) {
                            frame = 12;
                        } else {
                            frame = 11;
                        }
                    }
                    setpocketBrook(frame);
                } else {
                    nextThrow.setVisibility(View.GONE);
                    nextFrame.setVisibility(View.VISIBLE);
                    spare.setVisibility(View.VISIBLE);
                }

                throwLabel.setText(ballNumber + CommonUtil.numberSuffix[ballNumber]
                        + " Throw");
                renderPins(firstBallParent, 2 * (currentFrame - 1) + ballNumber);
                // checkPreviousState(currentFrame, 1);
                break;

            case R.id.previousThrow:
                done.setVisibility(View.GONE);
                nextFrame.setVisibility(View.GONE);
                nextThrow.setVisibility(View.VISIBLE);
                spare.setVisibility(View.GONE);
                strike.setVisibility(View.GONE);
                ballNumber--;

                if (currentFrame == 10) {
                    strikeSpareToggle();
                    int frame = 10;
                    if (ballNumber == 2) {
                        if (pinFallMap.get(19) == 0) {
                            frame = 11;
                        } else {
                            frame = 10;
                        }
                    } else if (ballNumber == 3) {
                        if (pinFallMap.get(20) == 0) {
                            frame = 12;
                        } else {
                            frame = 11;
                        }
                    }
                    setpocketBrook(frame);
                } else {
                    strike.setVisibility(View.VISIBLE);
                }

                if (ballNumber == 1) {
                    previousThrow.setVisibility(View.GONE);
                    if (currentFrame != 1) {
                        previousFrame.setVisibility(View.VISIBLE);
                    }
                }
                throwLabel.setText(ballNumber + CommonUtil.numberSuffix[ballNumber]
                        + " Throw");
                renderPins(firstBallParent, 2 * (currentFrame - 1) + ballNumber);
                // checkPreviousState(currentFrame, 1);
                break;

            case R.id.nextFrame:
                if (currentFrame + 1 <= 10) {
                    changeFrame(frames.findViewById(CommonUtil.getIdFromName(
                            "frame" + (currentFrame + 1), R.id.class)));
                }
                break;

            case R.id.previousFrame:
                if (currentFrame - 1 > 0) {
                    changeFrame(frames.findViewById(CommonUtil.getIdFromName(
                            "frame" + (currentFrame - 1), R.id.class)));
                }
                break;

            case R.id.done:
            case R.id.doneLand:
                checkPreviousState(currentFrame, getPocketCode());
                break;

            case R.id.sideMenu:
                // checkout(venueId, bowlerName, laneNumber, getScoringType());
                // ((Button) findViewById(R.id.sideMenu)).performClick();
//                cancelGame(bowlingGameId, getScoringType());
//                toggle();
                showSecondaryMenu();
                ((TextView) findViewById(R.id.sideCenterName)).setText(centerName);
                ((TextView) findViewById(R.id.sideLaneNumber)).setText(laneNumber);
                ((TextView) findViewById(R.id.sidePlayerName)).setText(bowlerName);

                updateTags(GameScreen.this);
                findViewById(R.id.tags).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(GameScreen.this, Tags.class);
                        intent.putExtra("gameId", bowlingGameId);
                        startActivityForResult(intent, TAG_REQUEST);
                    }
                });

                findViewById(R.id.sideQuitGame).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelGame(bowlingGameId, getScoringType());
                    }
                });

                findViewById(R.id.landscapeView).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                findViewById(R.id.sideGameSummary).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fetchStats((ListView) findViewById(R.id.summaryList));
                    }
                });

                findViewById(R.id.sideLeaderboard).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(GameScreen.this, Leaderboard.class);
                        intent.putExtra("center", true);
                        intent.putExtra("venueId", venueId);
                        intent.putExtra("venueName", centerName);
                        startActivity(intent);
                    }
                });
                break;

            case R.id.strikePro:
            case R.id.sparePro:
            case R.id.strike:
            case R.id.spare: {
                if (!getScoringType().equalsIgnoreCase("manual")) {
                    return;
                }
                int squareNumber = 2 * (currentFrame - 1) + ballNumber;
                pinFallMap.put(squareNumber, 0);
                if (view.getId() == R.id.strike || view.getId() == R.id.strikePro) {
                    if (squareNumber < 19) {
                        pinFallMap.put(squareNumber + 1, 0);
                        nextThrow.setVisibility(View.GONE);
                        nextFrame.setVisibility(View.VISIBLE);
                    } else if (squareNumber == 19) {
                        pinFallMap.put(20, 1023);
                    } else if (squareNumber == 20) {
                        pinFallMap.put(21, 1023);
                    }

                    if (squareNumber >= 19) {
                        int frame = 10;
                        if (ballNumber == 2) {
                            if (pinFallMap.get(19) == 0) {
                                frame = 11;
                            } else {
                                frame = 10;
                            }
                        } else if (ballNumber == 3) {
                            if (pinFallMap.get(20) == 0) {
                                frame = 12;
                            } else {
                                frame = 11;
                            }
                        }
                        setpocketBrook(frame);
                    }
                } else {
                    if (currentFrame == 10) {
                        boolean temp = pinFallMap.get(19) == 0
                                || pinFallMap.get(20) == 0;
                        if (squareNumber <= 20) {
                            nextThrow.setVisibility(temp ? View.VISIBLE : View.GONE);
                            done.setVisibility(!temp ? View.VISIBLE : View.GONE);
                        }
                        int frame = 10;
                        if (ballNumber == 2) {
                            if (pinFallMap.get(19) == 0) {
                                frame = 11;
                            } else {
                                frame = 10;
                            }
                        } else if (ballNumber == 3) {
                            if (pinFallMap.get(20) == 0) {
                                frame = 12;
                            } else {
                                frame = 11;
                            }
                        }
                        setpocketBrook(frame);
                    }
                }
                for (int i = 1; i <= 10; i++) {
                    pins[i].setBackgroundResource(R.drawable.bowling_pin_down);
                }
                String[] scores = calculateScore(
                        currentFrame,
                        pinFallMap.get(currentFrame * 2 - 1),
                        pinFallMap.get(currentFrame * 2),
                        currentFrame == 10
                                && (pinFallMap.get(19) == 0 || pinFallMap.get(20) == 0) ? pinFallMap
                                .get(currentFrame * 2 + 1) : -1);
                throw1.setText(scores[0]);
                throw2.setText(scores[1]);
                if (throw3 != null) {
                    throw3.setText(scores[2]);
                }
                break;
            }

            case R.id.foul: {
                break;
            }

            case R.id.bowlAgain:
                checkout(Data.gameData.venueId, Data.gameData.screenName,
                        Data.gameData.laneNumber, getScoringType());
                break;

            case R.id.back:
                finish();
                break;

            case R.id.backToPinView:
                clearArea();
                scoringParent.setVisibility(View.VISIBLE);
                if (gameCompleted) {
                    gameSummary.setVisibility(View.VISIBLE);
                } else {
                    challengeParent.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.gameSummary:
                clearArea();
                challengeSummary(view);
                summaryParent.setVisibility(View.VISIBLE);
                if (gameCompleted) {
                    findViewById(R.id.summaryList).setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                            (int) (370 * ASSL.Xscale())));
                    bowlAgainParent.setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.summaryList).setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                            (int) (550 * ASSL.Xscale())));
                    challengeParent.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.postGame:
                final AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Post Game");
                alert.setMessage("Enter a name for this game :");

                final EditText input = new EditText(this);
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String gameName = input.getText().toString();
                        if (gameName.isEmpty()) {
                            Toast.makeText(GameScreen.this,
                                    "Please enter game name.", Toast.LENGTH_SHORT).show();
                        } else {
                            postGame(bowlingGameId, gameName);
                        }
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                alert.show();
                break;

            case R.id.shareOnFb:
                askToPost("fb");
                break;

            case R.id.coachView:
                Intent intent = new Intent(GameScreen.this, CoachView.class);
                intent.putExtra("laneNumber", laneNumber);
                intent.putExtra("centerName", centerName);
                intent.putExtra("venueId", venueId);
                startActivity(intent);
                break;

            case R.id.lastFrameLeft:
                findViewById(R.id.firstBallParent).setVisibility(View.VISIBLE);
                findViewById(R.id.thirdBallParent).setVisibility(View.GONE);
                findViewById(R.id.lastFrameRight).setVisibility(View.VISIBLE);
                findViewById(R.id.lastFrameLeft).setVisibility(View.GONE);
                findViewById(R.id.doneLand).setVisibility(View.GONE);
                break;

            case R.id.lastFrameRight:
                findViewById(R.id.firstBallParent).setVisibility(View.GONE);
                findViewById(R.id.thirdBallParent).setVisibility(View.VISIBLE);
                findViewById(R.id.lastFrameRight).setVisibility(View.GONE);
                findViewById(R.id.lastFrameLeft).setVisibility(View.VISIBLE);
                findViewById(R.id.doneLand).setVisibility(View.VISIBLE);
                break;
        }
    }

    public static void updateTags(Context ctx) {
        TextView tagsTv = (TextView) ((Activity) ctx).findViewById(R.id.tags);
        StringBuilder builder = new StringBuilder("Tags : ");
        if (tags.size() == 0) {
            builder.append("Add Tags   ");
        } else {
            for (int i = 0; i < tags.size(); i++) {
                builder.append(tags.get(i));
                builder.append(" | ");
            }
        }
        tagsTv.setText(builder.substring(0, builder.length() - 3));
    }

    private void askToPost(final String network) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameScreen.this);
        builder.setTitle("Confirm");
        if (network.equals("fb")) {
            builder.setMessage("Are you sure you want to post your score on Facebook ? You will get 5 credits for posting.");
        } else {
            builder.setMessage("Are you sure you want to tweet your score ? You will get 5 credits for tweeting.");
        }
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                if (network.equals("fb")) {
                    fbLogin();
                } else {
//                    twitterPost();
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
                : false;
    }

    public void fbLogin() {
        Session session = new Session(GameScreen.this);
        Session.setActiveSession(session);
        Session.OpenRequest openRequest = null;
        openRequest = new Session.OpenRequest(GameScreen.this);
        openRequest.setPermissions(Arrays.asList("publish_actions", "basic_info"));
        try {
            if (isSystemPackage(getPackageManager().getPackageInfo(
                    "com.facebook.katana", 0))) {
                openRequest.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
            } else {
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        openRequest.setCallback(new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state,
                             Exception exception) {
                if (session.isOpened()) {
                    facebookPost(session.getAccessToken());
                }
            }
        });
        session.openForPublish(openRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAG_REQUEST) {
            if (resultCode == RESULT_OK) {
                updateTags(GameScreen.this);
                showSecondaryMenu();
            }
        } else {
            Session.getActiveSession().onActivityResult(this, requestCode,
                    resultCode, data);
        }
    }

    public void facebookPost(String token) {

        if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                getApplicationContext())) {
            CommonUtil.noInternetDialog(GameScreen.this);
            return;
        }

        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(this, "Please wait...");
        }
        RequestParams rv = new RequestParams();
        rv.put("token", CommonUtil.getAccessToken(getApplicationContext()).replaceAll("[+]", "%2B"));
        rv.put("apiKey", Data.apiKey);
        rv.put("accessToken", token);

        AsyncHttpClient client = new AsyncHttpClient();
        // http://api.staging.xbowling.com/leaderboard/usbcintercollegiatesingles?state=tn&startIndex=0&pageSize=25&gender=m&token
        client.setTimeout(CommonUtil.TIMEOUT);
        client.post(Data.baseUrl + "social/facebook/bowlinggame/"
                        + bowlingGameId + "?" + rv.toString(),
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        CommonUtil
                                .commonDialog(GameScreen.this, "Congratulations",
                                        "Your score was posted to Facebook and you received 5 credits.");
//                        getUserCredit();
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.loading_box_stop();
                        HttpResponseException hre = (HttpResponseException) e;
                        CommonUtil.commonGameErrorDialog(GameScreen.this,
                                e.getMessage()
                        );
                    }
                });

    }

    private void postGame(final String bowlingGameId, String competitionName) {
        StringEntity entity = null;
        try {
            JSONObject bowlingGame = new JSONObject();
            bowlingGame.put("id", bowlingGameId);
            JSONObject competition = new JSONObject();
            competition.put("maxGroups", 1);
            competition.put("maxChallengersPerGroup", 1);
            competition.put("name", competitionName);
            competition.put("competitionType", "Posted");

            JSONObject json = new JSONObject();
            json.put("bowlingGame", bowlingGame);
            json.put("competition", competition);
            json.put("entryFeeCredits", 0);
            entity = new StringEntity(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                getApplicationContext())) {
            CommonUtil.noInternetDialog(GameScreen.this);
            return;
        }
        CommonUtil.loading_box(this, "Please wait...");

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.post(getApplicationContext(), Data.baseUrl
                        + "bowlingcompetition/posted?token="
                        + CommonUtil.getAccessToken(getApplicationContext())
                        .replaceAll("[+]", "%2B") + "&apiKey=" + Data.apiKey,
                entity, "application/json", new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        Toast.makeText(getApplicationContext(), "Posted",
                                Toast.LENGTH_SHORT).show();
                        if (!tags.contains("Posted Game")) {
                            tags.add("Posted Game");
                            Tags.addTags(GameScreen.this, bowlingGameId, tags, false); //       tags.add("Posted Game");
//                            updateTags(GameScreen.this);
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.commonGameErrorDialog(GameScreen.this,
                                "An error occured. Please try again.");
                        CommonUtil.loading_box_stop();
                    }
                });
    }

    private void strikeSpareToggle() {
        if (ballNumber == 1) {
            strike.setVisibility(View.VISIBLE);
        } else if (ballNumber == 2) {
            if (pinFallMap.get(19) != 0) {
                spare.setVisibility(View.VISIBLE);
            } else {
                strike.setVisibility(View.VISIBLE);
            }
        } else if (ballNumber == 3) {
            if (pinFallMap.get(19) == 0) {
                if (pinFallMap.get(20) == 0) {
                    strike.setVisibility(View.VISIBLE);
                } else {
                    spare.setVisibility(View.VISIBLE);
                }
            } else {
                strike.setVisibility(View.VISIBLE);
            }
        }
    }

    private void cancelGame(final String bowlingGameId, final String scoringType) {

        if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                getApplicationContext())) {
            CommonUtil.noInternetDialog(GameScreen.this);
            return;
        }

        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(GameScreen.this, "Please wait...");
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("id", laneCheckoutId);
                    HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(
                            Data.baseUrl
                                    + (scoringType.equals("Manual") ? "manual"
                                    : "")
                                    + "lanecheckout?token="
                                    + CommonUtil.getAccessToken(
                                    getApplicationContext())
                                    .replaceAll("[+]", "%2B")
                                    + "&apiKey=" + Data.apiKey);
                    StringEntity entity = new StringEntity(jsonObj.toString(),
                            HTTP.UTF_8);
                    entity.setContentType("application/json");
                    httpDelete.setEntity(entity);
                    HttpParams httpParameters = new BasicHttpParams();
                    // Set the timeout in milliseconds until a connection is
                    // established.
                    // The default value is zero, that means the timeout is not
                    // used.
                    int timeoutConnection = 5000;
                    HttpConnectionParams.setConnectionTimeout(httpParameters,
                            timeoutConnection);
                    // Set the default socket timeout (SO_TIMEOUT)
                    // in milliseconds which is the timeout for waiting for
                    // data.
                    int timeoutSocket = 5000;
                    HttpConnectionParams.setSoTimeout(httpParameters,
                            timeoutSocket);

                    DefaultHttpClient client = new DefaultHttpClient(
                            httpParameters);
                    final HttpResponse response = client.execute(httpDelete);
                    if (response.getStatusLine().getStatusCode() == 204) {
                        DbController controller = new DbController(
                                getApplicationContext());
                        controller.open();
                        Log.v("Rows Deleted : ", "" + controller.deleteGame(CommonUtil
                                .getScreenName(getApplicationContext())));
                        controller.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            CommonUtil.loading_box_stop();
                        }
                    });
                    Intent intent = new Intent(GameScreen.this, GoBowling.class);
                    startActivity(intent);
                    finish();
                }
            }
        }).start();
    }

    private void pinClickAction(int circle, ViewGroup parent) {

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (parent.getId() == R.id.first_ball_layout) {
                ballNumber = 1;
            } else if (parent.getId() == R.id.second_ball_layout) {
                ballNumber = 2;
            } else if (parent.getId() == R.id.third_ball_layout) {
                ballNumber = 3;
            }
        }
        int squareNumber = 2 * (currentFrame - 1) + ballNumber;
        if (squareNumber >= 19) {
            lastFrameHandling(squareNumber, ballNumber, circle, parent);
        } else {
            int pinFall = pinFallMap.get(squareNumber);
            int mask = (int) Math.pow(2, circle - 1);
            boolean currentStanding = (pinFall & mask) > 0;

            if (ballNumber == 1) {
                currentStanding = !currentStanding;
                pinFallMap.put(squareNumber, pinFall ^ mask);
                parent.findViewById(CommonUtil.getIdFromName("pin" + circle, R.id.class))
                        .setBackgroundResource(currentStanding ? R.drawable.bowling_pin
                                : R.drawable.bowling_pin_down);

                squareNumber = currentFrame * 2;
                pinFall = pinFallMap.get(squareNumber);
                if (currentStanding) {
                    pinFallMap.put(squareNumber, pinFall | mask);
                } else {
                    pinFallMap.put(squareNumber, pinFall & ~mask);
                }
                if (secondBallParent != null) {
                    secondBallParent.findViewById(CommonUtil.getIdFromName("pin" + circle, R.id.class))
                            .setBackgroundResource(currentStanding ? R.drawable.bowling_pin
                                    : R.drawable.bowling_pin_down);
                }

                if (pinFallMap.get(squareNumber) == 0) {
                    nextThrow.setVisibility(View.GONE);
                    nextFrame.setVisibility(View.VISIBLE);
                } else {
                    nextThrow.setVisibility(View.VISIBLE);
                    nextFrame.setVisibility(View.GONE);
                }

            } else if (ballNumber == 2) {
                int squareNumber1 = currentFrame * 2 - 1;
                int pinFall1 = pinFallMap.get(squareNumber1);
                int mask1 = (int) Math.pow(2, circle - 1);
                boolean firstStand = (pinFall1 & mask1) > 0;
                if (firstStand) {
                    currentStanding = !currentStanding;
                    pinFallMap.put(squareNumber, pinFall ^ mask);
                    parent.findViewById(CommonUtil.getIdFromName("pin" + circle, R.id.class))
                            .setBackgroundResource(currentStanding ? R.drawable.bowling_pin
                                    : R.drawable.bowling_pin_down);
                }
            }
        }

    }

    private void lastFrameHandling(int squareNumber, int fillColor, int circle, ViewGroup parent) {

        int pinFall = pinFallMap.get(squareNumber);
        int mask = (int) Math.pow(2, circle - 1);
        boolean currentStanding = (pinFall & mask) > 0;

        int previousState = pinFallMap.get(squareNumber);
        int ball = 0;

        // 1st Ball
        if (squareNumber == 19) {
            ball = 1;
            parent.findViewById(CommonUtil.getIdFromName("pin" + circle, R.id.class))
                    .setBackgroundResource(!currentStanding ? R.drawable.bowling_pin
                            : R.drawable.bowling_pin_down);
            pinFallMap.put(squareNumber, pinFall ^ mask);

            if ((pinFallMap.get(19) == 0 || pinFallMap.get(20) == 0)) {
                // if (pinFallMap.get(21) != 1023) {
                // for (int i = 1; i <= 10; i++) {
                // myGrad = (GradientDrawable) parent.findViewById(
                // CommonUtil.getIdFromName("thirdpin" + i,
                // R.id.class)).getBackground();
                // myGrad.setColor(AppConstants.COLOR_YELLOW);
                // }
                // pinFallMap.put(21, 0);
                // }
            }

            // findViewById(R.id.nextThrow).setVisibility(
            // pinFallMap.get(19) == 0 || pinFallMap.get(20) ==
            // 0?View.VISIBLE:View.GONE);

            if (previousState == 0 && pinFallMap.get(19) != 0) {
                pinFallMap.put(20, 0);
                pinFallMap.put(20, 0 | mask);
                if (secondBallParent != null) {
                    for (int i = 1; i <= 10; i++) {
                        secondBallParent.findViewById(CommonUtil.getIdFromName("pin" + i, R.id.class))
                                .setBackgroundResource(i == circle ? R.drawable.bowling_pin
                                        : R.drawable.bowling_pin_down);
                    }
                }
            } else {
                squareNumber = currentFrame * 2;
                pinFall = pinFallMap.get(squareNumber);
                if (!currentStanding) {
                    pinFallMap.put(squareNumber, pinFall | mask);
                } else {
                    pinFallMap.put(squareNumber, pinFall & ~mask);
                }
                if (secondBallParent != null) {
                    secondBallParent.findViewById(CommonUtil.getIdFromName("pin" + circle, R.id.class))
                            .setBackgroundResource(!currentStanding ? R.drawable.bowling_pin
                                    : R.drawable.bowling_pin_down);
                }
            }

            // New Code
            if (pinFallMap.get(19) == 0) {
                pinFallMap.put(20, 1023);
                if (secondBallParent != null) {
                    for (int i = 1; i <= 10; i++) {
                        secondBallParent.findViewById(CommonUtil.getIdFromName("pin" + i, R.id.class))
                                .setBackgroundResource(R.drawable.bowling_pin);
                    }
                }
            }
        } else if (squareNumber == 20) {
            ball = 2;
            boolean firstStand = false;
            if (pinFallMap.get(19) != 0) {

                int squareNumber1 = currentFrame * 2 - 1;
                int pinFall1 = pinFallMap.get(squareNumber1);
                int mask1 = (int) Math.pow(2, circle - 1);
                firstStand = (pinFall1 & mask1) > 0;
                if (firstStand) {
                    parent.findViewById(CommonUtil.getIdFromName("pin" + circle, R.id.class))
                            .setBackgroundResource(!currentStanding ? R.drawable.bowling_pin
                                    : R.drawable.bowling_pin_down);
                    pinFallMap.put(squareNumber, pinFall ^ mask);

                    if ((pinFallMap.get(19) == 0 || pinFallMap.get(20) == 0)) {
                        // if (pinFallMap.get(21) != 1023) {
                        // for (int i = 1; i <= 10; i++) {
                        // myGrad = (GradientDrawable) parent
                        // .findViewById(
                        // CommonUtil.getIdFromName(
                        // "thirdpin" + i,
                        // R.id.class))
                        // .getBackground();
                        // myGrad.setColor(AppConstants.COLOR_YELLOW);
                        // }
                        // pinFallMap.put(21, 0);
                        // }
                    } else {
                        pinFallMap.put(21, 1023);
                        if (thirdBallParent != null) {
                            for (int i = 1; i <= 10; i++) {
                                thirdBallParent.findViewById(CommonUtil.getIdFromName("pin" + i, R.id.class))
                                        .setBackgroundResource(R.drawable.bowling_pin);
                            }
                        }
                    }

                    boolean temp = pinFallMap.get(19) == 0
                            || pinFallMap.get(20) == 0;
                    nextThrow.setVisibility(temp ? View.VISIBLE : View.GONE);
                    done.setVisibility(!temp ? View.VISIBLE : View.GONE);
                }
            } else {
                parent.findViewById(CommonUtil.getIdFromName("pin" + circle, R.id.class))
                        .setBackgroundResource(!currentStanding ? R.drawable.bowling_pin
                                : R.drawable.bowling_pin_down);
                pinFallMap.put(squareNumber, pinFall ^ mask);

                if (previousState == 0 && pinFallMap.get(20) != 0) {
                    pinFallMap.put(21, 0);
                    pinFallMap.put(21, 0 | mask);
                    if (thirdBallParent != null) {
                        for (int i = 1; i <= 10; i++) {
                            thirdBallParent.findViewById(CommonUtil.getIdFromName("pin" + i, R.id.class))
                                    .setBackgroundResource(i == circle ? R.drawable.bowling_pin
                                            : R.drawable.bowling_pin_down);
                        }
                    }
                } else {
                    squareNumber = currentFrame * 2 + 1;
                    pinFall = pinFallMap.get(squareNumber);
                    if (!currentStanding) {
                        pinFallMap.put(squareNumber, pinFall | mask);
                    } else {
                        pinFallMap.put(squareNumber, pinFall & ~mask);
                    }
                    if (thirdBallParent != null) {
                        thirdBallParent.findViewById(CommonUtil.getIdFromName("pin" + circle, R.id.class))
                                .setBackgroundResource(!currentStanding ? R.drawable.bowling_pin
                                        : R.drawable.bowling_pin_down);
                    }
                }
            }

            // New Code
            if (firstStand && pinFallMap.get(20) == 0) {
                pinFallMap.put(21, 1023);
                if (thirdBallParent != null) {
                    for (int i = 1; i <= 10; i++) {
                        thirdBallParent.findViewById(CommonUtil.getIdFromName("pin" + i, R.id.class))
                                .setBackgroundResource(R.drawable.bowling_pin);
                    }
                }
            }

        } else if (squareNumber == 21) {
            ball = 3;
            // if (pinFallMap.get(19) == 0 || pinFallMap.get(20) == 0) {
            // myGrad.setColor(!currentStanding ? Color.WHITE : fillColor);
            // pinFallMap.put(squareNumber, pinFall ^ mask);
            // }

            if (pinFallMap.get(19) == 0 || pinFallMap.get(20) == 0) {
                if (pinFallMap.get(20) != 0) {
                    int squareNumber1 = currentFrame * 2;
                    int pinFall1 = pinFallMap.get(squareNumber1);
                    int mask1 = (int) Math.pow(2, circle - 1);
                    boolean firstStand = (pinFall1 & mask1) > 0;
                    if (firstStand) {
                        parent.findViewById(CommonUtil.getIdFromName("pin" + circle, R.id.class))
                                .setBackgroundResource(!currentStanding ? R.drawable.bowling_pin
                                        : R.drawable.bowling_pin_down);
                        pinFallMap.put(squareNumber, pinFall ^ mask);
                    }
                } else {
                    parent.findViewById(CommonUtil.getIdFromName("pin" + circle, R.id.class))
                            .setBackgroundResource(!currentStanding ? R.drawable.bowling_pin
                                    : R.drawable.bowling_pin_down);
                    pinFallMap.put(squareNumber, pinFall ^ mask);
                }
            }
        }
    }

    private void fetchStats(final ListView listView) {

        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(GameScreen.this, "Please Wait...");
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Data.baseUrl
                        + "UserStat/GetByGameId?token="
                        + CommonUtil.getAccessToken(getApplicationContext())
                        .replaceAll("[+]", "%2B") + "&apiKey=" + Data.apiKey
                        + "&BowlingGameId=" + bowlingGameId,

                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        JSONObject res;
                        List<String[]> datalist = new ArrayList<String[]>();
                        try {
                            if (getSlidingMenu().isSecondaryMenuShowing()) {
                                showContent();
                            }
                            clearArea();
                            summaryParent.setVisibility(View.VISIBLE);
                            if (gameCompleted) {
                                listView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                                        (int) (370 * ASSL.Xscale())));
                                bowlAgainParent.setVisibility(View.VISIBLE);
                            } else {
                                listView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                                        (int) (550 * ASSL.Xscale())));
                                challengeParent.setVisibility(View.VISIBLE);
                            }

                            res = new JSONObject(response);
                            // JSONObject res;
                            Log.v("Response", "response" + res.toString());

                            datalist.add(new String[]{"Strike %",
                                    res.getString("strikepercent")});
                            datalist.add(new String[]{"Spare %",
                                    res.getString("sparepercent")});
                            datalist.add(new String[]{"Single Pin Spare %",
                                    res.getString("singlePinpercent")});
                            datalist.add(new String[]{"Multi Pin Spare %",
                                    res.getString("multiPinpercent")});
                            datalist.add(new String[]{"Splits Spare Conversion %",
                                    res.getString("splitpercent")});

                            datalist.add(new String[]{"Open %",
                                    res.getString("openpercent")});
                            datalist.add(new String[]{"Total Scores",
                                    res.getString("totalScores")});
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                        StatsAdapter statsAdapter = new StatsAdapter(GameScreen.this,
                                datalist, R.layout.summary_item, 90);
                        listView.setAdapter(statsAdapter);
                    }

                    @Override
                    public void onFailure(Throwable arg0) {
                        CommonUtil.loading_box_stop();
                        Log.e("request fail", arg0.toString());
                        Toast.makeText(GameScreen.this, "Server Issue",
                                Toast.LENGTH_LONG).show();

                    }
                });
    }

    protected void clearArea() {
        scoringParent.setVisibility(View.GONE);
        summaryParent.setVisibility(View.GONE);
        gameSummary.setVisibility(View.GONE);
        challengeParent.setVisibility(View.GONE);
        bowlAgainParent.setVisibility(View.GONE);
    }

    class CustomButtonListener implements OnTouchListener {

        int diameter;
        float radiusSquare;
        float verticalSpace;
        float horizontalSpace;
        Map<Integer, int[][]> circleCenter;
        int[][] coordinates;
        int currentCircle;
        View parent;
        int parentId;

        public CustomButtonListener(int diameter, float horizontalSpace,
                                    float verticalSpace) {
            this.diameter = diameter;
            this.horizontalSpace = horizontalSpace;
            this.verticalSpace = verticalSpace;
            initializeCircleCenters(diameter, horizontalSpace, verticalSpace);
        }

//        private ViewGroup getParent(int id){
//            if(id == R.id.first_ball_layout){
//                return
//            }
//        }

        @Override
        public boolean onTouch(final View v, final MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    parentId = v.getId();
                    parent = v;
                    if (checkCircle(
                            (int) (event.getY() / (diameter + verticalSpace)),
                            event.getX(), event.getY()) != 0) {
                        // xy.setText("Score : "
                        // + calculateScore(1, pinFallMap.get(1), 0, 0)[0]);
                    }
                    return true; // if you want to handle the touch event

                case MotionEvent.ACTION_MOVE:

                    if (checkCircle(
                            (int) (event.getY() / (diameter + verticalSpace)),
                            event.getX(), event.getY()) != 0) {
                        // xy.setText("Score : "
                        // + calculateScore(1, pinFallMap.get(1), 0, 0)[0]);
                    }

                    break;

                case MotionEvent.ACTION_UP:
                    parentId = 0;
                    parent = null;
                    currentCircle = 0;
                    break;
            }
            return false;
        }

        synchronized int checkCircle(int row, float x, float y) {
            int[][] centres = circleCenter.get(row);
            int circle = 0;

            if (centres != null) {
                int i;
                for (i = 0; i < centres.length; i++) {
                    if ((Math.pow(centres[i][0] - x, 2) + Math.pow(
                            centres[i][1] - y, 2)) < radiusSquare) {
                        circle = i + 1 + ((3 - row) * (4 - row)) / 2;
                        if (currentCircle != circle) {
                            currentCircle = circle;
                            pinClickAction(circle, (ViewGroup) parent);
                        }
                        break;
                    }
                }

                if (i >= centres.length) {
                    currentCircle = 0;
                }

                if (circle != 0) {
                    String[] scores = calculateScore(
                            currentFrame,
                            pinFallMap.get(currentFrame * 2 - 1),
                            pinFallMap.get(currentFrame * 2),
                            currentFrame == 10
                                    && (pinFallMap.get(19) == 0 || pinFallMap
                                    .get(20) == 0) ? pinFallMap
                                    .get(currentFrame * 2 + 1) : -1);
                    throw1.setText(scores[0]);
                    throw2.setText(scores[1]);
                    if (throw3 != null) {
                        throw3.setText(scores[2]);
                    }

                    int maxFrameBowled = currentFrame;
                    int[] pinFall = new int[maxFrameBowled == 10 ? 3 : 2];
                    pinFall[0] = pinFallMap.get(maxFrameBowled * 2 - 1);
                    pinFall[1] = pinFallMap.get(maxFrameBowled * 2);
                    if (pinFall.length == 3) {
                        pinFall[2] = pinFallMap.get(maxFrameBowled * 2 + 1);
                    }
                    savedFrame = new SavedFrame(maxFrameBowled, pinFall, scores);
                }
            }
            return circle;
        }

        private void initializeCircleCenters(float diameter,
                                             float horizontalSpace, float verticalSpace) {

            float radius = diameter / 2f;
            radiusSquare = (float) Math.pow(radius, 2);
            circleCenter = new ConcurrentHashMap<Integer, int[][]>();
            int frameCenterX;
            // frameCenterX = (int) ((4 * diameter + 3 * horizontalSpace) / 2);
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                frameCenterX = (int) (getResources().getDimension(
                        R.dimen.screen_width)
                        * ASSL.Xscale() / 2);
            } else {
                frameCenterX = (int) ((4 * diameter + 3 * horizontalSpace) / 2);
            }

            // Fill Centers
            for (int i = 0; i < 4; i++) {
                int[][] pointsArray = new int[4 - i][2];
                int rowY;
                rowY = (int) (i * (verticalSpace + 2 * radius) + (verticalSpace / 2 + radius));
                int required = (4 - i) / 2;

                if ((i & 1) == 1) {
                    pointsArray[required][0] = frameCenterX;
                    pointsArray[required][1] = rowY;
                    if (required == 1) {
                        pointsArray[required - 1][0] = (int) (frameCenterX - (horizontalSpace + diameter));
                        pointsArray[required - 1][1] = rowY;
                        pointsArray[required + 1][0] = (int) (frameCenterX + (horizontalSpace + diameter));
                        pointsArray[required + 1][1] = rowY;
                    }
                } else {
                    int increment = (int) (horizontalSpace / 2 + radius);
                    for (int j = 0; j < required; j++) {
                        pointsArray[j][0] = frameCenterX
                                - ((required - j) * 2 - 1) * increment;
                        pointsArray[j][1] = rowY;
                        pointsArray[required * 2 - 1 - j][0] = frameCenterX
                                + ((required - j) * 2 - 1) * increment;
                        pointsArray[required * 2 - 1 - j][1] = rowY;
                    }
                }
                circleCenter.put(i, pointsArray);
            }
        }
    }

    private void enterScore(final int frameNumber, String firstBall,
                            String secondBall, String thirdBall, final int code) {

        // if (!firstBall.isEmpty() && (frameNumber * 2 - 1) > maxSquareBowled)
        // {
        // maxSquareBowled = frameNumber * 2 - 1;
        // }

        if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                getApplicationContext())) {
            CommonUtil.noInternetDialog(GameScreen.this);
            return;
        }

        if (frameNumber == 10) {
            if (!CommonUtil.is_loading_showing()) {
                CommonUtil.loading_box(GameScreen.this, "Please wait...");
            }
        }

        RequestParams rv = new RequestParams();
        rv.put("frameNumber", frameNumber + "");
        rv.put("firstBall", firstBall);
        rv.put("secondBall", secondBall);
        rv.put("thirdBall", thirdBall);
        rv.put("FirstBallStandingPin", pinFallMap.get(frameNumber * 2 - 1) + "");
        rv.put("SecondBallStandingPin", pinFallMap.get(frameNumber * 2) + "");
        rv.put("ThirdBallStandingPin",
                frameNumber == 10 ? pinFallMap.get(frameNumber * 2 + 1) + ""
                        : "");

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.post(
                Data.baseUrl
                        + "bowlinggame/"
                        + bowlingGameId
                        + "/manualscores?token="
                        + CommonUtil.getAccessToken(getApplicationContext())
                        .replaceAll("[+]", "%2B") + "&apiKey="
                        + Data.apiKey, rv, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        gameView(false);
                        if (Data.userStatsSubscribed) {
                            createThrow(frameNumber, code);
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.loading_box_stop();
                    }
                });
    }

    private void checkout(final int venueId, final String bowlerName,
                          final String laneNumber, final String scoringType) {

        if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                getApplicationContext())) {
            CommonUtil.noInternetDialog(GameScreen.this);
            return;
        }

        StringEntity entity = null;
        try {
            JSONObject venue = new JSONObject();
            venue.put("id", venueId);

            JSONObject json = new JSONObject();
            json.put("venue", venue);
            json.put("bowlerName", bowlerName);
            json.put("laneNumber", Integer.parseInt(laneNumber));
            json.put("CompetitionTypeId", Data.compTypeId);
            json.put("UserStatPatternLengthId", Data.patternLengthId);
            json.put("UserStatPatternNameId", Data.patternNameId);
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            Date date = new Date();
            json.put("CreatedDate", dateFormat.format(date));
            entity = new StringEntity(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(this, "Please wait...");
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.post(
                getApplicationContext(),
                Data.baseUrl
                        + (scoringType.equals("Manual") ? "manual" : "")
                        + "lanecheckout?token="
                        + CommonUtil.getAccessToken(getApplicationContext())
                        .replaceAll("[+]", "%2B") + "&apiKey="
                        + Data.apiKey, entity, "application/json",
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        try {
                            clearArea();
                            scoringParent.setVisibility(View.VISIBLE);
                            challengeParent.setVisibility(View.VISIBLE);
                            JSONObject json = new JSONObject(response);
                            String checkoutId = json.getString("id");
                            JSONObject bowlingGame = json
                                    .getJSONObject("bowlingGame");
                            bowlingGameId = bowlingGame.getString("id");
                            laneCheckoutId = json.getString("id");
                            tenFrameParent.removeAllViews();
                            frames = null;
                            frames = inflater.inflate(R.layout.frame, null);
                            ViewGroup scoreRoot = (ViewGroup) frames
                                    .findViewById(R.id.root);
                            scoreRoot.setTag(true);

                            frames.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                                    AppConstants.SCREEN_WIDTH, 150));
                            ASSL.DoMagic(frames);
                            tenFrameParent.addView(frames);
                            liveCompetitionId = "";
                            latestFrameNumber = 0;
                            lastSquare = 0;
                            gameCompleted = false;
                            maxSquareBowled = 0;
                            currentFrame = 1;
                            maxFrameBowled = 0;
                            updateScore = false;
                            Data.postedEntered = false;
                            ((TextView) findViewById(R.id.challengeText)).setText("Enter Challenges");


                            Data.gameData = new Game(CommonUtil
                                    .getScreenName(getApplicationContext()),
                                    checkoutId + "", scoringType, bowlingGame
                                    .getString("id") + "", laneNumber,
                                    centerName, "", bowlerName, venueId,
                                    Data.patternNameId, Data.patternLengthId,
                                    Data.compTypeId);
                            DbController controller = new DbController(
                                    getApplicationContext());
                            controller.open();
                            controller.deleteGame(CommonUtil
                                    .getScreenName(getApplicationContext()));
                            controller.insertNewGame(Data.gameData);
                            controller.close();

                            if (getScoringType().equals("Manual")) {
                                gameView(true);
                            } else {
                                handler.removeCallbacksAndMessages(null);
                                handler.post(runnable);
                            }

                            pinFallMap = new ConcurrentHashMap<Integer, Integer>();
                            pinFallPreviousState = new ConcurrentHashMap<Integer, Integer>();
                            pocketBrookMap = new HashMap<Integer, Integer>();
                            tags.clear();
                            initilizePins();
                            changeFrame(findViewById(CommonUtil.getIdFromName(
                                    "frame1", R.id.class)));
                            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                                findViewById(R.id.backgroundLayout).setBackgroundResource(R.drawable.bg);
                                findViewById(R.id.h2hView).setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.loading_box_stop();
                    }
                });
    }

    private void initilizePins() {
        for (int i = 1; i <= 21; i++) {
            pinFallMap.put(i, 1023);
            pinFallPreviousState.put(i, 1023);
        }
        for (int i = 1; i <= 10; i++) {
            pocketBrookMap.put(i, 0);
        }
        pocketBrookMap.put(11, 0);
        pocketBrookMap.put(12, 0);
    }

    private void gameView(final boolean updateThrows) {

        if (!CommonUtil.is_loading_showing() && updateThrows) {
            CommonUtil.loading_box(this, "Please wait...");
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.get(
                Data.baseUrl
                        + "manuallanecheckout/"
                        + laneCheckoutId
                        + "/bowlinggameviewnew?token="
                        + CommonUtil.getAccessToken(getApplicationContext())
                        .replaceAll("[+]", "%2B") + "&apiKey="
                        + Data.apiKey, new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        try {
                            JSONObject json = new JSONObject(response);

                            JSONObject bowlingGame = json
                                    .getJSONObject("bowlingGame");
                            gameView(updateThrows, bowlingGame);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        updateScore = true;
                        CommonUtil.loading_box_stop();
                    }
                });
    }

    public void gameView(boolean updateThrows, JSONObject bowlingGame) {
        try {
            int latestSquareNumber = bowlingGame
                    .getInt("latestSquareNumber");

            if (updateThrows) {

                if (savedFrame != null) {
//                                    maxFrameBowled = savedFrame.frameNumber;

                    int firstThrow = savedFrame.frameNumber * 2 - 1;
                    int secondThrow = savedFrame.frameNumber * 2;
                    bowlingGame.put("standingPins" + (firstThrow < 10 ? "0" : "") + firstThrow,
                            savedFrame.pinFall[0]);
                    bowlingGame.put("squareScore" + firstThrow,
                            savedFrame.squareScores[0]);
                    bowlingGame.put("standingPins" + (secondThrow < 10 ? "0" : "") + secondThrow,
                            savedFrame.pinFall[1]);
                    bowlingGame.put("squareScore" + secondThrow,
                            savedFrame.squareScores[1]);

                    //10th Frame Case
                    if (savedFrame.pinFall.length == 3) {
                        int thirdThrow = savedFrame.frameNumber * 2 + 1;
                        bowlingGame.put("standingPins" + thirdThrow,
                                savedFrame.pinFall[2]);
                        bowlingGame.put("squareScore" + thirdThrow,
                                savedFrame.squareScores[2]);
                    }
//                                    savedFrame = null;
                }

                int previous = 0;
                for (int i = 1; i <= 21; i++) {
                    int pins = bowlingGame
                            .getInt("standingPins"
                                    + (i < 10 ? "0" : "") + i);
                    if (pins == -1) {
                        if ((i & 1) == 0 && previous == 0) {
                            pins = 0;
                        } else {
                            pins = 1023;
                        }
                    }
                    pinFallMap.put(i, pins);
                    pinFallPreviousState.put(i, pins);
                    previous = pins;
                }
//                                maxFrameBowled = 0;
                for (int i = 1; i <= 21; i++) {
//                                    int pins = bowlingGame
//                                            .getInt("standingPins"
//                                                    + (i < 10 ? "0" : "") + i);
//                                    if (pins == -1) {
//                                        pins = 1023;
//                                    }
//                                    pinFallMap.put(i, pins);
//                                    pinFallPreviousState.put(i, pins);

                    TextView throwScore = (TextView) frames
                            .findViewById(CommonUtil
                                    .getIdFromName("throw" + i,
                                            R.id.class));
                    throwScore.setText(bowlingGame
                            .getString("squareScore" + i));
                    if (!bowlingGame.getString(
                            "squareScore" + i).isEmpty()
                            && i > maxSquareBowled) {
                        if (i == 1) {
                            maxFrameBowled = 0;
                        }
                        maxSquareBowled = i;
                        if (false || (maxSquareBowled > maxFrameBowled * 2)) {
                            int temp = maxFrameBowled + 1;
                            if (temp > 10) {
                                temp = 10;
                            }
                            changeFrame(findViewById(CommonUtil
                                    .getIdFromName(
                                            "frame"
                                                    + temp,
                                            R.id.class)));
                        }
                    }
                }
                // if (maxFrameBowled < 10) {
                // changeFrame(findViewById(CommonUtil
                // .getIdFromName("frame"
                // + (maxFrameBowled + 1),
                // R.id.class)));
                // }

            }
            updateScore = true;

            // TextView rawHandiScore = (TextView) frames
            // .findViewById(R.id.rawHandiScore);
            // rawHandiScore.setText(bowlingGame
            // .getString("finalScore")
            // + "/"
            // + bowlingGame.getString("handicapScore"));

            // Update second square value for last frame
            // entered.
            if (latestFrameNumber != 0) {
                TextView throwScore = (TextView) frames.findViewById(CommonUtil
                        .getIdFromName("throw"
                                        + latestFrameNumber * 2,
                                R.id.class));
                throwScore.setText(bowlingGame
                        .getString("squareScore"
                                + latestFrameNumber * 2));
            }

            for (int i = 1; i <= 10; i++) {
                TextView frameScore = (TextView) frames
                        .findViewById(CommonUtil.getIdFromName(
                                "frameScore" + i, R.id.class));
                frameScore.setText(bowlingGame
                        .getString("frameScore" + i));
            }
            totalScore.setText(bowlingGame
                    .getString("finalScore"));

            lastSquare = latestSquareNumber;

            if (bowlingGame.getBoolean("isComplete") && !getScoringType().equalsIgnoreCase("History")) {
                if (!gameCompleted) {
                    gameCompleted = true;
                    challengeSummary(null);
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                        fetchStats((ListView) findViewById(R.id.summaryList));
                    } else {
                        walletCall(this, venueId);
                        new AlertDialog.Builder(GameScreen.this)
                                .setTitle("Game Completed")
                                .setCancelable(false)
                                .setMessage(
                                        "Congratulations.You have scored "
                                                + bowlingGame
                                                .getString("finalScore")
                                                + ".")
                                .setPositiveButton(
                                        android.R.string.yes,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                    }
                }
            }

            for (int i = 1; i <= 21; i++) {
                if (!bowlingGame.getString("squareScore" + i)
                        .isEmpty() && i > maxSquareBowled) {
                    maxSquareBowled = i;
                }
            }

            // getUserCredit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (getScoringType().equalsIgnoreCase("History")) {
            finish();
        } else {
            Intent intent = new Intent(this, ScreenMain.class);
            startActivity(intent);
            finish();
        }
    }

    private void getTags(final boolean showInScroll) {
//        tags.clear();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(Data.baseUrl + "Tags/TagList?GameId=" + bowlingGameId + "&token="
                        + CommonUtil.getAccessToken(getApplicationContext())
                        .replaceAll("[+]", "%2B") + "&apiKey="
                        + Data.apiKey,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        try {
                            tags.clear();
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i < array.length(); i++) {
                                tags.add(array.getJSONObject(i).getString("tag").trim());
                            }
                            checkTags();
                            if (showInScroll) {
                                List<String[]> tags1 = new ArrayList<String[]>();
                                ListView tagsList = (ListView) findViewById(R.id.tagsListView);
                                tagsList.setVisibility(View.VISIBLE);
                                int columnCount;
                                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                    columnCount = 2;
                                } else {
                                    columnCount = 4;
                                }
                                int q = tags.size() / columnCount;
                                int r = tags.size() % columnCount;
                                for (int i = 0; i < q; i++) {
                                    String[] str = new String[columnCount];
                                    for (int j = 0; j < columnCount; j++) {
                                        str[j] = tags.get(columnCount * i + j);
                                    }
                                    tags1.add(str);
                                }
                                if (r != 0) {
                                    String[] str = new String[columnCount];
                                    for (int i = 0; i < columnCount; i++) {
                                        str[i] = "";
                                    }
                                    for (int i = 0; i < r; i++) {
                                        str[i] = tags.get(columnCount * q + i);
                                    }
                                    tags1.add(str);
                                }
                                tagsList.setAdapter(new TagAdapter(tags1, columnCount));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {

                        CommonUtil.loading_box_stop();
                    }
                });
    }

    class TagAdapter extends BaseAdapter {

        List<String[]> tags;
        int columnCount;

        public TagAdapter(List<String[]> tags, int columnCount) {
            this.tags = tags;
            this.columnCount = columnCount;
        }

        @Override
        public int getCount() {
            return tags.size();
        }

        @Override
        public Object getItem(int position) {
            return tags.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        class ViewHolder {
            TextView[] tags = new TextView[columnCount];
            LinearLayout rlt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final String[] data = tags.get(position);
            final ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) GameScreen.this
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(orientation == Configuration.ORIENTATION_PORTRAIT ?
                        R.layout.tags_item_portrait : R.layout.tags_item_landscape, null);
                holder = new ViewHolder();
                for (int i = 0; i < columnCount; i++) {
                    holder.tags[i] = (TextView) convertView.findViewById(CommonUtil.getIdFromName("tag" + (i + 1),
                            R.id.class));
                }
                holder.rlt = (LinearLayout) convertView.findViewById(R.id.root);
                holder.rlt.setLayoutParams(new AbsListView.LayoutParams(
                        AbsListView.LayoutParams.MATCH_PARENT, 60));
                holder.rlt.setTag(holder);
                ASSL.DoMagic(holder.rlt);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            for (int i = 0; i < data.length; i++) {
                if (data[i].isEmpty()) {
                    holder.tags[i].setVisibility(View.GONE);
                } else {
                    holder.tags[i].setVisibility(View.VISIBLE);
                    holder.tags[i].setText(data[i]);
                }
            }
            return convertView;
        }
    }

    private void getChallengers() {
        Log.v("func", "getChallengers");
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.get(
                Data.baseUrl
                        + "bowlinggame/"
                        + bowlingGameId
                        + "/challengers?token="
                        + CommonUtil.getAccessToken(getApplicationContext())
                        .replaceAll("[+]", "%2B") + "&apiKey="
                        + Data.apiKey, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            Data.postedEntered = (jsonArray.length() != 0);
                            if (orientation == Configuration.ORIENTATION_PORTRAIT && Data.postedEntered) {
                                findViewById(R.id.backgroundLayout).setBackgroundResource(R.drawable.challenge_screen_bg);
                                findViewById(R.id.h2hView).setVisibility(View.VISIBLE);
                                ((TextView) findViewById(R.id.challengeText)).setText("View Challenges");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                    }
                });
    }

    public void challengeSummary(View view) {

        CommonUtil.loading_box(this, "Please wait...");
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.get(
                Data.baseUrl
                        + "bowlingchallenge/results?gameId="
                        + bowlingGameId
                        + "&token="
                        + CommonUtil.getAccessToken(getApplicationContext())
                        .replaceAll("[+]", "%2B") + "&apiKey="
                        + Data.apiKey, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        try {
                            CommonUtil.loading_box_stop();

                            boolean challengeEntered = false;
                            if (Data.gameData.liveGameId != null
                                    && !Data.gameData.liveGameId.isEmpty()) {
                                findViewById(
                                        R.id.liveChallengeParent)
                                        .setVisibility(View.VISIBLE);
                                challengeEntered = true;
                            }
                            if (Data.postedEntered) {
                                findViewById(
                                        R.id.postedChallengeParent)
                                        .setVisibility(View.VISIBLE);
                                challengeEntered = true;
                            }

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                String challengeName = jsonArray
                                        .getJSONObject(i)
                                        .getJSONObject("challenge")
                                        .getString("name");
                                String challengeState = jsonArray
                                        .getJSONObject(i).getString(
                                                "challengeState");
                                if (challengeName.startsWith("Live")) {
                                    findViewById(
                                            R.id.liveChallengeParent)
                                            .setVisibility(View.VISIBLE);
                                    TextView xText = (TextView) findViewById(R.id.liveProgress);
                                    String competitionState = jsonArray
                                            .getJSONObject(i).getString(
                                                    "competitionGameState");
                                    if (competitionState
                                            .equalsIgnoreCase("Won")) {
                                        xText.setText("You won "
                                                + jsonArray.getJSONObject(i)
                                                .getString(
                                                        "totalPoints")
                                                + " points");
                                    } else if (competitionState
                                            .equalsIgnoreCase("Lost")) {
                                        xText.setText("You lost.Better luck next time.");
                                    }
                                } else if (challengeName.startsWith("Posted")) {
                                    findViewById(
                                            R.id.postedChallengeParent)
                                            .setVisibility(View.VISIBLE);
                                    String competitionState = jsonArray
                                            .getJSONObject(i).getString(
                                                    "competitionGameState");
                                    TextView xText = (TextView) findViewById(R.id.postedProgress);
                                    if (competitionState
                                            .equalsIgnoreCase("Won")) {
                                        xText.setText("You won "
                                                + jsonArray.getJSONObject(i)
                                                .getString(
                                                        "totalPoints")
                                                + " points");
                                    } else if (competitionState
                                            .equalsIgnoreCase("Lost")) {
                                        xText.setText("You lost.Better luck next time.");
                                    }
                                }
                            }

                            findViewById(
                                    R.id.noChallengeText).setVisibility(
                                    (!challengeEntered) ? View.VISIBLE : View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.loading_box_stop();
                        CommonUtil.commonGameErrorDialog(GameScreen.this,
                                "An error occured. Please try again.");
                    }
                });
    }

    public void pocketBrookClick(View view) {

        switch (view.getId()) {

            case R.id.pocket: {
                if (!trackPocket) {
                    return;
                }

                int frame = currentFrame;
                if (frame == 10) {
                    String text = "";//pocketBrookText.getText().toString();
                    // if(text.equals("1st Ball : ")){
                    // frame = 10;
                    // } else if(text.equals("1st & 2nd Ball : ")){
                    // frame = 10;
                    // } else
                    if (ballNumber == 2) {
                        if (pinFallMap.get(19) == 0) {
                            frame = 11;
                        } else {
                            frame = 10;
                        }
                    } else if (ballNumber == 3) {
                        if (pinFallMap.get(20) == 0) {
                            frame = 12;
                        } else {
                            frame = 11;
                        }
                    }
                }

                if (pocket.getTag().equals("off")) {
                    pocket.setBackgroundResource(R.drawable.blue_normal);
                    pocket.setTag("on");
                    brook.setTag("off");
                    pocketBrookMap.put(frame, 2);
                    brook.setBackgroundResource(R.drawable.black_round);
                } else {
                    pocket.setBackgroundResource(R.drawable.black_round);
                    pocket.setTag("off");
                    pocketBrookMap.put(frame, 0);
                }
                break;
            }

            case R.id.brooklyn:
                if (!trackPocket) {
                    return;
                }
                int frame = currentFrame;
                if (frame == 10) {
                    String text = "";//pocketBrookText.getText().toString();
                    // if(text.equals("1st Ball : ")){
                    // frame = 10;
                    // } else if(text.equals("1st & 2nd Ball : ")){
                    // frame = 10;
                    // } else

                    if (ballNumber == 2) {
                        if (pinFallMap.get(19) == 0) {
                            frame = 11;
                        } else {
                            frame = 10;
                        }
                    } else if (ballNumber == 3) {
                        if (pinFallMap.get(20) == 0) {
                            frame = 12;
                        } else {
                            frame = 11;
                        }
                    }

//                    if (text.equals("2nd Ball : ")) {
//                        frame = 11;
//                    } else if (text.equals("2nd & 3rd Ball : ")) {
//                        frame = 11;
//                    } else if (text.equals("3rd Ball : ")) {
//                        frame = 12;
//                    }
                }

                if (brook.getTag().equals("off")) {
                    brook.setBackgroundResource(R.drawable.blue_normal);
                    brook.setTag("on");
                    pocketBrookMap.put(frame, 1);
                    pocket.setTag("off");
                    pocket.setBackgroundResource(R.drawable.black_round);
                } else {
                    brook.setBackgroundResource(R.drawable.black_round);
                    brook.setTag("off");
                    pocketBrookMap.put(frame, 0);
                }
                break;

        }
    }

    private void updateThrowStat(String ballId, int[] frameNo) {

        StringEntity entity = null;
        try {
            JSONObject venue = new JSONObject();
            venue.put("BowlingGameId", bowlingGameId);

            JSONArray array = new JSONArray();
            for (int i = 0; i < frameNo.length; i++) {
                JSONObject json = new JSONObject();
                json.put("FrameNo", frameNo[i]);
                json.put("BowlingBallNameId", ballId);
                json.put("isBrooklynOrPocket", pocketBrookMap.get(frameNo[i]));
                array.put(json);
            }
            venue.put("BowlingThrowUserStatDataList", array);
            entity = new StringEntity(venue.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("Call", " Create Throw");
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.post(getApplicationContext(), Data.baseUrl
                        + "UserStat/CreateBowlingThrowUserStat?token="
                        + CommonUtil.getAccessToken(getApplicationContext())
                        .replaceAll("[+]", "%2B") + "&apiKey=" + Data.apiKey,
                entity, "application/json", new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        if (gameCompleted) {
                            gameView(false);
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                    }
                });
    }

    private int getPocketCode() {
        Button brook = (Button) findViewById(R.id.brooklyn);
        Button pocket = (Button) findViewById(R.id.pocket);
        int code = 0;
        if (pocket.getTag().equals("on")) {
            code = 2;
        } else if (brook.getTag().equals("on")) {
            code = 1;
        }
        return code;
    }

    private void createThrow(int frameToDisplay, int code) {
        if (Data.userStatsSubscribed) {
            boolean changed = false;

//            if (pocketBrookMap.get(frameToDisplay) != null) {
//                if (pocketBrookMap.get(frameToDisplay) != code) {
//                    changed = true;
//                    pocketBrookMap.put(frameToDisplay, code);
//                }
//            }
            changed = true;

            int[] frameNums;
            if (frameToDisplay == 10) {

                /////
                if (ballNumber == 1 && pinFallMap.get(19) == 0) {
                    if (throw3.getText().toString().isEmpty()) {
                        lastFrameStates = 4;
                    } else {
                        lastFrameStates = 1;
                    }
                } else if (ballNumber != 3 && pinFallMap.get(19) != 0) {
                    if (throw3.getText().toString().isEmpty()) {
                        lastFrameStates = 4;
                    } else {
                        lastFrameStates = 2;
                    }
                } else if (ballNumber == 2 && pinFallMap.get(19) == 0) {
                    if (throw3.getText().toString().isEmpty()) {
                        lastFrameStates = 4;
                    } else {
                        lastFrameStates = 1;
                    }
                } else if (ballNumber != 1 && pinFallMap.get(20) != 0) {
                    lastFrameStates = 3;
                } else if (ballNumber == 3 && pinFallMap.get(20) == 0) {
                    if (throw2.getText().toString().equals("/")) {
                        lastFrameStates = 2;
                    } else if (throw2.getText().toString().equals("X")) {
                        lastFrameStates = 1;
                    }
                }
                ////

                if (lastFrameStates == 1) {
                    frameNums = new int[]{10, 11, 12};
                } else if (lastFrameStates == 4) {
                    frameNums = new int[]{10};
                } else if (lastFrameStates == 2) {
                    pocketBrookMap.put(11, pocketBrookMap.get(12));
                    frameNums = new int[]{10, 11};
                } else {
                    frameNums = new int[]{10, 11};
                }

            } else {
                frameNums = new int[]{frameToDisplay};
            }
            if (ballName.getSelectedItem() == null) {
                if (changed) {
                    updateThrowStat(ballNameMap.get(frameToDisplay), frameNums);
                }
            } else {
//                String newId = currentBallId;
//                if (!ballNameMap.get(frameToDisplay).equals(newId)) {
//                    changed = true;
//                    ballNameMap.put(frameToDisplay, newId);
//                }
                if (changed) {
                    updateThrowStat(ballNameMap.get(frameToDisplay), frameNums);
                }
            }

        }
    }

    private void getPocketBrooklyn() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.get(Data.baseUrl
                        + "UserStat/getframe?token="
                        + CommonUtil.getAccessToken(getApplicationContext())
                        .replaceAll("[+]", "%2B") + "&apiKey=" + Data.apiKey
                        + "&bowlinggameId=" + bowlingGameId,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            JSONObject object;
                            for (int i = 0; i < array.length(); i++) {
                                object = array.getJSONObject(i);
                                pocketBrookMap.put(object.getInt("frameNo"),
                                        object.getInt("isBrooklynOrPocket"));
                                if (!object.isNull("bowlingBallNames")) {

                                    ballNameMap.put(
                                            object.getInt("frameNo"),
                                            object.getJSONObject(
                                                    "bowlingBallNames")
                                                    .getString("id"));
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    private void getUserSettings(final JSONArray array) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(
                Data.baseUrl
                        + "UserStat/UserStatSettingsList?token="
                        + CommonUtil.getAccessToken(GameScreen.this)
                        .replaceAll("[+]", "%2B") + "&apiKey="
                        + Data.apiKey, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        try {
                            if (response == null || response.equals("null")) {
                                trackBallType = true;
                                trackPocket = true;
                                return;
                            }
                            JSONObject obj = new JSONObject(response);
                            trackBallType = obj.getBoolean("ballType");
                            trackPocket = obj.getBoolean("pocketPercentage");

                            ballName.setEnabled(trackBallType);
                            ballName.setClickable(trackBallType);
                            // fetchBowlingNameLists();

                            if (trackBallType) {

                                final List<String[]> ballList = new ArrayList<String[]>();
                                ballList.add(new String[]{"0", "Ball Name"});
                                for (int i = 0; i < array.length(); i++) {
                                    ballList.add(new String[]{
                                            array.getJSONObject(i).getString(
                                                    "id"),
                                            array.getJSONObject(i).getString(
                                                    "userBowlingBallName")});
                                }

                                bowlNameAdapter = new UserStatsAdapter<String[]>(
                                        getApplicationContext(),
                                        android.R.layout.simple_spinner_item,
                                        ballList);
                                ballName.setAdapter(bowlNameAdapter);
                                bowlNameAdapter
                                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                ballName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    public void onItemSelected(
                                            AdapterView<?> parent, View view,
                                            int pos, long id) {
                                        currentBallId = ((String[]) ballName
                                                .getSelectedItem())[0];
                                        ballNameMap.put(currentFrame, ((String[]) ballName
                                                .getSelectedItem())[0]);
                                        ((TextView) findViewById(R.id.ballNameText))
                                                .setText(((String[]) ballName
                                                        .getSelectedItem())[1]);
                                    }

                                    public void onNothingSelected(
                                            AdapterView<?> parent) {
                                    }
                                });

                                String id = getIntent().getStringExtra(
                                        "ballName");
                                if (!(id == null || id.isEmpty())) {

                                    for (int i = 1; i <= 10; i++) {
                                        ballNameMap.put(i, id);
                                    }
                                }
                            } else {
                                ((TextView) findViewById(R.id.ballNameText))
                                        .setTextColor(Color.GRAY);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.v("exception ", e.toString() + "");
                        CommonUtil.commonGameErrorDialog(GameScreen.this,
                                "An error occured. Please try again.");
                        CommonUtil.loading_box_stop();

                    }
                });
    }

    private void fetchCommonLists() {

        if (!CommonUtil.is_loading_showing())
            CommonUtil.loading_box(GameScreen.this, "Please wait...");

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(Data.baseUrl
                        + "UserStat/CommonStandards?token="
                        + CommonUtil.getAccessToken(getApplicationContext())
                        .replaceAll("[+]", "%2B") + "&apiKey=" + Data.apiKey,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        try {
                            if (new JSONObject(response)
                                    .getBoolean("subcriptionStatus")) {
                                Data.userStatsSubscribed = true;
                                findViewById(R.id.strikeSpareParentPro).setVisibility(View.VISIBLE);
                                findViewById(R.id.strikeSpareParent).setVisibility(View.GONE);

                                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                    findViewById(R.id.ballNameParent).setVisibility(View.VISIBLE);
                                    findViewById(R.id.pocket).setVisibility(View.VISIBLE);
                                    findViewById(R.id.brooklyn).setVisibility(View.VISIBLE);
                                    findViewById(R.id.demoSpaceView).setVisibility(View.GONE);
                                }

                                strike = (Button) findViewById(R.id.strikePro);
                                spare = (Button) findViewById(R.id.sparePro);

                                JSONObject main = new JSONObject(response)
                                        .getJSONObject("commonStatsStandards");
                                JSONArray array = main
                                        .getJSONArray("bowlingBallNames");
                                getUserSettings(array);
                                getPocketBrooklyn();
                            } else {
                                spareStrikeNormal();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            spareStrikeNormal();
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.v("exception ", e.toString() + "");
                        CommonUtil.commonGameErrorDialog(GameScreen.this,
                                "An error occured. Please try again.");
                        CommonUtil.loading_box_stop();
                        spareStrikeNormal();
                    }
                });
    }

    private void spareStrikeNormal() {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            findViewById(R.id.strikeSpareParentPro).setVisibility(View.GONE);
        } else {
            findViewById(R.id.ballNameParent).setVisibility(View.GONE);
            findViewById(R.id.pocket).setVisibility(View.GONE);
            findViewById(R.id.brooklyn).setVisibility(View.GONE);
            findViewById(R.id.demoSpaceView).setVisibility(View.VISIBLE);

//            ViewGroup challengeParent = (ViewGroup) findViewById(R.id.challengeParent);
//            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) challengeParent.getLayoutParams();
//            params.setMargins(0, (int) (80 * ASSL.Yscale()), 0, 0);
//            challengeParent.setLayoutParams(params);
        }
        findViewById(R.id.strikeSpareParent).setVisibility(View.VISIBLE);
        strike = (Button) findViewById(R.id.strike);
        spare = (Button) findViewById(R.id.spare);
    }
}

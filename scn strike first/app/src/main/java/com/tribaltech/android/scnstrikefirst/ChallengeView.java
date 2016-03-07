package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tribaltech.android.util.AppConstants;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;
import com.tribaltech.android.util.DbController;
import com.tribaltech.android.util.PostedOpponentDetails;

import org.apache.http.client.HttpResponseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rmn.androidscreenlibrary.ASSL;

public class ChallengeView extends Activity {

    LinearLayout frameParent;
    PostedOpponentDetails details;
    LayoutInflater inflater;
    Runnable updatePostedScore;
    Runnable runnable;
    Handler handler;
    View frames;
    View oppFrames;
    boolean gameCompleted;
    List<Integer> maxSquareBowled = new ArrayList<>();
    List<Integer> maxFrameBowled = new ArrayList<>();
    List<int[]> frameStates = new ArrayList<>();
    List<Integer> currentFrame = new ArrayList<>();
    //    Game gameData;
    boolean h2hLive;
    LinearLayout liveParent;
    public static final int REQ_CODE_LIVE = 100;
    String liveCompetitionId;
    Runnable updateLiveChallengers;
    String liveGameState;
    Button createGame;
    Button joinGame;
    String bowlingGameId;
    TextView challengeType;
    Set<String> visitedBowlers = new HashSet<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_view);
        new ASSL(this, (ViewGroup) findViewById(R.id.root), 1134,
                720, false);
        challengeType = (TextView) findViewById(R.id.challengeType);
        liveParent = (LinearLayout) findViewById(R.id.liveParent);
        createGame = (Button) findViewById(R.id.createGame);
        joinGame = (Button) findViewById(R.id.joinGame);

        inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < 2; i++) {
            currentFrame.add(1);
            maxSquareBowled.add(0);
            maxFrameBowled.add(0);
            frameStates.add(new int[11]);
        }
        handler = new Handler();
        updatePostedScore = new Runnable() {
            @Override
            public void run() {
                getOpponentScore(details, oppFrames);
                handler.postDelayed(this, 20000);
            }
        };

        updateLiveChallengers = new Runnable() {

            @Override
            public void run() {
                getLiveChallengers(liveCompetitionId);
                handler.postDelayed(this, 20000);
            }
        };

        runnable = new Runnable() {
            @Override
            public void run() {
                automaticGameView();
                handler.postDelayed(this, 20000);
            }
        };

        frameParent = (LinearLayout) findViewById(R.id.frameParent);
        if (getIntent().hasExtra("postedOpp")) {
            details = (PostedOpponentDetails) getIntent().getSerializableExtra("postedOpp");
            frames = createFrame();
            frameParent.addView(frames);
            oppFrames = createFrame();
            frameParent.addView(oppFrames);
            challengeType.setText("H2H Posted");
            findViewById(R.id.myGame).setVisibility(View.VISIBLE);
        } else {
            liveCompetitionId = Data.gameData.liveGameId;
            bowlingGameId = Data.gameData.gameId;
            h2hLive = true;
            Data.lastChallengeVisited = AppConstants.H2H_LIVE;
            if (liveCompetitionId != null && !liveCompetitionId.isEmpty()) {
                handler.post(updateLiveChallengers);
            } else {
                liveParent.setVisibility(View.VISIBLE);
                findViewById(R.id.myGame).setVisibility(View.GONE);
            }
            challengeType.setText("H2H Live");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!h2hLive) {
            if (!Data.gameData.centerType.equalsIgnoreCase("Manual")) {
                handler.removeCallbacksAndMessages(null);
                handler.post(runnable);
                handler.post(updatePostedScore);
            } else {
                gameView(true);
                getOpponentScore(details, frameParent.getChildAt(1));
            }
        } else {
            if (Data.gameData.liveGameId != null && !Data.gameData.liveGameId.isEmpty()) {
                handler.removeCallbacksAndMessages(null);
                handler.post(updateLiveChallengers);
            }
        }

    }

    private View createFrame() {
        View frames = inflater.inflate(R.layout.challenge_item, null);
        frames.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 285));
        ASSL.DoMagic(frames);
        return frames;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.myGame: {
                Intent intent = new Intent(this, GameScreen.class);
                startActivity(intent);
                finish();
            }
            break;

            case R.id.back: {
                Intent intent;
                if (h2hLive) {
                    intent = new Intent(this, Challenges.class);
                } else {
                    intent = new Intent(this, H2HPostedMain.class);
                }
                startActivity(intent);
                finish();
            }
            break;

            case R.id.createGame: {
                Intent intent = new Intent(this, H2HLiveCreate.class);
                intent.putExtra("bowlingGameId", bowlingGameId);
//                intent.putExtra("game", getIntent().getSerializableExtra("Data.gameData"));
                startActivityForResult(intent, REQ_CODE_LIVE);
            }
            break;

            case R.id.joinGame: {
                Intent intent = new Intent(this, ChooseOpponent.class);
                intent.putExtra("type", "live");
//                intent.putExtra("bowlingGameId", bowlingGameId);
                startActivityForResult(intent, REQ_CODE_LIVE);
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_LIVE) {
            if (resultCode == RESULT_OK) {
                liveCompetitionId = data.getStringExtra("liveCompId");
                Data.gameData.liveGameId = liveCompetitionId;
                handler.post(updateLiveChallengers);
                findViewById(R.id.myGame).setVisibility(View.VISIBLE);
            }
        }
    }

    private void getOpponentScore(PostedOpponentDetails details,
                                  final View frames) {
        Log.v("func", "getOppScore");
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.get(Data.baseUrl
                        + "bowlinggame/"
                        + Data.gameData.gameId
                        + "/challengers/posted/"
                        + details.competitionId
                        + "/bowlinggame/"
                        + details.bowlingGameId
                        + "?token="
                        + CommonUtil.getAccessToken(getApplicationContext())
                        .replaceAll("[+]", "%2B") + "&apiKey=" + Data.apiKey,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        try {

                            CommonUtil.loading_box_stop();
                            JSONObject bowlingGame = new JSONObject(response);
//                            TextView myHandicapScore = (TextView) GameScreen.this.frames
//                                    .findViewById(R.id.rawHandiScore);
                            int opponentScore = 0;
                            int myScore = 0;
                            try {
                                opponentScore = Integer.parseInt(bowlingGame
                                        .getString("handicapScore"));
//                                myScore = Integer.parseInt(myHandicapScore
//                                        .getText().toString().split("/")[1]);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                            TextView rawScore = (TextView) frames
                                    .findViewById(R.id.rawScore);
                            rawScore.setText(bowlingGame
                                    .getString("finalScore"));
                            TextView handiScore = (TextView) frames
                                    .findViewById(R.id.handicapScore);
                            handiScore.setText(bowlingGame
                                    .getString("handicapScore"));

                            TextView bowlerName = (TextView) frames
                                    .findViewById(R.id.bowlerName);
                            bowlerName.setText(bowlingGame.getString("name"));

                            updateScore(bowlingGame, frameParent.getChildAt(1), false, 1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.commonGameErrorDialog(ChallengeView.this,
                                "An error occured. Please try again.");
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
                        + Data.gameData.checkoutId
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
                            TextView rawScore = (TextView) frames
                                    .findViewById(R.id.rawScore);
                            rawScore.setText(bowlingGame
                                    .getString("finalScore"));
                            TextView handiScore = (TextView) frames
                                    .findViewById(R.id.handicapScore);
                            handiScore.setText(bowlingGame
                                    .getString("handicapScore"));

                            TextView bowlerName = (TextView) frames
                                    .findViewById(R.id.bowlerName);
                            bowlerName.setText(bowlingGame.getString("name"));
//                            TextView rawHandiScore = (TextView) frames
//                                    .findViewById(R.id.rawHandiScore);
//                            rawHandiScore.setText(bowlingGame
//                                    .getString("finalScore")
//                                    + "/"
//                                    + bowlingGame.getString("handicapScore"));
                            updateScore(bowlingGame, frames, true, 0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {

                    }
                });
    }

    private void updateScore(JSONObject bowlingGame, View frames,
                             boolean checkGameCompletion, int index) {
        try {
            for (int i = 1; i <= 21; i++) {
                TextView throwScore = (TextView) frames.findViewById(CommonUtil
                        .getIdFromName("throw" + i, R.id.class));
                throwScore.setText(bowlingGame.getString("squareScore" + i));
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

                if (!bowlingGame.getString("squareScore" + i).isEmpty()
                        && i > maxSquareBowled.get(index)) {
                    maxSquareBowled.set(index, i);

                    if (maxSquareBowled.get(index) > maxFrameBowled.get(index) * 2) {
                        int temp = maxFrameBowled.get(index) + 1;
                        if (temp > 10) {
                            temp = 10;
                        }
                        changeFrame(frames.findViewById(CommonUtil.getIdFromName(
                                "frame" + temp, R.id.class)), frames, index);
                    }
                }
            }

            for (int i = 1; i <= 10; i++) {
                TextView frameScore = (TextView) frames.findViewById(CommonUtil
                        .getIdFromName("frameScore" + i, R.id.class));

                frameScore.setText(bowlingGame.getString("frameScore" + i));
                if (checkGameCompletion) {
                    if (!bowlingGame.getString("frameScore" + i).isEmpty()
                            && i > maxSquareBowled.get(index)) {
                        maxSquareBowled.set(index, i);
                    }
                }
            }
            if (checkGameCompletion) {
                if (bowlingGame.getBoolean("isComplete")) {
                    if (!gameCompleted) {
                        gameCompleted = true;

                        new AlertDialog.Builder(ChallengeView.this)
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeFrame(View view) {
        return;
//        changeFrame(view, view.getRootView(), (int) ((View) view.getParent()).getTag(KEY_FRAME_INDEX));
    }

    public void changeFrame(View view, View frames, int index) {

        ViewGroup frameParent = (ViewGroup) view;
        int frameNo = Integer.parseInt(frameParent.getChildAt(1).getTag()
                .toString());

        if (frameNo > maxFrameBowled.get(index) + 1) {
            return;
        } else if (frameNo > maxFrameBowled.get(index)) {
            maxFrameBowled.set(index, frameNo);
        }

        if (frameStates.get(index)[currentFrame.get(index)] == AppConstants.SELECTED) {
            frameStates.get(index)[currentFrame.get(index)] = AppConstants.BOWLED;
            // Change to Bowled
            toggleFrameState((ViewGroup) frames.findViewById(CommonUtil
                            .getIdFromName("frame" + currentFrame.get(index), R.id.class)), R.drawable.bowled_background, R.drawable.bowled_background,
                    R.drawable.bowled_background,
                    AppConstants.BOWLED_TEXT_COLOR);
        }
        currentFrame.set(index, frameNo);
        frameStates.get(index)[currentFrame.get(index)] = AppConstants.SELECTED;
        // Change to selected
        toggleFrameState(frameParent, R.drawable.selected_background, R.drawable.selected_background,
                R.drawable.selected_background,
                AppConstants.SELECTED_TEXT_COLOR);
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
                        + Data.gameData.checkoutId
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

                            if (updateThrows) {

                                for (int i = 1; i <= 21; i++) {
                                    TextView throwScore = (TextView) frames
                                            .findViewById(CommonUtil
                                                    .getIdFromName("throw" + i,
                                                            R.id.class));
                                    throwScore.setText(bowlingGame
                                            .getString("squareScore" + i));
                                    if (!bowlingGame.getString(
                                            "squareScore" + i).isEmpty()
                                            && i > maxSquareBowled.get(0)) {
                                        maxSquareBowled.set(0, i);
                                        if (maxSquareBowled.get(0) > maxFrameBowled.get(0) * 2) {
                                            int temp = maxFrameBowled.get(0) + 1;
                                            if (temp > 10) {
                                                temp = 10;
                                            }
                                            changeFrame(findViewById(CommonUtil
                                                    .getIdFromName(
                                                            "frame"
                                                                    + temp,
                                                            R.id.class)), frames, 0);
                                        }
                                    }
                                }
                            }
                            TextView bowlerNametxt = (TextView) frames
                                    .findViewById(R.id.bowlerName);
                            bowlerNametxt.setText(bowlingGame.getString("name"));
                            TextView rawScore = (TextView) frames
                                    .findViewById(R.id.rawScore);
                            rawScore.setText(bowlingGame
                                    .getString("finalScore"));
                            TextView handiScore = (TextView) frames
                                    .findViewById(R.id.handicapScore);
                            handiScore.setText(bowlingGame
                                    .getString("handicapScore"));

                            for (int i = 1; i <= 10; i++) {
                                TextView frameScore = (TextView) frames
                                        .findViewById(CommonUtil.getIdFromName(
                                                "frameScore" + i, R.id.class));
                                frameScore.setText(bowlingGame
                                        .getString("frameScore" + i));
                            }

                            if (bowlingGame.getBoolean("isComplete")) {
                                if (!gameCompleted) {
                                    gameCompleted = true;
                                    new AlertDialog.Builder(ChallengeView.this)
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

                            for (int i = 1; i <= 21; i++) {
                                if (!bowlingGame.getString("squareScore" + i)
                                        .isEmpty() && i > maxSquareBowled.get(0)) {
                                    maxSquareBowled.set(0, i);
                                }
                            }

                            // getUserCredit();
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

    private void getLiveChallengers(String liveCompId) {
        Log.v("func", "getLiveChallengers");
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.get(
                Data.baseUrl
                        + "bowlingcompetition/live/"
                        + liveCompId
                        + "/challengers?token="
                        + CommonUtil.getAccessToken(getApplicationContext())
                        .replaceAll("[+]", "%2B") + "&apiKey="
                        + Data.apiKey, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        try {
                            try {
                                if (new JSONObject(response)
                                        .has("waitingForApproval")) {
                                    liveGameState = "waiting";
                                    liveParentChange(liveGameState);
                                    return;
                                }
                            } catch (JSONException e) {
                            }

                            JSONArray gameArray = new JSONArray(response);

                            int noGames = gameArray.length();
                            int idx = -1;
                            boolean isHost = gameArray.getJSONObject(0)
                                    .getBoolean("isHost");
                            int myScore = 0;
                            int[] scoreArray = new int[noGames];
                            liveGameState = "entered";
                            liveParentChange(liveGameState);

                            for (int k = 0; k < noGames; k++) {
                                JSONObject json = gameArray.getJSONObject(k);
                                JSONObject bowlingGame = gameArray
                                        .getJSONObject(k).getJSONObject(
                                                "scoredGame");
                                String state = gameArray.getJSONObject(k)
                                        .getString("state");
                                String bowlerName = bowlingGame
                                        .getString("name");
                                if (state.equalsIgnoreCase("Pending") && isHost) {
                                    handler
                                            .removeCallbacksAndMessages(null);
                                    String gameId = gameArray.getJSONObject(k)
                                            .getString("bowlingGameId");
                                    if (!visitedBowlers.contains(gameId)) {
                                        visitedBowlers.add(gameId);
                                        askToJoin(gameArray.getJSONObject(k)
                                                        .getString("bowlingGameId"),
                                                bowlerName);
                                    }
                                } else if (state.equals("Entered")
                                        || state.equals("Won")) {
                                    JSONObject venue = gameArray.getJSONObject(
                                            k).getJSONObject("venue");
                                    View frames = frameParent.getChildAt(++idx);

                                    int scoreVal = 0;
                                    try {
                                        scoreVal = Integer.parseInt(bowlingGame
                                                .getString(json.getString(
                                                        "scoringMode").equals(
                                                        "Handicap") ? "handicapScore"
                                                        : "finalScore"));
                                    } catch (NumberFormatException e) {
                                    }
                                    scoreArray[idx] = scoreVal;

                                    if (frames == null) {
                                        frames = inflater.inflate(
                                                R.layout.challenge_item, null);
                                        frames.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                                                ViewGroup.LayoutParams.MATCH_PARENT, 285));
                                        ASSL.DoMagic(frames);
                                        frameParent.addView(frames, idx);
//                                        if (idx == 0) {
//                                            ViewGroup scoreRoot = (ViewGroup) frames
//                                                    .findViewById(R.id.root);
//                                            scoreRoot.setTag(true);
//                                        }
                                    }
                                    TextView bowlerNametxt = (TextView) frames
                                            .findViewById(R.id.bowlerName);
                                    bowlerNametxt.setText(bowlerName);
                                    TextView rawScore = (TextView) frames
                                            .findViewById(R.id.rawScore);
                                    rawScore.setText(bowlingGame
                                            .getString("finalScore"));
                                    TextView handiScore = (TextView) frames
                                            .findViewById(R.id.handicapScore);
                                    handiScore.setText(bowlingGame
                                            .getString("handicapScore"));
//                                    TextView center = (TextView) frames
//                                            .findViewById(R.id.centerName);
//                                    center.setText(venue.getString("name")
//                                            + " IN "
//                                            + venue.getJSONObject("address")
//                                            .getJSONObject(
//                                                    "administrativeArea")
//                                            .getString("shortName")
//                                            + " , "
//                                            + venue.getJSONObject("address")
//                                            .getJSONObject(
//                                                    "administrativeArea")
//                                            .getString("countryCode"));
//                                    TextView score = (TextView) frames
//                                            .findViewById(R.id.scoreLive);
//                                    score.setText((json
//                                            .getString("scoringMode").equals(
//                                                    "Handicap") ? "Handicap "
//                                            : "")
//                                            + "Score : " + scoreVal);
//
                                    updateScore(bowlingGame, frames, false, k);

                                }

                                if (state.equals("Won")) {
                                    Toast.makeText(ChallengeView.this, "You won the challenge.", Toast.LENGTH_LONG).show();
                                } else if (state.contains("los") || state.contains("Los")) {
                                    Toast.makeText(ChallengeView.this, "You lost the challenge.", Toast.LENGTH_LONG).show();
                                }
                            }

                            int childCount = frameParent.getChildCount();
                            for (int i = idx + 1; i < childCount; i++) {
                                frameParent.removeViewAt(i);
                            }

//                            childCount = frameParent.getChildCount();
//                            for (int i = 1; i < childCount; i++) {
//                                View frame = frameParent.getChildAt(i);
//                                TextView diffScore = (TextView) frame
//                                        .findViewById(R.id.differentialScore);
//                                if (scoreArray[i] < myScore) {
//                                    diffScore.setTextColor(Color.RED);
//                                    diffScore.setText("("
//                                            + (scoreArray[i] - myScore) + ")");
//                                } else if (scoreArray[i] > myScore) {
//                                    diffScore.setTextColor(Color.GREEN);
//                                    diffScore.setText("(+"
//                                            + (scoreArray[i] - myScore) + ")");
//                                } else {
//                                    diffScore.setText("");
//                                }
//
//                            }
                            scoreArray = null;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        int statusCode = 0;
                        if (e instanceof HttpResponseException) {
                            HttpResponseException hre = (HttpResponseException) e;
                            statusCode = hre.getStatusCode();
                        }
                        if ((e.getMessage() != null
                                && e.getMessage().equalsIgnoreCase(
                                "Unauthorized")) || statusCode == 500) {
                            Data.gameData.liveGameId = "";
                            liveCompetitionId = "";
                            DbController controller = new DbController(
                                    getApplicationContext());
                            controller.open();
                            controller.updateGame(CommonUtil
                                            .getScreenName(getApplicationContext()),
                                    liveCompetitionId);
                            controller.close();
                            handler.removeCallbacksAndMessages(null);
                            liveGameState = "rejected";
                            liveParentChange(liveGameState);
                        }
                    }
                });
    }

    private void askToJoin(final String id, final String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChallengeView.this);

        builder.setTitle("Confirm");
        builder.setMessage(name + " wants to join this game. Allow?");
        builder.setCancelable(false);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                updateGameState("Entered", id);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateGameState("Rejected", id);
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void updateGameState(final String state, String gameId) {
        RequestParams rv = new RequestParams();
        rv.put("state", state);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.post(Data.baseUrl
                        + "bowlingcompetition/live/"
                        + liveCompetitionId
                        + "/game/"
                        + gameId
                        + "/state?token="
                        + CommonUtil.getAccessToken(getApplicationContext())
                        .replaceAll("[+]", "%2B") + "&apiKey=" + Data.apiKey,
                rv, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        handler.post(updateLiveChallengers);
                        Toast.makeText(getApplicationContext(), state,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.commonGameErrorDialog(ChallengeView.this,
                                "An error occured. Please try again.");
                        handler.post(updateLiveChallengers);
                    }
                });
    }

    private void liveParentChange(String status) {
        TextView liveMsg = (TextView) liveParent
                .findViewById(R.id.liveMessage);
        if (status.equalsIgnoreCase("waiting")) {
            findViewById(R.id.myGame).setVisibility(View.GONE);
            liveMsg
                    .setText("Waiting for the Organizer to approve your game.");
            createGame.setVisibility(View.GONE);
            joinGame.setVisibility(View.GONE);
            liveParent.setVisibility(View.VISIBLE);
        } else if (status.equalsIgnoreCase("rejected")) {
            findViewById(R.id.myGame).setVisibility(View.GONE);
            liveMsg.setText("Oh no! The Organizer rejected your entry into the live game.");
            createGame.setVisibility(View.VISIBLE);
            joinGame.setVisibility(View.VISIBLE);
            liveParent.setVisibility(View.VISIBLE);
        } else if (status.equals("entered")) {
            findViewById(R.id.myGame).setVisibility(View.VISIBLE);
            liveParent.setVisibility(View.GONE);
            frameParent.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent;
        if (h2hLive) {
            intent = new Intent(this, Challenges.class);
        } else {
            intent = new Intent(this, H2HPostedMain.class);
        }
        startActivity(intent);
        finish();
    }
}

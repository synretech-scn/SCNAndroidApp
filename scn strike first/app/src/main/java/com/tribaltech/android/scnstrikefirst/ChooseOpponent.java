package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tribaltech.android.entities.CompetitionView;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;
import com.tribaltech.android.util.DbController;
import com.tribaltech.android.util.GameAdapter;
import com.tribaltech.android.util.LevelSelectAdapter;
import com.tribaltech.android.util.LiveGameAdapter;
import com.tribaltech.android.util.PostedGameAdapter;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rmn.androidscreenlibrary.ASSL;

public class ChooseOpponent extends Activity {

    LevelSelectAdapter levelAdapter;
    GameAdapter gameAdapter;
    String bowlingGameId;
    ListView competitionsList;
    TextView errorMsg;
    boolean friendsTabSelected;
    EditText search;
    Button allXBowlers;
    Button friendsBtn;
    String type;
    LinearLayout tabsParent;
    RelativeLayout balanceParent;
    RelativeLayout searchParent;
    TextView bottomLabel;
    TextView credits;
    TextView headerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_opponent);
        new ASSL(this, (ViewGroup) findViewById(R.id.root), 1134,
                720, false);
        headerText = (TextView) findViewById(R.id.headerText);
        searchParent = (RelativeLayout) findViewById(R.id.searchParent);
        credits = (TextView) findViewById(R.id.creditBalance);
        bottomLabel = (TextView) findViewById(R.id.bottomLabel);
        tabsParent = (LinearLayout) findViewById(R.id.tabs);
        balanceParent = (RelativeLayout) findViewById(R.id.balanceParent);
        search = (EditText) findViewById(R.id.searchBowler);
        errorMsg = (TextView) findViewById(R.id.errorMsgH2H);
        allXBowlers = (Button) findViewById(R.id.allxbowlerBtn);
        friendsBtn = (Button) findViewById(R.id.friendsBtn);
        competitionsList = (ListView) findViewById(R.id.competitionList);
        type = getIntent().getStringExtra("type");

        bowlingGameId = Data.gameData.gameId;
        if (type.equalsIgnoreCase("posted")) {
            gameAdapter = new PostedGameAdapter(getApplicationContext(),
                    new ArrayList<CompetitionView>());
            headerText.setText("H2H Posted");
        } else {
            gameAdapter = new LiveGameAdapter(getApplicationContext(),
                    new ArrayList<CompetitionView>());
            headerText.setText("H2H Live");
        }
        competitionsList.setAdapter(gameAdapter);
        friendsTabSelected = false;
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                fetchGamesData(friendsTabSelected, search.getText().toString(),
                        type);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search.getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);
                return false;
            }
        });
        fetchGamesData(false, "", type);
        getUserCredit();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.allxbowlerBtn:
                friendsBtn.setBackgroundResource(R.drawable.blue_right_outline_semi_round);
                allXBowlers.setBackgroundResource(R.drawable.blue_left_semi_round);
                friendsTabSelected = false;
                fetchGamesData(friendsTabSelected, search.getText().toString(),
                        type);
                break;

            case R.id.friendsBtn:
                friendsBtn.setBackgroundResource(R.drawable.blue_right_semi_round);
                allXBowlers.setBackgroundResource(R.drawable.blue_left_outline_semi_round);
                friendsTabSelected = true;
                fetchGamesData(friendsTabSelected, search.getText().toString(),
                        type);
                break;

            case R.id.addOpponent:
                if (bottomLabel.getText().toString().equalsIgnoreCase("enter")) {
                    if (levelAdapter.selectedIndex == -1) {
                        Toast.makeText(this,
                                "Please select a level",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        selectOpponent(gameAdapter.getCompetitions().get(gameAdapter.selectedIndex).id, type,
                                Integer.parseInt(levelAdapter.data[levelAdapter.selectedIndex][levelAdapter.CREDITS_INDEX]));
                    }
                } else {
                    if (gameAdapter.selectedIndex == -1) {
                        Toast.makeText(this,
                                "Please select a game",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        if (type.equals("live")) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
                            selectOpponent(
                                    gameAdapter.getCompetitions().get(
                                            gameAdapter.selectedIndex).id, type, 0);
                        } else {
                            levelSelectToggle(true);
                        }
                    }
                }
                break;

            case R.id.back:
                if (balanceParent.getVisibility() == View.VISIBLE) {
                    levelSelectToggle(false);
                } else {
                    finish();
                }
                break;
        }
    }

    private void levelSelectToggle(boolean showLevels) {
        if (showLevels) {
            tabsParent.setVisibility(View.GONE);
            searchParent.setVisibility(View.GONE);
            balanceParent.setVisibility(View.VISIBLE);
            if (levelAdapter == null) {
                levelAdapter = new LevelSelectAdapter(this);
            }
            competitionsList.setAdapter(levelAdapter);
            bottomLabel.setText("Enter");
        } else {
            tabsParent.setVisibility(View.VISIBLE);
            searchParent.setVisibility(View.VISIBLE);
            balanceParent.setVisibility(View.GONE);
            competitionsList.setAdapter(gameAdapter);
            bottomLabel.setText("Select Opponent");
        }
        findViewById(R.id.root).requestLayout();
    }

    private void selectOpponent(final String competitionId, final String type,
                                int credits) {
        StringEntity entity = null;
        try {
            JSONObject bowlingGame = new JSONObject();
            bowlingGame.put("id", bowlingGameId);

            JSONObject json = new JSONObject();
            json.put("bowlingGame", bowlingGame);
            json.put("creditWager", credits);
            entity = new StringEntity(type.equals("posted") ? json.toString()
                    : bowlingGame.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        CommonUtil.loading_box(this, "Please wait...");
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.post(getApplicationContext(),
                Data.baseUrl
                        + "bowlingcompetition/"
                        + type
                        + "/"
                        + competitionId
                        + "/game?token="
                        + CommonUtil.getAccessToken(getApplicationContext())
                        .replaceAll("[+]", "%2B") + "&apiKey="
                        + Data.apiKey, entity, "application/json",
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        if (type.equals("posted")) {
//                            handler.post(updatePostedChallengers);
                        } else {
                            DbController controller = new DbController(
                                    getApplicationContext());
                            controller.open();
                            controller.updateGame(CommonUtil
                                            .getScreenName(getApplicationContext()),
                                    competitionId);
                            controller.close();
                            Intent intent = new Intent();
                            intent.putExtra("liveCompId", competitionId);
                            setResult(RESULT_OK, intent);
                        }
                        finish();
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.loading_box_stop();
                        if (e.getMessage().equalsIgnoreCase("Payment Required")) {
                            Toast.makeText(ChooseOpponent.this, "You do not have enough credits to challenge the opponent",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            String message = e.getMessage();
                            if (message.equalsIgnoreCase("conflict")) {
                                message = "Unable to enter. Please try another competition.";
                            }
                            CommonUtil.commonGameErrorDialog(ChooseOpponent.this,
                                    message);
                        }
                    }
                });
    }

    private void fetchGamesData(boolean getFriends, String search,
                                final String type) {
        CommonUtil.loading_box(this, "Please wait...");
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.get(Data.baseUrl
                        + "/bowlinggame/"
                        + bowlingGameId
                        + "/competition/"
                        + type
                        + "/available"
                        + (getFriends ? "/friends" : "")
                        + "/search?token="
                        + CommonUtil.getAccessToken(getApplicationContext())
                        .replaceAll("[+]", "%2B") + "&apiKey=" + Data.apiKey
                        + "&search=" + search.replaceAll(" ", "%20"),
                // (search.isEmpty() ? "" :
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        List<CompetitionView> competitions = gameAdapter
                                .getCompetitions();
                        competitions.clear();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if (type.equals("posted")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject json = jsonArray
                                            .getJSONObject(i);

                                    competitions.add(new CompetitionView(json
                                            .getString("creatorUserName"), json
                                            .getString("creatorRegion"), json
                                            .getInt("creatorAverage"), json
                                            .getInt("creatorHandicap"), json
                                            .getString("name"), json
                                            .getString("id"), json
                                            .getString("expirationDateTime")));
                                }
                            } else {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject json = jsonArray
                                            .getJSONObject(i);

                                    competitions.add(new CompetitionView(
                                            json.getString("creatorUserName"),
                                            json.getString("creatorRegion"),
                                            json.getInt("creatorAverage"),
                                            json.getString("name"),
                                            json.getString("id"),
                                            json.getInt("creditWager"),
                                            getRewardPoints(
                                                    json.getInt("creditWager"),
                                                    json.getInt("playersRemaining"))
                                                    + ""));
                                }
                            }

                            gameAdapter.notifyDataSetChanged();
                            if (competitions.size() == 0) {
                                competitionsList.setVisibility(View.GONE);
                                errorMsg.setText("No "
                                        + (type.equals("live") ? "live games"
                                        : "opponents")
                                        + " are currently available");
                                errorMsg.setVisibility(View.VISIBLE);
                            } else {
                                competitionsList.setVisibility(View.VISIBLE);
                                errorMsg.setVisibility(View.GONE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.commonGameErrorDialog(ChooseOpponent.this,
                                "An error occured. Please try again.");
                        CommonUtil.loading_box_stop();
                    }
                });
    }

    private void getUserCredit() {

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.get(Data.baseUrl
                        + "userprofile/wallet?token="
                        + CommonUtil.getAccessToken(getApplicationContext())
                        .replaceAll("[+]", "%2B") + "&apiKey=" + Data.apiKey,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            credits.setText("Credit Balance : " + json.getString("credits"));
                            Data.credits = json.getInt("credits");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.commonGameErrorDialog(ChooseOpponent.this,
                                "An error occured. Please try again.");
                    }
                });
    }

    private long getRewardPoints(int credits, int opponents) {
        long r = 0;
        switch (credits) {
            case 10:
                r = 700;
                break;

            case 25:
                r = 1800;
                break;

            case 50:
                r = 3700;
                break;

            case 100:
                r = 7600;
                break;

            case 500:
                r = 40000;
                break;

            case 1000:
                r = 90000;
                break;

            default:
                r = 0;
                break;
        }
        return r * opponents;
    }

}

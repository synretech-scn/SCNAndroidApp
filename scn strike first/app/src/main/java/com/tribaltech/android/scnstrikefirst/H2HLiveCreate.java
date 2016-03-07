package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tribaltech.android.util.AppStatus;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;
import com.tribaltech.android.util.DbController;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rmn.androidscreenlibrary.ASSL;


public class H2HLiveCreate extends Activity {

    Spinner opponents;
    Spinner credits;
    Spinner playMode;
    Spinner scoringMode;
    EditText opponentsText;
    EditText creditsText;
    EditText playModeText;
    EditText scoringModeText;
    EditText gameName;
    String bowlingGameId;
    //    Game game;
    List<String> creditList;
    List<String> opponentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h2h_live_create);
        new ASSL(this, (ViewGroup) findViewById(R.id.root), 1134,
                720, false);
        bowlingGameId = Data.gameData.gameId;
        opponents = (Spinner) findViewById(R.id.opponentSpinner);
        credits = (Spinner) findViewById(R.id.creditSpinner);
        playMode = (Spinner) findViewById(R.id.playModeSpinner);
        scoringMode = (Spinner) findViewById(R.id.scoringSpinner);
        gameName = (EditText) findViewById(R.id.gameName);
        creditsText = (EditText) findViewById(R.id.credits);
        playModeText = (EditText) findViewById(R.id.playMode);
        scoringModeText = (EditText) findViewById(R.id.scoringMode);
        opponentsText = (EditText) findViewById(R.id.oppCount);

        opponentsList = new ArrayList<String>();
        opponentsList.add("NUMBER OF OPPONENTS");
        for (int i = 1; i <= 8; i++) {
            opponentsList.add(i + "");
        }
        CustomAdapter<String> opponentAdapter = new CustomAdapter<String>(
                getApplicationContext(), android.R.layout.simple_spinner_item,
                opponentsList);
        opponents.setAdapter(opponentAdapter);
        opponentAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final List<String> playModeList = new ArrayList<String>();
        playModeList.add("PLAY MODE");
        playModeList.add((!Data.gameData.centerType.equalsIgnoreCase("manual") ? "AUTOMATED" : "SELF") + " INPUT ONLY");
        playModeList.add("AUTOMATED OR SELF INPUT");
        CustomAdapter<String> playModeAdapter = new CustomAdapter<String>(
                getApplicationContext(), android.R.layout.simple_spinner_item,
                playModeList);
        playModeAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        playMode.setAdapter(playModeAdapter);
//        playMode.setSelection(Data.gameData.centerType.equals("Manual") ? 1 : 2);

        creditList = new ArrayList<String>();
        creditList.add("CREDIT AMOUNT");
        creditList.add("10");
        creditList.add("25");
        creditList.add("50");
        creditList.add("100");
        creditList.add("500");
        creditList.add("1000");
        CustomAdapter<String> creditAdapter = new CustomAdapter<String>(
                getApplicationContext(), android.R.layout.simple_spinner_item,
                creditList);
        creditAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        credits.setAdapter(creditAdapter);

        final List<String> scoringList = new ArrayList<String>();
        scoringList.add("SCORING MODE");
        scoringList.add("REGULAR GAMES - SCORES WITH HANDICAP");
        scoringList.add("I AM THAT GOOD - SCRATCH");
        CustomAdapter<String> scoringAdapter = new CustomAdapter<String>(
                getApplicationContext(), android.R.layout.simple_spinner_item,
                scoringList);
        scoringAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scoringMode.setAdapter(scoringAdapter);

        opponents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                opponentsText.setText(opponentsList.get(pos));
//                if (opponents.getSelectedItemPosition() != 0
//                        && credits.getSelectedItemPosition() != 0) {
//                    long rewards = getRewardPoints(Integer.parseInt(creditList
//                            .get(credit.getSelectedItemPosition())), Integer
//                            .parseInt(opponentsList.get(opponent
//                                    .getSelectedItemPosition())));
//                    rewardValue.setText(rewards + " PTS");
//                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        playMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                playModeText.setText(playModeList.get(pos));
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        credits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                creditsText.setText(creditList.get(pos));
//                if (opponents.getSelectedItemPosition() != 0
//                        && credits.getSelectedItemPosition() != 0) {
//                    long rewards = getRewardPoints(Integer.parseInt(creditList
//                            .get(credits.getSelectedItemPosition())), Integer
//                            .parseInt(opponentsList.get(opponents
//                                    .getSelectedItemPosition())));
//                    rewardValue.setText(rewards + " PTS");
//                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        scoringMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                scoringModeText.setText(scoringList.get(pos));
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createGame:
                if (gameName.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter Game Name", Toast.LENGTH_SHORT)
                            .show();
                } else if (credits.getSelectedItemPosition() == 0) {
                    Toast.makeText(getApplicationContext(),
                            "Please select Credit Amount", Toast.LENGTH_SHORT)
                            .show();
                } else if (opponents.getSelectedItemPosition() == 0) {
                    Toast.makeText(getApplicationContext(),
                            "Please select Number of Opponents",
                            Toast.LENGTH_SHORT).show();
                } else if (scoringMode.getSelectedItemPosition() == 0) {
                    Toast.makeText(getApplicationContext(),
                            "Please select Scoring Mode", Toast.LENGTH_SHORT)
                            .show();
                } else if (playMode.getSelectedItemPosition() == 0) {
                    Toast.makeText(getApplicationContext(),
                            "Please select Play Mode", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    String entryRes = null;
                    int playModeIndex = playMode.getSelectedItemPosition();
                    if (playModeIndex == 1) {
                        if (Data.gameData.centerType.equalsIgnoreCase("manual")) {
                            entryRes = "ManualScoring";
                        } else {
                            entryRes = "MachineScoring";
                        }
                    } else if (playModeIndex == 2) {
                        entryRes = "All";
                    }

                    String scoringMode = null;
                    int scoringIndex = this.scoringMode.getSelectedItemPosition();
                    if (scoringIndex == 1) {
                        scoringMode = "Handicap";
                    } else {
                        scoringMode = "Scratch";
                    }
                    createLiveGame(creditList.get(credits
                            .getSelectedItemPosition()), entryRes, gameName
                            .getText().toString(), opponentsList.get(opponents
                            .getSelectedItemPosition()), scoringMode);
                }
                break;

            case R.id.back:
                finish();
        }
    }

    private void createLiveGame(String entryFee, String entryRest, String name,
                                String opponentNum, String scoringMode) {
        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(H2HLiveCreate.this, "Please wait...");
        }

        StringEntity entity = null;
        try {
            JSONObject bowlingGame = new JSONObject();
            bowlingGame.put("id", bowlingGameId);
            JSONObject competition = new JSONObject();
            competition.put("competitionType", "live");
            competition.put("entryFeeCredits", entryFee);
            competition.put("entryRestrictions", entryRest);
            competition.put("name", name);
            competition.put("maxChallengersPerGroup", opponentNum);
            competition.put("maxGroups", 1);
            competition.put("scoringMode", scoringMode);

            JSONObject json = new JSONObject();
            json.put("bowlingGame", bowlingGame);
            json.put("competition", competition);
            entity = new StringEntity(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                getApplicationContext())) {
            CommonUtil.noInternetDialog(this);
            return;
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.post(getApplicationContext(),
                Data.baseUrl
                        + "bowlingcompetition/live?token="
                        + CommonUtil.getAccessToken(getApplicationContext())
                        .replaceAll("[+]", "%2B") + "&apiKey="
                        + Data.apiKey, entity, "application/json",
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();

                        try {
                            String liveCompetitionId = new JSONObject(response)
                                    .getJSONObject("competition").getString(
                                            "id");
                            DbController controller = new DbController(
                                    getApplicationContext());
                            controller.open();
                            controller.updateGame(CommonUtil
                                            .getScreenName(getApplicationContext()),
                                    liveCompetitionId);
                            controller.close();
                            setResult(RESULT_OK, new Intent().putExtra("liveCompId", liveCompetitionId));
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.loading_box_stop();
                        if (e.getMessage().equalsIgnoreCase("Payment Required")) {
                            CommonUtil.commonErrorDialog(H2HLiveCreate.this, "You do not have enough credits to create this game");
                        } else {
                            CommonUtil.commonGameErrorDialog(H2HLiveCreate.this,
                                    "An error occured. Please try again.");
                        }

                    }
                });
    }

    static class CustomAdapter<T> extends ArrayAdapter<String> {
        public CustomAdapter(Context context, int textViewResourceId,
                             List<String> countryList) {
            super(context, textViewResourceId, countryList);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView textView = (TextView) view
                    .findViewById(android.R.id.text1);

            textView.setText("");
            return view;
        }
    }
}

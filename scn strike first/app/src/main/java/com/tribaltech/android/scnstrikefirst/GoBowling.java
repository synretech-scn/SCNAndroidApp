package com.tribaltech.android.scnstrikefirst;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tribaltech.android.entities.Game;
import com.tribaltech.android.util.AppStatus;
import com.tribaltech.android.entities.Center;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;
import com.tribaltech.android.util.DbController;
import com.tribaltech.android.util.HttpDeleteWithBody;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import rmn.androidscreenlibrary.ASSL;

public class GoBowling extends MenuIntent {
    RelativeLayout gobowlbtn, livescorebtn;

    Integer[][] standingShowIndex;
    Boolean updateView = true;
    static String action = "";
    // String url = "http://api.xbowling.com/";
    private EditText lane;
    private EditText name;
    private Bundle bundle;

    protected ListView liveScoreDetailList;
    private String centerIdString;
    private String centerNameString;
    ImageView adsImage;

    protected String URL = "http://www.xbowling.com";
    private String scoringType;
    protected boolean ballType = true;
    private Dialog equipPopup;
    protected boolean oilPattern;
    protected String ballNameId = "";
    protected String commonListResponse = "";
    private boolean fromUserStats;
    CenterFragment centerFragment;
    TextView range;
    private Center center;
    boolean trackPocket;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.go_bowling);
        new ASSL(GoBowling.this, (ViewGroup) findViewById(R.id.root), 1134,
                720, false);

        centerFragment = new CenterFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.centerfragmentParent, centerFragment).commit();
        lane = (EditText) findViewById(R.id.lane);
        name = (EditText) findViewById(R.id.name);
        range = (TextView) findViewById(R.id.range);

        bundle = getIntent().getExtras();
        checkExistingGame();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bowlNowBtn:

                if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                        getApplicationContext())) {
                    CommonUtil.noInternetDialog(GoBowling.this);
                    return;
                }

                if (!name.getText().toString().isEmpty()
                        && !lane.getText().toString().isEmpty()
                        && centerFragment.centerList.size() > 0) {
                    center = centerFragment.getSelectedCenter();

                    int range = Integer.parseInt(lane.getText().toString());
                    if (center.laneCount != 0 && ((range < 1) || (range > center.laneCount))) {
                        CommonUtil.commonGameErrorDialog(GoBowling.this,
                                "Lane Number should be in the range 1-" + center.laneCount);
                        return;
                    }

                    scoringType = center.scoringType;

                    if (Data.userStatsSubscribed) {
                        Intent intent = new Intent(GoBowling.this, UserStatsSelection.class);
                        intent.putExtra("listResponse", commonListResponse);
                        intent.putExtra("ballType", ballType);
                        intent.putExtra("oilPattern", oilPattern);
                        startActivityForResult(intent, 101);
                    } else {
                        checkout(center.id,
                                name.getText().toString().toUpperCase(), lane
                                        .getText().toString(), scoringType,
                                Data.compTypeId, Data.patternLengthId,
                                Data.patternNameId);
                    }

                } else {
                    CommonUtil.commonGameErrorDialog(GoBowling.this,
                            "Please fill in all fields.");
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if(resultCode == RESULT_OK) {
                checkout(center.id,
                        name.getText().toString().toUpperCase(), lane
                                .getText().toString(), scoringType,
                        Data.compTypeId, Data.patternLengthId,
                        Data.patternNameId);
            }
        }
    }

    public void toggle(View v) {
        toggle();
    }

    private void cancelGame(final String bowlingGameId, final String scoringType) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("id", bowlingGameId);
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
                    HttpClient client = new DefaultHttpClient();
                    final HttpResponse response = client.execute(httpDelete);
                    if (response.getStatusLine().getStatusCode() == 204) {
                        DbController controller = new DbController(
                                getApplicationContext());
                        controller.open();
                        controller.deleteGame(CommonUtil
                                .getScreenName(getApplicationContext()));
                        controller.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void specifyRange(int totalLanes) {
        if (totalLanes == 0) {
            range.setText("");
        } else {
            range.setText("Range : 1-" + totalLanes);
        }
    }

    private void checkExistingGame() {

        DbController controller = new DbController(getApplicationContext());
        controller.open();
        Data.gameData = controller.getGameData(CommonUtil
                .getScreenName(getApplicationContext()));
        controller.close();
        if (Data.gameData == null) {
            // loadCentresNearby();
            centerFragment.load();
            fetchCommonLists(false);
        } else {
//            fetchCommonLists(true);
            startExistingGame();
        }
        // } else {
        // final Dialog resumeGamePopup = new Dialog(GoBowling.this,
        // android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        //
        // resumeGamePopup.setTitle(null);
        // resumeGamePopup.setCancelable(false);
        // resumeGamePopup.setContentView(R.layout.resume_game_dialog);
        // resumeGamePopup.getWindow().getAttributes().windowAnimations =
        // R.style.Animations_from_top;
        // new ASSL(GoBowling.this,
        // (ViewGroup) resumeGamePopup.findViewById(R.id.root), 720,
        // 1196, true);
        //
        // Button resumeGame = (Button) resumeGamePopup
        // .findViewById(R.id.resumeGame);
        // Button cancelGame = (Button) resumeGamePopup
        // .findViewById(R.id.cancelGame);
        // resumeGame.setOnClickListener(new View.OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        //
        // Data.patternNameId = game.patternName;
        // Data.patternLengthId = game.patternLength;
        // Data.compTypeId = game.compType;
        //
        // Intent intent = new Intent(GoBowling.this,
        // GameScreen.class);
        // intent.putExtra("laneCheckoutId", game.checkoutId);
        // intent.putExtra("laneNumber", game.laneNumber);
        // intent.putExtra("centerName", game.centerName);
        // intent.putExtra("bowlingGameId", game.gameId);
        // intent.putExtra("bowlerName", game.screenName);
        // intent.putExtra("scoringType", game.centerType);
        // intent.putExtra("liveGameId", game.liveGameId);
        // intent.putExtra("teamId", game.liveGameId);
        // intent.putExtra("venueId", game.venueId);
        // intent.putExtra("newGame", false);
        // resumeGamePopup.dismiss();
        // startActivity(intent);
        // finish();
        // overridePendingTransition(R.anim.from_right, R.anim.to_left);
        // }
        // });
        //
        // cancelGame.setOnClickListener(new View.OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // // Data.CMAGame = false;
        // resumeGamePopup.dismiss();
        // cancelGame(game.checkoutId, game.centerType);
        // loadCentresNearby();
        // fetchCommonLists();
        // }
        // });
        // Button close = (Button) resumeGamePopup
        // .findViewById(R.id.closedialog);
        // close.setOnClickListener(new View.OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        //
        // resumeGamePopup.dismiss();
        // Intent i = new Intent(getApplicationContext(),
        // ScreenMain.class);
        // startActivity(i);
        // finish();
        // overridePendingTransition(R.anim.hold, R.anim.hold);
        // }
        // });
        // resumeGamePopup.show();
        // }
    }

    private void startExistingGame() {
        Intent intent = new Intent(GoBowling.this, GameScreen.class);
//            intent.putExtra("laneCheckoutId", game.checkoutId);
//            intent.putExtra("laneNumber", game.laneNumber);
//            intent.putExtra("centerName", game.centerName);
//            intent.putExtra("bowlingGameId", game.gameId);
//            intent.putExtra("bowlerName", game.screenName);
//            intent.putExtra("scoringType", game.centerType);
//            intent.putExtra("liveGameId", game.liveGameId);
//            intent.putExtra("teamId", game.liveGameId);
//            intent.putExtra("venueId", game.venueId);
//            intent.putExtra("newGame", false);
        intent.putExtra("listResponse", commonListResponse);
        intent.putExtra("ballType", ballType);
        intent.putExtra("oilPattern", oilPattern);
        intent.putExtra("trackPocket", trackPocket);
        GameScreen.savedFrame = null;
        startActivity(intent);
        finish();
    }


    private void checkout(final int venueId, final String bowlerName,
                          final String laneNumber, final String scoringType,
                          final String competitionTypeId,
                          final String userStatPatternLengthId,
                          final String userStatPatternNameId) {

        if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                getApplicationContext())) {
            CommonUtil.noInternetDialog(GoBowling.this);
            return;
        }

        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(this, "Please wait...");
        }

        StringEntity entity = null;
        try {
            JSONObject venue = new JSONObject();
            venue.put("id", venueId);
            JSONObject json = new JSONObject();
            json.put("venue", venue);
            json.put("bowlerName", bowlerName);
            json.put("laneNumber", Integer.parseInt(laneNumber));
            json.put("CompetitionTypeId", competitionTypeId);
            json.put("UserStatPatternLengthId", userStatPatternLengthId);
            json.put("UserStatPatternNameId", userStatPatternNameId);
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
                        try {
                            CommonUtil.loading_box_stop();
                            JSONObject json = new JSONObject(response);
                            String checkoutId = json.getString("id");
                            JSONObject bowlingGame = json
                                    .getJSONObject("bowlingGame");

                            Intent intent = new Intent(GoBowling.this,
                                    GameScreen.class);
//                            intent.putExtra("laneCheckoutId", checkoutId);

                            Center center = centerFragment.getSelectedCenter();
                            String centerName = center.name;

//                            intent.putExtra("laneNumber", laneNumber);
//                            intent.putExtra("centerName", centerName);
//                            intent.putExtra("bowlerName", bowlerName);
//                            intent.putExtra("bowlingGameId",
//                                    bowlingGame.getString("id"));
//                            intent.putExtra("scoringType", scoringType);
//                            intent.putExtra("liveGameId", "");
//                            intent.putExtra("venueId", venueId);
                            intent.putExtra("newGame", true);
                            intent.putExtra("listResponse", commonListResponse);
                            intent.putExtra("ballType", ballType);
                            intent.putExtra("oilPattern", oilPattern);
                            intent.putExtra("trackPocket", trackPocket);
//                            intent.putExtra("ballName", ballNameId);
                            Data.gameData = new Game(CommonUtil
                                    .getScreenName(getApplicationContext()),
                                    checkoutId + "", scoringType, bowlingGame
                                    .getInt("id") + "", laneNumber,
                                    centerName, "", bowlerName, venueId,
                                    Data.patternNameId, Data.patternLengthId,
                                    Data.compTypeId);
                            DbController controller = new DbController(
                                    getApplicationContext());
                            controller.open();
                            controller.insertNewGame(Data.gameData);
                            controller.close();
                            GameScreen.savedFrame = null;
                            Data.postedEntered = false;
                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.commonGameErrorDialog(GoBowling.this,
                                e.getMessage() + "");
                        CommonUtil.loading_box_stop();

                    }
                });
    }

    private void fetchCommonLists(final boolean existingGame) {

        if (existingGame && !CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(this, "Please wait...");
        }

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
                                commonListResponse = response;
                                getUserSettings(commonListResponse, existingGame);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (existingGame) {
                                startExistingGame();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        // Log.v("exception ", e.toString() + "");
                        CommonUtil.loading_box_stop();
                        CommonUtil.commonGameErrorDialog(GoBowling.this,
                                e.getMessage() + "");
                        if (existingGame) {
                            startExistingGame();
                        }
                    }
                });
    }

    public static Calendar minusTime(int min) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, min);
        return c;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // gps.stopUsingGPS();
        ASSL.closeActivity((ViewGroup) findViewById(R.id.root));
    }

    private void getUserSettings(final String listResponse, final boolean existingGame) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(
                Data.baseUrl
                        + "UserStat/UserStatSettingsList?token="
                        + CommonUtil.getAccessToken(GoBowling.this).replaceAll(
                        "[+]", "%2B") + "&apiKey=" + Data.apiKey,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        try {
                            if (response == null || response.equals("null")) {
                                ballType = true;
                                return;
                            }
                            JSONObject obj = new JSONObject(response);
                            ballType = obj.getBoolean("ballType");
                            oilPattern = obj.getBoolean("oilPattern");
                            trackPocket = obj.getBoolean("pocketPercentage");
                            // equipmentDetailsPopup(listResponse);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally{
                            if(existingGame) {
                                startExistingGame();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        // Log.v("exception ", e.toString() + "");
                        CommonUtil.commonGameErrorDialog(GoBowling.this,
                                e.getMessage() + "");
                        CommonUtil.loading_box_stop();
                        if(existingGame) {
                            startExistingGame();
                        }                    }
                });
    }

    public static class CustomAdapter<T> extends ArrayAdapter<String> {
        public CustomAdapter(Context context, int textViewResourceId,
                             List<String> countryList) {

            super(context, textViewResourceId, countryList);
            // TextView tv=(TextView)findViewById(textViewResourceId);
            // tv.setTextSize(10);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView textView = (TextView) view
                    .findViewById(android.R.id.text1);

            textView.setText("");
            return view;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, ScreenMain.class);
        startActivity(intent);
        finish();
    }

}

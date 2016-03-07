package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.tribaltech.android.util.AppConstants;
import com.tribaltech.android.util.AppStatus;
import com.tribaltech.android.util.CircleTransform;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;

import rmn.androidscreenlibrary.ASSL;


public class LeaderboardDetail extends Activity {

    TextView score;
    TextView averageScore;
    TextView totalGames;
    TextView userName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard_detail);
        new ASSL(LeaderboardDetail.this, (ViewGroup) findViewById(R.id.root),
                AppConstants.SCREEN_HEIGHT, AppConstants.SCREEN_WIDTH,
                false);
        score = (TextView) findViewById(R.id.scores);
        totalGames = (TextView) findViewById(R.id.totalGames);
        averageScore = (TextView) findViewById(R.id.avgScore);
        userName = (TextView) findViewById(R.id.username);
        getDetail(getIntent().getStringExtra("userId"));
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    private void getDetail(String userId) {

        if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                getApplicationContext())) {
            CommonUtil.noInternetDialog(LeaderboardDetail.this);
            return;
        }

        CommonUtil.loading_box(LeaderboardDetail.this, "Loading...");
        RequestParams rv = new RequestParams();
        rv.put("apiKey", Data.apiKey);
        rv.put("token", CommonUtil.getAccessToken(getApplicationContext())
                .replaceAll("[+]", "%2B"));
        rv.put("userId", userId);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(
                Data.baseUrl + "bowlingstatistic/GetStats?" + rv.toString(),
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        try {
                            JSONObject json = new JSONObject(response);
                            score.setText(NumberFormat.getNumberInstance(Locale.US)
                                    .format(json.getInt("totalScore")).toString());
                            totalGames.setText(NumberFormat.getNumberInstance(Locale.US)
                                    .format(json.getInt("totalGamesPlayed")).toString());
                            averageScore.setText(NumberFormat.getNumberInstance(Locale.US)
                                    .format(json.getInt("averageScore")).toString());

                            json = json.getJSONObject("userProfile");
                            userName.setText(json.getString("screenName"));
                            if (!json.isNull("pictureFile")) {
                                Picasso.with(getApplicationContext())
                                        .load(json.getJSONObject(
                                                "pictureFile").getString("fileUrl"))
                                        .error(R.drawable.profile_icon_selector)
                                        .transform(new CircleTransform()).fit()
                                        .into((ImageView) findViewById(R.id.profile_dp));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.loading_box_stop();
                    }
                });

    }

}

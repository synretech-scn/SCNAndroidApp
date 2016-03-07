package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tribaltech.android.entities.LeaderboardFilterItem;
import com.tribaltech.android.util.AppStatus;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;
import com.tribaltech.android.util.LeaderboardAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rmn.androidscreenlibrary.ASSL;


public class Leaderboard extends Activity {

    LeaderboardAdapter adapter;
    ListView list;
    public static final int FILTER_REQUEST = 101;
    TextView topText;
    boolean centerLeaderboard;
    Integer venueId = 0;
    String venueName;
    RelativeLayout noItemsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        new ASSL(Leaderboard.this, (ViewGroup) findViewById(R.id.root), 1134,
                720, false);
        noItemsText = (RelativeLayout) findViewById(R.id.noItems);
        topText = (TextView) findViewById(R.id.topText);
        list = (ListView) findViewById(R.id.leaderboardList);
        adapter = new LeaderboardAdapter(Leaderboard.this, new ArrayList<String[]>());
        list.setAdapter(adapter);

        centerLeaderboard = getIntent().hasExtra("center");
        if (centerLeaderboard) {
            venueId = getIntent().getIntExtra("venueId", 0);
            venueName = getIntent().getStringExtra("venueName");
            ((TextView) findViewById(R.id.headerText)).setText("Centre Leaderboard");
        }

//        if (Data.leaderboardFilter == null) {
        if (centerLeaderboard) {
            Data.leaderboardFilter = new LeaderboardFilterItem(venueId.toString(), venueName);
        } else {
            Data.leaderboardFilter = new LeaderboardFilterItem();
        }
//        }
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String userId = adapter.data.get(position)[3];
                Intent intent = new Intent(Leaderboard.this,LeaderboardDetail.class);
                intent.putExtra("userId",userId);
                startActivity(intent);
            }
        });
        getleaderboard("true");
    }

    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.back:
                finish();
                break;

            case R.id.filter: {
                Intent intent = new Intent(Leaderboard.this, LeaderboardFilter.class);
                intent.putExtra("center", centerLeaderboard);
                intent.putExtra("venueId", venueId);
                startActivityForResult(intent, FILTER_REQUEST);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILTER_REQUEST && resultCode == RESULT_OK) {
            getleaderboard("true");
        }
    }

    /**
     * Venue Leaderboard
     *
     * @param isVenueLeaderboard
     */
    private void getleaderboard(String isVenueLeaderboard) {

        if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                getApplicationContext())) {
            CommonUtil.noInternetDialog(Leaderboard.this);
            return;
        }

        CommonUtil.loading_box(Leaderboard.this, "Loading...");
        RequestParams rv = new RequestParams();
        rv.put("apiKey", Data.apiKey);
        rv.put("token", CommonUtil.getAccessToken(getApplicationContext())
                .replaceAll("[+]", "%2B"));
        rv.put("countryId", Data.leaderboardFilter.countryId);
        rv.put("administrativeAreaId", Data.leaderboardFilter.stateId);
        rv.put("venueId", Data.leaderboardFilter.venueId);
        rv.put("isVenueLeaderboard", isVenueLeaderboard);
        rv.put("friends", Data.leaderboardFilter.allBowlers ? "false" : "true");

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(
                Data.baseUrl + "leaderboard/"
                        + Data.leaderboardFilter.leaderboardType.replaceAll(" ", "%20") + "?" + rv.toString(),
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {

                        JSONObject jsn = null;
                        JSONArray jsonArray = null;
                        CommonUtil.loading_box_stop();
                        try {
                            jsn = new JSONObject(response);
                            jsonArray = jsn.getJSONArray("leaderboardResults");
                            List<String[]> data = adapter.getData();
                            data.clear();

                            if (jsonArray.length() == 0) {
                                noItemsText.setVisibility(View.VISIBLE);
//                                noItemsText.setText("No record found. Leaderboard is wide open.");
                            } else {
                                noItemsText.setVisibility(View.GONE);
                            }

                            for (int i = 0; i < jsonArray.length(); i++) {
                                data.add(new String[]{jsonArray.getJSONObject(i).getString(
                                        "name"), Data.numberFormat.format(Integer
                                        .parseInt(jsonArray.getJSONObject(i)
                                                .getString("score"))), Data.leaderboardFilter.pointsText,
                                        jsonArray.getJSONObject(i).getString(
                                                "userId")});
                            }
                            adapter.notifyDataSetChanged();
                            list.smoothScrollToPosition(0);
                            String text = Data.leaderboardFilter.leaderboardTypeName;
                            if (jsn.getString("currentUserResult").equals(
                                    "null")) {
                                text = "You have not been ranked on the " + text;
                            } else {
                                JSONObject myRank = jsn
                                        .getJSONObject("currentUserResult");
                                text = "You have ranked "
                                        + myRank.getString("rank") + " on "
                                        + CommonUtil.toCamelCase(text, " ");
                            }
                            text += " Leaderboard";
                            topText.setText(text);
                            ((ViewGroup) topText.getParent()).setVisibility(View.VISIBLE);

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

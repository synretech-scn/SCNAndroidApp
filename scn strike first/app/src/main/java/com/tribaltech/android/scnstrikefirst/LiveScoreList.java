package com.tribaltech.android.scnstrikefirst;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tribaltech.android.util.AppStatus;
import com.tribaltech.android.entities.Center;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;
import com.tribaltech.android.util.ExpandableListAdapter;
import com.tribaltech.android.util.LiveScorePlayerData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import rmn.androidscreenlibrary.ASSL;

public class LiveScoreList extends MenuIntent {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader = new ArrayList<String>();
    HashMap<String, List<LiveScorePlayerData>> listDataChild = new HashMap<String, List<LiveScorePlayerData>>();
    Center center;
    TextView noActiveLane;
    TextView availableLanes;
    TextView centerName;
    public static int laneCount;
    Context _context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.livescore_detail);
        new ASSL(LiveScoreList.this, (ViewGroup) findViewById(R.id.root), 1134,
                720, false);
        _context = LiveScoreList.this;
        expListView = (ExpandableListView) findViewById(R.id.liveScoreList);
        availableLanes = (TextView) findViewById(R.id.lanes);
        noActiveLane = (TextView) findViewById(R.id.noActiveLanes);
        centerName = (TextView) findViewById(R.id.centerName);
        center = (Center) getIntent().getSerializableExtra("center");
        centerName.setText(center.name);
        listAdapter = new ExpandableListAdapter(this, center.id + "", center.name, expListView,
                noActiveLane, availableLanes);
        expListView.setAdapter(listAdapter);
        getLaneCount(center.id);
        listAdapter.updateAllLanes(center.id + "", true);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.callCenter:
                Uri number = Uri.parse("tel:" + center.contact);
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);
                break;

            case R.id.collapse:
                for (int i = 0; i < listAdapter.getGroupCount(); i++) {
                    expListView.collapseGroup(i);
                }
                break;

            case R.id.back:
                finish();
                break;

            default:
                break;
        }
    }

    public void toggle(View v) {
        toggle();
    }

    private void getLaneCount(final int venueId) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(Data.baseUrl + "venue/" + venueId + "/getlanecount",
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        try {
                            laneCount = new JSONObject(response)
                                    .getJSONArray("table").getJSONObject(0)
                                    .getInt("laneCount");
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            //getLiveScore(center.id, center.name, center.contact);
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
//                        getLiveScore(center.id, center.name, center.contact);
                    }
                });

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listAdapter != null) {
            listAdapter.stopUpdating();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (listAdapter != null) {
            listAdapter.startUpdating();
        }
    }

    private void getLiveScore(final int id, final String cName,
                              final String contact) {

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String to = df.format(c.getTime());

        String from = df.format(CommonUtil.minusTime(-90).getTime());

        if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                getApplicationContext())) {
            CommonUtil.noInternetDialog(LiveScoreList.this);
            return;
        }

        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(LiveScoreList.this, "Loading...");
        }
        RequestParams rv = new RequestParams();
        rv.put("from", from);
        rv.put("to", to);
        rv.put("apiKey", Data.apiKey);
        rv.put("token", CommonUtil.getAccessToken(getApplicationContext()));

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(Data.baseUrl + "venue/" + id + "/summary", rv,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        try {
                            String laneNo = "";
                            JSONArray laneArray = new JSONArray(response);
                            int avLanes = laneCount;
                            avLanes -= laneArray.length();
                            if (avLanes <= 0) {
                                avLanes = 0;
                            }
                            availableLanes.setText(avLanes + "");

                            if (laneArray.length() == 0) {
                                expListView.setVisibility(View.GONE);
                                noActiveLane.setVisibility(View.VISIBLE);
                                noActiveLane.setText("No Active Lanes at "
                                        + center.name + "!");
                            } else {
                                expListView.setVisibility(View.VISIBLE);
                                noActiveLane.setVisibility(View.GONE);
                            }

                            for (int i = 0; i < laneArray.length(); i++) {
                                JSONObject item = laneArray.getJSONObject(i);
                                laneNo = item.getString("laneNumber");
                                listDataHeader.add(laneNo);
                                List<LiveScorePlayerData> data = new ArrayList<LiveScorePlayerData>();
                                String names[] = item.getString("name").split(
                                        ", ");
                                for (String playerName : names) {
                                    // data.add(new LiveScorePlayerData(
                                    // playerName, "", ""));
                                }
                                listDataChild.put(listDataHeader
                                                .get(listDataHeader.size() - 1),
                                        new ArrayList<LiveScorePlayerData>());
                                listAdapter.notifyDataSetChanged();
                                listAdapter.stopUpdating();
                                listAdapter.startUpdating();
                                for (String lane : listDataHeader) {
                                    // getLiveScoreUpdate(center.id + "", lane);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.commonGameErrorDialog(LiveScoreList.this,
                                "An error occured.Please try again.");
                        CommonUtil.loading_box_stop();

                    }
                });
    }


}
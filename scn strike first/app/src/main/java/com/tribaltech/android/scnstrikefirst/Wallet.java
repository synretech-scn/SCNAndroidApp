package com.tribaltech.android.scnstrikefirst;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;
import com.tribaltech.android.util.StatsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rmn.androidscreenlibrary.ASSL;


public class Wallet extends MenuIntent {

    ListView listView;
    StatsAdapter centerAdapter;
    StatsAdapter pointsAdapter;
    TextView headerText;
    Map<String, List<String[]>> pointsMap = new HashMap<>();
    private static final int CENTER_VIEW = 1;
    private static final int POINT_VIEW = 2;
    static Set<Integer> venues = new HashSet<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        new ASSL(this, (ViewGroup) findViewById(R.id.root), 1134,
                720, false);
        headerText = (TextView) findViewById(R.id.headerText);
        listView = (ListView) findViewById(R.id.pointsList);

        centerAdapter = new StatsAdapter(this,
                new ArrayList<String[]>(), R.layout.points_item, 102);
        pointsAdapter = new StatsAdapter(this,
                new ArrayList<String[]>(), R.layout.points_item, 102);
       // listView.setAdapter(centerAdapter);
      listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                boolean centerView = headerText.getText().toString().equalsIgnoreCase("Wallet");
                if (centerView) {
                    String[] data = centerAdapter.getData().get(position);
                    if (position == 0) {
                        Intent intent = new Intent(Wallet.this, WalletTransaction.class);
                        intent.putExtra("prizes", true);
//                        startActivity(intent);
                    } else if (pointsMap.get(data[2]) == null) {
                        getPoints(data[0],
                                data[2]);

                        //add
                        //    if(centerisadded )
                        getPoints("SCN Strike First","15103");
                        changeView(POINT_VIEW, "SCN Strike First");

                    } else {
                        pointsAdapter.setContestList(pointsMap.get(data[2]));
                        listView.setAdapter(pointsAdapter);
                        pointsAdapter.notifyDataSetChanged();

                        //add
                        //    if(centerisadded )
                        getPoints("SCN Strike First", "15103");
                        changeView(POINT_VIEW, "SCN Strike First");

                       // changeView(POINT_VIEW, data[0]);
                    }
                }
            }
          });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isCenterView()) {
            getCentres();
        } else {
            if (pointsAdapter.getData().size() > 0) {
                getPoints(pointsAdapter.getData().get(0)[3], pointsAdapter.getData().get(0)[2]);
            }
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addPoints: {
                Intent intent = new Intent(this, WalletTransaction.class);
                intent.putExtra("venueId", pointsAdapter.getData().get(0)[2]);
                intent.putExtra("caseAdd", true);
                //modi
              intent.putExtra("availablePoints", Integer.toString(Integer.MAX_VALUE));
              //  intent.putExtra("availablePoints", pointsAdapter.getData().get(0)[1]);
                startActivity(intent);
                break;
            }

            case R.id.redeemPoints: {
                Intent intent = new Intent(this, WalletTransaction.class);
                //modi
                //intent.putExtra("venueId", pointsAdapter.getData().get(0)[2]);

                //intent.putExtra("availablePoints", pointsAdapter.getData().get(0)[1]);
                intent.putExtra("venueId", pointsAdapter.getData().get(1)[2]);
                intent.putExtra("availablePoints", pointsAdapter.getData().get(1)[1]);
                startActivity(intent);
                break;
            }

            case R.id.addCenter: {
                Intent intent = new Intent(this, AddCenterWallet.class);
                startActivity(intent);
                break;
            }
        }
    }

    private boolean isCenterView() {
        return headerText.getText().toString().equalsIgnoreCase("Wallet");
    }

    private void changeView(int changeTo, String... text) {
        findViewById(R.id.pointsBtnParent).setVisibility(changeTo == CENTER_VIEW ? View.GONE : View.VISIBLE);
        headerText.setText(changeTo == CENTER_VIEW ? "Wallet" : text[0]);
        if (changeTo == CENTER_VIEW) {
            listView.setAdapter(centerAdapter);
            centerAdapter.notifyDataSetChanged();
            //modi
            listView.setVisibility(View.GONE);

        } else {
            listView.setAdapter(pointsAdapter);
            pointsAdapter.notifyDataSetChanged();
        }
    }

    public void toggle(View view) {
        toggle();
    }

    private void getCentres() {
        //add
         boolean centerisadded=true;

        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(this, "Please wait...");
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(
                Data.baseUrl
                        + "venue/venuepointslist?token="
                        + CommonUtil.getAccessToken(this).replaceAll(
                        "[+]", "%2B") + "&apiKey=" + Data.apiKey, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        if (venues != null) {
                            venues.clear();
                        } else {
                            venues = new HashSet<Integer>();
                        }
                        try {
                            JSONArray array = new JSONArray(response);
                            List<String[]> dataList = new ArrayList<String[]>();
                            dataList.add(new String[]{"XBowling Points", Data.userPoints});
                            if (array.length() == 0) {
                                findViewById(R.id.noItems).setVisibility(View.VISIBLE);
                                listView.setVisibility(View.GONE);
                            } else {
                                findViewById(R.id.noItems).setVisibility(View.GONE);
                                listView.setVisibility(View.VISIBLE);
                            }
                            for (int i = 0; i < array.length(); i++) {
                                String[] data = new String[3];
                                data[0] = array.getJSONObject(i).getString("venueName").trim();
                                data[1] = array.getJSONObject(i).getString("points");
                                data[2] = array.getJSONObject(i).getString("venueId");

                                //add
                           //     if(data[0].equals("SCN Strike First" ) )
                                //  centerisadded=true;

                                venues.add(Integer.valueOf(data[2]));
                                dataList.add(data);
                            }
                            centerAdapter.setContestList(dataList);
                            changeView(CENTER_VIEW);
                            getUserCredit();

                            //add
                        //    if(centerisadded )
                            getPoints("SCN Strike First","15103");
                            changeView(POINT_VIEW,"SCN Strike First");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        super.onFailure(throwable);
                        CommonUtil.loading_box_stop();
                    }
                });
    }

    private void getPoints(final String venueName, final String venueId) {
        if (!CommonUtil.is_loading_showing()) {
//            CommonUtil.loading_box(this, "Please wait...");
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(
                Data.baseUrl
                        + "venue/" + venueId + "/userpointPair?token="
                        + CommonUtil.getAccessToken(this).replaceAll(
                        "[+]", "%2B") + "&apiKey=" + Data.apiKey, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        try {
                            JSONObject obj = new JSONObject(response);
                            List<String[]> dataList = new ArrayList<String[]>();
                            String[] data = new String[4];
                            data[0] = "Lifetime Points";
                            data[1] = obj.getString("lifeTimePoint");
                            data[2] = venueId;
                            data[3] = venueName;
                            dataList.add(data);
                            data = new String[4];
                            data[0] = "Available Points";
                            data[1] = obj.getString("totalAvaliablePoints");
                            data[2] = venueId;
                            data[3] = venueName;

                            dataList.add(data);
                            pointsMap.put(venueId, dataList);
                            changeView(POINT_VIEW, venueName);
                            listView.setAdapter(pointsAdapter);
                            pointsAdapter.setContestList(dataList);
                            pointsAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        super.onFailure(throwable);
                        CommonUtil.loading_box_stop();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        venues = null;
    }

    @Override
    public void onBackPressed() {
        //modi
        //finish();

        if (isCenterView()) {
            super.onBackPressed();
        } else {

            //modi
            getPoints("SCN Strike First", "15103");
            changeView(POINT_VIEW,"SCN Strike First");
            super.onBackPressed();
            ////changeView(CENTER_VIEW);
        }
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
                            Data.userPoints = Integer.toString(json.getInt("credits"));
                            centerAdapter.getData().get(0)[1] = Data.userPoints;
                            centerAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.commonGameErrorDialog(Wallet.this,
                                "An error occured. Please try again.");
                    }
                });
    }
}

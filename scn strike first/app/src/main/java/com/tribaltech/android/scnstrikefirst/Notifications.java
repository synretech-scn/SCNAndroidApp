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
import com.tribaltech.android.util.NotificationAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rmn.androidscreenlibrary.ASSL;


public class Notifications extends MenuIntent {

    public static final int CENTRES = 1;
    public static final int NOTIFICATIONS = 2;
    public static final int INDEX_VENUE_ID = 0;

    ListView listView;
    NotificationAdapter centerAdapter;
    NotificationAdapter notificationAdapter;
    int currentVisible = CENTRES;
    TextView topHeaderText;
    String currentVenue = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        new ASSL(Notifications.this, (ViewGroup) findViewById(R.id.root), 1134,
                720, false);
        topHeaderText = (TextView) findViewById(R.id.centerName);
        listView = (ListView) findViewById(R.id.notificationList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentVisible == CENTRES) {
                    currentVisible = NOTIFICATIONS;
                    topHeaderText.setText(centerAdapter.getData().get(position)[1]);
                    findViewById(R.id.menu).setVisibility(View.GONE);
                    findViewById(R.id.back).setVisibility(View.VISIBLE);
                    updateNotifications(Notifications.this);
                    currentVenue = centerAdapter.getData().get(position)[INDEX_VENUE_ID];
                    getNotifications(currentVenue);
//                    List<String[]> data = new ArrayList<>();
//                    data.add(new String[]{"", "Hey! You have won today's Strike Challenge. You can come and enjoy the next free game over the center."});
//                    data.add(new String[]{"", "Hey! Play on Monday and get free on every 3rd game you play."});
//                    data.add(new String[]{"", "Hey! You have won monthly Spare leader challenge. You can enjoy free coke over the next meal you have."});
//                    data.add(new String[]{"", "Hey! Its excellent to see that you made 300 (Perfect Score). Enjoy free coke today to celebrate it."});
//                    notificationAdapter = new NotificationAdapter(Notifications.this, data);
//                    listView.setAdapter(notificationAdapter);
                } else {
                    Intent intent = new Intent(Notifications.this, NotificationDetails.class);
                    intent.putExtra("id", notificationAdapter.getData().get(position)[0]);
                    intent.putExtra("text", notificationAdapter.getData().get(position)[1]);
                    intent.putExtra("centerName", topHeaderText.getText().toString());
                    startActivity(intent);
                }
            }
        });
        getCentres();

//        List<String[]> centres = new ArrayList<>();
//        centres.add(new String[]{"", "Country Club Lanes - Sacramento CA"});
//        centres.add(new String[]{"", "Bel Mateo Bowl"});
//        centres.add(new String[]{"", "Cloverleaf Family Bowl"});
//        centres.add(new String[]{"", "Tenpin Cambridge"});
//
//        centerAdapter = new NotificationAdapter(Notifications.this, centres);
//        listView.setAdapter(centerAdapter);
    }

    public void toggle(View v) {
        toggle();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, ScreenMain.class);
        startActivity(intent);
        finish();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                currentVisible = CENTRES;
                findViewById(R.id.menu).setVisibility(View.VISIBLE);
                findViewById(R.id.back).setVisibility(View.GONE);
                updateNotifications(Notifications.this);
                topHeaderText.setText("Notifications");
                listView.setAdapter(centerAdapter);
                break;
        }
    }

    private void getCentres() {

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(Data.baseUrl + "NotificationHistory/venues?token="
                        + CommonUtil.getAccessToken(getApplicationContext())
                        .replaceAll("[+]", "%2B") + "&apiKey="
                        + Data.apiKey,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        currentVisible = CENTRES;
                        CommonUtil.loading_box_stop();
                        try {
                            JSONArray array = new JSONObject(response).getJSONArray("table");
                            List<String[]> data = new ArrayList<String[]>();
                            for (int i = 0; i < array.length(); i++) {
                                data.add(new String[]{array.getJSONObject(i).getString("venueId"),
                                        array.getJSONObject(i).getString("venueName")});
                            }
                            centerAdapter = new NotificationAdapter(Notifications.this, data);
                            listView.setAdapter(centerAdapter);
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

    private void getNotifications(String venueId) {

        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(Notifications.this, "Please wait...");
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(Data.baseUrl + "NotificationHistory/Notification/" + venueId + "?token="
                        + CommonUtil.getAccessToken(getApplicationContext())
                        .replaceAll("[+]", "%2B") + "&apiKey="
                        + Data.apiKey,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        currentVisible = NOTIFICATIONS;
                        CommonUtil.loading_box_stop();
                        try {
                            JSONArray array = new JSONArray(response);
                            List<String[]> data = new ArrayList<String[]>();
                            for (int i = 0; i < array.length(); i++) {
                                data.add(new String[]{array.getJSONObject(i).getInt("id") + "",
                                        array.getJSONObject(i).getString("notificationMessage"),
                                        array.getJSONObject(i).getString("status")});
                            }
                            notificationAdapter = new NotificationAdapter(Notifications.this, data);
                            listView.setAdapter(notificationAdapter);
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

    @Override
    public void onResume() {
        super.onResume();
        if (currentVisible == NOTIFICATIONS && !currentVenue.isEmpty()) {
            getNotifications(currentVenue);
        }
        ScreenMain.getNotificationCount(this);
    }

}

package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;

import rmn.androidscreenlibrary.ASSL;


public class NotificationDetails extends Activity {

    TextView notificationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_details);
        new ASSL(NotificationDetails.this, (ViewGroup) findViewById(R.id.root), 1134,
                720, false);
        notificationText = (TextView) findViewById(R.id.notificationText);
        notificationText.setText(getIntent().getStringExtra("text"));

//        TextView topHeaderText = (TextView) findViewById(R.id.centerName);
//        topHeaderText.setText(getIntent().getStringExtra("centerName"));
        changeNotificationStatus(getIntent().getStringExtra("id"));
    }

    private void changeNotificationStatus(String id) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.post(Data.baseUrl + "NotificationHistory/SetRead?token="
                        + CommonUtil.getAccessToken(getApplicationContext())
                        .replaceAll("[+]", "%2B") + "&apiKey="
                        + Data.apiKey + "&PushNotificationId=" + id,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                    }

                    @Override
                    public void onFailure(Throwable e) {

                        CommonUtil.loading_box_stop();
                    }
                });
    }

    public void onClick(View view) {
        finish();
    }

}

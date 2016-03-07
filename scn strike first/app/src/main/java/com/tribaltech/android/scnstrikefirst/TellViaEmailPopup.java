package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tribaltech.android.util.AppStatus;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;

import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import rmn.androidscreenlibrary.ASSL;

public class TellViaEmailPopup extends Activity {

    Activity context;
    TextView errorMsg;
    private JSONObject jj;
    EditText emailAddress;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tell_friend_vai_email_popup);
        context = this;
        new ASSL(this, (ViewGroup) findViewById(R.id.root), 1134, 720,
                false);

        emailAddress = (EditText) findViewById(R.id.email);

        Button addEmails = (Button) findViewById(R.id.addEmails);

        addEmails.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(TellViaEmailPopup.this, EmailList.class);
                startActivityForResult(intent, 100);

            }
        });

        Button send = (Button) findViewById(R.id.send);

        send.setOnClickListener(new OnClickListener() {
            private JSONObject item;

            @Override
            public void onClick(View v) {
                int count = 0;
                try {

                    String[] ids = emailAddress.getText().toString().split(",");

                    jj = new JSONObject();

                    JSONArray arry = new JSONArray();
                    for (int i = 0; i < ids.length; i++) {
                        if (CommonUtil.isValidEmail(ids[i])) {
                            item = new JSONObject();
                            item.put("emailAddress", ids[i]);
                            item.put("userReferralType", "email");
                            arry.put(count, item);
                            count++;
                        }

                    }
                    jj.put("list", arry);
                    Log.v("json ", jj.toString(2));
                    EmailPost(jj);
                } catch (Exception e) {
                    Log.v("json creating error", e.toString());
                }

            }
        });

        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(emailAddress.getWindowToken(), 0);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    public void EmailPost(JSONObject entry) {

        StringEntity emails = null;
        try {
            emails = new StringEntity(entry.toString());
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        if (!AppStatus.getInstance(context).isOnline(

                context)) {

            CommonUtil.noInternetDialog(context);

            return;

        }

        CommonUtil.loading_box(context, "Please wait...");

        AsyncHttpClient client = new AsyncHttpClient();

        // http://api.staging.xbowling.com/leaderboard/usbcintercollegiatesingles?state=tn&startIndex=0&pageSize=25&gender=m&token

        client.setTimeout(CommonUtil.TIMEOUT);

        client.post(context, Data.baseUrl + "userreferral/batch?token="
                        + CommonUtil.getAccessToken(context).replaceAll("[+]", "%2B")
                        + "&apiKey=" + Data.apiKey, emails, "application/json",

                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {

                        Log.v("Email response = ", response + ",");

                        CommonUtil.loading_box_stop();
                        CommonUtil.commonDialog(context, "Email",
                                "Email sent successfully!");
                        emailAddress.setText("");
                    }

                    @Override
                    public void onFailure(Throwable e) {

                        HttpResponseException hre = (HttpResponseException) e;

                        CommonUtil.loading_box_stop();

                        Log.v("Email response = " + hre.getStatusCode(),

                                e.toString());

                        CommonUtil
                                .commonDialog(context, "Sorry!",
                                        "An error occurred sending Email.  Please try again later!");

                    }

                });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                String previous = emailAddress.getText().toString().trim();
                if (!previous.isEmpty()) {
                    previous += ",";
                }
                emailAddress.setText(previous + data.getStringExtra("email"));

            }
        }
    }

}

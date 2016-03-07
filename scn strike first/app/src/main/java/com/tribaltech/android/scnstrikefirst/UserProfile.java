package com.tribaltech.android.scnstrikefirst;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.tribaltech.android.entities.Center;
import com.tribaltech.android.util.AppStatus;
import com.tribaltech.android.util.CircleTransform;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rmn.androidscreenlibrary.ASSL;


public class UserProfile extends MenuIntent {
    TextView header, edit, userName, emailLabel, email, screenLabel,
            screenName, homeHeader, homeName, homeAddress;
    ImageView userPhoto;
    Button changePassword;
    String nearCountry="";
    String nearState="";
    Center nearCenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        new ASSL(UserProfile.this, (ViewGroup) findViewById(R.id.root), 1134, 720,
                false);

        initComponent();

    }




    private void initComponent() {
//        Typeface font_regular = Typeface.createFromAsset(this.getAssets(), "fonts/avenir_next.otf");
//        Typeface font_bold = Typeface.createFromAsset(this.getAssets(), "fonts/avenir_bold.otf");

        header = (TextView) findViewById(R.id.header);
        edit = (TextView) findViewById(R.id.edit_profile);
        userName = (TextView) findViewById(R.id.username);
        emailLabel = (TextView) findViewById(R.id.email_label);
        email = (TextView) findViewById(R.id.email);
        screenLabel = (TextView) findViewById(R.id.screen_label);
        screenName = (TextView) findViewById(R.id.screen_name);
        homeHeader = (TextView) findViewById(R.id.home);
        homeName = (TextView) findViewById(R.id.house_name);
        homeAddress = (TextView) findViewById(R.id.area);
        userPhoto = (ImageView) findViewById(R.id.profile_dp);
        changePassword = (Button) findViewById(R.id.change_pwd);

        //header.setTypeface(font_regular);
//        edit.setTypeface(font_regular);
//        email.setTypeface(font_regular);
//        emailLabel.setTypeface(font_regular);
//        screenLabel.setTypeface(font_regular);
//        screenName.setTypeface(font_regular);
//        homeAddress.setTypeface(font_regular);
//        homeHeader.setTypeface(font_regular);
//        homeName.setTypeface(font_regular);
//        changePassword.setTypeface(font_bold);
//        userName.setTypeface(font_bold);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu:
                toggle();
                break;
            case R.id.edit_profile:
                Intent intent = new Intent(this, EditProfile.class);
                if(!nearCountry.isEmpty()) {
                    intent.putExtra("nearCountry", nearCountry);
                    intent.putExtra("nearState", nearState);
                    intent.putExtra("nearCenter", nearCenter);
                }
                startActivity(intent);
                break;
            case R.id.change_pwd:
                Intent intentCP = new Intent(this, ChangePassword.class);
                startActivity(intentCP);
                break;
            default:
                break;
        }
    }

    public void getProfileData() {
        if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                getApplicationContext())) {
            CommonUtil.noInternetDialog(UserProfile.this);
            return;
        }

        if (!CommonUtil.is_loading_showing())
            CommonUtil.loading_box(this, "Please wait...");
        RequestParams rv = new RequestParams();
        rv.put("token", CommonUtil.getAccessToken(getApplicationContext()));
        rv.put("apiKey", Data.apiKey);

        AsyncHttpClient client = new AsyncHttpClient();

        client.setTimeout(100000);
        client.get(Data.baseUrl + "userprofile", rv,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        try {
                            CommonUtil.loading_box_stop();
                            JSONObject js = new JSONObject(response);
                            CommonUtil.push(js.getString("screenName"));
                            CommonUtil.saveScreenName(
                                    js.getString("screenName"),
                                    getApplicationContext());
                            Data.email = js.getString("email");

                            email.setText(Data.email);
                            screenName.setText(Data.userName);
                            if (!js.isNull("firstName") || !js.isNull("lastName")) {
                                Data.firstName = js.getString("firstName");
                                Data.lastName = js.getString("lastName");
                                userName.setText(Data.firstName + " " +
                                        Data.lastName);
                            } else {
                                userName.setText(Data.userName);
                            }

                            if (!js.isNull("pictureFile")) {
                                Data.userImageUrl = js.getJSONObject(
                                        "pictureFile").getString("fileUrl");

                                Picasso.with(getApplicationContext())
                                        .load(Data.userImageUrl)
                                        .error(R.drawable.profile_icon_selector)
                                        .transform(new CircleTransform()).fit()
                                        .into(userPhoto);
                            }

                        } catch (Exception e) {
                            Log.v("response = ", e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.loading_box_stop();
                        CommonUtil.commonGameErrorDialog(UserProfile.this,
                                "An error occurred. Please try later.");
                        Picasso.with(getApplicationContext())
                                .load(Data.userImageUrl)
                                .error(R.drawable.profile_icon_selector)
                                .transform(new CircleTransform()).fit()
                                .into(userPhoto);
                    }

                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, ScreenMain.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        loadData();
        loadData();
        getProfileData();
        getHomeCenter();
    }

    private void loadData() {
        userName.setText(Data.firstName + " " +
                Data.lastName);
        Picasso.with(getApplicationContext())
                .load(Data.userImageUrl)
                .error(R.drawable.profile_icon_selector)
                .transform(new CircleTransform()).fit()
                .into(userPhoto);
        email.setText(Data.email);
        screenName.setText(Data.userName);
        homeName.setText(Data.center);
        homeAddress.setText(Data.country);
    }

    public void getHomeCenter() {
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(Data.baseUrl
                        + "MyCenter?token="
                        + CommonUtil.getAccessToken(getApplicationContext())
                        .replaceAll("[+]", "%2B") + "&apiKey=" + Data.apiKey,
                params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e("home response = ", response + ",");

                        JSONArray jsonArray;
                        JSONObject jObj;
                        try {
                            jObj = new JSONObject(response);
                            jsonArray = jObj.getJSONArray("table");

                            String logMessage = jObj.getString("table");

                            if (logMessage.equalsIgnoreCase("[]")) {
                                homeName.setText("You have not added any home center yet");
                                homeAddress.setVisibility(View.GONE);
                            } else {
                                homeAddress.setVisibility(View.VISIBLE);
                                JSONObject jObj1 = jsonArray.getJSONObject(0);
                                Log.v("JSON: ", jObj1.toString());
                                homeName.setText(jObj1.getString("name"));
                                homeAddress.setText(jObj1.getString("longName") + ", " + jObj1
                                        .getString("countryDisplayName"));
                                nearCountry = jObj1
                                        .getString("countryDisplayName");
                                nearCenter = new Center(
                                        jObj1.getString("name"), jObj1
                                        .getInt("id"), jObj1
                                        .getString("scoringType"));
                                nearState = jObj1.getString("longName");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        CommonUtil.commonGameErrorDialog(UserProfile.this,
                                "An error occured.Please try again.");
                        CommonUtil.loading_box_stop();
                    }
                });
    }
}

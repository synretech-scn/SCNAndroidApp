package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.ViewGroup;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tribaltech.android.util.AppStatus;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;

import org.json.JSONObject;

import java.security.MessageDigest;

import rmn.androidscreenlibrary.ASSL;


public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new ASSL(Splash.this, (ViewGroup) findViewById(R.id.root), 1134, 720,
                false);
        try {
            //zNeFQZm6Vlbcos2o8Hl0J3yX4AY=
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }catch(Exception e){
            e.printStackTrace();
        }



        Data.AccessToken = CommonUtil.getAccessToken(getApplicationContext());
        if (Data.AccessToken.isEmpty()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(getApplicationContext(), Login.class);
                    startActivity(i);
                    finish();
                }
            }, 2000);
        } else {
            if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                    getApplicationContext())) {
                CommonUtil.noInternetDialog(Splash.this);
                return;
            }
            getProfileData();
        }
    }

    public void getProfileData() {

//        if (!CommonUtil.is_loading_showing())
//            CommonUtil.loading_box(this, "Please wait...");
        RequestParams rv = new RequestParams();

        rv.put("token", CommonUtil.getAccessToken(getApplicationContext()));
        rv.put("apiKey", Data.apiKey);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(Data.baseUrl + "userprofile", rv,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {

                        Log.v("response = ", response + ",");
                        CommonUtil.loading_box_stop();
                        try {
                            JSONObject js = new JSONObject(response);
                            Data.userName = js.getString("screenName");
                            CommonUtil.saveScreenName(
                                    js.getString("screenName"),
                                    getApplicationContext());

                            // js.getJSONObject("pictureFile").getString("fileUrl")
                            if (!js.isNull("pictureFile")) {

                                Data.userImageUrl = js.getJSONObject(
                                        "pictureFile").getString("fileUrl");
                            }
                            // js.getString("email");
                            Data.email = js.getString("email");
                            CommonUtil.push(Data.email);

                        } catch (Exception e) {
                            Log.v("response = ", e.toString());
                        }
                        Log.v("Data.userImageUrl",
                                Data.userImageUrl);

                        Intent i = new Intent(getApplicationContext(),
                                ScreenMain.class);
                        startActivity(i);
                        finish();
                        /*overridePendingTransition(R.anim.from_right,
                                R.anim.to_left);*/
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.v("onFailure = ", e.toString());
                        CommonUtil.loading_box_stop();

                        Intent i = new Intent(getApplicationContext(),
                                Login.class);
                        startActivity(i);
                        finish();
                        /*overridePendingTransition(R.anim.from_right,
                                R.anim.to_left);*/

                    }

                });
    }
}

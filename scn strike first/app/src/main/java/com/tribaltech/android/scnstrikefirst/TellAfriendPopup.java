package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tribaltech.android.util.AppStatus;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;
import com.tribaltech.android.util.FacebookLoginCallback;

import org.apache.http.client.HttpResponseException;

import java.util.Arrays;

import rmn.androidscreenlibrary.ASSL;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

public class TellAfriendPopup extends Activity {

    Activity context;
    protected Dialog alert;

    public void onCreate(Bundle onSaveInstanceState) {
        super.onCreate(onSaveInstanceState);
        setContentView(R.layout.tell_a_friend_popup);
        context = this;
        new ASSL(TellAfriendPopup.this, (ViewGroup) findViewById(R.id.root), 1134, 720,
                false);

        Button email = (Button) findViewById(R.id.emailClick);

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TellAfriendPopup.this, TellViaEmailPopup.class);
                startActivity(intent);
            }
        });

        Button fb = (Button) findViewById(R.id.fbClick);

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbLogin();
                FacebookLoginCallback facebookLoginCallback = new FacebookLoginCallback() {
                    @Override
                    public void facebookLoginDone() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                context);
                        builder.setTitle("Post to Facebook")
                                .setMessage(
                                        "I am really enjoying my XBowling experience!  If you have not downloaded or activated your XBowling App, what are you waiting for?  I am ready to challenge you to a friendly game of bowling!  Download today: http://bit.ly/xbowlme")
                                .setCancelable(true)
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog, int id) {

                                                facebookPost(Session
                                                        .getActiveSession()
                                                        .getAccessToken());
                                            }
                                        })
                                .setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog, int id) {
                                                alert.cancel();
                                            }
                                        });
                        alert = builder.create();
                        alert.show();
                    }
                };
//                new FacebookLoginHelper().openFacebookSessionForPublish(TellAfriendPopup.this, facebookLoginCallback, false);
            }
        });

        Button tw = (Button) findViewById(R.id.twClick);

        tw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (LoginActivity.isActive(context)) {
                    twCheck();
                } else {
                    context.startActivityForResult(new Intent(context,
                            LoginActivity.class), 555);
                }
            }
        });
    }


    public void onClick(View view){
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 555) {
            if (isAuthenticated())
                twCheck();
        } else {
            Session.getActiveSession().onActivityResult(this,
                    requestCode,
                    resultCode, data);
        }
    }

    public void twCheck() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Post to Twitter")
                .setMessage(
                        "I'm really enjoying my XBowling experience! Have you downloaded your XBowling App yet? I'm ready to bowl against you! http://bit.ly/xbowlme")
                .setCancelable(true)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                twitterPost();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                alert.cancel();
                            }
                        });
        alert = builder.create();
        alert.show();
    }

    public void fbLogin() {

        Session session = new Session(context);
        Session.setActiveSession(session);
        Session.OpenRequest openRequest = null;
        openRequest = new Session.OpenRequest(context);
        openRequest.setPermissions(Arrays.asList("publish_actions","basic_info"));


        try {
            if (isSystemPackage(context.getPackageManager().getPackageInfo(
                    "com.facebook.katana", 0))) {
                openRequest.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
            } else {
            }

        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        openRequest.setCallback(new Session.StatusCallback() {

            @Override
            public void call(Session session, SessionState state,

                             Exception exception) {

                Log.v("callback = ", "callback");

                if (session.isOpened()) {
                    // publishFeedDialog();

                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            context);
                    builder.setTitle("Post to Facebook")
                            .setMessage(
                                    "I am really enjoying my XBowling experience!  If you have not downloaded or activated your XBowling App, what are you waiting for?  I am ready to challenge you to a friendly game of bowling!  Download today: http://bit.ly/xbowlme")
                            .setCancelable(true)
                            .setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog, int id) {

                                            facebookPost(Session
                                                    .getActiveSession()
                                                    .getAccessToken());
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog, int id) {
                                            alert.cancel();
                                        }
                                    });
                    alert = builder.create();
                    alert.show();

                }

            }

        });

        session.openForPublish(openRequest);

    }

    private void publishFeedDialog() {
        // PuzzlePic
        // Take a Pic, and Puzzle it!
        // I just finished solving a puzzle!

        Bundle params = new Bundle();
        params.putString("name", "XBowling");
        params.putString("caption", "Bowl. Have Fun. Win Prizes.");
        params.putString(
                "description",
                "I am really enjoying my XBowling experience!  If you have not downloaded or activated your XBowling App, what are you waiting for?  I am ready to challenge you to a friendly game of bowling!");
        params.putString("link", "http://www.xbowling.com/");
        params.putString(
                "picture",
                "https://pbs.twimg.com/profile_images/3117956421/917618ce9e303da42ead11884b0151df.png");

        Session session = Session.getActiveSession();

        WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(context,
                session, params)).setOnCompleteListener(
                new OnCompleteListener() {

                    @Override
                    public void onComplete(Bundle values,
                                           FacebookException error) {

                        if (error == null) {
                            // When the story is posted, echo the success
                            // and the post Id.
                            final String postId = values.getString("post_id");
                            if (postId != null) {
                                // Toast.makeText(WinActivity.this,
                                // "Posted on your wall" + postId,
                                // Toast.LENGTH_SHORT).show();
                                CommonUtil
                                        .commonDialog(context, "Facebook",
                                                "Successfully posted on your Facebook Wall.");

                                facebookPost(Session.getActiveSession()
                                        .getAccessToken());
                            } else {
                                // User clicked the Cancel button
                                // Toast.makeText(context.getApplicationContext(),
                                // "Publish cancelled", Toast.LENGTH_SHORT)
                                // .show();

                            }
                        } else if (error instanceof FacebookOperationCanceledException) {
                            // Toast.makeText(context.getApplicationContext(),
                            // "Publish cancelled", Toast.LENGTH_SHORT)
                            // .show();
                        } else {
                            Toast.makeText(context.getApplicationContext(),
                                    "Error posting story", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }

                }).build();
        feedDialog.show();
    }

    public void facebookPost(String token) {

        Session.getActiveSession().closeAndClearTokenInformation();

        if (!AppStatus.getInstance(context).isOnline(

                context)) {

            CommonUtil.noInternetDialog(context);

            return;

        }

        CommonUtil.loading_box(context, "Please wait...");

        RequestParams rv = new RequestParams();

        rv.put("token", CommonUtil.getAccessToken(context));

        rv.put("apiKey", Data.apiKey);

        rv.put("referralMessageType", "2");
        rv.put("accessToken", token);

        AsyncHttpClient client = new AsyncHttpClient();

        // http://api.staging.xbowling.com/leaderboard/usbcintercollegiatesingles?state=tn&startIndex=0&pageSize=25&gender=m&token

        client.setTimeout(CommonUtil.TIMEOUT);

        client.post(Data.baseUrl + "userreferral/facebook", rv,

                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        try {
                            Log.v("facebook response = ", response + ",");

                            CommonUtil.loading_box_stop();
                            // getUserCredit();
                            CommonUtil.commonDialog(context, "Facebook!",
                                    "Successfully posted on your wall!");
                            Session.getActiveSession().closeAndClearTokenInformation();
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        e.printStackTrace();
                        try {
                            HttpResponseException hre = (HttpResponseException) e;

                            CommonUtil.loading_box_stop();

                            if (hre.getStatusCode() == 400) {
                                CommonUtil
                                        .commonDialog(
                                                context,
                                                "Sorry!",
                                                "You cannot post the same message twice in a row to Facebook. Please try again after you have posted a different message at least once!");
                            } else if (hre.getStatusCode() == 403) {
                                // user has already
                                // posted the message,
                                // can't post a dupe

                                CommonUtil
                                        .commonDialog(
                                                context,
                                                "Sorry!",
                                                "You cannot post to social media more than 6 times in a 1 month period.  Please try again later!");

                            } else {
                                CommonUtil
                                        .commonDialog(context, "Sorry!",
                                                "An error occurred posting to Facebook.  Please try again later!");

                            }

                            Session.getActiveSession().closeAndClearTokenInformation();
                        } catch (Exception eq) {

                        }

                    }

                });

    }

    public void twitterPost() {

        // Session.getActiveSession().closeAndClearTokenInformation();

        if (!AppStatus.getInstance(context).isOnline(

                context)) {

            CommonUtil.noInternetDialog(context);

            return;

        }

        CommonUtil.loading_box(context, "Please wait...");

        RequestParams rv = new RequestParams();

        rv.put("token", CommonUtil.getAccessToken(context));

        rv.put("apiKey", Data.apiKey);

        rv.put("referralMessageType", "2");
        rv.put("accessTokenKey", LoginActivity.getAccessToken(context));

        rv.put("accessTokenSecret", LoginActivity.getAccessTokenSecret(context));

        AsyncHttpClient client = new AsyncHttpClient();

        // http://api.staging.xbowling.com/leaderboard/usbcintercollegiatesingles?state=tn&startIndex=0&pageSize=25&gender=m&token

        client.setTimeout(CommonUtil.TIMEOUT);

        client.post(Data.baseUrl + "userreferral/twitter", rv,

                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {

                        Log.v("twitter response = ", response + ",");

                        CommonUtil.loading_box_stop();
                        CommonUtil.commonDialog(context, "Twitter!",
                                "Tweet sent successfully!");

                    }

                    @Override
                    public void onFailure(Throwable e) {

                        HttpResponseException hre = (HttpResponseException) e;

                        CommonUtil.loading_box_stop();

                        Log.v("twitter response = " + hre.getStatusCode(),

                                e.toString());
                        if (hre.getStatusCode() == 409) {
                            CommonUtil
                                    .commonDialog(
                                            context,
                                            "Sorry!",
                                            "You cannot post the same message twice in a row to Twitter. Please try again after you have posted a different tweet at least once!");
                        } else if (hre.getStatusCode() == 403) {
                            CommonUtil
                                    .commonDialog(
                                            context,
                                            "Sorry!",
                                            "You cannot post to social media more than 6 times in a 1 month period.  Please try again later!");

                        } else {
                            CommonUtil
                                    .commonDialog(context, "Sorry!",
                                            "An error occurred posting to Twitter.  Please try again later!");

                        }

                    }

                });

    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
                : false;
    }

    public boolean isAuthenticated() {
        try {
            String token = LoginActivity
                    .getAccessToken(getApplicationContext());
            String secret = LoginActivity
                    .getAccessTokenSecret(getApplicationContext());

            Twitter twitter = new TwitterFactory().getInstance();
            twitter.setOAuthConsumer(
                    getResources().getString(R.string.twitter_consumer_key),
                    getResources().getString(R.string.twitter_consumer_secret));

            twitter.getAuthorization();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}

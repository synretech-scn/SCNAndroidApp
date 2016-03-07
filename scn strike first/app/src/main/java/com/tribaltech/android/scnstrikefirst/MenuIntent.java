package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.Session;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tribaltech.android.util.AppConstants;
import com.tribaltech.android.util.AppStatus;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;
import com.tribaltech.android.util.FilterItem;

import rmn.androidscreenlibrary.ASSL;

public class MenuIntent extends SlidingFragmentActivity implements View.OnFocusChangeListener {

    AlertDialog alert;
    TextView notificationCountText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBehindContentView(R.layout.sidebar);
//        setupUI(findViewById(R.id.root));
        // roo = (FrameLayout) findViewById(R.id.frameLayout);
        new ASSL(this, (ViewGroup) findViewById(R.id.root),
                AppConstants.SCREEN_HEIGHT, AppConstants.SCREEN_WIDTH, false);
        notificationCountText = (TextView) findViewById(R.id.notificationCount);
        // Regular =
        // Typeface.createFromAsset(getAssets(),"MyriadPro-Regular.otf");
        SlidingMenu sm = getSlidingMenu();
        // sm.setShadowWidthRes(R.dimen.menu_header_line);
        // sm.setShadowDrawable(R.drawable.ic_launcher);
        // sm.setBehindOffsetRes(R.dimen.menu_icon_side);
        sm.setMode(SlidingMenu.LEFT_RIGHT);
//		sm.setMenu(R.layout.sidebar);
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sideMenu = inflater.inflate(R.layout.gamescreen_side, null);
        sideMenu.setLayoutParams(new android.view.ViewGroup.LayoutParams((int) (610 * ASSL.Xscale()), ViewGroup.LayoutParams.MATCH_PARENT));
        ASSL.DoMagic(sideMenu);
//		sm.setSecondaryMenu(R.layout.gamescreen_side);
        sm.setSecondaryMenu(sideMenu);
//        sm.setSecon
        sm.setFadeDegree(0.50f);
        sm.setBehindWidth((int) (610 * ASSL.Xscale()));
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);

        findViewById(R.id.home).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Data.currentContext,
                        ScreenMain.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.goBowling).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (Data.currentContext instanceof GameScreen || Data.currentContext instanceof GoBowling) {
                            toggle();
                        } else {
                            if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                                    getApplicationContext())) {
                                CommonUtil.noInternetDialog(MenuIntent.this);
                                return;
                            }
                            Intent intent = new Intent(Data.currentContext,
                                    GoBowling.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });

        findViewById(R.id.liveScore).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (Data.currentContext instanceof LiveScore) {
                            toggle();
                        } else {
                            Intent intent = new Intent(Data.currentContext,
                                    LiveScore.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });

        findViewById(R.id.xbpro).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (Data.currentContext instanceof UserStats) {
                            toggle();
                        } else {
                            if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                                    getApplicationContext())) {
                                CommonUtil.noInternetDialog(MenuIntent.this);
                                return;
                            }
                            Intent intent = new Intent(Data.currentContext,
                                    UserStats.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });

        findViewById(R.id.userProfile).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (Data.currentContext instanceof UserProfile) {
                            toggle();
                        } else {
                            if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                                    getApplicationContext())) {
                                CommonUtil.noInternetDialog(MenuIntent.this);
                                return;
                            }
                            Intent intent = new Intent(Data.currentContext,
                                    UserProfile.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });

        findViewById(R.id.notificationParent).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (Data.currentContext instanceof Notifications) {
                            toggle();
                        } else {
                            Intent intent = new Intent(Data.currentContext,
                                    Notifications.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });

        findViewById(R.id.logout).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        logout();
                    }
                });



        //add

        findViewById(R.id.rewardpoints).setOnClickListener(
                new View.OnClickListener()  {
                    @Override
                    public void onClick(View v)
                    {
                        if (Data.currentContext instanceof Wallet) {
                             toggle();
                         } else
                        {
                            if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                                    getApplicationContext())) {
                                CommonUtil.noInternetDialog(MenuIntent.this);
                                return;
                            }

                            Intent intent = new Intent(Data.currentContext,
                                    Wallet.class);
                            // Intent intent = new Intent(this, Wallet.class);
                            startActivity(intent);
                            finish();
                            //break;
                        }//else
        }
    });


//add
        findViewById(R.id.coupon).setOnClickListener(
                new View.OnClickListener()  {
        @Override
        public void onClick(View v)
        {

            // if (Data.currentContext instanceof Coupon) {
            // toggle();
            //} else
            {
                if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                        getApplicationContext())) {
                    CommonUtil.noInternetDialog(MenuIntent.this);
                    return;
                }

                Intent intent = new Intent(Data.currentContext,
                        webView.class);
                // Intent intent = new Intent(this, webView.class);
                intent.putExtra("url", "http://xbowling-mobile.trafficmanager.net/Coupon/Index?VenueId=15103&Token=" + CommonUtil.getAccessToken(getApplicationContext()).replaceAll("[+]", "%2B") + "&apiKey=" + Data.apiKey);

                Log.v("get coupon url = ", "http://xbowling-mobile.trafficmanager.net/Coupon/Index?VenueId=15103&Token=" + CommonUtil.getAccessToken(getApplicationContext()).replaceAll("[+]", "%2B") + "&apiKey=" + Data.apiKey);
                //    intent.putExtra("url", "http://xbowling-mobile.trafficmanager.net/Coupon/Index?VenueId=15103&Token="+CommonUtil.getAccessToken(getApplicationContext())                         .replaceAll("[+]", "%2B")         +"&apiKey="+Data.apiKey);

/*
                rv.put("VenueId",  "15103");
                rv.put("apiKey", Data.apiKey);
                rv.put("token", CommonUtil.getAccessToken(getApplicationContext())
                        .replaceAll("[+]", "%2B"));
*/
                startActivity(intent);
                finish();
                //break;
            }//else
        }
});


    }

    public void setParentTouch() {
        View view = findViewById(R.id.root);
        if (view != null) {
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.setClickable(true);
        }
    }

    public void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
//                    CommonUtil.hideSoftKeyboard(MenuIntent.this);
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView);
            }
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home: {
                Intent intent = new Intent(Data.currentContext, ScreenMain.class);
                startActivity(intent);
                finish();
                break;
            }

            case R.id.liveScore: {
                Intent intent = new Intent(this, LiveScore.class);
                startActivity(intent);
                finish();
                break;
            }

            case R.id.goBowling: {
                Intent intent = new Intent(this, GoBowling.class);
                startActivity(intent);
                finish();
                break;
            }
//add

            case R.id.rewardpoints: {
               /* if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                        getApplicationContext())) {
                    CommonUtil.noInternetDialog(ScreenMain.this);
                    return;
                }
                */
                Intent intent = new Intent(this, Wallet.class);
                startActivity(intent);
                finish();
                break;
            }



//add

            case R.id.coupon: {


                /*if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                        getApplicationContext())) {
                    CommonUtil.noInternetDialog(ScreenMain.this);
                    return;
                }
*/
                Intent intent = new Intent(this, webView.class);
                intent.putExtra("url", "http://xbowling-mobile.trafficmanager.net/Coupon/Index?VenueId=15103&Token="+CommonUtil.getAccessToken(getApplicationContext()) .replaceAll("[+]", "%2B")         +"&apiKey="+Data.apiKey);

                Log.v("get coupon url = ", "http://xbowling-mobile.trafficmanager.net/Coupon/Index?VenueId=15103&Token=" + CommonUtil.getAccessToken(getApplicationContext()).replaceAll("[+]", "%2B") + "&apiKey=" + Data.apiKey);
                //    intent.putExtra("url", "http://xbowling-mobile.trafficmanager.net/Coupon/Index?VenueId=15103&Token="+CommonUtil.getAccessToken(getApplicationContext())                         .replaceAll("[+]", "%2B")         +"&apiKey="+Data.apiKey);

/*
                rv.put("VenueId",  "15103");
                rv.put("apiKey", Data.apiKey);
                rv.put("token", CommonUtil.getAccessToken(getApplicationContext())
                        .replaceAll("[+]", "%2B"));
*/
                startActivity(intent);
                finish();
                break;
            }


            default:
                break;
        }
    }

    public void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm")
                .setMessage("Are you sure you want to logout?")
                .setCancelable(true)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                logoutCall();
                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alert.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }

    private void clearData() {
        Data.AccessToken = "";
        Data.email = "";
        CommonUtil.saveAccessToken("", MenuIntent.this);
        CommonUtil.saveFilter(new FilterItem(), MenuIntent.this);
        Data.userImageUrl = "http://api.xbowling.com/";
        Data.userStatsSubscribed = false;
        Data.trialPurchased = false;
        Data.statsStatusChecked = false;
        Data.userName = "";
        Data.firstName = "";
        Data.lastName="";

        try {
            Session session = new Session(MenuIntent.this);
            Session.setActiveSession(session);
            session.closeAndClearTokenInformation();

        } catch (Exception e) {
        }

        Intent i = new Intent(getApplicationContext(), Login.class);
        startActivity(i);
        finish();
    }

    public void logoutCall() {

        if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                getApplicationContext())) {
            clearData();
            return;
        }

        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(MenuIntent.this, "Please wait...");
        }

        AsyncHttpClient client = new AsyncHttpClient();

        client.setTimeout(CommonUtil.TIMEOUT);
        client.post(
                Data.baseUrl
                        + "EnterInCenter/Logout?token="
                        + CommonUtil.getAccessToken(getApplicationContext())
                        .replace("+", "%2B") + "&apiKey=" + Data.apiKey,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        clearData();
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        clearData();
                    }

                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Data.currentContext = this;
        updateNotifications(this);
    }

    public static void updateNotifications(Context ctx) {
        Button menu = (Button) ((Activity) ctx).findViewById(R.id.menu);
        int diameter = (int) (40 * ASSL.Xscale());
        if (menu != null) {
            ViewGroup parent = (ViewGroup) menu.getParent();
            Button button = (Button) parent.findViewById(R.id.notification_button);
            Button overlay = (Button) parent.findViewById(R.id.transparent_overlay);
            TextView tv = (TextView) ((Activity) ctx).findViewById(R.id.notificationCount);

            if (menu.getVisibility() != View.VISIBLE) {
                if (button != null) {
                    button.setVisibility(View.GONE);
                }

                if (overlay != null) {
                    overlay.setVisibility(View.GONE);
                }

                if (tv != null) {
                    tv.setVisibility(View.GONE);
                }
                return;

            }

            if (overlay == null) {
                overlay = new Button(ctx);
                overlay.setId(R.id.transparent_overlay);
                overlay.setPadding(0, 0, 0, 0);
                overlay.setBackgroundResource(0);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (95 * ASSL.Xscale()),
                        ViewGroup.LayoutParams.MATCH_PARENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                overlay.setLayoutParams(params);
                overlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((SlidingFragmentActivity) Data.currentContext).toggle();
                    }
                });
                parent.addView(overlay);
            }

            if (Data.notificationCount != 0) {
                if (button == null) {
                    button = new Button(ctx);
                    button.setId(R.id.notification_button);
                    button.setPadding(0, 0, 0, 0);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(diameter, diameter);
                    params.setMargins(-1 * diameter / 2, (int) (22.5 * ASSL.Yscale() - diameter / 2), 0, 0);
                    params.addRule(RelativeLayout.RIGHT_OF, menu.getId());
                    button.setLayoutParams(params);
                    button.setBackgroundResource(R.drawable.notification_icon_bg);
                    button.setSingleLine(true);
                    button.setClickable(false);
                    button.setFocusable(false);
                    button.setTextSize(TypedValue.COMPLEX_UNIT_PX, 20 * ASSL.Xscale());
                    button.setTextColor(ctx.getResources().getColor(R.color.white));
                    parent.addView(button);
                }

                button.setVisibility(View.VISIBLE);
                button.setText(Data.notificationCount + "");

                tv.setText(Data.notificationCount + "");
                tv.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Data.currentContext = null;
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            CommonUtil.hideSoftKeyboard(this, v);
        }
    }
}
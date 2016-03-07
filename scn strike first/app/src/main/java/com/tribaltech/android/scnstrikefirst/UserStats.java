package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.tribaltech.android.util.AppConstants;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;
import com.tribaltech.android.util.FilterItem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import rmn.androidscreenlibrary.ASSL;

public class UserStats extends MenuIntent {

    int currentBackground;
    LinearLayout tabParent;
    LinearLayout statsTabParent;
    private int statsTabCount;
    private int tabCount;
    int orientation;
    Fragment currentFragment;
    static final String SUMMARY_TAG = "summaryFragment";
    static final String PIN_STATS_TAG = "pinStatsFragment";
    Map<Integer, Fragment> fragmentMap = new HashMap<>();
    FragmentTransaction transaction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.orientation = getResources().getConfiguration().orientation;
        setContentView(R.layout.activity_user_stats);
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            new ASSL(UserStats.this, (ViewGroup) findViewById(R.id.root),
                    AppConstants.SCREEN_HEIGHT, AppConstants.SCREEN_WIDTH,
                    false);
        } else {
            new ASSL(UserStats.this, (ViewGroup) findViewById(R.id.root),
                    AppConstants.SCREEN_WIDTH, 1196,
                    false);
        }

        tabParent = (LinearLayout) findViewById(R.id.tabParent);
        statsTabParent = (LinearLayout) findViewById(R.id.statsTabParent);
        tabCount = tabParent.getChildCount();
        statsTabCount = statsTabParent.getChildCount();

        if (savedInstanceState == null) {
            currentBackground = R.id.gamesTab;
//            getFragmentManager().beginTransaction()
//                    .add(R.id.contentArea, new StatsFragment()).commit();
        } else {
            currentBackground = savedInstanceState.getInt("background",
                    R.id.gamesTab);
        }
//        fragmentMap.put(R.id.gamesTab, new MyGamesFragment());
//        fragmentMap.put(R.id.statsTab, new StatsFragment());
//        fragmentMap.put(R.id.noStatsFragment, new NoStatsFragment());
//        fragmentMap.put(R.id.pinStats, new PinStatsFragment());
//        fragmentMap.put(R.id.compareTab, new CompareFragment());
//        fragmentMap.put(R.id.settingsTab, new SettingsFragment());
//        fragmentMap.put(R.id.summary, new StatsFragment());
//        toggleMainTabs(currentBackground);
//        transaction = getFragmentManager().beginTransaction();
//        for (Map.Entry<Integer, Fragment> entry : fragmentMap.entrySet()) {
//            transaction.add(R.id.contentArea, entry.getValue());
//        }
//        transaction.commit();

        statsTab(findViewById(R.id.statsTab));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("background", currentBackground);
    }

    public void statsTab(View v) {
        Fragment fragment;
        transaction = getFragmentManager().beginTransaction();
        for (Map.Entry<Integer, Fragment> entry : fragmentMap.entrySet()) {
            transaction.hide(entry.getValue());
        }

        switch (v.getId()) {
            case R.id.gamesTab:
                toggleMainTabs(v.getId());
                fragment = fragmentMap.get(R.id.gamesTab);
                if (fragment == null) {
                    fragment = new MyGamesFragment();
                    transaction.add(R.id.contentArea, fragment);
                    fragmentMap.put(R.id.gamesTab, fragment);
                }
//                getFragmentManager().beginTransaction()
//                        .replace(R.id.contentArea, fragment).commit();
                transaction.show(fragment).commit();
                currentBackground = R.id.gamesTab;
                break;

            case R.id.statsTab:
                toggleMainTabs(v.getId());
                if (Data.userStatsSubscribed) {
                    toggleStatsTab(R.id.summary);
                    fragment = fragmentMap.get(R.id.statsTab);
                    if (fragment == null) {
                        fragment = new StatsFragment();
                        transaction.add(R.id.contentArea, fragment);
                        fragmentMap.put(R.id.statsTab, fragment);
                    }
//                    getFragmentManager().beginTransaction()
//                            .replace(R.id.contentArea, fragment).commit();
                    transaction.show(fragment).commit();
                } else {
                    noStatsFragent();
                }
                currentBackground = R.id.statsTab;
                break;

            case R.id.summary:
                toggleStatsTab(v.getId());
                fragment = fragmentMap.get(R.id.statsTab);
                if (fragment == null) {
                    fragment = new StatsFragment();
                    transaction.add(R.id.contentArea, fragment);
                    fragmentMap.put(R.id.statsTab, fragment);
                }
//                getFragmentManager().beginTransaction()
//                        .replace(R.id.contentArea, new StatsFragment()).commit();
                transaction.show(fragment).commit();
                break;

            case R.id.graphs:
                Intent intent = new Intent(this, GraphActivity.class);
                startActivity(intent);
                break;

            case R.id.pinStats:
                toggleStatsTab(v.getId());
                fragment = fragmentMap.get(R.id.pinStats);
                if (fragment == null) {
                    fragment = new PinStatsFragment();
                    transaction.add(R.id.contentArea, fragment);
                    fragmentMap.put(R.id.pinStats, fragment);
                }
//                getFragmentManager().beginTransaction()
//                        .replace(R.id.contentArea, new PinStatsFragment()).commit();
                transaction.show(fragment).commit();
                break;

            case R.id.compareTab:
                toggleMainTabs(v.getId());
                if (Data.userStatsSubscribed) {
                    fragment = fragmentMap.get(R.id.compareTab);
                    if (fragment == null) {
                        fragment = new CompareFragment();
                        transaction.add(R.id.contentArea, fragment);
                        fragmentMap.put(R.id.compareTab, fragment);
                    }
//                    getFragmentManager().beginTransaction()
//                            .replace(R.id.contentArea, fragment).commit();
                    transaction.show(fragment).commit();
                } else {
                    noStatsFragent();
                }
                currentBackground = R.id.compareTab;
                break;

            case R.id.settingsTab:
                toggleMainTabs(v.getId());
                if (Data.userStatsSubscribed) {
                    fragment = fragmentMap.get(R.id.settingsTab);
                    if (fragment == null) {
                        fragment = new SettingsFragment();
                        transaction.add(R.id.contentArea, fragment);
                        fragmentMap.put(R.id.settingsTab, fragment);
                    }
//                    getFragmentManager().beginTransaction()
//                            .replace(R.id.contentArea, fragment).commit();
                    transaction.show(fragment).commit();
                } else {
                    noStatsFragent();
                }
                currentBackground = R.id.settingsTab;
                break;

        }
    }

    private void noStatsFragent() {
        Fragment fragment = null;
        fragment = fragmentMap.get(R.id.noStatsFragment);
        if (fragment == null) {
            fragment = new NoStatsFragment();
            transaction.add(R.id.contentArea, fragment);
            fragmentMap.put(R.id.noStatsFragment, fragment);
        }
//        getFragmentManager().beginTransaction()
//                .replace(R.id.contentArea, new NoStatsFragment()).commit();
        transaction.show(fragment).commit();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.filter: {
                if (Data.userStatsSubscribed || currentBackground == R.id.gamesTab) {
                    Intent intent = new Intent(this, FilterActivity.class);
                    startActivityForResult(intent, 1);
                }
                break;
            }
        }
    }

    private void toggleMainTabs(int tabId) {
        for (int i = 0; i < tabCount; i++) {
            if (i == 0 || (i == tabCount - 1)) {
                if (tabId == tabParent.getChildAt(i).getId()) {
                    tabParent.getChildAt(i).setBackgroundResource(i == 0 ? R.drawable.blue_left_semi_round :
                            R.drawable.blue_right_semi_round);
                } else {
                    tabParent.getChildAt(i).setBackgroundResource(i == 0 ? R.drawable.blue_left_outline_semi_round :
                            R.drawable.blue_right_outline_semi_round);
                }
            } else {
                if (tabId == tabParent.getChildAt(i).getId()) {
                    tabParent.getChildAt(i).setBackgroundResource(R.drawable.blue_rectangle);
                } else {
                    tabParent.getChildAt(i).setBackgroundResource(R.drawable.blue_outline);
                }
            }
        }
        statsTabParent.setVisibility(tabId == R.id.statsTab &&
                Data.userStatsSubscribed ? View.VISIBLE : View.GONE);
        if (tabId == R.id.settingsTab || tabId == R.id.compareTab) {
            findViewById(R.id.filter).setVisibility(View.GONE);
        } else {
            findViewById(R.id.filter).setVisibility(View.VISIBLE);
        }
    }

    private void toggleStatsTab(int tabId) {
        for (int i = 0; i < statsTabCount; i++) {
            statsTabParent.getChildAt(i).setBackgroundResource(tabId == statsTabParent.getChildAt(i).getId() ?
                    R.drawable.selected_background : R.drawable.pending_background);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (true || resultCode == Activity.RESULT_OK) {
                    Fragment currentFragment = getFragmentManager().findFragmentById(R.id.contentArea);
                    if (currentFragment instanceof Filterable) {
                        ((Filterable) currentFragment).filter((FilterItem) data
                                .getSerializableExtra("filter"));
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                }
                break;

            default:
                try {
                    Session.getActiveSession().onActivityResult(this, requestCode,
                            resultCode, data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }


    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
                : false;
    }

    public void fbLogin() {

        Session session = new Session(UserStats.this);
        Session.setActiveSession(session);
        Session.OpenRequest openRequest = null;
        openRequest = new Session.OpenRequest(UserStats.this);
        openRequest.setPermissions(Arrays.asList("publish_actions", "basic_info"));

        try {

            if (isSystemPackage(UserStats.this.getPackageManager()
                    .getPackageInfo("com.facebook.katana", 0))) {
                openRequest.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
            } else {
//                openRequest.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
            }
//            openRequest.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
        } catch (NameNotFoundException e) {
            e.printStackTrace();

        }

        openRequest.setCallback(new Session.StatusCallback() {

            @Override
            public void call(Session session, SessionState state,

                             Exception exception) {

                // Log.v("callback = ", "callback");

                if (session.isOpened()) {
                    // facebookPost(session.getAccessToken());
                    publishFeedDialog();
                }

            }

        });

        session.openForPublish(openRequest);

    }

    private void publishFeedDialog() {

        Bundle params = new Bundle();
        params.putString("name", "XBowling");
        params.putString("caption", "XB Pro");
        params.putString(
                "description",
                "Hey!! I am using XB Pro Package to analyze my advanced stats to gain better insights into my performance.");
        params.putString(
                "link",
                "https://play.google.com/store/apps/details?id=com.tribaltech.android.scnstrikefirst&hl=en");
//		params.putString("picture", "");

        Session session = Session.getActiveSession();

        WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(UserStats.this,
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
                                // Toast.makeText(UserStats.this,
                                // "Posted on your wall" + postId,
                                // Toast.LENGTH_SHORT).show();
                                CommonUtil
                                        .commonDialog(UserStats.this, null,
                                                "Successfully posted on your Facebook Wall.");
                            } else {
                                // User clicked the Cancel button
                                Toast.makeText(
                                        UserStats.this.getApplicationContext(),
                                        "Publish cancelled", Toast.LENGTH_SHORT)
                                        .show();

                            }
                        } else if (error instanceof FacebookOperationCanceledException) {
                            Toast.makeText(
                                    UserStats.this.getApplicationContext(),
                                    "Publish cancelled", Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            Toast.makeText(
                                    UserStats.this.getApplicationContext(),
                                    "Error posting story", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }

                }).build();
        feedDialog.show();
    }

    public void toggle(View v) {
        toggle();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), ScreenMain.class);
        startActivity(i);
        finish();
    }

    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    public interface Filterable {
        void filter(FilterItem filterItem);
    }

}

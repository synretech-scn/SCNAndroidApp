package com.tribaltech.android.scnstrikefirst;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.tribaltech.android.util.AppStatus;
import com.tribaltech.android.util.CommonUtil;

import rmn.androidscreenlibrary.ASSL;

public class LiveScore extends MenuIntent {

    CenterFragment centerFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_score);
        new ASSL(LiveScore.this, (ViewGroup) findViewById(R.id.root), 1134,
                720, false);
        centerFragment = new CenterFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("fromLive", true);
        centerFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.centerfragmentParent, centerFragment).commit();
        centerFragment.load();
    }

    public void toggle(View v) {
        toggle();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.getLiveScore: {
                if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                        getApplicationContext())) {
                    CommonUtil.noInternetDialog(LiveScore.this);
                    return;
                }
                Intent intent = new Intent(LiveScore.this, LiveScoreList.class);
                intent.putExtra("center", centerFragment.getSelectedCenter());
                startActivity(intent);
                break;
            }
            case R.id.globalLeaderboard: {
                Intent intent = new Intent(LiveScore.this, Leaderboard.class);
                startActivity(intent);
                break;
            }

            case R.id.centerLeaderboard: {
                Intent intent = new Intent(LiveScore.this, CenterLeaderboard.class);
                startActivity(intent);
                break;
            }

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, ScreenMain.class);
        startActivity(intent);
        finish();
    }
}
package com.tribaltech.android.scnstrikefirst;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;

import com.tribaltech.android.entities.Center;

import rmn.androidscreenlibrary.ASSL;


public class CenterLeaderboard extends FragmentActivity {

    CenterFragment centerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center_leaderboard);
        new ASSL(CenterLeaderboard.this, (ViewGroup) findViewById(R.id.root), 1134,
                720, false);
        centerFragment = new CenterFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.centerfragmentParent, centerFragment).commit();
        centerFragment.load();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;

            case R.id.getLeaderboard:
                if (centerFragment.centerList.size() > 0) {
                    Center center = centerFragment.getSelectedCenter();
                    Intent intent = new Intent(CenterLeaderboard.this, Leaderboard.class);
                    intent.putExtra("center", true);
                    intent.putExtra("venueId", center.id);
                    intent.putExtra("venueName", center.name);
                    startActivity(intent);
                }
                break;

        }

    }
}

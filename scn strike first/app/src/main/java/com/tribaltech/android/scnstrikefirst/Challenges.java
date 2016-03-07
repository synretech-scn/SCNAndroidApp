package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tribaltech.android.util.Data;

import rmn.androidscreenlibrary.ASSL;


public class Challenges extends Activity {

    String bowlingGameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenges);
        new ASSL(this, (ViewGroup) findViewById(R.id.root), 1134,
                720, false);
        bowlingGameId = Data.gameData.gameId;
        ((Button) findViewById(R.id.enterh2hPosted)).setText(Data.postedEntered ? "View" : "Enter");
        ((Button) findViewById(R.id.enterh2hLive)).setText(!Data.gameData.liveGameId.isEmpty() ? "View" : "Enter");
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.enterh2hLive: {
                Intent intent = new Intent(this, ChallengeView.class);
                startActivity(intent);
                finish();
                break;
            }

            case R.id.enterh2hPosted: {
                Intent intent = new Intent(this, H2HPostedMain.class);
                startActivity(intent);
                finish();
                break;
            }

            case R.id.back: {
                Intent intent = new Intent(this, GameScreen.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, GameScreen.class);
        startActivity(intent);
        finish();
    }
}

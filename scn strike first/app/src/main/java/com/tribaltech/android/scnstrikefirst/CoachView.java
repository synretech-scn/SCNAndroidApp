package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tribaltech.android.util.CoachViewAdapter;
import com.tribaltech.android.util.CoachViewItem;

import java.util.ArrayList;

import rmn.androidscreenlibrary.ASSL;


public class CoachView extends Activity {

    CoachViewAdapter adapter;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_view);
        new ASSL(CoachView.this, (ViewGroup) findViewById(R.id.root), 720, 1196,
                false);
        ((TextView) findViewById(R.id.centerName)).setText(getIntent().getStringExtra("centerName"));
        list = (ListView) findViewById(R.id.coachViewList);
        adapter = new CoachViewAdapter(CoachView.this, new ArrayList<CoachViewItem>(),
                getIntent().getIntExtra("venueId", 0) + "", getIntent().getStringExtra("laneNumber"),
                (RelativeLayout) findViewById(R.id.noItems));
        list.setAdapter(adapter);
        adapter.getLiveScoreUpdate(getIntent().getIntExtra("venueId", 0) + "",
                getIntent().getStringExtra("laneNumber"), true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.startUpdating();
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopUpdating();
        }
    }
}

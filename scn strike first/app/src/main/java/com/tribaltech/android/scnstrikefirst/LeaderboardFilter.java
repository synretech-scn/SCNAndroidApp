package com.tribaltech.android.scnstrikefirst;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.tribaltech.android.entities.LeaderboardFilterItem;
import com.tribaltech.android.util.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rmn.androidscreenlibrary.ASSL;


public class LeaderboardFilter extends FragmentActivity {

    LeaderboardCenterFragment centerFragment;
    Spinner type;
    Spinner bowlers;
    GoBowling.CustomAdapter<String> typeAdapter;
    GoBowling.CustomAdapter<String> bowlersAdapter;
    Map<Integer, String[]> typeMap = new HashMap<>();
    TextView typeText;
    TextView bowlersText;
    boolean centerLeaderboard;
    Integer venueId = 0;
    String venueName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard_filter);
        new ASSL(LeaderboardFilter.this, (ViewGroup) findViewById(R.id.root), 1134,
                720, false);
        centerLeaderboard = getIntent().getBooleanExtra("center", false);
        if (!centerLeaderboard) {
            centerFragment = new LeaderboardCenterFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.centerfragmentParent, centerFragment).commit();
        } else {
            venueId = getIntent().getIntExtra("venueId", 0);
            venueName = getIntent().getStringExtra("venueName");
        }
        type = (Spinner) findViewById(R.id.leaderboardType);
        bowlers = (Spinner) findViewById(R.id.bowlers);
        typeText = (TextView) findViewById(R.id.typeText);
        bowlersText = (TextView) findViewById(R.id.bowlersText);
        type.setOnItemSelectedListener(typeListener);
        bowlers.setOnItemSelectedListener(bowlersListener);

        List<String> bowlerTypeList = new ArrayList<String>();
        bowlerTypeList.add("All XBowlers");
        bowlerTypeList.add("My Friends");
        bowlersAdapter = new GoBowling.CustomAdapter<String>(
                LeaderboardFilter.this,
                android.R.layout.simple_spinner_item,
                bowlerTypeList);
        bowlersAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bowlers.setAdapter(bowlersAdapter);
        bowlers.setSelection(Data.leaderboardFilter.allBowlers ? 0 : 1);

        List<String> leaderboardTypeList = new ArrayList<String>();
        leaderboardTypeList.add("All Time Score");
        typeMap.put(0, new String[]{"alltimescore", "Score"});
        leaderboardTypeList.add("Strike King");
        typeMap.put(1, new String[]{"strikeking", "Strikes"});
        leaderboardTypeList.add("Spare King");
        typeMap.put(2, new String[]{"spareking", "Spares"});
        leaderboardTypeList.add("Points Won");
        typeMap.put(3, new String[]{"points", "Points"});
        leaderboardTypeList.add("Challenges Played");
        typeMap.put(4, new String[]{"challengesplayed", "Challenges"});
        leaderboardTypeList.add("Challenges Won");
        typeMap.put(5, new String[]{"challengeswon", "Challenges"});
        leaderboardTypeList.add("XB 300 Club");
        typeMap.put(6, new String[]{"xb300club", "Games"});

        typeAdapter = new GoBowling.CustomAdapter<String>(
                LeaderboardFilter.this,
                android.R.layout.simple_spinner_item,
                leaderboardTypeList);
        typeAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(typeAdapter);
        type.setSelection(leaderboardTypeList.indexOf(Data.leaderboardFilter.leaderboardTypeName));
        if (!centerLeaderboard) {
            centerFragment.load();
        }
    }

    AdapterView.OnItemSelectedListener typeListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1,
                                   int position, long arg3) {
            typeText.setText(type.getItemAtPosition(
                    position).toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };

    AdapterView.OnItemSelectedListener bowlersListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1,
                                   int position, long arg3) {
            bowlersText.setText(bowlers.getItemAtPosition(
                    position).toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;

            case R.id.done:
                String[] selectedIds = null;
                String[] str = typeMap.get(type.getSelectedItemPosition());
                if (centerLeaderboard) {
                    Data.leaderboardFilter = new LeaderboardFilterItem(str[0], type.getSelectedItem().toString(),
                            str[1], venueId.toString(), venueName);
                } else {
                    selectedIds = centerFragment.getSelectedIds();
                    Data.leaderboardFilter = new LeaderboardFilterItem(str[0], type.getSelectedItem().toString(),
                            str[1], bowlers.getSelectedItemPosition() == 0,
                            selectedIds[0], selectedIds[2], selectedIds[4], selectedIds[1], selectedIds[3], selectedIds[5]);
                }
                setResult(RESULT_OK);
                finish();
                break;
        }
    }
}

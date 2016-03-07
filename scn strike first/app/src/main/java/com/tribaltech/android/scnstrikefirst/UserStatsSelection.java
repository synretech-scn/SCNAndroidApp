package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.tribaltech.android.util.Data;
import com.tribaltech.android.util.UserStatsAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rmn.androidscreenlibrary.ASSL;


public class UserStatsSelection extends Activity {

    String ballNameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.equip_detail_popup);
        new ASSL(UserStatsSelection.this, (ViewGroup) findViewById(R.id.root), 1134,
                720, false);
        final Spinner bowlingBall = (Spinner) findViewById(R.id.bowlingBallList);
        final Spinner patternName = (Spinner) findViewById(R.id.patternNameList);
        final Spinner patternLength = (Spinner) findViewById(R.id.patternLengthList);
        final Spinner competitionType = (Spinner) findViewById(R.id.competitionTypeList);

        final EditText ballText = (EditText) findViewById(R.id.ballText);
        final EditText patternText = (EditText) findViewById(R.id.patternText);
        final EditText patLengthText = (EditText) findViewById(R.id.patLengthText);
        final EditText typeText = (EditText) findViewById(R.id.typeText);

        final boolean ballType = getIntent().getBooleanExtra("ballType", false);
        final boolean oilPattern = getIntent().getBooleanExtra("oilPattern", false);

        if (!ballType) {
            bowlingBall.setEnabled(ballType);
            ballText.setTextColor(Color.GRAY);
        }

        if (!oilPattern) {
            patternName.setEnabled(oilPattern);
            patternLength.setEnabled(oilPattern);
            patternText.setTextColor(Color.GRAY);
            patLengthText.setTextColor(Color.GRAY);
        }

        Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        Button ok = (Button) findViewById(R.id.okEquip);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] ball = (String[]) bowlingBall.getSelectedItem();
                if (ball == null) {
                    Data.ballId = "";
                } else {
                    Data.ballId = ball[0];
                }

                String[] ptrName = (String[]) patternName.getSelectedItem();
                if (ptrName == null) {
                    Data.patternNameId = "";
                } else {
                    Data.patternNameId = ptrName[0];
                }

                String[] ptrLen = (String[]) patternLength.getSelectedItem();
                if (ptrLen == null) {
                    Data.patternLengthId = "";
                } else {
                    Data.patternLengthId = ptrLen[0];
                }

                String[] comType = (String[]) competitionType.getSelectedItem();
                if (comType == null) {
                    Data.compTypeId = "";
                } else {
                    Data.compTypeId = comType[0];
                }
                setResult(RESULT_OK);
                finish();
            }
        });

        TextView skip = (TextView) findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ballNameId = "";
                Data.patternLengthId = "";
                Data.ballId = "";
                Data.patternNameId = "";
                Data.compTypeId = "";
                setResult(RESULT_OK);
                finish();
            }
        });
        try {
            JSONObject main = new JSONObject(getIntent().getStringExtra("listResponse"))
                    .getJSONObject("commonStatsStandards");
            JSONArray array = main.getJSONArray("bowlingBallNames");
            final List<String[]> ballList = new ArrayList<String[]>();
            ballList.add(new String[]{"0", "Select Ball Name"});
            for (int i = 0; i < array.length(); i++) {
                ballList.add(new String[]{array.getJSONObject(i).getString("id"),
                        array.getJSONObject(i).getString("userBowlingBallName")});
            }
            if (ballList.size() == 0) {
                bowlingBall.setEnabled(false);
            } else {
                UserStatsAdapter<String[]> opponentAdapter = new UserStatsAdapter<String[]>(
                        getApplicationContext(),
                        android.R.layout.simple_spinner_item, ballList);
                bowlingBall.setAdapter(opponentAdapter);
                opponentAdapter
                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            }
            array = main.getJSONArray("userStatPatternNameList");
            final List<String[]> patternNameList = new ArrayList<String[]>();
            patternNameList.add(new String[]{"0", "Select Pattern Name"});
            for (int i = 0; i < array.length(); i++) {
                patternNameList.add(new String[]{
                        array.getJSONObject(i).getString("id"),
                        array.getJSONObject(i).getString("patternName")});
            }
            UserStatsAdapter<String[]> patternAdapter = new UserStatsAdapter<String[]>(
                    getApplicationContext(), android.R.layout.simple_spinner_item,
                    patternNameList);
            patternName.setAdapter(patternAdapter);
            patternAdapter
                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            array = main.getJSONArray("userStatPatternLengthList");

            final List<String[]> patternLengthList = new ArrayList<String[]>();
            patternLengthList.add(new String[]{"0", "Select Pattern Length"});
            for (int i = 0; i < array.length(); i++) {
                patternLengthList.add(new String[]{
                        array.getJSONObject(i).getString("id"),
                        array.getJSONObject(i).getString("patternLength")});
            }
            UserStatsAdapter<String[]> patternLengthAdapter = new UserStatsAdapter<String[]>(
                    getApplicationContext(), android.R.layout.simple_spinner_item,
                    patternLengthList);
            patternLength.setAdapter(patternLengthAdapter);
            patternLengthAdapter
                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            array = main.getJSONArray("userStatCompetitionTypeList");
            final List<String[]> compTypeList = new ArrayList<String[]>();
            compTypeList.add(new String[]{"0", "Select Game Type"});
            for (int i = 0; i < array.length(); i++) {
                compTypeList.add(new String[]{
                        array.getJSONObject(i).getString("id"),
                        array.getJSONObject(i).getString("competition")});
            }
            UserStatsAdapter<String[]> compTypeAdapter = new UserStatsAdapter<String[]>(
                    getApplicationContext(), android.R.layout.simple_spinner_item,
                    compTypeList);
            competitionType.setAdapter(compTypeAdapter);
            compTypeAdapter
                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            bowlingBall
                    .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> parent,
                                                   View view, int pos, long id) {
                            ballText.setText(ballList.get(pos)[1]);
                            if (ballType) {
                                ballNameId = ballList.get(pos)[0];
                            }
                        }

                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
            patternName
                    .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> parent,
                                                   View view, int pos, long id) {
                            patternText.setText(patternNameList.get(pos)[1]);
                        }

                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
            patternLength
                    .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> parent,
                                                   View view, int pos, long id) {
                            patLengthText.setText(patternLengthList.get(pos)[1]);

                        }

                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
            competitionType
                    .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> parent,
                                                   View view, int pos, long id) {
                            typeText.setText(compTypeList.get(pos)[1]);
                        }

                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    public void onClick(View view) {
        finish();
    }

}

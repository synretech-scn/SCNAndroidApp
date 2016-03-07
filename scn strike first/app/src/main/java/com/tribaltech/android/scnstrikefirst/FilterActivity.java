package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tribaltech.android.entities.Center;
import com.tribaltech.android.util.AppConstants;
import com.tribaltech.android.util.AppStatus;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;
import com.tribaltech.android.util.FilterItem;
import com.tribaltech.android.util.GPSTracker;
import com.tribaltech.android.util.UserStatsAdapter;
import com.tribaltech.android.util.liveLanes;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rmn.androidscreenlibrary.ASSL;

public class FilterActivity extends Activity {

    Spinner countryDropDown;
    Spinner stateDropDown;
    Spinner centerDropDown;
    CustomAdapter<String> countriesAdapter;
    CustomAdapter<String> statesAdapter;
    CustomAdapter<String> centresAdapter;
    List<String> centerName;
    List<String> countryList;
    Map<String, List<String>> statesMap;
    List<Center> centerList;
    ArrayList<liveLanes> liveLaneList;
    Boolean updateView = true;
    EditText country, state, center;
    static String action = "";
    private GPSTracker gps;
    protected Center nearCenter;
    protected String nearCountry = "";
    protected String nearState = "";
    protected boolean allow;

    String locationId;
    String oilPatternId;
    String gameTypeId;
    String patLengthId;
    String timeDuration;
    String filterCountry = "";
    String filterState = "";
    String filterCenter = "";

    boolean isFirstLocation = true;
    boolean isFirstOil = true;
    boolean isFirstGame = true;
    boolean waitLock;
    List<String[]> tags;

    OnItemSelectedListener countryListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1,
                                   int position, long arg3) {
            if (!allow) {
                return;
            }
            // allow = false;
            country.setText(countryDropDown.getItemAtPosition(position)
                    .toString());

            if (statesMap.get(countryDropDown.getItemAtPosition(position)
                    .toString()) == null) {
                state.setText("");
            } else {
                List<String> states = statesMap.get(countryDropDown
                        .getItemAtPosition(position).toString());
                // stateDropDown.setOnItemSelectedListener(null);
                statesAdapter = new CustomAdapter<String>(
                        getApplicationContext(),
                        android.R.layout.simple_spinner_item,
                        new ArrayList<String>(states));
                statesAdapter
                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                stateDropDown.setAdapter(statesAdapter);
                if (nearCountry.equals(country.getText().toString())) {
                    // stateDropDown.setOnItemSelectedListener(stateAdapter);
                    // allow = true;
                    stateDropDown.setSelection(states.indexOf(filterState
                            .isEmpty() ? nearState : filterState));
                    // stateDropDown.setOnItemSelectedListener(stateAdapter);
                } else { // stateDropDown.setOnItemSelectedListener(stateAdapter);
                    // stateDropDown.setOnItemSelectedListener(stateAdapter);
                    // allow = true;
                    state.setText(stateDropDown.getSelectedItem().toString());
                    String state = stateDropDown.getSelectedItem().toString();
                    if (!state.equalsIgnoreCase("Select State")) {
                        loadCentres(countryDropDown.getSelectedItem().toString(),
                                stateDropDown.getSelectedItem().toString()
                                        .replaceAll(" ", "%20"));
                    }
                    // stateDropDown.setSelection(0);
                    // state.setText(new ArrayList<String>(states).get(0));
                }

            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };

    OnItemSelectedListener stateAdapter = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1,
                                   int position, long arg3) {


            state.setText(stateDropDown.getSelectedItem().toString());
            if (position == 0 && stateDropDown.getSelectedItem().toString().equalsIgnoreCase("Select State")) {
                if (centresAdapter != null) {
                    centresAdapter.clear();
                }
                centerName.clear();
                centerList.clear();
                center.setText("");
                centerList.add(new Center("Select Center",
                        0, "manual"));

                centerName.add("Select Center");

                centresAdapter = new CustomAdapter<String>(
                        getApplicationContext(),
                        android.R.layout.simple_spinner_item, centerName);
                centresAdapter
                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                centerDropDown.setAdapter(centresAdapter);
            } else {
                loadCentres(
                        countryDropDown.getSelectedItem().toString(),
                        stateDropDown.getSelectedItem().toString()
                                .replaceAll(" ", "%20"));
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };
    private Spinner patternLength;
    private Spinner patternName;
    private Spinner competitionType;
    private Spinner timeDrop;
    private Spinner tagDrop;
    private EditText ballText;
    private EditText patternText;
    private EditText typeText;
    private EditText timeText;
    private EditText tagText;

    //	View[] timeDurationButtons;
    ScrollView filterScroll;
    FilterItem filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_popup);
        new ASSL(FilterActivity.this, (ViewGroup) findViewById(R.id.root),
                AppConstants.SCREEN_HEIGHT, AppConstants.SCREEN_WIDTH,
                false);

        if (!Data.userStatsSubscribed) {
            findViewById(R.id.oilPattern).setVisibility(View.GONE);
            findViewById(R.id.ballType).setVisibility(View.GONE);
            findViewById(R.id.gameType).setVisibility(View.GONE);
            findViewById(R.id.location).setVisibility(View.GONE);
        }

        filterScroll = (ScrollView) findViewById(R.id.filterScroll);
        filter = CommonUtil.getFilter(FilterActivity.this);
        timeDuration = filter.timeDuration;
        locationId = filter.locationId;
        oilPatternId = filter.oilPatternId;
        gameTypeId = filter.gameTypeId;
        patLengthId = filter.patternLengthId;
        filterCountry = filter.country;
        filterState = filter.state;
        filterCenter = filter.center;

        nearCountry = filter.country;
        nearState = filter.state;
        nearCenter = new Center(filter.center);

        patternLength = (Spinner) findViewById(R.id.ballNameDrop);
        patternName = (Spinner) findViewById(R.id.oilPatternDrop);
        competitionType = (Spinner) findViewById(R.id.gameTypeDrop);
        timeDrop = (Spinner) findViewById(R.id.timeDrop);
        tagDrop = (Spinner) findViewById(R.id.tagsDrop);

        ballText = (EditText) findViewById(R.id.ballNameText);
        patternText = (EditText) findViewById(R.id.oilPatternText);
        typeText = (EditText) findViewById(R.id.gameTypeText);
        timeText = (EditText) findViewById(R.id.timeText);
        tagText = (EditText) findViewById(R.id.tagText);

        gps = new GPSTracker(FilterActivity.this);
        country = (EditText) findViewById(R.id.country);
        state = (EditText) findViewById(R.id.state);
        center = (EditText) findViewById(R.id.center);
        countryDropDown = (Spinner) findViewById(R.id.countries);
        stateDropDown = (Spinner) findViewById(R.id.states);
        centerDropDown = (Spinner) findViewById(R.id.centres);

        countryDropDown.setOnItemSelectedListener(countryListener);
        stateDropDown.setOnItemSelectedListener(stateAdapter);

        final List<String[]> timeData = new ArrayList<>();
        timeData.add(new String[]{"", "All Time"});
        timeData.add(new String[]{"daily", "Today"});
        timeData.add(new String[]{"week", "This Week"});
        timeData.add(new String[]{"month", "This Month"});
        timeData.add(new String[]{"year", "This Year"});
        UserStatsAdapter<String[]> compTypeAdapter = new UserStatsAdapter<String[]>(
                getApplicationContext(),
                android.R.layout.simple_spinner_item,
                timeData);
        timeDrop.setAdapter(compTypeAdapter);
        for (int i = 0; i < timeData.size(); i++) {
            if (timeData.get(i)[0].equals(filter.timeDuration)) {
                timeDrop.setSelection(i);
                break;
            }
        }
        compTypeAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeDrop
                .setOnItemSelectedListener(new OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view, int pos, long id) {
                        timeText.setText(timeData
                                .get(pos)[1]);
//                        oilPatternId = timeData
//                                .get(pos)[0];
                    }

                    public void onNothingSelected(
                            AdapterView<?> parent) {
                    }
                });

        tagDrop
                .setOnItemSelectedListener(new OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view, int pos, long id) {
                        tagText.setText(tags
                                .get(pos)[1]);
//                        oilPatternId = timeData
//                                .get(pos)[0];
                    }

                    public void onNothingSelected(
                            AdapterView<?> parent) {
                    }
                });

        centerDropDown
                .setOnItemSelectedListener(new OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int position, long arg3) {

                        if (isFirstLocation) {
                            isFirstLocation = false;
                        } else {
                            locationId = centerList.get(centerDropDown
                                    .getSelectedItemPosition()).id + "";
                        }
                        center.setText(centerDropDown.getItemAtPosition(
                                position).toString());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                });

        fetchCommonLists();
//        loadCentresNearby();
        loadCountries();
        fetchTags();
    }

    private void fetchCommonLists() {

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT * 2);

        client.get(Data.baseUrl
                        + "UserStat/CommonStandards?token="
                        + CommonUtil.getAccessToken(getApplicationContext())
                        .replaceAll("[+]", "%2B") + "&apiKey=" + Data.apiKey,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {



                        try {
                            JSONObject main = new JSONObject(response)
                                    .getJSONObject("commonStatsStandards");
                            JSONArray array = main
                                    .getJSONArray("userStatPatternNameList");
                            final List<String[]> patternNameList = new ArrayList<String[]>();
                            patternNameList.add(new String[]{"0",
                                    "Select Oil Pattern"});
                            int oilPatternDefault = -1;
                            for (int i = 0; i < array.length(); i++) {
                                if (array.getJSONObject(i).getString("id")
                                        .equals(oilPatternId)) {
                                    oilPatternDefault = i;
                                }
                                patternNameList.add(new String[]{
                                        array.getJSONObject(i).getString("id"),
                                        array.getJSONObject(i).getString(
                                                "patternName")});
                            }
                            UserStatsAdapter<String[]> patternAdapter = new UserStatsAdapter<String[]>(
                                    getApplicationContext(),
                                    android.R.layout.simple_spinner_item,
                                    patternNameList);
                            patternName.setAdapter(patternAdapter);
                            patternAdapter
                                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            array = main
                                    .getJSONArray("userStatCompetitionTypeList");

                            final List<String[]> compTypeList = new ArrayList<String[]>();
                            compTypeList.add(new String[]{"0",
                                    "Select Game Type"});
                            int gamePatternDefault = -1;
                            for (int i = 0; i < array.length(); i++) {
                                if (array.getJSONObject(i).getString("id")
                                        .equals(gameTypeId)) {
                                    gamePatternDefault = i;
                                }
                                compTypeList.add(new String[]{
                                        array.getJSONObject(i).getString("id"),
                                        array.getJSONObject(i).getString(
                                                "competition")});
                            }

                            UserStatsAdapter<String[]> compTypeAdapter = new UserStatsAdapter<String[]>(
                                    getApplicationContext(),
                                    android.R.layout.simple_spinner_item,
                                    compTypeList);
                            competitionType.setAdapter(compTypeAdapter);
                            compTypeAdapter
                                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            array = main
                                    .getJSONArray("userStatPatternLengthList");

                            final List<String[]> patternLengthList = new ArrayList<String[]>();
                            patternLengthList.add(new String[]{"0",
                                    "Select Pattern Length"});
                            int patternLengthDefault = -1;
                            for (int i = 0; i < array.length(); i++) {
                                if (array.getJSONObject(i).getString("id")
                                        .equals(patLengthId)) {
                                    patternLengthDefault = i;
                                }
                                patternLengthList.add(new String[]{
                                        array.getJSONObject(i).getString("id"),
                                        array.getJSONObject(i).getString(
                                                "patternLength")});
                            }
                            UserStatsAdapter<String[]> patternLengthAdapter = new UserStatsAdapter<String[]>(
                                    getApplicationContext(),
                                    android.R.layout.simple_spinner_item,
                                    patternLengthList);
                            patternLength.setAdapter(patternLengthAdapter);
                            patternLengthAdapter
                                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            patternLength
                                    .setOnItemSelectedListener(new OnItemSelectedListener() {
                                        public void onItemSelected(
                                                AdapterView<?> parent,
                                                View view, int pos, long id) {
                                            ballText.setText(patternLengthList
                                                    .get(pos)[1]);
                                            patLengthId = patternLengthList
                                                    .get(pos)[0];
                                        }

                                        public void onNothingSelected(
                                                AdapterView<?> parent) {
                                        }
                                    });
                            patternName
                                    .setOnItemSelectedListener(new OnItemSelectedListener() {
                                        public void onItemSelected(
                                                AdapterView<?> parent,
                                                View view, int pos, long id) {
                                            patternText.setText(patternNameList
                                                    .get(pos)[1]);
                                            oilPatternId = patternNameList
                                                    .get(pos)[0];
                                        }

                                        public void onNothingSelected(
                                                AdapterView<?> parent) {
                                        }
                                    });

                            competitionType
                                    .setOnItemSelectedListener(new OnItemSelectedListener() {
                                        public void onItemSelected(
                                                AdapterView<?> parent,
                                                View view, int pos, long id) {
                                            typeText.setText(compTypeList
                                                    .get(pos)[1]);

                                            gameTypeId = compTypeList.get(pos)[0];
                                        }

                                        public void onNothingSelected(
                                                AdapterView<?> parent) {
                                        }
                                    });
                            patternLength
                                    .setSelection(patternLengthDefault + 1);
                            patternName.setSelection(oilPatternDefault + 1);
                            competitionType
                                    .setSelection(gamePatternDefault + 1);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.v("exception ", e.toString() + "");
                        CommonUtil.commonGameErrorDialog(FilterActivity.this,
                                "An error occured.Please try again.");
                        CommonUtil.loading_box_stop();

                    }
                });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.done: {
                Intent i = getIntent();

                timeDuration = ((String[]) timeDrop.getSelectedItem())[0];
                String tag = "";
                try {
                    filterCountry = countryDropDown.getSelectedItem().toString();
                    filterState = stateDropDown.getSelectedItem().toString();
                    filterCenter = centerDropDown.getSelectedItem().toString();
                    tag = ((String[]) tagDrop.getSelectedItem())[0];
                } catch (Exception e) {
                    filterCountry = "";
                    filterState = "";
                    filterCenter = "";
                } finally {
                    FilterItem filter = new FilterItem(timeDuration, locationId
                            + "", oilPatternId, gameTypeId, patLengthId,
                            filterCountry, filterState, filterCenter, tag);
                    CommonUtil.saveFilter(filter, FilterActivity.this);

                    i.putExtra("filter", filter);
                    setResult(RESULT_OK, i);
                    finish();
                }
                break;
            }

            case R.id.resetFilter:
                FilterItem filter = new FilterItem();
                timeDuration = filter.timeDuration;
                locationId = filter.locationId;
                oilPatternId = filter.oilPatternId;
                gameTypeId = filter.gameTypeId;
                patLengthId = filter.patternLengthId;
                filterCountry = filter.country;
                filterState = filter.state;
                filterCenter = filter.center;

                patternLength.setSelection(0);
                patternName.setSelection(0);
                competitionType.setSelection(0);
                countryDropDown.setSelection(0);
                stateDropDown.setSelection(0);
                centerDropDown.setSelection(0);
                tagDrop.setSelection(0);
                timeDrop.setSelection(0);
                CommonUtil.saveFilter(filter, getApplicationContext());
                break;

            case R.id.back: {
                Intent i = new Intent();
                i.putExtra("filter", CommonUtil.getFilter(getApplicationContext()));
                setResult(RESULT_CANCELED, i);
                finish();
                break;
            }
        }
    }

    private void loadCountries() {

        if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                getApplicationContext())) {
            CommonUtil.noInternetDialog(FilterActivity.this);
            return;
        }
        // CommonUtil.loading_box(this, "Please wait...");

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.get(Data.baseUrl + "venue/locations?apiKey=" + Data.apiKey,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {

                        CommonUtil.loading_box_stop();
                        try {
                            JSONArray countriesArray = new JSONArray(response);
                            countryList = new ArrayList<String>();
                            statesMap = new HashMap<String, List<String>>();

                            // List<String> dummy = new ArrayList<String>();
                            // dummy.add("Select State");
                            // statesMap.put("Select Country", dummy);

                            countryList.add("Select Country");
                            List<String> statesList = new ArrayList<String>();
                            statesList.add("Select State");
                            statesMap.put(countryList.get(0), statesList);

                            for (int i = 0; i < countriesArray.length(); i++) {
                                JSONObject country = countriesArray
                                        .getJSONObject(i);
                                String countryName = country
                                        .getString("displayName");
                                countryList.add(countryName);

                                JSONArray statesArray = country
                                        .getJSONArray("states");
                                List<String> states = new ArrayList<String>();
//                                states.add("Select State");
                                for (int j = 0; j < statesArray.length(); j++) {
                                    states.add(statesArray.getJSONObject(j)
                                            .getString("displayName"));
                                }
                                if (states.size() != 0) {
                                    statesMap.put(countryName, states);
                                }
                            }
                            allow = true;
                            countriesAdapter = new CustomAdapter<String>(
                                    getApplicationContext(),
                                    android.R.layout.simple_spinner_item,
                                    countryList);
                            countriesAdapter
                                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            countryDropDown.setAdapter(countriesAdapter);
                            countryDropDown.setSelection(countryList.indexOf(filterCountry
                                    .isEmpty() ? nearCountry : filterCountry));

                            // statesAdapter = new CustomAdapter<String>(
                            // getApplicationContext(),
                            // android.R.layout.simple_spinner_item,
                            // new ArrayList<String>());
                            // statesAdapter
                            // .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            // stateDropDown.setAdapter(statesAdapter);

                            centerList = new ArrayList<Center>();
                            centerName = new ArrayList<String>();
                            // centresAdapter = new CustomAdapter<String>(
                            // getApplicationContext(),
                            // android.R.layout.simple_spinner_item,
                            // centerName);
                            // centresAdapter
                            // .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            // centerDropDown.setAdapter(centresAdapter);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {

                        CommonUtil.loading_box_stop();
                        CommonUtil.commonGameErrorDialog(FilterActivity.this,
                                "An error occured.Please try again.");

                    }
                });

    }

    private void loadCentres(String country, final String state1) {

        if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                getApplicationContext())) {
            CommonUtil.noInternetDialog(FilterActivity.this);
            return;
        }
        // CommonUtil.loading_box(this, "Please wait...");
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        country = country.replaceAll(" ", "%20");
        // state1 = state1.replaceAll(" ", "%20");

        RequestParams rv = new RequestParams();
        if (action.equalsIgnoreCase("live")) {
            rv.put("scoringType", "Machine");
            // scoringType
        } else {
            rv = null;
        }
        client.get(Data.baseUrl + "venue/locations/" + country + "/" + state1
                + "?apiKey=" + Data.apiKey, rv, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                Log.v("response = ", response);
                CommonUtil.loading_box_stop();
                try {
                    if (centresAdapter != null) {
                        centresAdapter.clear();
                    }
                    JSONArray centresArray = new JSONArray(response);
                    centerName.clear();
                    centerList.clear();
                    center.setText("");
//                    centerList.add(new Center("Select Center",
//                            0, "manual"));
//                    centerName.add("Select Center");
                    for (int i = 0; i < centresArray.length(); i++) {
                        JSONObject center = centresArray.getJSONObject(i);
                        centerList.add(new Center(center.getString("name"),
                                center.getInt("id"), center
                                .getString("scoringType")));

                        centerName.add(center.getString("name"));
                    }

                    centresAdapter = new CustomAdapter<String>(
                            getApplicationContext(),
                            android.R.layout.simple_spinner_item, centerName);
                    centresAdapter
                            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    centerDropDown.setAdapter(centresAdapter);
                    if (nearState.equals(state1)) {
                        centerDropDown.setSelection(centerName.indexOf(filterCenter
                                .isEmpty() ? nearCenter.name : filterCenter));
                    } else {
                        center.setText(centerDropDown.getItemAtPosition(0)
                                .toString());
                    }

//                    if (nearCenter != null) {
//                        Log.v("loaded nearby center", "loaded nearby center");
//                        try {
//                            centerName.indexOf(nearCenter.name);
//                            centerDropDown.setSelection(centerName.indexOf(filterCenter
//                                    .isEmpty() ? nearCenter.name : filterCenter));
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        try {
//                            center.setText(centerDropDown.getItemAtPosition(0)
//                                    .toString());
//                        } catch (Exception e) {
//
//                        }
//                    }

                } catch (Exception e) {
                    Log.v("Exception = ", e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable e) {
                centerName.clear();
                centerList.clear();
                center.setText("");
                state.setText("");

                CommonUtil.loading_box_stop();
                CommonUtil.commonGameErrorDialog(FilterActivity.this,
                        "An error occured.Please try again.");

            }
        });

    }

    private void loadCentresNearby() {
        // countryDropDown.setOnItemSelectedListener(countryListener);
        if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                getApplicationContext())) {
            CommonUtil.noInternetDialog(FilterActivity.this);
            return;
        }

        RequestParams rv = new RequestParams();
        rv.put("latitude", gps.getLatitude() + "");
        rv.put("longitude", gps.getLongitude() + "");
        rv.put("apiKey", Data.apiKey);
        rv.put("distanceLimitMiles", "25");

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(Data.baseUrl + "venue/nearby", rv,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        Log.v("nearby response = ", response);
                        CommonUtil.loading_box_stop();

                        try {
                            JSONArray centresArray = new JSONArray(response);

                            if (centresArray.length() > 0) {
                                // allow = true;
                                JSONObject center = centresArray
                                        .getJSONObject(0);
                                nearCountry = center.getJSONObject("address")
                                        .getJSONObject("country")
                                        .getString("countryDisplayName");
                                nearState = center.getJSONObject("address")
                                        .getJSONObject("administrativeArea")
                                        .getString("longName");
                                nearCenter = new Center(center
                                        .getString("name"),
                                        center.getInt("id"), center
                                        .getString("scoringType"));
                                // country.setText(nearCountry);

                                // countryDropDown.setSelection(countryList
                                // .indexOf(nearCountry));
                                Log.v("center name = ",
                                        center.getString("name"));
                            }
                            // else {
                            // // nearCountry = countryList.get(0);
                            // // nearState =
                            // //
                            // ((List<String>)statesMap.get(nearCountry)).get(0);
                            // // country.setText(countryList.get(0));
                            //
                            // countryDropDown.setSelection(2);
                            // allow = true;
                            // countryDropDown.setSelection(0);
                            //
                            // nearCenter = null;
                            // }

                        } catch (Exception e) {
                            Log.v("Exception = ", e.toString());
                            e.printStackTrace();
                        }

                        // loadCentres(country, state1);
                        loadCountries();
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.loading_box_stop();
                        // loadCentres(country, state1);
                        loadCountries();
                    }
                });

    }

    class CustomAdapter<T> extends ArrayAdapter<String> {
        public CustomAdapter(Context context, int textViewResourceId,
                             List<String> countryList) {

            super(context, textViewResourceId, countryList);
            // TextView tv=(TextView)findViewById(textViewResourceId);
            // tv.setTextSize(10);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView textView = (TextView) view
                    .findViewById(android.R.id.text1);

            textView.setText("");
            return view;
        }
    }

    private void fetchTags() {

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(Data.baseUrl
                        + "Tags/TagListUserSpecific?token="
                        + CommonUtil.getAccessToken(getApplicationContext())
                        .replaceAll("[+]", "%2B") + "&apiKey=" + Data.apiKey,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        try {
                            tags = new ArrayList<String[]>();
                            JSONArray array = new JSONArray(response);
                            tags.add(new String[]{"",
                                    "Select Tag"});
                            int selectedIdx = -1;
                            for (int i = 0; i < array.length(); i++) {
                                tags.add(new String[]{array.getJSONObject(i).getString("tag"),
                                        array.getJSONObject(i).getString("tag")});
                                if (tags.get(i)[1].equalsIgnoreCase(filter.tag)) {
                                    selectedIdx = i;
                                }
                            }
                            UserStatsAdapter<String[]> compTypeAdapter = new UserStatsAdapter<String[]>(
                                    getApplicationContext(),
                                    android.R.layout.simple_spinner_item,
                                    tags);
                            tagDrop.setAdapter(compTypeAdapter);
                            compTypeAdapter
                                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            if (selectedIdx != -1) {
                                tagDrop.setSelection(selectedIdx);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                    }
                }

        );
    }
}

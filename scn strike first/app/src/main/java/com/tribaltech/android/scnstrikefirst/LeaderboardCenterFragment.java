package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tribaltech.android.util.AppStatus;
import com.tribaltech.android.entities.Center;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;
import com.tribaltech.android.scnstrikefirst.GoBowling.CustomAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rmn.androidscreenlibrary.ASSL;

public class LeaderboardCenterFragment extends Fragment {

    //    Center nearCenter;
    Spinner countryDropDown;
    Spinner stateDropDown;
    Spinner centerDropDown;
    CustomAdapter<String> countriesAdapter;
    CustomAdapter<String> statesAdapter;
    CustomAdapter<String> centresAdapter;
    List<String> centerName;
    List<String> countryList;
    Map<String, List<String>> statesMap;
    public List<Center> centerList = new ArrayList<Center>();
    EditText country, state, center;
    //    protected String nearCountry = "";
//    protected String nearState = "";
    protected boolean allow;
    //    private GPSTracker gps;
    protected int failCount;
    Handler handler;
    private boolean loadOnAttach;
    boolean fromLive;
    LinearLayout statesParent;
    LinearLayout centerParent;
    Map<Integer, Integer> countryIdMap;
    Map<String, Integer> stateIdMap;
    Integer countryId = 0;
    Integer stateId = 0;
    Integer centerId = 0;
//    String selectedStateName = "";
//    String selectedCountryName = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.leaderboard_center_fragment,
                container, false);
        new ASSL(getActivity(), (ViewGroup) view.findViewById(R.id.root), 1134,
                720, false);
        statesParent = (LinearLayout) view.findViewById(R.id.statesParent);
        centerParent = (LinearLayout) view.findViewById(R.id.centerParent);
        fromLive = (getArguments() != null);
        country = (EditText) view.findViewById(R.id.country);
        state = (EditText) view.findViewById(R.id.state);
        center = (EditText) view.findViewById(R.id.center);

        countryDropDown = (Spinner) view.findViewById(R.id.countries);
        stateDropDown = (Spinner) view.findViewById(R.id.states);
        centerDropDown = (Spinner) view.findViewById(R.id.centres);

        countryDropDown.setOnItemSelectedListener(countryListener);
        stateDropDown.setOnItemSelectedListener(stateAdapter);
        centerDropDown.setOnItemSelectedListener(centerListener);
        return view;
    }

    public void load() {
        if (getActivity() == null) {
            loadOnAttach = true;
        } else {
//            loadCentresNearby();
            loadCountries();
        }
    }

    public String[] getSelectedIds() {

        String stateName = "All States";
        String centerName = "All Centres";
        try {
            countryId = countryIdMap.get(countryDropDown.getSelectedItemPosition());
            if (statesParent.getVisibility() == View.VISIBLE) {
                stateId = stateIdMap.get(stateDropDown.getSelectedItem().toString());
                stateName = stateDropDown.getSelectedItem().toString();
                if (centerParent.getVisibility() == View.VISIBLE) {
                    centerId = centerList.get(centerDropDown.getSelectedItemPosition()).id;
                    centerName = centerList.get(centerDropDown.getSelectedItemPosition()).name;
                } else {
                    centerId = 0;
                }
            } else {
                stateId = 0;
                centerId = 0;
            }
        } catch (Exception e) {
            return new String[]{"0", "All Countries",
                    "0", "All States",
                    "0", "All Centres"};
        }
        return new String[]{countryId + "", countryDropDown.getSelectedItem().toString(),
                stateId + "", stateName,
                centerId + "", centerName};
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onStop() {
        super.onStop();
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (loadOnAttach) {
            loadOnAttach = false;
            loadCountries();
//            loadCentresNearby();
        }
    }

    ;

    OnItemSelectedListener countryListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1,
                                   int position, long arg3) {
            if (!allow) {
                return;
            }
            country.setText(countryDropDown.getItemAtPosition(position)
                    .toString());
            countryId = countryIdMap.get(position);
            if (position == 0) {
                stateId = 0;
                centerId = 0;
                statesParent.setVisibility(View.GONE);
                centerParent.setVisibility(View.GONE);
                return;
            } else {
                statesParent.setVisibility(View.VISIBLE);
                if (stateDropDown.getSelectedItemPosition() != 0) {
                    centerParent.setVisibility(View.VISIBLE);
//                    centerId = centerList.get(centerDropDown.getSelectedItemPosition()).id;
                }
            }

            if (statesMap.get(countryDropDown.getItemAtPosition(position)
                    .toString()) == null) {
                state.setText("");
            } else {
                List<String> states = statesMap.get(countryDropDown
                        .getItemAtPosition(position).toString());
                // stateDropDown.setOnItemSelectedListener(null);
                statesAdapter = new CustomAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item,
                        new ArrayList<String>(states));
                statesAdapter
                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                stateDropDown.setAdapter(statesAdapter);
                if (Data.leaderboardFilter.countryName.equals(country.getText().toString())) {
                    // stateDropDown.setOnItemSelectedListener(stateAdapter);
                    // allow = true;
                    stateDropDown.setSelection(states.indexOf(Data.leaderboardFilter.stateName));
                    stateId = stateIdMap.get(stateDropDown.getSelectedItem().toString());
                    // stateDropDown.setOnItemSelectedListener(stateAdapter);
                } else { // stateDropDown.setOnItemSelectedListener(stateAdapter);
                    // stateDropDown.setOnItemSelectedListener(stateAdapter);
                    // allow = true;
                    state.setText(stateDropDown.getSelectedItem().toString());
//                    stateId = stateIdMap.get(stateDropDown.getSelectedItem().toString());
                    if (stateDropDown.getSelectedItemPosition() != 0) {
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
            if (position == 0) {
                centerParent.setVisibility(View.GONE);
            } else {
                centerParent.setVisibility(View.VISIBLE);
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

    OnItemSelectedListener centerListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1,
                                   int position, long arg3) {

            center.setText(centerDropDown.getItemAtPosition(
                    position).toString());
            if (getActivity() instanceof GoBowling) {
                ((GoBowling) getActivity()).specifyRange(centerList.get(position).laneCount);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };

    private void loadCountries() {

        if (!AppStatus.getInstance(getActivity()).isOnline(getActivity())) {
            CommonUtil.noInternetDialog(getActivity());
            return;
        }
        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(getActivity(), "Please wait...");
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.get(Data.baseUrl + "venue/locations?apiKey=" + Data.apiKey
                        + (fromLive ? "&scoringType=Machine" : ""),
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        Integer selectedCountryId = Integer.parseInt(Data.leaderboardFilter.countryId);
                        Integer selectedStateId = Integer.parseInt(Data.leaderboardFilter.stateId);

                        CommonUtil.loading_box_stop();
                        try {
                            JSONArray countriesArray = new JSONArray(response);
                            countryList = new ArrayList<String>();
                            countryIdMap = new HashMap<Integer, Integer>();
                            stateIdMap = new HashMap<String, Integer>();
                            countryList.add("All Countries");
                            countryIdMap.put(0, 0);

                            statesMap = new HashMap<String, List<String>>();
                            for (int i = 0; i < countriesArray.length(); i++) {
                                JSONObject country = countriesArray
                                        .getJSONObject(i);
                                if (country.getJSONArray("states").length() != 0) {
                                    String countryName = country
                                            .getString("displayName");
                                    countryList.add(countryName);
                                    int countryId = country.getInt("countryId");
                                    countryIdMap.put(countryList.size() - 1, countryId);
//                                    if (countryId == selectedCountryId) {
//                                        selectedCountryName = countryName;
//                                    }
                                    JSONArray statesArray = country
                                            .getJSONArray("states");
                                    List<String> states = new ArrayList<String>();
                                    states.add("All States");
                                    stateIdMap.put("All States", 0);
                                    for (int j = 0; j < statesArray.length(); j++) {
                                        states.add(statesArray.getJSONObject(j)
                                                .getString("displayName"));
                                        int stateId = statesArray.getJSONObject(j)
                                                .getInt("administrativeAreaId");
                                        stateIdMap.put(statesArray.getJSONObject(j)
                                                .getString("displayName"), stateId);
//                                        if (countryId == selectedCountryId) {
//                                            if (stateId == selectedStateId) {
//                                                selectedStateName = statesArray.getJSONObject(j)
//                                                        .getString("displayName");
//                                            }
//                                        }
                                    }
                                    if (states.size() != 0) {
                                        statesMap.put(countryName, states);
                                    }
                                }
                            }
                            allow = true;
                            countriesAdapter = new CustomAdapter<String>(
                                    getActivity(),
                                    android.R.layout.simple_spinner_item,
                                    countryList);
                            countriesAdapter
                                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            countryDropDown.setAdapter(countriesAdapter);
                            countryDropDown.setSelection(countryList
                                    .indexOf(Data.leaderboardFilter.countryName));

                            // statesAdapter = new CustomAdapter<String>(
                            // getActivity(),
                            // android.R.layout.simple_spinner_item,
                            // new ArrayList<String>());
                            // statesAdapter
                            // .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            // stateDropDown.setAdapter(statesAdapter);

                            centerList = new ArrayList<Center>();
                            centerName = new ArrayList<String>();
                            // centresAdapter = new CustomAdapter<String>(
                            // getActivity(),
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
                        // CommonUtil.commonGameErrorDialog(getActivity(),
                        // e.getMessage() + "");
                        if (++failCount < 3) {
                            loadCountries();
                        }
                    }
                });

    }

    private void loadCentres(String country, final String state1) {

        if (!AppStatus.getInstance(getActivity()).isOnline(getActivity())) {
            CommonUtil.noInternetDialog(getActivity());
            return;
        }
        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(getActivity(), "Please wait...");
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        country = country.replaceAll(" ", "%20");

        RequestParams rv = new RequestParams();
        if (fromLive) {
            rv.put("scoringType", "Machine");
        } else {
            rv = null;
        }
        client.get(Data.baseUrl + "venue/locations/" + country + "/" + state1
                + "?apiKey=" + Data.apiKey, rv, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                CommonUtil.loading_box_stop();
                try {
                    if (centresAdapter != null) {
                        centresAdapter.clear();
                    }
                    JSONArray centresArray = new JSONArray(response);
                    centerName.clear();
                    centerList.clear();
                    center.setText("");
                    centerList.add(new Center("All Centres", 0, ""));
                    centerName.add("All Centres");
                    for (int i = 0; i < centresArray.length(); i++) {
                        JSONObject center = centresArray.getJSONObject(i);
                        centerList.add(new Center(center.getString("name"),
                                center.getInt("id"), center
                                .getString("scoringType"), center
                                .getJSONObject("phoneNumber")
                                .getString("number")
                                .replaceAll("[- ]+", ""), center.getInt("totalLanes")));

                        centerName.add(center.getString("name"));
                        // //Log.v("center name = ", center.getString("name"));
                    }

                    centresAdapter = new CustomAdapter<String>(getActivity(),
                            android.R.layout.simple_spinner_item, centerName);
                    centresAdapter
                            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    centerDropDown.setAdapter(centresAdapter);
                    if (Data.leaderboardFilter.stateName.equals(state1)) {
                        centerDropDown.setSelection(centerName
                                .indexOf(Data.leaderboardFilter.venueName));
                    } else {
                        center.setText(centerDropDown.getItemAtPosition(0)
                                .toString());
                    }

                    if (true) {
                        // Log.v("loaded nearby center",
                        // "loaded nearby center");
                        try {
                            centerName.indexOf(Data.leaderboardFilter.venueName);
                            centerDropDown.setSelection(centerName
                                    .indexOf(Data.leaderboardFilter.venueName));
                        } catch (Exception e) {

                        }
                    } else {
                        try {
                            center.setText(centerDropDown.getItemAtPosition(0)
                                    .toString());
                        } catch (Exception e) {

                        }
                    }

                } catch (Exception e) {
                    // Log.v("Exception = ", e.toString());
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
                CommonUtil.commonGameErrorDialog(getActivity(), e.getMessage()
                        + "");

            }
        });

    }

}

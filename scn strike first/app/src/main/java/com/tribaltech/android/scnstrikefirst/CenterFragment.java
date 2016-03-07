package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tribaltech.android.util.AppStatus;
import com.tribaltech.android.entities.Center;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;
import com.tribaltech.android.util.GPSTracker;
import com.tribaltech.android.scnstrikefirst.GoBowling.CustomAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rmn.androidscreenlibrary.ASSL;

public class CenterFragment extends Fragment {

    Center nearCenter;
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
    protected String nearCountry = "";
    protected String nearState = "";
    protected boolean allow;
    private GPSTracker gps;
    protected int failCount;
    Handler handler;
    private boolean loadOnAttach;
    boolean fromLive;
    boolean restricted;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.center_fragment,
                container, false);
        new ASSL(getActivity(), (ViewGroup) view.findViewById(R.id.root), 1134,
                720, false);
        if (getArguments() != null) {
            fromLive = getArguments().containsKey("fromLive");
            restricted = getArguments().containsKey("restrict");
        }

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
            loadCentresNearby();
        }
    }

    public Center getSelectedCenter() {
        Center center;
        try {
            center = centerList.get(centerDropDown.getSelectedItemPosition());
        } catch (Exception e) {
            e.printStackTrace();
            center = new Center();
        }
        return center;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onStop() {
        super.onStop();
        if (gps != null) {
            gps.stopUsingGPS();
        }
    }


    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (loadOnAttach) {
            loadOnAttach = false;
            gps = new GPSTracker(getActivity());
            loadCentresNearby();
        }
    }

    OnItemSelectedListener countryListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1,
                                   int position, long arg3) {
            if (!allow) {
                return;
            }
            country.setText(countryDropDown.getItemAtPosition(position)
                    .toString());

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
                if (nearCountry.equals(country.getText().toString())) {
                    // stateDropDown.setOnItemSelectedListener(stateAdapter);
                    // allow = true;
                    stateDropDown.setSelection(states.indexOf(nearState));
                    // stateDropDown.setOnItemSelectedListener(stateAdapter);
                } else { // stateDropDown.setOnItemSelectedListener(stateAdapter);
                    // stateDropDown.setOnItemSelectedListener(stateAdapter);
                    // allow = true;
                    state.setText(stateDropDown.getSelectedItem().toString());
                    loadCentres(countryDropDown.getSelectedItem().toString().replaceAll(" ", "%20"),
                            stateDropDown.getSelectedItem().toString()
                                    .replaceAll(" ", "%20"));
                    // stateDropDown.setSelection(0);
                    // state.setText(new ArrayList<String>(states).get(0));
                }


            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };


    OnItemSelectedListener stateAdapter = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1,
                                   int position, long arg3) {

            state.setText(stateDropDown.getSelectedItem().toString());
            loadCentres(
                    countryDropDown.getSelectedItem().toString().replaceAll(" ", "%20"),
                    stateDropDown.getSelectedItem().toString()
                            .replaceAll(" ", "%20"));
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };

    OnItemSelectedListener centerListener = new AdapterView.OnItemSelectedListener() {

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
        //changed pilotGroup from 1 to 5 24/08
        //changed pilotGroup from 5 to 1 25/8
        client.get(Data.baseUrl + "venue/" + (restricted ? "Restricted" : "") + "locations?apiKey=" + Data.apiKey
                        + (fromLive ? "&scoringType=Machine" : "") + (restricted ? "&PilotGroup=1" : ""),
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {

                        CommonUtil.loading_box_stop();
                        try {
                            JSONArray countriesArray = new JSONArray(response);
                            countryList = new ArrayList<String>();
                            statesMap = new HashMap<String, List<String>>();
                            for (int i = 0; i < countriesArray.length(); i++) {
                                JSONObject country = countriesArray
                                        .getJSONObject(i);
                                if (country.getJSONArray("states").length() != 0) {
                                    String countryName = country
                                            .getString("displayName");
                                    countryList.add(countryName);

                                    JSONArray statesArray = country
                                            .getJSONArray("states");
                                    List<String> states = new ArrayList<String>();
                                    for (int j = 0; j < statesArray.length(); j++) {
                                        states.add(statesArray.getJSONObject(j)
                                                .getString("displayName"));
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
                                    .indexOf(nearCountry));

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

    private void loadCentres(final String country, final String state1) {

        if (!AppStatus.getInstance(getActivity()).isOnline(getActivity())) {
            CommonUtil.noInternetDialog(getActivity());
            return;
        }
        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(getActivity(), "Please wait...");
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
//        country = country.replaceAll(" ", "%20");

        RequestParams rv = new RequestParams();
        if (fromLive) {
            rv.put("scoringType", "Machine");
        } else {
            rv = null;
        }
        //changed pilotGroup from 1 to 5 24/08
        //changed pilotGroup from 5 to 1 24/08
        client.get(Data.baseUrl + "venue/" + (restricted ? "Restricted" : "") + "locations/" + country + "/" + state1
                + "?apiKey=" + Data.apiKey + (restricted ? "&PilotGroup=1" : ""), rv, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                CommonUtil.loading_box_stop();
                Log.d("country", country);
                try {
                    if (centresAdapter != null) {
                        centresAdapter.clear();
                    }
                    JSONArray centresArray = new JSONArray(response);
                    centerName.clear();
                    centerList.clear();
                    center.setText("");
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
                    // centresAdapter.addAll(centerList);
                    // centresAdapter.addAll(centerList);
                    // centresAdapter.notifyDataSetChanged();
                    // centerDropDown.setSelection(0);

                    // Toast.makeText(getActivity(),
                    // centerList.size()+"", 5000).show();

                    centresAdapter = new CustomAdapter<String>(getActivity(),
                            android.R.layout.simple_spinner_item, centerName);
                    centresAdapter
                            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    centerDropDown.setAdapter(centresAdapter);
                    if (nearState.equals(state1)) {
                        centerDropDown.setSelection(centerName
                                .indexOf(nearCenter.name));
                    } else {
                        center.setText(centerName.get(0));
                    }

//                    if (nearCenter != null) {
//                        // Log.v("loaded nearby center",
//                        // "loaded nearby center");
//                        try {
//                            centerName.indexOf(nearCenter.name);
//                            centerDropDown.setSelection(centerName
//                                    .indexOf(nearCenter.name));
//                        } catch (Exception e) {
//
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

    public void loadCentresNearby() {
        if (getArguments() != null && getArguments().containsKey("nearCountry")) {
            Bundle args = getArguments();
            nearCountry = args.getString("nearCountry");
            nearState = args.getString("nearState");
            nearCenter = (Center) args.getSerializable("nearCenter");
            loadCountries();
            return;
        }
        // countryDropDown.setOnItemSelectedListener(countryListener);
        if (!AppStatus.getInstance(getActivity()).isOnline(getActivity())) {
            CommonUtil.noInternetDialog(getActivity());
            return;
        }
        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(getActivity(), "Please wait...");
        }

        RequestParams rv = new RequestParams();
        rv.put("latitude", gps.getLatitude() + "");
        rv.put("longitude", gps.getLongitude() + "");
//        rv.put("latitude", "34.450538");
//        rv.put("longitude", "-86.941270");
        rv.put("apiKey", Data.apiKey);
        rv.put("distanceLimitMiles", "25");

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(Data.baseUrl + "venue/nearby", rv,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        // Log.v("nearby response = ", response);
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
                                int totalLanes = (center.has("totalLanes") ? center.getInt("totalLanes") : 0);
                                nearCenter = new Center(center
                                        .getString("name"),
                                        center.getInt("id"), center
                                        .getString("scoringType"),
                                        center.getJSONObject("phoneNumber")
                                                .getString("number")
                                                .replaceAll("[- ]+", ""), totalLanes);
                                // country.setText(nearCountry);

                                // countryDropDown.setSelection(countryList
                                // .indexOf(nearCountry));

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
                            // Log.v("Exception = ", e.toString());
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

    public String getSelectedCountry() {
        return countryList.get(countryDropDown.getSelectedItemPosition());
    }

}

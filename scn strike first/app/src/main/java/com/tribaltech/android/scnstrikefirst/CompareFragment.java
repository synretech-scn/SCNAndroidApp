package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tribaltech.android.entities.Center;
import com.tribaltech.android.entities.Xbowler;
import com.tribaltech.android.util.AppStatus;
import com.tribaltech.android.util.BowlersAdapter;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;
import com.tribaltech.android.util.FilterItem;
import com.tribaltech.android.util.GPSTracker;
import com.tribaltech.android.util.liveLanes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rmn.androidscreenlibrary.ASSL;

public class CompareFragment extends Fragment implements OnClickListener {

    int index = 0;
    ArrayList<Xbowler> xBowlerList;
    Activity context;
    // TextView //errorMsg;
    private Dialog friendsDailog;
    private ListView friendList;
    Boolean isAllXbowlerShowling = false;
    View footerView;
    protected BowlersAdapter gameList_details;
    private EditText search;

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
    private TextView errorMsg;
    protected Dialog comparisonPopup;
    private View view;
    private static String comparisonId = "";

    // friendsDailog.show();
    // InputMethodManager imm = (InputMethodManager) context
    // .getSystemService(Context.INPUT_METHOD_SERVICE);
    // imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
    // getMyFriends();
    // }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = (View) inflater.inflate(R.layout.compare_fragment, container,
                false);

        new ASSL(getActivity(), (ViewGroup) view.findViewById(R.id.root), 1134,
                720, true);
        context = getActivity();
        friendList = (ListView) view.findViewById(R.id.listViewFriends);

        errorMsg = (TextView) view.findViewById(R.id.errorMsg);
        // //errorMsg.setVisibility(View.GONE);
        // ((RelativeLayout) friendsDailog.findViewById(R.id.rltView))
        // .setVisibility(View.VISIBLE);

        search = (EditText) view.findViewById(R.id.searchBowler);

        final Button friendsBtn = (Button) view.findViewById(R.id.friendsBtn);
        final Button allxbowlerBtn = (Button) view
                .findViewById(R.id.allxbowlerBtn);

        view.findViewById(R.id.makeComparison).setOnClickListener(this);
        view.findViewById(R.id.makeComparisonAll).setOnClickListener(this);

        friendsBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAllXbowlerShowling) {
                    friendsBtn.setBackgroundResource(R.drawable.blue_right_semi_round);
                    allxbowlerBtn.setBackgroundResource(R.drawable.blue_left_outline_semi_round);
                    isAllXbowlerShowling = false;
                    getMyFriends();
                    search.setHint("Search");
                    search.setText("");
                }
            }
        });

        // GridView list = (GridView) dailog.findViewById(R.id.grid);

        allxbowlerBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isAllXbowlerShowling) {
                    friendsBtn.setBackgroundResource(R.drawable.blue_right_outline_semi_round);
                    allxbowlerBtn.setBackgroundResource(R.drawable.blue_left_semi_round);
                    search.setHint("Search");
                    search.setText("");
                    isAllXbowlerShowling = true;
                    getAllXbowlers(search.getText().toString(), "0", "10");

                }
            }
        });

        search.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {

                if (isAllXbowlerShowling) {
                    getAllXbowlers(arg0.getText().toString(), "0", "10");
                } else {
                    getMyFriends();
                }
                return false;
            }

        });


        final Spinner tabDropDown = (Spinner) view
                .findViewById(R.id.compareSpinner);
        List<String> gameModes = new ArrayList<String>();
        gameModes.add("Individual Users");
        gameModes.add("All XBowlers");

        final EditText selectedMode = (EditText) view
                .findViewById(R.id.spinnerText);
        CustomAdapter<String> countriesAdapter = new CustomAdapter<String>(
                getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_item, gameModes);
        countriesAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tabDropDown.setAdapter(countriesAdapter);
        tabDropDown
                .setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int position, long arg3) {
                        selectedMode.setText("Compare With : " + tabDropDown.getItemAtPosition(
                                position).toString());

                        switch (position) {
                            case 0:
                                errorMsg.setVisibility(View.GONE);
                                view.findViewById(R.id.allBowlerParent)
                                        .setVisibility(View.GONE);
                                view.findViewById(R.id.individualParent)
                                        .setVisibility(View.VISIBLE);
                                break;

                            case 1:
                                errorMsg.setVisibility(View.GONE);
                                view.findViewById(R.id.individualParent)
                                        .setVisibility(View.GONE);
                                view.findViewById(R.id.allBowlerParent)
                                        .setVisibility(View.VISIBLE);
                                break;

                            default:
                                break;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                });

        gps = new GPSTracker(getActivity());
        country = (EditText) view.findViewById(R.id.country);
        state = (EditText) view.findViewById(R.id.state);
        center = (EditText) view.findViewById(R.id.center);
        countryDropDown = (Spinner) view.findViewById(R.id.countries);
        stateDropDown = (Spinner) view.findViewById(R.id.states);
        centerDropDown = (Spinner) view.findViewById(R.id.centres);

        countryDropDown.setOnItemSelectedListener(countryListener);
        stateDropDown.setOnItemSelectedListener(stateAdapter);

        centerDropDown
                .setOnItemSelectedListener(new OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int position, long arg3) {

                        center.setText(centerDropDown.getItemAtPosition(
                                position).toString());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                });

        // if (savedInstanceState != null) {
        // String comparisonId = savedInstanceState.getString(
        // "comparisonId", "");
        // if (comparisonId != null && !comparisonId.isEmpty()) {
        // comparison(
        // savedInstanceState
        // .getBoolean("individualComparison"),
        // comparisonId);
        // }
        // if (savedInstanceState.getBoolean("individualComparison")) {
        // if (savedInstanceState.getBoolean("showAllBowlers")) {
        // allxbowlerBtn.performClick();
        // } else {
        // getMyFriends();
        // }
        // }
        // } else {
        // loadCentresNearby();
        // getMyFriends();
        // }
        statsIndividual(new FilterItem());
        return view;
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (getView().findViewById(R.id.individualParent).getVisibility() == View.VISIBLE) {
            outState.putBoolean("individualComparison", true);
            outState.putBoolean("showAllBowlers", isAllXbowlerShowling);
        } else {
            outState.putBoolean("individualComparison", false);
        }
        if (!comparisonId.isEmpty()) {
            outState.putString("comparisonId", comparisonId);
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        Log.d("dd", "conf changed");
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // getMyFriends();
    }

    public void getMyFriends() {

        if (!AppStatus.getInstance(context).isOnline(context)) {
            CommonUtil.noInternetDialog(context);
            return;
        }
        if (!CommonUtil.is_loading_showing())
            CommonUtil.loading_box(context, "Please wait...");
        RequestParams rv = new RequestParams();

        rv.put("token", CommonUtil.getAccessToken(context));
        rv.put("apiKey", Data.apiKey);
        rv.put("startIndex", "0");
        rv.put("pageSize", "100");

        AsyncHttpClient client = new AsyncHttpClient();

        client.setTimeout(CommonUtil.TIMEOUT);
        client.get(Data.baseUrl + "friend", rv, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {

                Log.v("response = ", response + ",");

                xBowlerList = new ArrayList<Xbowler>();
                xBowlerList.clear();
                try {
                    JSONArray jsArry = new JSONArray(response);
                    for (int i = 0; i < jsArry.length(); i++) {
                        JSONObject obj = jsArry.getJSONObject(i);
                        if (obj.getString("screenName").contains(
                                search.getText().toString())) {
                            xBowlerList.add(new Xbowler(obj
                                    .getString("screenName"), obj
                                    .getString("averageScore"), obj
                                    .getString("regionShortName"), obj
                                    .getString("regionLongName"), obj
                                    .getString("countryDisplayName"), obj
                                    .getString("countryCode"), obj
                                    .getString("friendId"), obj
                                    .getString("userId"), obj
                                    .getString("isFriend")));
                        }
                    }

                    // if (jsArry.length() <= 0) {
                    // // friendList.setVisibility(View.GONE);
                    // if (!isAllXbowlerShowling) {
                    // errorMsg.setVisibility(View.VISIBLE);
                    // }
                    // //
                    // errorMsg.setText("So sorry, You have not added any friends yet. Tap the 'ALL XBOWLERS' tab to get started!");
                    // }

                    if (jsArry.length() <= 0) {
                        friendList.setVisibility(View.GONE);
                        if (!isAllXbowlerShowling) {
                            errorMsg.setVisibility(View.VISIBLE);
                        }
//						((RelativeLayout) getView().findViewById(R.id.rltView))
//								.setVisibility(View.INVISIBLE);
//                        errorMsg.setText("So sorry, You have not added any friends yet. Tap the 'ALL XBOWLERS' tab to get started!");
                    } else if (xBowlerList.size() <= 0) {
                        friendList.setVisibility(View.GONE);
//						((RelativeLayout) getView().findViewById(R.id.rltView))
//								.setVisibility(View.INVISIBLE);
                        errorMsg.setText("We can't find anybody with that Username!");
                        if (!isAllXbowlerShowling) {
                            errorMsg.setVisibility(View.VISIBLE);
                        }

                    } else {
                        friendList.setVisibility(View.VISIBLE);
                        errorMsg.setVisibility(View.GONE);
//						((RelativeLayout) getView().findViewById(R.id.rltView))
//								.setVisibility(View.VISIBLE);
                    }

                    friendList.setDivider(null);
                    friendList.setDividerHeight(0);

                    if (friendList.getFooterViewsCount() > 0) {
                        friendList.removeFooterView(footerView);
                    }

                    gameList_details = new BowlersAdapter(xBowlerList, context);
                    friendList.setAdapter(gameList_details);
                    CommonUtil.loading_box_stop();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
                } catch (Exception e) {
                    CommonUtil.loading_box_stop();
                    Log.v("exception e", e.toString());
                }

            }

            @Override
            public void onFailure(Throwable e) {
                Log.v("response = ", e.toString());
                CommonUtil.loading_box_stop();
                CommonUtil.commonGameErrorDialog(context,
                        "An error occurred. Please try later.");
            }

        });
    }

    public void getAllXbowlers(final String searchText, String startIndex,
                               String pageSize) {
        errorMsg.setVisibility(View.GONE);
        if (!AppStatus.getInstance(context).isOnline(context)) {
            CommonUtil.noInternetDialog(context);
            return;
        }
        if (!CommonUtil.is_loading_showing())
            CommonUtil.loading_box(context, "Please wait...");
        RequestParams rv = new RequestParams();

        rv.put("token", CommonUtil.getAccessToken(context));
        rv.put("apiKey", Data.apiKey);
        rv.put("startIndex", startIndex);
        rv.put("pageSize", pageSize);
        rv.put("search", searchText);

        AsyncHttpClient client = new AsyncHttpClient();

        client.setTimeout(CommonUtil.TIMEOUT);
        client.get(Data.baseUrl + "friend/available", rv,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {

                        Log.v("response = ", response + ",");
                        CommonUtil.loading_box_stop();
                        xBowlerList = new ArrayList<Xbowler>();
                        xBowlerList.clear();
                        try {
                            JSONArray jsArry = new JSONArray(response);

                            if (jsArry.length() > 0) {
                                for (int i = 0; i < jsArry.length(); i++) {
                                    JSONObject obj = jsArry.getJSONObject(i);
                                    if (obj.getString("screenName").contains(
                                            search.getText().toString())) {
                                        xBowlerList.add(new Xbowler(
                                                obj.getString("screenName"),
                                                obj.getString("averageScore"),
                                                obj.getString("regionShortName"),
                                                obj.getString("regionLongName"),
                                                obj.getString("countryDisplayName"),
                                                obj.getString("countryCode"),
                                                obj.getString("friendId"), obj
                                                .getString("userId"),
                                                obj.getString("isFriend")));
                                    }
                                }

                                if (jsArry.length() <= 0 && search.length() > 0) {
                                    friendList.setVisibility(View.GONE);
                                    errorMsg.setVisibility(View.VISIBLE);
//									((RelativeLayout) getView().findViewById(
//											R.id.rltView)).setVisibility(View.INVISIBLE);
                                    errorMsg.setText("We can't find anybody with that Username!");
                                } else {
                                    friendList.setVisibility(View.VISIBLE);
//									((RelativeLayout) getView().findViewById(
//											R.id.rltView)).setVisibility(View.VISIBLE);
                                }

                                if (friendList.getFooterViewsCount() == 0
                                        && xBowlerList.size() >= 10) {
                                    footerView = ((LayoutInflater) context
                                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                                            .inflate(R.layout.footer, null,
                                                    false);
                                    RelativeLayout root = (RelativeLayout) footerView
                                            .findViewById(R.id.root);
                                    root.setLayoutParams(new AbsListView.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT, 80));
                                    ASSL.DoMagic(root);
                                    Button loadMore = (Button) footerView
                                            .findViewById(R.id.loadMore);

                                    loadMore.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            index = index + 10;
                                            getAllXbowlers_LoadMore(search
                                                            .getText().toString(),
                                                    index + "", "10");
                                        }
                                    });

                                    friendList.addFooterView(footerView);
                                }

                                if (jsArry.length() < 10) {
                                    friendList.removeFooterView(footerView);
                                }
                                gameList_details = new BowlersAdapter(
                                        xBowlerList, context);
                                friendList.setAdapter(gameList_details);
//								((RelativeLayout) getView().findViewById(
//										R.id.rltView)).setVisibility(View.VISIBLE);
                                errorMsg.setVisibility(View.GONE);
                            } else {
                                friendList.setVisibility(View.GONE);
                                gameList_details.notifyDataSetChanged();
//								((RelativeLayout) getView().findViewById(
//										R.id.rltView)).setVisibility(View.INVISIBLE);
                                errorMsg.setText("We can't find anybody with that Username!");
                                errorMsg.setVisibility(View.VISIBLE);

                            }
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
                        } catch (Exception e) {
                            Log.v("exception e", e.toString());
                        }

                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.v("response = ", e.toString());

                        CommonUtil.loading_box_stop();
                        CommonUtil.commonGameErrorDialog(context,
                                "An error occurred. Please try later.");
                    }

                });
    }

    public void getAllXbowlers_LoadMore(final String search, String startIndex,
                                        String pageSize) {
        errorMsg.setVisibility(View.GONE);
        if (!AppStatus.getInstance(context).isOnline(context)) {
            CommonUtil.noInternetDialog(context);
            return;
        }
        if (!CommonUtil.is_loading_showing())
            CommonUtil.loading_box(context, "Please wait...");
        RequestParams rv = new RequestParams();

        rv.put("token", CommonUtil.getAccessToken(context));
        rv.put("apiKey", Data.apiKey);
        rv.put("startIndex", startIndex);
        rv.put("pageSize", pageSize);
        rv.put("search", search);

        AsyncHttpClient client = new AsyncHttpClient();

        client.setTimeout(CommonUtil.TIMEOUT);
        client.get(Data.baseUrl + "friend/available", rv,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {

                        Log.v("response = ", response + ",");
                        CommonUtil.loading_box_stop();

                        try {
                            JSONArray jsArry = new JSONArray(response);
                            for (int i = 0; i < jsArry.length(); i++) {
                                JSONObject obj = jsArry.getJSONObject(i);

                                xBowlerList.add(new Xbowler(obj
                                        .getString("screenName"), obj
                                        .getString("averageScore"), obj
                                        .getString("regionShortName"), obj
                                        .getString("regionLongName"), obj
                                        .getString("countryDisplayName"), obj
                                        .getString("countryCode"), obj
                                        .getString("friendId"), obj
                                        .getString("userId"), obj
                                        .getString("isFriend")));
                            }

                            if (jsArry.length() < 10) {
                                friendList.removeFooterView(footerView);
                            }
                            gameList_details.notifyDataSetChanged();
                        } catch (Exception e) {
                            Log.v("exception e", e.toString());
                        }

                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.v("response = ", e.toString());

                        CommonUtil.loading_box_stop();
                        CommonUtil.commonGameErrorDialog(context,
                                "An error occurred. Please try later.");
                    }

                });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.makeComparison:
            case R.id.makeComparisonAll: {
                try {
                    if (view.findViewById(R.id.allBowlerParent).getVisibility() == View.GONE) {
                        if (gameList_details.selectedIndex == -1) {
                            Toast.makeText(getActivity(),
                                    "Please select an XBowler",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            comparison(true, gameList_details.Xbowlerdetail
                                    .get(gameList_details.selectedIndex).userId, gameList_details.Xbowlerdetail
                                    .get(gameList_details.selectedIndex).screenName);
                        }
                    } else {
                        String centerIdString = String.valueOf(centerList
                                .get(centerDropDown.getSelectedItemPosition()).id);
                        comparison(false, centerIdString, centerList
                                .get(centerDropDown.getSelectedItemPosition()).name);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void comparison(final boolean withIndividual, final String id, final String opponent) {
        comparisonId = id;
        if (!CommonUtil.is_loading_showing())
            CommonUtil.loading_box(getActivity(), "Please wait...");

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.addHeader("Content-type", "application/json");
        client.addHeader("Accept", "application/json");

        client.get(
                Data.baseUrl
                        + "UserStat/BowlingGameUserStatViewListComparisonBy"
                        + (withIndividual ? "User" : "Venue")
                        + "?token="
                        + CommonUtil.getAccessToken(getActivity()).replaceAll(
                        "[+]", "%2B") + "&apiKey=" + Data.apiKey
                        + (withIndividual ? "&anotherUserId=" : "&VenueId=")
                        + id,

                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();

                        JSONArray array;
                        try {
                            array = new JSONArray(response);
                            JSONArray insideArray, insideArray2;
                            Log.v("Response", "response" + array.toString());
                            ArrayList<String[]> datalist = new ArrayList<String[]>();

                            insideArray = array.getJSONArray(0);
                            insideArray2 = array.getJSONArray(1);

                            if (insideArray2.length() == 0) {
                                JSONObject res = insideArray.getJSONObject(0);
                                // JSONObject res1 =
                                // insideArray2.getJSONObject(0);

                                datalist.add(new String[]{
                                        "Average Score",
                                        String.format("%.2f",
                                                res.getDouble("averageScores")),
                                        ""});// ,String.format("%.2f",res1.getDouble("averageScores")});
                                // datalist.add(new
                                // String[]{"HighScore",String.format("%.2f",res.getDouble("highScore")});
                                datalist.add(new String[]{"High Score",
                                        res.getString("highScore"), ""});

                                datalist.add(new String[]{
                                        "Strike Percent",
                                        String.format("%.2f",
                                                res.getDouble("strikepercent"))
                                                + "%", ""});// ,String.format("%.2f",res1.getDouble("strikepercent")});
                                datalist.add(new String[]{
                                        "Single Pin Percent",
                                        String.format("%.2f", res
                                                .getDouble("singlePinpercent"))
                                                + "%", ""});// ,String.format("%.2f",res1.getDouble("singlePinpercent")});
                                datalist.add(new String[]{
                                        "Multi Pin Percent",
                                        String.format("%.2f", res
                                                .getDouble("multiPinpercent"))
                                                + "%", ""});// ,String.format("%.2f",res1.getDouble("multiPinpercent")});
                                datalist.add(new String[]{
                                        "Open Percent",
                                        String.format("%.2f",
                                                res.getDouble("openpercent"))
                                                + "%", ""});// ,String.format("%.2f",res1.getDouble("openpercent")});
                                datalist.add(new String[]{
                                        "Split Percent",
                                        String.format("%.2f",
                                                res.getDouble("splitpercent"))
                                                + "%", ""});// ,String.format("%.2f",res1.getDouble("splitpercent")});

                                String[] str = datalist.get(1);
                            } else {
                                JSONObject res = insideArray.getJSONObject(0);
                                JSONObject res1 = insideArray2.getJSONObject(0);

                                datalist.add(new String[]{
                                        "Average Score",
                                        String.format("%.2f",
                                                res.getDouble("averageScores")),
                                        String.format("%.2f",
                                                res1.getDouble("averageScores"))});
                                // datalist.add(new
                                // String[]{"HighScore",String.format("%.2f",res.getDouble("highScore")});
                                datalist.add(new String[]{"High Score",
                                        res.getString("highScore"),
                                        res1.getString("highScore")});

                                datalist.add(new String[]{
                                        "Strike Percent",
                                        String.format("%.2f",
                                                res.getDouble("strikepercent"))
                                                + "%",
                                        String.format("%.2f",
                                                res1.getDouble("strikepercent"))
                                                + "%"});
                                datalist.add(new String[]{
                                        "Single Pin Percent",
                                        String.format("%.2f", res
                                                .getDouble("singlePinpercent"))
                                                + "%",
                                        String.format("%.2f", res1
                                                .getDouble("singlePinpercent"))
                                                + "%"});
                                datalist.add(new String[]{
                                        "Multi Pin Percent",
                                        String.format("%.2f", res
                                                .getDouble("multiPinpercent"))
                                                + "%",
                                        String.format("%.2f", res1
                                                .getDouble("multiPinpercent"))
                                                + "%"});
                                datalist.add(new String[]{
                                        "Open Percent",
                                        String.format("%.2f",
                                                res.getDouble("openpercent"))
                                                + "%",
                                        String.format("%.2f",
                                                res1.getDouble("openpercent"))
                                                + "%"});
                                datalist.add(new String[]{
                                        "Split Percent",
                                        String.format("%.2f",
                                                res.getDouble("splitpercent"))
                                                + "%",
                                        String.format("%.2f",
                                                res1.getDouble("splitpercent"))
                                                + "%"});
                            }
                            Intent intent = new Intent(getActivity(), ComparisonActivity.class);
                            intent.putExtra("oppName", withIndividual ? opponent : "Others");
                            intent.putExtra("comparisonData", datalist);
                            startActivity(intent);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Throwable arg0) {
                        CommonUtil.loading_box_stop();
                        Log.e("request fail", arg0.toString());
//                        Toast.makeText(getActivity(), "Server Issue",
//                                Toast.LENGTH_LONG).show();

                    }
                });

    }

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
                statesAdapter = new CustomAdapter<String>(getActivity()
                        .getApplicationContext(),
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
                    loadCentres(countryDropDown.getSelectedItem().toString(),
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

    OnItemSelectedListener stateAdapter = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1,
                                   int position, long arg3) {

            state.setText(stateDropDown.getSelectedItem().toString());
            loadCentres(
                    countryDropDown.getSelectedItem().toString(),
                    stateDropDown.getSelectedItem().toString()
                            .replaceAll(" ", "%20"));
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
                            for (int i = 0; i < countriesArray.length(); i++) {
                                JSONObject country = countriesArray
                                        .getJSONObject(i);
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
                            allow = true;
                            countriesAdapter = new CustomAdapter<String>(
                                    getActivity().getApplicationContext(),
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
                        CommonUtil.commonGameErrorDialog(getActivity(),
                                "An error occured.Please try again.");

                    }
                });

    }

    private void loadCentres(String country, final String state1) {

        if (!AppStatus.getInstance(getActivity()).isOnline(getActivity())) {
            CommonUtil.noInternetDialog(getActivity());
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
                    for (int i = 0; i < centresArray.length(); i++) {
                        JSONObject center = centresArray.getJSONObject(i);
                        centerList.add(new Center(center.getString("name"),
                                center.getInt("id"), center
                                .getString("scoringType")));

                        centerName.add(center.getString("name"));
                    }

                    centresAdapter = new CustomAdapter<String>(getActivity()
                            .getApplicationContext(),
                            android.R.layout.simple_spinner_item, centerName);
                    centresAdapter
                            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    centerDropDown.setAdapter(centresAdapter);
                    if (nearState.equals(state1)) {
                        centerDropDown.setSelection(centerName
                                .indexOf(nearCenter.name));
                    } else {
                        center.setText(centerDropDown.getItemAtPosition(0)
                                .toString());
                    }

                    if (nearCenter != null) {
                        Log.v("loaded nearby center", "loaded nearby center");
                        try {
                            centerName.indexOf(nearCenter.name);
                            centerDropDown.setSelection(centerName
                                    .indexOf(nearCenter.name));
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
                CommonUtil.commonGameErrorDialog(getActivity(), e.getMessage()
                        + "");

            }
        });

    }

    private void loadCentresNearby() {
        // countryDropDown.setOnItemSelectedListener(countryListener);
        if (!AppStatus.getInstance(getActivity()).isOnline(getActivity())) {
            CommonUtil.noInternetDialog(getActivity());
            return;
        }
        // CommonUtil.loading_box(this, "Please wait...");

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
                        // CommonUtil.loading_box_stop();

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


    private void statsIndividual(FilterItem filterItem) {
        errorMsg.setVisibility(View.GONE);
        CommonUtil.loading_box(getActivity(), "Please wait...");

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.addHeader("Content-type", "application/json");
        client.addHeader("Accept", "application/json");

        client.get(
                Data.baseUrl
                        + "UserStat/BowlingGameUserStatViewListIndividual?token="
                        + CommonUtil.getAccessToken(getActivity()).replaceAll(
                        "[+]", "%2B") + "&apiKey=" + Data.apiKey
                        + CommonUtil.getFilterString(filterItem),

                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();

                        JSONArray array;
                        try {
                            array = new JSONArray(response);
                            if (array.isNull(0)) {
                                errorMsg.setText("You have no stats. Please play more games.");
                                errorMsg.setVisibility(View.VISIBLE);
                                getView().findViewById(R.id.rootChild).setVisibility(View.GONE);
                            } else {
                                loadCentresNearby();
                                getMyFriends();
                            }

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                            loadCentresNearby();
                            getMyFriends();
                        }

                    }

                    @Override
                    public void onFailure(Throwable arg0) {
                        Log.e("request fail", arg0.toString());
//                        Toast.makeText(getActivity(), "Server Issue",
//                                Toast.LENGTH_LONG).show();
                        CommonUtil.loading_box_stop();
                        loadCentresNearby();
                        getMyFriends();
                    }
                });
    }
}

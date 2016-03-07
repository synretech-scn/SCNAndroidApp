package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tribaltech.android.entities.Xbowler;
import com.tribaltech.android.util.AppStatus;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;
import com.tribaltech.android.util.HttpDeleteWithBody;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import rmn.androidscreenlibrary.ASSL;

public class FriendsActivity extends MenuIntent {
    int index = 0;
    ArrayList<Xbowler> xBowlerList;
    Activity context;
    TextView errorMsg;
    private ListView friendList;
    Boolean isAllXbowlerShowling = false;
    View footerView;
    protected XbowlerDetailsAdapter gameList_details;
    private EditText search;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_popup);
        new ASSL(this, (ViewGroup) findViewById(R.id.root), 1134,
                720, false);
        context = this;
        friendList = (ListView) findViewById(R.id.friendsList);

        errorMsg = (TextView) findViewById(R.id.errorMsg);
        errorMsg.setVisibility(View.GONE);


        search = (EditText) findViewById(R.id.searchBowler);

        final Button friendsBtn = (Button) findViewById(R.id.friendsBtn);
        final Button allxbowlerBtn = (Button) findViewById(R.id.allxbowlerBtn);

        // GridView list = (GridView) dailog.findViewById(R.id.grid);
//        friendsBtn.setBackgroundResource(R.drawable.blue_right_semi_round);
        friendsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAllXbowlerShowling) {
                    isAllXbowlerShowling = false;
                    friendsBtn.setBackgroundResource(R.drawable.blue_right_semi_round);
                    allxbowlerBtn.setBackgroundResource(R.drawable.blue_left_outline_semi_round);
                    errorMsg.setVisibility(View.GONE);

                    getMyFriends();
                    search.setHint("Search");
                    search.setText("");
                }
            }
        });

        allxbowlerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isAllXbowlerShowling) {
                    search.setHint("Search");
                    search.setText("");
                    isAllXbowlerShowling = true;
                    friendsBtn.setBackgroundResource(R.drawable.blue_right_outline_semi_round);
                    allxbowlerBtn.setBackgroundResource(R.drawable.blue_left_semi_round);
                    errorMsg.setVisibility(View.GONE);
                    getAllXbowlers(search.getText().toString(), "0", "10");

                }
            }
        });

        search.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                // if(arg1 == EditorInfo.IME_ACTION_SEARCH)
                // {
                // Toast.makeText(context, "search", 50000).show();
                // // search pressed and perform your functionality.
                // }
                // Toast.makeText(context, arg0.getText().toString()+" search2",
                // 50000).show();
                if (isAllXbowlerShowling) {
                    getAllXbowlers(arg0.getText().toString(), "0", "10");
                } else {

                    getMyFriends();
                }
                return false;
            }

        });
//        InputMethodManager imm = (InputMethodManager) context
//                .getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
        getMyFriends();
    }

    public void toggle(View v) {
        toggle();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ScreenMain.class);
        startActivity(intent);
        finish();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;

            case R.id.share:
                Intent intent = new Intent(this,TellAfriendPopup.class);
                startActivity(intent);
                break;
        }
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
                CommonUtil.loading_box_stop();
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

                    if (jsArry.length() <= 0) {
                        friendList.setVisibility(View.GONE);
                        errorMsg.setVisibility(View.VISIBLE);
                        errorMsg.setText("So sorry, You have not added any friends yet. Tap the 'ALL XBOWLERS' tab to get started!");
                    } else if (xBowlerList.size() <= 0) {
                        friendList.setVisibility(View.GONE);
                        errorMsg.setText("We can't find anybody with that Username!");
                        errorMsg.setVisibility(View.VISIBLE);

                    } else {
                        friendList.setVisibility(View.VISIBLE);
                        errorMsg.setVisibility(View.GONE);
                    }

//					friendList = (ListView) findViewById(R.id.listViewFriends);
                    friendList.setDivider(null);
                    friendList.setDividerHeight(0);

                    if (friendList.getFooterViewsCount() > 0) {
                        friendList.removeFooterView(footerView);
                    }

                    gameList_details = new XbowlerDetailsAdapter(xBowlerList,
                            context);
                    friendList.setAdapter(gameList_details);
                } catch (Exception e) {
                    Log.v("exception e", e.toString());
                    e.printStackTrace();
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

    public void deleteFreind(final Xbowler friend) {

        if (!AppStatus.getInstance(context).isOnline(context)) {
            CommonUtil.noInternetDialog(context);
            return;
        }
        if (!CommonUtil.is_loading_showing())
            CommonUtil.loading_box(context, "Please wait...");

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("id", friend.friendId);
                    HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(
                            Data.baseUrl
                                    + "friend?token="
                                    + CommonUtil.getAccessToken(
                                    context.getApplicationContext())
                                    .replaceAll("[+]", "%2B")
                                    + "&apiKey=" + Data.apiKey);
                    StringEntity entity = new StringEntity(jsonObj.toString(),
                            HTTP.UTF_8);
                    entity.setContentType("application/json");
                    httpDelete.setEntity(entity);
                    HttpClient client = new DefaultHttpClient();
                    final HttpResponse response = client.execute(httpDelete);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        CommonUtil.loading_box_stop();
                        context.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                xBowlerList.remove(friend);
                                gameList_details.notifyDataSetChanged();
                            }
                        });

                    } else {
                        CommonUtil.loading_box_stop();
                        context.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                CommonUtil.commonGameErrorDialog(context,
                                        "An error occurred. Please try later.");
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * @param regionLongName
     * @param regionShortName
     * @param userId
     * @param screenName
     * @param friendId
     */
    public void addFreind(String regionLongName, String regionShortName,
                          String userId, final String screenName, String friendId,
                          String countryDisplayName, String countryCode) {

        if (!AppStatus.getInstance(context).isOnline(context)) {
            CommonUtil.noInternetDialog(context);
            return;
        }
        if (!CommonUtil.is_loading_showing())
            CommonUtil.loading_box(context, "Please wait...");
        RequestParams rv = new RequestParams();

        rv.put("token", CommonUtil.getAccessToken(context));
        rv.put("apiKey", Data.apiKey);
        rv.put("countryCode", countryCode);
        rv.put("countryDisplayName", countryDisplayName);

        rv.put("friendId", friendId);
        rv.put("isFriend", "false");
        rv.put("regionLongName", regionLongName);
        rv.put("regionShortName", regionShortName);
        rv.put("userId", userId);
        rv.put("screenName", screenName);

        AsyncHttpClient client = new AsyncHttpClient();

        client.setTimeout(CommonUtil.TIMEOUT);
        client.post(Data.baseUrl + "friend", rv,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {

                        Log.v("response = ", response + ",");
                        CommonUtil.loading_box_stop();
                        CommonUtil.commonDialog(context, "Congratulations",
                                "Added successfully");

                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.v("response = ", e.toString());

                        if (e instanceof HttpResponseException) {
                            HttpResponseException hre = (HttpResponseException) e;
                            CommonUtil.loading_box_stop();
                            if (hre.getStatusCode() == 409) {
                                CommonUtil.commonErrorDialog(context,
                                        "You have already added " + screenName
                                                + " as friend.");
                            } else {
                                CommonUtil.commonGameErrorDialog(context,
                                        "An error occurred. Please try later.");
                            }
                        }

                    }

                });
    }

    public void getAllXbowlers(final String searchText, String startIndex,
                               String pageSize) {

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

                                    errorMsg.setText("We can't find anybody with that Username!");
                                } else {
                                    friendList.setVisibility(View.VISIBLE);

                                }

//								friendList = (ListView)findViewById(R.id.listViewFriends);
                                friendList.setDivider(null);
                                friendList.setDividerHeight(0);

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

                                    loadMore.setOnClickListener(new View.OnClickListener() {
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
                                gameList_details = new XbowlerDetailsAdapter(
                                        xBowlerList, context);
                                friendList.setAdapter(gameList_details);

                                errorMsg.setVisibility(View.GONE);
                            } else {
                                friendList.setVisibility(View.GONE);
                                gameList_details.notifyDataSetChanged();

                                errorMsg.setText("We can't find anybody with that Username!");
                                errorMsg.setVisibility(View.VISIBLE);

                            }

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

    public class XbowlerDetailsAdapter extends BaseAdapter {
        Context context;

        ArrayList<Xbowler> Xbowlerdetail;// = new ArrayList<Xbowler>();

        private LayoutInflater inflater;

        public XbowlerDetailsAdapter(ArrayList<Xbowler> livelanes, Context c) {
            context = c;
            this.Xbowlerdetail = livelanes;

            inflater = (LayoutInflater) c
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return Xbowlerdetail.size();
        }

        @Override
        public Object getItem(int arg0) {
            return Xbowlerdetail.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        class ViewHolder {

            TextView bowlerName, avgScore, regionName;
            RelativeLayout rlt;
            Button friend;
            int p;
        }

        @Override
        public View getView(int arg0, View convertView, ViewGroup arg2) {

            final ViewHolder holder;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.myfriends_listitem,
                        null);
                holder = new ViewHolder();
                // if (arg1 == null)

                holder.rlt = (RelativeLayout) convertView
                        .findViewById(R.id.root);

                holder.bowlerName = (TextView) convertView
                        .findViewById(R.id.name);
                holder.regionName = (TextView) convertView
                        .findViewById(R.id.regionName);
                holder.avgScore = (TextView) convertView
                        .findViewById(R.id.avgScore);
                // holder.no_of_players = (TextView) convertView
                // .findViewById(R.id.no_of_players);
                holder.friend = (Button) convertView.findViewById(R.id.friend);

                holder.rlt.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        110));
                holder.rlt.setTag(holder);
                ASSL.DoMagic(holder.rlt);
                holder.p = arg0;
                convertView.setTag(holder);
                holder.friend.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.p = arg0;

            holder.bowlerName.setText(Xbowlerdetail.get(arg0).screenName);
            holder.avgScore.setText("XBowling Avg: "
                    + Xbowlerdetail.get(arg0).averageScore);

            if (Xbowlerdetail.get(arg0).regionLongName.length() <= 0) {
                holder.regionName.setText("---");
            } else {
                holder.regionName
                        .setText(Xbowlerdetail.get(arg0).regionLongName + ", "
                                + Xbowlerdetail.get(arg0).countryCode);
            }
            if (Xbowlerdetail.get(arg0).isFriend.equalsIgnoreCase("true")) {
                holder.friend.setText("Remove");
                holder.friend
                        .setBackgroundResource(R.drawable.red_corner);
            } else {
                holder.friend.setText("Add");
                holder.friend
                        .setBackgroundResource(R.drawable.blue_corner);

            }
            holder.friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewHolder holder = (ViewHolder) v.getTag();
                    if (Xbowlerdetail.get(holder.p).isFriend
                            .equalsIgnoreCase("true")) {
                        deleteFreind(Xbowlerdetail.get(holder.p));
                    } else {
                        addFreind(Xbowlerdetail.get(holder.p).regionLongName,
                                Xbowlerdetail.get(holder.p).regionShortName,
                                Xbowlerdetail.get(holder.p).userId,
                                Xbowlerdetail.get(holder.p).screenName,
                                Xbowlerdetail.get(holder.p).friendId,
                                Xbowlerdetail.get(holder.p).countryDisplayName,
                                Xbowlerdetail.get(holder.p).countryCode);
                    }
                }
            });
            return convertView;
        }

    }



}

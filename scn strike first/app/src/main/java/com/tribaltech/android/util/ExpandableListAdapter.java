package com.tribaltech.android.util;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tribaltech.android.scnstrikefirst.LiveScoreList;
import com.tribaltech.android.scnstrikefirst.LiveScoreScreen;
import com.tribaltech.android.scnstrikefirst.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import rmn.androidscreenlibrary.ASSL;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader = new ArrayList<>(); // header titles
    // child data in format of header title, child title
    private HashMap<String, List<LiveScorePlayerData>> _listDataChild = new HashMap<>();
    String venueId;
    Handler handler;
    String centerName;
    Object lock;
    ListView list;
    TextView noLaneText;
    TextView availableLanes;

    public ExpandableListAdapter(Context context,
                                 String venueId, String centerName, ListView list, TextView noLaneText,
                                 TextView availableLanes) {
        this._context = context;
        this.venueId = venueId;
        handler = new Handler();
        this.centerName = centerName;
        lock = new Object();
        this.list = list;
        this.noLaneText = noLaneText;
        this.availableLanes = availableLanes;
    }

    public void startUpdating() {
//        updateAllLanes(venueId, true);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updateAllLanes(venueId, false);
                handler.postDelayed(this, 20000);
            }
        };
        handler.postDelayed(runnable, 0);
    }

    public void stopUpdating() {
        handler.removeCallbacksAndMessages(null);
    }

    public void setdata(List<String> listDataHeader,
                        HashMap<String, List<LiveScorePlayerData>> listChildData) {
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    private static class ViewHolderChild {
        TextView playerName;
        TextView frames;
        TextView framesLabel;
        TextView scores;
        RelativeLayout rlt;
    }

    private static class ViewHolderGroup {
        TextView laneNum;
        Button indicator;
        RelativeLayout rlt;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        ViewHolderChild holder;
        final LiveScorePlayerData childData = (LiveScorePlayerData) getChild(
                groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.live_list_child, null);
            holder = new ViewHolderChild();
            holder.playerName = (TextView) convertView
                    .findViewById(R.id.playerName);
            holder.frames = (TextView) convertView
                    .findViewById(R.id.frameCount);
            holder.framesLabel = (TextView) convertView
                    .findViewById(R.id.frameLabel);
            holder.scores = (TextView) convertView.findViewById(R.id.scores);

            holder.rlt = (RelativeLayout) convertView.findViewById(R.id.root);
            holder.rlt.setLayoutParams(new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT, 110));
            ASSL.DoMagic(holder.rlt);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolderChild) convertView.getTag();
        }
        holder.rlt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_context, LiveScoreScreen.class);
                intent.putExtra("laneNumber", _listDataHeader.get(groupPosition));
                intent.putExtra("centerName", centerName);
                intent.putExtra("bowlerName", childData.playerName);
                intent.putExtra("scoringType", "Live");
                intent.putExtra("rowKey", childData.rowKey);
                intent.putExtra("venueId", Integer.parseInt(venueId));
                _context.startActivity(intent);
            }
        });
        holder.playerName.setText(childData.playerName);
        holder.frames.setText(childData.frameCount + "");
        if (childData.frameCount == 1) {
            holder.framesLabel.setText("Frame");
        } else {
            holder.framesLabel.setText("Frames");
        }
        holder.scores.setText(childData.scores);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded,
                             View convertView, ViewGroup parent) {

        final ViewHolderGroup holder;
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.live_list_group, null);
            holder = new ViewHolderGroup();
            holder.laneNum = (TextView) convertView.findViewById(R.id.laneNum);
            holder.indicator = (Button) convertView
                    .findViewById(R.id.indicator);
            holder.rlt = (RelativeLayout) convertView.findViewById(R.id.root);
            holder.rlt.setLayoutParams(new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT, 90));
            ASSL.DoMagic(holder.rlt);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolderGroup) convertView.getTag();
        }

        if (isExpanded) {
            holder.indicator.setBackgroundResource(R.drawable.minus_selector);
        } else {
            holder.indicator.setBackgroundResource(R.drawable.plus_selector);
        }
        holder.laneNum.setText("Lane " + headerTitle);
        // getLiveScoreUpdate(venueId, _listDataHeader.get(groupPosition));

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
        if (_listDataChild.get(_listDataHeader.get(groupPosition)).size() == 0) {
//            getLiveScoreUpdate(venueId, _listDataHeader.get(groupPosition));
        }
    }

    private void getLiveScoreUpdate(final String venueId, final String lane) {

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String to = df.format(c.getTime());

        String from = df.format(CommonUtil.minusTime(-90).getTime());

        if (!AppStatus.getInstance(_context).isOnline(_context)) {
            CommonUtil.noInternetDialog(_context);
            return;
        }

        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(_context, "Loading...");
        }

        RequestParams rv = new RequestParams();
        rv.put("from", from);
        rv.put("to", to);
        rv.put("apiKey", Data.apiKey);
        rv.put("token", CommonUtil.getAccessToken(_context));

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(Data.baseUrl + "venue/" + venueId + "/lane/" + lane, rv,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        try {
                            JSONArray jsonArry = new JSONArray(response);
                            synchronized (lock) {
                                List<LiveScorePlayerData> playerData = _listDataChild
                                        .get(lane);
                                playerData.clear();
                                JSONObject obj;
                                for (int i = 0; i < jsonArry.length(); i++) {
                                    obj = jsonArry.getJSONObject(i);
                                    int frame = obj.getInt("latestSquareNumber") + 1;
                                    frame /= 2;
                                    frame = frame - frame / 11;
                                    LiveScorePlayerData data = new LiveScorePlayerData(
                                            obj.getString("name"), frame, obj
                                            .getString("finalScore"), obj
                                            .getString("rowKey"));
                                    playerData.add(data);
                                }
                                notifyDataSetChanged();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.loading_box_stop();
                    }
                });

    }


    public void updateAllLanes(final String venueId, boolean showLoading) {

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String to = df.format(c.getTime());

        String from = df.format(CommonUtil.minusTime(-90).getTime());

        if (!AppStatus.getInstance(_context).isOnline(_context)) {
            if (showLoading) {
                CommonUtil.noInternetDialog(_context);
            }
            return;
        }
        if (showLoading && !CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(_context, "Loading...");
        }

        RequestParams rv = new RequestParams();
        rv.put("from", from);
        rv.put("to", to);
        rv.put("apiKey", Data.apiKey);
        rv.put("token", CommonUtil.getAccessToken(_context));

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(Data.baseUrl + "venue/" + venueId + "/summarywithscore", rv,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        Log.d("Refresh Live Score", "updateAll Called");
                        Set<String> visitedSet = new HashSet<String>(_listDataHeader);
                        try {
                            JSONArray jsonArry = new JSONArray(response);
                            int avLanes = LiveScoreList.laneCount;
                            avLanes -= jsonArry.length();
                            if (avLanes <= 0) {
                                avLanes = 0;
                            }
                            availableLanes.setText(avLanes + "");

                            if (jsonArry.length() == 0) {
                                list.setVisibility(View.GONE);
                                noLaneText.setVisibility(View.VISIBLE);
                                noLaneText.setText("No Active Lanes at "
                                        + centerName + "!");
                            } else {
                                list.setVisibility(View.VISIBLE);
                                noLaneText.setVisibility(View.GONE);
                            }
                            JSONObject obj;
                            String laneNo;
                            for (int i = 0; i < jsonArry.length(); i++) {
                                obj = jsonArry.getJSONObject(i);
                                laneNo = obj.getString("laneNumber");
                                visitedSet.remove(laneNo);
                                synchronized (lock) {
                                    List<LiveScorePlayerData> data;
                                    if (_listDataChild.get(laneNo) == null) {
                                        //New Lane added
                                        _listDataHeader.add(laneNo);
                                        data = new ArrayList<LiveScorePlayerData>();
                                        _listDataChild.put(laneNo, data);
                                    } else {
                                        data = _listDataChild.get(laneNo);
                                        data.clear();
                                    }
                                    JSONArray players = obj.getJSONArray("playerList");
                                    JSONObject player;
                                    for (int j = 0; j < players.length(); j++) {
                                        player = players.getJSONObject(j);
                                        data.add(new LiveScorePlayerData(java.net.URLDecoder.decode(player.getString("name"), "UTF-8"),
                                                player.getInt("latestFrame"),
                                                player.getString("totalScore"), player.getString("rowKey")));
                                    }
                                }

//                                for (String lane : visitedSet) {
//                                    _listDataHeader.remove(lane);
//                                    _listDataChild.put(lane, null);
//                                }

                                notifyDataSetChanged();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.loading_box_stop();
                    }
                });

    }
}
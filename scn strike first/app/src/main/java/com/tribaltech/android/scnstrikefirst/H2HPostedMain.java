package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tribaltech.android.util.AppConstants;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;
import com.tribaltech.android.util.PostedOpponentDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rmn.androidscreenlibrary.ASSL;


public class H2HPostedMain extends Activity {

    String bowlingGameId;
    FrameViewAdapter postedFrameAdapter;
    ListView postedFrameList;
    Runnable updatePostedChallengers;
    Handler handler;
    LinearLayout errorMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h2h_posted_main);
        new ASSL(this, (ViewGroup) findViewById(R.id.root), 1134,
                720, false);
        handler = new Handler();
        errorMsg = (LinearLayout) findViewById(R.id.errorMsgH2H);
        bowlingGameId = Data.gameData.gameId;
        postedFrameList = (ListView) findViewById(R.id.h2hPostedList);
        postedFrameAdapter = new FrameViewAdapter(
                new ArrayList<PostedOpponentDetails>(), H2HPostedMain.this);
        postedFrameList.setAdapter(postedFrameAdapter);

        updatePostedChallengers = new Runnable() {

            @Override
            public void run() {
                getChallengers();
                handler.postDelayed(this, 20000);
            }
        };

        postedFrameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Data.lastPostedOpp = postedFrameAdapter.opponentList.get(position);
                Intent intent = new Intent(H2HPostedMain.this, ChallengeView.class);
                intent.putExtra("postedOpp", postedFrameAdapter.opponentList.get(position));
                intent.putExtras(getIntent());
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.removeCallbacksAndMessages(null);
        handler.post(updatePostedChallengers);
        Data.lastPostedOpp = null;
        Data.lastChallengeVisited = AppConstants.H2H_POSTED;
    }

    private void getChallengers() {
        Log.v("func", "getChallengers");
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.get(
                Data.baseUrl
                        + "bowlinggame/"
                        + bowlingGameId
                        + "/challengers?token="
                        + CommonUtil.getAccessToken(getApplicationContext())
                        .replaceAll("[+]", "%2B") + "&apiKey="
                        + Data.apiKey, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        try {
                            errorMsg.setVisibility(View.GONE);
                            postedFrameList.setVisibility(View.VISIBLE);
                            JSONArray jsonArray = new JSONArray(response);
                            List<PostedOpponentDetails> opponents = postedFrameAdapter
                                    .getOpponentList();
                            opponents.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject json = jsonArray.getJSONObject(i);
                                opponents.add(new PostedOpponentDetails(json
                                        .getString("userScreenName"), json
                                        .getString("opponentBowlingGameId"),
                                        json.getString("competitionId"), json
                                        .getString("userAverage"), json
                                        .getString("userHandicap"),
                                        json.getString("userRegion"), json
                                        .getString("opponentScore"),
                                        json.getString("opponentHandicapScore")));
                            }
                            if (opponents.size() == 0) {
                                errorMsg.setVisibility(View.VISIBLE);
                                postedFrameList.setVisibility(View.GONE);
                                findViewById(R.id.headerStrip).setVisibility(View.GONE);
                            } else {
                                findViewById(R.id.headerStrip).setVisibility(View.VISIBLE);
                            }
                            Data.postedEntered = (opponents.size() != 0);
                            postedFrameAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                    }
                });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addOpponent: {
                Intent intent = new Intent(this, ChooseOpponent.class);
                intent.putExtra("bowlingGameId", bowlingGameId);
                intent.putExtra("type", "posted");
                startActivity(intent);
            }
            break;

            case R.id.back: {
                Intent intent = new Intent(this, Challenges.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private static class FrameViewAdapter extends BaseAdapter {

        List<PostedOpponentDetails> opponentList;
        Context ctx;

        public FrameViewAdapter(List<PostedOpponentDetails> opponentList,
                                Context ctx) {
            this.opponentList = opponentList;
            this.ctx = ctx;
        }

        @Override
        public int getCount() {
            return opponentList.size();
        }

        @Override
        public Object getItem(int position) {
            return opponentList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        private static class ViewHolder {
            TextView screenName;
            TextView userAverage;
            TextView userRegion;
            TextView opponentScore;
            RelativeLayout rlt;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) ctx
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.posted_score_listitem,
                        null);
                holder = new ViewHolder();
                holder.rlt = (RelativeLayout) convertView
                        .findViewById(R.id.root);
                holder.screenName = (TextView) convertView
                        .findViewById(R.id.userName);
                holder.userAverage = (TextView) convertView
                        .findViewById(R.id.userAverage);
                holder.userRegion = (TextView) convertView
                        .findViewById(R.id.region_value);
                holder.opponentScore = (TextView) convertView
                        .findViewById(R.id.opponentScore);
                holder.rlt.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        144));
                holder.rlt.setTag(holder);
                ASSL.DoMagic(holder.rlt);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.screenName.setText(opponentList.get(position).screenName);
            holder.userAverage.setText(opponentList.get(position).userAverage + "/" +
                    opponentList.get(position).userHandicap);
            holder.userRegion.setText(opponentList.get(position).userRegion);
            holder.opponentScore
                    .setText(opponentList.get(position).opponentScore + "/" +
                            opponentList.get(position).opponentHandicapScore);
            return convertView;
        }

        public List<PostedOpponentDetails> getOpponentList() {
            return opponentList;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Challenges.class);
        startActivity(intent);
        finish();
    }
}

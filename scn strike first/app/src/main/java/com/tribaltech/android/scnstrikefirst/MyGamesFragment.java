package com.tribaltech.android.scnstrikefirst;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tribaltech.android.entities.Game;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;
import com.tribaltech.android.util.FilterItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import rmn.androidscreenlibrary.ASSL;

/**
 * Created by cl-99 on 3/19/2015.
 */
public class MyGamesFragment extends Fragment implements UserStats.Filterable {

    ListView gamesList;
    MyGameAdapter gameAdapter;
    RelativeLayout errorMsg;
    String json = "";

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.mygames_fragment,
                container, false);
        new ASSL(getActivity(), (ViewGroup) view.findViewById(R.id.root), 1134,
                720, true);
        errorMsg = (RelativeLayout) view.findViewById(R.id.errorMsg);
        gamesList = (ListView) view.findViewById(R.id.myGamesList);
        gameAdapter = new MyGameAdapter(new ArrayList<String[]>(), getActivity());
        gamesList.setAdapter(gameAdapter);
        myGames(CommonUtil.getFilter(getActivity()));
        return view;
    }

    private void myGames(FilterItem filterItem) {
        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(getActivity(), "Please wait...");
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.addHeader("Content-type", "application/json");
        client.addHeader("Accept", "application/json");

        client.get(
                Data.baseUrl
                        + "scoredbowlinggame/getmygameHistory?token="
                        + CommonUtil.getAccessToken(getActivity()).replaceAll(
                        "[+]", "%2B") + "&apiKey=" + Data.apiKey
                        + CommonUtil.getFilterString(filterItem),

                                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        parseData(response);
                    }

                    @Override
                    public void onFailure(Throwable arg0) {
                        Log.e("request fail", arg0.toString());
                        CommonUtil.commonErrorDialog(getActivity(), "There was a problem connecting to the server.Please try again.");
//                        Toast.makeText(getActivity(), "Server Issue",
//                                Toast.LENGTH_LONG).show();
                        CommonUtil.loading_box_stop();
                    }
                });
    }

    private void parseData(String response) {
        try {
            JSONArray array = new JSONArray(response);
            JSONObject game;
            gameAdapter.data.clear();
            for (int i = 0; i < array.length(); i++) {
                game = array.getJSONObject(i).getJSONObject("scoredGame");
                gameAdapter.data.add(new String[]{game.getString("name"), game.getString("finalScore"),
                        game.toString(), array.getJSONObject(i).toString()});
            }
            gameAdapter.notifyDataSetChanged();
            errorMsg.setVisibility(array.length() == 0 ? View.VISIBLE : View.GONE);
//                            gamesList.setVisibility(array.length() != 0 ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void filter(FilterItem filterItem) {
        myGames(filterItem);
    }


    static class MyGameAdapter extends BaseAdapter {

        private List<String[]> data;
        WeakReference<Context> actCtx;

        public MyGameAdapter(List<String[]> data, Context ctx) {
            this.data = data;
            actCtx = new WeakReference<Context>(ctx);
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        private static class ViewHolderChild {
            TextView playerName;
            TextView score;
            TextView tag1;
            TextView tag2;
            Button dotsBtn;
            Button dots;
            RelativeLayout rlt;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolderChild holder;
            final String[] childData = data.get(position);

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) actCtx.get()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.mygames_item, null);
                holder = new ViewHolderChild();
                holder.playerName = (TextView) convertView
                        .findViewById(R.id.playerName);
                holder.tag1 = (TextView) convertView
                        .findViewById(R.id.tag1);
                holder.tag2 = (TextView) convertView
                        .findViewById(R.id.tag2);
                holder.score = (TextView) convertView
                        .findViewById(R.id.scores);
                holder.dotsBtn = (Button) convertView.findViewById(R.id.dotsBtn);
                holder.rlt = (RelativeLayout) convertView.findViewById(R.id.root);
                holder.rlt.setLayoutParams(new AbsListView.LayoutParams(
                        AbsListView.LayoutParams.MATCH_PARENT, 110));
                ASSL.DoMagic(holder.rlt);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolderChild) convertView.getTag();
            }
            holder.tag1.setVisibility(View.GONE);
            holder.tag2.setVisibility(View.GONE);
            holder.dotsBtn.setVisibility(View.GONE);

            holder.rlt.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(actCtx.get(), GameScreen.class);
                    String gameId = "";
                    try {
                        gameId = new JSONObject(childData[2]).getString("bowlingGameId");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Data.gameData = new Game("", "History", gameId, "");
                    Data.gameData.screenName = CommonUtil.getScreenName(actCtx.get());
                    intent.putExtra("gameName", childData[0]);
                    intent.putExtra("response", childData[2]);
                    actCtx.get().startActivity(intent);
                }
            });

            try {
                JSONArray array = new JSONObject(childData[3]).getJSONArray("gameTags");
                for (int i = 0; i < 3 && i < array.length(); i++) {
                    if (i == 0) {
                        holder.tag1.setVisibility(View.VISIBLE);
                        holder.tag1.setText(array.getJSONObject(i).getString("tag"));
                    } else if (i == 1) {
                        holder.tag2.setVisibility(View.VISIBLE);
                        holder.tag2.setText(array.getJSONObject(i).getString("tag"));
                    } else {
                        holder.dotsBtn.setVisibility(View.VISIBLE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.playerName.setText(childData[0]);
            holder.score.setText(childData[1]);
            return convertView;
        }

        public List<String[]> getData() {
            return data;
        }

        public void setData(List<String[]> data) {
            this.data = data;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        if (savedInstanceState != null && savedInstanceState.containsKey("json")) {
//            String json = savedInstanceState.getString("json");
//            if (json.isEmpty()) {
//                myGames(CommonUtil.getFilter(getActivity()));
//            } else {
//                parseData(json);
//            }
//        } else {
//            myGames(CommonUtil.getFilter(getActivity()));
//        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putString("json", json);
    }
}

package com.tribaltech.android.util;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tribaltech.android.scnstrikefirst.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import rmn.androidscreenlibrary.ASSL;

public class CoachViewAdapter extends BaseAdapter {

    Context ctx;
    //    List<CoachViewItem> data;
//    Map<String, CoachViewItem> data;
    List<CoachViewItem> itemList;
    String venueId;
    String lane;
    Handler handler;
    LayoutInflater inflater;
    RelativeLayout noItems;

    public CoachViewAdapter(Context ctx, List<CoachViewItem> data, String venueId, String lane, RelativeLayout noItems) {
        this.ctx = ctx;
//        this.data = data;
        this.itemList = new ArrayList<>();
//        this.data = new HashMap<>();
        this.venueId = venueId;
        this.lane = lane;
        handler = new Handler();
        this.noItems = noItems;
    }

    private static class ViewHolder {
        TextView playerName;
        TextView score;
        TextView[] squareScores = new TextView[22];
        List<Button[]> pins = new ArrayList<>();
        LinearLayout rlt;
        LinearLayout framesContainer;
        int level = 0;
        int maxSquareBowled = 0;
        int maxFrameBowled = 0;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final CoachViewItem item = itemList.get(position);
        final ViewHolder holder;
        if (convertView == null) {
            inflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.coach_view_item, null);
            holder = new ViewHolder();
            holder.rlt = (LinearLayout) convertView.findViewById(R.id.root);
            holder.framesContainer = (LinearLayout) convertView.findViewById(R.id.frameView);
            holder.playerName = (TextView) convertView
                    .findViewById(R.id.bowlerName);
            holder.score = (TextView) convertView
                    .findViewById(R.id.totalScore);

            for (int i = 1; i <= 10; i++) {
                LinearLayout layout = new LinearLayout(ctx);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(8, 0, 8, 0);
                layout.setLayoutParams(layoutParams);
                layout.setOrientation(LinearLayout.VERTICAL);

                View frame = inflater.inflate(i == 10 ? R.layout.single_frame_last : R.layout.single_frame, layout);
                inflater.inflate(R.layout.single_pin, layout);
                inflater.inflate(R.layout.single_pin, layout);
                if (i == 10) {
                    inflater.inflate(R.layout.single_pin, layout);
//                    pins.setVisibility(View.GONE);
                }
//                frame.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 103));
//                ASSL.DoMagic(frame);
                ((TextView) frame.findViewById(R.id.frameText)).setText(i + "");
                holder.framesContainer.addView(layout);
//                layout.getChildAt(1).setVisibility(View.GONE);
//                layout.getChildAt(2).setVisibility(View.GONE);
//                if (i == 10) {
//                    layout.getChildAt(3).setVisibility(View.GONE);
//                }
//                layout.addView(inflater.inflate(R.layout.single_frame,null),0);
//                layout.addView(inflater.inflate(R.layout.single_pin,null),1);
            }

//            for (int i = 1; i <= 21; i++) {
//                holder.squareScores[i] = (TextView) convertView.findViewById(CommonUtil.
//                        getIdFromName("throw" + i, R.id.class));
//            }
            LinearLayout layout = (LinearLayout) holder.framesContainer.getChildAt(0);
            int children = layout.getChildCount();
            children--;
            holder.rlt.setLayoutParams(new AbsListView.LayoutParams(
                    LayoutParams.MATCH_PARENT, 218 + 110 * 2));
            ASSL.DoMagic(holder.rlt);
            holder.rlt.setTag(holder);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        LinearLayout layout;// = (LinearLayout) holder.framesContainer.getChildAt(0);
        ViewGroup pinParent;
        int frameNo;
        TextView square;
        TextView frame;
        Button pin;

        if (!item.squareScores[21].isEmpty()) {
            holder.level = 3;
        } else if (!item.squareScores[2].isEmpty()) {
            holder.level = 2;
        } else if (!item.squareScores[1].isEmpty()) {
            holder.level = 1;
        }

//        int maxSquareBowled
        for (int i = 1; i <= 21; i++) {
            frameNo = (i + 1) / 2;
            if (frameNo > 10) {
                frameNo = 10;
            }
            layout = (LinearLayout) holder.framesContainer.getChildAt(frameNo - 1);
            square = (TextView) layout.findViewById(i == 21 ? R.id.throw3 :
                    ((i & 1) > 0 ? R.id.throw1 : R.id.throw2));
            square.setText(item.squareScores[i]);
            frame = (TextView) layout.findViewById(R.id.frameScore);
            frame.setText(item.frameScore[frameNo]);

            if ((frameNo < 10) && item.squareScores[i].equalsIgnoreCase("X")) {
                layout.getChildAt(2).setVisibility(View.GONE);
            } else if (!item.squareScores[i].isEmpty()) {
                layout.getChildAt(2).setVisibility(View.VISIBLE);
            }

            if (i == 21 && !item.squareScores[i].isEmpty()) {
                holder.rlt.setLayoutParams(new AbsListView.LayoutParams(
                        LayoutParams.MATCH_PARENT, (int) ((218 + 110 * 3) * ASSL.Yscale())));
            } else {
                holder.rlt.setLayoutParams(new AbsListView.LayoutParams(
                        LayoutParams.MATCH_PARENT, (int) ((218 + 110 * 2) * ASSL.Yscale())));
            }

            if (!item.squareScores[i].isEmpty()
                    && i > holder.maxSquareBowled) {
                holder.maxSquareBowled = i;

                if (holder.maxSquareBowled > holder.maxFrameBowled * 2) {
                    if (holder.maxFrameBowled != 0)
                        toggleFrameState((ViewGroup) ((ViewGroup) holder.framesContainer.getChildAt(holder.maxFrameBowled - 1)).getChildAt(0),
                                R.drawable.bowled_background_round,
                                R.drawable.bowled_background_bottom_round, R.drawable.bowled_background,
                                AppConstants.BOWLED_TEXT_COLOR);
                    int temp = holder.maxFrameBowled + 1;
                    if (temp > 10) {
                        temp = 10;
                    }
                    holder.maxFrameBowled = temp;
                    toggleFrameState((ViewGroup) layout.getChildAt(0), R.drawable.selected_background_round,
                            R.drawable.selected_background_bottom_round, R.drawable.selected_background,
                            AppConstants.SELECTED_TEXT_COLOR);

                }
            }


            int throwNo = 2 - (i & 1);
            if (i == 21) {
                throwNo = 3;
            }
            pinParent = (ViewGroup) layout.getChildAt(throwNo);
//            pinParent.setVisibility(View.VISIBLE);

//            holder.rlt.setLayoutParams(new AbsListView.LayoutParams(
//                    LayoutParams.MATCH_PARENT, (int) ((210 + 114 * (holder.level)) * ASSL.Xscale())));

//            if (pinParent == null) {
//                pinParent = (ViewGroup) inflater.inflate(R.layout.single_pin, layout);
////                pinParent.setLayoutParams(new LinearLayout.LayoutParams((int) (102.5),
////                        (int) (100)));
////                ASSL.DoMagic(pinParent);
//                holder.rlt.setLayoutParams(new AbsListView.LayoutParams(
//                        LayoutParams.MATCH_PARENT, (int) ((210 + 114 * (layout.getChildCount() - 1)) * ASSL.Yscale())));
////                ASSL.DoMagic(holder.rlt);
//            }

            int pinState = item.pinFall[i];
            for (int j = 1; j <= 10; j++) {
                pin = (Button) pinParent.findViewById(CommonUtil.getIdFromName(
                        "firstpin" + j, R.id.class));
                if ((pinState & (int) Math.pow(2, j - 1)) > 0) {
                    pin.setBackgroundResource(R.drawable.ball_up_small);
                } else {
                    pin.setBackgroundResource(R.drawable.ball_down_small);
                }
            }
        }


//        LinearLayout layout = (LinearLayout) holder.framesContainer.getChildAt(0);
//        int children = layout.getChildCount();
//        children--;
//        holder.rlt.setLayoutParams(new AbsListView.LayoutParams(
//                LayoutParams.MATCH_PARENT, (int) ((210 + (children * 114)) * ASSL.Xscale())));
//        ASSL.DoMagic(holder.rlt);

        holder.playerName.setText(item.playerName);
        holder.score.setText(item.score);
        return convertView;
    }

    public void toggleFrameState(ViewGroup parent, int frameBack, int scoreBack, int squareBack, int textColor) {

        TextView frame = (TextView) parent.getChildAt(0);
        TextView score = (TextView) parent.getChildAt(2);
        TextView square1 = (TextView) ((ViewGroup) parent.getChildAt(1)).getChildAt(0);
        TextView square2 = (TextView) ((ViewGroup) parent.getChildAt(1)).getChildAt(1);

        if (((ViewGroup) parent.getChildAt(1)).getChildAt(2) != null) {
            TextView square3 = (TextView) ((ViewGroup) parent.getChildAt(1)).getChildAt(2);
            square3.setBackgroundResource(squareBack);
            square3.setTextColor(textColor);
        }

        frame.setBackgroundResource(frameBack);
        score.setBackgroundResource(scoreBack);
        square1.setBackgroundResource(squareBack);
        square2.setBackgroundResource(squareBack);

        frame.setTextColor(textColor);
        score.setTextColor(textColor);
        square1.setTextColor(textColor);
        square2.setTextColor(textColor);
    }

    public void startUpdating() {
//        data.add(new CoachViewItem("Abc", "34", null, null));
//        data.add(new CoachViewItem("fed", "34", null, null));
//        data.add(new CoachViewItem("Aewdfewbc", "34", null, null));
//        data.add(new CoachViewItem("rr", "34", null, null));
//        data.add(new CoachViewItem("nkk", "34", null, null));

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getLiveScoreUpdate(venueId, lane, false);
                handler.postDelayed(this, 10000);
            }
        };
        handler.post(runnable);
    }

    public void stopUpdating() {
        handler.removeCallbacksAndMessages(null);
    }

    public void getLiveScoreUpdate(final String venueId, final String lane, boolean showLoading) {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String to = df.format(c.getTime());
        String from = df.format(CommonUtil.minusTime(-90).getTime());

        if (!AppStatus.getInstance(ctx).isOnline(
                ctx)) {
            CommonUtil.noInternetDialog(ctx);
            return;
        }

        if (showLoading && !CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(ctx, "Loading...");
        }

        RequestParams rv = new RequestParams();
        rv.put("from", from);
        rv.put("to", to);
        rv.put("apiKey", Data.apiKey);
        rv.put("token", CommonUtil.getAccessToken(ctx));

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(Data.baseUrl + "venue/" + venueId + "/lane/" + lane, rv,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        Log.d("Refresh Live Score", "update Called");
                        try {
                            CommonUtil.loading_box_stop();
                            JSONArray jsonArry = new JSONArray(response);
                            JSONObject obj;
                            itemList.clear();

                            if (jsonArry.length() == 0) {
                                noItems.setVisibility(View.VISIBLE);
                            } else {
                                noItems.setVisibility(View.GONE);
                            }

                            for (int i = 0; i < jsonArry.length(); i++) {
                                obj = jsonArry.getJSONObject(i);
                                CoachViewItem item;
                                //= data.get(obj.getString("rowKey"));
                                //if (item == null) {
                                item = new CoachViewItem(java.net.URLDecoder.decode(obj.getString("name")),
                                        obj.getString("rowKey"));
                                //data.put(obj.getString("rowKey"), item);
                                itemList.add(item);
                                //}
                                for (int j = 1; j <= 21; j++) {
                                    int pins = obj.getInt("standingPins"
                                            + (j < 10 ? "0" : "") + j);
                                    if (pins == 0) {
                                        if (!obj.getString("squareScore" + j)
                                                .equalsIgnoreCase("X")
                                                && !obj.getString("squareScore" + j)
                                                .equalsIgnoreCase("/")) {
                                            pins = 1023;
                                        }
                                    }
                                    item.pinFall[j] = pins;
                                    item.squareScores[j] = obj.getString("squareScore" + j);
                                }

                                for (int j = 1; j <= 10; j++) {
                                    item.frameScore[j] = obj.getString("frameScore" + j);
                                }
                                item.score = obj.getString("finalScore");
                            }
                            notifyDataSetChanged();
//                                }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.loading_box_stop();
                    }
                }

        );

    }


}
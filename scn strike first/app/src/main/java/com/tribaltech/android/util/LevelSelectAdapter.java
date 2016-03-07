package com.tribaltech.android.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tribaltech.android.scnstrikefirst.R;

import rmn.androidscreenlibrary.ASSL;

public class LevelSelectAdapter extends BaseAdapter {

    public static final int CREDITS_INDEX = 1;
    public static final int POINTS_INDEX = 0;
    public int selectedIndex = -1;
    View previous;
    public String[][] data = new String[6][2];
    Context ctx;

    public LevelSelectAdapter(Context ctx) {
        this.ctx = ctx;
        data[0][POINTS_INDEX] = "1,000";
        data[0][CREDITS_INDEX] = "10";
        data[1][POINTS_INDEX] = "3,200";
        data[1][CREDITS_INDEX] = "25";
        data[2][POINTS_INDEX] = "6,500";
        data[2][CREDITS_INDEX] = "50";
        data[3][POINTS_INDEX] = "14,000";
        data[3][CREDITS_INDEX] = "100";
        data[4][POINTS_INDEX] = "75,000";
        data[4][CREDITS_INDEX] = "200";
        data[5][POINTS_INDEX] = "170,000";
        data[5][CREDITS_INDEX] = "1000";
    }

    private static class ViewHolder {
        TextView level;
        TextView creditWager;
        Button selectBowler;
        RelativeLayout rlt;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.level_listitem,
                    null);
            holder = new ViewHolder();
            holder.rlt = (RelativeLayout) convertView.findViewById(R.id.root);
            holder.level = (TextView) convertView
                    .findViewById(R.id.level);
            holder.creditWager = (TextView) convertView
                    .findViewById(R.id.creditBalance);
            holder.selectBowler = (Button) convertView.findViewById(R.id.selectBox);
            holder.rlt.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 110));
            holder.rlt.setTag(holder);
            ASSL.DoMagic(holder.rlt);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.level.setText("Level  " + (position + 1));
        holder.creditWager.setText("Win " + data[position][POINTS_INDEX] + " Points | " + data[position][CREDITS_INDEX] + " Credits");

        if (position == selectedIndex) {
            holder.selectBowler.setBackgroundResource(R.drawable.checked_box);
            holder.selectBowler.setTag("on");
        } else {
            holder.selectBowler.setBackgroundResource(R.drawable.box);
            holder.selectBowler.setTag("off");
        }

        holder.selectBowler.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!v.getTag().equals("on")) {
                    selectedIndex = position;
                    v.setBackgroundResource(R.drawable.checked_box);
                    v.setTag("on");
                    if (previous != null) {
                        previous.setBackgroundResource(R.drawable.box);
                        previous.setTag("off");
                    }
                    previous = v;
                } else {
                    previous = null;
                    selectedIndex = -1;
                    v.setBackgroundResource(R.drawable.box);
                    v.setTag("off");
                }
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
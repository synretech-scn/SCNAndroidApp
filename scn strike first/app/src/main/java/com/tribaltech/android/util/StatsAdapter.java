package com.tribaltech.android.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tribaltech.android.scnstrikefirst.R;

import java.util.List;

import rmn.androidscreenlibrary.ASSL;

public class StatsAdapter extends BaseAdapter {

    Context ctx;
    List<String[]> statsList;
    public int itemHeight;
    int layoutId;

    public StatsAdapter(Context ctx, List<String[]> list, int layoutId,
                        int itemHeight) {
        this.ctx = ctx;
        this.statsList = list;
        this.itemHeight = itemHeight;
        this.layoutId = layoutId;
    }

    public void setContestList(List<String[]> contestList) {
        this.statsList = contestList;
    }

    private static class ViewHolder {
        TextView statName;
        TextView statValue;
        RelativeLayout rlt;
    }

    @Override
    public int getCount() {
        return statsList.size();
    }

    @Override
    public String[] getItem(int position) {
        return statsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final String[] contest = statsList.get(position);
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutId, null);
            holder = new ViewHolder();
            holder.rlt = (RelativeLayout) convertView.findViewById(R.id.root);
            holder.statName = (TextView) convertView
                    .findViewById(R.id.statName);
            holder.statValue = (TextView) convertView
                    .findViewById(R.id.statValue);
            holder.rlt.setLayoutParams(new AbsListView.LayoutParams(
                    LayoutParams.MATCH_PARENT, itemHeight));
            holder.rlt.setTag(holder);
            ASSL.DoMagic(holder.rlt);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.statName.setText(contest[0]);
        if(contest.length > 1) {
            holder.statValue.setText(contest[1]);
        } else {
            holder.statValue.setText("");
        }
        return convertView;
    }

    public List<String[]> getData() {
        return statsList;
    }

}
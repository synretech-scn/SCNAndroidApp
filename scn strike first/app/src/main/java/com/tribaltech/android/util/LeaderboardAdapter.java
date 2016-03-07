package com.tribaltech.android.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tribaltech.android.scnstrikefirst.R;

import java.util.List;

import rmn.androidscreenlibrary.ASSL;

public class LeaderboardAdapter extends BaseAdapter {

    private Context _context;
    public List<String[]> data;

    public LeaderboardAdapter(Context context, List<String[]> data) {
        this._context = context;
        this.data = data;
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
        TextView value;
        TextView type;
        RelativeLayout rlt;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolderChild holder;
        final String[] childData = data.get(position);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.leaderboard_item, null);
            holder = new ViewHolderChild();
            holder.playerName = (TextView) convertView
                    .findViewById(R.id.playerName);
            holder.value = (TextView) convertView
                    .findViewById(R.id.value);
            holder.type = (TextView) convertView
                    .findViewById(R.id.type);
            holder.rlt = (RelativeLayout) convertView.findViewById(R.id.root);
            holder.rlt.setLayoutParams(new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT, 110));
            ASSL.DoMagic(holder.rlt);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolderChild) convertView.getTag();
        }
        holder.playerName.setText(childData[0]);
        holder.value.setText(childData[1]);
        holder.type.setText(childData[2]);
        return convertView;
    }

    public List<String[]> getData() {
        return data;
    }

    public void setData(List<String[]> data) {
        this.data = data;
    }
}
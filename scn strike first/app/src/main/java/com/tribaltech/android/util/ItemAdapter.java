package com.tribaltech.android.util;

/**
 * Created by cl-99 on 7/28/2015.
 */

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

import java.util.List;

import rmn.androidscreenlibrary.ASSL;

public class ItemAdapter extends BaseAdapter {

    Context context;

    public List<String[]> data;

    private LayoutInflater inflater;

    public String selectedPos = "";

    public int selectedIndex = -1;

    View previous;

    int maxValue;

    public ItemAdapter(List<String[]> data, Context c, int maxValue) {
        context = c;
        this.data = data;
        this.maxValue = maxValue;
        inflater = (LayoutInflater) c
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    @Override
    public String[] getItem(int arg0) {
        return data.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    static class ViewHolder {

        TextView bowlerName, regionName;
        RelativeLayout rlt;
        Button selectBowler;
        RelativeLayout disableLayer;
        int p;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup arg2) {

        final ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.bowler_listitem, null);
            holder = new ViewHolder();

            holder.rlt = (RelativeLayout) convertView.findViewById(R.id.root);

            holder.bowlerName = (TextView) convertView.findViewById(R.id.name);
            holder.regionName = (TextView) convertView
                    .findViewById(R.id.regionName);
            holder.selectBowler = (Button) convertView
                    .findViewById(R.id.selectBowler);
            holder.disableLayer = (RelativeLayout) convertView
                    .findViewById(R.id.disableLayer);
            holder.rlt.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 102));
            holder.rlt.setTag(holder);
            ASSL.DoMagic(holder.rlt);
            holder.p = position;
            convertView.setTag(holder);
            holder.selectBowler.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.p = position;

        if (Integer.parseInt(data.get(position)[1]) > maxValue) {
            holder.disableLayer.setVisibility(View.VISIBLE);
            holder.selectBowler.setEnabled(false);
        } else {
            holder.disableLayer.setVisibility(View.GONE);
            holder.selectBowler.setEnabled(true);
        }
        holder.bowlerName.setText(data.get(position)[0]);
        holder.regionName
                .setText(data.get(position)[1]);

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

                // notifyDataSetChanged();

            }
        });


        return convertView;
    }

    public void setData(List<String[]> data) {
        this.data = data;
    }
}

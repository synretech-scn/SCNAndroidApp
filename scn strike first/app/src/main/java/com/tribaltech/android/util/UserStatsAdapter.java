package com.tribaltech.android.util;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class UserStatsAdapter<T> extends ArrayAdapter<String[]> {

    List<String[]> countryList;

    public UserStatsAdapter(Context context, int textViewResourceId,
                            List<String[]> countryList) {

        super(context, textViewResourceId, countryList);
        // TextView tv=(TextView)findViewById(textViewResourceId);
        // tv.setTextSize(10);
        this.countryList = countryList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView textView = (TextView) view
                .findViewById(android.R.id.text1);
        textView.setText("");
        // textView.setText(countryList.get(position)[1]);
        return view;
    }

    public int getIndexFromId(String id) {
        for (int i = 0; i < countryList.size(); i++) {
            if (countryList.get(i)[0].equals(id)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView label = (TextView) view.findViewById(android.R.id.text1);
        label.setTextColor(Color.WHITE);
        label.setText(countryList.get(position)[1]);

        return label;
    }
}
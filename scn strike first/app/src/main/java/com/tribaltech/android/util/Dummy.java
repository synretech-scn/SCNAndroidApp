package com.tribaltech.android.util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by cl-99 on 1/21/2015.
 */
public class Dummy extends BaseAdapter {

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }
}

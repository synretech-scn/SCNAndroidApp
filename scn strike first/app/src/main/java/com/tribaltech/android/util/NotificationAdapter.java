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

public class NotificationAdapter extends BaseAdapter {

    private Context _context;
    private List<String[]> data;
    boolean notifications;

    public NotificationAdapter(Context context, List<String[]> data) {
        this._context = context;
        this.data = data;
        if (!data.isEmpty() && data.get(0).length == 3) {
            notifications = true;
        }
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
        TextView notification;
        RelativeLayout rlt;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolderChild holder;
        final String[] childData = data.get(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.notification_item, null);
            holder = new ViewHolderChild();
            holder.notification = (TextView) convertView
                    .findViewById(R.id.playerName);

            holder.rlt = (RelativeLayout) convertView.findViewById(R.id.root);
            holder.rlt.setLayoutParams(new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT, 110));
            ASSL.DoMagic(holder.rlt);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolderChild) convertView.getTag();
        }
        holder.notification.setText(childData[1]);
        //2 : Unread
        //1 : Read
        if (notifications) {
            if (childData[2].equalsIgnoreCase("2")) {
                holder.rlt.setBackgroundResource(R.drawable.grey);
            } else {
                holder.rlt.setBackgroundResource(R.drawable.item_large_background);
            }
        }
        return convertView;
    }


    public List<String[]> getData() {
        return data;
    }

    public void setData(List<String[]> data) {
        this.data = data;
    }
}
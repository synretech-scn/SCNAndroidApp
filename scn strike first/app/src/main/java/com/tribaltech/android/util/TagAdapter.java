package com.tribaltech.android.util;


import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tribaltech.android.scnstrikefirst.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rmn.androidscreenlibrary.ASSL;

public class TagAdapter extends BaseAdapter {

    public List<String> data;
    ViewHolder viewHolder = null;
    private LayoutInflater inflater = null;
    Context ctx;
    public int itemHeight;
    ListView tagList;
    Set<String> defaultTags = new HashSet<>();


    public TagAdapter(Context ctx, List<String> data, ListView tagList) {
        inflater = LayoutInflater.from(ctx);
        this.ctx = ctx;
        this.data = data;
        itemHeight = 110;
        this.tagList = tagList;
        defaultTags.add("H2H Posted");
        defaultTags.add("H2H Live");
        defaultTags.add("Posted Game");
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
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        if (view == null) {
            view = inflater.inflate(R.layout.tag_item, null);
            viewHolder = new ViewHolder();

            viewHolder.deleteTag = (Button) view.findViewById(R.id.deleteTag);
            viewHolder.name = (EditText) view.findViewById(R.id.name);
            viewHolder.rlt = (RelativeLayout) view
                    .findViewById(R.id.mainRL);
            viewHolder.rlt.setLayoutParams(new ListView.LayoutParams(
                    720, 110));
            ASSL.DoMagic(viewHolder.rlt);
            viewHolder.rlt.setTag(viewHolder);
            viewHolder.deleteTag.setTag(viewHolder);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.name.setText(data.get(position));
        viewHolder.deleteTag.setVisibility(defaultTags.contains(data.get(position)) ? View.GONE : View.VISIBLE);
        viewHolder.deleteTag.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                data.remove(position);
                notifyDataSetChanged();
                CommonUtil
                        .setListViewHeightBasedOnChildren(
                                tagList,
                                itemHeight);
            }
        });

        viewHolder.name.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String value = ((EditText) v).getText().toString();
                    if (value.isEmpty()) {
                        Toast.makeText(ctx, "Tag cannot be empty.", Toast.LENGTH_SHORT).show();
                    } else {
                        data.add(position, value);
                        notifyDataSetChanged();
                    }
                    return true;
                }
                return false;
            }
        });

        return view;
    }


    static class ViewHolder {
        int pos;
        RelativeLayout rlt;
        EditText name;
        Button deleteTag;
    }

    /**
     * This method is to get all circle we are in.
     */
    public void deleteTag(String tagId) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(Data.baseUrl + "Tags/DeleteTag?token="
                + CommonUtil.getAccessToken(ctx)
                .replaceAll("[+]", "%2B") + "&apiKey="
                + Data.apiKey + "&TagId=" + tagId, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
            }

            @Override
            public void onFailure(Throwable throwable, String s) {
                super.onFailure(throwable, s);
            }
        });
    }
}
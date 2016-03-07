package com.tribaltech.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tribaltech.android.scnstrikefirst.LiveScore;
import com.tribaltech.android.scnstrikefirst.R;
import com.tribaltech.android.scnstrikefirst.UserStats;
import com.tribaltech.android.scnstrikefirst.webView;

import java.util.ArrayList;

import rmn.androidscreenlibrary.ASSL;

public class ViewPagerAdapter extends PagerAdapter {
    // Declare Variables
    Context context;
    ArrayList<Object> left;
    // ArrayList<String> right;
    ArrayList<String> url;
    ArrayList<String> names;
    ArrayList<Boolean> isUrl;
    LayoutInflater inflater;
    int count;

    public ViewPagerAdapter(Context context, ArrayList<Object> left,
                            ArrayList<Object> right, ArrayList<String> url,
                            ArrayList<String> names, ArrayList<Boolean> isUrl) {
        this.context = context;
        left.add(0, left.get(left.size() - 1));
        left.add(left.get(1));

        url.add(0, url.get(url.size() - 1));
        url.add(url.get(1));

        isUrl.add(0, isUrl.get(isUrl.size() - 1));
        isUrl.add(isUrl.get(1));

        names.add(0, names.get(names.size() - 1));
        names.add(names.get(1));
        count = left.size();
        this.left = left;
        // this.right = right;
        this.names = names;
        this.url = url;
        this.isUrl = isUrl;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {

        // Declare Variables
        ImageView bowlnow;

        RelativeLayout root;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.viewpager_item, container,
                false);

        // Locate the ImageView in viewpager_item.xml
        bowlnow = (ImageView) itemView.findViewById(R.id.bowlnow);

        root = (RelativeLayout) itemView.findViewById(R.id.root);

        root.setLayoutParams(new AbsListView.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        ASSL.DoMagic(root);

//        bowlnow.setTag(url.get(position));
        // Capture position and set to the ImageView

        if (left.get(position) instanceof String) {
            bowlnow.setImageBitmap(CommonUtil.decodeBase64((String) left.get(position)));
            bowlnow.setTag(R.id.banner_url, url.get(position));
            bowlnow.setTag(R.id.web_redirect, isUrl.get(position));
        } else {
            bowlnow.setImageResource((int) left.get(position));
            bowlnow.setTag(R.id.banner_url, "http://www.xbowling.com/");
            bowlnow.setTag(R.id.web_redirect, true);
        }

        bowlnow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Boolean) v.getTag(R.id.web_redirect)) {
                    Intent intent = new Intent(context, webView.class);
                    intent.putExtra("url", v.getTag(R.id.banner_url).toString());
                    context.startActivity(intent);
                } else {
                    switch (AppSection.fromString(v.getTag(R.id.banner_url)
                            .toString())) {
                        case LIVE_SCORE:
                            Intent intent = new Intent(context,
                                    LiveScore.class);
                            context.startActivity(intent);
                            ((Activity) context).finish();
                            break;
                        case USER_STATS:
                            intent = new Intent(context, UserStats.class);
                            context.startActivity(intent);
                            ((Activity) context).finish();
                            break;
                    }
                }
            }
        });

        // Add viewpager_item.xml to ViewPager
        ((ViewPager) container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((RelativeLayout) object);

    }


}

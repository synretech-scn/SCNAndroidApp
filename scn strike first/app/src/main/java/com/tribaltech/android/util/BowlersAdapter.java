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

import com.tribaltech.android.entities.Xbowler;
import com.tribaltech.android.scnstrikefirst.R;

import java.util.ArrayList;

import rmn.androidscreenlibrary.ASSL;

public class BowlersAdapter extends BaseAdapter {

	Context context;

	public ArrayList<Xbowler> Xbowlerdetail;

	private LayoutInflater inflater;

	public String selectedPos = "";

	public int selectedIndex = -1;

	View previous;

	public BowlersAdapter(ArrayList<Xbowler> livelanes, Context c) {
		context = c;
		this.Xbowlerdetail = livelanes;

		inflater = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return Xbowlerdetail.size();
	}

	@Override
	public Object getItem(int arg0) {
		return Xbowlerdetail.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	static class ViewHolder {

		TextView bowlerName, regionName;
		RelativeLayout rlt;
		Button selectBowler;
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

		holder.bowlerName.setText(Xbowlerdetail.get(position).screenName);

		if (Xbowlerdetail.get(position).regionLongName.length() <= 0) {
			holder.regionName.setText("---");
		} else {
			holder.regionName
					.setText(Xbowlerdetail.get(position).regionLongName + ", "
							+ Xbowlerdetail.get(position).countryCode);
		}

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

}

package com.tribaltech.android.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tribaltech.android.scnstrikefirst.R;

import java.util.List;

import rmn.androidscreenlibrary.ASSL;

public class CompareAdapter extends BaseAdapter {

	Context ctx;
	List<String[]> statsList;
	public int itemHeight;
	int layoutId;

	public CompareAdapter(Context ctx, List<String[]> list) {
		this.ctx = ctx;
		this.statsList = list;
	}

	public void setContestList(List<String[]> contestList) {
		this.statsList = contestList;
	}

	private static class ViewHolder {
		TextView statName;
		TextView userValue;
		TextView otherValue;
		LinearLayout rlt;
	}

	@Override
	public int getCount() {
		return statsList.size();
	}

	@Override
	public Object getItem(int position) {
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
			convertView = inflater.inflate(R.layout.comparison_item, null);
			holder = new ViewHolder();
			holder.rlt = (LinearLayout) convertView.findViewById(R.id.root);
			holder.statName = (TextView) convertView
					.findViewById(R.id.statName);
			holder.userValue = (TextView) convertView
					.findViewById(R.id.yourScore);
			holder.otherValue = (TextView) convertView
					.findViewById(R.id.oppScore);
			holder.rlt.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, 188));
			holder.rlt.setTag(holder);
			ASSL.DoMagic(holder.rlt);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.statName.setText(contest[0]);
		holder.userValue.setText(contest[1]);
		holder.otherValue.setText(contest[2]);
		return convertView;
	}

	public List<String[]> getContestList() {
		return statsList;
	}

}
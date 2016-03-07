package com.tribaltech.android.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.tribaltech.android.entities.CompetitionView;

import java.util.List;

public class GameAdapter extends BaseAdapter {

	List<CompetitionView> competitions;
	Context ctx;
    public int selectedIndex = -1;
    View previous;

	public GameAdapter(Context ctx, List<CompetitionView> competitions) {
		this.ctx = ctx;
		this.competitions = competitions;
	}

	@Override
	public int getCount() {
		return competitions.size();
	}

	@Override
	public Object getItem(int position) {
		return competitions.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}

	public List<CompetitionView> getCompetitions() {
		return competitions;
	}

	public void setCompetitions(List<CompetitionView> competitions) {
		this.competitions = competitions;
	}
	//
	// public Context getCtx() {
	// return ctx;
	// }
	//
	// public void setCtx(Context ctx) {
	// this.ctx = ctx;
	// }

}

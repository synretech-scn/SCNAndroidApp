package com.tribaltech.android.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tribaltech.android.entities.CompetitionView;
import com.tribaltech.android.scnstrikefirst.R;

import java.util.List;

import rmn.androidscreenlibrary.ASSL;

public class PostedGameAdapter extends GameAdapter {

	public PostedGameAdapter(Context ctx, List<CompetitionView> competitions) {
		super(ctx, competitions);
	}

	private static class ViewHolder {

		TextView competitionName;
		TextView bowlerName;
		TextView handicapScore;
		TextView expirationTime;
        Button selectBowler;
		RelativeLayout rlt;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(
					R.layout.posted_competition_listitem, null);
			holder = new ViewHolder();
			holder.rlt = (RelativeLayout) convertView.findViewById(R.id.root);
			holder.competitionName = (TextView) convertView
					.findViewById(R.id.competitionName);
			holder.bowlerName = (TextView) convertView
					.findViewById(R.id.bowlerName);
			holder.handicapScore = (TextView) convertView
					.findViewById(R.id.handicapScore);
			holder.expirationTime = (TextView) convertView
					.findViewById(R.id.expirationTime);
            holder.selectBowler = (Button) convertView
                    .findViewById(R.id.selectBox);
			holder.rlt.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 110));
			holder.rlt.setTag(holder);
			ASSL.DoMagic(holder.rlt);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
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

                notifyDataSetChanged();

            }
        });

		holder.competitionName
				.setText(competitions.get(position).competitionName);
		holder.bowlerName.setText(competitions.get(position).creatorUserName);
		holder.handicapScore.setText(competitions.get(position).creatorHandicap
				+ "");
		holder.expirationTime
				.setText(competitions.get(position).expirationTime);
		return convertView;
	}

	// public List<CompetitionView> getCompetitions() {
	// return competitions;
	// }
	//
	// public void setCompetitions(List<CompetitionView> competitions) {
	// this.competitions = competitions;
	// }

}

package com.tribaltech.android.scnstrikefirst;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;
import com.tribaltech.android.util.FilterItem;
import com.tribaltech.android.util.StatsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rmn.androidscreenlibrary.ASSL;

public class StatsFragment extends Fragment implements OnClickListener,UserStats.Filterable {

	ListView statsList;
	StatsAdapter statsAdapter;
	TextView errorMsg;
    LinearLayout scoresParent;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = (View) inflater.inflate(R.layout.stats_fragment, container,
				false);
        new ASSL(getActivity(), (ViewGroup) view.findViewById(R.id.root), 1134,
                720, true);
        scoresParent = (LinearLayout)view.findViewById(R.id.scoreParent);
		statsList = (ListView) view.findViewById(R.id.statsList);
		errorMsg = (TextView) view.findViewById(R.id.errorMsg);
		statsAdapter = new StatsAdapter(getActivity(),
				new ArrayList<String[]>(), R.layout.stat_item, 116);
		statsList.setAdapter(statsAdapter);
		statsIndividual(CommonUtil.getFilter(getActivity()));
		return view;
	}

	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.filter: {
			Intent intent = new Intent(getActivity(), FilterActivity.class);
			startActivityForResult(intent, 1);
			break;
		}

		default:
			break;
		}
	}

	private void statsIndividual(FilterItem filterItem) {
		errorMsg.setVisibility(View.GONE);
		CommonUtil.loading_box(getActivity(), "Please wait...");

		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(CommonUtil.TIMEOUT);
		client.addHeader("Content-type", "application/json");
		client.addHeader("Accept", "application/json");

		client.get(
				Data.baseUrl
						+ "UserStat/BowlingGameUserStatViewListIndividual?token="
						+ CommonUtil.getAccessToken(getActivity()).replaceAll(
								"[+]", "%2B") + "&apiKey=" + Data.apiKey
						+ CommonUtil.getFilterString(filterItem),

				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(String response) {
						CommonUtil.loading_box_stop();

						JSONArray array;
						try {
							array = new JSONArray(response);
							JSONObject res;
							List<String[]> datalist = new ArrayList<String[]>();
							statsAdapter
									.setContestList(new ArrayList<String[]>());
							if (array.isNull(0)) {
								statsAdapter.notifyDataSetChanged();
								errorMsg.setVisibility(View.VISIBLE);
                                scoresParent.setVisibility(View.GONE);
								return;
							}
                            scoresParent.setVisibility(View.VISIBLE);
                            statsList.setVisibility(View.VISIBLE);
							res = array.getJSONObject(0);
                            ((TextView)getView().findViewById(R.id.totalGames)).setText(NumberFormat.getNumberInstance(Locale.US)
                                    .format(res.getInt("games")).toString());
//							datalist.add(new String[] { "Games",
//									"" + res.getInt("games") });
                            ((TextView)getView().findViewById(R.id.avgScore)).setText(NumberFormat.getNumberInstance(Locale.US)
                                    .format(res.getInt("averageScores")).toString());
                            ((TextView)getView().findViewById(R.id.totalScore)).setText(NumberFormat.getNumberInstance(Locale.US)
                                    .format(res.getInt("totalScore")).toString());

//                            datalist.add(new String[] { "Average Score",
//									"" + res.getInt("averageScores") });
							datalist.add(new String[] {
									"Open",
									"" + NumberFormat.getNumberInstance(Locale.US)
                                            .format(res.getInt("opens")) + "/"
											+ NumberFormat.getNumberInstance(Locale.US)
                                            .format(res.getInt("openChances")).toString() });
							datalist.add(new String[] { "Open Percentage",
									"" + res.getDouble("openpercent") + "%" });
							datalist.add(new String[] {
									"Strike",
									"" + NumberFormat.getNumberInstance(Locale.US)
                                            .format(res.getInt("strike")).toString() + "/"
											+ NumberFormat.getNumberInstance(Locale.US)
                                            .format(res.getInt("strikeChances")).toString() });
							datalist.add(new String[] { "Strike Percentage",
									res.getDouble("strikepercent") + "%" + "" });
							// datalist.add(new String[] { "Spares",
							// "" + res.getInt("spares") });
							// datalist.add(new String[] { "Spare Percentage",
							// "" + res.getInt("sparepercent") + "%" });
							datalist.add(new String[] {
									"Single-Pin Spare",
									"" + NumberFormat.getNumberInstance(Locale.US)
                                            .format(res.getInt("singlePin")).toString() + "/"
											+ NumberFormat.getNumberInstance(Locale.US)
                                            .format(res.getInt("singlePinChances")).toString() });
							datalist.add(new String[] {
									"Single-Pin Spare Percentage",
									"" + res.getDouble("singlePinpercent")
											+ "%" });
							datalist.add(new String[] {
									"Multi-Pin Spare",
									"" + NumberFormat.getNumberInstance(Locale.US)
                                            .format(res.getInt("multiPin")).toString() + "/"
											+ NumberFormat.getNumberInstance(Locale.US)
                                            .format(res.getInt("multiPinChances")).toString() });
							datalist.add(new String[] {
									"Multi-Pin Spare Percentage",
									"" + res.getDouble("multiPinpercent") + "%" });
							datalist.add(new String[] {
									"Split",
									"" + NumberFormat.getNumberInstance(Locale.US)
                                            .format(res.getInt("split")).toString() + "/"
											+ NumberFormat.getNumberInstance(Locale.US)
                                            .format(res.getInt("splitChances")).toString() });
							datalist.add(new String[] { "Split Percentage",
									"" + res.getDouble("splitpercent") + "%" });

							datalist.add(new String[] {
									"Games Less than 150",
									""
											+ res.getDouble("percentageOfgameLessthan150")
											+ "%" });
							datalist.add(new String[] {
									"Games between 151-175",
									""
											+ res.getDouble("percentageOfgameBet151To175")
											+ "%" });
							datalist.add(new String[] {
									"Games between 176-200",
									""
											+ res.getDouble("percentageOfgameBet176To200")
											+ "%" });
							datalist.add(new String[] {
									"Games between 201-225",
									""
											+ res.getDouble("percentageOfgameBet201To225")
											+ "%" });
							datalist.add(new String[] {
									"Games between 226-250",
									""
											+ res.getDouble("percentageOfgameBet226To250")
											+ "%" });
							datalist.add(new String[] {
									"Games between 251-299",
									""
											+ res.getDouble("percentageOfgameBet251To299")
											+ "%" });
							datalist.add(new String[] {
									"Perfect Score Percentage",
									""
											+ res.getDouble("percentageOfPerfectScore")
											+ "%" });
							datalist.add(new String[] {
									"Filled Frame Percentage",
									"" + res.getDouble("filledFrame") + "%" });
							datalist.add(new String[] {
									"Pocket Percentage",
									"" + res.getDouble("noOfPocketpercent")
											+ "%" });
							datalist.add(new String[] { "Carry Percentage",
									"" + res.getDouble("carry") + "%" });
							statsAdapter.setContestList(datalist);
							statsAdapter.notifyDataSetChanged();

						} catch (JSONException e1) {
							e1.printStackTrace();
						}

					}

					@Override
					public void onFailure(Throwable arg0) {
						Log.e("request fail", arg0.toString());
                        CommonUtil.commonErrorDialog(getActivity(), "There was a problem connecting to the server.Please try again.");
                        CommonUtil.loading_box_stop();
					}
				});

	}

    @Override
    public void filter(FilterItem filterItem) {
        statsIndividual(filterItem);
    }
}

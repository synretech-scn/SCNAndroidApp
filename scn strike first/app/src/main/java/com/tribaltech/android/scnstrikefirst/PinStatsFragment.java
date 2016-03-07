package com.tribaltech.android.scnstrikefirst;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tribaltech.android.util.PinView;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;
import com.tribaltech.android.util.FilterItem;
import com.tribaltech.android.util.StatsAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rmn.androidscreenlibrary.ASSL;

public class PinStatsFragment extends Fragment implements OnClickListener,UserStats.Filterable {

	boolean pinView = true;
	ListView pinSpareList;
	ListView multiSpareList;
	ListView splitList;
	StatsAdapter pinSpareAdapter;
	StatsAdapter multiSpareAdapter;
	StatsAdapter splitsAdapter;

	RelativeLayout pinViewSinglePin;
	RelativeLayout pinViewMultiPin;
	RelativeLayout pinViewSplits;

	RelativeLayout pinSpareHeader;
	RelativeLayout multiPinSpareHeader;
	RelativeLayout splitsHeader;
	LinearLayout tabHeader;
	public static final int FILTER_CODE = 1;
	TextView errorMsg;

	ScrollView statsScrollView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = (View) inflater.inflate(R.layout.pin_stats_fragment,
				container, false);
        new ASSL(getActivity(), (ViewGroup) view.findViewById(R.id.root), 1134,
                720, true);
		statsScrollView = (ScrollView) view.findViewById(R.id.statsScrollView);
		pinSpareList = (ListView) view.findViewById(R.id.pinSpareList);
		multiSpareList = (ListView) view.findViewById(R.id.multiPinSpareList);
		splitList = (ListView) view.findViewById(R.id.splitsList);

		errorMsg = (TextView) view.findViewById(R.id.errorMsg);
		tabHeader = (LinearLayout) view.findViewById(R.id.toggleLay);
		pinViewSinglePin = (RelativeLayout) view
				.findViewById(R.id.singlePinSpareParent);
		pinViewMultiPin = (RelativeLayout) view
				.findViewById(R.id.multiPinSpareParent);
		pinViewSplits = (RelativeLayout) view
				.findViewById(R.id.splitsPinParent);

		pinSpareHeader = (RelativeLayout) view
				.findViewById(R.id.pinSpareHeader);
		multiPinSpareHeader = (RelativeLayout) view
				.findViewById(R.id.multiPinSpareHeader);
		splitsHeader = (RelativeLayout) view.findViewById(R.id.splitsHeader);
		pinSpareHeader.setOnClickListener(this);
		multiPinSpareHeader.setOnClickListener(this);
		splitsHeader.setOnClickListener(this);
		((Button) view.findViewById(R.id.pinView)).setOnClickListener(this);
		((Button) view.findViewById(R.id.listView)).setOnClickListener(this);

		pinSpareAdapter = new StatsAdapter(getActivity(),
				new ArrayList<String[]>(), R.layout.pin_spare_item, 100);
		pinSpareList.setAdapter(pinSpareAdapter);

		multiSpareAdapter = new StatsAdapter(getActivity(),
				new ArrayList<String[]>(), R.layout.pin_spare_item, 100);
		multiSpareList.setAdapter(multiSpareAdapter);

		splitsAdapter = new StatsAdapter(getActivity(),
				new ArrayList<String[]>(), R.layout.pin_spare_item, 100);
		splitList.setAdapter(splitsAdapter);

		pinDetail(view, CommonUtil.getFilter(getActivity()));
		return view;
	}

	private final void focusOnView(final View view) {
		view.getTop();
		view.getBottom();
		statsScrollView.getScrollY();
		float scroll = view.getScrollY();
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				statsScrollView.smoothScrollTo(0, view.getScrollY());
			}
		});
	}

	boolean isOpened(RelativeLayout view) {
		return view.getTag().toString().equalsIgnoreCase("open");
	}

	// private void fillPins(View view) {
	// Random rand = new Random();
	// for (int i = 1; i <= 10; i++) {
	// PinView pinView = (PinView) view.findViewById(CommonUtil
	// .getIdFromName("pinView" + i, R.id.class));
	// pinView.updateProgress(CommonUtil.getRandNumber(rand, 20, 80));
	// }
	// }

	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.pinSpareHeader: {
			boolean open = toggleHeaders(v);
			(pinView ? pinViewSinglePin : pinSpareList)
					.setVisibility(open ? View.GONE : View.VISIBLE);
			if (!open) {
				// statsScrollView.scrollTo(0, 75);
				// focusOnView(v);
			}
			break;
		}

		case R.id.multiPinSpareHeader: {
			boolean open = toggleHeaders(v);
			(pinView ? pinViewMultiPin : multiSpareList)
					.setVisibility(open ? View.GONE : View.VISIBLE);
			if (!open) {
				// focusOnView(v);
				int scroll = 75;
				if (isOpened(pinSpareHeader)) {
					scroll += (pinSpareAdapter.getCount() * pinSpareAdapter.itemHeight);
				}
				statsScrollView.scrollTo(0, scroll);
			}
			break;
		}

		case R.id.splitsHeader: {
			boolean open = toggleHeaders(v);
			(pinView ? pinViewSplits : splitList)
					.setVisibility(open ? View.GONE : View.VISIBLE);
			if (!open) {
				int scroll = 150;
				if (isOpened(pinSpareHeader)) {
					scroll += (pinSpareAdapter.getCount() * pinSpareAdapter.itemHeight);
				}
				if (isOpened(multiPinSpareHeader)) {
					scroll += (multiSpareAdapter.getCount() * multiSpareAdapter.itemHeight);
				}

				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						statsScrollView.scrollTo(
								0,
								(int) (statsScrollView.getScrollY() + 50
										* ASSL.Xscale()));
					}
				}, 100);

			}
			break;
		}

		case R.id.pinView: {
			pinView = true;
            getView().findViewById(R.id.pinView).setBackgroundResource(R.drawable.blue_left_semi_round);
            getView().findViewById(R.id.listView).setBackgroundResource(R.drawable.blue_right_outline_semi_round);
            pinSpareHeader.setVisibility(View.GONE);
			pinSpareList.setVisibility(View.GONE);
			getView().findViewById(R.id.multiSpare).setVisibility(View.GONE);
			getView().findViewById(R.id.splits).setVisibility(View.GONE);
			pinViewSinglePin.setVisibility(View.VISIBLE);
			break;
		}

		case R.id.listView: {
			pinView = false;
            getView().findViewById(R.id.pinView).setBackgroundResource(R.drawable.blue_left_outline_semi_round);
            getView().findViewById(R.id.listView).setBackgroundResource(R.drawable.blue_right_semi_round);
            pinViewSinglePin.setVisibility(View.GONE);
			pinSpareHeader.setVisibility(View.VISIBLE);
			getView().findViewById(R.id.multiSpare).setVisibility(View.VISIBLE);
			getView().findViewById(R.id.splits).setVisibility(View.VISIBLE);
			Boolean open = pinSpareHeader.getTag().toString()
					.equalsIgnoreCase("open");
			pinViewSinglePin.setVisibility(View.GONE);
			if (open) {
				pinSpareList.setVisibility(View.VISIBLE);
			}

			open = multiPinSpareHeader.getTag().toString()
					.equalsIgnoreCase("open");
			if (open) {
				// pinViewMultiPin.setVisibility(View.GONE);
				multiSpareList.setVisibility(View.VISIBLE);
			}

			open = splitsHeader.getTag().toString().equalsIgnoreCase("open");
			if (open) {
				// pinViewSplits.setVisibility(View.GONE);
				splitList.setVisibility(View.VISIBLE);
			}
			break;
		}

		case R.id.filter: {
			Intent intent = new Intent(getActivity(), FilterActivity.class);
			startActivityForResult(intent, FILTER_CODE);
			break;
		}
		}
	}

	private boolean toggleHeaders(View v) {
		Boolean open = v.getTag().toString().equalsIgnoreCase("open");
//		((TextView) (((ViewGroup) v).getChildAt(2))).setText("Tap to "
//				+ (open ? "Show" : "Hide") + " Status");
		((TextView) (((ViewGroup) v).getChildAt(1)))
				.setBackgroundResource(open ? R.drawable.plus_selector
						: R.drawable.minus_selector);
		v.setTag(open ? "closed" : "open");
		return open;
	}

	private void pinDetail(final View view, FilterItem filterItem) {

		CommonUtil.loading_box(getActivity(), "Please wait...");

		errorMsg.setVisibility(View.GONE);
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(CommonUtil.TIMEOUT);

		client.get(
				Data.baseUrl
						+ "UserStat/BowlingGameUserPinDetail?token="
						+ CommonUtil.getAccessToken(getActivity()).replaceAll(
								"[+]", "%2B") + "&apiKey=" + Data.apiKey
						+ CommonUtil.getFilterString(filterItem),
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(String response) {
						CommonUtil.loading_box_stop();

						try {

							JSONArray array = new JSONArray(response);

							if (array.isNull(0)) {

								view.findViewById(R.id.pinsLayout)
										.setVisibility(View.GONE);
								errorMsg.setVisibility(View.VISIBLE);

								pinSpareAdapter.getData().clear();
								pinSpareAdapter.notifyDataSetChanged();
								multiSpareAdapter.getData().clear();
								multiSpareAdapter.notifyDataSetChanged();
								splitsAdapter.getData().clear();
								splitsAdapter.notifyDataSetChanged();

								pinSpareList.post(new Runnable() {

									@Override
									public void run() {
										CommonUtil
												.setListViewHeightBasedOnChildren(
														pinSpareList,
														pinSpareAdapter.itemHeight);
										pinSpareList.setFocusable(false);
									}
								});

								multiSpareList.post(new Runnable() {

									@Override
									public void run() {
										CommonUtil
												.setListViewHeightBasedOnChildren(
														multiSpareList,
														multiSpareAdapter.itemHeight);
										multiSpareList.setFocusable(false);
									}
								});

								splitList.post(new Runnable() {

									@Override
									public void run() {
										CommonUtil
												.setListViewHeightBasedOnChildren(
														splitList,
														splitsAdapter.itemHeight);
										splitList.setFocusable(false);
									}
								});
								return;
							}
							if (pinView) {
								view.findViewById(R.id.pinsLayout)
										.setVisibility(View.VISIBLE);
							}
							JSONObject json = array.getJSONObject(0);
							List<String[]> singleData = new ArrayList<String[]>();
							for (int i = 1; i <= 10; i++) {
								String ratio = json.getString("singlePincount"
										+ i)
										+ "/"
										+ json.getString("singlePinChancecount"
												+ i);
								singleData.add(new String[] {
										i + "-pin",
										"Converted: " + ratio,
										json.getDouble("singlePinPercentage"
												+ i)
												+ "" });
								PinView pinView = (PinView) view
										.findViewById(CommonUtil.getIdFromName(
												"pinView" + i, R.id.class));
								pinView.updateProgress((int) json
										.getDouble("singlePinPercentage" + i));

								TextView pinText = (TextView) view
										.findViewById(CommonUtil.getIdFromName(
												"pinText" + i, R.id.class));
								pinText.setText(singleData.get(singleData.size() - 1)[2]
                                        + "%"
										+ "\n"
										+ ratio);
							}

							List<String[]> multiPinData = new ArrayList<String[]>();
							List<String[]> splitData = new ArrayList<String[]>();
							JSONArray multiJson = json
									.getJSONArray("checkMultipinList");
							for (int i = 0; i < multiJson.length(); i++) {

								if (multiJson.getJSONObject(i).getString(
										"multiPinText") != null
										&& !multiJson.getJSONObject(i)
												.getString("multiPinText")
												.equals("null")) {
									multiPinData
											.add(new String[] {
													multiJson
															.getJSONObject(i)
															.getString(
																	"multiPinText")
															.replaceAll(",",
																	"-"),
													"Converted: "
															+ multiJson
																	.getJSONObject(
																			i)
																	.getString(
																			"multiPin")
															+ "/"
															+ multiJson
																	.getJSONObject(
																			i)
																	.getString(
																			"multiPinChances") });
								}
							}

							multiJson = json.getJSONArray("checkSplitList");
							for (int i = 0; i < multiJson.length(); i++) {
								if (multiJson.getJSONObject(i).getString(
										"splitText") != null
										&& !multiJson.getJSONObject(i)
												.getString("splitText")
												.equals("null")) {
									splitData
											.add(new String[] {
													multiJson
															.getJSONObject(i)
															.getString(
																	"splitText")
															.replaceAll(",",
																	"-"),
													"Converted: "
															+ multiJson
																	.getJSONObject(
																			i)
																	.getString(
																			"split")
															+ "/"
															+ multiJson
																	.getJSONObject(
																			i)
																	.getString(
																			"splitChances") });
								}
							}

							pinSpareAdapter.setContestList(singleData);
							pinSpareAdapter.notifyDataSetChanged();
							multiSpareAdapter.setContestList(multiPinData);
							multiSpareAdapter.notifyDataSetChanged();
							splitsAdapter.setContestList(splitData);
							splitsAdapter.notifyDataSetChanged();

							pinSpareList.post(new Runnable() {

								@Override
								public void run() {
									CommonUtil
											.setListViewHeightBasedOnChildren(
													pinSpareList,
													pinSpareAdapter.itemHeight);
									pinSpareList.setFocusable(false);
								}
							});

							multiSpareList.post(new Runnable() {

								@Override
								public void run() {
									CommonUtil
											.setListViewHeightBasedOnChildren(
													multiSpareList,
													multiSpareAdapter.itemHeight);
									multiSpareList.setFocusable(false);
								}
							});

							splitList.post(new Runnable() {

								@Override
								public void run() {
									CommonUtil
											.setListViewHeightBasedOnChildren(
													splitList,
													splitsAdapter.itemHeight);
									splitList.setFocusable(false);
								}
							});

						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(Throwable e) {
						Log.v("exception ", e.toString() + "");
						CommonUtil.commonGameErrorDialog(getActivity(),
								"An error occured.Please try again.");
						CommonUtil.loading_box_stop();

					}
				});
	}

    @Override
    public void filter(FilterItem filterItem) {
        pinDetail(getView(),filterItem);
    }
}

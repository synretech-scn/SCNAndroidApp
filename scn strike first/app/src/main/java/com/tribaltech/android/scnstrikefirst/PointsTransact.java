package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;
import com.tribaltech.android.util.ItemAdapter;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import rmn.androidscreenlibrary.ASSL;


public class PointsTransact extends Fragment implements View.OnClickListener {

    ListView itemsList;
    ItemAdapter adapter;
    String venueId;
    WalletTransaction activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.activity_points_transact, container,
                false);
        new ASSL(getActivity(), (ViewGroup) view.findViewById(R.id.root), 1134,
                720, false);
        itemsList = (ListView) view.findViewById(R.id.itemsList);
        adapter = new ItemAdapter(new ArrayList<String[]>(), activity,
                getArguments().getInt("availablePoints"));
        itemsList.setAdapter(adapter);
        view.findViewById(R.id.submit).setOnClickListener(this);
        getItems(activity.venueId);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (WalletTransaction) activity;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit:
                if (adapter.selectedIndex == -1) {
                    CommonUtil.commonDialog(activity, "Error", "Please select an item.");
                } else {
                    activity.confirmPoints(adapter.getItem(adapter.selectedIndex)[2],
                            adapter.getItem(adapter.selectedIndex)[1]);
                }
                break;
        }
    }

    private void getItems(String venueId) {
        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(activity, "Please wait...");
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(
                Data.baseUrl
                        + "businessbuildervault/" + (activity.caseAdd ? "" : "redemption/")
                        + venueId + "?token=" + CommonUtil.getAccessToken(activity).replaceAll(
                        "[+]", "%2B") + "&apiKey=" + Data.apiKey, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        try {
                            JSONArray array = new JSONArray(response);
                            if (array.length() == 0) {
                                getView().findViewById(R.id.noItems).setVisibility(View.VISIBLE);
                                getView().findViewById(R.id.submit).setVisibility(View.GONE);
                                itemsList.setVisibility(View.GONE);
                            } else {
                                getView().findViewById(R.id.noItems).setVisibility(View.GONE);
                                getView().findViewById(R.id.submit).setVisibility(View.VISIBLE);
                                itemsList.setVisibility(View.VISIBLE);
                            }
                            List<String[]> dataList = new ArrayList<String[]>();
                            for (int i = 0; i < array.length(); i++) {
                                String[] data = new String[3];
                                data[0] = array.getJSONObject(i).getString("itemDescription");
                                data[1] = array.getJSONObject(i).getString("itemPoint");
                                data[2] = array.getJSONObject(i).getString("itemId");
                                dataList.add(data);
                            }
                            adapter.setData(dataList);
                            adapter.notifyDataSetChanged();
//                            itemsList.getChildAt(0).setEnabled(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        super.onFailure(throwable);
                        CommonUtil.loading_box_stop();
                    }
                });
    }
}

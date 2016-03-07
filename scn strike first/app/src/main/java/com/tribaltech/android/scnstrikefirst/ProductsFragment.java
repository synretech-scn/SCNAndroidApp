package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;
import com.tribaltech.android.util.StatsAdapter;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import rmn.androidscreenlibrary.ASSL;

/**
 * Created by cl-99 on 8/3/2015.
 */
public class ProductsFragment extends Fragment {

    StatsAdapter adapter;
    ListView itemsList;
    WalletTransaction activity;
    String category;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.products_list, container,
                false);
        new ASSL(getActivity(), (ViewGroup) view.findViewById(R.id.root), 1134,
                720, false);
        itemsList = (ListView) view.findViewById(R.id.listView);
        adapter = new StatsAdapter(getActivity(), new ArrayList<String[]>(), R.layout.points_item, 102);
        category = getArguments().getString("header");
        itemsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                activity.productDetails(adapter.getItem(position)[0], adapter.getItem(position)[3],
                        adapter.getItem(position)[4], adapter.getItem(position)[2],adapter.getItem(position)[1]);
            }
        });
        itemsList.setAdapter(adapter);
        if (activity.productsCache.get(category) != null) {
            adapter.setContestList(activity.productsCache.get(category));
            adapter.notifyDataSetChanged();
        } else {
            String query = category.replaceAll(" ", "+");
            if (query.startsWith("Browse")) {
                query = "";
            }
            getProducts(query);
        }
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (WalletTransaction) activity;
    }

    private void getProducts(String query) {
        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(getActivity(), "Please wait...");
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(
                Data.baseUrl
                        + "redemptionproduct?token="
                        + CommonUtil.getAccessToken(getActivity()).replaceAll(
                        "[+]", "%2B") + "&apiKey=" + Data.apiKey + "&category=" + query, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        try {
                            CommonUtil.loading_box_stop();
                            JSONArray array = new JSONArray(response);
                            if (array.length() == 0) {
                                getView().findViewById(R.id.noItems).setVisibility(View.VISIBLE);
                                itemsList.setVisibility(View.GONE);
                            } else {
                                getView().findViewById(R.id.noItems).setVisibility(View.GONE);
                                itemsList.setVisibility(View.VISIBLE);
                            }
                            List<String[]> dataList = new ArrayList<String[]>();
                            for (int i = 0; i < array.length(); i++) {
                                String[] data = new String[5];
                                data[0] = array.getJSONObject(i).getString("name");
                                data[1] = array.getJSONObject(i).getString("costPoints");
                                data[2] = array.getJSONObject(i).getString("id");
                                data[3] = array.getJSONObject(i).getString("description");
                                data[4] = array.getJSONObject(i).getString("imageUrl");
                                dataList.add(data);
                            }
                            activity.productsCache.put(category, dataList);
                            adapter.setContestList(dataList);
                            adapter.notifyDataSetChanged();
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

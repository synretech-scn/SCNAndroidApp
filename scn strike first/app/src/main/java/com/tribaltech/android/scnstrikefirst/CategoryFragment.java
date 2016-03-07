package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tribaltech.android.util.StatsAdapter;

import java.util.ArrayList;
import java.util.List;

import rmn.androidscreenlibrary.ASSL;

/**
 * Created by cl-99 on 8/3/2015.
 */
public class CategoryFragment extends Fragment {

    ListView listView;
    StatsAdapter adapter;
    WalletTransaction activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.products_list, container,
                false);
        new ASSL(getActivity(), (ViewGroup) view.findViewById(R.id.root), 1134,
                720, false);
        listView = (ListView) view.findViewById(R.id.listView);
        List<String[]> data = new ArrayList<>();
        data.add(new String[]{"Browse All"});
        data.add(new String[]{"Sporting Goods"});
        data.add(new String[]{"Games"});
        data.add(new String[]{"Bowling Balls"});
        data.add(new String[]{"Bowling Pins"});
        data.add(new String[]{"Bowling Bags"});
        data.add(new String[]{"Toys"});
        data.add(new String[]{"Clothing"});
        data.add(new String[]{"Electronics"});
        data.add(new String[]{"Watches"});
        data.add(new String[]{"Home Goods"});
        adapter = new StatsAdapter(getActivity(),
                data, R.layout.points_item, 102);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                activity.productsList(adapter.getItem(position)[0]);
            }
        });
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (WalletTransaction)activity;
    }
}

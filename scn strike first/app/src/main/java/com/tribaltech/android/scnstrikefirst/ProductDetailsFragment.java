package com.tribaltech.android.scnstrikefirst;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import rmn.androidscreenlibrary.ASSL;

/**
 * Created by cl-99 on 8/3/2015.
 */
public class ProductDetailsFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.product_details, container,
                false);
        new ASSL(getActivity(), (ViewGroup) view.findViewById(R.id.root), 1134,
                720, false);
        String desc = getArguments().getString("desc");
        Picasso.with(getActivity())
                .load(getArguments().getString("image"))
                .into((ImageView) view.findViewById(R.id.image));
        ((TextView) view.findViewById(R.id.desc)).setText(getArguments().getString("desc"));
        view.findViewById(R.id.submit).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        ((WalletTransaction) getActivity()).shippingAddress(getArguments().getString("id"),
                getArguments().getString("rewardPoints"));
    }
}

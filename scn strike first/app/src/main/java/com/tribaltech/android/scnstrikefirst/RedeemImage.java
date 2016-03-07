package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;

import org.json.JSONException;
import org.json.JSONObject;

import rmn.androidscreenlibrary.ASSL;


public class RedeemImage extends Fragment implements View.OnClickListener {

    ImageView imageView;
    WalletTransaction activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.activity_redeem_image, container,
                false);
        new ASSL(getActivity(), (ViewGroup) view.findViewById(R.id.root), 1134,
                720, false);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        view.findViewById(R.id.close).setOnClickListener(this);
        getImage();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (WalletTransaction) activity;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close:
//                activity.addRedeemPoints(activity.passcode);
                CommonUtil.commonDialog(getActivity(), "Success", "Points Redeemed",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                activity.finish();
                            }
                        });
                break;
        }
    }

    public void getImage() {
        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(activity, "Please wait...");
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(
                Data.baseUrl
                        + "bizvltimg/" + activity.venueId + "/" + activity.itemId + "?token="
                        + CommonUtil.getAccessToken(activity).replaceAll(
                        "[+]", "%2B") + "&apiKey=" + Data.apiKey, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);

                            Picasso.with(getActivity())
                                    .load(obj.getString("itemImage"))
                                    .into(imageView, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            CommonUtil.loading_box_stop();
                                        }

                                        @Override
                                        public void onError() {
                                            CommonUtil.loading_box_stop();
                                        }
                                    });
                        } catch (JSONException e) {
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

package com.tribaltech.android.scnstrikefirst;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tribaltech.android.entities.Center;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import rmn.androidscreenlibrary.ASSL;


public class AddCenterWallet extends MenuIntent {

    CenterFragment centerFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_center_wallet);
        new ASSL(this, (ViewGroup) findViewById(R.id.root), 1134,
                720, false);
        centerFragment = new CenterFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("restrict", true);
        centerFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.centerfragmentParent, centerFragment).commit();
        centerFragment.load();
    }

    public void onClick(View view) {
        Center center = centerFragment.getSelectedCenter();
        if (Wallet.venues.contains(center.id)) {
            CommonUtil.commonDialog(this, "Error", "Center already added");
        } else {
            addCenter(center.id);
        }
    }

    public void addCenter(int venueId) {
        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(this, "Please wait...");
        }
        JSONObject obj = null;
        StringEntity entity = null;
        try {
            obj = new JSONObject();
            obj.put("Points", 0);
            obj.put("Notes", "");
            obj.put("IsRedeemable", "");
            obj.put("VenueId", venueId);
            obj.put("BusinessBuilderItemID", 0);
            entity = new StringEntity(obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (obj == null) return;

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.post(
                getApplicationContext(),
                Data.baseUrl
                        + "venue/userpoint/0?token="
                        + CommonUtil.getAccessToken(getApplicationContext())
                        .replaceAll("[+]", "%2B") + "&apiKey="
                        + Data.apiKey, entity, "application/json",
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        CommonUtil.commonDialog(AddCenterWallet.this, "Success", "Center Added", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.commonGameErrorDialog(AddCenterWallet.this,
                                e.getMessage() + "");
                        CommonUtil.loading_box_stop();

                    }
                });
    }
}

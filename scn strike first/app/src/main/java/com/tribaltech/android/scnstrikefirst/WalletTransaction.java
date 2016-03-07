package com.tribaltech.android.scnstrikefirst;

import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;

import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rmn.androidscreenlibrary.ASSL;


public class WalletTransaction extends FragmentActivity {

    String venueId;
    boolean caseAdd;
    String itemId;
    String points;
    String passcode;
    TextView headerText;
    TextView titleText;
    TextView titlebarText;
    Map<String, List<String[]>> productsCache = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_transaction);
        new ASSL(this, (ViewGroup) findViewById(R.id.root), 1134,
                720, false);
        headerText = (TextView) findViewById(R.id.headerText);

        titleText = (TextView) findViewById(R.id.titleText);
        titlebarText = (TextView) findViewById(R.id.titlebarText);

        getSupportFragmentManager().addOnBackStackChangedListener(listener);
        if (getIntent().hasExtra("prizes")) {
            CategoryFragment cf = new CategoryFragment();
            Bundle bundle = new Bundle();
            bundle.putString("header", "Select Category");
            cf.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, cf).
                    addToBackStack(null).commit();
        } else {
            venueId = getIntent().getStringExtra("venueId");
            caseAdd = getIntent().hasExtra("caseAdd");
            PointsTransact pt = new PointsTransact();
            Bundle bundle = new Bundle();
            bundle.putString("header", caseAdd ? "Add Reward Points" : "Player's Bank");
            //add

            bundle.putString("title", caseAdd ? "Select an activity to add Reward Points" : "Select an item to buy with reward points");
           // bundle.putString("titlebar", caseAdd ? "   " : "Current Player Bank Points :  "+getIntent().getStringExtra("availablePoints")+getIntent().getStringExtra("venueId")  );
            bundle.putString("titlebar", caseAdd ? "   " : "Current Player Bank Points :  "+getIntent().getStringExtra("availablePoints")  );


            bundle.putInt("availablePoints", Integer.parseInt(getIntent().getStringExtra("availablePoints")));
            pt.setArguments(bundle);
            getSupportFragmentManager().addOnBackStackChangedListener(listener);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, pt).
                    addToBackStack(null).commit();
        }
    }

    FragmentManager.OnBackStackChangedListener listener = new FragmentManager.OnBackStackChangedListener() {
        public void onBackStackChanged() {
            FragmentManager manager = getSupportFragmentManager();
            if (manager != null) {
               headerText.setText(manager.findFragmentById(R.id.fragmentContainer)
                        .getArguments().getString("header"));

                //add
                titleText.setText(manager.findFragmentById(R.id.fragmentContainer)
                        .getArguments().getString("title"));

                titlebarText.setText(manager.findFragmentById(R.id.fragmentContainer)
                        .getArguments().getString("titlebar"));
               // titleText.setVisibility(View.GONE);

            }
        }
    };

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                onBackPressed();
                break;
        }
    }

    public void confirmPoints(String itemId, String points) {
        this.itemId = itemId;
        this.points = points;

        ConfirmPoints cp = new ConfirmPoints();
        Bundle bundle = new Bundle();
        bundle.putString("header", "Enter Code");
        cp.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, cp).
                addToBackStack(null).commit();
    }

    public void redeemImage(String passcode) {
        this.passcode = passcode;
        RedeemImage ri = new RedeemImage();
        Bundle bundle = new Bundle();
        bundle.putString("header", "Redemption Image");
        ri.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, ri).
                addToBackStack(null).commit();
    }

    public void productsList(String category) {
        ProductsFragment ri = new ProductsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("header", category);
        ri.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, ri).
                addToBackStack(null).commit();
    }

    public void productDetails(String productName, String desc, String imageUrl, String id,String rewardPts) {
        ProductDetailsFragment ri = new ProductDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("header", productName);
        bundle.putString("desc", desc);
        bundle.putString("image", imageUrl);
        bundle.putString("id", id);
        bundle.putString("rewardPoints",rewardPts);

        ri.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, ri).
                addToBackStack(null).commit();
    }

    public void shippingAddress(String id,String rewardPoints) {
        AddressFragment ri = new AddressFragment();
        Bundle bundle = new Bundle();
        bundle.putString("header", "Shipping Information");
        bundle.putString("id", id);
        bundle.putInt("rewardPoints", Integer.parseInt(rewardPoints));
        ri.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, ri).
                addToBackStack(null).commit();
    }


    public void addRedeemPoints(final String passcode,final boolean flag) {
        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(this, "Please wait...");
        }
        JSONObject obj = null;
        StringEntity entity = null;
        try {
            obj = new JSONObject();
            obj.put("Points", (caseAdd ? "" : "-") + points);
            obj.put("Notes", "Redeem");
            obj.put("IsRedeemable", "true");
            obj.put("VenueId", venueId);
            obj.put("BusinessBuilderItemID", itemId);
            entity = new StringEntity(obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (obj == null) return;

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.post(
                this,
                Data.baseUrl
                        + "venue/userpoint/" + passcode + "?token="
                        + CommonUtil.getAccessToken(this)
                        .replaceAll("[+]", "%2B") + "&apiKey="
                        + Data.apiKey, entity, "application/json",
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        if(flag) {
                            CommonUtil.commonDialog(WalletTransaction.this, "Success", "Points " + (caseAdd ? "Added" : "Redeemed"),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            finish();
                                        }
                                    });
                        } else {
                            redeemImage(passcode);
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.loading_box_stop();
                        if (e instanceof HttpResponseException && ((HttpResponseException) e).getStatusCode() == 403) {
                            CommonUtil.commonGameErrorDialog(WalletTransaction.this,
                                    "Incorrect Passphrase");
                        } else {
                            CommonUtil.commonGameErrorDialog(WalletTransaction.this,
                                    e.getMessage() + "");
                        }

                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    public void redeemPrize(String jsonObj) {
        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(this, "Please wait...");
        }
        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonObj);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.post(
                this,
                Data.baseUrl
                        + "redemptionrequest?token="
                        + CommonUtil.getAccessToken(this)
                        .replaceAll("[+]", "%2B") + "&apiKey="
                        + Data.apiKey, entity, "application/json",
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        CommonUtil.commonDialog(WalletTransaction.this, "Success", "Order Confirmed",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.loading_box_stop();
                        CommonUtil.commonGameErrorDialog(WalletTransaction.this,
                                e.getMessage() + "");
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        productsCache = null;
    }
}

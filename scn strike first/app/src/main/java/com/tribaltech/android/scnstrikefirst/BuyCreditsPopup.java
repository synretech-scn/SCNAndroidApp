package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.trivialdrivesample.util.IabHelper;
import com.example.android.trivialdrivesample.util.IabResult;
import com.example.android.trivialdrivesample.util.Inventory;
import com.example.android.trivialdrivesample.util.Purchase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tribaltech.android.entities.Credits;
import com.tribaltech.android.util.AppStatus;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;

import org.apache.http.client.HttpResponseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import rmn.androidscreenlibrary.ASSL;

//import android.util.Log;

public class BuyCreditsPopup extends MenuIntent {

    static final int RC_REQUEST = 10001;
    private static IabHelper mHelper;
    protected String TAG;
    Activity context;
    private ListView creditList;
    private liveLanesAdapter liveAdapter;
    ArrayList<Credits> creditsList;
    int credits = 0;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        context = this;
        // Toast.makeText(c, "called", 5000).show();
        buyCreditPopup();


        // String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhQIBOmcYy2+ggKG7e73sI8MhcxbtfzPYesr6IijrzApBwkUnIhxAbMQJLS2bhr25fCRWlB533yvDUYN+b3iNCLNRLMtoJinUQaTLWm/2KJPbv12MJVGqnwadksvNt1aDIytdWIHyu5Ir0YeiTG4uLSErs6p2fnhgiaBubCL+op1cFSL39cKkMmVMxWqQS5sRDM8BoEQrcZunIpLmCFMoLiqAsGRIjHl0iSZdxSVvLNYe1svVOcY3Yk/sf7XqO5f00MLulOYR+4FGhrW7NWmqZ/bYDiLDgjua3uPxczAdEJBB6VpNtRfNI41TRrHDAsy3LiJ23z/pMdIifJ4QaycJtwIDAQAB";
//modi
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAn5bwSI5GKY2jIqrfzZxrxnXMycEnKRfRmQEltkpDz4Nzp8ZDL09UOx8YDtUZp0+KRZ3V1ltVHxp2+Ti2abIbEE0qtXsDcS3qb97F5IavbldZOT+FcPKa+xOptR+6nvQTZ3KKY4NegODEQGYzJyyubHTCF+774ABG3Pg0l9/U2Ul111C/Dndxm1u82oFNcTcheoVlWKNXipk/jlwqgszmh3kssxPyh2bG5DzNNJqGcHC9a8H1sO1BlkmJJL6CMuD4D/AyauXfmft7+4fQy777w5jbHMf3YLAUnPHLAXQe6O7zeFC72IdEYnb+TzARmDlZ100i5hIYlwumypf1utn6NwIDAQAB";


        // Some sanity checks to see if the developer (that's you!) really
        // followed the
        // instructions to run this sample (don't put these checks on your app!)
        if (base64EncodedPublicKey.contains("CONSTRUCT_YOUR")) {
            throw new RuntimeException(
                    "Please put your app's public key in MainActivity.java. See README.");
        }
        if (context.getPackageName().startsWith(
                "com.example.android.trivialdrivesample.util")) {
            throw new RuntimeException(
                    "Please change the sample's package name! See README.");
        }

        // Create the helper, passing it our context and the public key to
        // verify signatures with
        mHelper = new IabHelper(context, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set
        // this to false).
        mHelper.enableDebugLogging(false);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {

            public void onIabSetupFinished(IabResult result) {

                if (!result.isSuccess()) {

                    Toast.makeText(context, "in app billing not possible", Toast.LENGTH_SHORT)
                            .show();
                    // Oh noes, there was a problem.

                    // complain("Problem setting up in-app billing. Billing Service unavailable on this device. ");
                    return;
                }

                // Hooray, IAB is fully set up. Now, let's get an inventory of
                // stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });

    }

    // Listener that's called when we finish querying the items and
    // subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {
            Log.d("TAG", "Query inventory finished.");
            if (result.isFailure()) {
                // complain("Failed: " + result);
                return;
            } else {
                // System.out.println();

                // generateNoteOnSD("purchasedItems.txt", inventory.toString());
                // Log.d("TAG", "Query inventory was successful.");

				/*
                 * Check for items we own. Notice that for each purchase, we
				 * check the developer payload to see if it's correct! See
				 * verifyDeveloperPayload().
				 */

            }

        }
    };


    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: "
                    + purchase);

            if (result.isFailure()) {
                // complain("Error purchasing: " + result);
                Toast.makeText(context, "Payment canceled", Toast.LENGTH_LONG)
                        .show();
                setWaitScreen(false);
                // hit("1","2");
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                setWaitScreen(false);

                return;
            }

            Log.d(TAG, "Purchase successful.");

            // if (purchase.getSku().equals(Tokens_3000)) {

            // Data1.nooftoken_PURCHASED = 1000;
            // Data1.tickets = Data1.tickets + 1000;
            // String first = "You have ";
            // String sec = (Data1.tickets - Data1.bet) + "";
            // String next = "<font color='#ea1552'>" + sec + "</font>"
            // + " tokens";
            // coins.setText(Html.fromHtml(first + next));

            Log.v("getSignature", purchase.getSignature());

            Log.v("getOrderId", purchase.getOrderId());

            mHelper.consumeAsync(purchase, mConsumeFinishedListener);

            // hit("1","2");

            // updateSub();
            // new UpdateTickets().execute();
            // STORING SHARE && ticket update VALUE ###########
            // final Editor edit = prefs.edit();
            // edit.putInt("nooftickets", Data1.tickets);
            // edit.commit();

            // updateSub();
            // updateUi();

            // STORING SHARE VALUE #############

            // SuccessAlertDialog(Token.this);
            // }

            // else if (purchase.getSku().equals(Tokens_6000)) {

            // Data.loading_box(Payment.this, "Loading...");
            // mHelper.consumeAsync(purchase, mConsumeFinishedListener);

            // }
            Log.v("hello purchase.getSku()", purchase.getSku() + ",,");
            Log.v("hello getOriginalJson()", purchase.getOriginalJson() + ",,");
            Log.v("hello purchase.getSignature()", purchase.getSignature()
                    + ",,");

            Log.v("Purchase to string", purchase.toString() + ",,");
            try {
                updatCredits(purchase.getSku(), purchase.getOriginalJson(),
                        purchase.getSignature());

            } catch (Exception e) {
                Log.v("error in updating server", e.toString());
            }
        }
    };

    public void toggle(View v) {
        toggle();
    }

    public void generateNoteOnSD(String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(),
                    "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            // Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    protected void updateUi() {
        // TODO Auto-generated method stub
        Toast.makeText(context, "Payment Done!", Toast.LENGTH_SHORT).show();
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(context);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    /**
     * Verifies the developer payload of a purchase.
     */
    boolean verifyDeveloperPayload(Purchase p) {
        // String payload = p.getDeveloperPayload();

		/*
         * TODO: verify that the developer payload of the purchase is correct.
		 * It will be the same one that you sent when initiating the purchase.
		 * 
		 * WARNING: Locally generating a random string when starting a purchase
		 * and verifying it here might seem like a good approach, but this will
		 * fail in the case where the user purchases an item on one device and
		 * then uses your app on a different device, because on the other device
		 * you will not have access to the random string you originally
		 * generated.
		 * 
		 * So a good developer payload has these characteristics:
		 * 
		 * 1. If two different users purchase an item, the payload is different
		 * between them, so that one user's purchase can't be replayed to
		 * another user.
		 * 
		 * 2. The payload must be such that you can verify it even when the app
		 * wasn't the one who initiated the purchase flow (so that items
		 * purchased by the user on one device work on other devices owned by
		 * the user).
		 * 
		 * Using your own server to store and verify developer payloads across
		 * app installations is recommended.
		 */

        return true;
    }

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase
                    + ", result: " + result);

            // We know this is the "gas" sku because it's the only one we
            // consume,
            // so we don't check which sku was consumed. If you have more than
            // one
            // sku, you probably should check...
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in
                // our
                // game world's logic, which in our case means filling the gas
                // tank a bit
                Log.d(TAG, "Consumption successful. Provisioning.");

                // updateSub();
                // Toast.makeText(getApplicationContext(), ""+monthval,
                // 2000).show();
                saveData();

                // alert("You filled 1/4 tank. Your tank is now " +
                // String.valueOf(mTank) + "/4 full!");
            } else {

                complain("Error while consuming: " + result);
            }
            // updateUi();
            // setWaitScreen(false);
            Log.d(TAG, "End consumption flow.");
        }
    };

    // Enables or disables the "please wait" screen.
    void setWaitScreen(boolean set) {
        // findViewById(R.id.screen_main).setVisibility(set ? View.GONE :
        // View.VISIBLE);
        // findViewById(R.id.screen_wait).setVisibility(set ? View.VISIBLE :
        // View.GONE);
    }

    // @Override
    // protected void onActivityResult(int requestCode, int resultCode, Intent
    // data) {
    // Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + ","
    // + data);
    //
    // // Pass on the activity result to the helper for handling
    // if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
    // // not handled, so handle it ourselves (here's where you'd
    // // perform any handling of activity results not related to in-app
    // // billing...
    // super.onActivityResult(requestCode, resultCode, data);
    // // Session.getActiveSession().onActivityResult(this, requestCode,
    // // resultCode, data);
    // } else {
    // Log.d(TAG, "onActivityResult handled by IABUtil.");
    // }
    // }

    public static Boolean onActivitCustomy(int requestCode, int resultCode,
                                           Intent data) {
        try {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            Log.v("responseCode", responseCode + "");
            Log.v("purchaseData", purchaseData + "");
            Log.v("dataSignature", dataSignature + "");

            return !mHelper.handleActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            return true;
        }

    }

    void saveData() {

		/*
         * WARNING: on a real application, we recommend you save data in a
		 * secure way to prevent tampering. For simplicity in this sample, we
		 * simply store the data using a SharedPreferences.
		 */

        // SharedPreferences.Editor spe = getPreferences(MODE_PRIVATE).edit();
        // spe.putInt("tank", mTank);
        // spe.commit();
        // Log.d(TAG, "Saved data: tank = " + String.valueOf(mTank));
    }

    public void buyCreditPopup() {

        setContentView(R.layout.buy_credits_popup);
        new ASSL(this, (ViewGroup) findViewById(R.id.root), 1134,
                720, false);

        creditList = (ListView) findViewById(R.id.listView);
        getCredits();
    }

    private void getUserCredit() {

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.get(Data.baseUrl
                        + "userprofile/wallet?token="
                        + CommonUtil.getAccessToken(getApplicationContext())
                        .replaceAll("[+]", "%2B") + "&apiKey=" + Data.apiKey,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            ((TextView) findViewById(R.id.currentCredits)).setText("Total Credits : " + json.getString("credits"));
                            Data.credits = json.getInt("credits");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.commonGameErrorDialog(BuyCreditsPopup.this,
                                "An error occured. Please try again.");
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserCredit();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    public class liveLanesAdapter extends BaseAdapter {

        ViewHolder holder;
        ArrayList<Credits> creditslist = new ArrayList<Credits>();

        LayoutInflater inflater;

        public liveLanesAdapter(ArrayList<Credits> creditslist, Context c) {

            this.creditslist = creditslist;

            inflater = (LayoutInflater) c
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return creditslist.size();
        }

        @Override
        public Object getItem(int arg0) {
            return creditslist.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        class ViewHolder {

            RelativeLayout root;
            TextView discount;
            TextView credits;
            TextView price;
            int p;
        }

        @Override
        public View getView(int arg0, View convertView, ViewGroup arg2) {

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.credits_listitem, null);
                holder = new ViewHolder();
                // if (arg1 == null)

                holder.root = (RelativeLayout) convertView
                        .findViewById(R.id.root);
                holder.discount = (TextView) convertView.findViewById(R.id.discount);
                holder.credits = (TextView) convertView.findViewById(R.id.creditsCount);
                holder.price = (TextView) convertView.findViewById(R.id.price);

                holder.root.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        165));
                holder.root.setTag(holder);
                ASSL.DoMagic(holder.root);
                holder.p = arg0;
                convertView.setTag(holder);

                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder = (ViewHolder) v.getTag();
                        // Toast.makeText(context,
                        // " id = " + creditsList.get(holder.p).productId,
                        // 5000).show();
                        try {
                            // mHelper.launchPurchaseFlow(context,
                            // creditsList.get(holder.p).productId,
                            // RC_REQUEST, mPurchaseFinishedListener, "");

                            credits = Integer.parseInt(creditsList
                                    .get(holder.p).credits);

                            // mHelper.launchPurchaseFlow(context,
                            // "android.test.purchased",
                            // RC_REQUEST, mPurchaseFinishedListener, "");

                            mHelper.launchPurchaseFlow(context,
                                    creditsList.get(holder.p).productId,
                                    RC_REQUEST, mPurchaseFinishedListener, "");
                        } catch (IllegalStateException ex) {
                            Toast.makeText(context,
                                    "Please retry in a few seconds.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.p = arg0;
            holder.discount.setText("");
            holder.credits.setText(creditsList.get(arg0).credits + " Credits");
            holder.price
                    .setText("$ " +
                            +(float) (Integer.parseInt(creditsList.get(arg0).priceUsPennies))
                                    / 100);

            int k = Integer.parseInt(creditsList.get(arg0).credits)
                    - Integer.parseInt(creditsList.get(arg0).baseCredits);
            if (k > 0) {
                k = (k * 100)
                        / Integer.parseInt(creditsList.get(arg0).baseCredits);
                holder.discount.setText(creditsList.get(arg0).baseCredits + " + "
                        + k + "% Free ");
                holder.discount.setVisibility(View.VISIBLE);
            } else {
                holder.discount.setVisibility(View.GONE);
            }

            return convertView;
        }

    }

    private void getCredits() {
        if (!AppStatus.getInstance(context).isOnline(context)) {
            CommonUtil.noInternetDialog(context);
            return;
        }
        CommonUtil.loading_box(context, "Loading...");
        RequestParams rv = new RequestParams();

        rv.put("apiKey", Data.apiKey);
        rv.put("token", CommonUtil.getAccessToken(context));

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(Data.baseUrl + "creditpackage/Venue?venueId=15103&", rv,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        Log.v(" livescore response = ", response);
                        CommonUtil.loading_box_stop();
                        try {
                            creditsList = new ArrayList<Credits>();
                            creditsList.clear();
                            JSONArray js = new JSONArray(response);
                            for (int i = 0; i < js.length(); i++) {

                                if (!js.getJSONObject(i)
                                        .getBoolean("isDeleted")) {
                                    creditsList.add(new Credits(
                                            js.getJSONObject(i).getString(
                                                    "name"),
                                            js.getJSONObject(i).getString(
                                                    "priceUsPennies"), js
                                            .getJSONObject(i)
                                            .getString("credits"), js
                                            .getJSONObject(i)
                                            .getString("baseCredits"),
                                            js.getJSONObject(i).getString(
                                                    "productId"), js
                                            .getJSONObject(i)
                                            .getString("id")));
                                }
                            }

                            creditList.setDivider(null);
                            creditList.setDividerHeight(-10);
                            liveAdapter = new liveLanesAdapter(creditsList,
                                    context);
                            creditList.setAdapter(liveAdapter);
                        } catch (Exception e) {
                            Log.v("hello exception", e.toString());
                        }

                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.v("exception ", e.toString() + "");
                        CommonUtil.loading_box_stop();
                        CommonUtil
                                .commonErrorDialog(context,
                                        "An error occurred while loading IAPs. Please try later");
                    }
                });

    }

    private void updatCredits(String productId, String signedData,
                              String signature) {

        if (!AppStatus.getInstance(context).isOnline(context)) {
            CommonUtil.noInternetDialog(context);
            return;
        }

        CommonUtil.loading_box(context, "Updating...");
        RequestParams rv = new RequestParams();

        rv.put("apiKey", Data.apiKey);
        rv.put("token", CommonUtil.getAccessToken(context));
        rv.put("productId", productId);
        rv.put("signedData", signedData);
        rv.put("signature", signature);

        generateNoteOnSD("productId.txt", productId);
        generateNoteOnSD("signedData.txt", signedData);
        generateNoteOnSD("signature.txt", signature);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.post(Data.baseUrl + "creditpackagepurchase/android", rv,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        Log.v(" livescore response = ", response);

                        CommonUtil.loading_box_stop();

                        Data.credits = Data.credits + credits;

                        CommonUtil.commonDialog(context, "Congratulations!",
                                "Credits updated sucessfully.");

                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.v("exception ", e.toString() + "");

                        HttpResponseException hre = (HttpResponseException) e;
                        CommonUtil.loading_box_stop();
                        CommonUtil.commonGameErrorDialog(
                                context,
                                "An error occurred. Please try later :"
                                        + hre.getStatusCode());
                    }
                });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, ScreenMain.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (!mHelper.handleActivityResult(requestCode,
                resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

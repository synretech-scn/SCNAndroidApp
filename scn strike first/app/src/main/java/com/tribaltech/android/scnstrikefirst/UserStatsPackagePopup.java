package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.trivialdrivesample.util.IabHelper;
import com.example.android.trivialdrivesample.util.IabResult;
import com.example.android.trivialdrivesample.util.Inventory;
import com.example.android.trivialdrivesample.util.Purchase;
import com.example.android.trivialdrivesample.util.SkuDetails;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tribaltech.android.util.AppConstants;
import com.tribaltech.android.util.AppStatus;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;

import org.apache.http.client.HttpResponseException;
import org.json.JSONArray;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;

import rmn.androidscreenlibrary.ASSL;

public class UserStatsPackagePopup extends Activity {

    static final int RC_REQUEST = 10001;
    private static IabHelper mHelper;
    protected String TAG;
    Activity context;
    int credits = 0;
    String planChoosen = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);
        context = this;
        new ASSL(UserStatsPackagePopup.this, (ViewGroup) findViewById(R.id.root),
                AppConstants.SCREEN_HEIGHT, AppConstants.SCREEN_WIDTH,
                false);

   //   String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhQIBOmcYy2+ggKG7e73sI8MhcxbtfzPYesr6IijrzApBwkUnIhxAbMQJLS2bhr25fCRWlB533yvDUYN+b3iNCLNRLMtoJinUQaTLWm/2KJPbv12MJVGqnwadksvNt1aDIytdWIHyu5Ir0YeiTG4uLSErs6p2fnhgiaBubCL+op1cFSL39cKkMmVMxWqQS5sRDM8BoEQrcZunIpLmCFMoLiqAsGRIjHl0iSZdxSVvLNYe1svVOcY3Yk/sf7XqO5f00MLulOYR+4FGhrW7NWmqZ/bYDiLDgjua3uPxczAdEJBB6VpNtRfNI41TRrHDAsy3LiJ23z/pMdIifJ4QaycJtwIDAQAB";
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

        Button done = (Button) findViewById(R.id.subscribe);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (planChoosen.equals("monthly")) {
                    mHelper.launchSubscriptionPurchaseFlow(context,
                            "network.sportschallenge.scnstrikefirst.xbpsmonthlyfixed", RC_REQUEST,
                            mPurchaseFinishedListener, "");
                } else if (planChoosen.equals("yearly")) {
                    mHelper.launchSubscriptionPurchaseFlow(context,
                            "network.sportschallenge.scnstrikefirst.xbpsannualfixed", RC_REQUEST,
                            mPurchaseFinishedListener, "");
                } else {
                    Toast.makeText(context,
                            "Please select a subscription plan",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        final Button monthly = (Button) findViewById(R.id.monthly);
        monthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (monthly.getTag().equals("off")) {
                    monthly.setBackgroundResource(R.drawable.checked_box);
                    monthly.setTag("on");
                    if (previous != null) {
                        previous.setTag("off");
                        previous.setBackgroundResource(R.drawable.box);

                    }
                    previous = (Button) v;
                    planChoosen = "monthly";
                } else {
                    monthly.setBackgroundResource(R.drawable.box);
                    monthly.setTag("off");
                    previous = null;
                    planChoosen = "";
                }
            }
        });

        final Button yearly = (Button) findViewById(R.id.yearly);
        yearly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yearly.getTag().equals("off")) {
                    yearly.setBackgroundResource(R.drawable.checked_box);
                    yearly.setTag("on");
                    if (previous != null) {
                        previous.setTag("off");
                        previous.setBackgroundResource(R.drawable.box);

                    }
                    previous = (Button) v;
                    planChoosen = "yearly";
                } else {
                    yearly.setBackgroundResource(R.drawable.box);
                    yearly.setTag("off");
                    previous = null;
                    planChoosen = "";
                }
            }
        });

    }

    // Listener that's called when we finish querying the items and
    // subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {
            if (result.isFailure()) {
                // complain("Failed: " + result);
                return;
            } else {
                StringBuilder purchased = new StringBuilder("Purchased\n\n");
                for (Entry<String, Purchase> entry : inventory
                        .getmPurchaseMap().entrySet()) {
                    purchased.append(entry.getKey() + " : " + entry.getValue()
                            + "\n");
                }

                purchased.append("\n\nAll Sku\n\n");
                for (Entry<String, SkuDetails> entry : inventory.getmSkuMap()
                        .entrySet()) {
                    purchased.append(entry.getKey() + " : " + entry.getValue()
                            + "\n");
                }
                generateNoteOnSD("sku.txt", purchased.toString());
                // Log.d("TAG", "Query inventory was successful.");

				/*
                 * Check for items we own. Notice that for each purchase, we
				 * check the developer payload to see if it's correct! See
				 * verifyDeveloperPayload().
				 */

            }

        }
    };

    // UNMANAGED CALLLL
    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

            if (result.isFailure()) {
                // complain("Error purchasing: " + result);
                generateNoteOnSD("failure.txt", "failure");
                setWaitScreen(false);
                // hit("1","2");
                return;
            }

            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                setWaitScreen(false);

                return;
            }


            // if (purchase.getSku().equals(Tokens_3000)) {

            // Data1.nooftoken_PURCHASED = 1000;
            // Data1.tickets = Data1.tickets + 1000;
            // String first = "You have ";
            // String sec = (Data1.tickets - Data1.bet) + "";
            // String next = "<font color='#ea1552'>" + sec + "</font>"
            // + " tokens";
            // coins.setText(Html.fromHtml(first + next));


            Log.v("getOrderId", purchase.getOrderId());

            //	mHelper.consumeAsync(purchase, mConsumeFinishedListener);

            Log.v("hello purchase.getSku()", purchase.getSku() + ",,");
            Log.v("hello getOriginalJson()", purchase.getOriginalJson() + ",,");
            Log.v("hello purchase.getSignature()", purchase.getSignature()
                    + ",,");

            Log.v("Purchase to string", purchase.toString() + ",,");


            generateNoteOnSD("productId.txt", purchase.getSku());
            generateNoteOnSD("signedData.txt", purchase.getOriginalJson());
            generateNoteOnSD("signature.txt", purchase.getSignature());
            updateCredits(purchase.getSku(), purchase.getOriginalJson(),
                    purchase.getSignature());


        }
    };

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    public void generateNoteOnSD(String sFileName, String sBody) {
        if (true) {
            return;
        }
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
    private Button yearly;
    private Button monthly;
    protected Button previous;
    private Button free;

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

        userPlanList();
    }


    private void goToUserStats() {
        Intent i = new Intent(context, UserStats.class);
        context.startActivity(i);
        context.finish();
    }

    private void updateCredits(final String productId, final String signedData,
                               final String signature) {

        // if (true)
        // return;
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

        client.post(Data.baseUrl + "Userstatsubscription/android", rv,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();

                        Toast.makeText(context,
                                "Congratulations! Pack purchased.",
                                Toast.LENGTH_SHORT).show();
                        Data.userStatsSubscribed = true;
                        Intent i = new Intent(context, UserStats.class);
                        context.startActivity(i);
                        context.finish();
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

    private void userPlanList() {

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.get(Data.baseUrl + "userstat/PlanList/Venue?venueId=15103&token="
                + CommonUtil.getAccessToken(context).replaceAll("[+]", "%2B")
                + "&apiKey=" + Data.apiKey, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                try {
                    JSONArray array = new JSONArray(response);
                    if (array.length() < 3) {
//                        buyCreditDailog.findViewById(R.id.freeParent)
//                                .setVisibility(View.GONE);
//                        buyCreditDailog.findViewById(R.id.trialPurchased)
//                                .setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable e) {
            }
        });
    }

    private void freePlan() {

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.post(Data.baseUrl + "UserStat/FreeSubscription?token="
                + CommonUtil.getAccessToken(context).replaceAll("[+]", "%2B")
                + "&apiKey=" + Data.apiKey, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                Toast.makeText(context, "Trial Period Started",
                        Toast.LENGTH_SHORT).show();
                Data.userStatsSubscribed = true;
                Intent i = new Intent(context, UserStats.class);
                context.startActivity(i);
                context.finish();
            }

            @Override
            public void onFailure(Throwable e) {
            }
        });
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

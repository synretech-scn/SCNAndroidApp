package com.tribaltech.android.scnstrikefirst;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;
import com.tribaltech.android.util.NameAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rmn.androidscreenlibrary.ASSL;

public class SettingsFragment extends Fragment implements OnClickListener {

    boolean pinView = true;
    ListView bowlingBallList;
    ListView patternNameList;

    NameAdapter ballNameAdapter;
    NameAdapter patternNameAdapter;

    RelativeLayout bowlingBallHeader;
    RelativeLayout patternNameHeader;

    RelativeLayout bowlingBallContent;
    RelativeLayout patternNameContent;

    LinearLayout tabHeader;
    ScrollView otherSettParent;
    ScrollView equipParent;

    Button ballTypeYes;
    Button ballTypeNo;
    Button oilPatternYes;
    Button oilPatternNo;
    Button pocketYes;
    Button pocketNo;

    TextView endDate;
    private RelativeLayout exportSettParent;
    Context context;
    private int tabCount;
    LinearLayout tabParent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.settings_fragment,
                container, false);
        new ASSL(getActivity(), (ViewGroup) view.findViewById(R.id.root), 1134,
                720, true);
        context = getActivity();
        tabParent = (LinearLayout) view.findViewById(R.id.toggleLay);
        tabCount = tabParent.getChildCount();
        bowlingBallList = (ListView) view.findViewById(R.id.bowlingBallList);
        patternNameList = (ListView) view.findViewById(R.id.patternNameList);
        endDate = (TextView) view.findViewById(R.id.subEndDate);
        tabHeader = (LinearLayout) view.findViewById(R.id.toggleLay);
        otherSettParent = (ScrollView) view
                .findViewById(R.id.otherSettParent);

        exportSettParent = (RelativeLayout) view
                .findViewById(R.id.exportSettParent);
        equipParent = (ScrollView) view.findViewById(R.id.equipParent);

        bowlingBallHeader = (RelativeLayout) view
                .findViewById(R.id.bowlingBallHeader);
        patternNameHeader = (RelativeLayout) view
                .findViewById(R.id.patternNameHeader);
        bowlingBallContent = (RelativeLayout) view
                .findViewById(R.id.bowlingBallContent);
        patternNameContent = (RelativeLayout) view
                .findViewById(R.id.patternNameContent);
        bowlingBallHeader.setOnClickListener(this);
        patternNameHeader.setOnClickListener(this);
        ((Button) view.findViewById(R.id.equipSett)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.otherSett)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.exportSett)).setOnClickListener(this);

        ((Button) view.findViewById(R.id.addNewBall)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.addNewPattern))
                .setOnClickListener(this);
        ((Button) view.findViewById(R.id.saveOther)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.extendSub)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.exportStats)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.resetStats)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.shareOnFb)).setOnClickListener(this);

        ballTypeYes = (Button) view.findViewById(R.id.ballTypeYes);
        ballTypeNo = (Button) view.findViewById(R.id.ballTypeNo);
        oilPatternYes = (Button) view.findViewById(R.id.oilPatternYes);
        oilPatternNo = (Button) view.findViewById(R.id.oilPatternNo);
        pocketYes = (Button) view.findViewById(R.id.pocketYes);
        pocketNo = (Button) view.findViewById(R.id.pocketNo);

        ballTypeYes.setOnClickListener(this);
        ballTypeNo.setOnClickListener(this);
        oilPatternYes.setOnClickListener(this);
        oilPatternNo.setOnClickListener(this);
        pocketYes.setOnClickListener(this);
        pocketNo.setOnClickListener(this);

        ballNameAdapter = new NameAdapter(getActivity(),
                new ArrayList<String[]>(), 100, "BowlingBall", bowlingBallList);
        patternNameAdapter = new NameAdapter(getActivity(),
                new ArrayList<String[]>(), 100, "Pattern", patternNameList);
        fetchCommonLists();
        getUserSettings();
        subEndDate();
        return view;
    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.bowlingBallHeader: {
                boolean open = toggleHeaders(v);
                bowlingBallContent.setVisibility(open ? View.GONE : View.VISIBLE);
                break;
            }

            case R.id.patternNameHeader: {
                boolean open = toggleHeaders(v);
                patternNameContent.setVisibility(open ? View.GONE : View.VISIBLE);
                break;
            }

            case R.id.equipSett: {
//			tabHeader.setBackgroundResource(R.drawable.equip_bg);
                toggleStatsTab(R.id.equipSett);
                otherSettParent.setVisibility(View.GONE);
                exportSettParent.setVisibility(View.GONE);
                equipParent.setVisibility(View.VISIBLE);
                break;
            }

            case R.id.otherSett: {
                toggleStatsTab(R.id.otherSett);
//			tabHeader.setBackgroundResource(R.drawable.other_bg);
                equipParent.setVisibility(View.GONE);
                exportSettParent.setVisibility(View.GONE);
                otherSettParent.setVisibility(View.VISIBLE);
                break;
            }

            case R.id.exportSett: {
                toggleStatsTab(R.id.exportSett);
//			tabHeader.setBackgroundResource(R.drawable.export_bg);
                equipParent.setVisibility(View.GONE);
                otherSettParent.setVisibility(View.GONE);
                exportSettParent.setVisibility(View.VISIBLE);
                break;
            }

            case R.id.addNewBall: {
                ballNameAdapter.askName("0", "", -1);
                break;
            }

            case R.id.addNewPattern: {
                patternNameAdapter.askName("0", "", -1);
                break;
            }

            case R.id.ballTypeYes:
                toggleButtons(ballTypeYes, ballTypeNo);
                break;

            case R.id.ballTypeNo:
                toggleButtons(ballTypeNo, ballTypeYes);
                break;

            case R.id.oilPatternYes:
                toggleButtons(oilPatternYes, oilPatternNo);
                break;

            case R.id.oilPatternNo:
                toggleButtons(oilPatternNo, oilPatternYes);
                break;

            case R.id.pocketYes:
                toggleButtons(pocketYes, pocketNo);
                break;

            case R.id.pocketNo:
                toggleButtons(pocketNo, pocketYes);
                break;

            case R.id.saveOther:
                updateUserSettings();
                break;

            case R.id.extendSub:
//			new UserStatsPackagePopup(getActivity());
                Intent intent = new Intent(getActivity(), UserStatsPackagePopup.class);
                startActivity(intent);
                break;

            case R.id.exportStats:
                exportUserStats(false, true);
                break;

            case R.id.resetStats:
                askToReset();
                break;

            case R.id.shareOnFb:
                ((UserStats) getActivity()).fbLogin();
                // fbLogin();
                break;
        }
    }

    private void toggleButtons(Button clicked, Button other) {
        if (clicked.getTag().equals("off")) {
            clicked.setBackgroundResource(R.drawable.checked_box);
            clicked.setTag("on");
            other.setTag("off");
            other.setBackgroundResource(R.drawable.box);
        }
    }

    private void fetchCommonLists() {

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(
                Data.baseUrl
                        + "UserStat/CommonStandardsNew?token="
                        + CommonUtil.getAccessToken(getActivity()).replaceAll(
                        "[+]", "%2B") + "&apiKey=" + Data.apiKey,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject main = new JSONObject(response)
                                    .getJSONObject("commonStatsStandards");
                            JSONArray array = main
                                    .getJSONArray("bowlingBallNames");
                            final List<String[]> ballList = new ArrayList<String[]>();
                            for (int i = 0; i < array.length(); i++) {
                                ballList.add(new String[]{
                                        array.getJSONObject(i).getString("id"),
                                        array.getJSONObject(i).getString(
                                                "userBowlingBallName")});
                            }

                            ballNameAdapter.setContestList(ballList);
                            bowlingBallList.setAdapter(ballNameAdapter);
                            bowlingBallList.post(new Runnable() {

                                @Override
                                public void run() {
                                    CommonUtil
                                            .setListViewHeightBasedOnChildren(
                                                    bowlingBallList,
                                                    ballNameAdapter.itemHeight);
                                    bowlingBallList.setFocusable(false);
                                }
                            });

                            array = main
                                    .getJSONArray("userStatPatternNameList");
                            final List<String[]> patNameList = new ArrayList<String[]>();
                            for (int i = 0; i < array.length(); i++) {
                                patNameList.add(new String[]{
                                        array.getJSONObject(i).getString("id"),
                                        array.getJSONObject(i).getString(
                                                "patternName")});
                            }
                            patternNameAdapter.setContestList(patNameList);
                            patternNameList.setAdapter(patternNameAdapter);
                            patternNameList.post(new Runnable() {

                                @Override
                                public void run() {
                                    CommonUtil
                                            .setListViewHeightBasedOnChildren(
                                                    patternNameList,
                                                    patternNameAdapter.itemHeight);
                                    patternNameList.setFocusable(false);
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

    private void getUserSettings() {

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(
                Data.baseUrl
                        + "UserStat/UserStatSettingsList?token="
                        + CommonUtil.getAccessToken(getActivity()).replaceAll(
                        "[+]", "%2B") + "&apiKey=" + Data.apiKey,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        try {
                            if (response == null || response.equals("null")) {
                                ballTypeYes
                                        .setBackgroundResource(R.drawable.checked_box);
                                ballTypeYes.setTag("on");
                                oilPatternYes
                                        .setBackgroundResource(R.drawable.checked_box);
                                oilPatternYes.setTag("on");
                                pocketYes
                                        .setBackgroundResource(R.drawable.checked_box);
                                pocketYes.setTag("on");
                                return;
                            }
                            JSONObject obj = new JSONObject(response);
                            boolean ballType = obj.getBoolean("ballType");
                            boolean oilPattern = obj.getBoolean("oilPattern");
                            boolean pocketBrook = obj
                                    .getBoolean("pocketPercentage");
                            if (ballType) {
                                ballTypeYes
                                        .setBackgroundResource(R.drawable.checked_box);
                                ballTypeYes.setTag("on");
                            } else {
                                ballTypeNo
                                        .setBackgroundResource(R.drawable.checked_box);
                                ballTypeNo.setTag("on");
                            }

                            if (oilPattern) {
                                oilPatternYes
                                        .setBackgroundResource(R.drawable.checked_box);
                                oilPatternYes.setTag("on");
                            } else {
                                oilPatternNo
                                        .setBackgroundResource(R.drawable.checked_box);
                                oilPatternNo.setTag("on");
                            }

                            if (pocketBrook) {
                                pocketYes
                                        .setBackgroundResource(R.drawable.checked_box);
                                pocketYes.setTag("on");
                            } else {
                                pocketNo.setBackgroundResource(R.drawable.checked_box);
                                pocketNo.setTag("on");
                            }
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

    private void updateUserSettings() {

        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(getActivity(), "Please wait...");
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.post(
                Data.baseUrl
                        + "UserStat/CreateUserStatSettings?token="
                        + CommonUtil.getAccessToken(getActivity()).replaceAll(
                        "[+]", "%2B") + "&apiKey=" + Data.apiKey
                        + "&BallType=" + ballTypeYes.getTag().equals("on")
                        + "&OilPattern=" + oilPatternYes.getTag().equals("on")
                        + "&Pocket_BrooklynPercentage="
                        + pocketYes.getTag().equals("on"),
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        Toast.makeText(getActivity(), "Settings Saved",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.commonGameErrorDialog(getActivity(),
                                "An error occured.Please try again.");
                        CommonUtil.loading_box_stop();
                    }
                });
    }

    private void subEndDate() {

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(
                Data.baseUrl
                        + "UserStat/RemainingDays?token="
                        + CommonUtil.getAccessToken(getActivity()).replaceAll(
                        "[+]", "%2B") + "&apiKey=" + Data.apiKey,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        endDate.setText(response.replaceAll("\"", ""));
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.commonGameErrorDialog(getActivity(),
                                "An error occured.Please try again.");

                    }
                });
    }

    private void exportUserStats(final boolean resetStats, boolean sendMail) {

        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(getActivity(), "Please wait...");
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(
                Data.baseUrl
                        + "UserStat/ExportToExcelFile?token="
                        + CommonUtil.getAccessToken(getActivity()).replaceAll(
                        "[+]", "%2B") + "&apiKey=" + Data.apiKey
                        + "&reset=" + resetStats + "&mailtouser=" + sendMail,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();

                        if (resetStats) {
                            if (response.length() == 2) {
                                response = "No stats available.";
                            } else {
                                response = "Your bowling stats have been reset.";
                            }
                        } else {
                            if (response.length() == 2) {
                                response = "You do not have any stats yet.Please play more games.";
                            } else {
                                response = "Stats sent to your email address.";
                            }
                        }

                        new AlertDialog.Builder(getActivity())
                                .setTitle(null)
                                .setCancelable(false)
                                .setMessage(response)
                                .setPositiveButton(android.R.string.yes,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();

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

    /**
     * Background Async Task to download file
     */
    static class DownloadFileFromURL extends AsyncTask<String, String, String> {

        Context context;

        /**
         * Before starting background thread Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                String filePath = Environment.getExternalStorageDirectory()
                        .toString() + System.currentTimeMillis() + ".xlsx";
                // Output stream to write file
                OutputStream output = new FileOutputStream(filePath);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

                return filePath;
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            // pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String filePath) {
            // dismiss the dialog after the file was downloaded
            // dismissDialog(progress_bar_type);

            if (filePath != null) {
                // Toast.makeText(context,
                // "Unable to export stats.Please try again.",
                // Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context,
                        "Unable to export stats.Please try again.",
                        Toast.LENGTH_LONG).show();

            }
            // setting downloaded into image view
            // my_image.setImageDrawable(Drawable.createFromPath(imagePath));
        }
    }

    private boolean toggleHeaders(View v) {
        Boolean open = v.getTag().toString().equalsIgnoreCase("open");
        ((ViewGroup) v).getChildAt(2).setBackgroundResource(open ? R.drawable.plus_selector : R.drawable.minus_selector);
//		((TextView) (((ViewGroup) v).getChildAt(2))).setText("Tap to "
//				+ (open ? "Show" : "Hide") + " Status");
//		((TextView) (((ViewGroup) v).getChildAt(1)))
//				.setBackgroundResource(open ? R.drawable.arrow_down
//						: R.drawable.arrow_up);
        v.setTag(open ? "closed" : "open");
        return open;
    }

    private void askToReset() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Confirm");
        builder.setMessage("This would delete your stats and no stats would be shown here. All these stats would be emailed to you for future reference.Do you want to continue ?");
        builder.setCancelable(false);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                exportUserStats(true, true);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
                : false;
    }

    public void fbLogin() {

        Session session = new Session(getActivity());
        Session.setActiveSession(session);
        Session.OpenRequest openRequest = null;
        openRequest = new Session.OpenRequest(getActivity());
        openRequest.setPermissions(Arrays.asList("publish_actions"));

        try {

            if (isSystemPackage(getActivity().getPackageManager()
                    .getPackageInfo("com.facebook.katana", 0))) {
                openRequest.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
            } else {
            }

        } catch (NameNotFoundException e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

        }

        openRequest.setCallback(new Session.StatusCallback() {

            @Override
            public void call(Session session, SessionState state,

                             Exception exception) {

                // Log.v("callback = ", "callback");

                if (session.isOpened()) {
                    // facebookPost(session.getAccessToken());
                    publishFeedDialog();
                }

            }

        });

        session.openForPublish(openRequest);

    }

    private void publishFeedDialog() {
        // PuzzlePic
        // Take a Pic, and Puzzle it!
        // I just finished solving a puzzle!

        Bundle params = new Bundle();
        params.putString("name", "XBowling");
        params.putString("caption", "User Stats");
        params.putString("description", "I just finished solving a puzzle!");
        params.putString("link", "http://smarturl.it/PuzzlePicApp");
        params.putString("picture",
                "http://puzzle-pic.s3.amazonaws.com/puzzleImages/logo.png");

        Session session = Session.getActiveSession();

        WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(getActivity(),
                session, params)).setOnCompleteListener(
                new OnCompleteListener() {

                    @Override
                    public void onComplete(Bundle values,
                                           FacebookException error) {
                        if (error == null) {
                            // When the story is posted, echo the success
                            // and the post Id.
                            final String postId = values.getString("post_id");
                            if (postId != null) {
                                // Toast.makeText(getActivity(),
                                // "Posted on your wall" + postId,
                                // Toast.LENGTH_SHORT).show();
                                CommonUtil
                                        .commonDialog(
                                                getActivity(),
                                                "Successfully posted on your Facebook Wall.",
                                                "Facebook");

                            } else {
                                // User clicked the Cancel button
                                Toast.makeText(
                                        getActivity().getApplicationContext(),
                                        "Publish cancelled", Toast.LENGTH_SHORT)
                                        .show();

                            }
                        } else if (error instanceof FacebookOperationCanceledException) {
                            Toast.makeText(
                                    getActivity().getApplicationContext(),
                                    "Publish cancelled", Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            Toast.makeText(
                                    getActivity().getApplicationContext(),
                                    "Error posting story", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }

                }).build();
        feedDialog.show();
    }

    private void toggleStatsTab(int tabId) {
        for (int i = 0; i < tabCount; i++) {
            tabParent.getChildAt(i).setBackgroundResource(tabId == tabParent.getChildAt(i).getId() ?
                    R.drawable.selected_background : R.drawable.pending_background);
        }
    }

}

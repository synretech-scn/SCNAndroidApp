package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tribaltech.android.util.AppStatus;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;
import com.tribaltech.android.util.TagAdapter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import rmn.androidscreenlibrary.ASSL;


public class Tags extends Activity {

    ListView tagList;
    TagAdapter tagAdapter;
    String gameId;
    EditText enterTag;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);
        new ASSL(Tags.this, (ViewGroup) findViewById(R.id.root), 1134,
                720, false);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        tagList = (ListView) findViewById(R.id.tagList);
        enterTag = (EditText) findViewById(R.id.enterTag);
//        List<String[]> tags = (List<String[]>) getIntent().getSerializableExtra("tags");
        gameId = getIntent().getStringExtra("gameId");

//        List<String> tags = new ArrayList<>();
//        tags.addAll(GameScreen.tags);
        tagAdapter = new TagAdapter(this, GameScreen.tags, tagList);
        tagList.setAdapter(tagAdapter);
        CommonUtil
                .setListViewHeightBasedOnChildren(
                        tagList,
                        tagAdapter.itemHeight);
        tagList.setFocusable(false);


        enterTag.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                } else {
//                    Toast.makeText(getApplicationContext(), "lost the focus", Toast.LENGTH_LONG).show();
                }
            }
        });

        /*enterTag.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // hide virtual keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(enterTag.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    return true;
                }
                return true;
            }
        });*/
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                addTags(Tags.this, gameId, tagAdapter.data, true);
                break;

            case R.id.saveTags:
                String value = enterTag.getText().toString();
                if (value.isEmpty()) {
                    Toast.makeText(Tags.this, "Tag cannot be empty.", Toast.LENGTH_SHORT).show();
                } else if (tagAdapter.data.contains(value)) {
                    Toast.makeText(Tags.this, "Tag already exists.", Toast.LENGTH_SHORT).show();
                } else {
                    tagAdapter.data.add(value);
                    tagAdapter.notifyDataSetChanged();
                    CommonUtil
                            .setListViewHeightBasedOnChildren(
                                    tagList,
                                    tagAdapter.itemHeight);
                    enterTag.setText("");
                }
                break;
        }
    }

    public static void addTags(final Context ctx, final String gameId, final List<String> tags,
                               final boolean finishActivity) {
        if (!AppStatus.getInstance(ctx).isOnline(
                ctx)) {
            CommonUtil.noInternetDialog(ctx);
            return;
        }

        try {

            RequestParams params = new RequestParams();
            params.put("GameId", gameId);
            params.put("Tags", URLEncoder.encode(tags.toString().
                    substring(1, tags.toString().length() - 1).trim(), "utf-8"));
//        params.put("Tags","DummyTag");

            if (!CommonUtil.is_loading_showing()) {
                CommonUtil.loading_box(ctx, "Saving Tags...");
            }
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(CommonUtil.TIMEOUT);

            client.post(Data.baseUrl + "Tags/UpdateAllTags?token="
                    + CommonUtil.getAccessToken(ctx)
                    .replaceAll("[+]", "%2B") + "&apiKey="
                    + Data.apiKey + "&" + params, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(String s) {
                    CommonUtil.loading_box_stop();
                    if (finishActivity) {
                        Toast.makeText(ctx, "Tags saved.", Toast.LENGTH_SHORT).show();
//                        GameScreen.tags.clear();
//                        GameScreen.tags.addAll(tags);

                        ((Activity) ctx).setResult(RESULT_OK);
                        ((Activity) ctx).finish();
                    } else {
                        GameScreen.updateTags(ctx);
                    }
                }

                @Override
                public void onFailure(Throwable e) {
                    CommonUtil.loading_box_stop();
                }
            });

        } catch (Exception e) {
            if (e instanceof UnsupportedEncodingException) {
                Toast.makeText(ctx, "Unable to save tags.Please try again.", Toast.LENGTH_SHORT).show();
            }
            e.printStackTrace();
        }
    }

}

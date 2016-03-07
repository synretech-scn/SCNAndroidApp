package com.tribaltech.android.scnstrikefirst;

import android.os.Bundle;


import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.content.Intent;
import android.net.Uri;
import android.content.Context;
import com.tribaltech.android.util.CommonUtil;


public class webView extends MenuIntent {

    private WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        if (getIntent().hasExtra("url")) {
            webView = (WebView) findViewById(R.id.webView1);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setSupportZoom(true);
            webView.getSettings().setGeolocationEnabled(true);


            //add
            //启用数据库
            webView.getSettings().setDatabaseEnabled(true);
            String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
            //启用地理定位
            webView.getSettings().setGeolocationEnabled(true);
            //设置定位的数据库路径
            webView.getSettings().setGeolocationDatabasePath(dir);
            /* webView.getSettings().setJavaScriptEnabled(true);// 设置支持javascript
  webView.requestFocus();// 获取触摸焦点
  webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);// 取消滚动条
  webView.getSettings().setBuiltInZoomControls(true); // 构建缩放控制
  webView.getSettings().setSupportZoom(true); // 设置支持缩放

*/

            webView.getSettings().setAllowFileAccess(true);
            webView.getSettings().setDomStorageEnabled(true);//允许DCOM

            webView.requestFocus();


            webView.loadUrl(getIntent().getExtras().getString("url"));
            CommonUtil.loading_box(this, "Loading...");
            webView.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(WebView view,String url){
                    //当有新连接时，使用当前的 WebView
                    //调用拨号程序
                    if (url.startsWith("mailto:") || url.startsWith("geo:") ||url.startsWith("tel:")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }

                    else
                        view.loadUrl(url);

                    return true;
                }

                public void onPageFinished(WebView view, String url) {
                    // do your stuff here
                    CommonUtil.loading_box_stop();
                }
            });


        } else {
            finish();
        }

    }
 /*
 @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), ScreenMain.class);
        startActivity(i);
        finish();
    }

    public void toggle(View view){
        toggle();
    }

    */
}
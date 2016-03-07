package com.tribaltech.android.scnstrikefirst;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import rmn.androidscreenlibrary.ASSL;

public class USBC extends MenuIntent {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usbc);
        new ASSL(USBC.this, (ViewGroup) findViewById(R.id.root), 1134,
                720, false);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), ScreenMain.class);
        startActivity(i);
        finish();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.usbcOpen: {
                // new usbc1Popup(this);
                Intent intent = new Intent(USBC.this, webView.class);
                intent.putExtra("url", "http://www.xbowling.com/usbc-championship/USBC-Open-Championships-2015/");
                intent.putExtra("redirect", true);
                startActivity(intent);
                break;
            }

            case R.id.usbcWomen: {
                Intent intent = new Intent(USBC.this, webView.class);
                intent.putExtra("url", "http://www.xbowling.com/usbc-championship/USBC-Womens-Championships-2015/");
                intent.putExtra("redirect", true);
                startActivity(intent);
                break;
            }

            case R.id.interCollege: {
                // new usbc3Popup(this);
                Intent intent = new Intent(USBC.this, webView.class);
                intent.putExtra("url", "http://www.xbowling.com/usbc-championship/USBC-Intercollegiate-Team-Championships/");
                intent.putExtra("redirect", true);
                startActivity(intent);
                break;
            }
        }
    }

    public void toggle(View view){
        toggle();
    }

}

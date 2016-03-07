package com.tribaltech.android.util;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;

import com.tribaltech.android.scnstrikefirst.R;

import rmn.androidscreenlibrary.ASSL;

public class offline extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offline);
        new ASSL(this, (ViewGroup) findViewById(R.id.root), 720, 1196, true);

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        // super.onBackPressed();
        finish();

    }
}
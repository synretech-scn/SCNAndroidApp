package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.tribaltech.android.util.AppConstants;
import com.tribaltech.android.util.CompareAdapter;

import java.util.List;

import rmn.androidscreenlibrary.ASSL;


public class ComparisonActivity extends Activity {

    CompareAdapter compareAdapter;
    ListView compareList;
    TextView oppName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);
        new ASSL(ComparisonActivity.this, (ViewGroup) findViewById(R.id.root),
                AppConstants.SCREEN_HEIGHT, AppConstants.SCREEN_WIDTH,
                false);
        oppName = (TextView)findViewById(R.id.oppName);
        oppName.setText(getIntent().getStringExtra("oppName"));
        compareList = (ListView)findViewById(R.id.compareList);
        List<String[]> data = (List<String[]>)getIntent().getSerializableExtra("comparisonData");
        compareAdapter = new CompareAdapter(this, data);
        compareList.setAdapter(compareAdapter);
    }

    public void onClick(View view){
        finish();
    }

}

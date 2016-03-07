package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.tribaltech.android.util.AppConstants;
import com.tribaltech.android.util.FilterItem;

import rmn.androidscreenlibrary.ASSL;

public class GraphActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        new ASSL(GraphActivity.this, (ViewGroup) findViewById(R.id.root),
                AppConstants.SCREEN_WIDTH, 1196,
                false);
        getFragmentManager().beginTransaction()
                .replace(R.id.contentArea, new GraphFragment()).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (true || resultCode == Activity.RESULT_OK) {
                    Fragment currentFragment = getFragmentManager().findFragmentById(R.id.contentArea);
                    if (currentFragment instanceof UserStats.Filterable) {
                        ((UserStats.Filterable) currentFragment).filter((FilterItem) data
                                .getSerializableExtra("filter"));
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                }
                break;
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;

            case R.id.filter:
                Intent intent = new Intent(this, FilterActivity.class);
                startActivityForResult(intent, 1);
                break;
        }
    }
}

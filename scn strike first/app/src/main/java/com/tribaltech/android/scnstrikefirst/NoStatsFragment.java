package com.tribaltech.android.scnstrikefirst;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;

import rmn.androidscreenlibrary.ASSL;

public class NoStatsFragment extends Fragment implements OnClickListener {

    LinearLayout trialParent;
    TextView trialOver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.no_stats_fragment, container,
                false);
        new ASSL(getActivity(), (ViewGroup) view.findViewById(R.id.root), 1134,
                720, true);
        trialOver = (TextView) view.findViewById(R.id.trialVersionUsed);
        trialParent = (LinearLayout) view.findViewById(R.id.trialParent);
        view.findViewById(R.id.trialVersion).setOnClickListener(this);
        view.findViewById(R.id.buyStats).setOnClickListener(this);
        trialOver.setVisibility(Data.trialPurchased ? View.VISIBLE : View.GONE);
        trialParent.setVisibility(Data.trialPurchased ? View.GONE : View.VISIBLE);
        return view;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.trialVersion:
                freePlan();
                break;

            case R.id.buyStats:
                Intent intent = new Intent(getActivity(), UserStatsPackagePopup.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    private void freePlan() {

        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(getActivity(), "Please wait...");
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.post(Data.baseUrl + "UserStat/FreeSubscription?token="
                + CommonUtil.getAccessToken(getActivity()).replaceAll("[+]", "%2B")
                + "&apiKey=" + Data.apiKey, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                CommonUtil.loading_box_stop();
                Toast.makeText(getActivity(), "Trial Period Started",
                        Toast.LENGTH_SHORT).show();
                Data.userStatsSubscribed = true;
                ((UserStats) getActivity()).statsTab(getActivity().findViewById(R.id.statsTab));
            }

            @Override
            public void onFailure(Throwable e) {
                CommonUtil.loading_box_stop();
            }
        });
    }
}

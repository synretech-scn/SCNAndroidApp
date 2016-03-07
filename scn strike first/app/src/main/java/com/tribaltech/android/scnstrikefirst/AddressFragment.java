package com.tribaltech.android.scnstrikefirst;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tribaltech.android.util.AppStatus;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rmn.androidscreenlibrary.ASSL;

/**
 * Created by cl-99 on 8/3/2015.
 */
public class AddressFragment extends Fragment implements View.OnClickListener {

    EditText firstName;
    EditText lastName;
    EditText address1;
    EditText address2;
    EditText zipCode;
    EditText city;
    Spinner states;
    EditText state;
    String countryCode = "";
    String regionCode = "";
    String regionName = "";
    int rewardPoints;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.shipping_address, container,
                false);
        new ASSL(getActivity(), (ViewGroup) view.findViewById(R.id.root), 1134,
                720, false);
        address1 = (EditText) view.findViewById(R.id.address1);
        address2 = (EditText) view.findViewById(R.id.address2);
        firstName = (EditText) view.findViewById(R.id.firstName);
        lastName = (EditText) view.findViewById(R.id.lastName);
        zipCode = (EditText) view.findViewById(R.id.zipCode);
        city = (EditText) view.findViewById(R.id.cityName);
        state = (EditText) view.findViewById(R.id.state);
        states = (Spinner) view.findViewById(R.id.states);
        states.setOnItemSelectedListener(stateAdapterListener);
        view.findViewById(R.id.submit).setOnClickListener(this);
        rewardPoints = getArguments().getInt("rewardPoints");
        loadCountries();
        getLocation();
        return view;
    }

    @Override
    public void onClick(View v) {

        String message = "";
        String title = "Error";
        if (!countryCode.equalsIgnoreCase("US")) {
            message = "We currently offer our prize redemption capability only in the United States.  An expanded international redemption program is coming soon!  Keep playing and winning!";
        } else if (rewardPoints > 110000 && regionCode.equalsIgnoreCase("AZ")) {
            title = "Who would have guessed?";
            message = "In accordance with certain restrictions in Arizona state law, XBowlers from the great State of Arizona are not allowed to redeem SCN Reward Points for any single prize with a value in excess of 110,000 Points.Please adjust your seletions accordingly.  Happy shopping!";
        } else if (regionCode.equalsIgnoreCase("AK") || regionCode.equalsIgnoreCase("HI") || regionCode.equalsIgnoreCase("VT")) {
            message = "For regulatory reasons, we are currently not able to allow the redemption of SCN Reward Points for prizes in scn.location().regionName +  to XBowlers accessing our service from within that State. We have noted your request and will contact you if/when XBowling begins offering the redemption opportunity within the State of  " + regionName;
            title = "We apologize!";
        } else if (regionCode.equalsIgnoreCase("MT") ||
                regionCode.equalsIgnoreCase("SC") ||
                regionCode.equalsIgnoreCase("NJ") ||
                regionCode.equalsIgnoreCase("TN") ||
                regionCode.equalsIgnoreCase("WA") ||
                regionCode.equalsIgnoreCase("SD") ||
                regionCode.equalsIgnoreCase("NH")) {
            message = "For regulatory reasons, we are currently not able to allow the redemption of SCN Reward Points for prizes in your state.  We are working with regulatory authorities so we can provide you with this feature, although there is no guarantee that we will be able to do so.  We will notify you as soon as this option is available in your state.  In the meantime, please enjoy XBowling!";
            title = "We apologize!";
        }

        if (!message.isEmpty()) {
            CommonUtil.commonDialog(getActivity(), title, message);
            return;
        }
        try {
            JSONObject obj = new JSONObject();
            JSONObject product = new JSONObject();
            product.put("id", getArguments().getString("id"));
            obj.put("product", product);
            obj.put("shipToAddressLine1", address1.getText().toString());
            obj.put("shipToAddressLine2", address2.getText().toString());
            obj.put("shipToCity", city.getText().toString());
            obj.put("shipToState", states.getSelectedItem());
            obj.put("shipToName", firstName.getText().toString() + " " + lastName.getText().toString());
            obj.put("shipToZip", zipCode.getText().toString());

            ((WalletTransaction) getActivity()).redeemPrize(obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    AdapterView.OnItemSelectedListener stateAdapterListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1,
                                   int position, long arg3) {
            state.setText(states.getSelectedItem().toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };

    private void loadCountries() {

        if (!AppStatus.getInstance(getActivity()).isOnline(getActivity())) {
            CommonUtil.noInternetDialog(getActivity());
            return;
        }
        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(getActivity(), "Please wait...");
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.get(Data.baseUrl + "venue/locations?apiKey=" + Data.apiKey,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {

                        CommonUtil.loading_box_stop();
                        try {
                            JSONArray countriesArray = new JSONArray(response);
                            List<String> stateList = new ArrayList<String>();
                            for (int i = 0; i < countriesArray.length(); i++) {
                                JSONObject country = countriesArray
                                        .getJSONObject(i);
                                String countryName = country
                                        .getString("displayName");
                                if (countryName.equalsIgnoreCase("United States")) {
                                    JSONArray statesArray = country
                                            .getJSONArray("states");
                                    for (int j = 0; j < statesArray.length(); j++) {
                                        stateList.add(statesArray.getJSONObject(j)
                                                .getString("displayName"));
                                    }
                                    break;
                                }
                            }

                            GoBowling.CustomAdapter<String> adapter = new GoBowling.CustomAdapter<String>(
                                    getActivity(),
                                    android.R.layout.simple_spinner_item,
                                    stateList);
                            adapter
                                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            states.setAdapter(adapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.loading_box_stop();
                    }
                });
    }

    private void getLocation() {
        if (!AppStatus.getInstance(getActivity()).isOnline(getActivity())) {
            CommonUtil.noInternetDialog(getActivity());
            return;
        }
        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(getActivity(), "Please wait...");
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.get(Data.baseUrl + "geolocation",
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {

                        CommonUtil.loading_box_stop();
                        try {
                            JSONObject obj = new JSONObject(response);
                            countryCode = obj.getString("countryCode");
                            regionCode = obj.getString("regionCode");
                            regionName = obj.getString("regionName");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.loading_box_stop();
                    }
                });
    }

}

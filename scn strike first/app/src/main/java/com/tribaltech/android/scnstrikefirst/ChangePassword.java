package com.tribaltech.android.scnstrikefirst;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tribaltech.android.util.AppStatus;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;

import rmn.androidscreenlibrary.ASSL;


public class ChangePassword extends MenuIntent {

    EditText oldPassword, newPassword, confirmPassword;
    TextView oldPasswordLabel, newPasswordLabel, confirmPasswordLabel, done, cancel, header;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        new ASSL(ChangePassword.this, (ViewGroup) findViewById(R.id.root), 1134, 720,
                false);
        initComponents();
        setParentTouch();
    }

    private void initComponents() {
//        Typeface font_regular = Typeface.createFromAsset(this.getAssets(), "fonts/avenir_next.otf");
//        Typeface font_bold = Typeface.createFromAsset(this.getAssets(), "fonts/avenir_bold.otf");

        oldPassword = (EditText) findViewById(R.id.current_pwd);
        newPassword = (EditText) findViewById(R.id.new_pwd);
        confirmPassword = (EditText) findViewById(R.id.confirm_pwd);

        oldPassword.setOnFocusChangeListener(this);
        newPassword.setOnFocusChangeListener(this);
        confirmPassword.setOnFocusChangeListener(this);

        oldPasswordLabel = (TextView) findViewById(R.id.current_pwd_label);
        newPasswordLabel = (TextView) findViewById(R.id.new_pwd_label);
        confirmPasswordLabel = (TextView) findViewById(R.id.confirm_pwd_label);
        done = (TextView) findViewById(R.id.done);
        cancel = (TextView) findViewById(R.id.cancel);
        header = (TextView) findViewById(R.id.header);

//        done.setTypeface(font_regular);
//        cancel.setTypeface(font_regular);
//        header.setTypeface(font_bold);
//        newPasswordLabel.setTypeface(font_regular);
//        oldPasswordLabel.setTypeface(font_regular);
//        confirmPasswordLabel.setTypeface(font_regular);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.done:
                changePasswordMethod();
                break;
            case R.id.cancel:
                finish();
                break;
        }
    }

    private void changePasswordMethod() {
        if (!(oldPassword.getText().toString().isEmpty()
                )) {
            if(newPassword.getText().toString().isEmpty()){
                CommonUtil.commonGameErrorDialog(ChangePassword.this,
                        "Please enter new password.");
                return;
            }

            if(confirmPassword
                    .getText().toString().isEmpty()){
                CommonUtil.commonGameErrorDialog(ChangePassword.this,
                        "Please confirm new password.");
                return;
            }

            if (confirmPassword.getText().toString()
                    .equals(newPassword.getText().toString())) {
                updatePassword();
            } else {
                CommonUtil.commonGameErrorDialog(ChangePassword.this,
                        "Password not confirmed.");
                return;
            }
        } else {
            CommonUtil.commonGameErrorDialog(ChangePassword.this,
                    "Please enter current password.");
            return;
        }
    }

    public void updatePassword() {
        if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                getApplicationContext())) {
            CommonUtil.noInternetDialog(ChangePassword.this);
            return;
        }
        CommonUtil.loading_box(this, "Please wait...");
        RequestParams rv = new RequestParams();

        rv.put("token", CommonUtil.getAccessToken(getApplicationContext()));
        rv.put("apiKey", Data.apiKey);
        rv.put("oldPassword", oldPassword.getText().toString());
        rv.put("newPassword", newPassword.getText().toString());

        AsyncHttpClient client = new AsyncHttpClient();

        client.setTimeout(CommonUtil.TIMEOUT);
        client.post(Data.baseUrl + "/user/current/password", rv,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        Toast.makeText(getApplicationContext(),
                                "Password changed successfully.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ChangePassword.this, UserProfile.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.loading_box_stop();
                        // CommonUtil.commonGameErrorDialog(myProfile.this,
                        // e.getMessage()+ "");

                        // HttpResponseException hre = (HttpResponseException)e;
                        //
                        // if (hre.getStatusCode() == 401)
                        // CommonUtil
                        // .commonErrorDialog(
                        // myProfile.this,
                        // "There was a problem validating the users session OR the oldPassword provided in the body was incorrect.");
                        // else
                        CommonUtil.commonErrorDialog(ChangePassword.this,
                                "Please check current password and try again.");
                    }
                });
    }
}

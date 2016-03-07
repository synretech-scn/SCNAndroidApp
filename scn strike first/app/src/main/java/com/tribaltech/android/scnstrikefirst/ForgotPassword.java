package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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


public class ForgotPassword extends Activity {
    TextView forgotPasswordMessage;
    EditText email;
    Button proceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        new ASSL(ForgotPassword.this, (ViewGroup) findViewById(R.id.root), 1134, 720,
                false);
        forgotPasswordMessage = (TextView) findViewById(R.id.forgot_message);
        email = (EditText) findViewById(R.id.email);
        proceed = (Button) findViewById(R.id.proceed);
        Typeface font = Typeface.createFromAsset(this.getAssets(), "fonts/avenir_next.otf");
        forgotPasswordMessage.setTypeface(font);
        email.setTypeface(font);
        proceed.setTypeface(font);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.proceed:
                String value = email.getText().toString();
                if(!value.isEmpty() && CommonUtil.isValidEmail(value)) {
                    forgotPasswordServer(value);
                } else {
                    CommonUtil.commonGameErrorDialog(ForgotPassword.this,
                            "Please fill valid registered email.");
                    return;
                }
                break;
        }
    }

    private void forgotPasswordServer(String value) {
        if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                getApplicationContext())) {
            CommonUtil.noInternetDialog(ForgotPassword.this);
            return;
        }
        CommonUtil.loading_box(this, "Please wait...");
        RequestParams rv = new RequestParams();
        rv.put("email", value);
        rv.put("token", CommonUtil.getAccessToken(getApplicationContext()));
        rv.put("apiKey", Data.apiKey);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(10000000);
        client.post(Data.baseUrl + "/user/passwordreset", rv,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {

                        Log.v("response passwordreset = ", response);
                        CommonUtil.loading_box_stop();
                        Toast.makeText(getApplicationContext(),
                                "Your password is sent to your email.", Toast.LENGTH_LONG)
                                .show();

                        Intent i = new Intent(getApplicationContext(),
                                Login.class);
                        startActivity(i);
                        finish();
                        // overridePendingTransition(R.anim.from_right,
                        // R.anim.to_left);

                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.v("response = ", e.toString());
                        CommonUtil
                                .commonGameErrorDialog(ForgotPassword.this,
                                        "An error occured.Please check the email address and try again.");
                        CommonUtil.loading_box_stop();
                    }

                });
    }
}

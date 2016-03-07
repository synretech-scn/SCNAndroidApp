package com.tribaltech.android.scnstrikefirst;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tribaltech.android.util.AppStatus;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;

import org.apache.http.client.HttpResponseException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import rmn.androidscreenlibrary.ASSL;

public class Login extends MenuIntent {
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final int DLG_EXAMPLE1 = 0;
    private static final int TEXT_ID = 0;
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    //modi
    // public String SENDERID = "856250932622";
    public String SENDERID = "778079476575";

    protected String URL = "http://www.xbowling.com";
    EditText email, password;
    LinearLayout loginView, signupView;
    Boolean isLoginScreen = true;
    Button loginBTN, sideButton;
    private GoogleCloudMessaging gcm;
    private Session session;
    private ImageView adsImage;
    private Button cmaBanner, signUp, joinUs, fbSignIn;
    private TextView joinUsMessage, signInMessage, forgot;
    Button termsCheck;
    Button emailCheck;
    boolean fromSignUp;
    String facebookToken = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        new ASSL(Login.this, (ViewGroup) findViewById(R.id.root), 1134, 720,
                false);
        initComponent();
        setParentTouch();
        registerGCM(getApplicationContext());
    }


    private void initComponent() {
        Typeface font = Typeface.createFromAsset(this.getAssets(), "fonts/avenir_next.otf");
        loginBTN = (Button) findViewById(R.id.login);
        signUp = (Button) findViewById(R.id.sign_up);
        joinUs = (Button) findViewById(R.id.join_us);
        fbSignIn = (Button) findViewById(R.id.fb_login);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
//        email.setOnFocusChangeListener(new View.OnFocusChangeListener(){
//
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus) {
//                    CommonUtil.hideSoftKeyboard(Login.this, email);
//                }
//            }
//        });

        email.setOnFocusChangeListener(this);
        password.setOnFocusChangeListener(this);

        joinUsMessage = (TextView) findViewById(R.id.join_us_message);
        forgot = (TextView) findViewById(R.id.forgot_password);
        signInMessage = (TextView) findViewById(R.id.sign_in_message);
        email.setTypeface(font);
        password.setTypeface(font);
        joinUsMessage.setTypeface(font);
        forgot.setTypeface(font);
        signInMessage.setTypeface(font);
        signUp.setTypeface(font);
        joinUs.setTypeface(font);
        loginBTN.setTypeface(font);
        fbSignIn.setTypeface(font);
        termsCheck = (Button) findViewById(R.id.termsCheck);
        emailCheck = (Button) findViewById(R.id.emailCheck);
    }

    private void registerGCM(Context applicationContext) {
        try {
            // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
            if (checkPlayServices()) {
                gcm = GoogleCloudMessaging.getInstance(this);
                Data.regid = getRegistrationId(Login.this);

                if (Data.regid.isEmpty()) {
                    registerInBackground();
                }
            } else {
                Log.i("TAG", "No valid Google Play Services APK found.");
            }

        } catch (Exception e) {
            Log.e("exception GCM", e.toString() + " " + e.toString());
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("TAG", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                // String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(Login.this);
                    }

                    Data.regid = gcm.register(SENDERID);
                    // msg = "Device registered, registration ID=" + Utils.deviceToken;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(Login.this,Data.regid,Toast.LENGTH_LONG).show();
                        }
                    });

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(Login.this, Data.regid);
                } catch (IOException ex) {
                    // msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return "";

            }

            @Override
            protected void onPostExecute(String msg) {
                //mDisplay.append(msg + "\n");
            }
        }.execute(null, null, null);
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.commit();
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(Login.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i("TAG", "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        return registrationId;
    }

    public void loginMethod(View v) {
        if (!(email.getText().toString().length() > 0 && password.getText()
                .toString().length() > 0)) {
            CommonUtil.commonGameErrorDialog(Login.this,
                    "Please fill in all fields.");
            return;
        } else if (!CommonUtil.isValidEmail(email.getText().toString())) {
            CommonUtil.commonGameErrorDialog(Login.this,
                    "Please enter valid email address.");
            return;
        }
        if (isLoginScreen) {
            login(email.getText().toString(), password.getText().toString());
        }
    }

    public void loginView(View v) {
        isLoginScreen = true;
//        email.setText("");
//        password.setText("");
        joinUsMessage.setVisibility(View.VISIBLE);
        forgot.setVisibility(View.VISIBLE);
        loginBTN.setVisibility(View.VISIBLE);
        joinUs.setVisibility(View.VISIBLE);
        fbSignIn.setVisibility(View.VISIBLE);
        fbSignIn.setText("Sign in with Facebook");
        signUp.setVisibility(View.GONE);
        signInMessage.setVisibility(View.GONE);
        findViewById(R.id.back).setVisibility(View.GONE);
    }

    public void login(String email, String password) {

        if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                getApplicationContext())) {
            CommonUtil.noInternetDialog(Login.this);
            return;
        }
        CommonUtil.loading_box(this, "Please wait...");
        RequestParams rv = new RequestParams();
        rv.put("email", email);
        rv.put("password", password);
        rv.put("apiKey", Data.apiKey);

        AsyncHttpClient client = new AsyncHttpClient();

        client.setTimeout(CommonUtil.TIMEOUT);
        client.post(Data.baseUrl + "user/authenticate", rv,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {

                        Log.v("response = ", response + ",");
                        CommonUtil.loading_box_stop();

                        Data.AccessToken = response.substring(1,
                                response.length() - 1);
                        CommonUtil.saveAccessToken(Data.AccessToken,
                                getApplicationContext());
                        Log.v("token = ", CommonUtil
                                .getAccessToken(getApplicationContext()));
                        getProfileData();
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.loading_box_stop();
                        Log.v("response = ", e.toString());
                        if (e instanceof HttpResponseException) {
                            HttpResponseException hre = (HttpResponseException) e;
                            CommonUtil.loading_box_stop();
                            if (hre.getStatusCode() == 401)
                                CommonUtil
                                        .commonGameErrorDialog(Login.this,
                                                "Login Credentials invalid!");
                            else
                                CommonUtil.commonGameErrorDialog(Login.this,
                                        "Connection Timed out. Try again..");
                        }

                            /*CommonUtil.loading_box_stop();
                        CommonUtil.commonGameErrorDialog(Login.this,
                                "Invalid Email or Password.");*/
                    }

                });
    }

    /**
     * If a dialog has already been created, this is called to reset the dialog
     * before showing it a 2nd time. Optional.
     */
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {

        switch (id) {
            case DLG_EXAMPLE1:
                // Clear the input box.
                EditText text = (EditText) dialog.findViewById(TEXT_ID);
                text.setText("");
                break;
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                loginMethod(view);
                break;
            case R.id.join_us:
                signUpView();
                break;
            case R.id.sign_up:
                signUpMethod();
                break;
            case R.id.fb_login:
                fbLogin();
//                if (fbSignIn.getText().equals("Sign up with Facebook")) {
//
//                    findViewById(R.id.termsParent).setVisibility(View.VISIBLE);
//                    findViewById(R.id.loginSignupParent).setVisibility(View.GONE);
//                    findViewById(R.id.back).setVisibility(View.VISIBLE);
//
//                } else {
//
//
//                }
                break;

            case R.id.forgot_password:
                Intent intent = new Intent(Login.this, ForgotPassword.class);
                startActivity(intent);
                break;
            case R.id.sign_in_message:
                loginView(view);
                break;

            case R.id.conditions:
            case R.id.terms:
                openWebView("file:///android_asset/terms-of-use.html");
                break;

            case R.id.privacyPolicy:
                openWebView("http://www.xbowling.com/phone/privacy-policy.html");
                break;

            case R.id.termsCheck:
                if (termsCheck.getTag().toString().equalsIgnoreCase("off")) {
                    termsCheck.setTag("on");
                    termsCheck.setBackgroundResource(R.drawable.checked_box);
                } else {
                    termsCheck.setTag("off");
                    termsCheck.setBackgroundResource(R.drawable.box);
                }
                break;

            case R.id.emailCheck:
                if (emailCheck.getTag().toString().equalsIgnoreCase("off")) {
                    emailCheck.setTag("on");
                    emailCheck.setBackgroundResource(R.drawable.checked_box);
                } else {
                    emailCheck.setTag("off");
                    emailCheck.setBackgroundResource(R.drawable.box);
                }
                break;

            case R.id.continueTerms:
                if (termsCheck.getTag().toString().equalsIgnoreCase("off") ||
                        emailCheck.getTag().toString().equalsIgnoreCase("off")) {
                    //Toast.makeText(getApplicationContext(), "Please check the above boxes. ", Toast.LENGTH_SHORT).show();
                    CommonUtil.commonDialog(this, null, "In order to use XBowling App, you must agree to our Terms and Conditions, to Privacy Policy and to receive electronic communications from us and our affiliates.");
                } else {
                    findViewById(R.id.termsParent).setVisibility(View.GONE);
                    findViewById(R.id.loginSignupParent).setVisibility(View.VISIBLE);
                    findViewById(R.id.back).setVisibility(View.GONE);
                    if (fromSignUp) {
                        SignUpServer(email.getText().toString(), password.getText()
                                .toString());
                    } else {
//                        fbLogin();
//                        getProfileData();
                        SignUpServerFacebook(facebookToken);
                    }
                }
                break;

            case R.id.back:
                findViewById(R.id.termsParent).setVisibility(View.GONE);
                findViewById(R.id.back).setVisibility(View.GONE);
                findViewById(R.id.loginSignupParent).setVisibility(View.VISIBLE);
                break;
        }
    }

    public void openWebView(String url) {
        Intent intent = new Intent(Login.this, webView.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    private void signUpMethod() {
        if ((!email.getText().toString().isEmpty()) && !(password.getText()
                .toString().isEmpty())) {
            if (CommonUtil.isValidEmail(email.getText().toString())) {
//                SignUpServer(email.getText().toString(), password.getText()
//                        .toString());
                findViewById(R.id.termsParent).setVisibility(View.VISIBLE);
                findViewById(R.id.loginSignupParent).setVisibility(View.GONE);
                findViewById(R.id.back).setVisibility(View.VISIBLE);
                fromSignUp = true;
            } /*else if (password.getText().toString().length() < MIN_PASSWORD_LENGTH) {
                CommonUtil.commonGameErrorDialog(Login.this,
                        "Password must be atleast " + MIN_PASSWORD_LENGTH + " characters long.");
            }*/ else {
                CommonUtil.commonGameErrorDialog(Login.this,
                        "Please enter valid email address.");
            }
        } else {
            CommonUtil.commonGameErrorDialog(Login.this,
                    "Please fill in all fields.");
        }
    }

    private void signUpView() {
//        email.setText("");
//        password.setText("");
        joinUsMessage.setVisibility(View.GONE);
        forgot.setVisibility(View.GONE);
        loginBTN.setVisibility(View.GONE);
        joinUs.setVisibility(View.GONE);
        //fbSignIn.setVisibility(View.GONE);
        fbSignIn.setText("Sign up with Facebook");
        signUp.setVisibility(View.VISIBLE);
        signInMessage.setVisibility(View.VISIBLE);
        findViewById(R.id.back).setVisibility(View.VISIBLE);
    }

    public void fbLogin() {
        session = new Session(Login.this);
        Session.setActiveSession(session);
        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_RAW_RESPONSES);
        //Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        Session.OpenRequest openRequest = null;

        openRequest = new Session.OpenRequest(Login.this);
        openRequest.setPermissions(Arrays.asList("email", "user_friends",
                "user_photos"));
        try {
            if (isSystemPackage(getPackageManager().getPackageInfo(
                    "com.facebook.katana", 0))) {
                openRequest.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
            } else {
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        openRequest.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
        openRequest.setCallback(new Session.StatusCallback() {

            @Override

            public void call(Session session, SessionState state,
                             Exception exception) {
                if (session.isOpened()) {

                    facebookToken = session.getAccessToken();
//                    Log.v("accessToken ", accessToken);
//                    SignUpServerFacebook(accessToken);
                    getFacebookLoginStatus(facebookToken);
                } else if (session.isClosed()) {
                    Log.v("session.isClosed ", "session.isClosed");
                }
            }

        });
        session.openForRead(openRequest);
    }

    /**
     * This method is used to know whether provided Package name is system or
     * user installed
     *
     * @param pkgInfo package info
     * @return
     */
    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
                : false;
    }

    public void SignUpServerFacebook(final String token) {
        if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                getApplicationContext())) {
            CommonUtil.noInternetDialog(Login.this);
            return;
        }

        CommonUtil.loading_box(this, "Please wait...");
        RequestParams rv = new RequestParams();
        rv.put("token", token);
        rv.put("apiKey", Data.apiKey);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.post(Data.baseUrl + "/user/authenticate/facebook", rv,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, String response) {

                        Log.v("response = ", response);
                        CommonUtil.loading_box_stop();
                        Data.AccessToken = response.substring(1,
                                response.length() - 1);
                        CommonUtil
                                .saveAccessToken(Data.AccessToken, Login.this);
                        Log.v("token = ", CommonUtil
                                .getAccessToken(getApplicationContext()));

//                        getFacebookLoginStatus(token);
                            getProfileData();
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.loading_box_stop();
                        CommonUtil.commonGameErrorDialog(Login.this,
                                "An error occurred. Please try later.");

                    }

                });
    }

    public void getProfileData() {

        if (!CommonUtil.is_loading_showing())
            CommonUtil.loading_box(this, "Please wait...");
        RequestParams rv = new RequestParams();

        rv.put("token", CommonUtil.getAccessToken(getApplicationContext()));
        rv.put("apiKey", Data.apiKey);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT * 2);

        client.get(Data.baseUrl + "userprofile", rv,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {

                        Log.v("response = ", response + ",");
                        CommonUtil.loading_box_stop();
                        try {
                            JSONObject js = new JSONObject(response);
                            Data.userName = js.getString("screenName");
                            CommonUtil.saveScreenName(
                                    js.getString("screenName"),
                                    getApplicationContext());

                            // js.getJSONObject("pictureFile").getString("fileUrl")
                            if (!js.isNull("pictureFile")) {

                                Data.userImageUrl = js.getJSONObject(
                                        "pictureFile").getString("fileUrl");
                            }
                            // js.getString("email");
                            Data.email = js.getString("email");
                            CommonUtil.push(Data.email);

                        } catch (Exception e) {
                            Log.v("response = ", e.toString());
                        }
                        Log.v("Data.userImageUrl",
                                Data.userImageUrl);

                        Intent i = new Intent(getApplication(),
                                ScreenMain.class);
                        startActivity(i);
                        finish();
                        /*overridePendingTransition(R.anim.from_right,
                                R.anim.to_left);*/
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.v("onFailure = ", e.toString());
                        CommonUtil.loading_box_stop();
                        CommonUtil.commonErrorDialog(Login.this, "Some error occured.");
//                        Intent i = new Intent(getApplicationContext(),
//                                Login.class);
//                        startActivity(i);
//                        finish();
                        /*overridePendingTransition(R.anim.from_right,
                                R.anim.to_left);*/

                    }

                });
    }


    public void SignUpServer(final String email, String password) {

        if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                getApplicationContext())) {
            CommonUtil.noInternetDialog(Login.this);
            return;
        }
        CommonUtil.loading_box(this, "Please wait...");
        RequestParams rv = new RequestParams();
        rv.put("email", email);
        rv.put("password", password);
        rv.put("apiKey", Data.apiKey);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.post(Data.baseUrl + "user", rv, new AsyncHttpResponseHandler() {

            @SuppressWarnings("deprecation")
            @Override
            public void onSuccess(String response) {
                Data.email = email;
                Log.v("response = ", response);
                CommonUtil.loading_box_stop();
                Data.AccessToken = response.substring(1, response.length() - 1);
                CommonUtil.saveAccessToken(Data.AccessToken,
                        getApplicationContext());
                Log.v("token = ",
                        CommonUtil.getAccessToken(getApplicationContext()));
                showDialog(DLG_EXAMPLE1);

                try {
                    if (response.contains("error")) {
                        CommonUtil.commonGameErrorDialog(Login.this,
                                "Something went wrong!!");
                    } else {
                        Intent i = new Intent(getApplicationContext(),
                                ScreenMain.class);
                        startActivity(i);
                        finish();
                        /*overridePendingTransition(R.anim.from_right,
                                R.anim.to_left);*/
                    }
                } catch (Exception e) {
                    CommonUtil.loading_box_stop();
                    CommonUtil.commonGameErrorDialog(Login.this,
                            response);
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable e, String r) {
                // Log.v("response = ", e.toString());
                if (e instanceof HttpResponseException) {
                    HttpResponseException hre = (HttpResponseException) e;
                    CommonUtil.loading_box_stop();
                    if (hre.getStatusCode() == 409)
                        CommonUtil
                                .commonGameErrorDialog(Login.this,
                                        "A user with that email address already exists.");
                    else
                        CommonUtil.commonGameErrorDialog(Login.this,
                                "An error occurred while signing you up.");

                } else {
                    CommonUtil.commonGameErrorDialog(Login.this,
                            "An error occurred while signing you up.");
                }
            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Session.getActiveSession().onActivityResult(this, requestCode,
                resultCode, data);
    }

    public void getFacebookLoginStatus(final String token) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        RequestParams rv = new RequestParams();
        rv.put("token", token);
        rv.put("apiKey", Data.apiKey);

        client.post(Data.baseUrl + "user/CheckIsthisFirstLogin/facebook", rv,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        if (response.contains("True")) {
                            SignUpServerFacebook(token);
                        } else {
//                            getProfileData();
                            findViewById(R.id.termsParent).setVisibility(View.VISIBLE);
                            findViewById(R.id.loginSignupParent).setVisibility(View.GONE);
                            findViewById(R.id.back).setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        if(e.getMessage().startsWith("Unautho")) {

                            findViewById(R.id.termsParent).setVisibility(View.VISIBLE);
                            findViewById(R.id.loginSignupParent).setVisibility(View.GONE);
                            findViewById(R.id.back).setVisibility(View.VISIBLE);
                        } else {
                            CommonUtil.commonErrorDialog(Login.this,"There was some problem while performing this operation.Please try again.");
                        }
                        Log.v("exception in get ads", e.toString());
                    }
                });
    }
}

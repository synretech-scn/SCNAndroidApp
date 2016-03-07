package com.tribaltech.android.scnstrikefirst;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.tribaltech.android.entities.Center;
import com.tribaltech.android.util.AppStatus;
import com.tribaltech.android.util.CircleTransform;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;

import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import rmn.androidscreenlibrary.ASSL;


public class EditProfile extends MenuIntent {
    final int PICK_FROM_CAMERA = 1;
    final int PICK_FROM_FILE = 2;
    public Bitmap bitmap;
    EditText firstName, lastName, screenName;
    TextView firstNameLabel, lastNameLabel, emailLabel, screenLabel,
            done, cancel, editPhoto, homeHeader;
    ImageView userImage;
    CenterFragment centerFragment;
    private Uri mImageCaptureUri;
    private File mFileTemp;
    String imageString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        new ASSL(EditProfile.this, (ViewGroup) findViewById(R.id.root), 1134, 720,
                false);

        initComponent();
        setValues();
        setParentTouch();
    }

    private void setValues() {
        Picasso.with(getApplicationContext())
                .load(Data.userImageUrl)
                .error(R.drawable.profile_icon_selector)
                .transform(new CircleTransform()).fit()
                .into(userImage);
//        email.setText(Data.email);
        screenName.setText(Data.userName);
        firstName.setText(Data.firstName);
        lastName.setText(Data.lastName);
    }

    private void initComponent() {
//        Typeface font_regular = Typeface.createFromAsset(this.getAssets(), "fonts/avenir_next.otf");
//        Typeface font_bold = Typeface.createFromAsset(this.getAssets(), "fonts/avenir_bold.otf");

        centerFragment = new CenterFragment();
        centerFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction()
                .add(R.id.centerfragmentParent, centerFragment).commit();
        centerFragment.load();

        mFileTemp = CommonUtil.getTempImageFile();

        done = (TextView) findViewById(R.id.done);
        cancel = (TextView) findViewById(R.id.cancel);
        firstNameLabel = (TextView) findViewById(R.id.first_name_label);
        emailLabel = (TextView) findViewById(R.id.email_label);
        lastNameLabel = (TextView) findViewById(R.id.last_name_label);
        screenLabel = (TextView) findViewById(R.id.screen_label);
        editPhoto = (TextView) findViewById(R.id.edit_photo);
        homeHeader = (TextView) findViewById(R.id.home);
        screenName = (EditText) findViewById(R.id.screen_name);
//        email = (EditText) findViewById(R.id.email);
        firstName = (EditText) findViewById(R.id.first_name);
        lastName = (EditText) findViewById(R.id.last_name);
        userImage = (ImageView) findViewById(R.id.profile_dp);

        firstName.setOnFocusChangeListener(this);
        lastName.setOnFocusChangeListener(this);

//        done.setTypeface(font_regular);
//        email.setTypeface(font_regular);
//        emailLabel.setTypeface(font_regular);
//        screenLabel.setTypeface(font_regular);
//        screenName.setTypeface(font_regular);
//        firstName.setTypeface(font_regular);
//        firstNameLabel.setTypeface(font_regular);
//        lastName.setTypeface(font_regular);
//        lastNameLabel.setTypeface(font_regular);
//        homeHeader.setTypeface(font_regular);
//        editPhoto.setTypeface(font_bold);
//        cancel.setTypeface(font_regular);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.done:
                Center center = centerFragment.getSelectedCenter();
                Data.center = center.name;
                Data.centerID = center.id;
                homeCenterSelection(Data.centerID);
                sendToServer();
                break;
            case R.id.cancel:
                finish();
                break;
            case R.id.edit_photo:
                openChooser();
//                showSelection(1);
                break;
            default:
                break;
        }
    }

    private void openChooser() {
        // This AlertDialog will notify the successful sign up
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);
        builder.setTitle("Choose Option:");
        builder.setMessage("Camera or Gallery?");
        builder.setCancelable(true);
        builder.setPositiveButton("Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        showSelection(0);
                    }
                });
        builder.setNegativeButton("Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        showSelection(1);
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showSelection(int idx) {
        Intent intent = null;
        if (idx == 0) {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            mImageCaptureUri = Uri
                    .fromFile(new File(android.os.Environment
                            .getExternalStorageDirectory(),
                            CommonUtil.GAME_IMAGE_NAME));

            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    mImageCaptureUri);

            try {
                intent.putExtra("return-data", true);

                startActivityForResult(intent, PICK_FROM_CAMERA);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        } else if (idx == 1) {
            intent = new Intent();

            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(
                    Intent.createChooser(intent, "Complete action using"),
                    PICK_FROM_FILE);
        }
    }

    private void saveInformation() {
//        Data.email = email.getText().toString();
        Data.userName = screenName.getText().toString();
        Data.firstName = firstName.getText().toString();
        Data.lastName = lastName.getText().toString();
        Data.country = centerFragment.getSelectedCountry();
    }

    private void sendToServer() {
//        if (!CommonUtil.isValidEmail(email.getText().toString())) {
//            CommonUtil.commonErrorDialog(EditProfile.this,
//                    "Enter a valid email!");
//            return;
//        }
        if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                getApplicationContext())) {
            CommonUtil.noInternetDialog(EditProfile.this);
            return;
        }

        if (!CommonUtil.is_loading_showing())
            CommonUtil.loading_box(this, "Please wait...");

        StringEntity entity = null;
        try {
            JSONObject json = new JSONObject();

            if (imageString != null && !imageString.isEmpty()) {
                JSONObject venue = new JSONObject();
                venue.put("content", imageString);
                json.put("base64Picture", venue);
            }
            json.put("email", Data.email);
            json.put("screenName", screenName.getText().toString());
            json.put("firstName", firstName.getText().toString());
            json.put("lastName", lastName.getText().toString());
            // json.put("token",
            // CommonUtil.getAccessToken(getApplicationContext()));
            // json.put("apiKey", Data.apiKey);

            entity = new StringEntity(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }


        RequestParams rv = new RequestParams();

        rv.put("token", CommonUtil.getAccessToken(getApplicationContext()));
        rv.put("apiKey", Data.apiKey);
        rv.put("screenName", screenName.getText().toString());
        rv.put("email", Data.email);
        rv.put("firstName", firstName.getText().toString());
        rv.put("lastName", lastName.getText().toString());
        if (imageString != null && !imageString.isEmpty()) {
            rv.put("base64Picture", imageString);
        }

        AsyncHttpClient client = new AsyncHttpClient();

        client.setTimeout(CommonUtil.TIMEOUT);
        client.post(EditProfile.this, Data.baseUrl + "userprofile?token=" +
                        CommonUtil.getAccessToken(getApplicationContext())
                        .replace("+", "%2B") +
                        "&apiKey=" + Data.apiKey, entity, "application/json",
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        Toast.makeText(EditProfile.this,
                                "Changes updated successfully", Toast.LENGTH_SHORT)
                                .show();
                        saveInformation();
                        finish();
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.loading_box_stop();

                        HttpResponseException hre = (HttpResponseException) e;
                        if (hre.getStatusCode() == 409) {
                            CommonUtil.commonErrorDialog(EditProfile.this,
                                    "This usename is already in use. " +
                                            "Please choose another name.");
                        } else
                            CommonUtil.commonGameErrorDialog(EditProfile.this,
                                    "An error occurred please try again." + hre.getMessage());
                    }

                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {

            case PICK_FROM_FILE:
                try {
                    InputStream inputStream = getContentResolver().openInputStream(
                            data.getData());
                    FileOutputStream fileOutputStream = new FileOutputStream(
                            mFileTemp);
                    CommonUtil.copyStream(inputStream, fileOutputStream);
                    fileOutputStream.close();
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ShowImage();
                break;

            case PICK_FROM_CAMERA:
                try {

//                    InputStream inputStream = getContentResolver().openInputStream(
//                            mImageCaptureUri);
//                    FileOutputStream fileOutputStream = new FileOutputStream(
//                            mFileTemp);
//                    CommonUtil.copyStream(inputStream, fileOutputStream);
//                    fileOutputStream.close();
//                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ShowImage();
                break;

        }
    }

    private void ShowImage() {
        CommonUtil.loading_box(EditProfile.this, "Please wait...");
        new DownloadWebPageTask().execute("");
    }

    public void uploadImage(String BASE64_image) {

        if (!AppStatus.getInstance(getApplicationContext()).isOnline(
                getApplicationContext())) {
            CommonUtil.noInternetDialog(EditProfile.this);
            return;
        }

        CommonUtil.loading_box(this, "Please wait...");
        RequestParams rv = new RequestParams();

        StringEntity entity = null;
        try {
            JSONObject venue = new JSONObject();
            venue.put("content", BASE64_image);

            JSONObject json = new JSONObject();
            json.put("base64Picture", venue);
            json.put("email", Data.email);
            json.put("screenName", Data.userName);
            // json.put("token",
            // CommonUtil.getAccessToken(getApplicationContext()));
            // json.put("apiKey", Data.apiKey);

            entity = new StringEntity(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // client.post(getApplicationContext(), Data.baseUrl
        // + "manuallanecheckout?token=" +
        // CommonUtil.getAccessToken(getApplicationContext()) + "&apiKey="
        // + Data.apiKey, entity, "application/json",
        // new AsyncHttpResponseHandler() {
        //
        //
        //
        //
        //
        // rv1.put("content", "raman");
        // rv.put("email", PreEmail);
        // rv.put("screenName", PreName);
        // rv.put("token", CommonUtil.getAccessToken(getApplicationContext()));
        // rv.put("apiKey", Data.apiKey);
        // // rv.put("base64Picture", j.toString());
        // Log.v("rv.toString = ", rv.toString());

        android.util.Log.v("string entity = ", entity.toString());
        AsyncHttpClient client = new AsyncHttpClient();

        //client.addHeader("base64Picture", j.toString());
        String url = AsyncHttpClient.getUrlWithQueryString(
                Data.baseUrl + "userprofile?token="
                        + CommonUtil.getAccessToken(getApplicationContext())
                        + "&apiKey=" + Data.apiKey, rv);
        client.setTimeout(CommonUtil.TIMEOUT);

        client.post(
                EditProfile.this,
                Data.baseUrl
                        + "userprofile?token="
                        + CommonUtil.getAccessToken(getApplicationContext())
                        .replace("+", "%2B") + "&apiKey=" + Data.apiKey,
                entity, "application/json", new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();

                        getProfileImage();

                        // if (Data.userBitmap == null) {
                        // UserImage
                        // .setImageBitmap(getRoundedCornerBitmap(BitmapFactory
                        // .decodeResource(getResources(),
                        // R.drawable.user_image)));
                        // userImageSmall
                        // .setImageBitmap(getRoundedCornerBitmap(BitmapFactory
                        // .decodeResource(getResources(),
                        // R.drawable.user_image)));
                        // }
                        //
                        // else {
                        // UserImage
                        // .setImageBitmap(getRoundedCornerBitmap(Data.userBitmap));
                        // userImageSmall
                        // .setImageBitmap(getRoundedCornerBitmap(Data.userBitmap));
                        // }

                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.loading_box_stop();
                        CommonUtil.commonGameErrorDialog(EditProfile.this,
                                "An error occurred. Please try later.");

                    }

                });

    }

    public void getProfileImage() {
        CommonUtil.loading_box(this, "Please wait...");
        RequestParams rv = new RequestParams();

        rv.put("token", CommonUtil.getAccessToken(getApplicationContext()));
        rv.put("apiKey", Data.apiKey);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);
        client.get(Data.baseUrl + "userprofile", rv,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {

                        CommonUtil.loading_box_stop();
                        try {
                            JSONObject js = new JSONObject(response);

                            Data.userName = js.getString("screenName");
                            CommonUtil.saveScreenName(
                                    js.getString("screenName"),
                                    getApplicationContext());

                            // js.getJSONObject("pictureFile").getString("fileUrl")
                            if (!js.isNull("pictureFile"))

                                Data.userImageUrl = js.getJSONObject(
                                        "pictureFile").getString("fileUrl");

                        } catch (Exception e) {

                        }
                        Picasso.with(getApplicationContext())
                                .load(Data.userImageUrl)
                                .error(R.drawable.profile_icon_selector)
                                .transform(new CircleTransform()).fit()
                                .into(userImage);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        CommonUtil.loading_box_stop();
                        Picasso.with(getApplicationContext())
                                .load(Data.userImageUrl)
                                .error(R.drawable.circular_white)
                                .transform(new CircleTransform()).fit()
                                .into(userImage);

                    }

                });
    }

    public void homeCenterSelection(int venueId) {
        RequestParams params = new RequestParams();

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(this, "Please wait...");
        }

        client.post(Data.baseUrl
                        + "MyCenter?token="
                        + CommonUtil.getAccessToken(getApplicationContext())
                        .replaceAll("[+]", "%2B") + "&apiKey=" + Data.apiKey
                        + "&venueId=" + venueId, params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        // CommonUtil.commonDialog(myProfile.this, null,
                        // "Changes saved.");
                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                        // CommonUtil.commonGameErrorDialog(myProfile.this,
                        // throwabl"An error occured.Please try again.");
                        // CommonUtil.loading_box_stop();
                    }
                });
    }


    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String encodedImage = "";
            try {

                bitmap = BitmapFactory.decodeFile(CommonUtil.getTempImageFile()
                        .getPath());

                bitmap = Bitmap.createScaledBitmap(bitmap, 400, (int) (400 * ((float) bitmap
                        .getWidth() / (float) bitmap.getHeight())), false);

                if (bitmap != null) {

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); // bm is
                    // the
                    // bitmap
                    // object
                    byte[] b = baos.toByteArray();
                    encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                    android.util.Log.v("base64 string = ", encodedImage);
                }
            } catch (Exception e) {

            }
            return encodedImage;
        }

        @Override
        protected void onPostExecute(String result) {
            imageString = result;

            userImage.setImageBitmap(CommonUtil.getRoundedShape(bitmap));
            // Toast.makeText(getApplicationContext(),
            // "image converted : "+result, 5000).show();
            CommonUtil.loading_box_stop();
//            uploadImage(result);
        }
    }
}

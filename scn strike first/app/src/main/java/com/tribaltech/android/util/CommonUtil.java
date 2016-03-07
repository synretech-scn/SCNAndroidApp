package com.tribaltech.android.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tribaltech.android.scnstrikefirst.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

import rmn.androidscreenlibrary.ASSL;

public class CommonUtil {

    public static final int TIMEOUT = 15000;
    private static ProgressDialog pd_st;
    static Field idField;
    public static String[] numberSuffix = {"", "st", "nd", "rd"};
    public static String GAME_IMAGE_NAME = "userImage.jpg";

    public static int getIdFromName(String variableName, Class<?> c) {
        try {
            idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static Calendar minusTime(int min) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, min);
        return c;
    }

    public static void saveScreenName(String userName, Context context) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = pref.edit();
        editor.putString("userName", userName);
        editor.commit();
        Data.userName = userName;
    }

    public static void hideSoftKeyboard(Activity activity, View view) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void commonErrorDialog(Context ctx, String msg) {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Error");
        builder.setMessage(msg).setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        dialog = builder.create();
        dialog.show();
    }

    public static void setListViewHeightBasedOnChildren(
            ListView completedGames, int height) {

        ListAdapter listAdapter = completedGames.getAdapter();
        ViewGroup.LayoutParams params = completedGames.getLayoutParams();
        params.height = (int) (listAdapter.getCount() * height * ASSL.Yscale());
        completedGames.setLayoutParams(params);
        completedGames.requestLayout();
    }

    public static void commonGameErrorDialog(Context ctx, String msg) {
        try {
            if (!((Activity) ctx).isFinishing()) {
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle("Error");
                builder.setMessage(msg).setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                // Create the AlertDialog object and return it
                dialog = builder.create();
                dialog.show();
            }
        } catch (Exception e) {

        }
    }

    public static Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 50;
        int targetHeight = 50;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }


    public static void commonDialog(Context ctx, String title, String msg, DialogInterface.OnClickListener positive) {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(msg).setPositiveButton("Ok", positive == null ?
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                } : positive);
        // Create the AlertDialog object and return it
        dialog = builder.create();
        dialog.show();
    }

    public static void commonDialog(Context ctx, String title, String msg) {
        commonDialog(ctx, title, msg, null);
    }

    public static String getScreenName(Context context) {
        if (Data.userName.isEmpty()) {
            SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(context);
            Data.userName = pref.getString("userName", "");
        }
        return Data.userName;
    }

    public static String toCamelCase(String s, String separator) {
        String[] parts = s.split(separator);
        String camelCaseString = "";
        for (String part : parts) {
            camelCaseString = camelCaseString + toProperCase(part) + separator;
        }
        return camelCaseString;
    }

    private static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    public static boolean isValidEmail(String target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
                    .matches();
        }
    }

    public static void saveAccessToken(String token, Context context) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = pref.edit();
        editor.putString("accessToken", token);
        editor.commit();
    }

    public static void saveFilter(FilterItem filter, Context context) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = pref.edit();
        editor.putString("timeDuration", filter.timeDuration);
        editor.putString("locationId", filter.locationId);
        editor.putString("oilPatternId", filter.oilPatternId);
        editor.putString("gameTypeId", filter.gameTypeId);
        editor.putString("patternLengthId", filter.patternLengthId);
        editor.putString("country", filter.country);
        editor.putString("state", filter.state);
        editor.putString("center", filter.center);
        editor.putString("tag", filter.tag);

        editor.commit();
    }

    public static FilterItem getFilter(Context context) {
//        saveFilter(new FilterItem(), context);
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return new FilterItem(pref.getString("timeDuration", ""),
                pref.getString("locationId", "0"), pref.getString(
                "oilPatternId", "0"),
                pref.getString("gameTypeId", "0"), pref.getString(
                "patternLengthId", "0"), pref.getString("country", ""),
                pref.getString("state", ""), pref.getString("center", ""), pref.getString("tag", ""));
    }

    public static String getAccessToken(Context context) {
        if (Data.AccessToken.isEmpty()) {
            SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(context);
            return pref.getString("accessToken", "");
        }
        return Data.AccessToken;
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length,
                options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options,
                (int) (720 * ASSL.Xscale()), (int) (1134 * ASSL.Xscale()));

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap original = BitmapFactory.decodeByteArray(decodedByte, 0,
                decodedByte.length, options);
        Bitmap resized = Bitmap.createScaledBitmap(original,
                (int) (720 * ASSL.Xscale()), (int) (543 * ASSL.Xscale()), true);

        // ByteArrayOutputStream blob = new ByteArrayOutputStream();
        // resized.compress(Bitmap.CompressFormat.JPEG, 100, blob);

        return resized;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static String getFilterString(FilterItem filterItem) {
        if (filterItem == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("&timeDuration=" + filterItem.timeDuration);
        builder.append("&location=" + filterItem.locationId);
        builder.append("&oilPattern=" + filterItem.oilPatternId);
        builder.append("&gameType=" + filterItem.gameTypeId);
        builder.append("&patternLength=" + filterItem.patternLengthId);
        if (!filterItem.tag.isEmpty()) {
            try {
                builder.append("&Tag=" + URLEncoder.encode(filterItem.tag, "utf-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (filterItem.timeDuration.equalsIgnoreCase("daily")) {
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date date = new Date();
            builder.append("&currentDate=" + dateFormat.format(date));
        }
        return builder.toString();
    }

    public static String getExpirationTimeTodisplay(String expirationTime) {

        expirationTime = expirationTime.replace("T", "");
        Calendar current = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            cal.setTime(sdf.parse(expirationTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diffInMilis = cal.getTimeInMillis() - current.getTimeInMillis();
        long diffInSecond = diffInMilis / 1000;
        long prefix;
        String suffix;
        if (diffInSecond >= 60) {
            long diffInMinute = diffInSecond / 60;
            if (diffInMinute >= 60) {
                long diffInHour = diffInMinute / 60;
                if (diffInHour >= 24) {
                    long diffInDays = diffInHour / 24;
                    if (diffInDays > 365) {
                        long diffInYears = diffInDays / 365;
                        prefix = diffInYears;
                        suffix = " year" + (diffInYears == 1 ? "" : "s");
                    } else {
                        prefix = diffInDays;
                        suffix = " day" + (diffInDays == 1 ? "" : "s");
                    }
                } else {
                    prefix = diffInHour;
                    suffix = " hr" + (diffInHour == 1 ? "" : "s");
                }
            } else {
                prefix = diffInMinute;
                suffix = " min" + (diffInMinute == 1 ? "" : "s");
            }
        } else {
            prefix = diffInSecond;
            suffix = " sec";
        }

        return prefix + suffix;
    }

    public static String getAds(Context context) {

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return pref.getString("adsJSON", "");

    }

    public static void push(String Email) {

        RequestParams rv = new RequestParams();
        rv.put("Email", Email);
        rv.put("devicetype", "1");
        rv.put("deviceToken", Data.regid);

        AsyncHttpClient client = new AsyncHttpClient();

        client.setTimeout(100000);
        client.post(Data.baseUrl + "user/savedevicetoken", rv,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {

                    }

                    @Override
                    public void onFailure(Throwable e) {
                    }

                });
        String params=String.format("?apiKey=%s&token=%s&venueId=%s&deviceType=%s&deviceToken=%s",Data.apiKey,Data.AccessToken.replaceAll("[+]","%2B"),"15103","1",Data.regid);

        AsyncHttpClient venueclient = new AsyncHttpClient();

        venueclient.setTimeout(100000);
        venueclient.post(Data.baseUrl + "user/SaveVenueDeviceToken" + params, null,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {

                    }

                    @Override
                    public void onFailure(Throwable e) {
                    }

                });
        //http://api.xbowling.com/User/SaveVenueDeviceToken?venueId={venueId}&deviceType={type}&deviceToken={deviceToken}&apiKey={apiKey}&token={token}

    }

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range
            // under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF)
                newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static void loading_box_stop() {
        if (pd_st != null) {
            try {
                if (pd_st.isShowing()) {
                    pd_st.dismiss();
                }
            } catch (Exception e) {
                pd_st = null;
            }
        }

    }

    public static Boolean is_loading_showing() {
        if (pd_st != null)
            if (pd_st.isShowing())
                return true;
        return false;

    }

    public static void loading_box(Context c, String msg) {
        if (!((Activity) c).isFinishing()) {
            try {
                pd_st = new ProgressDialog(c,
                        android.R.style.Theme_Translucent_NoTitleBar);
                // pd_st.getWindow().setWindowAnimations(
                // R.style.Animations_progressFadeInOut);
                pd_st.show();
                pd_st.setCanceledOnTouchOutside(false);

                // pd1_static.requestWindowFeature();

                pd_st.setContentView(R.layout.loading_box);
                pd_st.setCancelable(false);
                // FrameLayout fl1 = (FrameLayout) pd_st.findViewById(R.id.rv);
                // new AndroidScreenSize(c, fl1, 800, 480);

                TextView t1 = (TextView) pd_st.findViewById(R.id.loadtext);

                // t1.setTypeface(CommonUtil.customFontSemibold);
                t1.setText(msg);

                // pd_st.findViewById(R.id.rlt).setAlpha(0.9f);
                ((RelativeLayout) pd_st.findViewById(R.id.rlt)).setAlpha(0.9f);
            } catch (Exception e) {
            }
        }
    }

    public static void noInternetDialog(Context ctx) {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage("Please check your internet connection.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        dialog = builder.create();
        dialog.show();

        // Intent i = new Intent(ctx, offline.class);
        // ctx.startActivity(i);

        // overridePendingTransition(R.anim.from_top, R.anim.hold);

    }

    public static File getTempImageFile() {
        // File cacheDir = new File(
        // android.os.Environment.getExternalStorageDirectory(),
        // GAME_IMAGE_DIR);
        File file = new File(
                android.os.Environment.getExternalStorageDirectory(),
                GAME_IMAGE_NAME);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    public static void saveAds(String adsJson, Context context) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = pref.edit();
        editor.putString("adsJSON", adsJson);
        editor.commit();
    }
}

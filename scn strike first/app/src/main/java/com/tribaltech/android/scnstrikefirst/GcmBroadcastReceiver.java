/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tribaltech.android.scnstrikefirst;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;


/**
 * This {@code WakefulBroadcastReceiver} takes care of creating and managing a
 * partial wake lock for your app. It passes off the work of processing the GCM
 * message to an {@code IntentService}, while ensuring that the device does not
 * go back to sleep in the transition. The {@code IntentService} calls
 * {@code GcmBroadcastReceiver.completeWakefulIntent()} when it is ready to
 * release the wake lock.
 */

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;
    NotificationCompat.Builder builder;

    public static final String TAG = "GCM Demo";

    @Override
    public void onReceive(Context context, final Intent intent) {
        if (Data.currentContext != null) {
            Handler mHandler = new Handler(context.getMainLooper());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    CommonUtil.commonDialog(Data.currentContext, null,
                            intent.getStringExtra("message"));
                }
            });
        } else {
            notificationManager(context, intent.getStringExtra("message"));
        }

    }


    private void notificationManager(Context context, String message)

    {
        long when = System.currentTimeMillis();

        NotificationManager notificationManager = (NotificationManager) context

                .getSystemService(Context.NOTIFICATION_SERVICE);

        @SuppressWarnings("deprecation")
        Notification notification = new Notification(R.drawable.app_icon,

                "XBowling", when);
        notification.sound = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notification.vibrate = new long[] { 1000, 1000, 1000, 1000, 1000 };
        String title = "XBowling";

        // Intent notificationIntent = new Intent(context,

        // UserHome.class);
        //
        // PackageManager pm = getPackageManager();
        // Intent notificationIntent = pm
        // .getLaunchIntentForPackage(getApplicationContext()
        // .getPackageName());
        //
        // // set intent so it does not start a new activity
        //
        // notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
        //
        // | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        Intent notificationIntent = new Intent(context, Notifications.class);
        notificationIntent.putExtra("notification", message);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // | Intent.FLAG_ACTIVITY_NEW_TASK);
        // Data.notification();
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        PendingIntent intent = PendingIntent.getActivity(context, 0,

                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(context, title, message, intent);

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, notification);

    }


}

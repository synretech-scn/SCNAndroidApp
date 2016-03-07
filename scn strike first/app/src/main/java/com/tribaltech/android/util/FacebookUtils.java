package com.tribaltech.android.util;

/**
 * This file is used for all constant value for twitter functioning. 
 * 
 * Project Name: - Fitmojis
 * Developed by ClickLabs. Developer: Ashish
 * Link: http://www.click-labs.com/
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class FacebookUtils implements StatusCallback {

	private static String email;
	// FACEBOOK WORK
	private static SharedPreferences prefs;
	private static StatusCallback scb;

	public static void checkValidation(final int value, final Activity c) {

		scb = (StatusCallback) c;
		if (Session.getActiveSession() == null) {
			fbLogin(value, c);
		} else {
			getUserData(value, c);
		}
	}

	// 0 for login || 1 for share || 2 for apprequest
	public static void fbLogin(final int value, final Activity c) {
		prefs = PreferenceManager.getDefaultSharedPreferences(c);
//		scb = (StatusCallback) c;
 
		 Session session = new Session(c);
		 
		 if(!session.isClosed())
		 {
		 session.closeAndClearTokenInformation();
		 session = new Session(c);
		 Session.setActiveSession(session);
		 }
		
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

		if (value != 0) {
			getUserData(value, c);
		} else {
			Session.OpenRequest openRequest = null;
			openRequest = new Session.OpenRequest(c);

			openRequest.setPermissions(Arrays.asList("email", "user_photos","user_friends"));

			openRequest.setCallback(new StatusCallback() {
				@Override
				public void call(Session session, SessionState state,
						Exception exception) {
					if (session.isOpened()) {
						getUserData(value, c);
					} else if (session.isClosed()) {
						Log.e("error login ", "error login");
					}
				}
			});
			session.openForRead(openRequest);
		}

	}

	public static void getUserData(final int value, final Activity c) {
		Session session = new Session(c);
		Session.setActiveSession(session);
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
		Session.openActiveSession(c, true, new StatusCallback() {
			@Override
			public void call(final Session session, SessionState state,
					Exception exception) {
				Log.v("app id", session.getApplicationId() + ",");

				if (session.isOpened()) {
                    final String fbaccesstoken = session.getAccessToken();
                } else {
					// Toast.makeText(c, "Session Expired", 1000).show();
				}
			}
		});
	}


	private static void postToFb(final Activity c, boolean picshare) {
		Session session = Session.getActiveSession();
		if (session == null) {
			session = new Session(c);

			// Check if there is an existing token to be
			// migrated
			if (prefs.getString("fbToken", "none") != null
					&& !prefs.getString("fbToken", "none").equalsIgnoreCase(
							"none")) {

				Log.v("hello in active section", "hello");
				AccessToken accessToken = AccessToken
						.createFromExistingAccessToken(
								prefs.getString("fbToken", "none"), null, null,
								null, null);

				session.open(accessToken, scb);
				Session.setActiveSession(session);
			} else {
				Log.v("hello in else ", "hello in else");
			}
		}

		Bundle b = new Bundle();

		b.putString("name", "Hytch!");
		b.putString("description", "Check this out: ");
		String link;

		link = "link";
		b.putString("link", link);

		WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(c,
				Session.getActiveSession(), b)).setOnCompleteListener(
				new OnCompleteListener() {

					@Override
					public void onComplete(Bundle values,
							FacebookException error) {

						if (error == null) {
							// When the story is posted,
							// echo the
							// success
							// and the post Id.
							final String postId = values.getString("post_id");
							if (postId != null) {

								String msg = "Successfully shared on your wall! ";


							} else {
								// User clicked the
								// Cancel button
								Toast.makeText(c, "Publish cancelled",
										Toast.LENGTH_SHORT).show();
							}
						} else if (error instanceof FacebookOperationCanceledException) {

							// User clicked the "x"
							// button
							Toast.makeText(c, "Publish cancelled",
									Toast.LENGTH_SHORT).show();
						} else {
							// Generic, ex: network
							// error
							Toast.makeText(c, "Error posting story",
									Toast.LENGTH_SHORT).show();
						}
					}

				}).build();
		feedDialog.show();

	}

	/**
	 * Makes a request for user's photo albums from Facebook Graph API
	 * 
	 * @param session
	 */
	public static void fetchAlbumsFromFB(Session session) {

		// callback after Graph API response with user object
		Request.GraphUserCallback graphUserCallback;
		graphUserCallback = new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser user, Response response) {
				JSONObject jsonObject = null;
				if (user != null)
					jsonObject = user.getInnerJSONObject();

				Log.e("albumsResponse", response.toString());
				// ImageAdapter.getInstance().setPhotoAlbums(jsonObject);
			}
		};

		// assign callback to final instance variable in inner class
		final Request.GraphUserCallback finalCallback = graphUserCallback;
		Request.Callback wrapperCallback = new Request.Callback() {
			@Override
			public void onCompleted(Response response) {
				finalCallback.onCompleted(
						response.getGraphObjectAs(GraphUser.class), response);
			}
		};

		// make a new async request
		Bundle params = new Bundle();
		params.putString("fields", "photos");
		Request request = new Request(session, "me/albums", params, null,
				wrapperCallback);
		request.executeAsync();
	}

	@Override
	public void call(Session session, SessionState state, Exception exception) {
	}

	public static void sendRequestDialog(final Activity c) {

		Session session = Session.getActiveSession();
		if (session == null) {
			session = new Session(c);

			// Check if there is an existing token to be
			// migrated
			if (prefs.getString("fbToken", "none") != null
					&& !prefs.getString("fbToken", "none").equalsIgnoreCase(
							"none")) {

				Log.v("hello in active section", "hello");
				AccessToken accessToken = AccessToken
						.createFromExistingAccessToken(
								prefs.getString("fbToken", "none"), null, null,
								null, null);

				session.open(accessToken, scb);
				Session.setActiveSession(session);
			} else {
				Log.v("hello in else ", "hello in else");
			}
		}

		Bundle params = new Bundle();
		params.putString("message", "Hytch request");

		WebDialog requestsDialog = (new WebDialog.RequestsDialogBuilder(c,
				Session.getActiveSession(), params)).setOnCompleteListener(
				new OnCompleteListener() {

					@Override
					public void onComplete(Bundle values,
							FacebookException error) {

						if (error != null) {

							Log.e("error", error.toString());

							if (error instanceof FacebookOperationCanceledException) {
								Toast.makeText(c.getApplicationContext(),
										"Request cancelled", Toast.LENGTH_SHORT)
										.show();
							} else {
								Toast.makeText(c.getApplicationContext(),
										"Network Error", Toast.LENGTH_SHORT)
										.show();
							}
						} else {

							Log.e("values", values.toString());

							final String requestId = values
									.getString("request");
							if (requestId != null) {
								Toast.makeText(c.getApplicationContext(),
										"Request sent", Toast.LENGTH_SHORT)
										.show();
							} else {
								Toast.makeText(c.getApplicationContext(),
										"Request cancelled", Toast.LENGTH_SHORT)
										.show();
							}
						}
					}

				}).build();
		requestsDialog.show();
	}



}
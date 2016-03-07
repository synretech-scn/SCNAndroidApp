package com.tribaltech.android.util;

import java.util.Arrays;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.Builder;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class FacebookLoginHelper {

	public FacebookLoginHelper(){
//		USER_DATA = null;
	}
	
	private Session session;
	
//	public static FacebookUserData USER_DATA;
	
	public void openFacebookSessionForPublish(final Activity activity, final FacebookLoginCallback facebookLoginCallback, final boolean fetchFBData) {
		session = new Session(activity);
		Session.setActiveSession(session);
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_RAW_RESPONSES);

		Session.OpenRequest openRequest = null;
		openRequest = new Session.OpenRequest(activity);
		openRequest.setPermissions(Arrays.asList("publish_actions"));
		openRequest.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);

		openRequest.setCallback(new Session.StatusCallback() {
			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				Log.i("openRequest session", "=" + session);
				Log.i("openRequest state", "=" + state);
				Log.e("openRequest exception", "=" + exception);
				if (session.isOpened()) {
					Session.setActiveSession(session);
                    facebookLoginCallback.facebookLoginDone();
//					callRequestMeAsync(session, activity, facebookLoginCallback, fetchFBData);
				}
			}
		});
		session.openForPublish(openRequest);
	}
	
	public void openFacebookSession(final Activity activity, final FacebookLoginCallback facebookLoginCallback, final boolean fetchFBData) {
		session = Session.getActiveSession();
		Log.i("session", "=" + session);
		if (session == null) {
			callOpenActiveSession(activity, facebookLoginCallback, fetchFBData);
		} else {
			if (session.getState() == SessionState.OPENED
					|| session.getState() == SessionState.OPENED_TOKEN_UPDATED) {
				callRequestMeAsync(session, activity, facebookLoginCallback, fetchFBData);
			} else {
				Session.setActiveSession(session);
				session.closeAndClearTokenInformation();
				callOpenActiveSession(activity, facebookLoginCallback, fetchFBData);
			}
		}
	}
	
	
	public void callOpenActiveSession(final Activity activity, final FacebookLoginCallback facebookLoginCallback, 
			final boolean fetchFBData){
		
		session = new Session(activity);
		Session.setActiveSession(session);
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_RAW_RESPONSES);

		Session.OpenRequest openRequest = null;
		openRequest = new Session.OpenRequest(activity);
		openRequest.setPermissions(Arrays.asList("email"));
		openRequest.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);

		openRequest.setCallback(new Session.StatusCallback() {
			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				Log.i("openRequest session", "=" + session);
				Log.i("openRequest state", "=" + state);
				Log.e("openRequest exception", "=" + exception);
				if (session.isOpened()) {
					Session.setActiveSession(session);
					callRequestMeAsync(session, activity, facebookLoginCallback, fetchFBData);
				}
			}
		});
		session.openForRead(openRequest);
		
	}
	
	public void callRequestMeAsync(Session session, final Activity activity, final FacebookLoginCallback facebookLoginCallback, 
			final boolean fetchFBData){
		final String accessToken = session.getAccessToken();
		if(fetchFBData){
			Data.fbAccessToken = session.getAccessToken();
			showLoadingDialog(activity, "Loading...");
			Request.newMeRequest(session, new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) { // fetching user data from FaceBook
						dismissLoadingDialog();
						if (user != null) {
							Log.i("res", "="+response);
							Log.i("user", "=" + user);
							
//							Data.fbId = user.getId();
//							Data.fbFirstName = user.getFirstName();
//							Data.fbLastName = user.getLastName();
//							Data.fbUserName = user.getUsername();
							
							String fbId = user.getId();
							String firstName = user.getFirstName();
							String lastName = user.getLastName();
							String userName = user.getUsername();
							
							String userEmail = "";
							try {
//								Data.fbUserEmail = ((String)user.asMap().get("email"));
								userEmail = ((String)user.asMap().get("email"));
							} catch (Exception e2) {
								e2.printStackTrace();
							}
							finally{
								if(userEmail == null || "".equalsIgnoreCase(userEmail)){
									if(userName != null && !"".equalsIgnoreCase(userName)){
										userEmail = userName + "@facebook.com";
//										Data.fbUserEmail = Data.fbUserName + "@facebook.com";
									}
									else{
										userEmail = fbId + "@facebook.com";
//										Data.fbUserEmail = Data.fbId + "@facebook.com";
									}
								}
							}
							
							if(userName == null){
								userName = "";
							}
							
//							USER_DATA = new FacebookUserData(accessToken, fbId, firstName, lastName, userName, userEmail);
//							Log.e("USER_DATA", "="+USER_DATA);
						}
						else{
							Log.e("Facebook Error", "Error in fetching information from Facebook.");
						}
						facebookLoginCallback.facebookLoginDone();
					}
				}).executeAsync();
		}
		else{
//			USER_DATA = new FacebookUserData(accessToken, "", "", "", "", "");
			facebookLoginCallback.facebookLoginDone();
		}
	}
	
	
	
	public void openAppInviteDialog(final Activity activity){
		
		Bundle parameters = new Bundle();
		parameters.putString("message", "Download app now to get started. Available on Google Play Store and App Store");
		parameters.putString("data", "Get from one place to another with ease.");
		parameters.putString("link", "https://play.google.com/store/apps/details?id=com.tribaltech.android.scnstrikefirst");

		Builder builder = new Builder(activity, Session.getActiveSession(), "apprequests", parameters);

		builder.setOnCompleteListener(new OnCompleteListener() {

		    @Override
		    public void onComplete(Bundle values, FacebookException error) {
		    	Log.e("values","="+values);
		    	Log.e("error","="+error);
		        if (error != null){
		        }
		        else{
		            final String requestId = values.getString("request");
		            if (requestId != null) {
		            	Toast.makeText(activity, "Friends invited successfully.", Toast.LENGTH_SHORT).show();
		            } 
		            else {
		                Toast.makeText(activity, "Cancelled", Toast.LENGTH_SHORT).show();
		            }
		        }                       
		    }
		});

		WebDialog webDialog = builder.build();
		webDialog.show();
	        
	}
	
	
	public void shareMessage(final Activity activity, String shareString){
		showLoadingDialog(activity, "Sharing...");
		Bundle parameters = new Bundle();
		parameters.putString("message", shareString);
//		parameters.putString("name", "Get amazing offers and discounts at your favorite restaurants");
//		parameters.putString("title", "Get amazing offers and discounts at your favorite restaurants");
//		parameters.putString("picture", "http://54.81.229.172/Bistro/api/v1/assets/images/1200X627.jpg");
//		parameters.putString("link", "http://tablabar.s3.amazonaws.com/user_profile/d5ae7fa64f58083b618891b3a0a514da.png");
//		parameters.putString("caption", "Download app now to get started. Available on Google Play Store and App Store");


		Request request = new Request(Session.getActiveSession(), "me/feed", parameters, HttpMethod.POST);
		request.setCallback(new Request.Callback() {
			@Override
			public void onCompleted(Response response) {
				if (response.getError() == null) {
					Toast.makeText(activity, "Shared successfully", Toast.LENGTH_SHORT).show();
				}
				else{
					Toast.makeText(activity, ""+response.getError().getErrorMessage(), Toast.LENGTH_SHORT).show();
				}
				dismissLoadingDialog();
				Log.e("Tests", "got response: " + response);
			}
		});
		request.executeAsync();
	        
	}
	
public void publishFeedDialog(final Activity activity, String shareString, String shareString2) {
		
		//http://i58.tinypic.com/db9j8.png
		
	    Bundle params = new Bundle();
	    params.putString("name", "TaxiMust");
	    params.putString("caption", shareString);
	    params.putString("description", shareString2);
	    params.putString("link", "http://smarturl.it/taximust");
	    params.putString("picture", "http://i59.tinypic.com/qqv2xe.jpg");

	    WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(activity, Session.getActiveSession(), params))
	        .setOnCompleteListener(new OnCompleteListener() {

	            @Override
	            public void onComplete(Bundle values,
	                FacebookException error) {
	                if (error == null) {
	                    final String postId = values.getString("post_id");
	                    if (postId != null) {
	                        Toast.makeText(activity, "Posted successfully", Toast.LENGTH_SHORT).show();
	                    } else {
	                        Toast.makeText(activity.getApplicationContext(), "Publish cancelled", Toast.LENGTH_SHORT).show();
	                    }
	                } else if (error instanceof FacebookOperationCanceledException) {
	                    Toast.makeText(activity.getApplicationContext(), "Publish cancelled", Toast.LENGTH_SHORT).show();
	                } else {
	                    Toast.makeText(activity.getApplicationContext(), "Error posting story", Toast.LENGTH_SHORT).show();
	                }
	            }

	        })
	        .build();
	    feedDialog.show();
	}
	
	
	public ProgressDialog progressDialog;
	public void showLoadingDialog(Activity activity, String message){
		try {
			dismissLoadingDialog();
			progressDialog = ProgressDialog.show(activity, "", message, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void dismissLoadingDialog(){
		try {
			if(progressDialog != null && progressDialog.isShowing()){
				progressDialog.dismiss();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void logoutFacebook(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					if(Session.getActiveSession() != null){
						Session.getActiveSession().closeAndClearTokenInformation();
					}
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		}).start();
	}
	
}

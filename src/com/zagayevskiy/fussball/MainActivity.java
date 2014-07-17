package com.zagayevskiy.fussball;

import java.io.IOException;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.zagayevskiy.fussball.api.ApiConnection;
import com.zagayevskiy.fussball.api.ApiConnection.IBindUnbindListener;
import com.zagayevskiy.fussball.api.IApiManager;
import com.zagayevskiy.fussball.tabs.TabsPagerAdapter;
import com.zagayevskiy.fussball.utils.C;

public class MainActivity extends FragmentActivity implements IBindUnbindListener, IApiManager {
	
	private static final String TAG = MainActivity.class.getName();
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1;
	private static final String PROPERTY_REG_ID = "regid";
	private static final String PROPERTY_APP_VERSION = "appver";
	private ApiConnection mApi;
	
	private SharedPreferences mPrefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main_activity);
        
		ViewPager pager = (ViewPager) findViewById(R.id.pager);
		ActionBar actionBar = getActionBar();
		TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(actionBar, getSupportFragmentManager(), pager);
		pager.setAdapter(tabsPagerAdapter);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		tabsPagerAdapter
    		.addTab(actionBar.newTab().setText(R.string.tab_players), PlayersFragment.class)
    		.addTab(actionBar.newTab().setText(R.string.tab_games), GamesFragment.class)
			;		
		
		mPrefs = getSharedPreferences(C.prefs.NAME, Context.MODE_PRIVATE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		int code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (code != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(code)) {
	            GooglePlayServicesUtil.getErrorDialog(code, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        }else{
	        	Toast.makeText(this, "Device no support push notifications", Toast.LENGTH_SHORT).show();
	        }
	    }else{
	    	Toast.makeText(this, "Play supported!", Toast.LENGTH_SHORT).show();
	    	
            String regid = getRegistrationId(getApplicationContext());

            if (regid.isEmpty()) {
                registerInBackground();
            }
	    	
	    }
	}
	
	private String getRegistrationId(Context context) {
	    final SharedPreferences prefs = getSharedPreferences(C.prefs.NAME, Context.MODE_PRIVATE);
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	        Log.i(TAG, "Registration not found.");
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        Log.i(TAG, "App version changed.");
	        return "";
	    }
	    return registrationId;
	}
	
	private static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}
	
	private void registerInBackground() {
	    new AsyncTask<Void, Void, String>() {
	        @Override
	        protected String doInBackground(Void... params) {
	            String msg = "";
	            try {
	            	
	                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
	               
	                String regid = gcm.register("1006493293326");
	                msg = "Device registered, registration ID=" + regid;

//	                // You should send the registration ID to your server over HTTP,
//	                // so it can use GCM/HTTP or CCS to send messages to your app.
//	                // The request to your server should be authenticated if your app
//	                // is using accounts.
//	                sendRegistrationIdToBackend();
//
//	                // For this demo: we don't need to send it because the device
//	                // will send upstream messages to a server that echo back the
//	                // message using the 'from' address in the message.
//
//	                // Persist the regID - no need to register again.
//	                storeRegistrationId(context, regid);
	            } catch (IOException ex) {
	                msg = "Error :" + ex.getMessage();
	                // If there is an error, don't just keep trying to register.
	                // Require the user to click a button again, or perform
	                // exponential back-off.
	            }
	            return msg;
	        }

	        @Override
	        protected void onPostExecute(String msg) {
	        	 Toast.makeText(MainActivity.this,msg, Toast.LENGTH_LONG).show();
	        	 Log.i(TAG, msg);
	        }
	    }.execute();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		mApi = new ApiConnection(this, this);
		mApi.bind();
	}
	
	@Override
	protected void onStop() {
		mApi.unbind();
		super.onStop();		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		
		if(requestCode == PLAY_SERVICES_RESOLUTION_REQUEST){
		}
		
	}
	
	@Override
	public void onApiBind() {
		
	}

	@Override
	public void onApiUnbind() {
	}
	
	@Override
	public ApiConnection getApi(){
		return mApi;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}
	
}

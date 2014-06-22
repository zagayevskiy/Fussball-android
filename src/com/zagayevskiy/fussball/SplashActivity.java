package com.zagayevskiy.fussball;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.zagayevskiy.fussball.api.ApiService;
import com.zagayevskiy.fussball.utils.C;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startService(new Intent(this, ApiService.class));
		SharedPreferences prefs = getSharedPreferences(C.prefs.NAME, MODE_PRIVATE);
		
		if("".equals(prefs.getString(C.prefs.key.ACCESS_TOKEN, ""))){
			startActivity(new Intent(this, AuthActivity.class));
			finish();
		}else{
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
	}
}

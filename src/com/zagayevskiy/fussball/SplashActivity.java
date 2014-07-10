package com.zagayevskiy.fussball;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.zagayevskiy.fussball.api.ApiService;
import com.zagayevskiy.fussball.api.Token;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Crashlytics.start(this);
		startService(new Intent(this, ApiService.class));
		
		if(Token.getInstance().hasToken(this)){
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}else{
			startActivity(new Intent(this, AuthActivity.class));			
			finish();
		}
	}
}

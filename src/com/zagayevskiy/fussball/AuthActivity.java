package com.zagayevskiy.fussball;

import org.apache.http.Header;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.zagayevskiy.fussball.api.ApiConnection;
import com.zagayevskiy.fussball.api.ApiConnection.IBindUnbindListener;
import com.zagayevskiy.fussball.utils.HttpHelper.IHttpEventsListener;
import com.zagayevskiy.fussball.utils.C;

public class AuthActivity extends FragmentActivity implements TextWatcher, IBindUnbindListener, IHttpEventsListener {
	
	public static final String TAG = AuthActivity.class.getSimpleName();
	
	private static final int AUTH_REQUEST = 1;
	private static final int LOAD_PLAYERS_REQUEST = 2;
	
	private EditText mAuthEmail, mAuthPassword;
	private View mButtonOk;
	
	private ProgressDialog mProgressDialog;
	
	private SharedPreferences mPrefs;
	
	private ApiConnection mApi;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.auth_activity);
		
		mAuthEmail = (EditText) findViewById(R.id.auth_email);
		mAuthPassword = (EditText) findViewById(R.id.auth_password);
		mButtonOk = findViewById(R.id.auth_ok);
		
		mAuthEmail.addTextChangedListener(this);
		mAuthPassword.addTextChangedListener(this);
		
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setCancelable(false);
		
		mPrefs = getSharedPreferences(C.prefs.NAME, MODE_PRIVATE);
		
		mButtonOk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mProgressDialog.setTitle(R.string.auth_in_progress);
				mProgressDialog.show();
				
				final String emailStr = mAuthEmail.getText().toString();
				
				HttpGet get = new HttpGet(C.api.url.LOGIN);
				final Header authHeader = BasicScheme.authenticate(
										new UsernamePasswordCredentials(
												emailStr,
												mAuthPassword.getText().toString()), HTTP.UTF_8, false);
				get.addHeader(authHeader);
				
				mApi.httpRequest(AuthActivity.this, get, AUTH_REQUEST);
				
			}
		});
		
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		mApi = new ApiConnection(this, this);
		mApi.bind();
		checkCanAuth();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		mApi.unbind();
	}

	@Override
	public void afterTextChanged(Editable s) {
		checkCanAuth();
	}


	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}


	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}
	
	private void checkCanAuth(){
		boolean canAuth = true;
		
		canAuth &= mAuthEmail.getText().length() != 0;
		canAuth &= mAuthPassword.getText().length() != 0;
		canAuth &= mApi.isBound();
		
		mButtonOk.setEnabled(canAuth);
	}


	@Override
	public void onApiBind() {
		checkCanAuth();
	}


	@Override
	public void onApiUnbind() {
		checkCanAuth();
	}
	
	public ApiConnection getApi(){
		return mApi;
	}

	@Override
	public void onHttpProgressUpdate(int requestId, Integer... progress) {
	}

	@Override
	public void onHttpResponse(int requestId, String result) {
		if(requestId == AUTH_REQUEST){
			mProgressDialog.hide();
			try{
				JSONObject json = new JSONObject(result);
				if(json.has(C.api.json.key.ACCESS_TOKEN)){
					final String token = json.getString(C.api.json.key.ACCESS_TOKEN);
					Editor editor = mPrefs.edit();					
					editor.putString(C.prefs.key.ACCESS_TOKEN, token);					
					editor.commit();
					Log.i(TAG, "auth success:" + token);
					mApi.loadPlayers(this, LOAD_PLAYERS_REQUEST);					
				}else{
					Toast.makeText(this, json.getString(C.api.json.key.MESSAGE), Toast.LENGTH_SHORT).show();
				}
			}catch(JSONException e){
				Log.e(TAG, "JSONException", e);
				Toast.makeText(this, "Auth failed: can not parse json", Toast.LENGTH_SHORT).show();
			}
		}
		if(requestId == LOAD_PLAYERS_REQUEST){
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
	}

	@Override
	public void onHttpRequestFail(int requestId, Exception ex) {
		if(requestId == AUTH_REQUEST || requestId == LOAD_PLAYERS_REQUEST){
			mProgressDialog.hide();
			Toast.makeText(this, "Auth failed: can not connect to server", Toast.LENGTH_SHORT).show();
		}
	}
}

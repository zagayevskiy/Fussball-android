package com.zagayevskiy.fussball;

import org.apache.http.Header;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.zagayevskiy.fussball.service.ApiConnection;
import com.zagayevskiy.fussball.service.ApiConnection.IBindUnbindListener;
import com.zagayevskiy.fussball.utils.C;
import com.zagayevskiy.fussball.utils.HttpHelper.IHttpEventsListener;

public class AuthActivity extends Activity implements TextWatcher, IBindUnbindListener, IHttpEventsListener {
	
	public static final String TAG = AuthActivity.class.getSimpleName();
	
	private static final int AUTH_REQUEST = 1;
	
	private EditText email, password;
	private View buttonOk;
	
	private ProgressDialog progressDialog;
	
	private SharedPreferences prefs;
	
	private ApiConnection api;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.auth_activity);
		
		email = (EditText) findViewById(R.id.email);
		password = (EditText) findViewById(R.id.password);
		buttonOk = findViewById(R.id.ok);
		
		email.addTextChangedListener(this);
		password.addTextChangedListener(this);
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		
		prefs = getSharedPreferences(C.prefs.NAME, MODE_PRIVATE);
		
		buttonOk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				progressDialog.setTitle(R.string.auth_in_progress);
				progressDialog.show();
				
				HttpGet get = new HttpGet("http://212.154.168.144:5050/api/login");
				final Header authHeader = BasicScheme.authenticate(
										new UsernamePasswordCredentials(
												email.getText().toString(),
												password.getText().toString()), HTTP.UTF_8, false);
				get.addHeader(authHeader);
				
				api.httpRequest(AuthActivity.this, get, AUTH_REQUEST);
				
			}
		});
		
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		api = new ApiConnection(this, this);
		api.bind();
		checkCanAuth();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		api.unbind();
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
		
		canAuth &= email.getText().length() != 0;
		canAuth &= password.getText().length() != 0;
		canAuth &= api.isBound();
		
		buttonOk.setEnabled(canAuth);
	}


	@Override
	public void onApiBind() {
		checkCanAuth();
	}


	@Override
	public void onApiUnbind() {
		checkCanAuth();
	}

	@Override
	public void onHttpProgressUpdate(int requestId, Integer... progress) {
	}

	@Override
	public void onHttpResponse(int requestId, String result) {
		if(requestId == AUTH_REQUEST){
			progressDialog.hide();
			try{
				JSONObject json = new JSONObject(result);
				if(json.has(C.json.key.ACCESS_TOKEN)){
					final String token = json.getString(C.json.key.ACCESS_TOKEN);
					Editor editor = prefs.edit();					
					editor.putString(C.prefs.key.ACCESS_TOKEN, token);					
					editor.commit();
					
					startActivity(new Intent(this, MainActivity.class));
					finish();
					
				}else{
					Toast.makeText(this, json.getString(C.json.key.MESSAGE), Toast.LENGTH_SHORT).show();
				}
			}catch(JSONException e){
				Log.e(TAG, "JSONException", e);
				Toast.makeText(this, "Auth failed: can not parse json", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onHttpRequestFail(int requestId, Exception ex) {
		if(requestId == AUTH_REQUEST){
			progressDialog.hide();
			Toast.makeText(this, "Auth failed: can not connect to server", Toast.LENGTH_SHORT).show();
		}
	}
	
}

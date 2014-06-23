package com.zagayevskiy.fussball;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.zagayevskiy.fussball.api.ApiConnection;
import com.zagayevskiy.fussball.api.ApiConnection.IBindUnbindListener;
import com.zagayevskiy.fussball.api.IApiManager;
import com.zagayevskiy.fussball.api.request.ApiRequest;
import com.zagayevskiy.fussball.api.request.LoadPlayersRequest;
import com.zagayevskiy.fussball.api.request.ApiRequest.ResultListener;
import com.zagayevskiy.fussball.api.request.Auth;

public class AuthActivity extends FragmentActivity implements TextWatcher, IBindUnbindListener, IApiManager, ResultListener {
	
	public static final String TAG = AuthActivity.class.getSimpleName();
	
	private static final int AUTH_REQUEST = 1;
	private static final int LOAD_PLAYERS_REQUEST = 2;
	
	private EditText mAuthEmail, mAuthPassword;
	private View mButtonOk;
	
	private ProgressDialog mProgressDialog;
	
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
		
		mButtonOk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mProgressDialog.setTitle(R.string.auth_in_progress);
				mProgressDialog.show();
				
				final String emailStr = mAuthEmail.getText().toString();
				
				getApi().request(new Auth(AuthActivity.this, emailStr, mAuthPassword.getText().toString()), AUTH_REQUEST);
				
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
	
	@Override
	public ApiConnection getApi(){
		return mApi;
	}

	@Override
	public void onApiResult(int requestCode, int resultCode) {
		mProgressDialog.cancel();
		if(requestCode == AUTH_REQUEST && resultCode == ApiRequest.SUCCESS){
			mApi.request(new LoadPlayersRequest(null), 0);
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
	}
}

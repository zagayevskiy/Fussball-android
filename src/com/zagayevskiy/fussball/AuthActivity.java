package com.zagayevskiy.fussball;

import java.util.ArrayList;
import java.util.List;

import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.zagayevskiy.fussball.api.ApiConnection;
import com.zagayevskiy.fussball.api.ApiConnection.IBindUnbindListener;
import com.zagayevskiy.fussball.api.IApiManager;
import com.zagayevskiy.fussball.api.request.ApiBaseRequest;
import com.zagayevskiy.fussball.api.request.ApiBaseRequest.ResultListener;
import com.zagayevskiy.fussball.api.request.AuthRequest;
import com.zagayevskiy.fussball.api.request.LoadGamesRequest;
import com.zagayevskiy.fussball.api.request.LoadPlayersRequest;

public class AuthActivity extends FragmentActivity
	implements TextWatcher, IBindUnbindListener, IApiManager, ResultListener, LoaderCallbacks<Cursor> {
	
	public static final String TAG = AuthActivity.class.getSimpleName();
	
	private static final int AUTH_REQUEST = 1;
	
	private AutoCompleteTextView mAuthEmail;
	private EditText mAuthPassword;
	private View mButtonOk;
	
	private ProgressDialog mProgressDialog;
	
	private ApiConnection mApi;
	
	private ArrayAdapter<String> mAdapter;
	
	private static final String[] CONTACT_PROJECTION = {
		ContactsContract.CommonDataKinds.Email.ADDRESS
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.auth_activity);
		
		mAuthEmail = (AutoCompleteTextView) findViewById(R.id.auth_email);
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
				
				getApi().request(new AuthRequest(AuthActivity.this, emailStr, mAuthPassword.getText().toString()), AUTH_REQUEST);
				
			}
		});
		
		getLoaderManager().initLoader(0, null, this);
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
		if(requestCode == AUTH_REQUEST && resultCode == ApiBaseRequest.SUCCESS){
			mApi.request(new LoadPlayersRequest(null), 0);
			mApi.request(new LoadGamesRequest(null), 0);
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(this,
            Uri.withAppendedPath(
                    ContactsContract.Profile.CONTENT_URI,
                    ContactsContract.Contacts.Data.CONTENT_DIRECTORY),
            CONTACT_PROJECTION,

            // Select only email addresses.
            ContactsContract.Contacts.Data.MIMETYPE + " = ?",
            new String[]{ ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE },
            ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		List<String> emails = new ArrayList<String>(cursor.getCount());
		while(cursor.moveToNext()){
			emails.add(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
		}
		
		mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, emails);
		mAuthEmail.setAdapter(mAdapter);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
}

package com.zagayevskiy.fussball;

import java.util.ArrayList;
import java.util.List;

import com.zagayevskiy.fussball.api.ApiConnection;
import com.zagayevskiy.fussball.api.IApiManager;
import com.zagayevskiy.fussball.api.request.ApiBaseRequest;
import com.zagayevskiy.fussball.api.request.LoadGamesRequest;
import com.zagayevskiy.fussball.api.request.LoadPlayersRequest;
import com.zagayevskiy.fussball.api.request.RegistrationRequest;
import com.zagayevskiy.fussball.api.request.ApiBaseRequest.ResultListener;

import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

public class RegistrationFragment extends Fragment implements TextWatcher, OnClickListener, ResultListener, LoaderCallbacks<Cursor> {

	private static final int REGISTRATION_REQUEST = 1;
	private static final int LOADER_ID = 1;
	
	private EditText mNick;
	private AutoCompleteTextView mEmail;
	private EditText mPassword;
	private View mButtonOk;	
	private ProgressDialog mProgressDialog;
	
	private ArrayAdapter<String> mAdapter;
	
	private static final String[] CONTACT_PROJECTION = {
		ContactsContract.CommonDataKinds.Email.ADDRESS
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View root = inflater.inflate(R.layout.registration, container, false);
		
		mNick = (EditText) root.findViewById(R.id.registration_nick);
		mEmail = (AutoCompleteTextView) root.findViewById(R.id.registration_email);
		mPassword = (EditText) root.findViewById(R.id.registration_password);
		mButtonOk = root.findViewById(R.id.ok);
		mButtonOk.setOnClickListener(this);
		
		mNick.addTextChangedListener(this);
		mEmail.addTextChangedListener(this);
		mPassword.addTextChangedListener(this);
		
		mProgressDialog = new ProgressDialog(getActivity());
		mProgressDialog.setCancelable(false);
		mProgressDialog.setTitle(R.string.registration_in_progress);
		
		checkCanRegister();
		
		getActivity().getLoaderManager().initLoader(LOADER_ID, null, this);
		return root;
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		checkCanRegister();
	}


	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}


	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}
	
	private void checkCanRegister(){
		boolean canRegister = true;
		
		canRegister &= mEmail.getText().length() != 0;
		canRegister &= mNick.getText().length() != 0;
		canRegister &= mPassword.getText().length() != 0;
		
		mButtonOk.setEnabled(canRegister);
	}
	
	@Override
	public void onApiResult(int requestCode, int resultCode){		
		if(requestCode == REGISTRATION_REQUEST){
			mProgressDialog.cancel();
			switch(resultCode){
				case ApiBaseRequest.SUCCESS:
					ApiConnection api = ((IApiManager)getActivity()).getApi();
					api.request(new LoadPlayersRequest(null), 0);
					api.request(new LoadGamesRequest(null), 0);
					getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
					getActivity().finish();
				break;
				
				default:
					Toast.makeText(getActivity(), "can not register: " + resultCode, Toast.LENGTH_LONG).show();
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		mProgressDialog.show();
		final String nick = mNick.getText().toString();
		final String email = mEmail.getText().toString();
		final String password = mPassword.getText().toString();
		final RegistrationRequest registration = new RegistrationRequest(RegistrationFragment.this, nick, email, password);
		((IApiManager) getActivity()).getApi().request(registration, REGISTRATION_REQUEST);
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(),
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
		
		mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, emails);
		mEmail.setAdapter(mAdapter);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
}

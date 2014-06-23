package com.zagayevskiy.fussball;

import com.zagayevskiy.fussball.api.IApiManager;
import com.zagayevskiy.fussball.api.request.ApiBaseRequest;
import com.zagayevskiy.fussball.api.request.LoadPlayersRequest;
import com.zagayevskiy.fussball.api.request.RegistrationRequest;
import com.zagayevskiy.fussball.api.request.ApiBaseRequest.ResultListener;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class RegistrationFragment extends Fragment implements TextWatcher, OnClickListener, ResultListener {

	private static final int REGISTRATION_REQUEST = 1;
	
	private EditText mNickname, mEmail, mPassword;
	private View mButtonOk;	
	private ProgressDialog mProgressDialog;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View root = inflater.inflate(R.layout.registration, container, false);
		
		mNickname = (EditText) root.findViewById(R.id.registration_nickname);
		mEmail = (EditText) root.findViewById(R.id.registration_email);
		mPassword = (EditText) root.findViewById(R.id.registration_password);
		mButtonOk = root.findViewById(R.id.ok);
		mButtonOk.setOnClickListener(this);
		
		mNickname.addTextChangedListener(this);
		mEmail.addTextChangedListener(this);
		mPassword.addTextChangedListener(this);
		
		mProgressDialog = new ProgressDialog(getActivity());
		mProgressDialog.setCancelable(false);
		mProgressDialog.setTitle(R.string.registration_in_progress);
		
		checkCanRegister();
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
		canRegister &= mNickname.getText().length() != 0;
		canRegister &= mPassword.getText().length() != 0;
		
		mButtonOk.setEnabled(canRegister);
	}
	
	@Override
	public void onApiResult(int requestCode, int resultCode){		
		if(requestCode == REGISTRATION_REQUEST){
			mProgressDialog.cancel();
			switch(resultCode){
				case ApiBaseRequest.SUCCESS:
					((IApiManager)getActivity()).getApi().request(new LoadPlayersRequest(null), 0);
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
		final String nick = mNickname.getText().toString();
		final String email = mEmail.getText().toString();
		final String password = mPassword.getText().toString();
		final RegistrationRequest registration = new RegistrationRequest(RegistrationFragment.this, nick, email, password);
		((IApiManager) getActivity()).getApi().request(registration, REGISTRATION_REQUEST);
	}
}

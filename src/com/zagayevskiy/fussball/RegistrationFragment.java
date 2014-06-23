package com.zagayevskiy.fussball;

import com.zagayevskiy.fussball.api.request.ApiRequest;
import com.zagayevskiy.fussball.api.request.Registration;
import com.zagayevskiy.fussball.api.request.ApiRequest.ResultListener;

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

public class RegistrationFragment extends Fragment implements TextWatcher, ResultListener {

	private EditText mNickname, mEmail, mPassword;
	private View mButtonOk;	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View root = inflater.inflate(R.layout.registration, container, false);
		
		mNickname = (EditText) root.findViewById(R.id.registration_nickname);
		mEmail = (EditText) root.findViewById(R.id.registration_email);
		mPassword = (EditText) root.findViewById(R.id.registration_password);
		mButtonOk = root.findViewById(R.id.ok);
		mButtonOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((AuthActivity) getActivity()).getApi().request(new Registration(RegistrationFragment.this, mNickname.getText().toString(), mEmail.getText().toString(), mPassword.getText().toString()), 0);
			}
		});
		
		mNickname.addTextChangedListener(this);
		mEmail.addTextChangedListener(this);
		mPassword.addTextChangedListener(this);
		
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
		switch(resultCode){
			case ApiRequest.SUCCESS:
				getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
				getActivity().finish();
			break;
			
			default:
				Toast.makeText(getActivity(), "can not register: " + resultCode, Toast.LENGTH_LONG).show();
			break;
		}
	}
}

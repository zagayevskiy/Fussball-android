package com.zagayevskiy.fussball;

import com.zagayevskiy.fussball.api.ApiConnection;
import com.zagayevskiy.fussball.api.ApiConnection.IBindUnbindListener;
import com.zagayevskiy.fussball.api.IApiManager;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class NewGameActivity extends FragmentActivity implements IApiManager, IBindUnbindListener {

	public static final String TAG = NewGameActivity.class.getName();
	public static final String KEY_PLAYER1_NICK = TAG + "_player1";
	public static final String KEY_PLAYER2_NICK = TAG + "_player2";
	
	private ApiConnection mApi;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApi = new ApiConnection(this, this);
		setContentView(R.layout.new_game_activity);
		
	}
	
	@Override
	protected void onStart() {
		mApi.bind();
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		mApi.unbind();
		super.onStop();
	}

	@Override
	public ApiConnection getApi() {
		// TODO Auto-generated method stub
		return mApi;
	}

	@Override
	public void onApiBind() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onApiUnbind() {
		// TODO Auto-generated method stub
	}
	
}

package com.zagayevskiy.fussball;

import com.zagayevskiy.fussball.api.ApiConnection;
import com.zagayevskiy.fussball.api.IApiManager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

public class ProfileActivity extends FragmentActivity implements IApiManager{
	
	private static final String TAG = ProfileActivity.class.getName();
	public static final String KEY_PLAYER_NICK = TAG + "_nick";
	
	private ApiConnection mApi;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.profile_activity);
		
		Intent intent = getIntent();
		if(!intent.hasExtra(KEY_PLAYER_NICK)){
			finish();
			return;
		}
		
		final String nick = intent.getStringExtra(KEY_PLAYER_NICK);
				
		Bundle args = new Bundle();
		args.putString(GamesFragment.KEY_OPPONENT_ONE_NICK, nick);
		Fragment fragment = new GamesFragment();
		fragment.setArguments(args);
		
		getSupportFragmentManager().beginTransaction()
			.add(R.id.container, fragment)
			.commitAllowingStateLoss();
			
		mApi = new ApiConnection(null, this);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		mApi.bind();
	}
	
	@Override
	protected void onStop() {
		mApi.unbind();
		super.onStop();
	}

	@Override
	public ApiConnection getApi() {
		return mApi;
	}
}

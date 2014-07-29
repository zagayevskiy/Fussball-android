package com.zagayevskiy.fussball;

import com.zagayevskiy.fussball.api.ApiConnection;
import com.zagayevskiy.fussball.api.IApiManager;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

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
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(String.format(getString(R.string.title_profile_fmt), nick));
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				goUp();
				return true;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public ApiConnection getApi() {
		return mApi;
	}
	
	private void goUp(){
		NavUtils.navigateUpFromSameTask(this);
	}
}

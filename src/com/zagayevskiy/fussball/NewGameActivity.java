package com.zagayevskiy.fussball;

import com.zagayevskiy.fussball.api.ApiConnection;
import com.zagayevskiy.fussball.api.ApiConnection.IBindUnbindListener;
import com.zagayevskiy.fussball.api.IApiManager;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.MenuItem;

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
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.title_new_game);
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
	
	private void goUp(){
		Intent upIntent = NavUtils.getParentActivityIntent(this);
		if(NavUtils.shouldUpRecreateTask(this, upIntent)){
            // This activity is NOT part of this app's task, so create a new task
            // when navigating up, with a synthesized back stack.
            TaskStackBuilder.create(this)
                .addNextIntentWithParentStack(upIntent)
                .startActivities();
        }else{
        	finish();
        }
	}
}

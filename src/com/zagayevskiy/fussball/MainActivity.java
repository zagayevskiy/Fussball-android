package com.zagayevskiy.fussball;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import com.zagayevskiy.fussball.api.ApiConnection;
import com.zagayevskiy.fussball.api.ApiConnection.IBindUnbindListener;
import com.zagayevskiy.fussball.api.IApiManager;
import com.zagayevskiy.fussball.tabs.TabsPagerAdapter;

public class MainActivity extends FragmentActivity implements IBindUnbindListener, IApiManager {
	
	private ApiConnection mApi;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main_activity);
        
		ViewPager pager = (ViewPager) findViewById(R.id.pager);
		ActionBar actionBar = getActionBar();
		TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(actionBar, getSupportFragmentManager(), pager);
		pager.setAdapter(tabsPagerAdapter);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		tabsPagerAdapter
    		.addTab(actionBar.newTab().setText(R.string.tab_players), PlayersFragment.class)
    		.addTab(actionBar.newTab().setText(R.string.tab_games), GamesFragment.class)
			;
	}

	@Override
	protected void onStart() {
		super.onStart();
		mApi = new ApiConnection(this, this);
		mApi.bind();
	}
	
	@Override
	protected void onStop() {
		mApi.unbind();
		super.onStop();		
	}
	
	@Override
	public void onApiBind() {
		
	}

	@Override
	public void onApiUnbind() {
	}
	
	@Override
	public ApiConnection getApi(){
		return mApi;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}
}

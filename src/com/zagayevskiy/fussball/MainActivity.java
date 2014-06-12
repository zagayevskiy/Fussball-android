package com.zagayevskiy.fussball;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.zagayevskiy.fussball.service.ApiConnection;
import com.zagayevskiy.fussball.service.ApiConnection.IBindUnbindListener;
import com.zagayevskiy.fussball.tabs.TabsPagerAdapter;

public class MainActivity extends FragmentActivity implements IBindUnbindListener {
	
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
    		.addTab(actionBar.newTab().setText(R.string.tab_players), UsersFragment.class)
    		.addTab(actionBar.newTab().setText(R.string.tab_new_game), NewGameFragment.class)
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
		super.onStop();
		mApi.unbind();
	}
	
	@Override
	public void onApiBind() {
//		mApi.loadUsers();	
	}

	@Override
	public void onApiUnbind() {
	}
}

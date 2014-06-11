package com.zagayevskiy.fussball.tabs;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.support.v4.view.ViewPager;

/**
 * Listen for tab changes - from action bar and view pager 
 **/
public class TabChangeListener implements ViewPager.OnPageChangeListener, ActionBar.TabListener {

	private ActionBar mActionBar;
	private ViewPager mViewPager;
	
	public TabChangeListener(ActionBar actionBar, ViewPager viewPager) {
		this.mActionBar = actionBar;
		this.mViewPager = viewPager;
		mViewPager.setOnPageChangeListener(this);
	}
	
    @Override
    public void onPageSelected(int position) {
        mActionBar.setSelectedNavigationItem(position);
    }
 
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }
 
    @Override
    public void onPageScrollStateChanged(int arg0) {
    }
    
    @Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}
}
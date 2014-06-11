package com.zagayevskiy.fussball.tabs;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class  TabsPagerAdapter extends FragmentPagerAdapter {
	
	private static final String UNREACHABLE_EXCEPTION_LOG = "WTF!? Unreachable exception";
	
	private ActionBar mActionBar;
	private TabListener mTabListener;
	private List<Class<?>> mFragmentClasses = new ArrayList<Class<?>>();
	
	public TabsPagerAdapter(ActionBar actionBar, FragmentManager fm, ViewPager viewPager) {
		super(fm);
		this.mActionBar = actionBar;
		this.mTabListener = new TabChangeListener(actionBar, viewPager);
	}

	public <F extends Fragment> TabsPagerAdapter addTab(Tab tab, Class<F> clazz){
		tab.setTabListener(mTabListener);
		mActionBar.addTab(tab);
		mFragmentClasses.add(clazz);
		notifyDataSetChanged();
		
		return this;
	}
	
	@Override
    public Fragment getItem(int index) {
		Class<?> clazz = mFragmentClasses.get(index);
		try{
			return (Fragment) clazz.newInstance();
		}catch(IllegalAccessException unreachable){
			throw new RuntimeException(UNREACHABLE_EXCEPTION_LOG, unreachable);
		}catch(InstantiationException unreachable){
			throw new RuntimeException(UNREACHABLE_EXCEPTION_LOG, unreachable);
		}
    }
 
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return mFragmentClasses.size();
    }

}

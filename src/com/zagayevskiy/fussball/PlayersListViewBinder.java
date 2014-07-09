package com.zagayevskiy.fussball;

import com.zagayevskiy.fussball.utils.GravatarResolver;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.view.View;
import android.widget.ImageView;

public class PlayersListViewBinder implements ViewBinder {

	private final Context mContext;
	private final GravatarResolver gravatar = GravatarResolver.getInstance();
	
	public PlayersListViewBinder(Context context) {
		mContext = context;
	}
	
	@Override
	public boolean setViewValue(View v, Cursor cursor, int columnIndex) {
		
		switch(v.getId()){
//			case android.R.id.text1:
//			case android.R.id.text2:
		
			case R.id.player_photo:
				gravatar.resolve(mContext, cursor.getString(columnIndex), 0, 
					new GravatarResolver.SimpleOnResolveListener((ImageView) v));
			return true;
		
		}
		
		return false;
	}

}

package com.zagayevskiy.fussball.widget;

import java.util.ArrayList;

import com.zagayevskiy.fussball.Player;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

public class RatingRemoteViewsFactory implements RemoteViewsFactory {
	
	private Context mContext;
	private ArrayList<String> mData;
	
	public RatingRemoteViewsFactory(Context context, Intent intent){
		mContext = context;
	}

	@Override
	public void onCreate() {
		mData = new ArrayList<String>();
	}

	@Override
	public void onDataSetChanged() {
		mData.clear();
		
		ContentResolver resolver = mContext.getContentResolver();
		Cursor cursor = resolver.query(Player.URI, Player.FULL_PROJECTION, null, null, Player.ORDER_RATING_DESC);
		final int columnNick = cursor.getColumnIndex(Player.FIELD_NICK);
		while(cursor.moveToNext()){
			mData.add(cursor.getString(columnNick));
		}
		
	}

	@Override
	public void onDestroy() {
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public RemoteViews getViewAt(int position) {
		RemoteViews views = new RemoteViews(mContext.getPackageName(), android.R.layout.simple_list_item_1);
		
		views.setTextViewText(android.R.id.text1, mData.get(position));
		Intent intent = new Intent();
		views.setOnClickFillInIntent(android.R.id.text1, intent);
		
		return views;
	}

	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

}

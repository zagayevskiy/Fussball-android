package com.zagayevskiy.fussball.widget;

import java.util.ArrayList;

import com.zagayevskiy.fussball.Player;
import com.zagayevskiy.fussball.R;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

public class RatingRemoteViewsFactory implements RemoteViewsFactory {
	
	private Context mContext;
	private ArrayList<Player> mData;
	
	public RatingRemoteViewsFactory(Context context, Intent intent){
		mContext = context;
	}

	@Override
	public void onCreate() {
		mData = new ArrayList<Player>();
	}

	@Override
	public void onDataSetChanged() {
		mData.clear();
		
		ContentResolver resolver = mContext.getContentResolver();
		Cursor cursor = resolver.query(Player.URI, Player.FULL_PROJECTION, null, null, Player.ORDER_RATING_DESC);
		
		mData = Player.fromCursor(cursor);
		
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
		RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_players_list_item);
		
		final Player player = mData.get(position);
		
		views.setTextViewText(R.id.nick, player.getNick());
		views.setTextViewText(R.id.rating, String.valueOf(player.getRating()));
		views.setTextViewText(R.id.total_played, String.valueOf(player.getTotalPlayed()));
		views.setTextViewText(R.id.total_won, String.valueOf(player.getTotalWon()));
		
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

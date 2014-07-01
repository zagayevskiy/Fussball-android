package com.zagayevskiy.fussball;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.view.View;
import android.widget.TextView;

public class GamesListViewBinder implements ViewBinder {

	private final Context context;
	
	public GamesListViewBinder(Context context) {
		this.context = context;
	}
	
	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		
		switch(view.getId()){
			case R.id.player1_nick:
			case R.id.player2_nick:
			case R.id.player1_score:
			case R.id.player2_score:
				((TextView) view).setText(cursor.getString(columnIndex));
				return true;
			case R.id.player1_rating_delta:
			case R.id.player2_rating_delta:
				final int delta = cursor.getInt(columnIndex);
				TextView textView = (TextView) view;
				String value;
				if(delta > 0){
					value = "+" + String.valueOf(delta);
					textView.setTextColor(context.getResources().getColor(R.color.win));
				}else{
					value = String.valueOf(delta);
					textView.setTextColor(context.getResources().getColor(R.color.lose));
				}
				textView.setText(value);
				return true;
		}
		
		return false;
	}

}

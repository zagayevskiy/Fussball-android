package com.zagayevskiy.fussball.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.zagayevskiy.fussball.R;
import com.zagayevskiy.fussball.utils.C;

public class RatingWidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		for (int id : appWidgetIds) {
			updateWidget(context, appWidgetManager, id);
		}

	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		
		if(C.intent.action.PLAYERS_LIST_LOADED.equalsIgnoreCase(intent.getAction())){
			 ComponentName thisAppWidgetProvider = new ComponentName(context.getPackageName(), getClass().getName());
		     AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		     int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidgetProvider);
		     for (int id : ids) {
		    	 updateWidget(context, appWidgetManager, id);
		     }
		}		
	}

	private void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

		updateList(context, appWidgetManager, appWidgetId, views);

		appWidgetManager.updateAppWidget(appWidgetId, views);
		appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.list);

	}

	private void updateList(Context context, AppWidgetManager appWidgetManager, int appWidgetId, RemoteViews views) {
		Intent factoryIntent = new Intent(context, RatingRemoteViewsService.class);
		factoryIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		factoryIntent.setData(Uri.parse(factoryIntent.toUri(Intent.URI_INTENT_SCHEME)));
		views.setRemoteAdapter(R.id.list, factoryIntent);
	}
}

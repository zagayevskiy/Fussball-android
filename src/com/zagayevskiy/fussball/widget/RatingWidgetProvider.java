package com.zagayevskiy.fussball.widget;

import com.zagayevskiy.fussball.R;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

public class RatingWidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		for (int id : appWidgetIds) {
			updateWidget(context, appWidgetManager, id);
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

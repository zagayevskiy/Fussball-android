package com.zagayevskiy.fussball.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class RatingRemoteViewsService extends RemoteViewsService {

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new RatingRemoteViewsFactory(getApplicationContext(), intent);
	}

}

package com.zagayevskiy.fussball.gcm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.support.v4.content.WakefulBroadcastReceiver;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ComponentName name = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());
		intent.setComponent(name);
		startWakefulService(context, intent);
	}

}

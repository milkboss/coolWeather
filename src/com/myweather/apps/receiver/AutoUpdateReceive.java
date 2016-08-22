package com.myweather.apps.receiver;

import com.myweather.apps.service.AutoUpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoUpdateReceive extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
	Intent i=new Intent(context,AutoUpdateService.class);
	context.startService(i);
	}

}

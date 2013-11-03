package com.babanomania.tasker.notifier;

import com.babanomania.tasker.notifier.bundle.BundleScrubber;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

public class FireReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
        if (com.twofortyfouram.locale.Intent.ACTION_FIRE_SETTING.equals(intent.getAction()))
        {
        	Bundle localeSettings = intent.getExtras().getBundle(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
        	BundleScrubber.scrub(localeSettings);
        	
        	Log.d( "FireReceiver" , localeSettings.toString() );
        	
        	//AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent notificationRequest = new Intent(context, TaskerNotificationExtensionService.class);
			
			if( notificationRequest.getExtras() != null  )
				notificationRequest.getExtras().clear();
			
			notificationRequest.setAction(TaskerNotificationExtensionService.INTENT_ACTION_ADD);
			notificationRequest.putExtras( localeSettings );
			context.startService(notificationRequest);
		    
		    //PendingIntent pi = PendingIntent.getService(context, 0, notificationRequest, 0);
	        //am.set(  AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), pi );

        }
	}

}

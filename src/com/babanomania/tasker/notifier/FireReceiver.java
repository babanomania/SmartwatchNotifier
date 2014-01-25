package com.babanomania.tasker.notifier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.babanomania.tasker.notifier.bundle.BundleScrubber;

public class FireReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
        if (com.twofortyfouram.locale.Intent.ACTION_FIRE_SETTING.equals(intent.getAction()))
        {
        	Bundle localeSettings = intent.getExtras().getBundle(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
        	BundleScrubber.scrub(localeSettings);
        	
        	final String action1Str = localeSettings.getString(TaskerNotificationExtensionService.ACTION1);
            final String action2Str = localeSettings.getString(TaskerNotificationExtensionService.ACTION2);
            final String action3Str = localeSettings.getString(TaskerNotificationExtensionService.ACTION3);
            
            String content = localeSettings.getString( TaskerNotificationExtensionService.CONTENT );
            
            if( !EditActivity.SELECT_A_TASK.equals(action1Str) )
            content += "\n\n Action ( 1 ) : " + action1Str ;
            
            if( !EditActivity.SELECT_A_TASK.equals(action2Str) )
            content += "\n Action ( 2 ) : " + action2Str ;
            
            if( !EditActivity.SELECT_A_TASK.equals(action3Str) )
            content += "\n Action ( 3 ) : " + action3Str ;
            
            localeSettings.putString( TaskerNotificationExtensionService.CONTENT, content );
            
        	Log.d( "FireReceiver" , localeSettings.toString() );
        	
        	Intent notificationRequest = new Intent(context, TaskerNotificationExtensionService.class);
			
			if( notificationRequest.getExtras() != null  )
				notificationRequest.getExtras().clear();
			
			notificationRequest.setAction(TaskerNotificationExtensionService.INTENT_ACTION_ADD);
			notificationRequest.putExtras( localeSettings );
			context.startService(notificationRequest);

        }
	}

}

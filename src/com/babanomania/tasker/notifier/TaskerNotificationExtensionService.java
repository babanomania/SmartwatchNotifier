/*
Copyright (c) 2011, Sony Ericsson Mobile Communications AB
Copyright (c) 2011-2013, Sony Mobile Communications AB

 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 * Neither the name of the Sony Ericsson Mobile Communications AB / Sony Mobile
 Communications AB nor the names of its contributors may be used to endorse or promote
 products derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.babanomania.tasker.notifier;

import net.dinglisch.android.tasker.TaskerUtil;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.aef.notification.Notification;
import com.sonyericsson.extras.liveware.extension.util.ExtensionService;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.notification.NotificationUtil;
import com.sonyericsson.extras.liveware.extension.util.registration.DeviceInfoHelper;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation;

/**
 * The sample extension service handles extension registration and inserts data
 * into the notification database.
 */
public class TaskerNotificationExtensionService extends ExtensionService {

	public static final String ACTION1 = "ACTION1";
	public static final String ACTION2 = "ACTION2";
	public static final String ACTION3 = "ACTION3";
	
	public static final String CONTENT = "Content";
	public static final String TITLE = "Title";
	public static final String ACTION_STRINGS = "ACTION_STRINGS";
	
	public static final String LOG_TAG = "TaskerNotifierService";  
	
	public static final String ALL = "ALL";
	
    /**
     * Extensions specific id for the source
     */
    public static final String EXTENSION_SPECIFIC_ID = "EXTENSION_SPECIFIC_ID_TASKER_NOTIFICATION";

    /**
     * Extension key
     */
    public static final String EXTENSION_KEY = "com.babanomania.tasker.notifier.key";

    /**
     * Add data, handled in onStartCommand()
     */
    public static final String INTENT_ACTION_ADD = "com.babanomania.tasker.notifier.action.add";

    public TaskerNotificationExtensionService() {
        super(EXTENSION_KEY);
    }

    /**
     * {@inheritDoc}
     *
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
    }

    /**
     * {@inheritDoc}
     *
     * @see android.app.Service#onStartCommand()
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int retVal = super.onStartCommand(intent, flags, startId);
        if (intent != null) {
           
        	if (INTENT_ACTION_ADD.equals(intent.getAction())) {
                Log.d(LOG_TAG, "onStart action: INTENT_ACTION_ADD");
                
                Bundle extras = intent.getExtras();
                    
                if( extras != null ){
                
	                if( extras.getString( TITLE ) != null && extras.getString( CONTENT ) != null ){
	               
	                	String hostAppPackageName = intent.getStringExtra(Control.Intents.EXTRA_AHA_PACKAGE_NAME);
	                	boolean advancedFeaturesSupported = DeviceInfoHelper.isSmartWatch2ApiAndScreenDetected(this, hostAppPackageName);
	                	
	                	Log.d( "TaskerNotificationExtensionService" , extras.toString() );
	                	
	                	if( !advancedFeaturesSupported )
	                		addData( extras.getString( TITLE ), extras.getString( CONTENT ), null );
	                	else
	                		addData( extras.getString( TITLE ), extras.getString( CONTENT ), extras.getString(ACTION_STRINGS) );
	                	
		                extras.clear();
		                
		                stopSelfCheck();
		                
	                }else{
	                	Log.d( "TaskerNotificationExtensionService", extras.toString() );
	                }
                
                }else{
                	Log.d( "TaskerNotificationExtensionService", "null" );
                }
                
             }
        }

        return retVal;
    }

    /**
     * {@inheritDoc}
     *
     * @see android.app.Service#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    /**
     * Add some "random" data
     */
    private void addData( String name, String message, String actionString  ) {

    	long time = System.currentTimeMillis();
        long sourceId = NotificationUtil.getSourceId(this, EXTENSION_SPECIFIC_ID);
        if (sourceId == NotificationUtil.INVALID_ID) {
            Log.e(LOG_TAG, "Failed to insert data");
            return;
        }
        
        String profileImage = ExtensionUtils.getUriString(this,R.drawable.icon_dark);

        ContentValues eventValues = new ContentValues();
        eventValues.put(Notification.EventColumns.EVENT_READ_STATUS, false);
        eventValues.put(Notification.EventColumns.DISPLAY_NAME, name);
        eventValues.put(Notification.EventColumns.MESSAGE, message);
        eventValues.put(Notification.EventColumns.PERSONAL, 1);
        eventValues.put(Notification.EventColumns.PROFILE_IMAGE_URI, profileImage);
        eventValues.put(Notification.EventColumns.PUBLISHED_TIME, time);
        eventValues.put(Notification.EventColumns.SOURCE_ID, sourceId);
        
        if( actionString != null )
        eventValues.put(Notification.EventColumns.FRIEND_KEY, actionString);

        try {
            getContentResolver().insert(Notification.Event.URI, eventValues);
            
        } catch (IllegalArgumentException e) {
            Log.e(LOG_TAG, "Failed to insert event", e);
            
        } catch (SecurityException e) {
            Log.e(LOG_TAG, "Failed to insert event, is Live Ware Manager installed?", e);
            
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Failed to insert event", e);
        }
    }

    @Override
    protected void onViewEvent(Intent intent) {
    	
        String action = intent.getStringExtra(Notification.Intents.EXTRA_ACTION);
        int eventId = intent.getIntExtra(Notification.Intents.EXTRA_EVENT_ID, -1);
        
        if (Notification.SourceColumns.ACTION_1.equals(action)) {
            doAction( ACTION1, eventId );
            
        } else if (Notification.SourceColumns.ACTION_2.equals(action)) {
        	doAction( ACTION2, eventId );
            
        } else if (Notification.SourceColumns.ACTION_3.equals(action)) {
        	doAction( ACTION3, eventId );
        }
    }

    @Override
    protected void onRefreshRequest() {
        // Do nothing here, only relevant for polling extensions, this
        // extension is always up to date
    }

    /**
     * Show toast with event information
     *
     * @param eventId The event id
     */
    public void doAction( String actionNo, int eventId) {
    	
        Log.d(LOG_TAG, "doAction1 event id: " + eventId);
        Cursor cursor = null;
        
        try {
            cursor = getContentResolver().query(Notification.Event.URI, null, Notification.EventColumns._ID + " = " + eventId, null, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                int unparsedDataIndex = cursor.getColumnIndex(Notification.EventColumns.FRIEND_KEY);
                
                String unparsedData = cursor.getString(unparsedDataIndex);
                String taskToRun = getTaskToRun( actionNo, unparsedData );
                
                if( taskToRun != null && !taskToRun.equals( EditActivity.SELECT_A_TASK )){
                	Log.d(LOG_TAG, "detected task to run : '" + taskToRun + "' ");
                	TaskerUtil.runTask( this, taskToRun );
                }
            }
            
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Failed to query event", e);
        } catch (SecurityException e) {
            Log.e(LOG_TAG, "Failed to query event", e);
        } catch (IllegalArgumentException e) {
            Log.e(LOG_TAG, "Failed to query event", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    protected RegistrationInformation getRegistrationInformation() {
        return new TaskerNotificationRegistrationInformation(this);
    }

    /*
     * (non-Javadoc)
     * @see com.sonyericsson.extras.liveware.aef.util.ExtensionService#
     * keepRunningWhenConnected()
     */
    @Override
    protected boolean keepRunningWhenConnected() {
        return false;
    }
    
    private String getTaskToRun( String actionNo, String unparsedData ){
    	
    	if( unparsedData == null || unparsedData.length() < 7 )    	
    		return null;
    	
    	String sep = unparsedData.substring(0, 1);
    	String stringToParse = unparsedData.substring(1);
    	if(! stringToParse.contains(sep) )
    		return null;
    	
    	Log.d(LOG_TAG, "string to parse : " + unparsedData );
    	String[] actionsArr = stringToParse.split(sep);
    	
    	if( actionNo == ACTION1 )
    		return actionsArr[0];
    	
    	if( actionNo == ACTION2 )
    		return actionsArr[1];
    	
    	if( actionNo == ACTION3 )
    		return actionsArr[2];
    	
    	else
    		return null;
    }
}

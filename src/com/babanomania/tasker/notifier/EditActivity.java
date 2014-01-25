package com.babanomania.tasker.notifier;

import java.util.ArrayList;
import java.util.List;

import net.dinglisch.android.tasker.TaskerUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.babanomania.tasker.notifier.bundle.BundleScrubber;

public class EditActivity extends Activity {

	public static final String SELECT_A_TASK = "Select A Task";

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        BundleScrubber.scrub(getIntent());

        final Bundle localeBundle = getIntent().getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
        BundleScrubber.scrub(localeBundle);

        setContentView(R.layout.tasker_plugin_edit);
        
        Spinner action1Spinner = (Spinner) findViewById( R.id.action1 );
        Spinner action2Spinner = (Spinner) findViewById( R.id.action2 );
        Spinner action3Spinner = (Spinner) findViewById( R.id.action3 );
        
        List<String> listOfTasks = new ArrayList<String>();
        listOfTasks.add( SELECT_A_TASK );
        listOfTasks.addAll( TaskerUtil.getTasks( this ) );
        
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listOfTasks);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        action1Spinner.setAdapter(dataAdapter);
        action2Spinner.setAdapter(dataAdapter);
        action3Spinner.setAdapter(dataAdapter);
        
        action1Spinner.setSelection(0);
        action2Spinner.setSelection(0);
        action3Spinner.setSelection(0);
        
        if (localeBundle != null && null == savedInstanceState)
        {
           // if (PluginBundleManager.isBundleValid(localeBundle))
        	if( localeBundle.getString(TaskerNotificationExtensionService.TITLE) != null &&
        		localeBundle.getString(TaskerNotificationExtensionService.CONTENT) != null &&
        		localeBundle.getString(TaskerNotificationExtensionService.ACTION1) != null &&
        		localeBundle.getString(TaskerNotificationExtensionService.ACTION2) != null &&
        		localeBundle.getString(TaskerNotificationExtensionService.ACTION3) != null	)
            {
                final String messageTitle = localeBundle.getString(TaskerNotificationExtensionService.TITLE);
                ((EditText) findViewById(R.id.messageTitle)).setText(messageTitle);
                
                final String messageContent = localeBundle.getString(TaskerNotificationExtensionService.CONTENT);
                ((EditText) findViewById(R.id.messageContent)).setText(messageContent);
                
                final String action1Str = localeBundle.getString(TaskerNotificationExtensionService.ACTION1);
                final String action2Str = localeBundle.getString(TaskerNotificationExtensionService.ACTION2);
                final String action3Str = localeBundle.getString(TaskerNotificationExtensionService.ACTION3);
                
                action1Spinner.setSelection( getIndex( listOfTasks, action1Str ) );
                action2Spinner.setSelection( getIndex( listOfTasks, action2Str ) );
                action3Spinner.setSelection( getIndex( listOfTasks, action3Str ) );
                
            }else{
            	Log.d( TaskerNotificationExtensionService.LOG_TAG, "localeBundle is not valid " + localeBundle );
            	
            	for (String key: localeBundle.keySet())
            	{
            	  Log.d (TaskerNotificationExtensionService.LOG_TAG, key + " is a key in the bundle");
            	}
            }
            
        }else{
        	Log.d( TaskerNotificationExtensionService.LOG_TAG, "savedInstanceState is null" );
        }
        
        
        Button saveButton = (Button) findViewById(R.id.saveButton);
        
        saveButton.setOnClickListener( new OnClickListener() {
			
			public void onClick(View view) {
				finish();
			}
		} );

        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        
        cancelButton.setOnClickListener( new OnClickListener() {
			
			public void onClick(View view) {
				cancel(view);
			}
		} );
        
    }
	
   public void finish() {
    	
    	EditText messageTitle = (EditText)findViewById( R.id.messageTitle );
    	EditText messageContent = (EditText)findViewById( R.id.messageContent );
    	
    	Spinner action1Spinner = (Spinner) findViewById( R.id.action1 );
        Spinner action2Spinner = (Spinner) findViewById( R.id.action2 );
        Spinner action3Spinner = (Spinner) findViewById( R.id.action3 );
    	
		Bundle extra = new Bundle();
		extra.putString( TaskerNotificationExtensionService.TITLE, messageTitle.getText().toString());
		extra.putString( TaskerNotificationExtensionService.CONTENT, messageContent.getText().toString());
		
		Intent resultIntent = new Intent();
		resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, extra);
        resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, messageTitle.getText().toString() );
		
        List<String> listOfTasks = TaskerUtil.getTasks( this );
        int action1Pos = action1Spinner.getSelectedItemPosition();
        int action2Pos = action2Spinner.getSelectedItemPosition();
        int action3Pos = action3Spinner.getSelectedItemPosition();
        
        String taskForAction1 = listOfTasks.get(action1Pos);
        String taskForAction2 = listOfTasks.get(action2Pos);
        String taskForAction3 = listOfTasks.get(action3Pos);
        
        extra.putString( TaskerNotificationExtensionService.ACTION1, taskForAction1);
        extra.putString( TaskerNotificationExtensionService.ACTION2, taskForAction2);
        extra.putString( TaskerNotificationExtensionService.ACTION3, taskForAction3);
        
        super.setResult(RESULT_OK, resultIntent);
    	super.finish();
    }
    
    public void cancel(View view) {
    	super.setResult(RESULT_CANCELED);
    	super.finish();
    }
    
    public int getIndex( List arraylist, String itemName)
    {
        for (int i = 0; i < arraylist.size(); i++)
        {
            Object auction = arraylist.get(i);
            if (itemName.equals(auction)){
                return i;
            }
        } 

        return 0;
    }
 
}

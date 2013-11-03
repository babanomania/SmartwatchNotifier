package com.babanomania.tasker.notifier;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.babanomania.tasker.notifier.bundle.BundleScrubber;
import com.babanomania.tasker.notifier.bundle.PluginBundleManager;

public class EditActivity extends Activity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        BundleScrubber.scrub(getIntent());

        final Bundle localeBundle = getIntent().getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
        BundleScrubber.scrub(localeBundle);

        setContentView(R.layout.tasker_plugin_edit);

        if (null == savedInstanceState)
        {
            if (PluginBundleManager.isBundleValid(localeBundle))
            {
                final String messageTitle = localeBundle.getString(TaskerNotificationExtensionService.TITLE);
                ((EditText) findViewById(R.id.messageTitle)).setText(messageTitle);
                
                final String messageContent = localeBundle.getString(TaskerNotificationExtensionService.CONTENT);
                ((EditText) findViewById(R.id.messageContent)).setText(messageContent);
            }
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
    
//    public void save(View view) {
//    	
//    	EditText messageTitle = (EditText)view.findViewById( R.id.messageTitle );
//    	EditText messageContent = (EditText)view.findViewById( R.id.messageContent );
//    	    	
//		Bundle extra = new Bundle();
//		extra.putString( TaskerNotificationExtensionService.TITLE, messageTitle.getText().toString());
//		extra.putString( TaskerNotificationExtensionService.CONTENT, messageContent.getText().toString());
//		
//		Intent resultIntent = new Intent();
//		resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, extra);
//        resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, messageTitle.getText().toString() );
//		
//        super.setResult(RESULT_OK, resultIntent);
//    	super.finish();
//    }
	
	   public void finish() {
	    	
	    	EditText messageTitle = (EditText)findViewById( R.id.messageTitle );
	    	EditText messageContent = (EditText)findViewById( R.id.messageContent );
	    	    	
			Bundle extra = new Bundle();
			extra.putString( TaskerNotificationExtensionService.TITLE, messageTitle.getText().toString());
			extra.putString( TaskerNotificationExtensionService.CONTENT, messageContent.getText().toString());
			
			Intent resultIntent = new Intent();
			resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, extra);
	        resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, messageTitle.getText().toString() );
			
	        super.setResult(RESULT_OK, resultIntent);
	    	super.finish();
	    }
    
    public void cancel(View view) {
    	super.setResult(RESULT_CANCELED);
    	super.finish();
    }
 
}

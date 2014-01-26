package net.dinglisch.android.tasker;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.dinglisch.android.tasker.TaskerIntent.Status;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.babanomania.tasker.notifier.TaskerNotificationExtensionService;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;

public class TaskerUtil {

	private static final String TAG = "TaskerUtil";

	public static List<String> getTasks( Context context, String projectName ){
		
		Cursor c = context.getContentResolver().query( Uri.parse( "content://net.dinglisch.android.tasker/tasks" ), null, null, null, null );
		List<String> taskerTasks = new ArrayList<String>();
		
		if ( c != null ) {
			int nameCol = c.getColumnIndex( "name" );
			int projNameCol = c.getColumnIndex( "project_name" );
			
			while ( c.moveToNext() ){
				if( projectName.equals( c.getString( projNameCol ) ) )
					taskerTasks.add( c.getString( nameCol ) );
			}
				
		    c.close();
		}
		
		return taskerTasks;
	}
	
	public static List<String> getTasks( Context context ){
		
		Cursor c = context.getContentResolver().query( Uri.parse( "content://net.dinglisch.android.tasker/tasks" ), null, null, null, null );
		List<String> taskerTasks = new ArrayList<String>();
		
		String projectResId = ExtensionUtils.getUriString( context, com.babanomania.tasker.notifier.R.string.preference_key_project );
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		String selectedProject = sharedPref.getString( projectResId, TaskerNotificationExtensionService.ALL );
		Log.d( TaskerNotificationExtensionService.LOG_TAG, "Selected Project Name : " + selectedProject );
		
		if ( c != null ) {
			int nameCol = c.getColumnIndex( "name" );
			int projNameCol = c.getColumnIndex( "project_name" );
			
			String projectName = TaskerNotificationExtensionService.ALL ;
			
			while ( c.moveToNext() ){

				projectName = c.getString(projNameCol);
				String taskName = c.getString(nameCol);
			
				Log.d( TaskerNotificationExtensionService.LOG_TAG, "Project Name : " + projectName );
				
				if( selectedProject.equals(TaskerNotificationExtensionService.ALL )){
					taskerTasks.add( taskName );
					
				}else{
					
					if( projectName.equals(selectedProject) ){
						taskerTasks.add( taskName );
					
					}else{
						Log.d( TaskerNotificationExtensionService.LOG_TAG, "not adding Project Name : " + projectName + ", because selected project name is " + selectedProject );
					}
				}
				
			}

		    c.close();
		}
		
		return taskerTasks;
	}
	
	public static String[] getTasksAsStrings(  Context context, String projectName  ){
		
		List<String> tasksList = getTasks(context, projectName);
		return tasksList.toArray(new String[tasksList.size()]);
		
	}

	public static String[] getTasksAsStrings(  Context context  ){
		
		List<String> tasksList = getTasks(context);
		return tasksList.toArray(new String[tasksList.size()]);
		
	}
	
	public static Set<String> getProjectNames( Context context ){
		
		Cursor c = context.getContentResolver().query( Uri.parse( "content://net.dinglisch.android.tasker/tasks" ), null, null, null, null );
		Set<String> taskerProjects = new TreeSet<String>();
		
		if ( c != null ) {
//			int nameCol = c.getColumnIndex( "name" );
			int projNameCol = c.getColumnIndex( "project_name" );
			
			while ( c.moveToNext() )
				taskerProjects.add( c.getString( projNameCol ) );

		    c.close();
		}
		
		return taskerProjects;
		
	}
	
	public static Status runTask( Context context, String taskName ){
		
		Status taskerStatus = TaskerIntent.testStatus( context );
		Log.d( TAG, "Tasker Status for task : '" + taskName + "' is " + taskerStatus );
		
		if ( TaskerIntent.Status.OK.equals( taskerStatus ) ) {
			TaskerIntent i = new TaskerIntent( taskName );
			context.sendBroadcast( i );
		}
		
		return taskerStatus;
	}
	
	public static String[] getProfiles( Context context ){
		
		return null;
		
	}
}

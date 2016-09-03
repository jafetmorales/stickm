

//TODO: USE HANDLER INSTEAD OF ALARMMANAGER

package com.mollys.display;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;


public class stickm_AppWidgetProvider extends AppWidgetProvider {
	// log tag
	private static final String DEBUG_TAG = "mydebug";

	private static final String LOCAL_PREFERENCES_FILE_NAME="com.mollys.display.stickm_prefs";
	private static final String MAIN_PREFERENCES_FILE_NAME="com.mollys.display.main_prefs";

	//App Widget Manager
	private AppWidgetManager mWMan;
	private int mAppWidgetId;
	private Context mAppCtx;
	private RemoteViews mRvs;
	private String mString;
	private int mSkinId;
	private String mSize;
	private boolean mInvisible;
	private long mDeadlineTime;

	static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
			int appWidgetId) {

		Log.d(DEBUG_TAG,"Entering updateAppWidget method in ExampleAppWidgetProvider");

		//LET MAIN APPWIDGETPROVIDER KNOW THAT WE HAVE SIGNED THIS PARTICULAR WIDGET AS OURS
		SharedPreferences.Editor prefsEd=context.getSharedPreferences(MAIN_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE).edit();
		prefsEd.putString("widType"+appWidgetId, "stickm");
		prefsEd.commit();


		//TODO: INSTEAD OF JUST PUTTING HARD CODED TEXT IN THE WIDGET EVERYTIME AN UPDATE IS DONE,
		//READ THE TEXT FROM THE SHARED PREFERENCES!!!
		//ALSO: CHECK IF A LOT LATER AFTER AN UPDATE IS DONE, THE CLICK LISTENER STOPS WORKINGL

		//LIST OF THINGS TO UPDATE:
		//1. THE LAYOUT
		//2. THE TEXT
		//3. THE CLICK LISTENER 
		SharedPreferences localPrefs=context.getSharedPreferences(LOCAL_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
		String text=localPrefs.getString("animatedText"+appWidgetId, "0");
		int layoutId=localPrefs.getInt("skinId"+appWidgetId, 0);
		boolean invisible=localPrefs.getBoolean("invisible"+appWidgetId, true);
		SharedPreferences globalPrefs=context.getSharedPreferences(MAIN_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
		String size=globalPrefs.getString("size"+appWidgetId, "unknown");

		//IN CASE NO PREVIOUSLY SET VALUES FOR TEXT AND LAYOUT WERE FOUND, JUST SET THEM UP HERE
		//AND ALSO STORE THEM IN THE PREFERENCES FILE
		//THIS IF STATEMENT IS USED FOR DEBUGGING AND TO AVOID SAVING TO THE FILE EVERY TIME
		if(text.matches("0") || layoutId==0 || size.matches("unknown"))
		{
			text="ERROR: Unable to initialize note text (stickm_AppWidgetProvider)";//context.getString(R.string.message_default);
			layoutId=R.layout.stickm_small_aw1;
			size="small";

			//STORE VALUES ON PREFERENCES
			SharedPreferences.Editor localPrefsEd=context.getSharedPreferences(LOCAL_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE).edit();
			localPrefsEd.putString("animatedText"+appWidgetId, text);
			localPrefsEd.putInt("skinId"+appWidgetId, layoutId);
			localPrefsEd.commit();
			//			SharedPreferences.Editor globalPrefsEd=context.getSharedPreferences(MAIN_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE).edit();
			//			globalPrefsEd.putString("size"+appWidgetId, size);
			//			globalPrefsEd.commit();
		}

		RemoteViews views = new RemoteViews(context.getPackageName(), layoutId);
		//THIS WORKS FOR ALL LAYOUTS. THE BUTTON IS NAMED R.ID.NAME_BUTTON IN ALL OF THE LAYOUTS!!! 
		if(invisible)
		{
			views.setViewVisibility(R.id.name_button, View.GONE);
			//ALSO UPDATE ALARM SINCE PHONE MIGHT HAVE BEEN TURNED OFF, ERASING THE ALARM TO MAKE IT VISIBLE

			long deadlineTime=localPrefs.getLong("deadlineTime"+appWidgetId, 0);
			Intent updateInt=new Intent(context, stickm_AppWidgetProvider.class);
			updateInt.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			updateInt.putExtra("fromAlarm", true);
			PendingIntent updatePend=PendingIntent.getBroadcast(context, 0, updateInt,PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager alarmMg=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			alarmMg.set(AlarmManager.RTC_WAKEUP, deadlineTime, updatePend);
		}
		else
			views.setViewVisibility(R.id.name_button, View.VISIBLE);			

		views.setTextViewText(R.id.name_button, text);
		Intent clickInt=new Intent(context, stickm_ConfigureActivity1.class);
		clickInt.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		clickInt.setData(ContentUris.withAppendedId(Uri.EMPTY,appWidgetId)); 
		PendingIntent clickPend=PendingIntent.getActivity(context, 0, clickInt,PendingIntent.FLAG_UPDATE_CURRENT);//Intent.FLAG_ACTIVITY_NEW_TASK);
		views.setOnClickPendingIntent(R.id.name_button, clickPend);
		//OKAY, BASICALLY WHAT HAPPENS HERE IS THAT IF A PENDING INTENT IS VERY SIMILAR TO ANOTHER ONE,
		//THEN ANDROID WILL NOT SEND THE NEW ONE AND JUST USE THE OLD ONE. THIS IS WHY YOU WHERE ONLY
		//ABLE TO UPDATE THE LATEST WIDGET THAT YOU ADDED AND ALL THE OTHER ONES YOU COULD NOT UPDATE
		//WHEN YOU ONLY HAD THE FOLLOWING LINE. BUT AFTER THE SECOND LINE FROM HERE BELOW WAS ADDED
		//THEN YOU CAN UPDATE THE WIDGET THAT WAS ACTUALLY CLICKED. THIS IS BECAUSE YOU ARE SENDING THE ID
		//OF THE ANDROID OBJECT THAT WAS ACTED UPON. PENDING INTENTS CANNOT CARRY EXTRAS RELIABLY,
		//BUT THEY CAN ACTUALLY CARRY URI'S RELIABLY

		// Tell the widget manager
		appWidgetManager.updateAppWidget(appWidgetId, views);
		Log.d(DEBUG_TAG,"About to exit updateAppWidget method in ExampleAppWidgetProvider");
	}


	static public void initParams(int appWidgetId,String theText, int skinId, long deadlineTime, boolean invisible,
			Context context)
	{
		Log.d(DEBUG_TAG, "Entering initParams method in stickm_AppWidgetProvider");

		//GET A HOLD OF THE PREFERENCES FILE
		SharedPreferences.Editor localPrefsW=context.getSharedPreferences(LOCAL_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE).edit();

		localPrefsW.putString("animatedText"+appWidgetId, theText);
		localPrefsW.putInt("skinId"+appWidgetId, skinId);
		localPrefsW.putBoolean("invisible"+appWidgetId, invisible);
		//Also save deadlineTime so that alarm can be set again if phone is rebooted
		localPrefsW.putLong("deadlineTime"+appWidgetId, deadlineTime);

		//UPDATE THE TOTAL NUMBER OF WIDGETS AND THEIR IDS
		SharedPreferences localPrefsR=context.getSharedPreferences(LOCAL_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
		int totWids=localPrefsR.getInt("totWids", 0);
		//CHECK TO SEE IF THIS IS A NEW NOTE WIDGET OR IT'S JUST BEING INITIALIZED AGAIN
		boolean isnew=true;
		int widid;
		for(int i=1;i<=totWids;i++)
		{
			widid=localPrefsR.getInt("widId"+i, -9999);		
			if(widid==appWidgetId)
				isnew=false;
		}
		if(isnew)
		{
			totWids=totWids+1;
			localPrefsW.putInt("totWids", totWids);
			localPrefsW.putInt("widId"+totWids, appWidgetId);
			localPrefsW.putBoolean("initialized"+appWidgetId, true);
		}

		//COMMIT ALL CHANGES WHETHER NEW OR NOT
		localPrefsW.commit();

		if(invisible)
		{
			Intent updateInt=new Intent(context, stickm_AppWidgetProvider.class);
			updateInt.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			updateInt.putExtra("fromAlarm", true);
			PendingIntent updatePend=PendingIntent.getBroadcast(context, 0, updateInt,PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager alarmMg=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			alarmMg.set(AlarmManager.RTC_WAKEUP, deadlineTime, updatePend);
		}

		Log.d(DEBUG_TAG, "Exiting initParams method in stickm_AppWidgetProvider");              

		stickm_AppWidgetProvider.updateAppWidget(context, AppWidgetManager.getInstance(context.getApplicationContext()), appWidgetId);

	}
	static public void removeAlarms()
	{
		/*AlarmManager alarmMg=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		  Intent updateInt=new Intent(context, WidgetAnimationProvider.class);
		  updateInt.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		  PendingIntent updatePend=PendingIntent.getBroadcast(context, 0, updateInt,PendingIntent.FLAG_UPDATE_CURRENT);
		  Double dPeriod=PERIOD_SECS*1000;
		  alarmMg.setRepeating(AlarmManager.RTC, SystemClock.elapsedRealtime(), dPeriod.intValue(), updatePend);   
		 */
	}
	public void onReceive(Context context, Intent myIntent)
	{	   
		Log.d(DEBUG_TAG,"Entering onReceive method in WidgetAnimationProvider");
		//FOLLOWING TWO LINES COULD BE DELETED AFTER ADDED IN INITIALIZATION METHOD
		this.mWMan=AppWidgetManager.getInstance(context.getApplicationContext());
		this.mAppWidgetId=myIntent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -999);
		this.mAppCtx=context;
		//GET THE STRING TO BE USED BY THE animateWidget() method of this class
		SharedPreferences prefs=this.mAppCtx.getSharedPreferences(LOCAL_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
		this.mString=prefs.getString("animatedText"+this.mAppWidgetId, "There has been a mistake (ERROR)");
		this.mSkinId=prefs.getInt("skinId"+this.mAppWidgetId, 0);
		this.mDeadlineTime=prefs.getLong("deadlineTime"+this.mAppWidgetId, 0);
		this.mInvisible=prefs.getBoolean("invisible", false);

		//		//UPDATE THE ALARM (BECAUSE ALARMS ARE CLEARED WHEN PHONE IS REBOOTED)
		//		if(myIntent.getBooleanExtra("fromAlarm", false))
		//		{
		SharedPreferences.Editor prefsEd=context.getSharedPreferences(LOCAL_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE).edit();
		prefsEd.putBoolean("invisible"+this.mAppWidgetId, false);
		this.mInvisible=false;
		prefsEd.commit();
		//		}
		//		else
		//		{			
		//			if(this.mInvisible)
		//			{
		//				Intent updateInt=new Intent(context, stickm_AppWidgetProvider.class);
		//				updateInt.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, this.mAppWidgetId);
		//				updateInt.putExtra("fromAlarm", true);
		//				PendingIntent updatePend=PendingIntent.getBroadcast(context, 0, updateInt,PendingIntent.FLAG_UPDATE_CURRENT);
		//				AlarmManager alarmMg=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		//				alarmMg.set(AlarmManager.RTC_WAKEUP, this.mDeadlineTime, updatePend);
		//			}
		//		}

		//DO AN UPDATE ON THE WIDGET THROUGH THE APPWIDGETPROVIDER
		//THIS WILL UPDATE THREE THINGS:
		//1. THE LAYOUT
		//2. THE TEXT
		//3. THE CLICK LISTENER
		stickm_AppWidgetProvider.updateAppWidget(this.mAppCtx, this.mWMan, this.mAppWidgetId);

		animateWidget();
	}

	private void animateWidget(){
		//Set the note field to the new value
		Log.d(DEBUG_TAG,"animateWidget method in WidgetAnimationProvider starting");

		//A FEW CHANGES HAVE TO BE MADE SO THAT WHAT GETS CHOSEN IS NOT
		//A DRAWABLE BUT A BUTTON INSTEAD
		//THEN WHAT GETS CHANGED HERE IS JUST THE ID OF THE BUTTON
		//			  this.mRvs = new RemoteViews(this.mAppCtx.getPackageName(), this.mSkinId);
		//			  this.mRvs.setTextViewText(R.id.name_button, this.mString);
		//			  this.mWMan.updateAppWidget(this.mAppWidgetId, this.mRvs);
		//			  
		//			  SharedPreferences.Editor prefs=this.mAppCtx.getSharedPreferences(LOCAL_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE).edit();
		//			  prefs.putString("animatedText"+this.mAppWidgetId,this.mString);
		//			  prefs.putInt("skinId"+this.mAppWidgetId, this.mSkinId);			  
		//			  prefs.commit();
		Log.d(DEBUG_TAG,"animateWidget about to exit in WidgetAnimationProvider");
	}

	static public void onDeletedMine(Context context, int appWidgetId) {

		//ERASE ALL WIDGET INFORMATION!!!
		
		//GET A HOLD OF THE PREFERENCES FILE
		SharedPreferences.Editor localPrefsW=context.getSharedPreferences(LOCAL_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE).edit();
//		localPrefsW.putString("animatedText"+appWidgetId, "");
		localPrefsW.remove("animatedText"+appWidgetId);
//		localPrefsW.putInt("skinId"+appWidgetId, 0);
		localPrefsW.remove("skinId"+appWidgetId);
//		localPrefsW.putBoolean("invisible"+appWidgetId, false);
		localPrefsW.remove("invisible"+appWidgetId);
		//Also save deadlineTime so that alarm can be set again if phone is rebooted
//		localPrefsW.putLong("deadlineTime"+appWidgetId, 0);
		localPrefsW.remove("deadlineTime"+appWidgetId);
//		localPrefsW.putBoolean("initialized"+appWidgetId, false);			
		localPrefsW.remove("initialized"+appWidgetId);			
		//UPDATE THE TOTAL NUMBER OF WIDGETS AND THEIR IDS
		SharedPreferences localPrefsR=context.getSharedPreferences(LOCAL_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
		int totWids=localPrefsR.getInt("totWids", 0);
		//CHECK TO SEE IF THIS IS A NEW NOTE WIDGET OR IT'S JUST BEING INITIALIZED AGAIN
		int widid;
		int nxt_widid;
		boolean found=false;
		int maxi=totWids;
		for(int i=1;i<=maxi;i++)
		{
			widid=localPrefsR.getInt("widId"+i, -9999);		
			if(widid==appWidgetId)
			{
				found=true;
				totWids=totWids-1;
				localPrefsW.putInt("totWids", totWids);
			}
			if(found && i<maxi)
			{
				int inx=i+1;
				nxt_widid=localPrefsR.getInt("widId"+inx, -9999);
				localPrefsW.putInt("widId"+i, nxt_widid);
			}
		}
		if(found)
			localPrefsW.remove("widId"+maxi);
		//COMMIT ALL CHANGES
		localPrefsW.commit();
	}
}

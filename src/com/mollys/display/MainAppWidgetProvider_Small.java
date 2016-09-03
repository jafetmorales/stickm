package com.mollys.display;

//TODO: WIDGET INFO IS STILL LOST AFTER REBOOT UNTIL A MINUTE PASSES AND AN UPDATE IS DONE.
//WHAT YOU CAN DO TO SOLVE THIS PROBLEM IS TO CALL THE UPDATE METHOD WHEN A PHONE IS REBOOTED
//TO DO THIS JUST LOOK AT THE APPWIDGETPROVIDER METHOD AND FIND THE APPROPIATE ONE. THE APPROPIATE
//METHOD MIGHT ACTUALLY BE ONENABLED, FROM WHICH YOU CAN CALL ON AN UPDATE ON ALL METHODS. MAYBE NOT.
//ANOTHER SOLUTION IS TO GOOGLE IT.

//TODO: TRY USING WHAT IS SHOWN IN http://stackoverflow.com/questions/1937236/android-launching-activity-from-widget
//WHICH MEANS THAT YOU MIGHT NEED TO USE A COMPONENT AND DO IT IN THE ONENABLE METHOD
//TO MAKE SURE THAT YOU CAN STILL PRESS THE BUTTONS EVEN IF THE ONE MINUTE UPDATE HAS NOT BEEN DONE
//ALSO, YOU NEED TO MAKE SURE THAT THE RIGHT WIDGET IS UPDATED ONLY
//APPARENTLY THE PROBLEM IS THAT YOU ARE UPDATING ALL WIDGETS WITH THE SAME LISTENER, NOT JUST ONE
//AND YOU CAN READ THAT IN THE WEB PAGE ABOVE.
//

//IF YOU LOOK AT THE DOCUMENTATION FOR APPWIDGETPROVIDER, YOU CAN SEE THAT THE ONUPDATE METHOD
//IS ONLY CALLED AFTER AN ACTION_APPWIDGET_UPDATE BROADCAST, WHICH IS ACTUALLY INDICATED IN THE
//RECEIVER IN THE MANIFEST ALREADY. BUT ANOTHER INHERITED METHOD CALLED ONRECEIVE IS ACTUALLY CALLED
//UPON AN INTENT. SINCE YOU HAVE SETUP THE PENDINGINTENT IN MAINACTIVITY ALREADY, YOU SHOULD PROBABLY
//USE THE ONRECEIVE METHOD. STILL, THE EXAMPLE_APPWIDGET_INFO.XML CAN BE SETUP SO THAT AN UPDATE
//IS ALSO DONE EVERY SUCH TIME. THAT WOULD BE AN EASIER WAY TO MAKE STYLE CHANGES ONCE A DAY OR EVERY
//1 HOUR. IF YOU RATHER NOT USE THIS, JUST SET THE UPDATE TIME TO 0. YOU SHOULD PROBABLY RANDOMLY
//CHOOSE FONT, COLOR, AND TYPE OF DISPLAY FOR THE TIME. BINARY, HEXADECIMAL, OCTAL, DECIMAL, ETC...
//YOU CAN EVEN CHANGE THE POSITION OF THE WIDGET SO THAT IT BOUNCES AROUND OR THAT IT MOVES LIKE A
//BANNER...THIS MIGHT BE USEFUL FOR FACEBOOK APPS...

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

public class MainAppWidgetProvider_Small extends AppWidgetProvider {
	// log tag
	private static final String DEBUG_TAG = "mydebug";
	private static final String DEBUG_TAG_DELETED = "delWidget";
	private static final String MAIN_PREFERENCES_FILE_NAME="com.mollys.display.main_prefs";

	public static Long LongCurrTime;

	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		final int N = appWidgetIds.length;
		Log.d(DEBUG_TAG,"Inside onUpdate at MainAppWidgetProvider_Small");
		for (int i=0; i<N; i++) {
			SharedPreferences prefs=context.getSharedPreferences(MAIN_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
			String widType=prefs.getString("widType"+appWidgetIds[i], "THERE HAS BEEN A MISTAKE (ERROR)");

			//TURN THIS INTO A CASE AND THEN SOMETHING MORE ELEGANT
			//			picframe_AppWidgetProvider.updateAppWidget(context, appWidgetManager, appWidgetIds[i]);

			if(widType.matches("stickm"))
				stickm_AppWidgetProvider.updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
			else if(widType.matches("picframe"))
				picframe_AppWidgetProvider.updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
			else if(widType.matches("nametag"))
				nametag_AppWidgetProvider.updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
			else
			{
				SharedPreferences.Editor mprefsEd=context.getSharedPreferences(MAIN_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE).edit();
				mprefsEd.putString("size"+appWidgetIds[i], "small");
				mprefsEd.commit();
				updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
			}

			Log.d(DEBUG_TAG,"Inside onUpdate at MainAppWidgetProvider_Small updating appWidget "+appWidgetIds[i]);
		}
	}
	static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
			int appWidgetId) {

		Log.d(DEBUG_TAG,"Entering updateAppWidget method in MainAppWidgetProvider_Small");

		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main_aw);
		//THIS WORKS FOR ALL LAYOUTS. THE BUTTON IS NAMED R.ID.NAME_BUTTON IN ALL OF THE LAYOUTS!!! 
		Intent clickInt=new Intent(context, MainConfigureActivity.class);
		clickInt.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		clickInt.setData(ContentUris.withAppendedId(Uri.EMPTY,appWidgetId)); 
		PendingIntent clickPend=PendingIntent.getActivity(context, 0, clickInt,PendingIntent.FLAG_UPDATE_CURRENT);//Intent.FLAG_ACTIVITY_NEW_TASK);
		views.setOnClickPendingIntent(R.id.config_button, clickPend);
		//OKAY, BASICALLY WHAT HAPPENS HERE IS THAT IF A PENDING INTENT IS VERY SIMILAR TO ANOTHER ONE,
		//THEN ANDROID WILL NOT SEND THE NEW ONE AND JUST USE THE OLD ONE. THIS IS WHY YOU WHERE ONLY
		//ABLE TO UPDATE THE LATEST WIDGET THAT YOU ADDED AND ALL THE OTHER ONES YOU COULD NOT UPDATE
		//WHEN YOU ONLY HAD THE FOLLOWING LINE. BUT AFTER THE SECOND LINE FROM HERE BELOW WAS ADDED
		//THEN YOU CAN UPDATE THE WIDGET THAT WAS ACTUALLY CLICKED. THIS IS BECAUSE YOU ARE SENDING THE ID
		//OF THE ANDROID OBJECT THAT WAS ACTED UPON. PENDING INTENTS CANNOT CARRY EXTRAS RELIABLY,
		//BUT THEY CAN ACTUALLY CARRY URI'S RELIABLY

		// Tell the widget manager
		appWidgetManager.updateAppWidget(appWidgetId, views);
		Log.d(DEBUG_TAG,"About to exit updateAppWidget method in MainAppWidgetProvider_Small");
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {

		//DELETE ANY DATA OR ACTIONS TO BE TAKEN THAT MIGHT BE LEFT BY THE
		//WIDGETS
		final int N = appWidgetIds.length;
		for (int i=0; i<N; i++) {
			SharedPreferences prefs=context.getSharedPreferences(MAIN_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
			String widType=prefs.getString("widType"+appWidgetIds[i], "THERE HAS BEEN A MISTAKE (ERROR)");

			if(widType.matches("stickm"))
				stickm_AppWidgetProvider.onDeletedMine(context, appWidgetIds[i]);
			else if(widType.matches("picframe"))
				;//				picframe_AppWidgetProvider.onDeletedMine(context, appWidgetIds[i]);
			else if(widType.matches("nametag"))
				;//				nametag_AppWidgetProvider.onDeletedMine(context, appWidgetIds[i]);
			else
			{
				//				stickm_AppWidgetProvider.onDeletedMine(context, appWidgetIds[i]);

				//DO ANYTHING THAT NEEDS TO BE DONE TO DELETE THE WIDGET
			}
		}

	}
	@Override
	public void onDisabled(Context context) {
		Log.d("delWidget","An AppWidget is about to be deleted");
		//DELETE ANY DATA OR ACTIONS TO BE TAKEN THAT MIGHT BE LEFT BY THE
		//WIDGETS

		//		SharedPreferences.Editor localPrefsW=context.getSharedPreferences("com.mollys.display.stickm_prefs", Context.MODE_PRIVATE).edit();
		//		localPrefsW.putInt("totWids", 0);
		//		localPrefsW.commit();
	}	

	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();
		if(AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
			final int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
			if(appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
				this.onDeleted(context, new int[] {appWidgetId});
			}
		}
		else {
			super.onReceive(context, intent);
		}
	}
}
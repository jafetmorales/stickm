//TODO: NEED TO FIX THE VISIBILITY OF THE IMAGEVIEWS THAT ARE NOT ACTIVE

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
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;


public class nametag_AppWidgetProvider extends AppWidgetProvider {
	// log tag
	private static final String DEBUG_TAG = "mydebug";

	private static final String LOCAL_PREFERENCES_FILE_NAME="com.mollys.display.nametag_prefs";
	private static final String MAIN_PREFERENCES_FILE_NAME="com.mollys.display.main_prefs";
	private static final int HORZ_BOX_SIZE=20; //WAS 20
	private static final int VERT_BOX_SIZE=16;
	//    private static final double PERIOD_SECS=.5;

	//App Widget Manager
	private AppWidgetManager mWMan;
	private int mAppWidgetId;
	private Context mAppCtx;
	private RemoteViews mRvs;
	private String mString;
	private int[] mSkinIds;

	/////////////////THIS METHOD IS TO BE DELETED SINCE IT IS NEVER CALLED/////////////////////
	//	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
	//		final int N = appWidgetIds.length;
	//		Log.d(DEBUG_TAG,"Inside onUpdate at MainAppWidgetProvider_Small");
	//		for (int i=0; i<N; i++) {
	//			updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
	//			Log.d(DEBUG_TAG,"Inside onUpdate at MainAppWidgetProvider_Small updating appWidget "+appWidgetIds[i]);
	//
	//		}
	//	}

	static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
			int appWidgetId) {

		Log.d(DEBUG_TAG,"Entering updateAppWidget method in ExampleAppWidgetProvider");

		//LET MAIN APPWIDGETPROVIDER KNOW THAT WE HAVE SIGNED THIS PARTICULAR WIDGET AS OURS
		SharedPreferences.Editor mprefsEd=context.getSharedPreferences(MAIN_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE).edit();
		mprefsEd.putString("widType"+appWidgetId, "nametag");
		mprefsEd.commit();

		//TODO: INSTEAD OF JUST PUTTING HARD CODED TEXT IN THE WIDGET EVERYTIME AN UPDATE IS DONE,
		//READ THE TEXT FROM THE SHARED PREFERENCES!!!
		//ALSO: CHECK IF A LOT LATER AFTER AN UPDATE IS DONE, THE CLICK LISTENER STOPS WORKINGL

		//CHANGE WAS MADE HERE IN GREEKLIFE
		//LIST OF THINGS TO UPDATE:
		//1. THE LAYOUT
		//2. THE TEXT
		//3. THE CLICK LISTENER 
		SharedPreferences prefs=context.getSharedPreferences(LOCAL_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
		//		String text=prefs.getString("animatedText"+appWidgetId, "0");
		int[] drawableIds={
				prefs.getInt("skinId0"+appWidgetId, 0),
				prefs.getInt("skinId1"+appWidgetId, 0),
				prefs.getInt("skinId2"+appWidgetId, 0),
				prefs.getInt("skinId3"+appWidgetId, 0),
				prefs.getInt("skinId4"+appWidgetId, 0),
				prefs.getInt("skinId5"+appWidgetId, 0),
				prefs.getInt("skinId6"+appWidgetId, 0)
		};

		SharedPreferences globalPrefs=context.getSharedPreferences(MAIN_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
		String size=globalPrefs.getString("size"+appWidgetId, "unknown");
		
		//CHANGE WAS MADE HERE IN GREEKLIFE
		//CORRECT COMMENT IN NEXT LINE
		int layoutId;
		if(size.matches("small"))
			layoutId=R.layout.nametag_small_aw1;
		else if(size.matches("big"))
			layoutId=R.layout.nametag_big_aw1;
		else
			layoutId=R.layout.nametag_medium_aw1;			
			
		RemoteViews views = new RemoteViews(context.getPackageName(), layoutId);//layoutId);

		//THIS WORKS FOR ALL LAYOUTS. THE BUTTON IS NAMED R.ID.NAME_BUTTON IN ALL OF THE LAYOUTS!!! 
		//CHANGE WAS MADE HERE IN GREEKLIFE
		Intent clickInt=new Intent(context, nametag_ConfigureActivity.class);
		clickInt.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		clickInt.setData(ContentUris.withAppendedId(Uri.EMPTY,appWidgetId)); 
		PendingIntent clickPend=PendingIntent.getActivity(context, 0, clickInt,PendingIntent.FLAG_UPDATE_CURRENT);//Intent.FLAG_ACTIVITY_NEW_TASK);
		//CHANGE WAS MADE HERE IN GREEKLIFE
		//SET THE CLICKERS
//		views.setOnClickPendingIntent(R.id.pic0, clickPend);
//		views.setOnClickPendingIntent(R.id.pic1, clickPend);
//		views.setOnClickPendingIntent(R.id.pic2, clickPend);
//		views.setOnClickPendingIntent(R.id.pic3, clickPend);
//		views.setOnClickPendingIntent(R.id.pic4, clickPend);
//		views.setOnClickPendingIntent(R.id.pic5, clickPend);
//		views.setOnClickPendingIntent(R.id.pic6, clickPend);
		//SET THE IMAGES
		//		views.setImageViewResource(R.id.pic0, drawableIds[0]);
		//		views.setImageViewResource(R.id.pic1, drawableIds[1]);
		//		views.setImageViewResource(R.id.pic2, drawableIds[2]);
		//		views.setImageViewResource(R.id.pic3, drawableIds[3]);
		//		views.setImageViewResource(R.id.pic4, drawableIds[4]);
		//		views.setImageViewResource(R.id.pic5, drawableIds[5]);
		//		views.setImageViewResource(R.id.pic6, drawableIds[6]);
		SharedPreferences.Editor prefsEd=context.getSharedPreferences(LOCAL_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE).edit();		
		int []picIds={R.id.pic0,R.id.pic1,R.id.pic2,R.id.pic3,R.id.pic4,R.id.pic5,R.id.pic6};
		for(int i=0;i<7;i++)
		{
			views.setOnClickPendingIntent(picIds[i], clickPend);	
			if(drawableIds[i]==0)
			{
				prefsEd.putInt("skinId".concat(new Integer(i).toString())+appWidgetId, drawableIds[i]);
//				views.setImageViewResource(picIds[i], 0);
				views.setViewVisibility(picIds[i], 0);
			}
			else
			{
///////////////////////////////////////////////////////////////////////
//				int w=100;
//				int h=100;
//				try{
//					InputStream photoStream = context.getContentResolver().openInputStream(picuri);
//					BitmapFactory.Options options = new BitmapFactory.Options();
//					options.inSampleSize=5;
//					Bitmap photoBitmap = BitmapFactory.decodeStream(photoStream,null,options);
//					h = photoBitmap.getHeight();
//					w = photoBitmap.getWidth();
//					int maxSize=200;
//					if((w>h)&&(w>maxSize)){
//						double ratio = new Double(maxSize)/w;
//						w=maxSize;
//						h=(int)(ratio*h);
//					}
//					else if((h>w)&&(h>maxSize)){
//						double ratio = new Double(maxSize)/h;
//						h=maxSize;
//						w=(int)(ratio*w);
//					}
//					Bitmap scaled = Bitmap.createScaledBitmap(photoBitmap, w, h, true);
//					photoBitmap.recycle();
//					//	         LinearLayout ll=(LinearLayout) findViewById(R.id.previewLayout);
//					//	         ll.setBackgroundDrawable(new BitmapDrawable(scaled));
//					ImageView iv=(ImageView) findViewById(R.id.pic_image_config);
//					iv.setImageDrawable(new BitmapDrawable(scaled));         
//				} catch (FileNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}		    
/////////////////////////////////////////////////////////////////////////////
				views.setViewVisibility(picIds[i], 1);
				views.setImageViewResource(picIds[i], drawableIds[i]);
			}
		}
		prefsEd.commit();

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

	//CHANGE WAS MADE HERE IN GREEKLIFE
	static public void initParams(int appWidgetId, int[] skinIds, Context context)
	{
		Log.d(DEBUG_TAG, "Entering initParams method in nametag_AppWidgetProvider");
		//    	  AppWidgetManager wMan=AppWidgetManager.getInstance(context.getApplicationContext());
		SharedPreferences.Editor prefs=context.getSharedPreferences(LOCAL_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE).edit();

		//CHANGE WAS MADE HERE IN GREEKLIFE
		prefs.putInt("skinId0"+appWidgetId, skinIds[0]);
		prefs.putInt("skinId1"+appWidgetId, skinIds[1]);
		prefs.putInt("skinId2"+appWidgetId, skinIds[2]);
		prefs.putInt("skinId3"+appWidgetId, skinIds[3]);
		prefs.putInt("skinId4"+appWidgetId, skinIds[4]);
		prefs.putInt("skinId5"+appWidgetId, skinIds[5]);
		prefs.putInt("skinId6"+appWidgetId, skinIds[6]);
		prefs.commit();

		Intent updateInt=new Intent(context, nametag_AppWidgetProvider.class);
		updateInt.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		PendingIntent updatePend=PendingIntent.getBroadcast(context, 0, updateInt,PendingIntent.FLAG_UPDATE_CURRENT);
		//		  Double dPeriod=PERIOD_SECS*1000;
		AlarmManager alarmMg=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmMg.set(AlarmManager.RTC, SystemClock.elapsedRealtime()+100, updatePend);
		//		  alarmMg.setRepeating(AlarmManager.RTC, SystemClock.elapsedRealtime(), dPeriod.intValue(), updatePend);   
		Log.d(DEBUG_TAG, "Exiting initParams method in nametag_AppWidgetProvider");

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

		//CHANGE WAS MADE HERE IN GREEKLIFE
		SharedPreferences prefs=this.mAppCtx.getSharedPreferences(LOCAL_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
		this.mString=prefs.getString("animatedText"+this.mAppWidgetId, "There has been a mistake (ERROR)");
		int[] skins={prefs.getInt("skinId0"+this.mAppWidgetId, 0),
				prefs.getInt("skinId1"+this.mAppWidgetId, 0),
				prefs.getInt("skinId2"+this.mAppWidgetId, 0)};
		this.mSkinIds=skins;

		//DO AN UPDATE ON THE WIDGET THROUGH THE APPWIDGETPROVIDER
		//THIS WILL UPDATE THREE THINGS:
		//1. THE LAYOUT
		//2. THE TEXT
		//3. THE CLICK LISTENER
		nametag_AppWidgetProvider.updateAppWidget(this.mAppCtx, this.mWMan, this.mAppWidgetId);

		animateWidget();
	}

	private void animateWidget(){
		//Set the note field to the new value
		Log.d(DEBUG_TAG,"animateWidget method in WidgetAnimationProvider starting");

		//CHANGE WAS MADE HERE IN GREEKLIFE
		//			  this.mRvs = new RemoteViews(this.mAppCtx.getPackageName(), R.layout.greeklife_config);
		//			  this.mRvs.setImageViewResource(R.id.greeklife_image_top, this.mSkinIds[0]);
		//			  this.mWMan.updateAppWidget(this.mAppWidgetId, this.mRvs);
		//			  this.mRvs.setImageViewResource(R.id.greeklife_image_middle, this.mSkinIds[1]);
		//			  this.mWMan.updateAppWidget(this.mAppWidgetId, this.mRvs);
		//			  this.mRvs.setImageViewResource(R.id.greeklife_image_bottom, this.mSkinIds[2]);
		//			  this.mWMan.updateAppWidget(this.mAppWidgetId, this.mRvs);
		//			  
		//			  //CHANGE WAS MADE HERE IN GREEKLIFE
		//			  SharedPreferences.Editor prefs=this.mAppCtx.getSharedPreferences(LOCAL_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE).edit();
		//			  prefs.putString("animatedText"+this.mAppWidgetId,this.mString);
		//			  prefs.putInt("skinId0"+this.mAppWidgetId, this.mSkinIds[0]);			  
		//			  prefs.putInt("skinId1"+this.mAppWidgetId, this.mSkinIds[1]);			  
		//			  prefs.putInt("skinId2"+this.mAppWidgetId, this.mSkinIds[2]);			  
		//			  prefs.commit();
		Log.d(DEBUG_TAG,"animateWidget about to exit in WidgetAnimationProvider");
	}   
}

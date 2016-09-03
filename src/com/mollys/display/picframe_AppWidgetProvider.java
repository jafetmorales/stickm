

//TODO: RESOLUTION IS THE PROBLEM. 1280 x 960 WORKS BUT 2560 X 1920 DOES NOT

package com.mollys.display;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.SystemClock;
import android.widget.RemoteViews;


public class picframe_AppWidgetProvider extends AppWidgetProvider {
	// log tag
    private static final String DEBUG_TAG = "mydebug";
    
    private static final String LOCAL_PREFERENCES_FILE_NAME="com.mollys.display.picframe_prefs";
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
    private String mPicuri;
    
	static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
			int appWidgetId) {

		//LET MAIN APPWIDGETPROVIDER KNOW THAT WE HAVE SIGNED THIS PARTICULAR WIDGET AS OURS
		SharedPreferences.Editor mprefsEd=context.getSharedPreferences(MAIN_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE).edit();
		mprefsEd.putString("widType"+appWidgetId, "picframe");
		mprefsEd.commit();

		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.picframe_aw1);//layoutId);

//		Uri uri = Uri.fromFile();
//		java.net.URI delme=new java.net.URI(uri.toString());

//		Uri uri=Uri.fromFile(new File("/sdcard/TempPicture.jpg"));
//		Uri uri=Uri.fromFile(new File("content://media/external/images/media/22"));
//		Uri uri=Uri.parse("content://media/external/images/media/1");
//				"/sdcard/images2.jpg");
//				"sdcard/DCIM/Camera/2010-02-14 16.16.36.jpg");
//				"content://media/external/images/media/22");
//				"http://www.filebuzz.com/software_screenshot/full/design_icon_set-48197.jpg");
//				"http://www.preceptor.ca/images/forDocs/reference-icon.jpg");		

		SharedPreferences prefs=context.getSharedPreferences(LOCAL_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
		String spicuri=prefs.getString("uri"+appWidgetId, "ERROR");
		int skinId=prefs.getInt("skinid"+appWidgetId, 0);
		Uri picuri=Uri.parse(spicuri);
		
		if(spicuri.matches("ERROR") || skinId==0)
		{
			
			spicuri="";
			skinId=0;
			
			//STORE VALUES ON PREFERENCES
			SharedPreferences.Editor prefsEd=context.getSharedPreferences(LOCAL_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE).edit();
			prefsEd.putString("uri"+appWidgetId, "");
			prefsEd.putInt("skinid"+appWidgetId, R.drawable.picframe_frame1);
			prefsEd.commit();
		}
		
	    try{
	        InputStream photoStream = context.getContentResolver().openInputStream(picuri);
	        BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inSampleSize=5;
	        Bitmap photoBitmap = BitmapFactory.decodeStream(photoStream,null,options);
	        Resources res=context.getResources();
	        
	        BitmapDrawable bd=(BitmapDrawable)res.getDrawable(skinId);
	        Bitmap frameBitmap=bd.getBitmap();
	        
	        int h_pic = photoBitmap.getHeight();
	        int w_pic = photoBitmap.getWidth();
	        int maxSize=300;
	        if((w_pic>h_pic)&&(w_pic>maxSize)){
	            double ratio = new Double(maxSize)/w_pic;
	            w_pic=maxSize;
	            h_pic=(int)(ratio*h_pic);
	        }
	        else if((h_pic>w_pic)&&(h_pic>maxSize)){
	            double ratio = new Double(maxSize)/h_pic;
	            h_pic=maxSize;
	            w_pic=(int)(ratio*w_pic);
	        }
	         Bitmap picScaled = Bitmap.createScaledBitmap(photoBitmap, w_pic, h_pic, true);
//	         photoBitmap.recycle();
	         Bitmap frameScaled = Bitmap.createScaledBitmap(frameBitmap, w_pic+5, h_pic+5, true);
//	         frameBitmap.recycle();
	         views.setImageViewBitmap(R.id.pic_image, picScaled);
	         views.setImageViewBitmap(R.id.pic_frame, frameScaled);
        } catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
    	e.printStackTrace();
	}
        
		Intent clickInt=new Intent(context, picframe_ConfigureActivity.class);
		clickInt.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		clickInt.setData(ContentUris.withAppendedId(Uri.EMPTY,appWidgetId)); 
		PendingIntent clickPend=PendingIntent.getActivity(context, 0, clickInt,PendingIntent.FLAG_UPDATE_CURRENT);//Intent.FLAG_ACTIVITY_NEW_TASK);
		views.setOnClickPendingIntent(R.id.pic_image, clickPend);
		appWidgetManager.updateAppWidget(appWidgetId, views);
	}
    
       //CHANGE WAS MADE HERE IN GREEKLIFE
       static public void initParams(int appWidgetId, Context context, Uri picuri, int skinId)
       {
//    	  DELETE THESE TWO LINES
//    	  AppWidgetManager wman=AppWidgetManager.getInstance(context.getApplicationContext());
//    	  updateAppWidget(context, wman, appWidgetId); 
    	   
		  SharedPreferences.Editor prefs=context.getSharedPreferences(LOCAL_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE).edit();
		
		  //CHANGE WAS MADE HERE IN GREEKLIFE
		  prefs.putString("uri"+appWidgetId, picuri.toString());
		  prefs.putInt("skinid"+appWidgetId, skinId);
		  prefs.commit();
		  
		  Intent updateInt=new Intent(context, picframe_AppWidgetProvider.class);
		  updateInt.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		  PendingIntent updatePend=PendingIntent.getBroadcast(context, 0, updateInt,PendingIntent.FLAG_UPDATE_CURRENT);
		  AlarmManager alarmMg=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		  alarmMg.set(AlarmManager.RTC, SystemClock.elapsedRealtime()+100, updatePend);
//		  alarmMg.setRepeating(AlarmManager.RTC, SystemClock.elapsedRealtime(), dPeriod.intValue(), updatePend);                 
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
		   this.mWMan=AppWidgetManager.getInstance(context.getApplicationContext());
		   this.mAppWidgetId=myIntent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -999);
		   this.mAppCtx=context;
		   
		   //CHANGE WAS MADE HERE IN GREEKLIFE
//		   SharedPreferences prefs=this.mAppCtx.getSharedPreferences(LOCAL_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
//		   this.mPicuri=prefs.getString("uri"+this.mAppWidgetId, "ERROR");
		   
		   //DO AN UPDATE ON THE WIDGET THROUGH THE APPWIDGETPROVIDER
		   //THIS WILL UPDATE THREE THINGS:
		   //1. THE LAYOUT
		   //2. THE TEXT
		   //3. THE CLICK LISTENER

		   //UNCOMMENT FOLLOWING LINE
		   picframe_AppWidgetProvider.updateAppWidget(this.mAppCtx, this.mWMan, this.mAppWidgetId);
		   
		   animateWidget();
	   }
	   
	   private void animateWidget(){
	   }   
}

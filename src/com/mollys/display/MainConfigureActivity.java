package com.mollys.display;


import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainConfigureActivity extends Activity {

	private static final String DEBUG_TAG = "mydebug";

	private static final String PREF_PREFIX_KEY = "prefix_";

	private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	private EditText mAppWidgetPrefix;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.d(DEBUG_TAG,"Entering onCreate method in ConfigureTabActivity");
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_config);
		
		Bundle extras=getIntent().getExtras();
		int wid_id=extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
		
		Button but1=(Button) findViewById(R.id.test_button1);
//		Button but2=(Button) findViewById(R.id.test_button2);
//		Button but3=(Button) findViewById(R.id.test_button3);
		but1.setOnClickListener(new ConfigListener(this.getApplicationContext(), wid_id, com.mollys.display.stickm_ConfigureActivity1.class));
//		but2.setOnClickListener(new ConfigListener(this.getApplicationContext(), wid_id, com.mollys.display.picframe_ConfigureActivity.class));
//		but3.setOnClickListener(new ConfigListener(this.getApplicationContext(), wid_id, com.mollys.display.nametag_ConfigureActivity.class));

//		Intent widgetConfig=new Intent( this.getApplicationContext(), com.mollys.display.picframe_ConfigureActivity.class);
//		widgetConfig.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, wid_id);
//		widgetConfig.setData(ContentUris.withAppendedId(Uri.EMPTY,wid_id)); 
//		startActivity(widgetConfig);
//
//		finish();

	}

	private class ConfigListener implements OnClickListener {
		private Context mCtx;
		private int mWid;
		private Class mWconfig;
		public ConfigListener(Context appCtx,int appWidgetId, Class wconfig)
		{
			this.mCtx=appCtx;
			this.mWid=appWidgetId;
			this.mWconfig=wconfig;
		}
		public void onClick(View v)
		{
			Intent widgetConfig=new Intent( this.mCtx, this.mWconfig);
			widgetConfig.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, this.mWid);
			widgetConfig.setData(ContentUris.withAppendedId(Uri.EMPTY,this.mWid)); 
			startActivity(widgetConfig);

			finish();
		}
	}
}


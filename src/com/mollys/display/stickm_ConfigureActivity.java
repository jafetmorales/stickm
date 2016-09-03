package com.mollys.display;


import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.AdapterView.OnItemSelectedListener;

public class stickm_ConfigureActivity extends Activity {

	private static final double PERIOD_SECS=.5;
	private static final String DEBUG_TAG = "mydebug";

	private static final String LOCAL_PREFERENCES_FILE_NAME="com.mollys.display.stickm_prefs";
	private static final String MAIN_PREFERENCES_FILE_NAME="com.mollys.display.main_prefs";

	private static final String PREF_PREFIX_KEY = "prefix_";

	private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	private EditText mAppWidgetPrefix;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.d(DEBUG_TAG,"Entering onCreate method in ConfigureTabActivity");
		super.onCreate(savedInstanceState);

		setContentView(R.layout.stickm_config);
		Button saveButton=(Button)findViewById(R.id.save_button);
		Bundle extras=getIntent().getExtras();
		int appWidgetId=extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);

		//SET THE DEFAULT TEXT TO WHATEVER THE USER HAD SAVED BEFORE
		EditText nameTextEd=(EditText)findViewById(R.id.name_edit);
		SharedPreferences prefs=this.getApplicationContext().getSharedPreferences(LOCAL_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
		String text=prefs.getString("animatedText"+appWidgetId,"Insert text");
		nameTextEd.setText(text);

		//SET UP GALLERY AND LISTENER
		Gallery g = (Gallery) findViewById(R.id.gallery);
		//THE ADAPTER PROVIDES THE DATA WHICH BACKS THE SPINNER
		g.setAdapter(new ImageAdapter(this,appWidgetId));
		g.setSpacing(10);
		//SET A CALLBACK FOR WHEN AN ITEM HAS BEEN SELECTED
		g.setOnItemSelectedListener(new galleryListener(this.getApplicationContext()));

		//INITIALIZE PREVIEW BUTTON
		//1. INIT LAYOUT
		//2. INIT TEXT
		//THIS IS WHAT REALLY SHOULD BE SET: THE DRAWABLE. BUT YOU DON'T WANT TO SAVE THIS IN
		//THE PREFERENCES FILE SO YOU SHOULD FIND A WAY TO DO IT USING A HASH TABLE
		//THAT IS ACCESSIBLE TO ALL CLASSES IN CONFIGUREACTIVITY AND ALSO TO WIDGETSERVICEPROVIDER
		Button but = (Button) findViewById(R.id.preview_button);
		but.setText(text);	  	 

		LinearLayout ll=(LinearLayout) this.findViewById(R.id.reminderLayout);
		ll.setVisibility(View.GONE);

		final CheckBox checkbox1 = (CheckBox) findViewById(R.id.checkbox_deadline);
		checkbox1.setOnClickListener(new DeadlineCBListener(ll));
		final CheckBox checkbox2 = (CheckBox) findViewById(R.id.checkbox_popup);
		//		checkbox2.setOnClickListener(new PopupCBListener(ll));
		final CheckBox checkbox3 = (CheckBox) findViewById(R.id.checkbox_invisible);
		//		checkbox3.setOnClickListener(new InvisibleCBListener(ll));


		//SET SAVE BUTTON LISTENER
		saveButton.setOnClickListener(new saveListener(this.getApplicationContext(),appWidgetId,
				nameTextEd,g, checkbox1,checkbox2,checkbox3,this));
	}

	private class galleryListener implements OnItemSelectedListener{
		private Context mAppCtx;

		public galleryListener(Context appCtx)
		{
			mAppCtx=appCtx;
		}

		public void onItemSelected(AdapterView parent, View v,  int position, long id)
		{
			//		  Toast.makeText(mAppCtx, ""+v.getId() , Toast.LENGTH_SHORT).show();

			Button but = (Button) findViewById(R.id.preview_button);
			EditText et = (EditText) findViewById(R.id.name_edit);
			but.setBackgroundResource(v.getId());
			but.setText(et.getText());
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub		
		}


	}

	private class DeadlineCBListener implements OnClickListener{
		private LinearLayout mll;
		public DeadlineCBListener(LinearLayout ll)
		{
			mll=ll;
		}

		public void onClick(View v) {
			int vis=mll.getVisibility();
			if (((CheckBox) v).isChecked()) {
				//              Toast.makeText(ConfigureActivity.this, "Selected", Toast.LENGTH_SHORT).show();
				mll.setVisibility(View.VISIBLE);
			} else {
				//              Toast.makeText(ConfigureActivity.this, "Not selected", Toast.LENGTH_SHORT).show();
				mll.setVisibility(View.GONE);
			}
		}
	}


	public class ImageAdapter extends BaseAdapter {
		int mGalleryItemBackground;
		private Context mContext;
		private int mWidId;
		//
		//	    public Integer[] mLayoutIds = {
		//	            R.layout.stickm_small_aw1,
		//	            R.layout.stickm_small_aw2,
		//	            R.layout.stickm_small_aw3,
		//	            R.layout.stickm_small_aw4,
		//	            R.layout.stickm_small_aw5,
		//	            R.layout.stickm_small_aw6,
		//	            R.layout.stickm_small_aw7
		//	            };
		//	    public Integer[] mDrawableIds = {
		//	    		R.drawable.stickm_skin1,
		//	    		R.drawable.stickm_skin2,
		//	    		R.drawable.stickm_skin3,
		//	    		R.drawable.stickm_skin4,
		//	    		R.drawable.stickm_skin5,
		//	    		R.drawable.stickm_skin6,
		//	    		R.drawable.stickm_skin7
		//	    		};

		private HashMap mDrawableMap;
		private HashMap mButtonMap;

		public ImageAdapter(Context c,int widId) {
			mContext = c;
			mWidId=widId;

			//	        mDrawableMap.put(new Integer(0), R.drawable.yellow);
			//	        mButtonMap.put(new Integer(0), R.id.name_button);
			//	        mDrawableMap.put(new Integer(1), R.drawable.icon);
			//	        mButtonMap.put(new Integer(1), R.id.name_button2);

			//      TypedArray a = obtainStyledAttributes(android.R.styleable.Theme);
			//      mGalleryItemBackground = a.getResourceId(
			//              android.R.styleable.Theme_galleryItemBackground, 0);
			//      a.recycle();
		}

		//FIX THIS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		public int getCount() {
			return stickm_items.mDrawableIds.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		//FIX THIS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		public int getLayoutResId(int position) {
			//	    	long resId=(Long)mButtonMap.get(new Integer(position));
			//	    	return resId;

			//NOT SUPPOSED TO BE LIKE THIS
			//	    	return this.mLayoutIds[position];
			SharedPreferences globalPrefs=mContext.getSharedPreferences(MAIN_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
			String size=globalPrefs.getString("size"+mWidId, "unknown");
			if(size.matches("small"))
				return stickm_items.mLayoutIds_small[position];				
			else if(size.matches("big"))
				return stickm_items.mLayoutIds_big[position];				
			else
				return stickm_items.mLayoutIds_medium[position];				

			//return mButtonIds[position];
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(mContext);

			Resources res=mContext.getResources();
			Drawable dw=(Drawable)res.getDrawable(stickm_items.mDrawableIds[position]/*(Integer)this.mDrawableMap.get(new Integer(position))*/);//b.getBackground();

			i.setImageDrawable(dw);
			i.setLayoutParams(new Gallery.LayoutParams(100, 100));
			i.setScaleType(ImageView.ScaleType.FIT_XY);
			i.setId(stickm_items.mDrawableIds[position]/*(Integer)this.mDrawableMap.get(new Integer(position))*/);

			return i;
		}
	}

	private class saveListener implements OnClickListener {
		private int appWidgetId;
		private Context mAppCtx;
		private AppWidgetManager mWMan;
		private String mText;
		private long mSkinId;
		private EditText mNameTextEd;
		private String mSize;
		private Gallery mGal;
		private CheckBox mcb1;
		private CheckBox mcb2;
		private CheckBox mcb3;
		private Activity mcaller;

		public saveListener(Context appCtx,int appWidgetId, EditText nameTextEd, Gallery g, CheckBox cb1, CheckBox cb2, CheckBox cb3, Activity caller)
		{
			this.appWidgetId=appWidgetId;
			this.mAppCtx=appCtx;
			this.mWMan=(AppWidgetManager)AppWidgetManager.getInstance(this.mAppCtx);
			//		this.mText=theText;
			//		this.mSkinId=skinId;
			this.mNameTextEd=nameTextEd;
			//		this.mSize=size;
			this.mGal=g;
			this.mcb1=cb1;
			this.mcb2=cb2;
			this.mcb3=cb3;
			this.mcaller=caller;
		}
		public void onClick(View v)
		{
			long deadlineTime=0;//System.currentTimeMillis();
			boolean invisible=false;
			
			int pos=this.mGal.getSelectedItemPosition();
			ImageAdapter adapt=(ImageAdapter) this.mGal.getAdapter();

			if(mcb1.isChecked())
			{
				DatePicker datePick=(DatePicker) findViewById(R.id.DatePicker01);
				TimePicker timePick=(TimePicker) findViewById(R.id.TimePicker01);
				
				if(mcb2.isChecked())
				{
					Reminder rem=new Reminder(this.mAppCtx,this.mcaller);
					//GET EVENT VARIABLES
					rem.setSubj("Reminder");			
					rem.setDesc(this.mNameTextEd.getText().toString());
					rem.setDay(datePick.getDayOfMonth());
					rem.setMonth(datePick.getMonth());
					rem.setYear(datePick.getYear()-1900);
					rem.setHour(timePick.getCurrentHour());
					rem.setMin(timePick.getCurrentMinute());
					rem.setPriv(2);
					rem.commit();
				}
				if(mcb3.isChecked())
				{
					Date adate = new Date(datePick.getYear()-1900,datePick.getMonth(),datePick.getDayOfMonth());
					adate.setHours(timePick.getCurrentHour());
					adate.setMinutes(timePick.getCurrentMinute());
					deadlineTime=adate.getTime();
					invisible=true;
				}
			}
			stickm_AppWidgetProvider.initParams(this.appWidgetId, this.mNameTextEd.getText().toString(), adapt.getLayoutResId(pos), deadlineTime, invisible, this.mAppCtx);

			/*RemoteViews rvs = new RemoteViews(this.mAppCtx.getPackageName(), R.layout.appwidget_config1);
		  rvs.setTextViewText(R.id.name_button, theText);
		  SharedPreferences.Editor prefs=this.mAppCtx.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE).edit();
		  prefs.putString("originalText"+this.appWidgetId,theText);
		  this.mWMan.updateAppWidget(this.appWidgetId, rvs);

		  //Set the animation
		  AlarmManager alarmMg=(AlarmManager) getSystemService(ALARM_SERVICE);
		  Intent updateInt=new Intent(this.mAppCtx, WidgetAnimationProvider.class);
		  updateInt.putExtra(this.mWMan.EXTRA_APPWIDGET_ID, this.appWidgetId);
		  prefs.putString("animatedText"+this.appWidgetId, "          "+theText+"          ");//was the text
		  //Save initial parameters for horizontal motion
		  prefs.putInt("animatedHorzPosition"+this.appWidgetId, 10);
		  prefs.putInt("animatedHorzPositionEnd"+this.appWidgetId,10+theText.length()-1);
		  prefs.putInt("animatedHorzDirection"+this.appWidgetId, 1);
		  //Save initial parameters for vertical motion
		  String animatedText=theText;
		  animatedText="\n\n\n\n\n\n\n\n\n\n          "+animatedText;
		  int i;
		  for(i=1;i<=(10-theText.length());i++) animatedText=animatedText+" ";
		  animatedText=animatedText+"\n\n\n\n\n\n";
		  prefs.putString("animatedText"+this.appWidgetId, animatedText);
		  prefs.putInt("animatedVertPosition"+this.appWidgetId, 10);
		  prefs.putInt("animatedVertDirection"+this.appWidgetId, 1);
		  prefs.commit();
		  PendingIntent updatePend=PendingIntent.getBroadcast(this.mAppCtx, 0, updateInt,PendingIntent.FLAG_UPDATE_CURRENT);
		  Double dPeriod=PERIOD_SECS*1000;
		  alarmMg.setRepeating(AlarmManager.RTC, SystemClock.elapsedRealtime(), dPeriod.intValue(), updatePend);

		  Log.d(DEBUG_TAG,"onClick method about to return: text is: "+theText+" appWidgetId is: "+this.appWidgetId);
			 */
			finish();
		}
	}


	/*  private class calendarButListen extends butListen {
	  public calendarButListen(int appWidgetId, Context appCtx)
	  {
		  super(appWidgetId,appCtx);
	  }

	  @Override
	  public void onClick(View v)
	  {
			 Intent i = new Intent();
			 i.setAction( "mollys.intent.action.CALENDAR" );
			 startActivity(i);		  
	  }

  }*/

}


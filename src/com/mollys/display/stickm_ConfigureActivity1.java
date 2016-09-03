package com.mollys.display;


import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class stickm_ConfigureActivity1 extends Activity {

	private static final String DEBUG_TAG = "mydebug";

	private static final String LOCAL_PREFERENCES_FILE_NAME="com.mollys.display.stickm_prefs";
	private static final String MAIN_PREFERENCES_FILE_NAME="com.mollys.display.main_prefs";

	private ListView lv1;
	private String lv_arr[]={"Android","iPhone","BlackBerry","AndroidPeople"};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.d(DEBUG_TAG,"Entering onCreate method in ConfigureTabActivity");
		super.onCreate(savedInstanceState);

		setContentView(R.layout.stickm_config1);
		initList();
	}
	
	private void initList()
	{
		Bundle extras=getIntent().getExtras();
		int appWidgetId=extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);

		lv1=(ListView)findViewById(R.id.ListView01);
		// By using setAdpater method in listview we an add string array in list.
		//lv1.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 , lv_arr));

		//READ ALL WIDGET INFORMATION FROM PREFERENCES FILE
		SharedPreferences localPrefsR=getApplicationContext().getSharedPreferences(LOCAL_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
		boolean initialized=localPrefsR.getBoolean("initialized"+appWidgetId, false);
		int numWids=localPrefsR.getInt("totWids", 0);
		if(!initialized)
			numWids=numWids+1;
		Note noteArr[]=new Note[numWids];//currNumWids];
		for(int i=1;i<numWids;i++)
		{
			//RETRIEVE THE MINIMUM NEEDED INFORMATION FOR A NOTE
			int widid=localPrefsR.getInt("widId"+i, -9999);		
			String widText=localPrefsR.getString("animatedText"+widid, "ERROR: Cannot recover note text (stickm_ConfigureActivity1)");
			//RETRIEVE EXTRA INFORMATION FOR A NOTE
			long deadline=localPrefsR.getLong("deadlineTime"+widid, 0);			
			//SET REQUIRED INFO FOR A NOTE
			noteArr[i-1]=new Note();
			noteArr[i-1].setText(widText);
			noteArr[i-1].setWid(widid);
			//SET EXTRA STUFF FOR A NOTE
			noteArr[i-1].setDeadline(deadline);				
		}
		if(!initialized)
		{
			//SET REQUIRED INFO FOR A NEW NOTE
			noteArr[numWids-1]=new Note();
			noteArr[numWids-1].setText("[Add Note]");
			noteArr[numWids-1].setWid(appWidgetId);					
		}
		else
		{
			//RETRIEVE THE MINIMUM NEEDED INFORMATION FOR A NOTE
			int widid=localPrefsR.getInt("widId"+numWids, -9999);		
			String widText=localPrefsR.getString("animatedText"+widid, "ERROR: Cannot recover note text (stickm_ConfigureActivity1)");
			//RETRIEVE EXTRA INFORMATION FOR A NOTE
			long deadline=localPrefsR.getLong("deadlineTime"+widid, 0);			

			//SET REQUIRED INFO FOR A NOTE
			noteArr[numWids-1]=new Note();
			noteArr[numWids-1].setText(widText);
			noteArr[numWids-1].setWid(widid);							

			//SET EXTRA STUFF FOR A NOTE
			noteArr[numWids-1].setDeadline(deadline);					
		}
		lv1.setAdapter(new MyListAdapter(this,noteArr));		
	}

	private class Note{
		private String mText;
		private int mWid;
		private long mDueBy=0;

		public void setText(String text)
		{
			mText=text;
		}
		public void setWid(int wid)
		{
			mWid=wid;
		}
		public void setDeadline(long dueBy)
		{
			mDueBy=dueBy;
		}
		public String getText()
		{
			return mText;
		}
		public int getWid()
		{
			return mWid;
		}
		public long getDeadline()
		{
			return mDueBy;
		}
	}

	private class MyListAdapter extends BaseAdapter
	{
		private Context mContext;
		//		private String[] mTitles = 
		//		{
		//				"Henry IV (1)",   
		//				"Henry V",
		//				"Henry VIII",       
		//				"Richard II",
		//				"Richard III",
		//				"Merchant of Venice",  
		//				"Othello",
		//				"King Lear"
		//		};
		private Note mNotes[];

		public MyListAdapter(Context context, Note noteArr[]) {
			mContext = context;
			mNotes=noteArr;
		}
		public int getCount() {
			return mNotes.length;
		}

		public Object getItem(int position) {
			return position;
		}
		public long getItemId(int position) {
			return position;
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			SpeechView sv;
//			if (convertView == null) {
				sv = new SpeechView(mContext, mNotes[position].getWid(), mNotes[position].getText());
				long deadline=mNotes[position].getDeadline();
				if(deadline!=0)
					sv.setNoteDeadline(deadline);
//			} else {
//				sv = (SpeechView) convertView;
////				sv.mNote.setText(mNotes[position].getText());
////				sv.setNoteDeadline(mNotes[position].getDeadline());
//			}
			return sv;
		}

	}
	private class SpeechView extends LinearLayout {
		private Context mContext;
		private int mwid;
		private TextView mNote;
		
		//ONLY THESE THREE PARAMETERS ARE ACTUALLY NEEDED
		public SpeechView(Context context, int wid, String text) {
			super(context);
			mContext=context;
			mwid=wid;

			this.setOrientation(VERTICAL);

			mNote=new TextView(context);
			mNote.setText(text);
			mNote.setBackgroundColor(Color.BLACK);		
			mNote.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
			mNote.setOnClickListener(new NoteClickListener(mContext,mwid));
			addView(mNote);
		}
		/**
		 * Convenience method to set the title of a SpeechView
		 */
//		public void setNoteText(String text) {
//			mNote.setText(text);
//		}
		public void setNoteDeadline(long deadline) {
			TextView deadlineTV;
			deadlineTV=new TextView(mContext);
			deadlineTV.setText("Due by: ");
			deadlineTV.setBackgroundColor(Color.BLACK);		
			deadlineTV.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			//			mDue.setOnClickListener(new NoteClickListener(mContext,mwid));
			deadlineTV.setText("Due By: "+deadline);
			addView(deadlineTV);
		}

		private class NoteClickListener implements OnClickListener{
			private int mwid;
			private Context mContext;
			public NoteClickListener(Context context, int wid)
			{
				mContext=context;
				mwid=wid;
			}

			public void onClick(View v) {
				Intent clickInt=new Intent(mContext, stickm_ConfigureActivity.class);
				clickInt.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mwid);
				startActivityForResult(clickInt,0);
			}
		}

	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		initList();
	}


}


//public class ListviewExample extends Activity
//{
//private ListView lv1;
//private String lv_arr[]={"Android","iPhone","BlackBerry","AndroidPeople"};
//@Override
//public void onCreate(Bundle icicle)
//{
//super.onCreate(icicle);
//setContentView(R.layout.main);
//lv1=(ListView)findViewById(R.id.ListView01);
//// By using setAdpater method in listview we an add string array in list.
//lv1.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 , lv_arr));
//}
//}

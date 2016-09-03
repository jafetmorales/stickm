//THE STARTTIME AND ENDTIME OF THIS REMINDER CAN ONLY BE OBTAINED ONCE THE REMINDER HAS BEEN COMMITED

package com.mollys.display;

import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;


public class Reminder {
	private Context mctx;
	private int year=0;
	private int month=0;
	private int day=0;
	private int hour=0;
	private int min=0;
	private String subj="Reminder";
	private String desc="Reminder";
	private int priv=2;
	private Activity mcaller;
	private long mStartTime;
	private long mEndTime;

	public Reminder(Context ctx, Activity caller)
	{
		mctx=ctx;
		mcaller=caller;
	}

	public void setYear(int y){year=y;}
	public void setMonth(int m){month=m;}
	public void setDay(int d){day=d;}
	public void setHour(int h){hour=h;}
	public void setMin(int m){min=m;}
	public void setSubj(String subject){subj=subject;}
	public void setDesc(String description){desc=description;}
	public void setPriv(int privacy){priv=privacy;}
	public long getStartTime(){return mStartTime;}
	public long getEndTime(){return mEndTime;}

	public void commit()
	{
		Date reminderDate = new Date(year,month,day);
		reminderDate.setHours(hour);
		reminderDate.setMinutes(min);
		long time=reminderDate.getTime();
		mStartTime = time;//System.//.currentTimeMillis()+5000;//START_TIME_MS;
		mEndTime = time;//System.currentTimeMillis()+5000+30000;//END_TIME_MS;

		String calName; 
		String calId=null;

		String[] projection = new String[] { "_id", "name" };
		Uri calendars = Uri.parse("content://calendar/calendars");
		Cursor managedCursor =mcaller.managedQuery(calendars, projection, null, null, null);
		if (managedCursor.moveToFirst()) {

			int nameColumn = managedCursor.getColumnIndex("name"); 
			int idColumn = managedCursor.getColumnIndex("_id");
			do {
				calName = managedCursor.getString(nameColumn);
				calId = managedCursor.getString(idColumn);
			} while (managedCursor.moveToNext());
		}

		if(calId!=null)
		{
			ContentValues event = new ContentValues();
			event.put("calendar_id", calId);
			event.put("description", desc);
			event.put("title", desc);
			event.put("dtstart", mStartTime);
			event.put("dtend", mEndTime);
			event.put("visibility", priv);
			event.put("transparency", 0);
			event.put("hasAlarm", 1); // 0 for false, 1 for true
			Uri eventsUri = Uri.parse("content://calendar/events");
			Uri eventsUrl = mctx.getContentResolver().insert(eventsUri, event);

			//GET BACK THE ID THAT THE EVENT WAS INSERTED WITH
			Cursor eventCursor = mctx.getContentResolver().query(eventsUri,
					new String[] { "_id"}, null,
					null, "_id DESC");
			eventCursor.moveToFirst();
			int eventId=eventCursor.getInt(0);				
			//NOTE: A MUCH EASIER WAY TO GET BACK THE ID IS TO DO:
			//long id=Long.parseLong(eventsUrl.getLastPathSegment());

			//CONNECT TO THE REMINDERS CONTENT PROVIDER AND ADD A REMINDER FOR THIS EVENT ID
			int alarmTimeMins1 = 1; 

			ContentValues reminder = new ContentValues();
			reminder.put("event_id",eventId);//was eventId instead of 999
			reminder.put("method", 1);
			reminder.put("minutes", alarmTimeMins1);
			Uri remindersUri = Uri.parse("content://calendar/reminders");
			Uri remUrl = mctx.getContentResolver().insert(remindersUri, reminder);			

			Toast toast=Toast.makeText(mctx, "Your reminder has been set", Toast.LENGTH_LONG);
			toast.show();

		}
	}
}

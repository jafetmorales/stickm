package com.mollys.display;


import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class nametag_ConfigureActivity extends Activity {

 private static final double PERIOD_SECS=.5;
 private static final String DEBUG_TAG = "mydebug";

 private static final String LOCAL_PREFERENCES_FILE_NAME="com.mollys.display.nametag_prefs";
 private static final String PREF_PREFIX_KEY = "prefix_";

 private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
 private EditText mAppWidgetPrefix;
 
  @Override
 public void onCreate(Bundle savedInstanceState)
 {
     Log.d(DEBUG_TAG,"Entering onCreate method in ConfigureTabActivity");
	 super.onCreate(savedInstanceState);
	 
	 Toast.makeText(this.getApplicationContext(), "Loading your settings", Toast.LENGTH_SHORT).show();
	 setContentView(R.layout.nametag_config);
	 Button saveButton=(Button)findViewById(R.id.save_button);
	 Bundle extras=getIntent().getExtras();
     Log.d(DEBUG_TAG,"nametag_ConfigureActivity reached this point. bundle desc: "+extras.toString());
     int appWidgetId=extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
     
     EditText ed=(EditText)findViewById(R.id.name_edit);
     TextView tv=(TextView)findViewById(R.id.preview_text);

     ImageView ivs[]={(ImageView) findViewById(R.id.pic0),
    		 (ImageView) findViewById(R.id.pic1),
    		 (ImageView) findViewById(R.id.pic2),
    		 (ImageView) findViewById(R.id.pic3),
    		 (ImageView) findViewById(R.id.pic4),
    		 (ImageView) findViewById(R.id.pic5),
    		 (ImageView) findViewById(R.id.pic6)
    		 };
     ed.setOnKeyListener(new previewListener2(ed,tv,ivs));
     
//	 Button previewButton=(Button)findViewById(R.id.preview_button);
//     previewButton.setOnClickListener(new previewListener(ed,tv));
     
//	 //CHANGE WAS MADE HERE IN GREEKLIFE   
//	 //SET SAVE BUTTON LISTENER
	 saveButton.setOnClickListener(new saveListener(this.getApplicationContext(),appWidgetId,ivs));

	 //INITIALIZE PREVIEW BUTTON
	 //1. INIT LAYOUT
	 //2. INIT TEXT
	  //THIS IS WHAT REALLY SHOULD BE SET: THE DRAWABLE. BUT YOU DON'T WANT TO SAVE THIS IN
	  //THE PREFERENCES FILE SO YOU SHOULD FIND A WAY TO DO IT USING A HASH TABLE
	  //THAT IS ACCESSIBLE TO ALL CLASSES IN CONFIGUREACTIVITY AND ALSO TO WIDGETSERVICEPROVIDER
//	  Button but = (Button) findViewById(R.id.preview_button);
//	  but.setText(text);	  	 
 	}


  private class previewListener2 implements OnKeyListener {
//	  private Context mAppCtx;
	  private EditText mEd;
	  private TextView mTv;
	  private ImageView[] mIvs;
	  
	  //CHANGE WAS MADE HERE IN greeklife
	  public previewListener2(EditText ed, TextView tv, ImageView[]ivs)
	  {
			this.mEd=ed;
			this.mTv=tv;
			this.mIvs=ivs;
	  }
	  @Override
		public boolean onKey(View v, int i, KeyEvent ke) {
			// TODO Auto-generated method stub
		  	String txt=mEd.getEditableText().toString().toLowerCase();
		  	
		  	if(txt.length()>7)
		  		{
		  		txt=txt.substring(0, 7);
		  		mEd.setText(txt);
		  		mEd.setSelection(6, 7);
		  		}
	  		nametag_DrawableMap dm=new nametag_DrawableMap();
		  	
		  	
//			  mTv.setText(txt);
//			  Map<Integer,Integer> m=new HashMap<Integer,Integer>();
			  for(int ix=0;ix<7;ix++)
			  {
				  String toget;
				  int toput;
				  try{
					  toget=txt.substring(ix,ix+1);
				  }
				  catch(IndexOutOfBoundsException iob){
					  toget="";
				  }
				  if(toget.matches("a")||
						  toget.matches("b")||
						  toget.matches("c")||
						  toget.matches("d")||
						  toget.matches("e")||
						  toget.matches("f")||
						  toget.matches("g")||
						  toget.matches("h")||
						  toget.matches("i")||
						  toget.matches("j")||
						  toget.matches("k")||
						  toget.matches("l")||
						  toget.matches("m")||
						  toget.matches("n")||
						  toget.matches("o")||
						  toget.matches("p")||
						  toget.matches("q")||
						  toget.matches("r")||
						  toget.matches("s")||
						  toget.matches("t")||
						  toget.matches("u")||
						  toget.matches("v")||
						  toget.matches("w")||
						  toget.matches("x")||
						  toget.matches("y")||
						  toget.matches("z"))
					  toput=dm.hm.get(toget);
				  else
					  toput=0;
//				  mTv.setText(Integer.toString(ix));
				  mIvs[ix].setBackgroundResource(toput);
				  mIvs[ix].setId(toput);
				  
//				  mIv1.setBackgroundResource(toput);				  
			  }
//				  mIv1.setBackgroundResource(R.drawable.greeklife_letter5);
//			  mIv2.setBackgroundResource(txt.length()>=2?dm.hm.get(txt.charAt(1)):R.drawable.greeklife_letter5);
//			  mIv3.setBackgroundResource(txt.length()>=3?dm.hm.get(txt.charAt(2)):R.drawable.greeklife_letter5);

  		return false;
		}
  }  
  
  private class saveListener implements OnClickListener {
	  private int appWidgetId;
	  private Context mAppCtx;
	  private AppWidgetManager mWMan;
	  private String mText;
	  private long mSkinId;
	  private EditText mNameTextEd;
	  private Gallery[] mGal;
	  private ImageView[] mIvs;
	  
	  //CHANGE WAS MADE HERE IN greeklife
	  public saveListener(Context appCtx,int appWidgetId,ImageView []ivs)
	  {
		this.appWidgetId=appWidgetId;
		this.mAppCtx=appCtx;
		this.mWMan=(AppWidgetManager)AppWidgetManager.getInstance(this.mAppCtx);
		this.mIvs=ivs;
//		this.mText=theText;
//		this.mSkinId=skinId;
//		this.mNameTextEd=nameTextEd;
	  }
	  public void onClick(View v)
	  {
		  Log.d(DEBUG_TAG,"onClick method just started!!!!");
		  
		  //CHANGE WAS MADE HERE IN greeklife
		  Log.d(DEBUG_TAG,"onClick method about to return: text is: "+this.mText);
//		  int[] pos={this.mGal[0].getSelectedItemPosition(),
//				  this.mGal[1].getSelectedItemPosition(),
//				  this.mGal[2].getSelectedItemPosition()};
		  int[] drawableResIds={
				  mIvs[0].getId(),
				  mIvs[1].getId(),
				  mIvs[2].getId(),
				  mIvs[3].getId(),
				  mIvs[4].getId(),
				  mIvs[5].getId(),
				  mIvs[6].getId()
		  };
		  nametag_AppWidgetProvider.initParams(this.appWidgetId, drawableResIds, this.mAppCtx);

		  finish();
	  }
  }  
}


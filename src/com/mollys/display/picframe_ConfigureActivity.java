package com.mollys.display;


import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class picframe_ConfigureActivity extends Activity {

	private static final double PERIOD_SECS=.5;
	private static final String DEBUG_TAG = "mydebug";

	private static final String LOCAL_PREFERENCES_FILE_NAME="com.mollys.display.picframe_prefs";
	private static final String PREF_PREFIX_KEY = "prefix_";

	public final static int SELECT_IMAGE = 1;

	private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	private EditText mAppWidgetPrefix;
	// private Uri mPicuri;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.picframe_config1);
		
		//     Intent intent2= new Intent("android.media.action.IMAGE_CAPTURE");
		//     Uri uri = Uri.fromFile(new File("/sdcard/TempPicture.jpg"));
		//     intent2.putExtra(MediaStore.EXTRA_OUTPUT,uri);
		//     startActivityForResult(intent2,1);

		Bundle extras=getIntent().getExtras();
		int wid_id=extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);

		Button but3=(Button) findViewById(R.id.test_button3);
		but3.setOnClickListener(new ConfigListener());//this.getApplicationContext(), wid_id, com.mollys.picframe.picframe_ConfigureActivity.class));
	}

	private class ConfigListener implements OnClickListener {
//		private Context mCtx;
//		private int mWid;
//		private Class mWconfig;
		public ConfigListener()//Context appCtx,int appWidgetId, Class wconfig)
		{
//			this.mCtx=appCtx;
//			this.mWid=appWidgetId;
//			this.mWconfig=wconfig;
		}
		public void onClick(View v)
		{
			int requestCode;
			startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), SELECT_IMAGE);
		}
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		System.gc();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		setContentView(R.layout.picframe_config2);


		if (requestCode == SELECT_IMAGE)
			if (resultCode == Activity.RESULT_OK) {
				Uri picuri = data.getData();
				//				SharedPreferences.Editor prefs=getApplicationContext().getSharedPreferences(LOCAL_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE).edit();
				//				prefs.putString("uri"+this.mAppWidgetId, picuri.toString());
				//				prefs.commit();


				//		    Button saveButton=(Button)findViewById(R.id.save_button);
				//		    saveButton.setText(Uri.parse(picuri.toString()).toString());
				// TODO Do something with the select image URI

				int w=100;
				int h=100;
				try{
					InputStream photoStream = this.getContentResolver().openInputStream(picuri);
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize=5;
					Bitmap photoBitmap = BitmapFactory.decodeStream(photoStream,null,options);
					h = photoBitmap.getHeight();
					w = photoBitmap.getWidth();
					int maxSize=200;
					if((w>h)&&(w>maxSize)){
						double ratio = new Double(maxSize)/w;
						w=maxSize;
						h=(int)(ratio*h);
					}
					else if((h>w)&&(h>maxSize)){
						double ratio = new Double(maxSize)/h;
						h=maxSize;
						w=(int)(ratio*w);
					}
					Bitmap scaled = Bitmap.createScaledBitmap(photoBitmap, w, h, true);
					photoBitmap.recycle();
					//	         LinearLayout ll=(LinearLayout) findViewById(R.id.previewLayout);
					//	         ll.setBackgroundDrawable(new BitmapDrawable(scaled));
					ImageView iv=(ImageView) findViewById(R.id.pic_image_config);
					iv.setImageDrawable(new BitmapDrawable(scaled));         
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		    

				Bundle extras=getIntent().getExtras();
				this.mAppWidgetId=extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
				//SET UP GALLERY AND LISTENER
				Gallery g = (Gallery) findViewById(R.id.gallery);
				//THE ADAPTER PROVIDES THE DATA WHICH BACKS THE SPINNER
				g.setAdapter(new ImageAdapter(this));
				g.setSpacing(10);
				//SET A CALLBACK FOR WHEN AN ITEM HAS BEEN SELECTED
				g.setOnItemSelectedListener(new galleryListener(this.getApplicationContext(),w,h));
				Button saveButton=(Button)findViewById(R.id.save_button);
				saveButton.setOnClickListener(new saveListener(this.getApplicationContext(),this.mAppWidgetId,g,picuri.toString()));
			} 
	}


	private class saveListener implements OnClickListener {
		private int mAppWidgetId;
		private Context mAppCtx;
		private AppWidgetManager mWMan;
		private String mText;
		private long mSkinId;
		private EditText mNameTextEd;
		private Uri mPicuri;
		private Gallery mGal;
		private String mSpicuri;

		//CHANGE WAS MADE HERE IN greeklife
		public saveListener(Context appCtx,int appWidgetId,Gallery g, String spicuri)
		{
			this.mAppWidgetId=appWidgetId;
			this.mAppCtx=appCtx;
			this.mWMan=(AppWidgetManager)AppWidgetManager.getInstance(this.mAppCtx);
			this.mGal=g;
			this.mSpicuri=spicuri;
		}
		public void onClick(View v)
		{
			//			SharedPreferences prefs=this.mAppCtx.getSharedPreferences(LOCAL_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
			//			String spicuri=prefs.getString("uri"+this.mAppWidgetId, "ERROR");
			Uri picuri=Uri.parse(mSpicuri);



			//		  picframe_AppWidgetProvider pme=new picframe_AppWidgetProvider();
			int pos=this.mGal.getSelectedItemPosition();
			ImageAdapter adapt=(ImageAdapter) this.mGal.getAdapter();
			picframe_AppWidgetProvider.initParams(this.mAppWidgetId, this.mAppCtx, picuri, adapt.getFrameResId(pos));
			finish();
		}
	}

	private class galleryListener implements OnItemSelectedListener{
		private Context mAppCtx;
		private int mW;
		private int mH;

		public galleryListener(Context appCtx, int w, int h)
		{
			mAppCtx=appCtx;
			mW=w;
			mH=h;
		}

		public void onItemSelected(AdapterView parent, View v,  int position, long id)
		{
			//		  Toast.makeText(mAppCtx, ""+v.getId() , Toast.LENGTH_SHORT).show();

			//		  Button but = (Button) findViewById(R.id.preview_image);
			//		  but.setBackgroundResource(v.getId());

			BitmapDrawable bd=(BitmapDrawable) v.getBackground();
			Bitmap bm=bd.getBitmap();
			Bitmap bmScaled=Bitmap.createScaledBitmap(bm, mW, mH, false);
			ImageView iv=(ImageView) findViewById(R.id.pic_frame_config);
			iv.setImageBitmap(bmScaled);
			TextView tv=(TextView) findViewById(R.id.credits);
			tv.setText(GalleryItems.mStringIds[position]);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub		
		}  
	}

	public class ImageAdapter extends BaseAdapter {
		int mGalleryItemBackground;
		private Context mContext;

		//	    public Integer[] mLayoutIds = {
		//	            R.layout.stickm_aw1,
		//	            R.layout.stickm_aw2,
		//	            R.layout.stickm_aw3,
		//	            R.layout.stickm_aw4,
		//	            R.layout.stickm_aw5,
		//	            R.layout.stickm_aw6,
		//	            R.layout.stickm_aw7
		//	            };

		private HashMap mDrawableMap;
		private HashMap mButtonMap;

		public ImageAdapter(Context c) {
			mContext = c;
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
			return GalleryItems.mDrawableIds.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		//FIX THIS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		public int getFrameResId(int position) {
			//	    	long resId=(Long)mButtonMap.get(new Integer(position));
			//	    	return resId;

			//NOT SUPPOSED TO BE LIKE THIS
			return GalleryItems.mDrawableIds[position];

			//return mButtonIds[position];
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(mContext);

			Resources res=mContext.getResources();
			Drawable dw=(Drawable)res.getDrawable(GalleryItems.mDrawableIds[position]/*(Integer)this.mDrawableMap.get(new Integer(position))*/);//b.getBackground();

			//			i.setImageDrawable(dw);
			i.setBackgroundDrawable(dw);
			i.setLayoutParams(new Gallery.LayoutParams(100, 100));
			i.setScaleType(ImageView.ScaleType.FIT_XY);
			i.setId(GalleryItems.mDrawableIds[position]/*(Integer)this.mDrawableMap.get(new Integer(position))*/);

			return i;
		}
	}
}


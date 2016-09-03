//TODO: YOU NEED TO MAKE THE APPLICATION STOP UPDATING ONCE A WIDGET HAS BEEN ELIMINATED.
//IN OTHER WORDS DELETE THE ALARM AND WHATEVER INSTANCES NEED TO BE DELETED.


package com.mollys.display;

//import com.mollys.;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {
	
	private static final int PERIOD_SECS=1;
	private static final String DEBUG_TAG="mydebug";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.d(DEBUG_TAG,"Entering main activity");
        //setContentView(R.layout.appwidget_config1);
        setContentView(R.layout.main_main);
    }
}
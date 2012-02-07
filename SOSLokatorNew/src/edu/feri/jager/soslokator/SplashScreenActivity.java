package edu.feri.jager.soslokator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

/**
 * @author Jager
 *
 */
public class SplashScreenActivity extends Activity {

	/**
	 */
	private final int SPLASH_TIME = new Integer(2000);

	/**
	 */
	private Thread splashTread;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.splash);

	    final SplashScreenActivity sPlashScreen = this; 

	    // thread for displaying the SplashScreen
	    splashTread = new Thread() {
	        @Override
	        public void run() {
	            try {
	            	synchronized(this) {

	            		//wait 5 sec
	            		wait(SPLASH_TIME);
	            	}

	            } catch (InterruptedException e) { } finally {
	                finish();

	                //start a new activity
	                Intent i = new Intent();
	                i.setClass(sPlashScreen, MainActivity.class);
	        		startActivity(i);

	                stop();
	            }
	        }
	    };

	    splashTread.start();
	}

	//Function that will handle the touch
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    if (event.getAction() == MotionEvent.ACTION_DOWN) {
	    	synchronized(splashTread) {
	    		splashTread.notifyAll();
	    	}
	    }
	    return true;
	}

}
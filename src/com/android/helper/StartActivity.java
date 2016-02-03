package com.android.helper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class StartActivity extends Activity {

	// Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Intent delayIntent = new Intent(StartActivity.this, HomeActivity.class);
				startActivity(delayIntent);
				finish();
				
			}
		}, SPLASH_TIME_OUT);
	}
}

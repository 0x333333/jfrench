package com.jesusjzp.android.jfrenchad;

import com.jesusjzp.android.jfrenchad.R;
import com.jesusjzp.db.DBManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.content.SharedPreferences;

public class SplashActivity extends Activity {
    /** Called when the activity is first created. */
	
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;
	LinearLayout ll;
	DBManager dbManager;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);
        
        ll = (LinearLayout)findViewById(R.id.splash);
        
        sharedPreferences = this.getSharedPreferences("test",MODE_WORLD_READABLE);  
        editor = sharedPreferences.edit();
        
        int isFirst = sharedPreferences.getInt("First", 1);
        
        dbManager = new DBManager(this);
        
        if(isFirst == 1) {
        	Log.v("For the first time", "true");
        	editor.putInt("First", 0);
        	editor.commit();
        	Handler x = new Handler();
            x.postDelayed(new StartThread(), 1000);
        } else {
        	Handler x = new Handler();
            x.postDelayed(new StartThread(), 2000);
        }
        
    };
    
    
    
    class StartThread implements Runnable{

		public void run() {
			// TODO Auto-generated method stub
    		dbManager.openDatabase();
    		dbManager.closeDatabase();
			startActivity(new Intent(getApplication(),JFrenchActivity.class));
			SplashActivity.this.finish();
		}
    	
    }
}

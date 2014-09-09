package com.jesusjzp.android.jfrenchad;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.jesusjzp.db.DBManager;
import com.jesusjzp.android.jfrenchad.R;
import com.jesusjzp.connet.GetData;

public class JFrenchActivity extends Activity {
	
	ListView list;
	ListAdapter list_adapter;
	DBManager dbManager;
	Cursor cur;
	EditText tv_word;
	Button btn_search_net;
	Button btn_search;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main2);
        
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
        	public void run() {
        		InputMethodManager imm = (InputMethodManager)JFrenchActivity.this.getSystemService(INPUT_METHOD_SERVICE); 
        		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); 
        	}
        }, 400);
        
        btn_search = (Button)findViewById(R.id.search);
        
        btn_search_net = (Button)findViewById(R.id.search_net);
        
        // auto complete
        tv_word = (EditText)findViewById(R.id.word_input);
        
        // initialize components
        list = (ListView)findViewById(R.id.search_list);
        list.setCacheColorHint(0);
        dbManager = new DBManager(this);
        
        // press the button and show list
        btn_search.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v) {
        		((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
        							.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 
        													InputMethodManager.HIDE_NOT_ALWAYS);
        		Intent intent = new Intent();
        		intent.setClass(JFrenchActivity.this, Histroy.class);
        		startActivity(intent);
        	}
        });
        
        // listen to edittext area
        tv_word.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String input = tv_word.getText().toString().trim();
				dbManager.openDatabase();
				cur = dbManager.fetchData(input);
				cur.moveToFirst();
				
				if(cur != null && cur.getCount() >= 0) {
                	list_adapter = new SimpleCursorAdapter(JFrenchActivity.this,
                			R.layout.labellistitem,
                			cur,
                			new String[] {"word"},
                			new int[] {R.id.LabelText});
                	list.setAdapter(list_adapter);
                }
        		dbManager.closeDatabase();
        		
        		btn_search_net.setVisibility(View.VISIBLE);
        		btn_search_net.setText("查询法语助手释义");
			}           
        }); 
        
        // press the button and show explanations from internet
        btn_search_net.setOnClickListener(new Button.OnClickListener() {
        	@SuppressLint("NewApi") public void onClick(View v) {
        		if(isNetwordAvailable() == false){
    	        	DisplayToast(getResources().getString(R.string.no_connect));
    	        }else{
    	        	
    	        	((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
    	        	.hideSoftInputFromWindow(getCurrentFocus()
    	        	.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    	        	
    	        	// show toast
    	        	DisplayToast(getResources().getString(R.string.connecting));
    	        	
    	        	// test data length
    	        	String word = tv_word.getText().toString().trim();
    	        	GetData getData = new GetData();
    	            String res = getData.getConnect(word);
    	            
    	            int start = res.indexOf("<!--CGHint-->");
    	            
    	            if(start == -1) {
    	            	DisplayToast(getResources().getString(R.string.no_result));
    	            } else {
	    	        	Intent intent = new Intent();
	        			Bundle bundle = new Bundle();
	        			bundle.putString("Word", tv_word.getText().toString().trim());
	        			bundle.putString("res", res);
	        			intent.setClass(JFrenchActivity.this, ShowNetExpActivity.class);
	        			intent.putExtras(bundle);
	        			startActivity(intent);
	        			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    	            }
    	        }
        	}
        });
        
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        		cur.moveToFirst();
        		for(int k = 0; k <  arg2; k ++) {
        			cur.moveToNext();
        		}
        		String id = cur.getString(cur.getColumnIndex(DBManager.KEY_ID));
        		String word = cur.getString(cur.getColumnIndex(DBManager.KEY_WORD));
        		String word1 = cur.getString(cur.getColumnIndex(DBManager.KEY_WORD1));
        		String v = cur.getString(cur.getColumnIndex(DBManager.KEY_V));
        		String text = cur.getString(cur.getColumnIndex(DBManager.KEY_MEANS));
        		
//        		Log.v("text chosed", text);    // for test
        		
        		Intent intent = new Intent();
        		Bundle bundle = new Bundle();
        		bundle.putString("Id", id);
        		bundle.putString("Word", word);
        		bundle.putString("Word1", word1);
        		bundle.putString("V", v);
        		bundle.putString("Text", text);
        		intent.setClass(JFrenchActivity.this, ShowExpActivity.class);
        		intent.putExtras(bundle);
        		startActivity(intent);
        	}
		});
        
        new Thread(new ViewThread()).start();
    }
    
    /*
     * menu
     */
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.mainmanu, menu);
    	return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	int item_id = item.getItemId();
    	switch(item_id) {
//    	case R.id.update:
//    		dbManager = new DBManager(this);
//    		dbManager.openDatabase();
//    		dbManager.closeDatabase();
//    		DisplayToast("Import database successfully!");
//    		break;
    	case R.id.histroy:
    		Intent intent = new Intent();
    		intent.setClass(JFrenchActivity.this, Histroy.class);
    		startActivity(intent);
    		break;
    	case R.id.about:
    		Dialog dialog = new AlertDialog.Builder(this)
    		.setIcon(R.drawable.ic_launcher_small)
			.setTitle("JFrench法语助手")
			.setMessage(R.string.info)
			.setPositiveButton(R.string.close, 
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					})
			.create();
    		// background blur
    		Window window = dialog.getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
			WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
    		dialog.show();
    		break;
    	case R.id.exit:
    		finish();
    		break;
    	}
    	return true;
    }
    
    public void DisplayToast(String str) {
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
    
    public boolean isNetwordAvailable() {
    	Context context = getApplicationContext();  
        ConnectivityManager connectivity = (ConnectivityManager) context  
                .getSystemService(Context.CONNECTIVITY_SERVICE);  
        if (connectivity == null) {  
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();  
            if (info != null) { 
                for (int i = 0; i < info.length; i++) {  
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {  
                        return true;  
                    }  
                }  
            }  
        }  
        return false;
    }
    
    class ViewThread implements Runnable{
		public void run() {
			// TODO Auto-generated method stub
			while(!Thread.currentThread().isInterrupted()){
				try{
					Thread.sleep(100);
				}
				catch(InterruptedException e){
					Thread.currentThread().interrupt();
				}
				//mDraw.postInvalidate();
			}
		}
    }
}
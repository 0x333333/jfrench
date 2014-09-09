package com.jesusjzp.android.jfrenchad;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jesusjzp.android.jfrenchad.R;
import com.jesusjzp.connet.GetData;

public class ShowNetExpActivity extends Activity {
	
	String word;
	String res;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.netexp);
        
        TextView tv_word = (TextView)findViewById(R.id.netexp_word);
        TextView tv_text = (TextView)findViewById(R.id.netexp_text);
        Button btn_return = (Button)findViewById(R.id.btn_return);
        
        // get word
        Bundle bundle = new Bundle();
        bundle = this.getIntent().getExtras();
        word = bundle.getString("Word");
        res = bundle.getString("res");
        
        // show word
        Spanned wordspan = Html.fromHtml("<font color='white'><b>" + word + "</b></font>"); 
        tv_word.setText(wordspan);
        
        Spanned resspan = Html.fromHtml(handleStr(res));
        tv_text.setText(resspan);
        
        // return
        btn_return.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v) {
        		finish();
        	}
        });
    }
    
    public void DisplayToast(String str) {
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
    
    public String handleStr(String res) {
    	int start = res.indexOf("<!--CGHint-->");
    	int end = res.indexOf("<!-- end #mainContent-->");
    	if(start != -1 && end != -1) {
    		res = res.substring(start, end);
    	}
    	end = res.indexOf("<div id=\"tab_");
    	if(end != -1)
    		res = res.substring(0, end);
    	return res;
    }
    
    public boolean isNetwordAvailable() {
    	Context context = getApplicationContext();  
        ConnectivityManager connectivity = (ConnectivityManager) context  
                .getSystemService(Context.CONNECTIVITY_SERVICE);  
        if (connectivity == null) {  
            return false;
        } else {//获取所有网络连接信息  
            NetworkInfo[] info = connectivity.getAllNetworkInfo();  
            if (info != null) {//逐一查找状态为已连接的网络  
                for (int i = 0; i < info.length; i++) {  
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {  
                        return true;  
                    }  
                }  
            }  
        }  
        return false;
    }
}

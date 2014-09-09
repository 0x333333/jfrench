package com.jesusjzp.connet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

public class GetData {
	
	public String getConnect(String word) {
		String res = "";
		String httpUrl = "http://www.frdic.com/dict/"+word;
		Log.v("httpUrl:", httpUrl);
		URL url = null;
		
		try {
			url = new URL(httpUrl);
		} catch (MalformedURLException e) {
			Log.e("GetData.java", "MalformedURLException");
		}
		
		if(url != null) {
			try {
				HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
				urlConn.connect();
				BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
				String inputLine = null;
				while (((inputLine = reader.readLine()) != null)) {
					res += inputLine + "\n";
				}
				reader.close();
				urlConn.disconnect();
			} catch (IOException e)	{
				Log.e("GetData.java", "IOException");
			}
		}
		
		return res;
	}

}

package com.jesusjzp.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.jesusjzp.android.jfrenchad.R;
import com.jesusjzp.android.jfrenchad.R.raw;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;

public class DBManager {
	
	private final int BUFFER_SIZE = 400000;
	public static final String KEY_ID = "_id";
	public static final String KEY_WORD1 = "word1";
	public static final String KEY_WORD = "word";
	public static final String KEY_V = "v";
	public static final String KEY_MEANS = "means";
	public static final String DB_NAME = "fd.db"; 
	private static final String DB_TABLE = "FRDic";
	private static final String CON_TABLE = "conjugaison";
	private static final String HIS_TABLE = "histroy";
    public static final String PACKAGE_NAME = "com.jesusjzp.android.jfrenchad";
    public static final String DB_PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/"
            + PACKAGE_NAME;  
    
    private SQLiteDatabase database;
    private Context context;
    
    public DBManager(Context context) {
    	this.context = context;
    }
    
    public void openDatabase() {
    	this.database = this.openDatabase(DB_PATH + "/" + DB_NAME);
    }
    
    private SQLiteDatabase openDatabase(String dbfile) {
    	try {
    		if (!(new File(dbfile).exists())) {
    			// import
    			InputStream is = this.context.getResources().openRawResource(R.raw.fd);
    			FileOutputStream fos = new FileOutputStream(dbfile);
    			byte[] buffer = new byte[BUFFER_SIZE];
    			int count = 0;
    			while ((count = is.read(buffer)) > 0) {
    				fos.write(buffer, 0, count);
    			}
    			fos.close();
    			is.close();
    		}
	    	SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
	    	Log.v("open database", "successfully!");
	    	return db;
	    } catch (FileNotFoundException e) {
	        Log.e("Database", "File not found");
	        e.printStackTrace();
	    } catch (IOException e) {
	        Log.e("Database", "IO exception");
	        e.printStackTrace();
	    }
	    return null;
    }
    
    public void closeDatabase() {
    	this.database.close();
    }
    
    public Cursor fetchAllData() {
    	return this.database.query(DB_TABLE, null, null, null, null, null, null);
    }
    
    public Cursor fetchWordList() throws SQLException {
    	String[] columns = new String[] {KEY_WORD1};
    	Cursor mCursor = this.database.query(DB_TABLE, columns, null, null, null, null, null);
    	if(mCursor != null) {
			mCursor.moveToFirst();
			Log.v("fetchData", "mCursor is not null"+mCursor.getCount());
		}
    	return mCursor;
    }
    
    public Cursor fetchData(String word) throws SQLException {
		Cursor mCursor = this.database.query(DB_TABLE, null, 
				KEY_WORD1 + " like '" + word + "%' limit 20", null, null, null, null);
		if(mCursor != null) {
			mCursor.moveToFirst();
			Log.v("fetchData", "mCursor is not null"+mCursor.getCount());
		}
		return mCursor;
	}
    
    public Cursor fetchConData(String id, String type, String stype) throws SQLException {
    	Cursor mCursor = this.database.query(CON_TABLE, null,
    			"id_word = " + id + " and id_type = " + type + " and id_stype = " + stype , 
    			null, null, null, null);
    	if(mCursor != null) {
    		mCursor.moveToFirst();
    		Log.v("fetchConData", "mCursor is not null");
    	}
    	return mCursor;
    }
    
    public int fetchHis(String id) throws SQLException {
    	Cursor mCursor = this.database.query("histroy", null,
    			"id = " + id, 
    			null, null, null, null);
    	if(mCursor != null) {
    		mCursor.moveToFirst();
    		Log.v("fetchConData", "mCursor is not null");
    		return Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("count")));
    	}
    	return 0;
    }
    
    public Cursor fetchHisDataTime() throws SQLException {
    	Cursor mCursor = this.database.query("histroy", null, null, null, null, null, "_id desc");
    	if(mCursor != null) {
			mCursor.moveToFirst();
			Log.v("fetchData", "mCursor is not null"+mCursor.getCount());
		}
    	return mCursor;
    }
    
    public Cursor fetchHisDataFre() throws SQLException {
    	Cursor mCursor = this.database.query("histroy", null, null, null, null, null, "count desc");
    	if(mCursor != null) {
			mCursor.moveToFirst();
			Log.v("fetchData", "mCursor is not null"+mCursor.getCount());
		}
    	return mCursor;
    }
    
    public void insert(int index, String id, String word1, String word, String v, String text) throws SQLException {
    	Cursor mCursor = this.database.query(HIS_TABLE, null, 
				"id" + " = " + id, null, null, null, null);
    	int count;
    	String sql = "";
    	mCursor.moveToFirst();
    	if(mCursor.getCount() > 0) {
    		String tmp = mCursor.getString(mCursor.getColumnIndex("count"));
    		Log.v("This word has been searched for ", tmp);
    		count = Integer.parseInt(tmp);
    		count ++;
    		sql = "update histroy set count = " + count + ", _id = " + index + ", time = \"" + getTime() + "\" where id = " + id;
    	} else {
    		Log.v("This word has been searched for ", "0");
    		text = text.replaceAll("\"", "'");
    		sql = "INSERT INTO histroy(_id, id, word1, word, v, means, count, time) VALUES" +
        			"("+ index + ", "
        			+ id + ", \""
        			+ word1 + "\", \""
        			+ word + "\", \""
        			+ v + "\", \""
        			+ text + "\", "
        			+ "1" + ", \"" 
        			+ getTime() + "\")";
    	}
    	
    	Log.v("sql:", sql);
    	this.database.execSQL(sql);
    }
    
    public void delHis() throws SQLException {
    	this.database.execSQL("delete from histroy where _id > -1");
    }
    
    public String getTime() {
		Time t = new Time();
		t.setToNow();
		int year = t.year;
		int month = t.month;
		int day = t.monthDay;
		int hour = t.hour;
		int minute = t.minute;
		int second = t.second;
		String time = hour+":"+minute+":"+second+" " + year + "-" + month + "-" + day;
		return time;
	}
    
}





package com.jesusjzp.android.jfrenchad;

import com.jesusjzp.db.DBManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TabHost;

public class Histroy extends Activity {

	Button btn_back;
	Button btn_del;
	Button btn_time;
	Button btn_frequency;
	RelativeLayout rl;
	ListView list;
	ListAdapter list_adapter;
	DBManager dbManager;
	Cursor cur;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.histroy);

		btn_back = (Button) findViewById(R.id.btn_return);
		btn_del = (Button) findViewById(R.id.btn_del);
		btn_time = (Button) findViewById(R.id.button1);
		btn_frequency = (Button) findViewById(R.id.button2);
		rl = (RelativeLayout) findViewById(R.id.rl1);
		list = (ListView) this.findViewById(R.id.list1);
		list.setCacheColorHint(0);

		dbManager = new DBManager(this);
		dbManager.openDatabase();
		cur = dbManager.fetchHisDataTime();
		cur.moveToFirst();
		dbManager.closeDatabase();

		if (cur != null && cur.getCount() >= 0) {
			list_adapter = new SimpleCursorAdapter(Histroy.this,
					R.layout.labellistitem2, cur,
					new String[] { "word", "time" }, new int[] {
							R.id.LabelText, R.id.LabelText2 });
			list.setAdapter(list_adapter);
		}

		btn_back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		btn_del.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				AlertDialog dialog = new AlertDialog.Builder(Histroy.this)
						.setTitle(getResources().getString(R.string.del_his))
						.setMessage(getResources().getString(R.string.que_his))
						.setPositiveButton("确认",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dbManager.openDatabase();
										dbManager.delHis();
										dbManager.closeDatabase();
										list_adapter = new SimpleCursorAdapter(
												Histroy.this,
												R.layout.labellistitem2, null,
												new String[] { "word" },
												new int[] { R.id.LabelText });
										list.setAdapter(list_adapter);
										dialog.dismiss();
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								}).create();
				dialog.show();
			}
		});

		btn_time.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				rl.setBackgroundResource(R.drawable.multibtn1);
				dbManager.openDatabase();
				cur = dbManager.fetchHisDataTime();
				cur.moveToFirst();
				dbManager.closeDatabase();
				if (cur != null && cur.getCount() >= 0) {
					list_adapter = new SimpleCursorAdapter(Histroy.this,
							R.layout.labellistitem2, cur, new String[] {
									"word", "time" }, new int[] {
									R.id.LabelText, R.id.LabelText2 });
					list.setAdapter(list_adapter);
				}
			}
		});

		btn_frequency.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				rl.setBackgroundResource(R.drawable.multibtn2);
				dbManager.openDatabase();
				cur = dbManager.fetchHisDataFre();
				cur.moveToFirst();
				dbManager.closeDatabase();
				if (cur != null && cur.getCount() >= 0) {
					list_adapter = new SimpleCursorAdapter(Histroy.this,
							R.layout.labellistitem2, cur, new String[] {
									"word", "count" }, new int[] {
									R.id.LabelText, R.id.LabelText2 });
					list.setAdapter(list_adapter);
				}
			}
		});

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				cur.moveToFirst();
				for (int k = 0; k < arg2; k++) {
					cur.moveToNext();
				}
				String id = cur.getString(cur.getColumnIndex("id"));
				String word = cur.getString(cur
						.getColumnIndex(DBManager.KEY_WORD));
				String word1 = cur.getString(cur
						.getColumnIndex(DBManager.KEY_WORD1));
				String v = cur.getString(cur.getColumnIndex(DBManager.KEY_V));
				String text = cur.getString(cur
						.getColumnIndex(DBManager.KEY_MEANS));

				// Log.v("text chosed", text); // for test

				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("Id", id);
				bundle.putString("Word", word);
				bundle.putString("Word1", word1);
				bundle.putString("V", v);
				bundle.putString("Text", text);
				intent.setClass(Histroy.this, ShowExpActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

	}
}

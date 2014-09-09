package com.jesusjzp.android.jfrenchad;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jesusjzp.android.jfrenchad.R;
import com.jesusjzp.connet.GetData;
import com.jesusjzp.db.DBManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;

public class ShowExpActivity extends Activity {

	int count;
	String id;
	String word;
	String word1;
	String ver;
	String text;
	DBManager dbManager;
	public SharedPreferences preferences;
	public SharedPreferences.Editor editor;
	String[] choices = { "直陈式 现在时 (Indicatif Présent)",
			"直陈式 过去时 (Indicatif Passé Composé)",
			"直陈式 未完成过去时 (Indicatif Imparfait)",
			"直陈式 愈过去时 (Indicatif Plus-que-parfait)",
			"直陈式 简单过去时 (Indicatif Passé Simple)",
			"直陈式 先过去时 (Indicatif Passé Antérieur)",
			"直陈式 简单将来时 (Indicatif Future Simple)",
			"先将来时 (Indicatif Future Antérieur)", "虚拟 现在时 (Subjonctif Présent)",
			"虚拟 过去时 (Subjonctif Passé)", "虚拟 未完成过去时 (Subjonctif Imparfait)",
			"虚拟 愈过去时 (Subjonctif Plus-que-parfait)",
			"条件式 现在时 (Conditionnel Présent)", "条件式 过去时 (Conditionnel Passé)",
			"命令式 现在时 (Impératif Présent)", "命令式 过去时 (Impératif Passé)",
			"现在分词 (Participe Présent)", "过去分词 (Participe Passé)" };

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.exp);
		TextView tv_word = (TextView) findViewById(R.id.exp_word);
		TextView tv_text = (TextView) findViewById(R.id.exp_text);
		TextView tv_count = (TextView) findViewById(R.id.count);
		TextView tv_example_tilte = (TextView) findViewById(R.id.textView2);
		TextView tv_line2 = (TextView) findViewById(R.id.line2);
		TextView tv_example = (TextView) findViewById(R.id.exp_text2);
		Button btn_return = (Button) findViewById(R.id.btn_return);
		Button btn_conjug = (Button) findViewById(R.id.btn_conjug);
		Button btn_net = (Button) findViewById(R.id.btn_internet);

		// get string
		Bundle bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		id = bundle.getString("Id");
		word = bundle.getString("Word");
		word1 = bundle.getString("Word1");
		ver = bundle.getString("V");
		text = bundle.getString("Text");

		// insert into database and check count from table histroy
		dbManager = new DBManager(this);
		dbManager.openDatabase();
		preferences = getSharedPreferences("pocketwiki", MODE_PRIVATE);
		editor = preferences.edit();
		int index = preferences.getInt("index", 0);
		dbManager.insert(index, id, word1, word, ver, text);
		count = dbManager.fetchHis(id);
		dbManager.closeDatabase();
		index++;
		editor.putInt("index", index);
		editor.commit();

		// set button visibility
		if (!ver.equals("1")) {
			btn_conjug.setVisibility(View.INVISIBLE);
		}

		// format string
		String word2 = "<font color='white'><b>" + word + "</b></font>";
		Spanned wordspan = Html.fromHtml(word2);

		// get word type
		String regex = "([a-z]+\\.; [a-z])|([a-z]+\\.)";
		Pattern pa = Pattern.compile(regex);
		Matcher ma = pa.matcher(text);
		while (ma.find()) {
			String temp = ma.group();
			temp.replace("; ", " ");
			text = text.replace(temp, "<font color='blue'><b><i><br>" + temp
					+ "</i></b></font>");
			// Log.v("get word type", temp);
		}

		// get example
		String regex_ex = "[a-z' ]*~[a-z' ]*";
		Pattern pa_ex = Pattern.compile(regex_ex);
		Matcher ma_ex = pa_ex.matcher(text);
		String text_pre = text;
		String text_res = "";
		if (ma_ex.find()) {
			String temp = ma_ex.group();
			// Log.v("first temp:", temp);
			text_pre = text.substring(0, text.indexOf(temp));
			text_res = text.substring(text.indexOf(temp));
			// Log.v("text res:", text_res);
			tv_example_tilte.setVisibility(View.VISIBLE);
			tv_line2.setVisibility(View.VISIBLE);
			text_res = text_res.replace(temp, "<b>" + temp + "</b>");

			// high light
			Matcher ma_ex_res = pa_ex.matcher(text_res);
			while (ma_ex_res.find()) {
				String temp_res = ma_ex_res.group();
				text_res = text_res
						.replace(temp_res, "<b>" + temp_res + "</b>");
			}
			text_res = text_res.replaceAll(";", ";<br>");
			Spanned textspan_res = Html.fromHtml(text_res);
			tv_example.setText(textspan_res);
		}

		text_pre = text_pre.replaceAll(";", ";<br>");

		Spanned textspan_pre = Html.fromHtml(text_pre);

		// show string
		tv_word.setText(wordspan);
		tv_text.setText(textspan_pre);
		if (count - 1 != 0) {
			tv_count.setVisibility(View.VISIBLE);
			tv_count.setText(count);
		}

		// return
		btn_return.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		// If the word is a verbal
		// show the conjugaison
		btn_conjug.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// Log.v("ver:", ver);
				if (ver.equals("1")) {
					AlertDialog dialog = new AlertDialog.Builder(
							ShowExpActivity.this)
							.setTitle(
									getResources().getString(
											R.string.chooseconj))
							.setItems(choices, conSelect).create();
					dialog.show();
				}
			}
		});

		// search explanation from internet
		btn_net.setOnClickListener(new Button.OnClickListener() {
			@SuppressLint("NewApi")
			public void onClick(View v) {
				if (isNetwordAvailable() == false) {
					DisplayToast(getResources().getString(R.string.no_connect));
				} else {
					// show toast
					DisplayToast(getResources().getString(R.string.connecting));

					GetData getData = new GetData();
					String res = getData.getConnect(word1);
					// Log.v("res", res);

					int start = res.indexOf("<!--CGHint-->");

					if (start == -1) {
						// Log.v("start", start+"");
						// Log.v("word", word);

						DisplayToast(getResources().getString(
								R.string.no_result));
					} else {
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putString("Word", word);
						bundle.putString("res", res);
						intent.setClass(ShowExpActivity.this,
								ShowNetExpActivity.class);
						intent.putExtras(bundle);
						startActivity(intent);
						overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);
					}
				}
			}
		});
	}

	OnClickListener conSelect = new OnClickListener() {
		@SuppressLint("NewApi")
		public void onClick(DialogInterface dialog, int which) {

			Intent intent = new Intent();
			Bundle bundle = new Bundle();

			bundle.putString("Id", id);
			bundle.putString("Word", word);
			bundle.putString("V", ver);
			bundle.putString("Text", text);
			bundle.putString("Which", which + "");

			switch (which) {
			case 0:
				bundle.putString("Type", "1");
				bundle.putString("Stype", "1");
				bundle.putString("ChoiceName", choices[which]);
				break;
			case 1:
				bundle.putString("Type", "1");
				bundle.putString("Stype", "3");
				bundle.putString("ChoiceName", choices[which]);
				break;
			case 2:
				bundle.putString("Type", "1");
				bundle.putString("Stype", "4");
				bundle.putString("ChoiceName", choices[which]);
				break;
			case 3:
				bundle.putString("Type", "1");
				bundle.putString("Stype", "5");
				bundle.putString("ChoiceName", choices[which]);
				break;
			case 4:
				bundle.putString("Type", "1");
				bundle.putString("Stype", "6");
				bundle.putString("ChoiceName", choices[which]);
				break;
			case 5:
				bundle.putString("Type", "1");
				bundle.putString("Stype", "7");
				bundle.putString("ChoiceName", choices[which]);
				break;
			case 6:
				bundle.putString("Type", "1");
				bundle.putString("Stype", "8");
				bundle.putString("ChoiceName", choices[which]);
				break;
			case 7:
				bundle.putString("Type", "1");
				bundle.putString("Stype", "9");
				bundle.putString("ChoiceName", choices[which]);
				break;
			case 8:
				bundle.putString("Type", "2");
				bundle.putString("Stype", "1");
				bundle.putString("ChoiceName", choices[which]);
				break;
			case 9:
				bundle.putString("Type", "2");
				bundle.putString("Stype", "2");
				bundle.putString("ChoiceName", choices[which]);
				break;
			case 10:
				bundle.putString("Type", "2");
				bundle.putString("Stype", "4");
				bundle.putString("ChoiceName", choices[which]);
				break;
			case 11:
				bundle.putString("Type", "2");
				bundle.putString("Stype", "5");
				bundle.putString("ChoiceName", choices[which]);
				break;
			case 12:
				bundle.putString("Type", "3");
				bundle.putString("Stype", "1");
				bundle.putString("ChoiceName", choices[which]);
				break;
			case 13:
				bundle.putString("Type", "3");
				bundle.putString("Stype", "2");
				bundle.putString("ChoiceName", choices[which]);
				break;
			case 14:
				bundle.putString("Type", "4");
				bundle.putString("Stype", "1");
				bundle.putString("ChoiceName", choices[which]);
				break;
			case 15:
				bundle.putString("Type", "4");
				bundle.putString("Stype", "2");
				bundle.putString("ChoiceName", choices[which]);
				break;
			case 16:
				bundle.putString("Type", "5");
				bundle.putString("Stype", "1");
				bundle.putString("ChoiceName", choices[which]);
				break;
			case 17:
				bundle.putString("Type", "5");
				bundle.putString("Stype", "2");
				bundle.putString("ChoiceName", choices[which]);
				break;
			}

			intent.setClass(ShowExpActivity.this, ShowConjActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		}

	};

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
}
package com.jesusjzp.android.jfrenchad;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jesusjzp.android.jfrenchad.R;
import com.jesusjzp.db.DBManager;

public class ShowConjActivity extends Activity {

	int which;

	String id;
	String word;
	String ver;
	String text;
	String type;
	String stype;
	String choiceName;
	String conjugaison;

	DBManager dbManager;
	Cursor cur;

	String[] conExps = {
			"<p><strong>现在时 Indicatif Présent</strong></p>"
					+ "<p><strong>变位方法：</strong></p>"
					+ "<p>第一组动词(通常为-er)：参见 <a>aimer</a> <br> 注意一些第一组动词由于发音需要，在变位时会有所变化：</p><ul>"
					+ "<br>以-eler, -eter结尾的第一组动词，如<a href=\"/SearchDic.aspx?noHis=1&amp;showCg=1&amp;word=appeler\">appeler</a>,在单数所有人称，以及复数第三人称的变位中，词尾字母变为\"ll\",\"tt\"。 "
					+ "<br>以-cer, -ger结尾的第一组动词，如<a href=\"/SearchDic.aspx?noHis=1&amp;showCg=1&amp;word=commencer\">commencer</a>, <a href=\"/SearchDic.aspx?noHis=1&amp;showCg=1&amp;word=manger\">manger</a>, 在复数第一人称时词尾应改为 -çons, -geons 。  "
					+ "<br>以-ayer,-oyer,-uyer结尾的第一组动词，如<a href=\"/SearchDic.aspx?noHis=1&amp;showCg=1&amp;word=essayer\">essayer</a>, <a href=\"/SearchDic.aspx?noHis=1&amp;showCg=1&amp;word=envoyer\">envoyer</a>, <a href=\"/SearchDic.aspx?noHis=1&amp;showCg=1&amp;word=ennuyer\">ennuyer</a>, 在单数所有人称，以及复数第三人称的变位中，词尾字母由y变为i。 </ul>"
					+ "<p>第二组动词 (通常为 -ir)：参见 <a href=\"/SearchDic.aspx?noHis=1&amp;showCg=1&amp;word=finir/\">finir</a></p><ul> <br>例外：<a href=\"/SearchDic.aspx?noHis=1&amp;showCg=1&amp;word=sortir\">sortir</a>, <a href=\"/SearchDic.aspx?noHis=1&amp;showCg=1&amp;word=courir\">courir</a>, "
					+ "<a href=\"/SearchDic.aspx?noHis=1&amp;showCg=1&amp;word=ouvrir\">ouvrir</a>, <a href=\"/SearchDic.aspx?noHis=1&amp;showCg=1&amp;word=servir\">servir</a>, <a href=\"/SearchDic.aspx?noHis=1&amp;showCg=1&amp;word=partir\">partir</a>, <a href=\"/SearchDic.aspx?noHis=1&amp;showCg=1&amp;word=dormir\">dormir</a>　等 </ul><p>第三组动词：不规则变化。</p>",

			"<p><strong>直陈式复合过去时(Indicatif Passé Composé)</strong>用于表示过去已经完成的动作或某一已经完成的动作是发生在多少时间内的。<br> Ex：<br> Il est parti.<br> Il a dormi toute l'après-midi.</p>"
					+ "<p><strong>变位方法：</strong><br> 由助动词avoir或être的直陈式现在时加上动词的过去分词。<br> 当使用 être作为助动词时，注意与主语进行性数配合：Elles sont parties. </p>"
					+ "<p>下列常用动词可以使用avoir或être作为助动词，但表达的意义不同。<br> sortir, rentrer, descendre, monter</p>",

			"<p><strong>未完成过去时 (Indicatif Imparfait)</strong> 主要用于表示过去未完成的动作。</p>"
					+ "<p><strong>变位方法：</strong>将该动词现在时第一人称复数的词尾 -ons 去除, 添加相应的词尾：</p>"
					+ "<p><strong>je -ais<br> tu -ais<br>il/elle -ait<br>nous -ions<br>vous -iez<br>ils/elles -aient</strong></p>"
					+ "<p>例外：<a href=\"/SearchDic.aspx?noHis=1&amp;showCg=1&amp;word=être\">être</a>不符合上面的规则。</p>",

			"<p><strong>直陈式俞过去时(Indicatif Plus-que-parfait)</strong>表示某个动作在另一个过去的动作开始之前已经完成，常用于复合句：<br>Quand je suis arrivé, il avait fini son travail. <br> 与简单将来时配合用于si引导的条件从句：<br>Si j'avais eu de l'argent, j'aurais acheté cette maison. </p>"
					+ "<p><strong>变位方法：</strong><br>由助动词avoir或être的未完成过去时加上动词的过去分词。<br>当使用 être作为助动词时，注意与主语进行性数配合。</p>"
					+ "<p>下列常用动词可以使用avoir或être作为助动词，但表达的意义不同。<br> sortir, rentrer, descendre, monter</p>",

			"<p><strong>直陈式简单过去时(Indicatif Passé Simple)</strong>：表示在过去某一确定时间内已经完成的动作。<br> <strong>变位方法：</strong><br>除去某些特殊情况外，在动词词根后加上以下词尾：</p>"
					+ "<ul> <br> 第一组动词： </ul><blockquote> <p>je -ai<br> tu -as<br> il/elle -a<br> nous -âmes<br> vous -âtes<br> ils/elles -èrent</p></blockquote>"
					+ "<ul> <br>第二组动词: </ul><blockquote> <p>je -is<br>tu -is<br>il/elle -it<br>nous -îmes<br>vous -îtes<br>ils/elles -irent</p></blockquote>"
					+ "<ul> <br>第二组动词: </ul><blockquote> <p>je -us<br> tu -us<br> il/elle -ut<br> nous -ûmes<br> vous -ûtes<br> ils/elles -urent</p></blockquote>"
					+ "<p>下列单词的变位较特殊：<br> tenir, venir, être, avoir </p>",

			"<p><strong>直陈式先过去时(Indicatif Passé Antérieur) </strong>表示发生在另一过去动作之前的动作，但两个动作之间距离较近。</p>"
					+ "<p><strong>变位方法：</strong><br>由助动词avoir或être的简单过去时加上动词的过去分词。<br>当使用 être作为助动词时，注意与主语进行性数配合。</p>"
					+ "<p>下列常用动词可以使用avoir或être作为助动词，但表达的意义不同。<br> sortir, rentrer, descendre, monter</p>",

			"<p><strong>直陈式简单将来时(Indicatif Future Simple)</strong>表示将要发生的行为或状态<br> <strong>变位方法： <br> </strong>第一、二组动词：在动词不定式后加上下列词尾：</p>"
					+ "<blockquote> <p>je -ai<br> tu -as<br> il/elle -a<br> nous -ons<br> vous -ez<br> ils/elles -ont</p></blockquote>"
					+ "<p>以-re结尾的第三组动词需要首先去掉词尾的-e再加上述词尾。</p>"
					+ "<p>注意下列单词的特殊变化：</p>"
					+ "<p>avoir, être, aller, faire, courir, voir, pouvoir ... </p>",

			"<p><strong>直陈式先将来时(Indicatif Future Antérieur)</strong>表示比另一个将来的动作先一步完成了的动作。</p>"
					+ "<p><strong>变位方法：</strong><br>由助动词avoir或être的简单将来时加上动词的过去分词。<br>当使用 être作为助动词时，注意与主语进行性数配合。</p>"
					+ "<p>下列常用动词可以使用avoir或être作为助动词，但表达的意义不同。<br> sortir, rentrer, descendre, monter</p>",

			"<p><strong>虚拟式现在时</strong>Subjonctif Présent </p>"
					+ "<p><strong>变位方法：</strong>对于第一组和第二组动词，除去直陈式现在时复数第三人称的词尾-ent，加上下面的词尾：</p>"
					+ "<p><strong>je -e<br> tu -es<br> il/elle -e<br> nous -ions<br>vous -iez<br>ils/elle -ent </strong></p>",

			"<p><strong>虚拟式过去时</strong>Subjonctif Passé </p>"
					+ "<p><strong>变位方法：</strong>由助动词avoir或être的虚拟式加上动词的过去分词构成。<br>当使用 être作为助动词时，注意与主语进行性数配合。</p>"
					+ "<p>下列常用动词可以使用avoir或être作为助动词，但表达的意义不同。<br> sortir, rentrer, descendre, monter</p>",

			"<p><strong>虚拟式未完成过去时Subjonctif Imparfait </strong></p>"
					+ "<p><strong>变位方法：</strong>由直陈式简单过去时第二人称单数去掉词尾( \"i\"或者 \"s\")，加下列词尾构成<br>je -sse<br>tu -sses<br>il/elle -ît<br>nous -ssions<br>vous -ssiez<br> ils/elles -ssent</p>"
					+ "<p>提示：第二组动词的虚拟式现在时和虚拟时未完成过去时出第三人称单数外，其余相同。<br></p>",

			"<p><strong>虚拟式俞过去时Subjonctif Plus-que-parfait</strong></p>"
					+ "<p><strong>变位方法：</strong>由助动词avoir或être的虚拟式未完成过去时加上动词的过去分词构成。<br>当使用 être作为助动词时，注意与主语进行性数配合。</p>"
					+ "<p>下列常用动词可以使用avoir或être作为助动词，但表达的意义不同。<br> sortir, rentrer, descendre, monter</p>",

			"<p><strong>条件式现在时(Conditionnel Présent) </strong>用于独立句中表示语气婉转的愿望、请求等，用于复合句中表示某一假设条件下可能发生的情况，例如：Si j'étais vous, je ferais autrement.<br> <br> <strong>变位方法：</strong><br>由简单将来时的词根加下列词尾构成：</p>"
					+ "<blockquote> <p><strong>je -ais<br> tu -ais<br> il/elle -ait<br> nous -ions<br> vous -iez<br> ils/elles -aient </strong><br> </p></blockquote>",

			"<p><strong>条件式过去时(Conditionnel Passé) </strong><br> <strong>变位方法：</strong>由助动词avoir或être的条件式现在时加上动词的过去分词构成。<br>当使用 être作为助动词时，注意与主语进行性数配合。</p>"
					+ "<p>下列常用动词可以使用avoir或être作为助动词，但表达的意义不同。<br> sortir, rentrer, descendre, monter</p>",

			"<p><strong>命令式现在时(Impératif Présent) </strong>表达命令、禁止等主观态度。</p>"
					+ "<p><strong>变位方法：</strong>命令式只有第一人称复数，第二人称单、复数三种变位形式，变位形式通常于直陈式现在时相同。</p>"
					+ "<p>注意：对于第一组动词以及-ir为词尾的动词，第二人称单数的命令式需要去掉词尾的-s：例如parler - tu parles - parle!</p>",

			"<p><strong>命令式过去时(Impératif Passé) </strong></p>"
					+ "<p><strong>变位方法：</strong>命令式过去时只有第一人称复数，第二人称单、复数三种变位形式,由助动词avoir或être的命令式现在时加上动词的过去分词构成。<br>当使用 être作为助动词时，注意与主语进行性数配合。－</p>"
					+ "<p>下列常用动词可以使用avoir或être作为助动词，但表达的意义不同。<br> sortir, rentrer, descendre, monter</p>",

			"",

			"" };

	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.conj);

		TextView tv_title = (TextView) findViewById(R.id.conj_title);
		TextView tv_con = (TextView) findViewById(R.id.conj_text);
		TextView tv_word = (TextView) findViewById(R.id.conj_word);
		TextView tv_con_exp = (TextView) findViewById(R.id.conj_explain);
		Button btn_return = (Button) findViewById(R.id.btn_con_return);

		// get string
		Bundle bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		id = bundle.getString("Id");
		word = bundle.getString("Word");
		ver = bundle.getString("V");
		text = bundle.getString("Text");
		type = bundle.getString("Type");
		stype = bundle.getString("Stype");
		choiceName = bundle.getString("ChoiceName");
		which = Integer.parseInt(bundle.getString("Which"));

		// search database
		dbManager = new DBManager(this);
		dbManager.openDatabase();
		cur = dbManager.fetchConData(id, type, stype);
		dbManager.closeDatabase();
		String title = "<i>" + "变位方法  " + choiceName + "</i>";
		conjugaison = cur.getString(cur.getColumnIndex("word"));

		// show string
		Spanned wordspan = Html.fromHtml(word);
		tv_word.setText(wordspan);
		Spanned conspan = Html.fromHtml(title);
		tv_title.setText(conspan);
		tv_con.setText(conjugaison);
		if (which <= 17 && which >= 0) {
			Spanned expspan = Html.fromHtml(conExps[which]);
			tv_con_exp.setText(expspan);
		}

		// return
		btn_return.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

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
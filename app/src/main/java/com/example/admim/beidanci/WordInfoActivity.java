package com.example.admim.beidanci;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Bg.Translate.BaiduTranslateResult;
import Bg.Translate.SetTranslateResult;
import Bg.Translate.Translate;
import Bg.Volley.VolleyRequest;
import Model.WordInfoItem;
import Model.WordInstance;
import Model.WordItem;
import View.WordInstanceAdapter;
import View.WordSimilarityAdapter;
import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class WordInfoActivity extends Activity
{
	//例句
	private ListView mLvWordInstances;
	//返回
	private ImageView mIvReturn;
	//组名
	private TextView mTvGroupName;
	//单词内容
	private TextView mTvWordContent;
	//单词 结果
	private TextView mTvWordResult;
	//语音按钮
	private ImageView mIvVoice;
	//相似单词
	private ListView mLvWordSimlarity;
	//音标
	private TextView mTvSoundmark;

	//例句和相似单词 的 adapter
	private BaseAdapter mInstaceAdapter,mSimilarityInstance;
	//启动Intent的Key
	public final static String QUERY="query";
	//默认查询的东西
	private final static String DEFAULT_QUERY="pit";
	//当前查询的东西
	private String mNowQuery="";
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.word_info_activity);

		initView();


		handleIntent(getIntent());

	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);

		Log.v("a3"," new Intent "+intent);
		handleIntent(intent);
	}
	public void initView()
	{
		mTvGroupName=(TextView) findViewById(R.id.word_info_group_name);
		mIvReturn=(ImageView) findViewById(R.id.word_info_return);
		mTvWordContent=(TextView) findViewById(R.id.word_info_word_content);
		mTvWordResult=(TextView) findViewById(R.id.word_info_word_result);
		mIvVoice=(ImageView) findViewById(R.id.word_info_voice);
		mLvWordSimlarity=(ListView) findViewById(R.id.word_info_word_similarity);
		mLvWordInstances= (ListView) findViewById(R.id.word_info_word_instance);
		mTvSoundmark=(TextView) findViewById(R.id.word_info_word_soudmark);

		//返回到上个Activity
		mIvReturn.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				finish();
			}
		});

	}



	public WordInfoItem createWordInfoItem()
	{

		Map<String, String> mapTranslation=new HashMap<String, String>();
		mapTranslation.put("adv.", "上船[飞机,车]；靠船边；在船上；在…上");
		mapTranslation.put("prep.", "上车；在船[飞机,车]上");
		//mapTranslation.put("网络", "在飞机上；登船；登飞机");

		Map<String, String> mapSoudmark=new HashMap<String, String>();
		mapSoudmark.put("american", "[ə'bɔ:rd]");
		mapSoudmark.put("english", "[ə'bɔ:d]");

		List<WordInstance>	listInstance=new ArrayList<WordInstance>();
		listInstance.add(new WordInstance("They were all aboard the ship last nightThey were all aboard the ship last night."
				, "昨天夜里他们都在船上。"));
		listInstance.add(new WordInstance("The train is about to leave . All aboard !"
				, "火车就要开动了。请大家上车！"));

		List<String> similaritydatas=new ArrayList<String>();
		similaritydatas.add("accord n.协议");
		similaritydatas.add("adored v. 崇拜");
		similaritydatas.add("award n.奖");
		return new WordInfoItem("aboard", mapTranslation, mapSoudmark, null, listInstance, similaritydatas);
	}

	/**
	 * 	View绑定数据
	 * @param item
	 */

	public void  bindViewData(WordInfoItem item)
	{
		//单词enlish
		mTvWordContent.setText(item.mContent);
		String strContent="";
		for (String key : item.translation.keySet())
		{
			strContent +=key+"   "+item.translation.get(key)+"\n";
		}
		//单词翻译
		mTvWordResult.setText(strContent.trim());

		String strSoundmark="";
		//音标
		for(String key : item.soundmark.keySet())
		{
			strSoundmark =item.soundmark.get(key);
			break;
		}
		mTvSoundmark.setText(strSoundmark);

		//例句
		if(item.getinstances().size()==0)
		{
			// 如果例句中 没有数据 父View隐藏
			View v=(View) mLvWordInstances.getParent();
			v.setVisibility(View.GONE);
		}
		else
		{
			//例句中 有数据
			mInstaceAdapter=new WordInstanceAdapter(item.getinstances(), this);
			mLvWordInstances.setAdapter(mInstaceAdapter);

		}


		//相似单词
		if(item.similarity.size()==0)
		{
			//相似单中没数据 父View隐藏
			View v=(View) mLvWordSimlarity.getParent();
			v.setVisibility(View.GONE);
		}
		else
		{
			List<WordItem> listSimilarityDatas=new ArrayList<WordItem>();
			for(int i=0;i<item.similarity.size();++i)
			{
				listSimilarityDatas.add(new WordItem(-1, item.similarity.get(i),""));
			}
			mSimilarityInstance=new WordSimilarityAdapter(this, listSimilarityDatas);
			mLvWordSimlarity.setAdapter(mSimilarityInstance);
		}
	}

	/**
	 * 	获取启动Intent
	 * @return
	 */
	public static Intent getStartIntent(Context context,String query)
	{
		Intent intent=new Intent(context,WordInfoActivity.class);
		intent.putExtra(QUERY, query);
		return intent;
	}

	/**
	 * 	处理 Intent
	 * @param
	 */
	public void handleIntent(Intent intent)
	{
		String english=intent.getStringExtra(QUERY);
		if(english==null)
		{
			english=DEFAULT_QUERY;
		}
		english.trim();
		if(mNowQuery.equals(english))
		{
			//查询就是当前界面 就不用理了
			return ;
		}
		Translate.getInstance().translate(english, new SetTranslateResult()
		{

			@Override
			public void setTranslateResult(WordItem result)
			{
				WordInfoItem item;
				mNowQuery=result.mContent.trim();
				if(result instanceof WordInfoItem)
				{
					item=(WordInfoItem) result;
				}
				else
				{
					Map<String, String> mapTranslation=new HashMap<String, String>();
					mapTranslation.put("", result.mResult);
					item=new WordInfoItem(result.mContent, mapTranslation, new HashMap<String, String>(),null, new ArrayList<WordInstance>(), new ArrayList<String>());
				}
				//绑定数据
				bindViewData(item);
			}
		});
	}
}

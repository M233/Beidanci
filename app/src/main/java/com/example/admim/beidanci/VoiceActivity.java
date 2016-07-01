package com.example.admim.beidanci;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.json.JSONArray;
import org.json.JSONObject;




import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import Bg.Local.SearchRecordManager;
import Bg.Translate.SetTranslateResult;
import Bg.Translate.Translate;
import Model.SearchRecordItem;
import Model.WordItem;

public class VoiceActivity extends Activity implements SetTranslateResult
{
	//搜索单词方式的图标
	public	ImageView imageViewSearchMethodIcon;
	//开始语音输入的按钮
	public Button btnSearchVoice;
	//手动输入的 输入框
	public EditText editTextSearchWrite;
	//上一次手动输入搜索的记录
	public String strLastSearchRecord="";
	//查询结果状态图
	public TextView textViewSearchStatus;
	//显示搜索记录
	public ListView listViewSearchRecord;
	public SimpleAdapter simpleAdapterSearchRecord;
	public ArrayList<Map<String,String>> arrayListSearchReocrd;
	//保存搜索记录数据
	public SearchRecordManager searchRecordManager;

	//搜索单词方式  0 语音搜素  1手动输入
	public int nNowSearchMethod=0;

	//语音提示视图----------------
	public View speechTips;
	//声音提示水平
	public View speechWave;

	//------讯飞语音听写---Start----------
	// 讯飞语音听写对象
	private SpeechRecognizer mXunFeiSpeechRecognizer;
	// 讯飞引擎类型
	private String mXunFeiEngineType = SpeechConstant.TYPE_CLOUD;
	/**
	 * 讯飞初始化监听器。
	 */
	private InitListener mXunFeiInitListener = new InitListener() {

		@Override
		public void onInit(int code)
		{
			if (code != ErrorCode.SUCCESS) {
				Log.v("a3","初始化失败，错误码：" + code);
			}
		}
	};
	/**
	 * 讯飞听写监听器。
	 */
	private RecognizerListener mXunFeiRecognizerListener = new RecognizerListener() {

		@Override
		public void onBeginOfSpeech()
		{
			// 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
			print("开始说话");
		}


		@Override
		public void onEndOfSpeech() {
			// 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
			//print//Log("结束说话");
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			String strResult=parseJsonXunFeiVoiceResult(results.getResultString());
			if(strResult.equals(""))
			{
				return;
			}
			//print//Log(results.getResultString());
			//打印识别结果
			print("识别结果为"+strResult);
			//开启翻译
			Translate.getInstance().translate(strResult, VoiceActivity.this);
		}

		@Override
		public void onVolumeChanged(int volume, byte[] data) {
			//print//Log("当前正在说话，音量大小：" + volume);
			//print//Log("返回音频数据："+data.length);
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			// 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
			// 若使用本地能力，会话id为null
			//	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
			//		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
			//		//Log.d(TAG, "session id =" + sid);
			//	}
		}

		@Override
		public void onError(com.iflytek.cloud.SpeechError error)
		{
			// Tips：
			// 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
			// 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
			print(error.getPlainDescription(true));
		}
	};
	//------讯飞语音听写---End----------

	//处理翻译返回的结果
	Handler mHandler=new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			if(msg.what==0x123)
			{
				String[] arrStrFanyi=(String[]) msg.obj;
				String content=arrStrFanyi[0].trim().toLowerCase();
				String result=arrStrFanyi[1].trim().toLowerCase();;
				print("\n翻译原文  :"+content+"\n翻译结果  :"+result);
				addSearchRecord(content,result);
				//播放结果
				//speakResult(content, result);
			}
		};
	};




	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.voice_activity);
		init();
	}
	//初始化
	public void init()
	{
		//-----讯飞语音初始化--start---------
		// 初始化识别无UI识别对象
		// 使用SpeechRecognizer对象，可根据回调消息自定义界面；
		mXunFeiSpeechRecognizer= SpeechRecognizer.createRecognizer(VoiceActivity.this, mXunFeiInitListener);
		//-----讯飞语音初始化--end---------

		//初始化保存数据类
		searchRecordManager=new SearchRecordManager(SearchRecordManager.NSORTMETHODNORSORT);

		imageViewSearchMethodIcon=(ImageView) findViewById(R.id.search_method_icon);
		btnSearchVoice=(Button) findViewById(R.id.search_voice);
		editTextSearchWrite=(EditText) findViewById(R.id.search_write);
		textViewSearchStatus=(TextView) findViewById(R.id.search_status);
		listViewSearchRecord=(ListView) findViewById(R.id.search_record);
		arrayListSearchReocrd=new ArrayList<Map<String,String>>();
		//为search_record设置adapter
		simpleAdapterSearchRecord=new SimpleAdapter(
				this,
				arrayListSearchReocrd,
				R.layout.voice_activity_search_record_item,
				new String[]{"content","result"},
				new int[]{R.id.search_record_item_content,
						R.id.search_record_item_result}
		);
		listViewSearchRecord.setAdapter(simpleAdapterSearchRecord);
		//切换输入模式
		imageViewSearchMethodIcon.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if(nNowSearchMethod==0)
				{
					//切换到手动输入
					imageViewSearchMethodIcon.setImageResource(R.drawable.serach_method_voice_icon);
					btnSearchVoice.setVisibility(View.GONE);
					editTextSearchWrite.setVisibility(View.VISIBLE);
					nNowSearchMethod=1;
				}
				else
				{
					//切换到语音输入
					imageViewSearchMethodIcon.setImageResource(R.drawable.search_method_write_icon);
					btnSearchVoice.setVisibility(View.VISIBLE);
					editTextSearchWrite.setVisibility(View.GONE);
					nNowSearchMethod=0;
				}
			}
		});

		//初始化手动搜索框
		initEditTextSearchRecord();

		//加载语音提示视图
		speechTips=View.inflate(this, R.layout.bd_asr_popup_speech, null);
		speechWave=speechTips.findViewById(R.id.wave);
		//隐藏视图
		speechTips.setVisibility(View.GONE);
		//添加视图
		addContentView(speechTips, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
		//设置按下按钮就开始语音识别
		btnSearchVoice.setOnTouchListener(new OnTouchListener()
		{

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				switch (event.getAction())
				{
					case MotionEvent.ACTION_DOWN:
					/*
						speechTips.setVisibility(View.VISIBLE);
						mSpeechRecognizer.cancel();
						Intent intent=new Intent();
						bindParams(intent);
						intent.putExtra("vad", "touch");
					*/
						textViewSearchStatus.setText("");
						btnSearchVoice.setText("松开 结束");
						//开始监听
						//mSpeechRecognizer.startListening(intent);
						setParam();
						int ret=mXunFeiSpeechRecognizer.startListening(mXunFeiRecognizerListener);
						if (ret != ErrorCode.SUCCESS) {
							print("听写失败,错误码：" + ret);
						} else {
							print("初始化引擎");
						}
						return true;
					case MotionEvent.ACTION_UP:
						//停止监听
						print("正在努力识别中......");
						mXunFeiSpeechRecognizer.stopListening();
						speechTips.setVisibility(View.GONE);
						btnSearchVoice.setText("按住 说话");
						break;
					default:
						break;
				}

				return false;
			}
		});
	}
	/**
	 * 参数设置
	 *
	 * @param
	 * @return
	 */
	public void setParam() {
		// 清空参数
		mXunFeiSpeechRecognizer.setParameter(SpeechConstant.PARAMS, null);

		// 设置听写引擎
		mXunFeiSpeechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, mXunFeiEngineType);
		// 设置返回结果格式
		mXunFeiSpeechRecognizer.setParameter(SpeechConstant.RESULT_TYPE, "json");

		/*
		String lag = mSharedPreferences.getString("iat_language_preference",
				"mandarin");
		if (lag.equals("en_us")) {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
		} else {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			// 设置语言区域
			mIat.setParameter(SpeechConstant.ACCENT, lag);
		}
		*/


		// 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
		//mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));

		// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
		//mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));

		// 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
		//mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));

		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
		mXunFeiSpeechRecognizer.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
		mXunFeiSpeechRecognizer.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");

		// 设置听写结果是否结果动态修正，为“1”则在听写过程中动态递增地返回结果，否则只在听写结束之后返回最终结果
		// 注：该参数暂时只对在线听写有效
		//mIat.setParameter(SpeechConstant.ASR_DWA, mSharedPreferences.getString("iat_dwa_preference", "0"));
	}





    /*
    //音量变化
    @Override
    public void onRmsChanged(float rmsdB) {
        final int VTAG = 0xFF00AA01;
        Integer rawHeight = (Integer) speechWave.getTag(VTAG);
        if (rawHeight == null) {
            rawHeight = speechWave.getLayoutParams().height;
            speechWave.setTag(VTAG, rawHeight);
        }

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) speechWave.getLayoutParams();
        params.height = (int) (rawHeight * rmsdB * 0.01);
        params.height = Math.max(params.height , speechWave.getMeasuredWidth());
        speechWave.setLayoutParams(params);
    }
    */




    /*
    //结果
    @Override
    public void onResults(Bundle results) {
        long end2finish = System.currentTimeMillis() - speechEndTime;
        ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        print("识别成功：" + Arrays.toString(nbest.toArray(new String[nbest.size()])));
        print//Log("识别成功：" + Arrays.toString(nbest.toArray(new String[nbest.size()])));
        String json_res = results.getString("origin_result");
        try {
            //print("origin_result=\n" + new JSONObject(json_res).toString(4));
        } catch (Exception e) {
            print("origin_result=[warning: bad json]\n" + json_res);
        }
        String strEnd2Finish = "";
        if (end2finish < 60 * 1000) {
            strEnd2Finish = "(waited " + end2finish + "ms)";
        }
        String result="语音识别结果: "+nbest.get(0) + strEnd2Finish;
        print(result);
        print//Log(result);
        print//Log("---------------------------------------------------------------");
        Translate.translate(nbest.get(0), this);
    }

    //临时识别结果
    @Override
    public void onPartialResults(Bundle partialResults)
    {
        ArrayList<String> nbest = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (nbest.size() > 0) {
            print//Log("~临时识别结果：" + Arrays.toString(nbest.toArray(new String[0])));
            print//Log(nbest.get(0));
        }
    }
	*/


	private void print(String msg)
	{
		textViewSearchStatus.append(msg + "\n");
	}
	@Override
	public void setTranslateResult(WordItem item)
	{
		Message msg=new Message();
		msg.what=0x123;
		msg.obj=new String[]{item.mContent.trim().toLowerCase(),item.mResult.trim().toLowerCase()};
		mHandler.sendMessage(msg);
	}

	//添加数据到 搜索记录中
	public void addSearchRecord(String strContent,String strResult)
	{
		int position=searchRecordManager.getPosition(strContent);
		if(position>-1)
		{
			SearchRecordItem item=searchRecordManager.getRecord(position);
			if(item==null)
			{
				return;
			}
			//增加搜索次数
			item.addCount();
			//如果搜索记录已经存在 则把焦点设置到那里 返回
			listViewSearchRecord.setSelection(position);
			return ;
		}
		//保存数据到搜索记录中
		searchRecordManager.addRecord(strContent, strResult);
		//更新记录视图
		Map<String, String> map=new HashMap<String, String>();
		map.put("content", strContent);
		map.put("result", strResult);
		arrayListSearchReocrd.add(map);
		simpleAdapterSearchRecord.notifyDataSetChanged();
		//移动焦点到最后一个
		listViewSearchRecord.setSelection(arrayListSearchReocrd.size() - 1);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		//当退出activity的时候 存储数据
		if(keyCode==KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN)
		{
			Intent intent=new Intent();
			Bundle bundle=new Bundle();
			bundle.putSerializable("update_data", searchRecordManager.arrayListSearchRecords);
			intent.putExtras(bundle);
			setResult(1, intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}




	@Override
	protected void onDestroy()
	{

		//释放讯飞
		mXunFeiSpeechRecognizer.cancel();
		mXunFeiSpeechRecognizer.destroy();

		super.onDestroy();
	}

	//解析讯飞的语音识别结果
	public String parseJsonXunFeiVoiceResult(String strJson)
	{
		try
		{
			String returnStr="";
			JSONObject jsonObject=new JSONObject(strJson);
			JSONArray wordArray=jsonObject.getJSONArray("ws");
			for(int i=0;i<wordArray.length();++i)
			{
				JSONObject obj=wordArray.getJSONObject(i);
				JSONArray chineseWordArray=obj.getJSONArray("cw");
				for(int k=0;k<chineseWordArray.length();++k)
				{
					String strTmp=chineseWordArray.getJSONObject(k).getString("w");
					returnStr +=strTmp;
				}
			}
			Pattern pattern=Pattern.compile("[。!? ]");
			Matcher matcher=pattern.matcher(returnStr);
			if(matcher.replaceAll("").length()==0)
			{
				return "";
			}
			return returnStr;
		}
		catch (Exception e)
		{
			return e.toString();
		}
	}

	/*
	 * 	初始化手动输入框
	 */
	public void initEditTextSearchRecord()
	{
		editTextSearchWrite.setOnKeyListener(new OnKeyListener()
		{

			@Override
			public boolean onKey(View view, int keyCode, KeyEvent keyEvent)
			{
				if(keyCode==KeyEvent.KEYCODE_ENTER)
				{
					String strSearchContent=editTextSearchWrite.getText().toString().trim();
					if(!strSearchContent.equals("") && !strSearchContent.equals(strLastSearchRecord))
					{
						//Log.v("a3","手动输入"+keyCode+" 翻译"+strSearchContent);
						strLastSearchRecord=strSearchContent;
						textViewSearchStatus.setText("");
						Translate.getInstance().translate(strSearchContent, VoiceActivity.this);
					}
					//清空
					editTextSearchWrite.setText("");
					return false;
				}
				return false;
			}
		});
	}

	//获取VoiceActivity 的启动方式
	public static Intent getStartIntent(Context context)
	{
		Intent intent=new Intent(context,VoiceActivity.class);
		return intent;
	}
}


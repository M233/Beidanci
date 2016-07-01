package com.example.admim.beidanci;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import android.app.Application;

public class MyApplication extends Application
{
	//Volley 请求队列
	private static RequestQueue mVolleyQueue;
	@Override
	public void onCreate()
	{
		super.onCreate();
		mVolleyQueue=Volley.newRequestQueue(this);

		//讯飞语音注册
		SpeechUtility.createUtility(this, SpeechConstant.APPID + "=57667e55");

	}

	//获取Volley请求队列
	public static RequestQueue getVolleyQueue()
	{
		return mVolleyQueue;
	}
}

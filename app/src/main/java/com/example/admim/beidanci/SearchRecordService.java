package com.example.admim.beidanci;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import Bg.Local.SearchRecordDatabaseManager;
import Model.SearchRecordItem;
import android.R.integer;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

public class SearchRecordService extends IntentService
{
	//事件类型  1  向数据库写入一堆数据
	public static final int NEVENTTYPEINSERTSOME=101;
	//事件类型  2  从服务器获取数据
	public static final int NEVENTTYPEGETSEARCHRECORD=102;
	//事件类型  3 删除指定记录
	public static final int NEVENTTYPEREMOVESEARCHRECORD=103;

	public SearchRecordDatabaseManager databaseManager;
	public SearchRecordService()
	{
		super("beidanci_search_record");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		databaseManager=new SearchRecordDatabaseManager(this);
		Bundle bundle=intent.getExtras();
		int eventType=bundle.getInt("event_type");
		////Log.v("a1","IntentService onHandlerIntent()-- "+eventType);
		switch(eventType)
		{
			//插入一些数据
			case NEVENTTYPEINSERTSOME:
				ArrayList<SearchRecordItem> arrayListUpdateSearchRecord=(ArrayList<SearchRecordItem>) bundle.getSerializable("update_data");
				handleInsertItems(arrayListUpdateSearchRecord);
				break;
			//获取搜索记录
			case NEVENTTYPEGETSEARCHRECORD:
				int sortMethod=bundle.getInt("sort_method");
				int page=bundle.getInt("page");
				////Log.v("a1", " page: "+page+" | sortMethod"+sortMethod);
				handleGetSearchRecord(page, sortMethod,eventType);
				break;
			//删除指定记录
			case NEVENTTYPEREMOVESEARCHRECORD:
				String content=bundle.getString("content");
				handleRemoveItem(content);
				break;
			default:
				break;
		}
	}

	/*
	 * 处理Activity传进来的插入记录
	 */
	public void handleInsertItems(ArrayList<SearchRecordItem> arrayList)
	{
		//写入数据到服务器中
		databaseManager.updateItems(arrayList);
	}
	/*
	 * 	处理Activity请求的更新数据
	 * 		param:	page  页数
	 * 				sortMethod 数据的排序方式
	 */
	public void handleGetSearchRecord(int page, int sortMethod,int eventType)
	{
		ArrayList<SearchRecordItem> arrayList=databaseManager.getItems(page, sortMethod);
		Intent intent=new Intent();
		intent.setAction(MainActivity.STRBRROADCASTRECEIVERACTION);
		Bundle bundle=new Bundle();
		bundle.putSerializable("update_search_record",arrayList );
		////Log.v("a1","service arrayList:"+arrayList.toString());
		bundle.putInt("sort_method", sortMethod);
		bundle.putInt("event_type",eventType);
		intent.putExtras(bundle);
		//发送广播
		sendBroadcast(intent);
	}

	/*
	 * 删除指定的记录
	 */
	public void handleRemoveItem(String content)
	{
		//Log.v("a1", "service "+content);
		databaseManager.removeItem(content);
	}
}

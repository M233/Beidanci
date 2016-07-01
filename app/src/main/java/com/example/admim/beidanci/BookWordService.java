package com.example.admim.beidanci;

import java.util.ArrayList;


import Bg.Local.BookWordDatabaseManager;
import Model.WordGroupItem;
import Model.WordItem;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

public class BookWordService extends IntentService
{
	//获取所有数据  单词和单词本
	public static final int NEVENTGETALL=201;
	//插入单词
	public static final int NEVENTINSERTWORDITEM=202;
	//插入单词组
	public static final int NEVENTINSERTWORDGROUPITEM=203;
	//删除单词
	public static final int NEVENTDELETEWORDITEM=204;
	//删除单词组
	public static final int NEVENTDELETEWORDGROUPITEM=205;
	//把单词移动到单词组
	public static final int NEVENTMOVETOGROUP=206;
	//修改组名
	public static final int NEVENTCHANGEWORDGROUPNAME=207;

	public BookWordDatabaseManager databaseManager;
	public BookWordService()
	{
		super("bandanci_book_word");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		databaseManager=new BookWordDatabaseManager(this);
		Bundle bundle=intent.getExtras();
		int eventType=bundle.getInt("event_type");
		WordItem wordItem;
		WordGroupItem wordGroupItem;
		switch (eventType)
		{
			//获取所有单词和单词本信息
			case NEVENTGETALL:
				handleGetAll();
				break;
			//插入单词
			case NEVENTINSERTWORDITEM:
				wordItem=(WordItem) bundle.getSerializable("word_item");
				handleInsertWordItem(wordItem);
				break;
			//插入单词组
			case NEVENTINSERTWORDGROUPITEM:
				wordGroupItem=(WordGroupItem) bundle.getSerializable("word_group_item");
				handleInsertWordGroupItem(wordGroupItem);
				break;
			//删除单词
			case NEVENTDELETEWORDITEM:
				wordItem=new WordItem(-1,  bundle.getString("content"),"");
				handleDeleteWordItem(wordItem);
				break;
			//删除单词组
			case NEVENTDELETEWORDGROUPITEM:
				wordGroupItem=(WordGroupItem) bundle.getSerializable("word_group_item");
				handleDeleteWordGroupItem(wordGroupItem);
				break;
			//移动单词到单词组
			case NEVENTMOVETOGROUP:
				int groupId=bundle.getInt("group_id");
				String wordContent=bundle.getString("word_content");
				handleMoveToGroup(groupId, wordContent);
				break;
			case NEVENTCHANGEWORDGROUPNAME:
				int groupId2=bundle.getInt("group_id");
				String newName=bundle.getString("new_name");
				handleChangeGroupName(groupId2, newName);
				break;
			default:
				break;
		}
	}

	//获取所有数据
	private void handleGetAll()
	{
		ArrayList<WordGroupItem> arrayListWordGroup=databaseManager.getAllWordGroupItems();
		//添加默认分组 goupId=-1
		arrayListWordGroup.add(new WordGroupItem("默认分组"));

		ArrayList<WordItem> arrayListWord=databaseManager.getAllWordItems();
		Intent intent=new Intent();
		intent.setAction(MainActivity.STRBRROADCASTRECEIVERACTION);
		Bundle bundle=new Bundle();
		bundle.putSerializable("update_word_items", arrayListWord);
		bundle.putSerializable("update_word_group_items", arrayListWordGroup);
		bundle.putInt("event_type", BookWordService.NEVENTGETALL);
		intent.putExtras(bundle);
		//发送广播
		sendBroadcast(intent);
	}
	//处理  插入单词
	private void handleInsertWordItem(WordItem item)
	{
		databaseManager.updateWordItem(item);
		//Log.v("a1", ""+item.mContent+" | "+item.mResult+" | "+item.mGroupId);
	}
	//处理 插入单词组
	private void handleInsertWordGroupItem(WordGroupItem item)
	{
		//如果groupId=-1 则代表返回失败
		int groupId=databaseManager.updateWordGroupItem(item);
		item.mGroupId=groupId;
		Intent intent=new Intent();
		intent.setAction(MainActivity.STRBRROADCASTRECEIVERACTION);
		Bundle bundle=new Bundle();
		bundle.putSerializable("word_group_item",item);
		bundle.putInt("event_type", BookWordService.NEVENTINSERTWORDGROUPITEM);
		intent.putExtras(bundle);
		//发送广播
		sendBroadcast(intent);
	}
	//处理  删除单词
	private void handleDeleteWordItem(WordItem item)
	{
		databaseManager.removeWordItem(item);
	}
	//处理 删除单词组
	private void handleDeleteWordGroupItem(WordGroupItem item)
	{
		databaseManager.removeWordGroupItem(item);
	}
	//处理 把单词移动到单词组
	private void handleMoveToGroup(int groupId,String wordContent)
	{
		boolean result=databaseManager.moveToGroup(groupId, wordContent);
		Intent intent=new Intent();
		intent.setAction(MainActivity.STRBRROADCASTRECEIVERACTION);
		Bundle bundle=new Bundle();
		bundle.putInt("event_type", BookWordService.NEVENTMOVETOGROUP);
		bundle.putInt("group_id", groupId);
		bundle.putString("word_content", wordContent);
		//result 结果 true为成功 false为 没有成功
		bundle.putBoolean("result", result);
		intent.putExtras(bundle);
		//发送广播
		sendBroadcast(intent);
	}

	//处理 修改单词组的名字
	private void handleChangeGroupName(int groupId,String newGroupName)
	{
		boolean result=databaseManager.changeGroupName(groupId, newGroupName);
		//把结果返回到MainActivity中
		Intent intent=new Intent();
		intent.setAction(MainActivity.STRBRROADCASTRECEIVERACTION);
		Bundle bundle=new Bundle();
		bundle.putInt("event_type", BookWordService.NEVENTCHANGEWORDGROUPNAME);
		//result true 修改成功   false修改失败
		bundle.putBoolean("result",result);
		bundle.putInt("group_id", groupId);
		bundle.putString("new_group_name", newGroupName);
		intent.putExtras(bundle);
		//发送广播
		sendBroadcast(intent);
	}

	@Override
	public void onDestroy()
	{
		databaseManager.close();
		super.onDestroy();
	}
}

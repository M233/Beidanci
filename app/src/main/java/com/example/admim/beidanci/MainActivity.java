package com.example.admim.beidanci;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import java.util.ArrayList;

import Bg.Local.BookWordManager;
import Bg.Local.SearchRecordDatabaseManager;
import Bg.Local.SearchRecordManager;
import Model.SearchRecordItem;
import Model.WordGroupItem;
import Model.WordItem;

import View.FragmentIndex;
import View.FragmentRecord;
import View.FragmentBookWord;
import View.FragmentMy;


public class MainActivity extends FragmentActivity implements View.OnClickListener
{
    /*
     * 		搜索记录
     *   		timeSearchRecordManager  时间排序
     *   		countSearchRecordManager  搜索次数排序
     */
    public static SearchRecordManager timeSearchRecordManager;
    public static SearchRecordManager countSearchRecordManager;
    /*
     * 单词本
     * 		timeBookWordManager		时间
     * 		groupBookWordManager	分组排序
     * 		noSortBookWordManager	乱序排序
     */
    public static BookWordManager timeBookWordManager;
    public static BookWordManager groupBookWordManager;
    public static BookWordManager noSortBookWordManager;

    //MainActivity 的 BroadcastReceiver
    public MainActivityBroadcastReceiver mainActivityBroadcastReceiver;
    //BroadcastReceiver的action
    public static final String STRBRROADCASTRECEIVERACTION="MIN.MAINACTIVITY.BROADCASTRECEIVER";

    FragmentIndex oFragmentIndex;
    FragmentRecord oFragmentRecord;
    FragmentBookWord oFragmentBookWord;
    FragmentMy oFragmentMy;
    //导航栏上的按钮
    LinearLayout btnNavQuery,btnNavRecord,btnNavBookWord,btnNavMy;
    //导航栏上的图标
    ImageView imageViewQuery,imageViewRecord,imageViewBookWord,imageViewMy;

    //目前的fragment
    public int  nNowNavBar=0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //讯飞语音注册
        SpeechUtility.createUtility(MainActivity.this, SpeechConstant.APPID+"=568bc51b");

        timeSearchRecordManager=new SearchRecordManager(SearchRecordManager.NSORTMETHODTIME);
        countSearchRecordManager=new SearchRecordManager(SearchRecordManager.NSORTMETHODCOUNT);
        timeBookWordManager=new BookWordManager(BookWordManager.NSORTTIME);
        groupBookWordManager=new BookWordManager(BookWordManager.NSORTGROUP);
        noSortBookWordManager=new BookWordManager(BookWordManager.NSORTNOSORT);

        //创建BroadcastReceiver
        createBroadcastRecevice();
        //从Service获取记录
        getSearchRecord(1, SearchRecordDatabaseManager.NSORTMETHODCOUNT);
        getSearchRecord(1, SearchRecordDatabaseManager.NSORTMETHODTIME);

        //从Service获取单词和单词本
        getBookWord();

        btnNavQuery=(LinearLayout) findViewById(R.id.nav_bar_query);
        btnNavRecord=(LinearLayout) findViewById(R.id.nav_bar_record);
        btnNavBookWord=(LinearLayout) findViewById(R.id.nav_bar_word_book);
        btnNavMy=(LinearLayout) findViewById(R.id.nav_bar_my);
        imageViewQuery=(ImageView) findViewById(R.id.nav_bar_query_image);
        imageViewRecord=(ImageView) findViewById(R.id.nav_bar_record_image);
        imageViewBookWord=(ImageView) findViewById(R.id.nav_bar_book_word_image);
        imageViewMy=(ImageView) findViewById(R.id.nav_bar_my_image);

        oFragmentIndex=new FragmentIndex();
        oFragmentRecord=new FragmentRecord();
        oFragmentBookWord=new FragmentBookWord();
        oFragmentMy=new FragmentMy();

        //设置底部按钮点击
        btnNavQuery.setOnClickListener(this);
        btnNavRecord.setOnClickListener(this);
        btnNavBookWord.setOnClickListener(this);
        btnNavMy.setOnClickListener(this);
        getFragmentManager().beginTransaction().replace(R.id.fragment_item, oFragmentIndex).commit();

    }


    @Override
    public void onClick(View view)
    {
        LinearLayout btn=(LinearLayout) view;
        switch (btn.getId())
        {
            case R.id.nav_bar_query:
                chageNavbar(0);
                break;
            case R.id.nav_bar_record:
                chageNavbar(1);
                break;
            case R.id.nav_bar_word_book:
                chageNavbar(2);
                break;
            case R.id.nav_bar_my:
                chageNavbar(3);
                break;
            default:
                break;
        }
    }

    //切换Fragment
    public void chageNavbar(int position)
    {
        if(position==nNowNavBar)
        {
            return ;
        }
        imageViewQuery.setImageResource(R.drawable.nav_bar_query);
        imageViewRecord.setImageResource(R.drawable.nav_bar_record);
        imageViewBookWord.setImageResource(R.drawable.nav_bar_book_word);
        imageViewMy.setImageResource(R.drawable.nav_bar_my);
        Fragment fragment;
        //更新Query的手写模式的搜索记录
        //oFragmentQuery.updateMainActivitySearchRecord();
        switch (position)
        {
            case 0:
                fragment=oFragmentIndex;
                imageViewQuery.setImageResource(R.drawable.nav_bar_query_selected);
                break;
            case 1:
                fragment=oFragmentRecord;
                imageViewRecord.setImageResource(R.drawable.nav_bar_record_selected);
                break;
            case 2:
                fragment=oFragmentBookWord;
                imageViewBookWord.setImageResource(R.drawable.nav_bar_book_word_selected);
                break;
            case 3:
                fragment=oFragmentMy;
                imageViewMy.setImageResource(R.drawable.nav_bar_my_selected);
                break;
            default:
                return;
        }
        getFragmentManager().beginTransaction().replace(R.id.fragment_item, fragment).commit();
        nNowNavBar=position;

    }

    //添加数据到搜索记录上
    public void addSearchRecord(ArrayList<SearchRecordItem> arrayListNewSearchRecord)
    {
        //添加内存到数据库中
        Intent intent=new Intent(MainActivity.this,SearchRecordService.class);
        Bundle bundle=new Bundle();
        bundle.putInt("event_type", SearchRecordService.NEVENTTYPEINSERTSOME);
        bundle.putSerializable("update_data", arrayListNewSearchRecord);
        intent.putExtras(bundle);
        startService(intent);
        //添加数据到内存中
        timeSearchRecordManager.addRecords(arrayListNewSearchRecord);
        countSearchRecordManager.addRecords(arrayListNewSearchRecord);
    }
    //获取搜索记录
    public void getSearchRecord(int page,int nSortMethod)
    {
        Intent intent=new Intent(MainActivity.this,SearchRecordService.class);
        Bundle bundle=new Bundle();
        bundle.putInt("event_type",SearchRecordService.NEVENTTYPEGETSEARCHRECORD );
        bundle.putInt("page", page);
        bundle.putInt("sort_method",nSortMethod);
        intent.putExtras(bundle);
        //
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(intent);
    }
    //删除搜索记录
    public void removeSearchRecord(String content)
    {
        Intent intent=new Intent(MainActivity.this,SearchRecordService.class);
        Bundle bundle=new Bundle();
        bundle.putInt("event_type", SearchRecordService.NEVENTTYPEREMOVESEARCHRECORD);
        bundle.putString("content", content);
        intent.putExtras(bundle);

        ////Log.v("a1", "activity "+content);
        //启动搜索记录后台
        startService(intent);
    }
    //创建广播
    public void createBroadcastRecevice()
    {
        mainActivityBroadcastReceiver=new MainActivityBroadcastReceiver();
        IntentFilter intentFilter=new IntentFilter(STRBRROADCASTRECEIVERACTION);
        registerReceiver(mainActivityBroadcastReceiver, intentFilter);

    }

    //这个BroadcastReceiver是动态配置权限的  然后 Service和Activity交换数据(愉快搞基)的地方
    public class MainActivityBroadcastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Bundle bundle=intent.getExtras();
            int event_type=bundle.getInt("event_type");
            int groupId=0;
            boolean result=false;
            switch (event_type)
            {
                //处理SERVICE返回的记录
                case SearchRecordService.NEVENTTYPEGETSEARCHRECORD:
                    ArrayList<SearchRecordItem> arrayListSearchRecord=(ArrayList<SearchRecordItem>) bundle.getSerializable("update_search_record");
                    int nSortMethod=bundle.getInt("sort_method");
                    handleServiceUpdateSearchRecord(arrayListSearchRecord,nSortMethod);
                    break;
                //获取所有的单词和单词本记录
                case BookWordService.NEVENTGETALL:
                    ArrayList<WordItem> arrayListWord=(ArrayList<WordItem>) bundle.getSerializable("update_word_items");
                    ArrayList<WordGroupItem> arrayListWordGroup=(ArrayList<WordGroupItem>) bundle.getSerializable("update_word_group_items");
                    handleServiceUpdateBookWord(arrayListWordGroup,arrayListWord);
                    break;
                //处理插入单词本
                case BookWordService.NEVENTINSERTWORDGROUPITEM:
                    WordGroupItem groupItem=(WordGroupItem) bundle.getSerializable("word_group_item");
                    handleServiceInsertWordGroupItem(groupItem);
                    break;
                //处理单词移动到单词本
                case BookWordService.NEVENTMOVETOGROUP:
                    result=bundle.getBoolean("result");
                    if(!result)
                    {
                        //移动失败
                        break;
                    }
                    groupId=bundle.getInt("group_id");
                    String wordContent=bundle.getString("word_content");
                    BookWordManager bbx= groupBookWordManager;
                    handleServiceMoveToGroup(groupId, wordContent);
                    break;
                //处理修改组的名字
                case BookWordService.NEVENTCHANGEWORDGROUPNAME:
                    result=bundle.getBoolean("result");
                    if(!result)
                    {
                        //修改失败
                        break;
                    }
                    groupId=bundle.getInt("group_id");
                    String groupName=bundle.getString("new_group_name");
                    handleServiceChangeWordGroupName(groupId, groupName);
                    break;
                default:
                    break;
            }

        }
    }
    //处理从service返回的搜索记录
    public void handleServiceUpdateSearchRecord(ArrayList<SearchRecordItem> newSearchRecord,int nSortMethod)
    {
        switch(nSortMethod)
        {
            case SearchRecordDatabaseManager.NSORTMETHODCOUNT:
                countSearchRecordManager.addRecords(newSearchRecord);
                ////Log.v("a1","BroadcastReceiver count : "+ countSearchRecordManager.arrayListSearchRecords.toString());
                break;
            case SearchRecordDatabaseManager.NSORTMETHODTIME:
                timeSearchRecordManager.addRecords(newSearchRecord);
                ////Log.v("a1","BroadcastReceiver time: "+ timeSearchRecordManager.arrayListSearchRecords.toString());
                break;
            default:
                break;
        }
    }
    //获取所有的单词和单词本
    public void getBookWord()
    {
        Intent intent=new Intent(MainActivity.this,BookWordService.class);
        Bundle bundle=new Bundle();
        bundle.putInt("event_type", BookWordService.NEVENTGETALL);
        intent.putExtras(bundle);
        startService(intent);
    }
    //处理 服务器获取的单词本更新数据
    public void handleServiceUpdateBookWord(ArrayList<WordGroupItem> arrayListWordGroup,ArrayList<WordItem> arrayListWord)
    {
        ////Log.v("a1","单词本:"+arrayListWordGroup+"  | \n单词:"+arrayListWord);
        //时间排序
        timeBookWordManager.arrayListWords=new ArrayList<WordItem>(arrayListWord);
        timeBookWordManager.arrayListWordGroups=new ArrayList<WordGroupItem>(arrayListWordGroup);
        //组排序
        groupBookWordManager.arrayListWords=new ArrayList<WordItem>(arrayListWord);
        groupBookWordManager.arrayListWordGroups=new ArrayList<WordGroupItem>(arrayListWordGroup);
        //乱序
        noSortBookWordManager.arrayListWords=new ArrayList<WordItem>(arrayListWord);
        noSortBookWordManager.arrayListWordGroups=new ArrayList<WordGroupItem>(arrayListWordGroup);
        noSortBookWordManager.shuffle();
    }
    //添加一个单词到数据库中
    public void addWord(String content,String result)
    {
        Intent intent=new Intent(MainActivity.this,BookWordService.class);
        Bundle bundle=new Bundle();
        bundle.putInt("event_type", BookWordService.NEVENTINSERTWORDITEM);
        bundle.putSerializable("word_item", new WordItem(-1, content, result));
        intent.putExtras(bundle);
        //启动后台
        startService(intent);
        timeBookWordManager.addWord(content,result);
        groupBookWordManager.addWord(content,result);
        noSortBookWordManager.addWord(content, result);
    }
    //添加一个单词组
    public void addWordGroup(String groupName)
    {
        Intent intent=new Intent(MainActivity.this,BookWordService.class);
        Bundle bundle=new Bundle();
        bundle.putInt("event_type", BookWordService.NEVENTINSERTWORDGROUPITEM);
        bundle.putSerializable("word_group_item", new WordGroupItem(groupName));
        intent.putExtras(bundle);
        //开启服务
        startService(intent);
    }
    //处理服务器返回的添加单词组信息
    public void handleServiceInsertWordGroupItem(WordGroupItem groupItem)
    {
        if(groupItem.mGroupId==-1)
        {
            return;
        }
        groupBookWordManager.addWordGroup(groupItem);
        oFragmentBookWord.adapterGroup.notifyDataSetChanged();
    }
    //把单词移动到单词组
    public void moveToGroup(String groupName,String wordContent)
    {
        int groupId=groupBookWordManager.getGroupId(groupName);
        if(groupId==-2)
        {
            //出错 groupName对应的WordGroup不存在
            return;
        }
        Intent intent=new Intent(MainActivity.this,BookWordService.class);
        Bundle bundle=new Bundle();
        bundle.putInt("event_type", BookWordService.NEVENTMOVETOGROUP);
        bundle.putInt("group_id", groupId);
        bundle.putString("word_content", wordContent);
        intent.putExtras(bundle);
        //启动后台
        startService(intent);

    }
    //删除单词
    public void removeWord(String content)
    {
        //从内存中删除记录
        timeBookWordManager.removeWord(content);
        noSortBookWordManager.removeWord(content);
        groupBookWordManager.removeWord(content);
        //从服务器中删除单词
        Intent intent=new Intent(MainActivity.this,BookWordService.class);
        Bundle bundle=new Bundle();
        bundle.putInt("event_type", BookWordService.NEVENTDELETEWORDITEM);
        bundle.putString("content", content);
        intent.putExtras(bundle);
        startService(intent);

        //更新listview
        updateFragmentBookWordUi();
    }
    //响应从Service返回的把单词移动到单词组
    public void handleServiceMoveToGroup(int groupId,String wordContent)
    {
        WordItem item=groupBookWordManager.getWordItem(wordContent);
        if(item==null)
        {
            ////Log.v("a1","handleServiceMoveToGroup 移动单词到单词本 失败!");
            return;
        }
        //把单词移动到对应组
        item.mGroupId=groupId;

        ////Log.v("a3","移动单词到单词本成功: "+wordContent);
        oFragmentBookWord.mapGroupWords=groupBookWordManager.getAllGroupChild();
        oFragmentBookWord.adapterGroup.notifyDataSetChanged();
    }

    //判断是否能修改单词组名
    public boolean judgeChangeWordGroupName(String oldName,String newName)
    {
        return groupBookWordManager.judgeChangeWordGroupName(oldName, newName);
    }

    //修改单词组的名字
    public void changeWordGorupName(String oldName,String newName)
    {
        if(!judgeChangeWordGroupName(oldName, newName))
        {
            return;
        }
        int groupId=groupBookWordManager.getGroupId(oldName);
        //从数据库中修改组名
        Intent intent=new Intent(MainActivity.this,BookWordService.class);
        Bundle bundle=new Bundle();
        bundle.putInt("event_type",BookWordService.NEVENTCHANGEWORDGROUPNAME);
        bundle.putInt("group_id",groupId);
        bundle.putString("new_name", newName);
        intent.putExtras(bundle);
        //启动Service
        startService(intent);
    }
    //响应从Service返回的修改单词组的名字
    public void handleServiceChangeWordGroupName(int groupId,String newGroupName)
    {
        WordGroupItem item=groupBookWordManager.getWordGroupItem(groupId);
        if(null==item)
        {
            //获取单词组失败
            return ;
        }
        //更新内存中的数据
        if(!groupBookWordManager.changeWordGroupName(item.mName,newGroupName))
        {
            //修改单词组失败
            return;
        }
        //更新UI
        ////Log.v("a3","移动单词本名字成功: "+item.mName+" -> "+newGroupName);
        oFragmentBookWord.mapGroupWords=groupBookWordManager.getAllGroupChild();
        oFragmentBookWord.adapterGroup.notifyDataSetChanged();
    }

    //删除一个单词组
    public void removeWordGroup(String name)
    {
        WordGroupItem item=groupBookWordManager.getWordGroupItem(name);
        //从Service中删除
        Intent intent=new Intent(MainActivity.this,BookWordService.class);
        Bundle bundle=new Bundle();
        bundle.putInt("event_type", BookWordService.NEVENTDELETEWORDGROUPITEM);
        bundle.putSerializable("word_group_item", item);
        intent.putExtras(bundle);
        //启动Service
        startService(intent);

        //从内存中删除
        groupBookWordManager.removeWordGroup(item);
        timeBookWordManager.removeWordGroup(item);
        noSortBookWordManager.removeWordGroup(item);

        //更新视图
        updateFragmentBookWordUi();
    }

    //更新单词本 页面的UI
    public void updateFragmentBookWordUi()
    {
        oFragmentBookWord.adapterTime.notifyDataSetChanged();
        oFragmentBookWord.adapterNoSort.notifyDataSetChanged();
        oFragmentBookWord.mapGroupWords=groupBookWordManager.getAllGroupChild();
        oFragmentBookWord.adapterGroup.notifyDataSetChanged();
    }
    @Override
    protected void onDestroy()
    {
        //oFragmentQuery.updateMainActivitySearchRecord();

        //注销广播接受器
        unregisterReceiver(mainActivityBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);

        if(requestCode==1 && resultCode==1)
        {
            //接收语音收索的搜索记录
            Bundle bundle=intent.getExtras();
            ArrayList<SearchRecordItem> arrayListNewSearchRecord=(ArrayList<SearchRecordItem>) bundle.getSerializable("update_data");
            //Log.v("a3", "result activity Main activity" + arrayListNewSearchRecord.size());
            //回传数据到MainActivity
            addSearchRecord(arrayListNewSearchRecord);
        }
    }
}

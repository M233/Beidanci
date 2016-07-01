package com.example.admim.beidanci;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Bg.Local.SearchRecordManager;
import Bg.Translate.SetTranslateResult;
import Bg.Translate.Translate;
import Model.SearchRecordItem;
import Model.WordItem;
import View.WordQueryRecordAdapter;

public class WriteQueryActivity extends Activity implements SetTranslateResult
{
    //语音图标
    private ImageView mIvVoice;
    //输入框
    private EditText mEtWrite;
    //搜索记录
    private ListView mLvRecord;
    //搜索记录的 Adapter
    private WordQueryRecordAdapter mAdapterRecord;
    //保存搜索记录数据
    public SearchRecordManager searchRecordManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_query_activity);

        mIvVoice= (ImageView) findViewById(R.id.write_query_toolbar_voice);
        mEtWrite= (EditText) findViewById(R.id.write_query_toolbar_et);

        mIvVoice.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //启动语音搜索
                startActivity(VoiceActivity.getStartIntent(WriteQueryActivity.this));
            }
        });

        mEtWrite.setOnKeyListener(new View.OnKeyListener()
        {

            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent)
            {
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.KEYCODE_UNKNOWN)
                {
                    String strSearchContent = mEtWrite.getText().toString().trim();
                    if (!strSearchContent.equals(""))
                    {
                        //Log.v("a3", "手动输入" + keyCode + " 翻译" + strSearchContent+"  "+keyEvent.getAction());
                        Translate.getInstance().translate(strSearchContent, WriteQueryActivity.this);
                    }
                    mEtWrite.setText("");
                    return true;
                }
                return false;
            }
        });

        //搜索记录初始化
        mLvRecord= (ListView) findViewById(R.id.write_query_record);
        mAdapterRecord=new WordQueryRecordAdapter(this,new ArrayList<SearchRecordItem>());
        mLvRecord.setAdapter(mAdapterRecord);
        searchRecordManager=new SearchRecordManager(SearchRecordManager.NSORTMETHODNORSORT);
    }

    /**
     *  设置翻译结果
     * @param result
     */
    @Override
    public void setTranslateResult(WordItem result)
    {
        addSearchRecord(result.mContent,result.mResult);
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
            mLvRecord.setSelection(position);
            return ;
        }
        //保存数据到搜索记录中
        searchRecordManager.addRecord(strContent, strResult);
        //更新记录视图
        Map<String, String> map=new HashMap<String, String>();
        map.put("content", strContent);
        map.put("result", strResult);
        //首添加
        mAdapterRecord.mDatas.add(0,new SearchRecordItem(strContent,strResult));
        mAdapterRecord.notifyDataSetChanged();
    }

    /**
     *  获取启动的intent
     * @param context
     * @return
     */
    public static Intent getStartIntent(Context context)
    {
        Intent intent=new Intent(context,WriteQueryActivity.class);
        return intent;
    }
}

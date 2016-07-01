package View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import Bg.Local.SearchRecordManager;
import Bg.Translate.SetTranslateResult;
import Bg.Translate.Translate;
import Model.SearchRecordItem;
import Model.WordItem;
import View.FragmentBookWord.ViewHolder;
import android.R.integer;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.view.View.OnKeyListener;

import com.example.admim.beidanci.MainActivity;
import com.example.admim.beidanci.R;
import com.example.admim.beidanci.VoiceActivity;

public class FragmentQuery extends Fragment implements OnClickListener,SetTranslateResult
{
	//进入语音输如的Activity的按钮
	ImageView btnVoiceActivity;
	//手写模式的EditText
	EditText editTextWriteSearchContent;

	//手写模式的搜索记录
	WriteSearchRecordAdapter adapterSearchRecordWrite;
	ArrayList<Map<String, String>> arrayListWriteSearchRecord;
	SearchRecordManager searchRecordManagerWrite;
	ListView listViewSearchRecordWrite;
	String strLastSerachRecord="";
	Handler mHandler=new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			if(msg.what==0x233)
			{
				//响应翻译线程 返回的翻译结果
				String[] arrTranslateResult=(String[]) msg.obj;
				addSearchRecord(arrTranslateResult[0], arrTranslateResult[1]);
			}
		};
	};


	//底部导航栏
	LinearLayout linearLayoutNavBarInputMethodVoice;
	LinearLayout linearLayoutNavBarInputMethodWrite;
	//底部导航栏的文字
	TextView textViewNavBarVoice;
	TextView textViewNavBarWrite;
	//中间的LinarLayout
	LinearLayout linearLayoutVoice;
	LinearLayout linearLayoutWrite;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		LinearLayout linearLayout=(LinearLayout) inflater.inflate(R.layout.fragment_query, container,false);


		//设置语音界面响应
		btnVoiceActivity=(ImageView) linearLayout.findViewById(R.id.fragment_query_voice_activity);
		btnVoiceActivity.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				//更新手写模式的搜索记录到MainActivity中
				updateMainActivitySearchRecord();
				Intent intent=new Intent(getActivity(),VoiceActivity.class);
				startActivityForResult(intent, 1);
			}
		});

		//手写模式
		editTextWriteSearchContent=(EditText) linearLayout.findViewById(R.id.fragment_query_edit_text_write);
		//回车提交
		editTextWriteSearchContent.setOnKeyListener(new OnKeyListener()
		{

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				if(keyCode==KeyEvent.KEYCODE_ENTER)
				{
					String content=editTextWriteSearchContent.getText().toString();
					content=content.trim();
					if(content!="" && content!=strLastSerachRecord)
					{
						strLastSerachRecord=content;
						Translate.getInstance().translate(content, FragmentQuery.this);
					}
				}
				return false;
			}
		});
		//手写模式的所需要的变量的初始化
		adapterSearchRecordWrite=new WriteSearchRecordAdapter();
		arrayListWriteSearchRecord=new ArrayList<Map<String,String>>();
		searchRecordManagerWrite=new SearchRecordManager(SearchRecordManager.NSORTMETHODNORSORT);
		listViewSearchRecordWrite=(ListView) linearLayout.findViewById(R.id.fragment_query_list_View_search_record);
		listViewSearchRecordWrite.setAdapter(adapterSearchRecordWrite);


		//导航栏初始化
		linearLayoutNavBarInputMethodVoice=(LinearLayout) linearLayout.findViewById(R.id.fragment_query_input_method_voice);
		linearLayoutNavBarInputMethodWrite=(LinearLayout) linearLayout.findViewById(R.id.fragment_query_input_method_write);
		linearLayoutNavBarInputMethodVoice.setOnClickListener(this);
		linearLayoutNavBarInputMethodWrite.setOnClickListener(this);

		linearLayoutVoice=(LinearLayout) linearLayout.findViewById(R.id.fragment_query_voice_linearlayout);
		linearLayoutWrite=(LinearLayout) linearLayout.findViewById(R.id.fragment_query_write_linearlayout);
		//导航栏文字
		textViewNavBarVoice=(TextView) linearLayout.findViewById(R.id.fragment_query_input_method_voice_text_view);
		textViewNavBarWrite=(TextView) linearLayout.findViewById(R.id.fragment_query_input_method_write_text_view);

		return linearLayout;
	};
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		super.onActivityResult(requestCode, resultCode, intent);
		if(requestCode==1 && resultCode==1)
		{
			//接收语音收索的搜索记录
			Bundle bundle=intent.getExtras();
			ArrayList<SearchRecordItem> arrayListNewSearchRecord=(ArrayList<SearchRecordItem>) bundle.getSerializable("update_data");
			//回传数据到MainActivity
			((MainActivity)getActivity()).addSearchRecord(arrayListNewSearchRecord);
		}
	}
	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
			//切换到语音输入模式
			case R.id.fragment_query_input_method_voice:
				linearLayoutVoice.setVisibility(View.VISIBLE);
				linearLayoutWrite.setVisibility(View.GONE);
				textViewNavBarVoice.setTextAppearance(getActivity(), R.style.fragment_query_input_method_selected);
				textViewNavBarWrite.setTextAppearance(getActivity(), R.style.fragment_query_input_method_normal);
				break;
			//切换到手写输入模式
			case R.id.fragment_query_input_method_write:
				linearLayoutVoice.setVisibility(View.GONE);
				linearLayoutWrite.setVisibility(View.VISIBLE);
				textViewNavBarVoice.setTextAppearance(getActivity(), R.style.fragment_query_input_method_normal);
				textViewNavBarWrite.setTextAppearance(getActivity(), R.style.fragment_query_input_method_selected);
				break;
			default:
				break;
		}
	}
	/*
	 * (non-Javadoc)
	 * @see com.example.beidanci_ui.Translate.SetTranslateResult#setTranslateResult(java.lang.String, java.lang.String)
	 * 		设置翻译结果到UI
	 */
	@Override
	public void setTranslateResult(WordItem item)
	{
		Message msg=new Message();
		msg.what=0x233;
		msg.obj=new String[]{item.mContent.trim().toLowerCase(),item.mResult.trim().toLowerCase()};
		mHandler.sendMessage(msg);
	}

	//添加数据到 搜索记录中
	public void addSearchRecord(String strContent,String strResult)
	{
		int position=searchRecordManagerWrite.getPosition(strContent);
		if(position>-1)
		{
			SearchRecordItem item=searchRecordManagerWrite.getRecord(position);
			if(item==null)
			{
				return;
			}
			//增加搜索次数
			item.addCount();
			//如果搜索记录已经存在 则把焦点设置到那里 返回
			listViewSearchRecordWrite.setSelection(position);
			return ;
		}
		//保存数据到搜索记录中
		searchRecordManagerWrite.addRecord(strContent, strResult);
		//更新记录视图
		Map<String, String> map=new HashMap<String, String>();
		map.put("content", strContent);
		map.put("result", strResult);
		arrayListWriteSearchRecord.add(map);
		adapterSearchRecordWrite.notifyDataSetChanged();
		//移动焦点到最后一个
		listViewSearchRecordWrite.setSelection(arrayListWriteSearchRecord.size()-1);
	}
	/*
     * 更新手写模式的搜索记录到MainActivity中
     */
	public void updateMainActivitySearchRecord()
	{
		if(searchRecordManagerWrite.arrayListSearchRecords.size()>0)
		{
			//更新搜索记录记录
			((MainActivity)getActivity()).addSearchRecord(searchRecordManagerWrite.arrayListSearchRecords);
			searchRecordManagerWrite.removeAll();
		}
	}
	public class WriteSearchRecordAdapter extends BaseAdapter
	{

		@Override
		public int getCount()
		{
			return arrayListWriteSearchRecord.size();
		}

		@Override
		public Object getItem(int position)
		{
			return arrayListWriteSearchRecord.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ViewHolder viewHolder;
			if(convertView==null)
			{
				convertView=View.inflate(getActivity(), R.layout.fragment_query_write_search_record_item, null);
				viewHolder=new ViewHolder(convertView);
			}
			else
			{
				viewHolder=(ViewHolder) convertView.getTag();
			}
			Map<String, String> mapItem=(Map<String, String>) getItem(position);
			viewHolder.tvIndex.setText(""+(position+1));
			viewHolder.tvContent.setText(mapItem.get("content"));
			viewHolder.tvResult.setText(mapItem.get("result"));
			return convertView;
		}

	}

	class ViewHolder
	{
		public TextView tvIndex,tvContent,tvResult;
		public ViewHolder(View view)
		{
			this.tvIndex=(TextView) view.findViewById(R.id.fragment_query_write_search_record_item_index);
			this.tvContent=(TextView) view.findViewById(R.id.fragment_query_write_search_record_item_content);
			this.tvResult=(TextView) view.findViewById(R.id.fragment_query_write_search_record_item_result);
			view.setTag(this);
		}
	}
}

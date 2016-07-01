package View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import Bg.Local.SearchRecordDatabaseManager;
import Model.SearchRecordItem;
import View.FragmentBookWord.ViewHolder;
import android.R.integer;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admim.beidanci.MainActivity;
import com.example.admim.beidanci.R;

public class FragmentRecord extends Fragment implements OnClickListener
{
	//格式化时间
	public static SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MM月dd日");;
	//切换搜索记录的导航栏
	public LinearLayout linearLayoutTime,linearLayoutCount;
	//切换搜索记录的导航栏的文字
	public TextView textViewNavBarTime,textViewNavBarCount;
	//以时间为排序方式的 adapter
	public SlideMenuListViewAdapter timeAdapter;
	//以搜索次数为排序方式的 adapter
	public SlideMenuListViewAdapter countAdapter;
	//显示数据的ListView
	public SlideMenuListView listViewSearchRecord;
	//目前记录的排序方式
	public int nNowSortMethod;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		LinearLayout linearLayout=(LinearLayout) inflater.inflate(R.layout.fragment_record, container,false);
		listViewSearchRecord=(SlideMenuListView) linearLayout.findViewById(R.id.fragment_record_list_view);
		timeAdapter=getAdapter(SearchRecordDatabaseManager.NSORTMETHODTIME);
		countAdapter=getAdapter(SearchRecordDatabaseManager.NSORTMETHODCOUNT);

		//导航栏初始化
		linearLayoutTime=(LinearLayout) linearLayout.findViewById(R.id.fragment_record_nav_bar_time);
		linearLayoutCount=(LinearLayout) linearLayout.findViewById(R.id.fragment_record_nav_bar_count);
		linearLayoutTime.setOnClickListener(this);
		linearLayoutCount.setOnClickListener(this);
		textViewNavBarTime=(TextView) linearLayout.findViewById(R.id.fragment_record_nav_bar_time_text_view);
		textViewNavBarCount=(TextView) linearLayout.findViewById(R.id.fragment_record_nav_bar_count_text_view);

		listViewSearchRecord.setAdapter(timeAdapter);
		nNowSortMethod=SearchRecordDatabaseManager.NSORTMETHODTIME;
		return linearLayout;
	}

	public SlideMenuListViewAdapter getAdapter(int nSortMethod)
	{
		ArrayList<SearchRecordItem> arrayList=getSearchRecordWord(nSortMethod);
		return new SlideMenuListViewAdapter(arrayList);
	}
	public ArrayList<SearchRecordItem> getSearchRecordWord(int nSortMethod)
	{
		ArrayList<SearchRecordItem> arrayListSearchRecord;
		switch (nSortMethod)
		{
			case SearchRecordDatabaseManager.NSORTMETHODCOUNT:
				arrayListSearchRecord=MainActivity.countSearchRecordManager.arrayListSearchRecords;
				break;
			case SearchRecordDatabaseManager.NSORTMETHODTIME:
				arrayListSearchRecord=MainActivity.timeSearchRecordManager.arrayListSearchRecords;
				break;
			default:
				arrayListSearchRecord=new ArrayList<SearchRecordItem>();
				break;
		}
		return arrayListSearchRecord;
	}

	class SlideMenuListViewAdapter extends BaseAdapter
	{
		public ArrayList<SearchRecordItem> arrayListData;
		public SlideMenuListViewAdapter(ArrayList<SearchRecordItem> arrayList)
		{
			this.arrayListData=arrayList;
		}
		@Override
		public int getCount()
		{
			return arrayListData.size();
		}

		@Override
		public Object getItem(int position)
		{
			return arrayListData.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			final int loc=position;
			ViewHolder holder=null;
			View menuView=null;
			if(convertView==null)
			{
				convertView=View.inflate(getActivity(), R.layout.fragment_record_list_view_item_content, null);
				menuView=View.inflate(getActivity(), R.layout.fragment_record_list_view_item_menu, null);
				convertView=new SlideMenuItem(convertView, menuView);
				holder=new ViewHolder(convertView);
			}
			else
			{
				holder=(ViewHolder) convertView.getTag();
			}
			SearchRecordItem item=arrayListData.get(position);
			final String content=item.content;
			final String result=item.result;
			holder.tv_content.setText(item.content);
			holder.tv_result.setText(item.result);
			holder.tv_count.setText(""+item.getCount());
			holder.tv_time.setText(getStrTime(item.getTime()));
			holder.tv_index.setText(""+position);
			holder.tv_add.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View arg0)
				{
					//添加数据到单词本中
					((MainActivity)getActivity()).addWord(content, result);
					Toast.makeText(getActivity(), "添加数据到单词本中 position: "+loc, Toast.LENGTH_SHORT).show();
				}
			});
			holder.tv_del.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View view)
				{
					//Log.v("a1", "content "+content);
					//删除记录
					removeSearchReocrd(timeAdapter.arrayListData, content);
					removeSearchReocrd(countAdapter.arrayListData, content);
					timeAdapter.notifyDataSetChanged();
					countAdapter.notifyDataSetChanged();
					//Log.v("a1","arrayList time: "+timeAdapter.arrayListData);
					//Log.v("a1","arrayList count: "+countAdapter.arrayListData);
					((MainActivity)getActivity()).removeSearchRecord(content);
				}
			});
			return convertView;
		}

	}

	class ViewHolder
	{
		TextView tv_index;
		TextView tv_content;
		TextView tv_result;
		TextView tv_count;
		TextView tv_time;
		TextView tv_add;
		TextView tv_del;
		public ViewHolder(View view)
		{
			tv_index=(TextView) view.findViewById(R.id.fragment_record_item_index);
			tv_content=(TextView)( view.findViewById(R.id.fragment_record_item_content));
			tv_result=(TextView)( view.findViewById(R.id.fragment_record_item_result));
			tv_count=(TextView)( view.findViewById(R.id.fragment_record_item_count));
			tv_time=(TextView)( view.findViewById(R.id.fragment_record_item_time));
			tv_add=(TextView)( view.findViewById(R.id.fragment_record_item_menu_add_word));
			tv_del=(TextView)( view.findViewById(R.id.fragment_record_item_menu_del));
			view.setTag(this);
		}
	}
	//获取时间字符串
	public String getStrTime(long time)
	{
		Calendar now=Calendar.getInstance();
		Calendar calendarToday,calendarYesterday,calendarQiantian;
		now.setTime(new Date(time));
		calendarToday=Calendar.getInstance();
		calendarToday.set(Calendar.HOUR_OF_DAY, 0);
		calendarToday.set(Calendar.MINUTE, 0);
		calendarToday.set(Calendar.MILLISECOND, 0);

		calendarYesterday=Calendar.getInstance();
		calendarYesterday.add(Calendar.DAY_OF_MONTH, -1);
		calendarYesterday.set(Calendar.HOUR_OF_DAY, 0);
		calendarYesterday.set(Calendar.MINUTE, 0);
		calendarYesterday.set(Calendar.MILLISECOND, 0);

		calendarQiantian=Calendar.getInstance();
		calendarQiantian.add(Calendar.DAY_OF_MONTH,-2);
		calendarQiantian.set(Calendar.HOUR_OF_DAY, 0);
		calendarQiantian.set(Calendar.MINUTE, 0);
		calendarQiantian.set(Calendar.MILLISECOND, 0);
		if(calendarToday.before(now))
		{
			return "今天";
		}
		else if(calendarYesterday.before(now))
		{
			return "昨天";
		}
		else if(calendarQiantian.before(now))
		{
			return "前天";
		}
		else
		{
			return simpleDateFormat.format(now.getTime());
		}
	}
	@Override
	public void onClick(View view)
	{
		int id=view.getId();
		switch (id)
		{
			case R.id.fragment_record_nav_bar_time:
				if (nNowSortMethod==SearchRecordDatabaseManager.NSORTMETHODTIME)
				{
					return;
				}
				textViewNavBarCount.setTextAppearance(getActivity(), R.style.fragment_query_input_method_normal);
				textViewNavBarTime.setTextAppearance(getActivity(), R.style.fragment_query_input_method_selected);
				listViewSearchRecord.setAdapter(timeAdapter);
				nNowSortMethod=SearchRecordDatabaseManager.NSORTMETHODTIME;
				break;
			case R.id.fragment_record_nav_bar_count:
				if(nNowSortMethod==SearchRecordDatabaseManager.NSORTMETHODCOUNT)
				{
					return;
				}
				textViewNavBarCount.setTextAppearance(getActivity(), R.style.fragment_query_input_method_selected);
				textViewNavBarTime.setTextAppearance(getActivity(), R.style.fragment_query_input_method_normal);
				listViewSearchRecord.setAdapter(countAdapter);
				nNowSortMethod=SearchRecordDatabaseManager.NSORTMETHODCOUNT;
				break;
			default:
				break;
		}
	}

	//删除搜索记录
	public void removeSearchReocrd(ArrayList<SearchRecordItem> arrayList,String content)
	{
		SearchRecordItem item;
		for(int i=0;i<arrayList.size();++i)
		{
			item=arrayList.get(i);
			if(item.content.equals(content))
			{
				arrayList.remove(i);
				i--;
			}
		}
	}

}

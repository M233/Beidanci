package View;

import java.util.ArrayList;
import java.util.Map;







import Model.WordGroupItem;
import Model.WordItem;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admim.beidanci.MainActivity;
import com.example.admim.beidanci.R;
import com.example.admim.beidanci.WordInfoActivity;


public class FragmentBookWord extends Fragment implements OnClickListener
{
	//底部导航栏
	TextView textViewNavBarTime,textViewNavBarNoSort,textViewNavBarGroup;
	//存放 时间排序和乱序排序的数据的地方
	LinearLayout linearLayoutTimeAndNoSort;
	//存放 分组排序的数据的地方
	FrameLayout frameLayoutGroup;
	//排序的图标
	ImageView imageViewSortMethod;

	//时间和乱序排序的listView
	SlideMenuListView listView;
	public SlideMenuListViewAdapter adapterTime;
	public SlideMenuListViewAdapter adapterNoSort;
	//组排序的ExpandableListView
	SlideMenuExpandableListView expandableListView;
	//组排序的adapter
	public GroupExpandableListAdapter adapterGroup;
	//组排序的adapter的Word数据
	public Map mapGroupWords;
	//添加到组的ID
	String strAddToGroupName="";
	//所有的单词组名
	String[] arrGroupName=null;


	//添加组
	ImageView imageViewAddGroup;
	//当前的组排序的位置 0表示时间  1表示组  2表示乱序 也就是排序位置
	public int nNowSortPosition=0;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	{
		LinearLayout linearLayout=(LinearLayout) inflater.inflate(R.layout.fragment_book_word, container,false);

		listView=(SlideMenuListView) linearLayout.findViewById(R.id.fragment_book_word_list_view);
		ArrayList<WordItem> arrayListWordTime=MainActivity.timeBookWordManager.arrayListWords;
		ArrayList<WordItem> arrayListWordNoSort=MainActivity.noSortBookWordManager.arrayListWords;
		adapterTime=new SlideMenuListViewAdapter(arrayListWordTime);
		adapterNoSort=new SlideMenuListViewAdapter(arrayListWordNoSort);
		listView.setAdapter(adapterTime);

		expandableListView=(SlideMenuExpandableListView) linearLayout.findViewById(R.id.fragment_book_word_expandable_list_view);
		mapGroupWords=MainActivity.groupBookWordManager.getAllGroupChild();
		adapterGroup=new GroupExpandableListAdapter(MainActivity.groupBookWordManager.arrayListWordGroups);
		expandableListView.setAdapter(adapterGroup);
		imageViewAddGroup=(ImageView) linearLayout.findViewById(R.id.fragment_book_word_tv_add_group);
		imageViewAddGroup.setOnClickListener(new OnClickListener()
		{
			//添加组
			@Override
			public void onClick(View arg0)
			{
				LinearLayout linearLayout=(LinearLayout) View.inflate(getActivity(),R.layout.fragment_book_word_add_group, null);
				final EditText editTextGroupName=(EditText) linearLayout.findViewById(R.id.fragment_book_word_add_group_name);
				new AlertDialog.Builder(getActivity())
						.setIcon(R.drawable.fragment_book_word_group_icon)
						.setTitle("添加组")
						.setView(linearLayout)
						.setPositiveButton("确定", new DialogInterface.OnClickListener()
						{

							@Override
							public void onClick(DialogInterface arg0, int arg1)
							{
								String groupName=editTextGroupName.getText().toString();
								Toast.makeText(getActivity(),"创建的组名为: "+groupName,Toast.LENGTH_SHORT).show();
								((MainActivity)getActivity()).addWordGroup(groupName);
							}
						})
						.setNegativeButton("取消", new DialogInterface.OnClickListener()
						{

							@Override
							public void onClick(DialogInterface arg0, int arg1)
							{

							}
						})
						.create()
						.show();
			}
		});

		//初始化底部导航栏
		textViewNavBarTime=(TextView) linearLayout.findViewById(R.id.fragment_book_word_sort_method_time);
		textViewNavBarNoSort=(TextView) linearLayout.findViewById(R.id.fragment_book_word_sort_method_no_sort);
		textViewNavBarGroup=(TextView) linearLayout.findViewById(R.id.fragment_book_word_sort_method_group);
		textViewNavBarTime.setOnClickListener(this);
		textViewNavBarNoSort.setOnClickListener(this);
		textViewNavBarGroup.setOnClickListener(this);

		//数据的存放处初始化
		linearLayoutTimeAndNoSort=(LinearLayout) linearLayout.findViewById(R.id.linear_layout_sort_method_time_and_no_sort);
		frameLayoutGroup=(FrameLayout) linearLayout.findViewById(R.id.frame_layout_sort_method_group);
		imageViewSortMethod=(ImageView) linearLayout.findViewById(R.id.sort_method_icon);
		return linearLayout;
	}

	public void changeWordSortMethod(int position)
	{
		if(position==nNowSortPosition)
		{
			return;
		}
		textViewNavBarGroup.setTextAppearance(getActivity(), R.style.fragment_book_word_bootom_nav_text_normal);
		textViewNavBarNoSort.setTextAppearance(getActivity(), R.style.fragment_book_word_bootom_nav_text_normal);
		textViewNavBarTime.setTextAppearance(getActivity(), R.style.fragment_book_word_bootom_nav_text_normal);
		nNowSortPosition=position;
		switch (position)
		{
			//切换到时间排序
			case 0:
				linearLayoutTimeAndNoSort.setVisibility(View.VISIBLE);
				imageViewSortMethod.setImageResource(R.drawable.fragment_book_word_time_icon);
				listView.setAdapter(adapterTime);
				frameLayoutGroup.setVisibility(View.GONE);
				textViewNavBarTime.setTextAppearance(getActivity(), R.style.fragment_book_word_bootom_nav_text_selected);
				break;
			//切换到组排序
			case 1:
				frameLayoutGroup.setVisibility(View.VISIBLE);
				linearLayoutTimeAndNoSort.setVisibility(View.GONE);
				textViewNavBarGroup.setTextAppearance(getActivity(), R.style.fragment_book_word_bootom_nav_text_selected);
				break;
			//切换到乱序排序
			case 2:
				linearLayoutTimeAndNoSort.setVisibility(View.VISIBLE);
				imageViewSortMethod.setImageResource(R.drawable.fragment_book_word_no_sort_icon);
				listView.setAdapter(adapterNoSort);
				frameLayoutGroup.setVisibility(View.GONE);
				textViewNavBarNoSort.setTextAppearance(getActivity(), R.style.fragment_book_word_bootom_nav_text_selected);
				break;
			default:
				break;
		}
	}


	public class SlideMenuListViewAdapter extends BaseAdapter
	{
		private ArrayList<WordItem> arrayListData;
		public SlideMenuListViewAdapter(ArrayList<WordItem> arrayList)
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
			ViewHolder holder=null;
			View menuView=null;
			if(convertView==null)
			{
				convertView=View.inflate(getActivity(), R.layout.fragment_book_word_list_view_item_content, null);
				menuView=View.inflate(getActivity(), R.layout.fragment_book_word_list_view_item_menu, null);
				convertView=new SlideMenuItem(convertView, menuView);
				holder=new ViewHolder(convertView);
			}
			else
			{
				holder=(ViewHolder) convertView.getTag();
			}
			WordItem item=arrayListData.get(position);
			final String strItemContent=item.mContent;
			holder.tv_content.setText(item.mContent);
			holder.tv_result.setText(item.mResult);
			holder.tv_index.setText(""+(position+1));
			holder.tv_del.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View arg0)
				{
					//删除单词
					((MainActivity)getActivity()).removeWord(strItemContent);
					Toast.makeText(getActivity(), "删除单词: "+strItemContent, Toast.LENGTH_SHORT).show();
				}
			});
			return convertView;
		}

	}
	class ViewHolder
	{
		TextView tv_content;
		TextView tv_result;
		TextView tv_del;
		TextView tv_index;
		public ViewHolder(View view)
		{
			tv_content=(TextView)( view.findViewById(R.id.fragment_book_word_list_view_item_content));
			tv_result=(TextView)( view.findViewById(R.id.fragment_book_word_list_view_item_result));
			tv_del=(TextView)( view.findViewById(R.id.fragment_book_word_list_view_item_menu_del));
			tv_index=(TextView) view.findViewById(R.id.fragment_book_word_list_view_item_index);
			view.setTag(this);
		}
	}

	public class GroupExpandableListAdapter extends BaseExpandableListAdapter
	{
		private ArrayList<WordGroupItem> arrayListWordGroupItems;
		public GroupExpandableListAdapter(ArrayList<WordGroupItem> arrayListWordGroup)
		{
			arrayListWordGroupItems=arrayListWordGroup;
		}
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition)
		{
			return true;
		}

		@Override
		public boolean hasStableIds()
		{
			return true;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
		{
			final int g=groupPosition;
			ViewHolerGroup viewHolder;
			View menuView;
			if(convertView==null)
			{
				convertView=View.inflate(getActivity(), R.layout.expandablelistview_group_item_content, null);
				menuView=View.inflate(getActivity(), R.layout.expandablelistview_group_item_menu, null);
				convertView=new SlideMenuItem(convertView, menuView);
				viewHolder=new ViewHolerGroup(convertView);
			}
			else
			{
				viewHolder=(ViewHolerGroup) convertView.getTag();
			}
			final String strGroupName=arrayListWordGroupItems.get(groupPosition).mName;
			viewHolder.tv_name.setText(strGroupName);
			viewHolder.tv_change.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					Toast.makeText(getActivity(),"open Position:"+g, Toast.LENGTH_SHORT).show();
					LinearLayout linearLayout=(LinearLayout) View.inflate(getActivity(), R.layout.fragment_book_word_change_group_name, null);
					final EditText etNewGroupName=(EditText) linearLayout.findViewById(R.id.fragment_book_word_change_group_name);
					//弹出对话框
					new AlertDialog.Builder(getActivity())
							.setIcon(R.drawable.fragment_book_word_group_icon)
							.setTitle("修改组名为")
							.setView(linearLayout)
							.setPositiveButton("确定", new DialogInterface.OnClickListener()
							{

								@Override
								public void onClick(DialogInterface dialog, int which)
								{
									String strNewGroupName=etNewGroupName.getText().toString();
									if(strGroupName.equals(strGroupName))
									{
										//新的名字和旧的名字一样
										return ;
									}
									if(!((MainActivity)getActivity()).judgeChangeWordGroupName(strGroupName,strNewGroupName))
									{
										Toast.makeText(getActivity(), "不能修改组名: "+strGroupName, Toast.LENGTH_SHORT).show();
										return;
									}
									//修改组名
									((MainActivity)getActivity()).changeWordGorupName(strGroupName, strNewGroupName);
								}
							})
							.setNegativeButton("取消", null)
							.create()
							.show();
				}
			});
			viewHolder.tv_del.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View arg0)
				{
					//删除单词分组
					((MainActivity)getActivity()).removeWordGroup(strGroupName);
				}
			});
			return convertView;
		}

		@Override
		public long getGroupId(int groupPosition)
		{
			return groupPosition;
		}

		@Override
		public int getGroupCount()
		{
			return arrayListWordGroupItems.size();
		}

		@Override
		public Object getGroup(int groupPosition)
		{
			return arrayListWordGroupItems.get(groupPosition);
		}

		@Override
		public int getChildrenCount(int groupPosition)
		{
			int groupId=arrayListWordGroupItems.get(groupPosition).mGroupId;
			ArrayList<WordItem> arrayList=(ArrayList<WordItem>) mapGroupWords.get(groupId);
			if(arrayList==null)
			{
				return 0;
			}
			else
			{
				return arrayList.size();
			}
		}

		@Override
		public View getChildView( int groupPosition, int childPosition, boolean isLastChild, View convertView,
								  ViewGroup parent)
		{
			View menuView;
			ViewHolderChild viewHolder;
			final int g=groupPosition;
			final int c=childPosition;
			if(convertView==null)
			{
				convertView=View.inflate(getActivity(),R.layout.expandablelistview_child_item_content, null);
				menuView=View.inflate(getActivity(), R.layout.expandablelistview_child_item_menu, null);
				convertView=new SlideMenuItem(convertView, menuView);
				viewHolder=new ViewHolderChild(convertView);
			}
			else
			{
				viewHolder=(ViewHolderChild) convertView.getTag();
			}
			final WordItem item=(WordItem) getChild(groupPosition, childPosition);
			final String strItemContent=item.mContent;
			viewHolder.tv_content.setText(item.mContent);
			viewHolder.tv_content.setPadding(20, 0, 0, 0);

			viewHolder.ll_wrap.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Log.v("a3", "sem click" + item.mContent);
					getActivity().startActivity(WordInfoActivity.getStartIntent(getActivity(),item.mContent));
				}
			});


			viewHolder.tv_result.setText(item.mResult);
			viewHolder.tv_index.setText(""+(childPosition+1));
			viewHolder.tv_add.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View arg0)
				{
					//移动组
					arrGroupName=((MainActivity)getActivity()).groupBookWordManager.getGroupNames();
					strAddToGroupName=arrGroupName[0];
					new AlertDialog.Builder(getActivity())
							.setTitle("移动到组")
							.setIcon(R.drawable.fragment_book_word_group_icon)
							.setSingleChoiceItems(arrGroupName, 0, new DialogInterface.OnClickListener()
							{

								@Override
								public void onClick(DialogInterface arg0, int position)
								{
									strAddToGroupName=arrGroupName[position];
								}
							})
							.setPositiveButton("确定", new DialogInterface.OnClickListener()
							{

								@Override
								public void onClick(DialogInterface arg0, int arg1)
								{
									((MainActivity)getActivity()).moveToGroup(strAddToGroupName, strItemContent);
									Toast.makeText(getActivity(),strItemContent+" 添加到组 : "+strAddToGroupName,Toast.LENGTH_SHORT).show();
								}
							})
							.setNegativeButton("取消",new DialogInterface.OnClickListener()
							{

								@Override
								public void onClick(DialogInterface arg0, int arg1)
								{

								}
							})
							.create()
							.show();

				}
			});
			viewHolder.tv_del.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View view)
				{
					((MainActivity)getActivity()).removeWord(strItemContent);
					//删除单词
					Toast.makeText(getActivity(), "position: group "+g+"  child:"+c, Toast.LENGTH_SHORT).show();
				}
			});
			return convertView;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition)
		{
			return childPosition;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition)
		{
			int groupId=arrayListWordGroupItems.get(groupPosition).mGroupId;
			ArrayList<WordItem> arrayList=(ArrayList<WordItem>) mapGroupWords.get(groupId);
			if(arrayList==null)
			{
				return new WordItem(-1, "未知错误", "未知错误2");
			}
			else
			{
				return arrayList.get(childPosition);
			}
		}
	}
	class ViewHolerGroup
	{
		TextView tv_name;
		ImageView iv_icon;
		TextView tv_change;
		TextView tv_del;
		public ViewHolerGroup(View view)
		{
			tv_name=(TextView) view.findViewById(R.id.fragment_book_word_expandable_list_view_item_content_group_item_name);
			iv_icon=(ImageView) view.findViewById(R.id.fragment_book_word_expandable_list_view_item_content_group_item_icon);
			tv_change=(TextView) view.findViewById(R.id.fragment_book_word_expandable_list_view_item_menu_group_item_change);
			tv_del=(TextView) view.findViewById(R.id.fragment_book_word_expandable_list_view_item_menu_group_item_del);
			view.setTag(this);
		}

	}
	class ViewHolderChild
	{
		TextView tv_content;
		TextView tv_result;
		TextView tv_index;
		TextView tv_del;
		TextView tv_add;
		LinearLayout ll_wrap;
		public ViewHolderChild(View view)
		{
			tv_index=(TextView) view.findViewById(R.id.fragment_book_word_expandable_list_view_item_content_child_item_index);
			tv_content=(TextView) view.findViewById(R.id.fragment_book_word_expandable_list_view_item_content_child_item_content);
			tv_result=(TextView) view.findViewById(R.id.fragment_book_word_expandable_list_view_item_content_child_item_result);
			tv_del=(TextView) view.findViewById(R.id.fragment_book_word_expandable_list_view_item_menu_child_item_del);
			tv_add=(TextView) view.findViewById(R.id.fragment_book_word_expandable_list_view_item_menu_child_item_add);

			ll_wrap= (LinearLayout) view.findViewById(R.id.fragment_book_word_expandable_list_view_item_content_child_item_wrap);
			view.setTag(this);
		}
	}
	@Override
	public void onClick(View view)
	{
		switch(view.getId())
		{
			case R.id.fragment_book_word_sort_method_time:
				changeWordSortMethod(0);
				break;
			case R.id.fragment_book_word_sort_method_group:
				changeWordSortMethod(1);
				break;
			case R.id.fragment_book_word_sort_method_no_sort:
				changeWordSortMethod(2);
				break;
		}
	}

}

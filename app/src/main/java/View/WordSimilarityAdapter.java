package View;

import java.util.List;


import Model.WordItem;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.admim.beidanci.R;
import com.example.admim.beidanci.WordInfoActivity;

public class WordSimilarityAdapter extends BaseAdapter
{
	//数据
	private List<WordItem> mDatas;
	private Context mContext;
	public WordSimilarityAdapter(Context context,List<WordItem> datas)
	{
		this.mContext=context;
		this.mDatas=datas;
	}
	@Override
	public int getCount()
	{
		return mDatas.size();
	}

	@Override
	public Object getItem(int position)
	{
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		TextView tv=createTextView();
		final WordItem item=mDatas.get(position);
		tv.setText(item.mContent+"  "+item.mResult);
		tv.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				Log.v("a3","jump word "+item.mContent);
				Intent intent= WordInfoActivity.getStartIntent(mContext, item.mContent);
				//跳转到具体的单词界面
				mContext.startActivity(intent);
			}
		});
		return tv;
	}

	public TextView createTextView()
	{
		TextView tv=(TextView) LayoutInflater.from(mContext).inflate(R.layout.word_similarity_list_view_item, null);
		return tv;
	}

}

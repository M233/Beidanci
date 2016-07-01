package View;

import java.util.List;


import Model.WordInstance;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.admim.beidanci.R;

public class WordInstanceAdapter extends BaseAdapter
{
	public List<WordInstance> mListDatas;
	public Context mContext;

	public WordInstanceAdapter(List<WordInstance> datas, Context context)
	{
		this.mListDatas = datas;
		this.mContext = context;
	}

	@Override
	public int getCount()
	{
		return mListDatas.size();
	}

	@Override
	public Object getItem(int position)
	{
		return mListDatas.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		WordInstance wi = (WordInstance) getItem(position);

		String str = wi.en + "\n" + wi.cn;
		return getTextView(str);
	}

	private View getTextView(String instance)
	{
		TextView view = (TextView) LayoutInflater.from(mContext).inflate(R.layout.word_instance_list_view_item, null);
		//TextView v = (TextView) view.findViewById(R.id.word_instance_list_view_item_tv);
		view.setText(instance);
		return view;
	}
}

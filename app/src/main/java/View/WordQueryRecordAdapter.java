package View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.admim.beidanci.R;
import com.example.admim.beidanci.WordInfoActivity;

import java.util.List;

import Model.SearchRecordItem;

/**
 * Created by admim on 2016/6/19.
 */
public class WordQueryRecordAdapter extends BaseAdapter
{
    public List<SearchRecordItem>  mDatas;
    private Context mContext;
    public WordQueryRecordAdapter( Context context,List<SearchRecordItem> datas)
    {
        this.mDatas=datas;
        this.mContext=context;
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
        ViewHolder holder;
        if(convertView==null)
        {
            convertView= LayoutInflater.from(mContext).inflate(R.layout.write_query_record_item,parent,false);
            holder=new ViewHolder(convertView);
        }
        else
        {
            holder= (ViewHolder) convertView.getTag();
        }
        final SearchRecordItem item=mDatas.get(position);
        holder.mTvContent.setText(item.content);
        holder.mTvResult.setText(item.result);

        holder.mView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //启动WordInfoItem
                mContext.startActivity(WordInfoActivity.getStartIntent(mContext,item.content));
            }
        });
        return convertView;
    }

    private  static class  ViewHolder
    {
        View mView;
        //搜索内容
        TextView mTvContent;
        //搜索结果
        TextView mTvResult;
        public ViewHolder(View v)
        {
            this.mView=v;
            this.mTvContent= (TextView) v.findViewById(R.id.write_query_record_item_content);
            this.mTvResult= (TextView) v.findViewById(R.id.write_query_record_item_result);
            v.setTag(this);
        }
    }
}

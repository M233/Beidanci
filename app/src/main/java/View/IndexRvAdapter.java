package View;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admim.beidanci.R;
import com.example.admim.beidanci.VoiceActivity;
import com.example.admim.beidanci.WebActivity;
import com.example.admim.beidanci.WriteQueryActivity;

import java.util.ArrayList;
import java.util.List;

import Model.AdItem;
import Model.ArticleItem;

/**
 * Created by admim on 2016/6/17.
 */
public class IndexRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    //item 的类型
    public static final  int HEAD_TYPE=0;
    //文章列表
    public List<ArticleItem> mListArticles;
    //ViewPage的 Fragment
    private List<IndexRvHeaderViewPageFragment> mListAdFragment;
    //ViewPage的 Adapter
    private FragmentPagerAdapter mVpAdapter;
    //
    FragmentActivity mAcitivity;
    public IndexRvAdapter(FragmentActivity activity,List<AdItem> listAdItem,List<ArticleItem> listArticles)
    {
        mAcitivity=activity;
        mListAdFragment=new ArrayList<IndexRvHeaderViewPageFragment>();
        //文章
        mListArticles=listArticles;

        for(int i=0;i<listAdItem.size();++i)
        {
            //广告 Fragment
            mListAdFragment.add(new IndexRvHeaderViewPageFragment(listAdItem.get(i)));
        }
        //广告VP
        mVpAdapter=new FragmentPagerAdapter(mAcitivity.getSupportFragmentManager())
        {
            @Override
            public Fragment getItem(int position)
            {
                return mListAdFragment.get(position);
            }

            @Override
            public int getCount()
            {
                return mListAdFragment.size();
            }
        };

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if(viewType==HEAD_TYPE)
        {
            return createHeaderViewHolder(parent);
        }
        return createNormalViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if (holder instanceof HeaderViewHolder)
        {
            bindHeadViewHolder((HeaderViewHolder) holder);
        }
        else
        {
            bindNormalViewHolder((NormalViewHolder) holder,position-1);
        }


    }

    @Override
    public int getItemCount()
    {
        return mListArticles.size()+1;
    }

    @Override
    public int getItemViewType(int position)
    {
        if(position==0)
        {
            return 0;
        }
        return 1;
    }

    public void bindHeadViewHolder(HeaderViewHolder viewHolder)
    {
        //语音搜索
        viewHolder.mVoice.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //启动语音搜索
                mAcitivity.startActivityForResult(VoiceActivity.getStartIntent(mAcitivity), 1);
            }
        });
        //手动搜索
        viewHolder.mWrite.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(event.getAction()==MotionEvent.ACTION_DOWN)
                {
                    Intent intent= WriteQueryActivity.getStartIntent(mAcitivity);
                    mAcitivity.startActivityForResult(intent, 1);
                    return  true;
                }
                return false;
            }
        });
        //设置广告
        viewHolder.mViewPager.setAdapter(mVpAdapter);


    }

    public void bindNormalViewHolder(NormalViewHolder viewHolder,int position)
    {
        final ArticleItem item=mListArticles.get(position);
        viewHolder.mTitle.setText(item.mTitle);
        viewHolder.mTag.setText(item.mTag);

        //默认图片
        //viewHolder.mThumbnail.setImageResource(R.drawable.at7);
        viewHolder.mThumbnail.setImageResource(mListArticles.get(position).id);

        //跳转网页
        viewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mAcitivity.startActivity(WebActivity.getIntent(mAcitivity,item.mUrl));
            }
        });
    }

    public HeaderViewHolder createHeaderViewHolder(ViewGroup viewGroup)
    {
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.index_rv_item_header,viewGroup,false);
        return new HeaderViewHolder(v);
    }

    public NormalViewHolder createNormalViewHolder(ViewGroup viewGroup)
    {
        View v=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.index_rv_item_normal,viewGroup,false);
        return new NormalViewHolder(v);
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder
    {
        //广告
        public ViewPager mViewPager;
        //语音图标
        public ImageView mVoice;
        //搜索栏
        public EditText mWrite;
        public HeaderViewHolder(View itemView)
        {
            super(itemView);

            mViewPager= (ViewPager) itemView.findViewById(R.id.index_rv_item_header_vp);
            mVoice= (ImageView) itemView.findViewById(R.id.index_rv_item_header_toolbar_voice);
            mWrite= (EditText) itemView.findViewById(R.id.index_rv_item_header_toolbar_et);
        }
    }

    public static  class NormalViewHolder extends RecyclerView.ViewHolder
    {
        //缩略图
        public ImageView mThumbnail;
        //标题
        public TextView mTitle;
        //标签
        public TextView mTag;
        public NormalViewHolder(View itemView)
        {
            super(itemView);

            mThumbnail= (ImageView) itemView.findViewById(R.id.index_rv_item_thumbnail);
            mTitle= (TextView) itemView.findViewById(R.id.index_rv_item_title);
            mTag= (TextView) itemView.findViewById(R.id.index_rv_item_tag);
        }
    }

}

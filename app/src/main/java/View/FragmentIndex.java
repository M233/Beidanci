package View;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.admim.beidanci.MainActivity;
import com.example.admim.beidanci.R;
import com.example.admim.beidanci.VoiceActivity;
import com.example.admim.beidanci.WriteQueryActivity;

import java.util.ArrayList;
import java.util.List;

import Model.AdItem;
import Model.ArticleItem;
import Model.SearchRecordItem;

/**
 * Created by admim on 2016/6/19.
 */
public class FragmentIndex extends Fragment
{
    //顶部导航栏 的语音按钮
    private ImageView mIvTopToolBarVoice;
    //顶部导航栏 的手动输入框
    private EditText  mEtTopToolBarWrite;

    private RecyclerView mRecyclerView;
    //最顶的tool bar
    private RelativeLayout mRlTopToolBar;
    //最顶的tool bar 上划动画
    private Animator mAnimatorTopToolBar;
    //最小滑动距离
    private int mTouchSlop;
    //手指滑动方向
    private int mTouchdirection;
    //最顶的toolbar 是否显示
    private boolean mIsTopToolBarShow=false;
    //最顶toolbar是否可以显示
    private boolean mIsTopToolBarVisible=false;
    //RecyclerView的Adapter
    private IndexRvAdapter mRvAdapter;
    //RecyclerView的布局管理器
    private LinearLayoutManager mRvLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View v=inflater.inflate(R.layout.index_fragment,container,false);
        //广告
        List<AdItem> listAds=getTestAd();

        //文章
        List<ArticleItem> listArticle=getTestArticle();

        //RecyclerView 初始化
        mRecyclerView= (RecyclerView) v.findViewById(R.id.index_rv);
        mRvAdapter=new IndexRvAdapter((FragmentActivity)getActivity(),listAds,listArticle);
        mRecyclerView.setAdapter(mRvAdapter);
        mRvLayoutManager=new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mRvLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        //TopToolBar 初始化
        mRlTopToolBar= (RelativeLayout) v.findViewById(R.id.index_tool_bar);
        initSlideShowTopToolBar();

        //顶部导航栏的语音按钮
        mIvTopToolBarVoice= (ImageView) v.findViewById(R.id.index_tool_bar_voice);
        mIvTopToolBarVoice.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //启动语音搜索
                Intent intent= VoiceActivity.getStartIntent(getActivity());
                startActivityForResult(intent,1);
            }
        });

        //顶部导航栏的输入框
        mEtTopToolBarWrite= (EditText) v.findViewById(R.id.index_tool_bar_write);
        mEtTopToolBarWrite.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(event.getAction()==MotionEvent.ACTION_DOWN)
                {
                    //手动输入搜索
                    Intent intent= WriteQueryActivity.getStartIntent(getActivity());
                    startActivityForResult(intent,1);
                    return  true;
                }
                return false;
            }
        });

        return v;
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
            //Log.v("a3","result activity "+arrayListNewSearchRecord.size());
            //回传数据到MainActivity
            ((MainActivity)getActivity()).addSearchRecord(arrayListNewSearchRecord);
        }
    }

    /**
     *  初始化 上划显示 最低的toolbar
     */
    public void initSlideShowTopToolBar()
    {
        //获取最小滑动距离
        mTouchSlop= ViewConfiguration.get(getActivity()).getScaledTouchSlop();

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);

                //获取第一个可见 的item
                int pos=mRvLayoutManager.findFirstVisibleItemPosition();
                if(pos>0 )
                {
                    //显示
                    changeTopToolBarVisible(1);
                }
                else if(pos==0 )
                {
                    //隐藏
                    changeTopToolBarVisible(0);
                }

                if(dy>mTouchSlop)
                {
                    //show
                    mTouchdirection=1;
                }
                else if(dy<-mTouchSlop)
                {
                    //down
                    mTouchdirection=0;
                }
                if(mTouchdirection==1)
                {
                    if(mIsTopToolBarShow)
                    {
                        //hide
                        toolbarAnim(0);
                        mIsTopToolBarShow=false;
                    }
                }
                else
                {
                    if(!mIsTopToolBarShow)
                    {
                        //show
                        toolbarAnim(1);
                        mIsTopToolBarShow=true;
                    }
                }
            }
        });
    }

    /**
     * 带动画效果的显示或隐藏actionbar
     * @param flag  0 隐藏actionbar
     *              1 显示actionbar
     */
    private void toolbarAnim(int flag)
    {
        if(mAnimatorTopToolBar!=null && mAnimatorTopToolBar.isRunning())
        {
            mAnimatorTopToolBar.cancel();
        }
        if(flag==0)
        {
            mAnimatorTopToolBar= ObjectAnimator.ofFloat(mRlTopToolBar, "translationY", mRlTopToolBar.getTranslationY(), -mRlTopToolBar.getHeight());
        }
        else
        {
            mAnimatorTopToolBar= ObjectAnimator.ofFloat(mRlTopToolBar,"translationY",mRlTopToolBar.getTranslationY(),0);
        }

        mAnimatorTopToolBar.start();
    }


    /**
     *  修改TopToolBar的visible属性
     * @param flag  0  隐藏
    1  显示
     */
    private void changeTopToolBarVisible(int flag)
    {
        if(flag==0)
        {
            //隐藏
            mIsTopToolBarVisible=false;
            mRlTopToolBar.setVisibility(View.INVISIBLE);
        }
        else
        {
            if(mIsTopToolBarShow)
            {
                //显示
                mIsTopToolBarVisible=true;
                mRlTopToolBar.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     *  获取测试的 广告数据
     * @return
     */
    private List<AdItem> getTestAd()
    {
        List<AdItem> data= new ArrayList<AdItem>();
        data.add(new AdItem("ad 1 ", "2"));
        data.add(new AdItem("ad 2 ","1"));
        data.add(new AdItem("ad 3 ","0"));
        return  data;
    }

    /**
     *  获取测试的 文章数据
     * @return
     */
    private List<ArticleItem> getTestArticle()
    {
        List<ArticleItem> datas=new ArrayList<ArticleItem>();
        for(int i=0;i<3;++i)
        {
            datas.add(new ArticleItem("artilce "+i,"背的不是历史考点,分明是青春 ","词单 ","" ,R.drawable.at1));
            datas.add(new ArticleItem("artilce "+i,"考研>研霸暑假修理手册","考研 ","" ,R.drawable.at2));
            datas.add(new ArticleItem("artilce "+i,"最动人的悲伤要轻描淡写","来翻译吧 ","" ,R.drawable.at3));
            datas.add(new ArticleItem("artilce "+i,"冰与火之歌,如何将台词玩出花样","词单 ","" ,R.drawable.at4));
            datas.add(new ArticleItem("artilce "+i,"坚持一个月,看美剧不用字幕","推广 ","" ,R.drawable.at5));
            datas.add(new ArticleItem("artilce "+i,"你的英语水平能到几分","测试 ","" ,R.drawable.at6));
            datas.add(new ArticleItem("artilce "+i,"【和Emily一起练口语】no doubt "+i,"外媒精选 "+i,"",R.drawable.at7 ));

        }
        return  datas;
    }
}

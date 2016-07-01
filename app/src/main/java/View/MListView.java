package View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by admim on 2016/4/30.
 * 		修复ListView的wrap_content 只有一个item的高度
 */
public class MListView extends ListView
{
    public MListView(Context context)
    {
        this(context, null);
    }

    public MListView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public MListView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ListAdapter adapter=getAdapter();
        if(adapter==null)
        {
            return;
        }
        //修复wrap_content的错误
        int count=adapter.getCount();
        int height=getPaddingBottom()+getPaddingTop();
        int width=getMeasuredWidth();
        for(int i=0;i<count;++i)
        {
            View child= adapter.getView(i,null,this);
            //measure的第一个MeasureSpec的模式 一定要为 MeasureSpec.AT_MOST 否则TextView 多行的时候高度会出错
            child.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),0);
            height+=child.getMeasuredHeight();
        }
        //子项分割线
        height +=(count-1)*getDividerHeight();
        setMeasuredDimension(width, height);
    }
}

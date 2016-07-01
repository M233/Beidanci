package View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class SlideMenuItem extends LinearLayout
{
	//正在关闭
	public static final int STATE_CLOSE=0;
	//正在打开菜单
	public static final int STATE_OPEN=1;
	private View mContentView;
	public LinearLayout mMenuView;
	public int mDownX;
	public int state=STATE_CLOSE;
	public boolean isFling;
	private int mBaseX;
	private Scroller scroll;

	public SlideMenuItem(Context context)
	{
		super(context);
	}

	public SlideMenuItem(Context context,AttributeSet attrs)
	{
		super(context,attrs);
	}
	public SlideMenuItem(View contentView,View menuView)
	{
		super(contentView.getContext());
		scroll=new Scroller(getContext());
		mContentView=contentView;
		mMenuView=(LinearLayout) menuView;
		init();
	}

	public void init()
	{
		setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		mContentView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		mMenuView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		addView(mContentView);
		addView(mMenuView);
	}
	public void swipe(int dis)
	{
		if(dis>mMenuView.getWidth())
		{
			dis=mMenuView.getWidth();
		}
		if(dis<0)
		{
			dis=0;
		}
		mContentView.layout(-dis, mContentView.getTop(), mContentView.getWidth()-dis, getMeasuredHeight());
		mMenuView.layout(mContentView.getWidth()-dis, mMenuView.getTop(), mContentView.getWidth()+mMenuView.getWidth()-dis, mMenuView.getBottom());
	}
	@Override
	public void computeScroll()
	{
		if(state==STATE_OPEN)
		{
			if(scroll.computeScrollOffset())
			{
				swipe(scroll.getCurrX());
				postInvalidate();
			}
		}
		else
		{
			if(scroll.computeScrollOffset())
			{
				swipe(mBaseX-scroll.getCurrX());
				postInvalidate();
			}
		}
	}
	public void smoothCloseMenu()
	{
		state=STATE_CLOSE;
		mBaseX=-mContentView.getLeft();
		scroll.startScroll(0, 0, mBaseX, 0,350);
		postInvalidate();
	}
	public void smoothOpenMenu()
	{
		state=STATE_OPEN;
		scroll.startScroll(-mContentView.getLeft(), 0, mMenuView.getWidth(),0, 350);
		postInvalidate();
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mMenuView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));
		mContentView.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(),MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		mContentView.layout(0, 0, getMeasuredWidth(), mContentView.getMeasuredHeight());
		mMenuView.layout(getMeasuredWidth(), 0, getMeasuredWidth()+mMenuView.getMeasuredWidth(), mContentView.getMeasuredHeight());
	}
}

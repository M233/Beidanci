package View;

import android.R.integer;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.admim.beidanci.WordInfoActivity;

import Model.SearchRecordItem;
import Model.WordItem;

public class SlideMenuExpandableListView extends ExpandableListView
{
	private boolean moveable=false;
	private boolean closed=true;
	private float mDownX,mDownY;
	private SlideMenuItem mTouchView,oldView;
	private int mTouchGroupPosition,mTouchChildPosition;
	private int oldGroupPosition,oldChildPosition;
	public SlideMenuExpandableListView(Context context)
	{
		super(context);
		init();
	}
	public SlideMenuExpandableListView(Context context,AttributeSet attrs)
	{
		super(context,attrs);
		init();
	}
	public SlideMenuExpandableListView(Context context,AttributeSet attrs,int defStyle)
	{
		super(context,attrs,defStyle);
		init();
	}
	public void init()
	{
		mTouchView=oldView=null;
		oldChildPosition=oldGroupPosition=-2;
	}
	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		switch(ev.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				int nListViewPosition=pointToPosition((int)ev.getX(),(int) ev.getY());
				if(nListViewPosition==AdapterView.INVALID_POSITION)
				{
					break;
				}
				mTouchView=(SlideMenuItem) getChildAt(nListViewPosition-getFirstVisiblePosition());
				if(mTouchView==null)
				{
					break;
				}
				long nExpandableListViewPositon=this.getExpandableListPosition(nListViewPosition);
				//groupPosition
				mTouchGroupPosition=ExpandableListView.getPackedPositionGroup(nExpandableListViewPositon);
				//childPosition
				mTouchChildPosition=ExpandableListView.getPackedPositionChild(nExpandableListViewPositon);
				mDownX=ev.getX();
				mDownY=ev.getY();
				if((mTouchChildPosition==oldChildPosition && mTouchGroupPosition==oldGroupPosition)||closed)
				{
					//设置可以移动
					moveable=true;
					mTouchView.mDownX=(int)mDownX;
				}
				else
				{
					moveable=false;
					if(oldView!=null)
					{
						//旧的归位
						mTouchView.smoothCloseMenu();
					}
				}
				oldGroupPosition=mTouchGroupPosition;
				oldChildPosition=mTouchChildPosition;
				oldView=mTouchView;
				break;
			case MotionEvent.ACTION_MOVE:
				//判断是否是侧滑
				if(Math.abs(ev.getX()-mDownX)<Math.abs(ev.getY()-mDownY)*dp2px(2))
				{
					break;
				}
				if(moveable)
				{
					int dis=(int) (mTouchView.mDownX-ev.getX());
					if(mTouchView.state==mTouchView.STATE_OPEN)
					{
						dis +=mTouchView.mMenuView.getWidth();
					}
					mTouchView.swipe(dis);
					ev.setAction(MotionEvent.ACTION_CANCEL);
				}
				break;
			case MotionEvent.ACTION_UP:
				if(moveable)
				{
					//Log.v("a1", ""+(int)(mTouchView.mDownX-ev.getX()));
					if((mTouchView.mDownX-ev.getX())>(mTouchView.mMenuView.getWidth()/2))
					{
						mTouchView.smoothOpenMenu();
						closed=false;
					}
					else
					{
						mTouchView.smoothCloseMenu();
						closed=true;
					}
				}
				//判断为点击
				if(((Math.abs(mDownX-ev.getX()))<10) && ( Math.abs(mDownY-ev.getY())<10))
				{
					//Log.v("a3","touchPosition"+mTouchPosition);
					//Log.v("a3",""+this.getAdapter().getItem(mTouchPosition));
					try
					{
						/*Object item= this.getAdapter().getItem(mTouchPosition);
						String query="";
						if(item instanceof SearchRecordItem)
						{
							SearchRecordItem sri= (SearchRecordItem) item;
							query=sri.content;
						}
						else if(item instanceof WordItem)
						{
							WordItem wi= (WordItem) item;
							query=wi.mContent;
						}
						if(!query.trim().equals(""))
						{
							//启动WordInfoItem
							getContext().startActivity(WordInfoActivity.getStartIntent(getContext(), query));
						}*/
						//mTouchView.
					}
					catch (Exception e)
					{
						Log.v("a3", "" + e.toString());
					}
				}
				break;
		}
		return super.onTouchEvent(ev);
	}
	private int dp2px(int dp)
	{
		return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,getContext().getResources().getDisplayMetrics());
	}
}

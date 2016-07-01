package View;

import android.app.Notification.Action;
import android.content.Context;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.admim.beidanci.WordInfoActivity;

import Model.SearchRecordItem;
import Model.WordItem;

public class SlideMenuListView extends ListView
{
	//是否可以拖动
	private boolean moveable=false;
	//是否全部关闭
	private boolean closed=true;
	//按下的点的X坐标  按下的点的Y左边
	private float mDownX,mDownY;
	//当前按下点所对应的item的位置   上一次按下item所对应的位置
	private int mTouchPosition,oldPosition=-1;
	//当前按下的item  上一次按下所对应的item
	private SlideMenuItem mTouchView,oldView;


	public SlideMenuListView(Context context)
	{
		super(context);
	}
	public  SlideMenuListView(Context context,AttributeSet attrs)
	{
		super(context, attrs);
	}
	public SlideMenuListView(Context context,AttributeSet attrs,int defStyle)
	{
		super(context,attrs,defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		switch (ev.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				//获取点击的position
				mTouchPosition=pointToPosition((int)ev.getX(),(int) ev.getY());
				if(mTouchPosition==AdapterView.INVALID_POSITION)
				{
					break;
				}
				//获取点击的View
				mTouchView=(SlideMenuItem) getChildAt(mTouchPosition-getFirstVisiblePosition());
				mDownX=ev.getX();
				mDownY=ev.getY();
				if(mTouchView==null)
				{
					break;
				}
				if(oldPosition==mTouchPosition ||closed)
				{
					moveable=true;
					mTouchView.mDownX=(int) mDownX;
				}
				else
				{
					moveable=false;
					if(oldView!=null)
					{
						//旧的归位
						oldView.smoothCloseMenu();
					}
				}
				oldPosition=mTouchPosition;
				oldView=mTouchView;
				break;
			case MotionEvent.ACTION_MOVE:
				//判断是否侧滑
				if(Math.abs(mDownX-ev.getX())<Math.abs(mDownX-ev.getY())*dp2px(2))
				{
					break;
				}
				if(moveable)
				{
					int dis=(int)(mTouchView.mDownX-ev.getX());
					if(mTouchView.state==mTouchView.STATE_OPEN)
					{
						dis +=mTouchView.mMenuView.getWidth();
					}
					mTouchView.swipe(dis);
					ev.setAction(MotionEvent.ACTION_CANCEL);
				}
				break;
			case  MotionEvent.ACTION_UP:
				if(moveable)
				{
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
					ev.setAction(MotionEvent.ACTION_CANCEL);
				}
				//判断为点击
				if(((Math.abs(mDownX-ev.getX()))<10) && ( Math.abs(mDownY-ev.getY())<10))
				{
					//Log.v("a3","touchPosition"+mTouchPosition);
					//Log.v("a3",""+this.getAdapter().getItem(mTouchPosition));
					try
					{
						Object item= this.getAdapter().getItem(mTouchPosition);
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
							getContext().startActivity(WordInfoActivity.getStartIntent(getContext(),query));
						}
					}
					catch (Exception e)
					{
						Log.v("a3",""+e.toString());
					}
				}
			default:
				break;
		}
		return super.onTouchEvent(ev);
	}
	private int dp2px(int dp)
	{
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,getContext().getResources().getDisplayMetrics());
	}
}

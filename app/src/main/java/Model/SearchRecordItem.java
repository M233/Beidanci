package Model;

import java.io.Serializable;
import java.util.Date;

import android.R.integer;

/**
 *  搜索记录的子项
 * @author admim
 *
 */
public class SearchRecordItem implements Serializable
{
	public String content;
	public String result;
	private long  time;
	private int count;
	public SearchRecordItem(String content,String result)
	{
		this.content=content;
		this.result=result;
		this.time=new Date().getTime();
		this.count=1;
	}
	public SearchRecordItem(String content,String result,long time,int count)
	{
		this.content=content;
		this.result=result;
		this.count=count;
		this.time=time;
	}
	public long getTime()
	{
		return this.time;
	}
	/**
	 *  增加搜索次数
	 */
	public void addCount()
	{
		this.count++;
		this.time=new Date().getTime();
	}
	public void addCount(SearchRecordItem  item)
	{
		this.count +=item.getCount();
		this.time=item.getTime();
	}
	public int getCount()
	{
		return this.count;
	}
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof SearchRecordItem)
		{
			SearchRecordItem item=(SearchRecordItem) o;

			if (this.content.equals(item.content))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString()
	{
		return "content:"+this.content+" result:"+this.result+" time:"+this.time+" count:"+this.count;
	}
}

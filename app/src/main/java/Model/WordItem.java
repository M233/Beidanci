package Model;

import java.io.Serializable;
import java.util.Date;

public class WordItem implements Serializable
{
	private static final long serialVersionUID = 1L;
	//  mGroupId=-1 表示默认分组
	public int mGroupId;
	public String mContent;
	public String mResult;
	private long mTime;
	public WordItem(int groupId,String content,String result)
	{
		this.mGroupId=groupId;
		this.mContent=content;
		this.mResult=result;
		this.mTime=new Date().getTime();
	}
	public WordItem(int groupId,String content,String result,long time)
	{
		this.mGroupId=groupId;
		this.mContent=content;
		this.mResult=result;
		this.mTime=time;
	}

	public long getTime()
	{
		return this.mTime;
	}
	@Override
	public String toString()
	{
		return "groupId: "+mGroupId+" content: "+mContent+" result: "+mResult;
	}
}

package Model;

import java.io.Serializable;
import java.util.Date;


public class WordGroupItem implements Serializable
{
	public int mGroupId;
	public String mName;
	public long mTime;
	public WordGroupItem(String name)
	{
		this.mGroupId=-1;
		this.mName=name;
		this.mTime=new Date().getTime();
	}
	public WordGroupItem(int groupId,String name,long time)
	{
		this.mGroupId=groupId;
		this.mName=name;
		this.mTime=time;
	}
	@Override
	public String toString()
	{
		return "mGroupId: "+mGroupId+" mName:"+mName+" mTime:"+mTime; 
	}
}

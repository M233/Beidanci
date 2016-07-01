package Bg.Local;

import java.util.ArrayList;

import Model.SearchRecordItem;

/**
 * 	搜索记录集合
 * @author admim
 *
 */
public class SearchRecordManager
{
    public  ArrayList<SearchRecordItem> arrayListSearchRecords;
    //排序方式  1 表示使用次数来排  2表示查询时间来排 0没有顺序
    public static final int NSORTMETHODNORSORT=0;
    public static final int NSORTMETHODCOUNT=1;
    public static final int NSORTMETHODTIME=2;
    public  int nSortMethod;
    public SearchRecordManager(int nSortMethod)
    {
        arrayListSearchRecords=new ArrayList<SearchRecordItem>();
        this.nSortMethod=nSortMethod;
    }

    //增加一个记录
    public boolean addRecord(String content,String result)
    {
        int position=getPosition(content);
        if(position!=-1)
        {
            switch (nSortMethod)
            {
                case NSORTMETHODCOUNT:
                    SearchRecordItem item=arrayListSearchRecords.remove(position);
                    item.addCount();

                    break;

                default:
                    break;
            }
            return false;
        }
        SearchRecordItem item=new SearchRecordItem(content, result);
        switch (nSortMethod)
        {
            case NSORTMETHODCOUNT:
                int index=getCountInsertIndex(arrayListSearchRecords.size()-1, 1);
                arrayListSearchRecords.add(index, item);
                break;
            case NSORTMETHODTIME:
                arrayListSearchRecords.add(0,item);
            default:
                arrayListSearchRecords.add(item);
                break;
        }
        return true;
    }
    //增加一个记录
    public boolean addRecord(SearchRecordItem item)
    {
        int position=getPosition(item.content);
        int index=0;
        if(position==-1)
        {	//不存在的是时候 添加到记录中
            SearchRecordItem newItem=new SearchRecordItem(item.content,item.result,item.getTime(),item.getCount()) ;
            switch(nSortMethod)
            {
                case NSORTMETHODCOUNT:
                    index=getCountInsertIndex(arrayListSearchRecords.size()-1, item.getCount());
                    arrayListSearchRecords.add(index,newItem);
                    break;
                case NSORTMETHODTIME:
                    index=getTimeInsertIndex(arrayListSearchRecords.size()-1, item.getTime());
                    arrayListSearchRecords.add(index,newItem);
                    break;
                default:
                    arrayListSearchRecords.add(newItem);
                    break;
            }
            return true;
        }
        else
        {
            //已经有记录存在
            SearchRecordItem item2=null;
            switch (nSortMethod)
            {
                case NSORTMETHODCOUNT:
                    item2=arrayListSearchRecords.remove(position);
                    item2.addCount(item);
                    if(position>0)
                    {
                        position -=1;
                    }
                    index=getCountInsertIndex(position, item2.getCount());
                    arrayListSearchRecords.add(index, item2);
                    break;
                case NSORTMETHODTIME:
                    item2=arrayListSearchRecords.remove(position);
                    item2.addCount(item);
                    if(position>0)
                    {
                        position -=1;
                    }
                    index=getTimeInsertIndex(position, item.getTime());
                    arrayListSearchRecords.add(index,item2);
                    break;
                default:
                    arrayListSearchRecords.get(position).addCount(item);
                    break;
            }
        }
        return false;
    }
    //当按搜索次数从大小排时  获取插入的位置
    public int getCountInsertIndex(int startPosition,int count)
    {
        int size=arrayListSearchRecords.size();
        if(size==0)
        {
            return 0;
        }
        for(int i=startPosition;i>=0;i--)
        {
            if(count<arrayListSearchRecords.get(i).getCount())
            {
                return i+1;
            }
        }
        return 0;
    }
    //当按搜索时间从从大到小排时  获取插入的位置
    public int getTimeInsertIndex(int startPosition ,long time)
    {
        int size=arrayListSearchRecords.size();
        if(size==0)
        {
            return 0;
        }
        for(int i=startPosition;i>=0;i--)
        {
            if(time<arrayListSearchRecords.get(i).getTime())
            {
                return i+1;
            }
        }
        return 0;
    }
    //增加一群记录
    public void addRecords(ArrayList<SearchRecordItem> newRecord)
    {
        int size=newRecord.size();
        for(int i=0;i<size;++i)
        {
            SearchRecordItem item=newRecord.get(i);
            addRecord(item);
        }
    }
    //查找content所对应的记录在所有记录中的未知  -1表示没找到
    public int getPosition(String content)
    {
        int size=arrayListSearchRecords.size();
        for(int i=0;i<size;++i)
        {
            SearchRecordItem item=arrayListSearchRecords.get(i);
            if(item.content.equals(content))
            {
                return i;
            }
        }
        return -1;
    }
    //获取记录
    public SearchRecordItem getRecord(int position)
    {
        if(position<0 || position>arrayListSearchRecords.size()-1)
        {
            return null;
        }
        return arrayListSearchRecords.get(position);
    }
    //删除记录
    public boolean removeRecord(String content)
    {
        int position=getPosition(content);
        if(position==-1)
        {
            return false;
        }
        arrayListSearchRecords.remove(position);
        return true;
    }
    //删除所有记录
    public void removeAll()
    {
        arrayListSearchRecords.clear();
    }
}

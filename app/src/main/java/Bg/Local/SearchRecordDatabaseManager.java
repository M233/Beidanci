package Bg.Local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import Model.SearchRecordItem;

/**
 * Created by admim on 2016/6/19.
 */
public class SearchRecordDatabaseManager
{
    public DatabaseBeidanci dbOpenHelpe;
    public SimpleDateFormat simpleDateFormat;
    //每页的数据量
    public int nOnePageRecordNum=100;
    //获取数据的排序方式
    public static final int NSORTMETHODCOUNT=0;
    public static final int NSORTMETHODTIME=1;
    public static String[] ARRSORTMETHOD=new String[]{"search_count","last_operate_time"};
    public SearchRecordDatabaseManager(Context context)
    {
        dbOpenHelpe=new DatabaseBeidanci(context, 1);
        simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
    //想数据库增加一个记录
    private boolean addItem(SearchRecordItem item)
    {
        Long nTime=item.getTime()/1000;
        String time="datetime("+nTime+",'unixepoch','localtime')";
        String content=item.content;
        String result=item.result;
        String count=""+item.getCount();
        SQLiteDatabase db=dbOpenHelpe.getReadableDatabase();
        try
        {
            db.execSQL("insert into search_record values(null,?,?,"+time+",?)",new String[]{ content,result,count });
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }
    //更新记录		判断这个记录存不存在，假如存在则 修改记录的数据为这个，不存在，则添加记录
    public boolean updateItem(SearchRecordItem newItem)
    {
        String content=newItem.content;
        SearchRecordItem oldItem=getItem(content);
        if(oldItem!=null)
        {
            //记录存在
            if(!setItemCount(content, newItem.getCount()+oldItem.getCount(), newItem.getTime()))
            {
                return false;
            }
        }
        else
        {
            //记录不存在
            if(!addItem(newItem))
            {
                return false;
            }
        }
        return true;
    }
    public void updateItems(ArrayList<SearchRecordItem> arrayList)
    {
        int size=arrayList.size();
        for(int i=0;i<size;++i)
        {
            updateItem(arrayList.get(i));
        }
    }
    //设置记录的搜索次数
    public boolean setItemCount(String content,int count,long time)
    {
        SQLiteDatabase db=dbOpenHelpe.getReadableDatabase();
        ContentValues values=new ContentValues();
        values.put("search_count", count);
        //SQLite会把datetime解析成字符串
        //values.put("last_operate_time","datetime("+(time/1000)+",\"unixepoch\",\"localtime\")" );
        values.put("last_operate_time", simpleDateFormat.format(new Date(time)));
        int nAffectedRaw=db.update("search_record",values," content=?",new String[]{content});
        if(nAffectedRaw==0)
        {
            return false;
        }
        return true;
    }

    //获取一个搜索记录
    public SearchRecordItem getItem(String content)
    {
        SQLiteDatabase db=dbOpenHelpe.getReadableDatabase();
        Cursor cursor=db.rawQuery("select content,result,last_operate_time,search_count from search_record where content=? limit 1",
                new String[]{content} );
        if(cursor.getCount()!=1)
        {
            return null;
        }
        cursor.moveToNext();
        long time =0;
        try
        {
            time = simpleDateFormat.parse(cursor.getString(cursor.getColumnIndex("last_operate_time"))).getTime();
            return new SearchRecordItem(cursor.getString(cursor.getColumnIndex("content")), cursor.getString(cursor.getColumnIndex("result")), time, cursor.getInt(cursor.getColumnIndex("search_count")));
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    //删除搜索记录
    public boolean removeItem(String content)
    {
        SQLiteDatabase db=dbOpenHelpe.getReadableDatabase();
        try
        {
            int nAffectdeRow=db.delete("search_record", " content=?", new String[]{content});
            return nAffectdeRow>0?true:false;
        }
        catch(Exception e)
        {
            //Log.v("a1",e.toString());
            return false;
        }
    }

    /*
     * 	获取search_record表中的数据  排序方式 搜索次数
     * 		param: page 表示第几页 从1开始
     * 			   sortMethod  排序方式
     * 					0表示 搜索次数  srarch_count
     * 					1表示 时间	 last_operate_time
     * 		return：
     * 				null 表示报错
     * 				ArrayList  如果数据为空 则表示没数据
     *
     */
    public ArrayList<SearchRecordItem> getItems(int page,int sortMethod)
    {
        ArrayList<SearchRecordItem> arrayListResult=new ArrayList<SearchRecordItem>();
        int nPageNum=getSearchRecordPageNum();
        if(page<1)
        {
            page=1;
        }
        if(page>nPageNum)
        {
            return arrayListResult;
        }
        if(sortMethod<0 || sortMethod>ARRSORTMETHOD.length)
        {
            return null;
        }
        int nStartPosition=(page-1)*nOnePageRecordNum;
        String strSortMethod=ARRSORTMETHOD[sortMethod];
        SQLiteDatabase db=dbOpenHelpe.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from search_record order by "+strSortMethod+" desc limit "+nStartPosition+",100",null);
        String content="";
        String result="";
        String strTime="";
        Long nTime=0L;
        int count=0;
        try
        {
            while(cursor.moveToNext())
            {
                content=cursor.getString(cursor.getColumnIndex("content"));
                result=cursor.getString(cursor.getColumnIndex("result"));
                strTime=cursor.getString(cursor.getColumnIndex("last_operate_time"));
                nTime=simpleDateFormat.parse(strTime).getTime();
                count=cursor.getInt(cursor.getColumnIndex("search_count"));
                SearchRecordItem item=new SearchRecordItem(content, result, nTime, count);
                arrayListResult.add(item);
            }
        }
        catch (Exception e)
        {
            return null;
        }
        return arrayListResult;
    }
    //获取存储到search_record表的数据页数
    public int getSearchRecordPageNum()
    {
        int nSearchRecordNum=getSearchRecordNum();
        if(nSearchRecordNum==0)
        {
            return 0;
        }
        return (nSearchRecordNum/nOnePageRecordNum+1);
    }
    //获取存储到search_record的记录数
    public int getSearchRecordNum()
    {
        SQLiteDatabase db=dbOpenHelpe.getReadableDatabase();
        Cursor cursor=db.rawQuery("select count(_id) from search_record", null);
        if(cursor.getCount()==0)
        {
            return 0;
        }
        else
        {
            cursor.moveToNext();
            return cursor.getInt(cursor.getColumnIndex("count(_id)"));
        }
    }
}

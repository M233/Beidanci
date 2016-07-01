package Bg.Local;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import Model.WordGroupItem;
import Model.WordItem;

/**
 * Created by admim on 2016/6/19.
 */
public class BookWordDatabaseManager
{
    public DatabaseBeidanci dbOpenHelper;
    private SimpleDateFormat simpleDateFormat;
    public BookWordDatabaseManager(Context context)
    {
        dbOpenHelper=new DatabaseBeidanci(context, 1);
        simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
    /*
     * 添加一个单词 记录
     */
    private boolean addWrodItem(WordItem word)
    {
        SQLiteDatabase db=dbOpenHelper.getReadableDatabase();
        String content=word.mContent;
        String result=word.mResult;
        int groupId=word.mGroupId;
        long nTime=word.getTime()/1000;
        String strTime="datetime("+nTime+",'unixepoch','localtime')";
        try
        {
            db.execSQL("insert into word values(null,?,?,?,"+strTime+") ",new Object[]{groupId,content,result});
            return true;
        }
        catch(Exception e)
        {
            //////Log.v("a1","word database addItem error"+e.toString());
            return false;
        }
    }
    //更新一个单词
    public boolean updateWordItem(WordItem word)
    {
        String content=word.mContent;
        if(getWordItem(content)!=null)
        {
            return false;
        }
        return addWrodItem(word);
    }
    //更新一堆单词数据
    public void updateWordItems(ArrayList<WordItem> arrayList)
    {
        int size=arrayList.size();
        for(int i=0;i<size;++i)
        {
            updateWordItem(arrayList.get(i));
        }
    }
    /*
     * 	获取一个单词记录
     */
    public WordItem getWordItem(String content)
    {
        SQLiteDatabase db=dbOpenHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery("select group_id,result,last_operate_time from word where content=?", new String[]{content});
        if(cursor.getCount()!=1)
        {
            return null;
        }
        cursor.moveToNext();
        int groupId=cursor.getInt(cursor.getColumnIndex("group_id"));
        String result=cursor.getString(cursor.getColumnIndex("result"));
        String strTime=cursor.getString(cursor.getColumnIndex("last_operate_time"));
        try {
            long time=simpleDateFormat.parse(strTime).getTime();
            return new WordItem(groupId, content, result,time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    //获取所有单词
    public ArrayList<WordItem> getAllWordItems()
    {
        ArrayList<WordItem> arrayListWordItems=new ArrayList<WordItem>();
        SQLiteDatabase db=dbOpenHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery("select group_id,content,result,last_operate_time from word order by last_operate_time desc", new String[]{});
        int groupId=-1;
        String content="";
        String result="";
        String strTime="";
        long time=0;
        try
        {
            while(cursor.moveToNext())
            {
                groupId=cursor.getInt(cursor.getColumnIndex("group_id"));
                content=cursor.getString(cursor.getColumnIndex("content"));
                result=cursor.getString(cursor.getColumnIndex("result"));
                strTime=cursor.getString(cursor.getColumnIndex("last_operate_time"));
                time=simpleDateFormat.parse(strTime).getTime();
                arrayListWordItems.add(new WordItem(groupId, content, result, time));
            }
            return arrayListWordItems;
        }
        catch (Exception e)
        {
            //////Log.v("a1",e.toString());
            return null;
        }
    }
    //删除一个单词记录
    public boolean removeWordItem(WordItem word)
    {
        SQLiteDatabase db=dbOpenHelper.getReadableDatabase();
        try
        {
            int nAffectdeRow=db.delete("word", " content=?", new String[]{word.mContent});
            return nAffectdeRow>0?true:false;
        }
        catch(Exception e)
        {
            //Log.v("a1",e.toString());
            return false;
        }
    }
    //获取一个单词本
    public WordGroupItem getWordGroupItem(String  name)
    {
        SQLiteDatabase db=dbOpenHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery("select _id,last_operate_time from word_group where name=?",new String[]{name});
        if(cursor.getCount()!=1)
        {
            return null;
        }
        cursor.moveToNext();
        int groupId=cursor.getInt(cursor.getColumnIndex("_id"));
        String strTime=cursor.getString(cursor.getColumnIndex("last_operate_time"));
        try
        {
            long time=simpleDateFormat.parse(strTime).getTime();
            return new WordGroupItem(groupId, name, time);
        }
        catch(Exception e)
        {
            //////Log.v("a1",""+e.toString());
            return null;
        }
    }
    //获取所有单词本
    public ArrayList<WordGroupItem> getAllWordGroupItems()
    {
        ArrayList<WordGroupItem> arrayListWordGroups=new ArrayList<WordGroupItem>();
        SQLiteDatabase db=dbOpenHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery("select _id,name,last_operate_time from word_group ",new String[]{});
        try
        {
            int groupId=-1;
            String name="";
            String strTime="";
            long time=0;
            while(cursor.moveToNext())
            {
                groupId=cursor.getInt(cursor.getColumnIndex("_id"));
                name=cursor.getString(cursor.getColumnIndex("name"));
                strTime=cursor.getString(cursor.getColumnIndex("last_operate_time"));
                time=simpleDateFormat.parse(strTime).getTime();
                arrayListWordGroups.add(new WordGroupItem(groupId, name, time));
            }
            return arrayListWordGroups;
        }
        catch (Exception e)
        {

            ////Log.v("a1",e.toString());
            return null;
        }
    }

    //添加一个单词本
    private int addWordGroupItem(WordGroupItem wordGroup)
    {
        SQLiteDatabase db=dbOpenHelper.getReadableDatabase();
        String name=wordGroup.mName;
        long nTime=wordGroup.mTime/1000;
        String strTime="datetime("+nTime+",'unixepoch','localtime')";
        try
        {
            db.execSQL("insert into word_group values(null,?,"+strTime+") ",new Object[]{name});
            Cursor cursor=db.rawQuery("select _id from word_group where name=?", new String[]{name});
            if(cursor.getCount()!=1)
            {
                return -1;
            }
            cursor.moveToNext();
            int groupId=cursor.getInt(cursor.getColumnIndex("_id"));
            return groupId;
        }
        catch(Exception e)
        {
            ////Log.v("a1","word database addItem error"+e.toString());
            return -1;
        }
    }
    //更新一个组
    public int updateWordGroupItem(WordGroupItem wordGroup)
    {
        String name=wordGroup.mName;
        if(getWordGroupItem(name)!=null)
        {
            return -1;
        }
        int group_id=addWordGroupItem(wordGroup);
        return group_id;
    }
    //删除一个单词本
    public boolean removeWordGroupItem(WordGroupItem groupItem)
    {
        try
        {
            SQLiteDatabase db=dbOpenHelper.getReadableDatabase();
            int groupId=groupItem.mGroupId;
            //删除组对应的单词
            db.delete("word", "group_id=?", new String[]{""+groupId});
            //删除组
            int nDeleteCount=db.delete("word_group", "_id=?", new String[]{""+groupId});
            return nDeleteCount!=0?true:false;
        }
        catch(Exception e)
        {
            ////Log.v("a3",e.toString());
            return false;
        }
    }

    //把单词移动到指定的组
    public boolean moveToGroup(int groupId,String wordContent)
    {
        SQLiteDatabase db=dbOpenHelper.getReadableDatabase();
        //判断单词分组是否存在
        if(!judgeWordGroupIsExist(db, groupId))
        {
            return false;
        }
        ContentValues values=new ContentValues();
        values.put("group_id", groupId);
        int nAffectRows=db.update("word", values," content=? ",new String[]{wordContent});
        if(nAffectRows!=1)
        {
            return false;
        }
        return true;
    }

    //判断单词组是否存在
    private boolean judgeWordGroupIsExist(SQLiteDatabase db,int groupId)
    {
        if(-1==groupId)
        {
            //grouopId=-1 分组一定存在
            return true;
        }
        if(groupId<0)
        {
            return false;
        }
        Cursor cursor=db.rawQuery("select _id from word_group where _id=?",new String[]{""+groupId});
        if(cursor.getCount()!=1)
        {
            //groupId不存在
            return false;
        }
        return true;
    }
    // 修改单词组的名字
    public boolean changeGroupName(int groupId,String newGroupName)
    {
        if(groupId==-1)
        {
            //不能修改默认分组的名字
            return false;
        }
        SQLiteDatabase db=dbOpenHelper.getReadableDatabase();
        //判断要修改的单词组存不存在
        if(!judgeWordGroupIsExist(db, groupId))
        {
            //单词分组不存在
            return false;
        }
        //判断当前有没名=newGroupName的单词组
        WordGroupItem item=getWordGroupItem(newGroupName);
        if(item!=null)
        {
            //当前newGroupName的单词组已经存在
            return false;
        }
        String strTime="datetime("+(new Date().getTime()/1000)+",'unixepoch','localtime')";
        db.execSQL("update word_group set name='"+newGroupName+"',last_operate_time="+strTime+" where _id="+groupId);
        Cursor cursor=db.rawQuery("select name from word_group where _id=? ", new String[]{""+groupId});
        if (cursor.getCount()!=1)
        {
            return false;
        }
        cursor.moveToNext();
        if(!cursor.getString(cursor.getColumnIndex("name")).equals(newGroupName))
        {
            //修改失败
            return false;
        }
        //修改成功
        return true;
    }
    //关闭数据库连接
    public void close()
    {
        dbOpenHelper.close();
    }
}

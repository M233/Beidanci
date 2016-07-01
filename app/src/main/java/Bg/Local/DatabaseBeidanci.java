package Bg.Local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by admim on 2016/6/19.
 */
public class DatabaseBeidanci  extends SQLiteOpenHelper
{
    //搜索记录
    private final String strCreateSearchRecordTableSql="create table search_record " +
            "(" +
            " _id integer primary key autoincrement, " +
            " content varchar(200) not null, " +
            " result varchar(200) not null, " +
            " last_operate_time TIMESTAMP not null default(datetime('now','localtime')), " +
            " search_count int not null default(1) " +
            ")";
    //单词组
    private final String strCreateGroupWordTableSql="create table word_group " +
            " (" +
            "		_id integer primary key autoincrement , " +
            "		name varchar(200) not null ,  " +
            "		last_operate_time TIMESTAMP not null default(datetime('now','localtime'))  " +
            " )";
    //单词
    private final String strCreateWordTableSql="create table word " +
            " ( " +
            "	_id integer primary key autoincrement, " +
            "	group_id integer not null, " +
            " 	content varchar(200), " +
            "	result varchar(200), " +
            "	last_operate_time timestamp not null default(datetime('now','localtime'))  " +
            " )";
    public DatabaseBeidanci(Context context, int version)
    {
        super(context,context.getFilesDir()+"/beidanci.db3", null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(strCreateSearchRecordTableSql);
        //Log.v("a1","创建数据库 成功: search_record");
        db.execSQL(strCreateGroupWordTableSql);
        //Log.v("a1","创建数据库 成功: word_group");
        db.execSQL(strCreateWordTableSql);
        //Log.v("a1","创建数据库成功: Word");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //Log.v("a1", "版本迭代  oldVersion: "+oldVersion+"\n newVersion: "+newVersion);
    }
}

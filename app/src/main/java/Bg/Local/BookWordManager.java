package Bg.Local;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import Model.WordGroupItem;
import Model.WordItem;

/**
 * Created by admim on 2016/6/19.
 */
public class BookWordManager
{
    //组列表
    public ArrayList<WordGroupItem> arrayListWordGroups;
    //单词列表
    public ArrayList<WordItem> arrayListWords;
    //排序方式
    public int nSort;
    //单词的排序方式   0 时间  1组    2乱序
    public static final int NSORTTIME=0;
    public static final int NSORTGROUP=1;
    public static final int NSORTNOSORT=2;

    public BookWordManager(int sort)
    {
        nSort=sort;
        arrayListWordGroups=new ArrayList<WordGroupItem>();
        arrayListWords=new ArrayList<WordItem>();
    }
    //增加一个单词
    public boolean addWord(String content,String result)
    {
        int position=getWordPosition(content);
        if(position!=-1)
        {
            return false;
        }
        //-1 表示不属于任何组
        WordItem item=new WordItem(-1, content, result);
        arrayListWords.add(0,item);
        //Log.v("a1",arrayListWords.toString());
        return true;
    }
    //增加一个单词
    public boolean addWord(WordItem item)
    {
        int position=getWordPosition(item.mContent);
        if(position!=-1)
        {
            return false;
        }
        arrayListWords.add(0,new WordItem(item.mGroupId,item.mContent,item.mContent,item.getTime()));
        return true;
    }
    //获取单词的记录
    public int getWordPosition(String content)
    {
        int size=arrayListWords.size();
        WordItem item=null;
        for(int i=0;i<size;++i)
        {
            item=arrayListWords.get(i);
            if(item.mContent.equals(content))
            {
                return i;
            }
        }
        return -1;
    }
    //删除一个单词
    public boolean removeWord(String content)
    {
        int position=getWordPosition(content);
        if(position==-1)
        {
            return false;
        }
        arrayListWords.remove(position);
        return true;
    }
    //添加一个单词组
    public boolean addWordGroup(WordGroupItem item)
    {
        int position=getWordGroupPosition(item.mName);
        if(position!=-1)
        {
            return false;
        }
        arrayListWordGroups.add(0,item);
        return true;
    }
    //获取组的位置
    public int getWordGroupPosition(String name)
    {
        int size=arrayListWordGroups.size();
        WordGroupItem item=null;
        for(int i=0;i<size;++i)
        {
            item=arrayListWordGroups.get(i);
            if(item.mName.equals(name))
            {
                return i;
            }
        }
        return -1;
    }
    //删除一个单词组
    public boolean removeWordGroup(WordGroupItem wordGroup)
    {
        int position=getWordGroupPosition(wordGroup.mName);
        if(position==-1)
        {
            return false;
        }
        int groupId=wordGroup.mGroupId;
        arrayListWordGroups.remove(position);
        WordItem word=null;
        for(int i=0;i<arrayListWords.size();i++)
        {
            word=arrayListWords.get(i);
            if(word.mGroupId==groupId)
            {
                arrayListWords.remove(i);
                --i;
            }
        }
        return true;
    }

    //判断是否能修改组名
    public boolean judgeChangeWordGroupName(String oldName,String newName)
    {
        int nOldGroupId=getGroupId(oldName);
        int nNewGroupId=getGroupId(newName);
        if(nOldGroupId<0 || nNewGroupId>0)
        {
            //nOldGroupId<0 表示要修改的组不存在 或者为默认分组
            //nNewGroupId>) 表示修改成newName的组已经存在
            return false;
        }
        return true;
    }

    //修改组的名字
    public boolean changeWordGroupName(String oldName,String newName)
    {
        if(!judgeChangeWordGroupName(oldName, newName))
        {
            return false;
        }
        WordGroupItem item=getWordGroupItem(oldName);
        item.mName=newName;
        return true;
    }
    //将单词数序打乱
    public void shuffle()
    {
        Collections.shuffle(arrayListWords);
    }
    //把所有单词按组分类  其实就是json groupId为key
    public Map getAllGroupChild()
    {
        Map map=new HashMap();
        int size=arrayListWordGroups.size();
        WordGroupItem item;
        for(int i=0;i<size;++i)
        {
            item=arrayListWordGroups.get(i);
            map.put(item.mGroupId,getGroupChild(item.mGroupId));
        }
        //Log.v("a1","map"+map.toString());
        return map;
    }
    //获取groupId对应的单词
    public ArrayList<WordItem> getGroupChild(int groupId)
    {
        ArrayList<WordItem> arrayList=new ArrayList<WordItem>();
        int size=arrayListWords.size();
        WordItem item;
        for(int i=0;i<size;++i)
        {
            item=arrayListWords.get(i);
            if(item.mGroupId==groupId)
            {
                arrayList.add(item);
            }
        }
        return arrayList;
    }
    public String[] getGroupNames()
    {
        String[] arrGroupName=new String[arrayListWordGroups.size()];
        WordGroupItem item;
        for(int i=0;i<arrayListWordGroups.size();++i)
        {
            item=arrayListWordGroups.get(i);
            arrGroupName[i]=item.mName;
        }
        return arrGroupName;
    }
    //获取组ID  groupid=-1的时候 表示默认分组  groupId=-2 表示没找到

    public int getGroupId(String groupName)
    {
        int size=arrayListWordGroups.size();
        WordGroupItem item=null;
        for(int i=0;i<size;++i)
        {
            item=arrayListWordGroups.get(i);
            if(item.mName.equals(groupName))
            {
                return item.mGroupId;
            }
        }
        return -2;
    }
    //获取单词组item
    public WordGroupItem getWordGroupItem(int groupId)
    {
        int size=arrayListWordGroups.size();
        WordGroupItem item=null;
        for(int i=0;i<size;++i)
        {
            item=arrayListWordGroups.get(i);
            if(item.mGroupId==groupId)
            {
                return item;
            }
        }
        return null;
    }

    //获取单词组item
    public WordGroupItem getWordGroupItem(String name)
    {
        int size=arrayListWordGroups.size();
        WordGroupItem item=null;
        for(int i=0;i<size;++i)
        {
            item=arrayListWordGroups.get(i);
            if(item.mName.equals(name))
            {
                return item;
            }
        }
        return null;
    }

    //获取单词item
    public WordItem getWordItem(String content)
    {
        int size=arrayListWords.size();
        WordItem item=null;
        for(int i=0;i<size;++i)
        {
            item=arrayListWords.get(i);
            if(item.mContent.equals(content))
            {
                return item;
            }
        }
        return null;
    }
}

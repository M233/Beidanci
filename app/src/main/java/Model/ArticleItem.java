package Model;

/**
 * Created by admim on 2016/6/17.
 */
public class ArticleItem
{
    //文章地址
    public String mUrl;
    //文章 标题
    public String mTitle;
    //文章 标签
    public String mTag;
    //文章 缩略图
    public String mThumbnail;

    public int id;

    public ArticleItem(String mUrl, String mTitle, String mTag, String mThumbnail)
    {
        this.mUrl = mUrl;
        this.mTitle = mTitle;
        this.mTag = mTag;
        this.mThumbnail = mThumbnail;
    }

    public ArticleItem(String mUrl, String mTitle, String mTag, String mThumbnail, int id)
    {
        this.mUrl = mUrl;
        this.mTitle = mTitle;
        this.mTag = mTag;
        this.mThumbnail = mThumbnail;
        this.id = id;
    }
}

package Bg.Translate;

public class Translate
{
    //百度翻译引擎
    public BaiduTranslateTool mBaiduTranslateTool;
    //自定义翻译引擎
    public MWordTranslateTool mMWordTranslateTool;

    private Translate()
    {
        this.mBaiduTranslateTool=new BaiduTranslateTool();
        this.mMWordTranslateTool=new MWordTranslateTool();

        //设置链
        mMWordTranslateTool.setNexttool(mBaiduTranslateTool);
    }

    //翻译  结果通过 SetTranslateResult回传
    public void translate(String strFanyi,SetTranslateResult setResult)
    {
        mMWordTranslateTool.translate(strFanyi.trim(), setResult);
    }

    //单例模式 返回单例
    public static Translate getInstance()
    {
        return TranslateHolder.mInstance;
    }
    private static class TranslateHolder
    {
        private  final static  Translate mInstance=new Translate();
    }

}

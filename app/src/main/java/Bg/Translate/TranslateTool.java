package Bg.Translate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//责任链模式
public abstract class TranslateTool
{
	//下一个的处理工具节点-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private TranslateTool mNexTool;
	/**
	 * 	翻译 英->中
	 * @param str		要翻译的东西
	 * @param setResult	设置结果
	 * @return
	 */
	public abstract  void translate(final String str,final SetTranslateResult setResult);

	//传递给下一个工具处理
	public void next(String str,SetTranslateResult setResult)
	{
		if(mNexTool!=null)
		{
			mNexTool.translate(str, setResult);
		}
	}
	//设置下一个处理工具
	public void setNexttool(TranslateTool next)
	{
		this.mNexTool=next;
	}

	/**
	 * 	判断有没中文
	 * @param str
	 * @return
	 */
	public boolean isHaveChinese(String str)
	{
		Pattern pattern=Pattern.compile("[\u4e00-\u9fa5]");
		Matcher matcher=pattern.matcher(str);
		return matcher.find();
	}
}

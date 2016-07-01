package Bg.Translate;

import java.util.Map;

/**
 * 	MWordTranslate 的翻译结果
 * @author admim
 *
 */

public class MWordTranslateResultByChinese
{
	//是否成功
	public Boolean success;
	//需要翻译的内容
	public String content;
	//翻译的结果
	public Map<String, String>  result;
	//附加详情
	public String detail;

	public MWordTranslateResultByChinese(Boolean success, String content,Map<String, String> result, String detail)
	{
		super();
		this.success = success;
		this.content = content;
		this.result = result;
		this.detail = detail;
	}

	public Boolean getSuccess()
	{
		return success;
	}

	public void setSuccess(Boolean success)
	{
		this.success = success;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public Map<String, String> getResult()
	{
		return result;
	}

	public String getResultToString()
	{
		String str="";
		for(String key : result.keySet())
		{
			str +=key.trim()+" "+result.get(key)+"\n";
		}
		return str.trim();
	}
	public void setResult(Map<String, String> result)
	{
		this.result = result;
	}

	public String getDetail()
	{
		return detail;
	}

	public void setDetail(String detail)
	{
		this.detail = detail;
	}
}

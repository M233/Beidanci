package Bg.Translate;

import java.util.List;


public class BaiduTranslateResult
{
	//从什么语言
	public String from;
	//到什么语言
	public String to;
	//翻译结果
	public List<Result> trans_result;

	//获取需要翻译的内容
	public String getContent()
	{
		Result result=trans_result.get(0);
		return result.getSrc();
	}
	//获取翻译的结果
	public String getResult()
	{
		Result result=trans_result.get(0);
		return result.getDst();
	}

	public String getFrom()
	{
		return from;
	}


	public void setFrom(String from)
	{
		this.from = from;
	}


	public String getTo()
	{
		return to;
	}


	public void setTo(String to)
	{
		this.to = to;
	}


	public List<Result> getTrans_result()
	{
		return trans_result;
	}


	public void setTrans_result(List<Result> trans_result)
	{
		this.trans_result = trans_result;
	}


	public BaiduTranslateResult(String from, String to,
								List<Result> trans_result)
	{
		this.from = from;
		this.to = to;
		this.trans_result = trans_result;
	}


	public static class Result
	{
		public String src;
		public String dst;
		public Result(String src, String dst)
		{
			this.src = src;
			this.dst = dst;
		}
		public String getSrc()
		{
			return src;
		}
		public void setSrc(String src)
		{
			this.src = src;
		}
		public String getDst()
		{
			return dst;
		}
		public void setDst(String dst)
		{
			this.dst = dst;
		}

	}
}

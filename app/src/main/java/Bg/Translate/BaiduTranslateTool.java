package Bg.Translate;

import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;

import Bg.Volley.VolleyInterface;
import Bg.Volley.VolleyRequest;
import Model.WordItem;
import Util.MD5Util;

public class BaiduTranslateTool extends TranslateTool
{

	//百度翻译API  AppId
	private final static String strBaiduAppid="20151204000007305";
	//百度翻译API 密匙
	private final static String strBaiduMishi="mlnPRxFACuXIw73tMjay";
	//百度翻译API 翻译语言 中文
	private final static String strLanguageChinese="zh";
	//百度翻译API 翻译语言 英语
	private final static String strLanguageEnglish="en";
	@Override
	public void translate(final String str,final SetTranslateResult setResult)
	{
		try
		{
			//创建url
			String url="";
			if(isHaveChinese(str))
			{
				url=createUrl(str, strBaiduAppid, strBaiduMishi, strLanguageChinese, strLanguageEnglish);
			}
			else
			{
				url=createUrl(str, strBaiduAppid, strBaiduMishi, strLanguageEnglish,strLanguageChinese);
			}
			VolleyRequest.requestGet(url, "", new VolleyInterface()
			{

				@Override
				public void onSuccess(String result)
				{
					Gson gson=new Gson();
					BaiduTranslateResult obj=gson.fromJson(result, BaiduTranslateResult.class);
					//回传结果
					setResult.setTranslateResult(new WordItem(-1,obj.getContent(),obj.getResult()));
				}

				@Override
				public void onError(VolleyError volleyError)
				{
					Log.v("a3","baidu translate error "+str+" "+volleyError);
					//责任链 交给下一个节点处理
					next(str, setResult);
				}
			});
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}

	//创建翻译的url
	private static String createUrl(String q,String appid,String mishi,String from,String to) throws UnsupportedEncodingException
	{
		String salt=getRandom(10);
		String sign=MD5Util.MD5(appid+q+salt+mishi).toLowerCase();
		String url="http://api.fanyi.baidu.com/api/trans/vip/translate?q="+URLEncoder.encode(q,"utf-8")+"&from="+from+"&to="+to+"&appid="+appid+"&salt="+salt+"&sign="+sign;
		return url;
	}

	//获取指定位置的随机数
	public static String getRandom(int strLength)
	{
		Random random=new Random();
		double pross=(1+random.nextDouble())*Math.pow(10,strLength);
		String fixLengthString=String.valueOf(pross);
		return fixLengthString.substring(2, strLength+2);

	}
}

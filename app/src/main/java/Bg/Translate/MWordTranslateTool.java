package Bg.Translate;

import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.net.URLEncoder;

import Bg.Volley.VolleyInterface;
import Bg.Volley.VolleyRequest;
import Model.WordItem;

public class MWordTranslateTool extends TranslateTool
{
	//主机地址
	private static String mHost="10.100.1.229:8080";
	//路径 通过中文查询
	private static String mPathByChinese="android/queryWordByChinese.php?q=";
	//路径 通过英文查询
	private static String mPathByEnglish="android/queryWordByEnglish.php?q=";
	@Override
	public void translate(final String str,final SetTranslateResult setResult)
	{
		String url="";
		Boolean isHaveChinese=false;
		if((isHaveChinese=isHaveChinese(str)))
		{
			//通过中文查询
			url="http://"+mHost+"/"+mPathByChinese+URLEncoder.encode(str);
		}
		else
		{
			//通过英文查询
			url="http://"+mHost+"/"+mPathByEnglish+URLEncoder.encode(str);
		}
		Log.v("a3"," MWord "+str+" chinese"+isHaveChinese+ " url "+url);
		final Boolean isHvaeChinese2=isHaveChinese;
		VolleyRequest.requestGet(url, "", new VolleyInterface()
		{

			@Override
			public void onSuccess(String result)
			{
				try
				{
					if(isHvaeChinese2)
					{
						//查询通过 中文
						MWordTranslateResultByChinese chineseResult=handleResultByChinese(result);
						if(!chineseResult.getSuccess())
						{
							throw new Exception();
						}
						Log.v("a3",chineseResult.toString());
						//设置结果
						setResult.setTranslateResult(new WordItem(-1, chineseResult.getContent(),chineseResult.getResultToString()));
					}
					else
					{
						//查询 通过英文
						MWordTranslateResultByEnglish englishResult=handleResultByEnglish(result);
						if(!englishResult.getSuccess())
						{
							throw new Exception();
						}
						Log.v("a3", englishResult.toString());
						//设置结果
						setResult.setTranslateResult(englishResult.toWordInfoItem());
					}

				} catch (Exception e)
				{
					onError(new VolleyError());
				}
			}
			@Override
			public void onError(VolleyError volleyError)
			{
				Log.v("a3","MWord translate error "+str+" "+volleyError);
				//责任链 交给下一个节点处理
				next(str, setResult);
			}
		});
	}

	//处理服务器 返回的查询 中文的结果
	public MWordTranslateResultByChinese handleResultByChinese(String result)
	{
		Gson gson=new Gson();
		MWordTranslateResultByChinese obj=gson.fromJson(result, MWordTranslateResultByChinese.class);
		return obj;
	}
	public MWordTranslateResultByEnglish handleResultByEnglish(String result)
	{
		Gson gson=new Gson();
		MWordTranslateResultByEnglish obj=gson.fromJson(result, MWordTranslateResultByEnglish.class);
		return obj;
	}
}

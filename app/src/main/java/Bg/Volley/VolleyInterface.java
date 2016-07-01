package Bg.Volley;


import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * 封装 Volley的成功 和失败调用的监听接口
 */
public abstract class VolleyInterface
{
	private Response.Listener<String> mSuccessListener;
	private Response.ErrorListener mErrorListener;

	public VolleyInterface()
	{
		mSuccessListener = new Response.Listener<String>()
		{
			@Override
			public void onResponse(String s)
			{
				onSuccess(s);
			}
		};
		mErrorListener = new Response.ErrorListener()
		{
			@Override
			public void onErrorResponse(VolleyError volleyError)
			{
				onError(volleyError);
			}
		};
	}

	public Response.Listener<String> getSuccessListener()
	{
		return mSuccessListener;
	};

	public Response.ErrorListener getErrorListener()
	{
		return mErrorListener;
	};

	public abstract void onSuccess(String result);

	public abstract void onError(VolleyError volleyError);
}

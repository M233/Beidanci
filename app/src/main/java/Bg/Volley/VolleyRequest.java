package Bg.Volley;

import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.admim.beidanci.MyApplication;

public class VolleyRequest
{
	/**
	 * Get方法
	 *
	 * @param url
	 * @param tag
	 * @param vi
	 */
	public static void requestGet(String url, String tag, VolleyInterface vi)
	{
		StringRequest request = new StringRequest(Request.Method.GET, url,
				vi.getSuccessListener(), vi.getErrorListener());
		request.setTag(tag);
		MyApplication.getVolleyQueue().add(request);
		MyApplication.getVolleyQueue().start();
	}

	/**
	 * Post方法
	 *
	 * @param url
	 * @param params
	 * @param tag
	 * @param vi
	 */
	public static void requestPost(String url,
								   final Map<String, String> params, String tag, VolleyInterface vi)
	{
		StringRequest request = new StringRequest(Request.Method.POST, url,
				vi.getSuccessListener(), vi.getErrorListener())
		{
			@Override
			protected Map<String, String> getParams() throws AuthFailureError
			{
				return params;
			}
		};
		request.setTag(tag);
		MyApplication.getVolleyQueue().add(request);
		MyApplication.getVolleyQueue().start();
	}
}

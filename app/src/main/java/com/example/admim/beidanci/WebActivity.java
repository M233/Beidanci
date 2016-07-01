package com.example.admim.beidanci;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

/**
 * Created by admim on 2016/6/17.
 */
public class WebActivity extends Activity
{
    public static final String URL="url";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        String url=bundle.getString(URL);

        //获取完链接地址

        TextView tv=new TextView(this);
        tv.setText(url);
        tv.setGravity(Gravity.CENTER);
        setContentView(tv);
    }


    /**
     *  获取 WebAcitivity
     * @param context
     * @return
     */
    public static  Intent getIntent(Context context,String url)
    {
        Intent intent=new Intent(context,WebActivity.class);
        intent.putExtra(URL,url);
        return intent;
    }

}

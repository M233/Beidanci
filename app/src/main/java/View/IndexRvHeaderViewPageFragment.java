package View;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.admim.beidanci.R;
import com.example.admim.beidanci.WebActivity;

import Model.AdItem;

/**
 * Created by admim on 2016/6/17.
 */
public class IndexRvHeaderViewPageFragment extends Fragment
{
    private AdItem mAdItem;
    public IndexRvHeaderViewPageFragment(AdItem item)
    {
        this.mAdItem=item;

    }
    private ImageView mIvAd;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mIvAd=(ImageView)  inflater.inflate(R.layout.index_rv_item_header_vp_item,null);
        mIvAd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //启动 WebActivity
                startActivity(WebActivity.getIntent(getContext(), mAdItem.mUrl));
            }
        });
        setImageView(mAdItem.mImg);
        return mIvAd;
    }
    public void setImageView(String str)
    {
        if(str.equals("0"))
        {
            mIvAd.setImageResource(R.drawable.ad0);
        }
        else if(str.equals("1"))
        {
            mIvAd.setImageResource(R.drawable.ad1);
        }
        else if(str.equals("2"))
        {
            mIvAd.setImageResource(R.drawable.ad2);
        }
    }

}

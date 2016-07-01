package View;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.admim.beidanci.R;

public class FragmentMy extends Fragment
{

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		LinearLayout linearLayout=(LinearLayout) inflater.inflate(R.layout.fragment_my, container,false);

		return linearLayout;
	}
}

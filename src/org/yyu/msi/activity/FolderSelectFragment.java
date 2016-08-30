package org.yyu.msi.activity;

import org.yyu.msi.R;
import org.yyu.msi.entity.DiskInfor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yyu.msi.slide.SlidingMenu;


public class FolderSelectFragment extends Fragment implements OnClickListener
{

	private LayoutInflater mInflater;
	protected static SlidingMenu mSlidingMenu;
	private DiskInfor diskinfor = null;
	public FolderSelectFragment(DiskInfor diskinfor)
	{
		this.diskinfor = diskinfor;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		
		View view = inflater.inflate(R.layout.folder_select_fragment, container,false);
		initView(view);
		return view;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		mInflater = LayoutInflater.from(getActivity());

	}

	private void initView(View view) 
	{
		((ImageButton) view.findViewById(R.id.ivTitleBtnLeft)).setOnClickListener(this);
		((ImageButton) view.findViewById(R.id.ivTitleBtnRigh))
				.setOnClickListener(this);
		
		TextView mTitleName = (TextView) view.findViewById(R.id.ivTitleName);

	}

	@Override
	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
		BaseFragmentActivity baseFragment = (BaseFragmentActivity) getActivity();
		mSlidingMenu = baseFragment.getSlidingMenu();
		switch (v.getId()) 
		{
		case R.id.ivTitleBtnLeft:
			mSlidingMenu.showMenu(true);
			break;
		case R.id.ivTitleBtnRigh:
			mSlidingMenu.showSecondaryMenu(true);
			break;

		default:
			break;
		}
	}


}

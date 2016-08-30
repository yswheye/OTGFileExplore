package org.yyu.msi.activity;

import org.yyu.msi.R;
import org.yyu.msi.entity.Global;
import org.yyu.msi.listener.FragmentKeyListener;
import org.yyu.msi.listener.IDiskScanListener;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.yyu.msi.receiver.MediaMountStateReceiver;
import com.yyu.msi.slide.SlidingActivityBase;
import com.yyu.msi.slide.SlidingActivityHelper;
import com.yyu.msi.slide.SlidingMenu;




@SuppressLint("Registered")
public class BaseFragmentActivity extends FragmentActivity implements SlidingActivityBase, IDiskScanListener{

	private SlidingActivityHelper mHelper;
	protected SlidingMenu mSlidingMenu;
	private TextView mTitleName;
	
	private FragmentKeyListener fragmentKeyListener = null;
	private MediaMountStateReceiver receiver = null;
	private LeftFragment mLeftFrag = null;
	private RightFragment mRightFrag = null;
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		mHelper = new SlidingActivityHelper(this);
		mHelper.onCreate(savedInstanceState);
		
		mLeftFrag = new LeftFragment();
		mRightFrag = new RightFragment();
		mLeftFrag.setIDiskScanListener(this);
		receiver = new MediaMountStateReceiver(mLeftFrag.getHandler());
		
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onResume()
	{
		
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	public void setFragmentKeyListener(FragmentKeyListener fragmentKeyListener)
	{
		this.fragmentKeyListener = fragmentKeyListener;
	}
	
	public MediaMountStateReceiver getReceiver()
	{
		return receiver;
	}
	
	public void initLeftMenu() 
	{  
		// TODO Auto-generated method stub
		setBehindContentView(R.layout.main_left_layout);
		
		FragmentTransaction mFragementTransaction = getSupportFragmentManager().beginTransaction();
		
		mFragementTransaction.replace(R.id.main_left_fragment, mLeftFrag);
		mFragementTransaction.commit();
		
	}
	public void initRightMenu()
	{   
		mSlidingMenu.setSecondaryMenu(R.layout.main_right_layout);
		FragmentTransaction mFragementTransaction = getSupportFragmentManager().beginTransaction();
		
		mFragementTransaction.replace(R.id.main_right_fragment, mRightFrag);
		mFragementTransaction.commit();
	}
	public void initView(Bundle savedInstanceState) 
	{
		initLeftMenu();
		initRightMenu();
	}

	public void initSlidingMenu() 
	{
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int mScreenWidth = dm.widthPixels;
		
		// customize the SlidingMenu
		mSlidingMenu = getSlidingMenu();
		mSlidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
		mSlidingMenu.setShadowWidth(mScreenWidth / 100);
		mSlidingMenu.setBehindOffset((mScreenWidth) / 2 + 50);
		mSlidingMenu.setFadeDegree(0.35f);
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		mSlidingMenu.setShadowDrawable(R.drawable.slidingmenu_shadow);
		mSlidingMenu.setSecondaryShadowDrawable(R.drawable.right_shadow);
		mSlidingMenu.setFadeEnabled(true);
		mSlidingMenu.setBehindScrollScale(0.333f);
		
	}
	
	public SlidingMenu getSlideMenu() 
	{
		return mSlidingMenu;
	}
	
	public TextView getTitleName() 
	{
		mTitleName = (TextView) findViewById(R.id.ivTitleName);
		//mTitleName.setText(LeftFragment.mTitleName);
		return mTitleName;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPostCreate(android.os.Bundle)
	 */
	@Override
	public void onPostCreate(Bundle savedInstanceState) 
	{
		super.onPostCreate(savedInstanceState);
		mHelper.onPostCreate(savedInstanceState);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#findViewById(int)
	 */
	@Override
	public View findViewById(int id) 
	{
		View v = super.findViewById(id);
		if (v != null)
			return v;
		return mHelper.findViewById(id);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) 
	{
		super.onSaveInstanceState(outState);
		mHelper.onSaveInstanceState(outState);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#setContentView(int)
	 */
	@Override
	public void setContentView(int id) 
	{
		setContentView(getLayoutInflater().inflate(id, null));
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#setContentView(android.view.View)
	 */
	@Override
	public void setContentView(View v) 
	{
		setContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#setContentView(android.view.View, android.view.ViewGroup.LayoutParams)
	 */
	@Override
	public void setContentView(View v, LayoutParams params) 
	{
		super.setContentView(v, params);
		mHelper.registerAboveContentView(v, params);
	}

	/* (non-Javadoc)
	 * @see com.slidingmenu.lib.app.SlidingActivityBase#setBehindContentView(int)
	 */
	public void setBehindContentView(int id) 
	{
		setBehindContentView(getLayoutInflater().inflate(id, null));
	}

	/* (non-Javadoc)
	 * @see com.slidingmenu.lib.app.SlidingActivityBase#setBehindContentView(android.view.View)
	 */
	public void setBehindContentView(View v) 
	{
		setBehindContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	/* (non-Javadoc)
	 * @see com.slidingmenu.lib.app.SlidingActivityBase#setBehindContentView(android.view.View, android.view.ViewGroup.LayoutParams)
	 */
	public void setBehindContentView(View v, LayoutParams params) 
	{
		mHelper.setBehindContentView(v, params);
	}

	/* (non-Javadoc)
	 * @see com.slidingmenu.lib.app.SlidingActivityBase#getSlidingMenu()
	 */
	public SlidingMenu getSlidingMenu()
	{
		return mHelper.getSlidingMenu();
	}

	/* (non-Javadoc)
	 * @see com.slidingmenu.lib.app.SlidingActivityBase#toggle()
	 */
	public void toggle()
	{
		mHelper.toggle();
	}

	/* (non-Javadoc)
	 * @see com.slidingmenu.lib.app.SlidingActivityBase#showAbove()
	 */
	public void showContent() 
	{
		mHelper.showContent();
	}

	/* (non-Javadoc)
	 * @see com.slidingmenu.lib.app.SlidingActivityBase#showBehind()
	 */
	public void showMenu() 
	{
		mHelper.showMenu();
	}

	/* (non-Javadoc)
	 * @see com.slidingmenu.lib.app.SlidingActivityBase#showSecondaryMenu()
	 */
	public void showSecondaryMenu()
	{
		mHelper.showSecondaryMenu();
	}

	/* (non-Javadoc)
	 * @see com.slidingmenu.lib.app.SlidingActivityBase#setSlidingActionBarEnabled(boolean)
	 */
	public void setSlidingActionBarEnabled(boolean b) 
	{
		mHelper.setSlidingActionBarEnabled(b);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyUp(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		boolean b = mHelper.onKeyUp(keyCode, event);
		if (b) return b;
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if(fragmentKeyListener != null)
			{
				fragmentKeyListener.onKeyBack();
				return true;//拦截按键事件
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	/**
	*callbacks
	*/
	@Override
	public void onScanFinish(FileInforFragment fileInforFragment)
	{
		// TODO Auto-generated method stub
		mRightFrag.setFileInforFragment(fileInforFragment);
	}

}
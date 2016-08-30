package org.yyu.msi.activity;

import org.yyu.msi.R;
import org.yyu.msi.cache.MyImageWorker;
import org.yyu.msi.entity.Global;
import org.yyu.msi.utils.MyFileUtil;
import org.yyu.msi.utils.MyUtil;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.yyu.msi.exception.MyExceptionHandler;


public class MainActivity extends BaseFragmentActivity
{
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_center_layout);
		
		super.initSlidingMenu();
		super.initView(savedInstanceState);
		
		Global.util = new MyUtil();
		Global.util.initUtil(getApplicationContext());
		
		Global.imageWorker = MyImageWorker.INSTANCE;
		Global.imageWorker.init(getApplicationContext());
		
		MyExceptionHandler excetionHandler = MyExceptionHandler.getInstance();
		excetionHandler.init(getApplicationContext());
		
		registerReceiver();
		
	}
	
	
	/** 
	 * @Description: TODO
	 * @param    
	 * @return void 
	 * @throws 
	 */
	private void registerReceiver()
	{
		// TODO Auto-generated method stub
		IntentFilter filter = new IntentFilter();  
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);  
        filter.addAction(Intent.ACTION_MEDIA_CHECKING);  
        filter.addAction(Intent.ACTION_MEDIA_EJECT);  
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addDataScheme("file");  
        this.registerReceiver(getReceiver(), filter); 
	}
	
	/** 
	 * @Description: TODO
	 * @param    
	 * @return void 
	 * @throws 
	 */
	private void unRegisterReceiver()
	{
		// TODO Auto-generated method stub
		this.unregisterReceiver(getReceiver());
	}
	
	@Override
	protected void onResume() 
	{
		
		Global.imageWorker.openImageWorker(0);
		// TODO Auto-generated method stub
		super.onResume();
		
	}

	@Override
	protected void onPause()
	{
		
		Global.imageWorker.closeImageWorker();
		// TODO Auto-generated method stub
		super.onPause();

	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		
		Global.imageWorker.closeImageWorker();
		unRegisterReceiver();
		super.onDestroy();
	}
}

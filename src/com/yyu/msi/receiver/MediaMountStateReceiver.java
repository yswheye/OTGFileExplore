/*  
* @Project: MobileFileExplorer 
* @User: Android 
* @Description: neldtv手机云相册第二版本
* @Author： yan.yu
* @Company：http://www.neldtv.org/
* @Date 2014-4-8 上午9:27:51 
* @Version V2.0   
*/ 

package com.yyu.msi.receiver; 

import org.yyu.msi.common.StorageState;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

/** 
 * @ClassName: Receiver 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-4-8 上午9:27:51  
 */
public class MediaMountStateReceiver extends BroadcastReceiver
{
	
	private Handler handler = null;

	
	public MediaMountStateReceiver(Handler handler)
	{
		this.handler = handler;
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onReceive(Context context, Intent intent)
	{
		
		// TODO Auto-generated method stub
		if(intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED))
		{
			//MyLog.e("ACTION_MEDIA_MOUNTED");
			handler.obtainMessage(StorageState.MEDIA_MOUNTED, intent.getDataString()).sendToTarget();
		}
		else if(intent.getAction().equals(Intent.ACTION_MEDIA_CHECKING))
		{
			//MyLog.e("ACTION_MEDIA_CHECKING");
			handler.obtainMessage(StorageState.MEDIA_CHECKING, intent.getDataString()).sendToTarget();
		}
		else if(intent.getAction().equals(Intent.ACTION_MEDIA_EJECT))
		{
			//MyLog.e("ACTION_MEDIA_EJECT");
			handler.obtainMessage(StorageState.MEDIA_EJECT, intent.getDataString()).sendToTarget();
		}
		else if(intent.getAction().equals(Intent.ACTION_MEDIA_REMOVED))
		{
			//MyLog.e("ACTION_MEDIA_REMOVED");
			handler.obtainMessage(StorageState.MEDIA_REMOVED, intent.getDataString()).sendToTarget();
		}
	}
}
 

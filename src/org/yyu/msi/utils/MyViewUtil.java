/*  
* @Project: MyUtils 
* @User: Android 
* @Description: neldtv手机云相册第二版本
* @Author： yan.yu
* @Company：http://www.neldtv.org/
* @Date 2014-2-13 上午11:28:47 
* @Version V2.0   
*/ 

package org.yyu.msi.utils; 

import org.yyu.msi.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/** 
 * @ClassName: MyViewUtils 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-2-13 上午11:28:47  
 */
public class MyViewUtil
{

	public static void setViewSelcted(View view, boolean isSelect)
	{
		if(isSelect)
			view.setBackgroundResource(R.drawable.view_item_selector_1);
		else
			view.setBackgroundResource(R.drawable.view_item_selector);
	}
	
	/**
	* @Description: activity跳转
	* @param @param context
	* @param @param cls
	* @param @param bundle   
	* @return void 
	* @throws
	 */
	public static void startActivity(Activity context, Class<?> cls, Bundle bundle)
	{
		Intent intent = new Intent();
		intent.setClass(context, cls);
		if(bundle != null)
			intent.putExtras(bundle);
		context.startActivity(intent);
	}
	
	/**
	* @Description: activity跳转
	* @param @param context
	* @param @param bundle   
	* @return void 
	* @throws
	 */
	public static void startActivity(Activity context, Bundle bundle)
	{
		Intent intent = new Intent();  
		intent.putExtras(bundle);
        context.setResult(Activity.RESULT_OK, intent);  
        context.finish();  
	}
	
	/**
	* @Description: 设置RelativeLayout参数
	* @param @param view
	* @param @param width
	* @param @param height   
	* @return void 
	* @throws
	 */
	public static void setRltViewParams(View view, int width, int height)
    {
    	RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
    	if(width > 0)
    		params.width = width;
    	if(height > 0)
    		params.height = height;
    	view.setLayoutParams(params);
    }
	/**
	* @Description: 设置LinearLayout参数
	* @param @param view
	* @param @param width
	* @param @param height   
	* @return void 
	* @throws
	 */
    public static void setLineViewParams(View view, int width, int height)
    {
    	LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
    	if(width > 0)
    		params.width = width;
    	if(height > 0)
    		params.height = height;
    	view.setLayoutParams(params);
    }
    /**
    * @Description: 设置FrameLayout参数
    * @param @param view
    * @param @param width
    * @param @param height   
    * @return void 
    * @throws
     */
    public static void setFrameViewParams(View view, int width, int height)
    {
    	FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
    	if(width > 0)
    		params.width = width;
    	if(height > 0)
    		params.height = height;
    	view.setLayoutParams(params);
    }
    
    public static void setDialogFullscreen(Activity activity)
	{
		WindowManager windowManager = activity.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		lp.height = (int)(display.getHeight()); //设置高度
		activity.getWindow().setAttributes(lp);
	}
    
    /**
	* @Description: 发送handler消息
	* @param @param handler
	* @param @param obj
	* @param @param what   
	* @return void 
	* @throws
	 */
	public static void sendMessage(Handler handler, int what, Object obj)
	{
		Message msg = new Message();
		if(obj != null)
			msg.obj = obj;
		msg.what = what;
		if(handler != null)
			handler.sendMessage(msg);
	}
	public static void sendMessage(Handler handler, int what)
	{
		if(handler != null)
			handler.sendEmptyMessage(what);
	}
}
 

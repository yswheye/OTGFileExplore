/*  
* @Project: SlideMenuFragment 
* @User: Android 
* @Description: neldtv手机云相册第二版本
* @Author： yan.yu
* @Company：http://www.neldtv.org/
* @Date 2014-3-12 下午3:48:38 
* @Version V2.0   
*/ 

package org.yyu.msi.utils; 

import org.yyu.msi.common.PublicFun;

import android.content.Context;

/** 
 * @ClassName: MyUtil 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-3-12 下午3:48:38  
 */
public class MyUtil
{
	private MyZipUtil zipUtil = null;
	private MyBitmapUtil bitmapUtil = null;
	private MyFileUtil fileUtil = null;
	private MySharedPreference sharedPreference = null;
	private MyStringUtil stringUtil = null;
	private MySystemUtil systemUtil = null;
	private PublicFun publicFun = null;
	public void initUtil(Context context)
	{
		zipUtil = new MyZipUtil();
		bitmapUtil = new MyBitmapUtil();
		fileUtil = new MyFileUtil();
		sharedPreference = new MySharedPreference(context, null);
		stringUtil = new MyStringUtil();
		systemUtil = new MySystemUtil();
		publicFun = new PublicFun();
	}
	
	
	public MyZipUtil getZipUtil()
	{
		return zipUtil;
	}
	public MyBitmapUtil getBitmapUtil()
	{
		return bitmapUtil;
	}
	public MyFileUtil getFileUtil()
	{
		return fileUtil;
	}
	public MySharedPreference getSharedPreference()
	{
		return sharedPreference;
	}
	public MyStringUtil getStringUtil()
	{
		return stringUtil;
	}
	public MySystemUtil getSystemUtil()
	{
		return systemUtil;
	}
	
	public PublicFun getPublicFun()
	{
		return publicFun;
	}
}
 

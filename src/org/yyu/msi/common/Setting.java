/*  
* @Project: SlideMenuFragment 
* @User: Android 
* @Description: neldtv手机云相册第二版本
* @Author： yan.yu
* @Company：http://www.neldtv.org/
* @Date 2014-3-7 下午3:07:13 
* @Version V2.0   
*/ 

package org.yyu.msi.common; 

import org.yyu.msi.utils.MySharedPreference;

import android.content.Context;


/** 
 * @ClassName: Setting 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-3-7 下午3:07:13  
 */
public class Setting
{
	private final static String THUMBAL = "THUMBAL";//缩略图
	private final static String HIDE_FILE = "HIDE_FILE";//隐藏文件
	private final static String GRID_VIEW = "GRID_VIEW";//网格显示
	private final static String FILTER = "FILTER";//过滤图片
	
	public static void hideFile(Context context, boolean isHide)
	{
		MySharedPreference msp = new MySharedPreference(context, null);
		msp.setData(HIDE_FILE, isHide);
	}
	public static boolean isHide(Context context)
	{
		MySharedPreference msp = new MySharedPreference(context, null);
		return msp.getData(HIDE_FILE, false);
	}
	
	public static void setGridView(Context context, boolean isHide)
	{
		MySharedPreference msp = new MySharedPreference(context, null);
		msp.setData(GRID_VIEW, isHide);
	}
	public static boolean isGridView(Context context)
	{
		MySharedPreference msp = new MySharedPreference(context, null);
		return msp.getData(GRID_VIEW, false);
	}
	
	public static void setFilter(Context context, boolean isHide)
	{
		MySharedPreference msp = new MySharedPreference(context, null);
		msp.setData(FILTER, isHide);
	}
	public static boolean isFilter(Context context)
	{
		MySharedPreference msp = new MySharedPreference(context, null);
		return msp.getData(FILTER, false);
	}
}
 

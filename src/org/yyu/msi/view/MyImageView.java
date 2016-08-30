/*  
* @Project: SlideMenuFragment 
* @User: Android 
* @Description: neldtv手机云相册第二版本
* @Author： yan.yu
* @Company：http://www.neldtv.org/
* @Date 2014-3-8 上午9:43:33 
* @Version V2.0   
*/ 

package org.yyu.msi.view; 

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

/** 
 * @ClassName: MyImageView 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-3-8 上午9:43:33  
 */
public class MyImageView
{
	private ImageView imageView = null;
	private View loadingView = null;
	
	private boolean isLoading = false;
	
	public MyImageView(ImageView imageView)
	{
		this.imageView = imageView;
	}
	public MyImageView(ImageView imageView, View loadingView)
	{
		this.imageView = imageView;
		this.loadingView = loadingView;
	}
	
	public void setImageView(ImageView imageView)
	{
		this.imageView = imageView;
	}
	public void setLoadingView(View loadingView)
	{
		this.loadingView = loadingView;
	}
	public void setImageBitmap(Bitmap bm)
	{
		imageView.setImageBitmap(bm);
	}
	public void setImageDrawable(Drawable drawable)
	{
		imageView.setImageDrawable(drawable);
	}
	public void setImageResource(int res)
	{
		imageView.setImageResource(res);
	}
	public Drawable getDrawable()
	{
		return imageView.getDrawable();
	}
	public void setScaleType(ScaleType type)
	{
		imageView.setScaleType(type);
	}
	public void setIsLoading(boolean isLoading)
	{
		this.isLoading = isLoading;
	}
	
	public View getLoadingView()
	{
		return loadingView;
	}
	
	public boolean isLoading()
	{
		return isLoading;
	}
	
}

 

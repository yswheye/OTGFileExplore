
package org.yyu.msi.view; 

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.yyu.msi.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

/** 
 * @ClassName: PowerImageView 
 * @Description: TODO
 * 4.0以上系统的手机启动了硬件加速功能之后会导致GIF动画播放不出来
 * 需要在AndroidManifest.xml中去禁用硬件加速功能，可以通过指定android:hardwareAccelerated属性来完成
 * @author yan.yu 
 * @date 2014-3-15 上午10:10:15  
 */
public class PowerImageView extends ImageView implements OnClickListener
{

	//播放GIF动画的关键类 
	private Movie movie = null;
	//开始播放按钮图片
	private Bitmap mStartButton = null;
	//记录动画开始的时间
	private long mMovieStart = 0;
	//GIF图片的宽度
	private int mImageWidth = 0;
	//GIF图片的高度 
	private int mImageHeight = 0;
	//图片是否正在播放
	private boolean isPlaying = false;
	//是否允许自动播放
	private boolean isAutoPlay = false;
	//是否允许显示GIF图片
	private boolean isGifEnable = false;
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context
	* @param attrs
	* @param defStyle 
	*/
	public PowerImageView(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
	}
	public PowerImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs, 0);
		// TODO Auto-generated constructor stub
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PowerImageView);
		isGifEnable = a.getBoolean(R.styleable.PowerImageView_gif_enable, false);
	}
	public PowerImageView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if(movie != null && isGifEnable)
		{
			//setMeasuredDimension(mImageWidth, mImageHeight);
		}
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom)
	{
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
	}
	
	public void showGif(String fileUrl)
	{
		InputStream is;
		try
		{
			File file = new File(fileUrl);
			int size = (int)file.length();
			is = new BufferedInputStream(new FileInputStream(new File(fileUrl)), size);
	        is.mark(size); 
			movie = Movie.decodeStream(is);
			if(movie != null)
			{
				// 如果返回值不等于null，就说明这是一个GIF图片，下面获取是否自动播放的属性
				Bitmap bitmap = BitmapFactory.decodeFile(fileUrl);
				mImageWidth = bitmap.getWidth();
				mImageHeight = bitmap.getHeight();
				
				bitmap.recycle();
				if(!isAutoPlay)
				{
					// 当不允许自动播放的时候，得到开始播放按钮的图片，并注册点击事件
					mStartButton = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
					setOnClickListener(this);
				}
			}
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void showGif(InputStream is, Bitmap bitmap)
	{
		movie = Movie.decodeStream(is);
		if(movie != null)
		{
			// 如果返回值不等于null，就说明这是一个GIF图片，下面获取是否自动播放的属性
			mImageWidth = bitmap.getWidth();
			mImageHeight = bitmap.getHeight();
			bitmap.recycle();
			if(!isAutoPlay)
			{
				// 当不允许自动播放的时候，得到开始播放按钮的图片，并注册点击事件
				mStartButton = BitmapFactory.decodeResource(getResources(), R.drawable.play);
				setOnClickListener(this);
			}
		}
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onDraw(Canvas canvas)
	{
		// TODO Auto-generated method stub
		if(movie == null)
		{
			// mMovie等于null，说明是张普通的图片，则直接调用父类的onDraw()方法
			super.onDraw(canvas);
		}
		else if(isGifEnable)
		{
			// mMovie不等于null，说明是张GIF图片
			if(isAutoPlay)
			{
				// 如果允许自动播放，就调用playMovie()方法播放GIF动画
				playMovie(canvas);
				invalidate();
			}
			else
			{
				// 不允许自动播放时，判断当前图片是否正在播放
				if(isPlaying)
				{
					// 正在播放就继续调用playMovie()方法，一直到动画播放结束为止
					if(playMovie(canvas))
					{
						//isPlaying = false;
					}
					invalidate();
				}
				else
				{
					// 还没开始播放就只绘制GIF图片的第一帧，并绘制一个开始按钮
					movie.setTime(0);
					movie.draw(canvas, 0, 0);
					int offsetWidth = (mImageWidth - mStartButton.getWidth()) / 2;
					int offsetHeight = (mImageHeight - mStartButton.getHeight()) / 2;
					canvas.drawBitmap(mStartButton, offsetWidth, offsetHeight, null);
				}
			}
		}
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		if(v.getId() == getId())
		{
			isPlaying = true;
			invalidate();
		}
	}
	
	/**
	* @Description: 开始播放GIF动画，播放完成返回true，未完成返回false。
	* @param @param canvas
	* @param @return   
	* @return boolean 
	* @throws
	 */
	private boolean playMovie(Canvas canvas)
	{
		long now = System.currentTimeMillis();
		if(mMovieStart == 0)
			mMovieStart = now;
		int duration = movie.duration();
		if(duration == 0)
			duration = 1000;
		int realTime = (int)((now - mMovieStart) % duration);
		movie.setTime(realTime);
		movie.draw(canvas, 0, 0);
		if((now - mMovieStart) >= duration)
		{
			mMovieStart = 0;
			return true;
		}
		return false;
	}
}
 

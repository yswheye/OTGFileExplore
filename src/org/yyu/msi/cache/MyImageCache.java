package org.yyu.msi.cache;

import java.io.File;

import org.yyu.msi.utils.MyBitmapUtil;

import android.content.Context;
import android.graphics.Bitmap;


/**
* @ClassName: MyImageCache 
* @Description: 图片缓存，包括内存缓存和本地缓存
* @author yan.yu 
* @date 2013-6-18 上午9:47:41 
*
 */
public enum MyImageCache 
{

	INSTANCE;

	/*默认内存缓存大小5MB*/ 
	private static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 1024 * 8; 

	/*默认图片宽*/
	private static final int DEFAULT_REQ_WIDTH = 480;

	/*默认图片高*/
	private static final int DEFAULT_REQ_HEIGHT = 800;
	
	/* 是否使用内存缓存*/
	private static final boolean DEFAULT_MEM_CACHE_ENABLED = true;
	
	/* 是否使用SD卡缓存*/
	private static final boolean DEFAULT_DISK_CACHE_ENABLED = true;
	
	/*是否在使用缓存前清理SD卡*/ 
	private static final boolean DEFAULT_CLEAR_DISK_CACHE_ON_START = false;
	
	/*本地缓存大小*/ 
	private static final int DEFAULT_DISK_CAHCE_SIZE = 1024*1024*100;
	
	/*本地缓存目录*/ 
	private static final String DEFAULT_DISK_CACHE_DIR = "MyThumbals";

	/*缓存参数*/
	private ImageCacheParams mImageCacheParams = null;
	
	/*内存缓存对象*/
	public MyMemoryCache mMemoryCache;
	
	public MyDiskCache mDiskCache = null;

	public static MyImageCache createCache() 
	{
		return INSTANCE;
	}

	private MyImageCache() 
	{
	}

	/**
	* @Description: 设置参数
	* @param @param cacheParams
	* @param @param context   
	* @return void 
	* @throws
	 */
	public void setCacheParams(ImageCacheParams cacheParams, Context context) 
	{
		init(cacheParams, context);
	}

	/**
	* @Description: 初始化缓存
	* @param @param cacheParams
	* @param @param context   
	* @return void 
	* @throws
	 */
	private void init(ImageCacheParams cacheParams, Context context) 
	{
		mImageCacheParams = cacheParams;

		if (cacheParams.diskCacheEnabled) 
		{
			mDiskCache = new MyDiskCache(context);
			if (cacheParams.clearDiskCacheOnStart) 
			{
				mDiskCache.clearCache();
			}
		}
		else
			mDiskCache = null;
		
		/*设置内存缓存大小*/
		if (cacheParams.memoryCacheEnabled) 
		{
			if(mMemoryCache == null)
				mMemoryCache = MyMemoryCache.createMemCache(cacheParams.memCacheSize);
			else
				mMemoryCache.setCacheSize(cacheParams.memCacheSize);
		}
		else
			mMemoryCache = null;
	}

	/**
	* @Description: 将图片添加到缓存
	* @param @param data
	* @param @param bitmap   
	* @return void 
	* @throws
	 */
	public void addBitmapToCache(String key, Bitmap bitmap) 
	{
		if (key == null || bitmap == null) 
		{
			return;
		}

		/*添加到内存缓存*/
		if (mMemoryCache != null && mMemoryCache.getBitmap(key) == null) 
		{
			mMemoryCache.putBitmap(key, bitmap);
		}
		
	}
	
	/**
	* @Description: 图片保存本地缓存
	* @param @param file
	* @param @param image   
	* @return void 
	* @throws
	 */
	public void putBitmapToDiskCache(String path, Bitmap image, int size)
	{
		if (mDiskCache != null) 
		{
			mDiskCache.putFile(path, image, size);
		}
	}

	/**
	* @Description: 从内存缓存中去掉一个
	* @param @param key   
	* @return void 
	* @throws
	 */
	public void removeBitmapFromCache(String key) 
	{
		if (key == null) 
		{
			return;
		}
		if (mMemoryCache != null) 
		{
			mMemoryCache.removeBitmap(key);
		}
	}
	
	/**
	* @Description: 从 内存取得图片
	* @param @param path
	* @param @return   
	* @return Bitmap 
	* @throws
	 */
	public Bitmap getBitmapFromMem(String path) 
	{
		if (mMemoryCache != null) 
		{
			final Bitmap memBitmap = mMemoryCache.getBitmap(path);
			if (memBitmap != null && !memBitmap.isRecycled()) 
			{
				return memBitmap;
			}
		}
		return null;
	}

	/**
	* @Description: 从本地缓存中取图片
	* @param @param path
	* @param @return   
	* @return File 
	* @throws
	 */
	public Bitmap getBitmapFromDiskCache(String path) 
	{
		if (mDiskCache != null && path != null) 
		{
			final File cacheFile = mDiskCache.getFile(path);
			if (cacheFile != null && mDiskCache.containsKey(path)) 
			{
				return decodeBitmap(cacheFile);
			}
		}
		return null;
	}

	/**
	* @Description: 获取本地缓存图片
	* @param @param path
	* @param @return   
	* @return File 
	* @throws
	 */
	public File getImageFromDisk(String path)
	{
		if (mDiskCache != null && path != null) 
		{
			return mDiskCache.getFile(path);
		}
		return null;
	}

	/**
	* @Description: 清理内存缓存
	* @param    
	* @return void 
	* @throws
	 */
	public void clearCaches() 
	{
		if (mMemoryCache != null) 
		{
			mMemoryCache.clear();
		}
		/*if(mDiskCache != null)
			mDiskCache.clearCache();*/
	}
	
	/**
	* @Description: 更新尺寸 
	* @param @param width
	* @param @param height   
	* @return void 
	* @throws
	 */
	public void updateSize(int width, int height)
	{
		mImageCacheParams.reqWidth = width;
		mImageCacheParams.reqHeight = height;
	}

	/**
	* @Description: 解码图片,大图
	* @param @param fileName 文件路径
	* @param @return   
	* @return Bitmap 
	* @throws
	 */
	public synchronized Bitmap decodeBitmap(String fileName) 
	{
		return MyBitmapUtil.decodeLocal(fileName, mImageCacheParams.reqWidth, mImageCacheParams.reqHeight);
	}

	/**
	* @Description: 解码图片，缩略图
	* @param @param file 文件
	* @param @return   
	* @return Bitmap 
	* @throws
	 */
	public synchronized Bitmap decodeBitmap(File file) 
	{
		return MyBitmapUtil.decodeLocal(file.getAbsolutePath(), mImageCacheParams.reqWidth, mImageCacheParams.reqHeight);
	}

	public synchronized Bitmap decodeBitmap(Context context, int res)
	{
		return MyBitmapUtil.decodeResource(context, res, mImageCacheParams.reqWidth, mImageCacheParams.reqHeight);
	}
	
	/**
	* @ClassName: ImageCacheParams 
	* @Description: 参数设置类
	* @author yan.yu 
	* @date 2013-6-19 上午11:25:04 
	*
	 */
	public static class ImageCacheParams 
	{
		/*使能内存缓存*/
		public boolean memoryCacheEnabled = DEFAULT_MEM_CACHE_ENABLED;
		
		/*使能本地缓存*/
		public boolean diskCacheEnabled = DEFAULT_DISK_CACHE_ENABLED;
		
		/*内存缓存大小*/
		public int memCacheSize = DEFAULT_MEM_CACHE_SIZE;
		
		/*本地缓存大小*/
		public int diskCachrSize = DEFAULT_DISK_CAHCE_SIZE;
		
		/*本地缓存目录*/
		public String diskCacheDir = DEFAULT_DISK_CACHE_DIR;
		
		/*是否在程序开始时清除本地缓存*/
		public boolean clearDiskCacheOnStart = DEFAULT_CLEAR_DISK_CACHE_ON_START;

		/*缩略图宽*/
		public int reqWidth = DEFAULT_REQ_WIDTH;
		
		/*缩略图高*/
		public int reqHeight = DEFAULT_REQ_HEIGHT;
		
		/*默认缩略图*/
		public Integer loadingResId = 0;
		
		/*图片类型：0，缩略图；1，大图；2，网络图片*/
		public int type = 0;
	}
}

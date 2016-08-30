
package org.yyu.msi.cache;

import java.io.File;

import org.yyu.msi.utils.MyBitmapUtil;
import org.yyu.msi.utils.MySystemUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
* @ClassName: MyDiskCache 
* @Description: 本地缓存工具类
* @author yan.yu 
* @date 2013-6-18 上午9:43:02 
 */
public class MyDiskCache 
{

	private final File mCacheDir;

	private int cacheSize = 1024*1024*100;
	
	private  Context context = null;
	
	private String DEFAULT_CACHE_DIR = "MSI/cache";
	
	private boolean isOriginal = false;
	
	public MyDiskCache(Context context) 
	{
		this.context = context;
		this.mCacheDir = openCache(DEFAULT_CACHE_DIR, cacheSize);
		initDiskCache();
	}
	
	private void initDiskCache()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				File[] files = mCacheDir.listFiles();
				if(files != null)
				{
					sFileCache.evictAll();
					for(File file : files)
					{
						if(file.exists())
						{
							synchronized(sFileCache)
							{  
								 sFileCache.put(file.getAbsolutePath(), file.length());
					        } 
						}
					}
				}
			}
		}).start();
	}
	
	/**
	* @Description: 创建对象，创建本地缓存目录
	* @param @return   
	* @return MyDiskCache 
	* @throws
	 */
	private File openCache(String path, int cacheSize) 
	{
		File cacheDir = MySystemUtil.getAvailableDir(context, path);
		if (!cacheDir.exists()) 
		{
			cacheDir.mkdirs();
		}
		
		/*检查当前可写的目录的剩余空间是否大于限定的空间DISK_CACHE_SIZE*/
		if (cacheDir.isDirectory() && cacheDir.canWrite()) 
		{
			return cacheDir;
		} 

		return null;
	}

	private final LruCache<String, Long> sFileCache = new LruCache<String, Long>(cacheSize){  
        @Override  
        public int sizeOf(String key, Long value){  
            return value.intValue();  
        }  
        @Override  
        protected void entryRemoved(boolean evicted, String key, Long oldValue, Long newValue){  
            File file = new File(key);  
            if(file != null && file.exists())  
                file.delete();  
        }  
    }; 

	/**
	* @Description: 清除cacheDir目录下的缓存文件
	* @param @param cacheDir   
	* @return void 
	* @throws
	 */
	private void clearCache(File cacheDir) 
	{
		final File[] files = cacheDir.listFiles();
		for (int i = 0; i < files.length; i++) 
		{
			files[i].delete();
		}
	}

	/**
	* @Description: 将文件名字编码
	* @param @param url
	* @param @return   
	* @return String 
	* @throws
	 */
	private String getFilePath(String url) 
	{
		try 
		{
			return String.valueOf(url.hashCode());  
	        // Another possible solution  
			//return URLEncoder.encode(url.replace("*", ""), "UTF-8");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	* @Description: 在指定的缓存路径下创建缓存文件
	* @param @param cacheDir
	* @param @param key
	* @param @return   
	* @return String 
	* @throws
	 */
	private String createFilePath(File cacheDir, String key) 
	{
		if(!cacheDir.exists())//本地缓存目录不存在就创建缓存目录
			cacheDir.mkdirs();
		if(isOriginal)//就直接按照图片名字存储
		{
			return cacheDir.getAbsolutePath() + File.separator + key;
		}
		else//否则对图片名字进行编码存储
			return cacheDir.getAbsolutePath() + File.separator + getFilePath(key);
	}

	/**
	* @Description: 从本地缓存中获取文件
	* @param @param key
	* @param @return   
	* @return String 
	* @throws
	 */
	public File getFile(String key) 
	{
		return new File(createFilePath(mCacheDir, key));
	}

	/**
	* @Description: 保存至本地缓存
	* @param @param file
	* @param @param image   
	* @return void 
	* @throws
	 */
	public void putFile(String key, Bitmap image, int size)
	{
		MyBitmapUtil.compressImage(getFile(key), image, size);
		 synchronized(sFileCache)
		 {  
			 File file = getFile(key);
			 if(file != null && file.exists())
				 sFileCache.put(key, file.length());  
         }  
	}
	
	/**
	* @Description: 从本地缓存中获取文件路径
	* @param @param key
	* @param @return   
	* @return String 
	* @throws
	 */
	private String get(String key) 
	{
		final String existingFile = createFilePath(mCacheDir, key);
		if (new File(existingFile).exists()) 
		{
			return existingFile;
		}

		return null;

	}

	/**
	* @Description: 检查缓存中是否有key
	* @param @param key
	* @param @return   
	* @return boolean 
	* @throws
	 */
	public boolean containsKey(String key) 
	{
		final String existingFile = createFilePath(mCacheDir, key);
		if (new File(existingFile).exists()) 
		{
			return true;
		}
		return false;
	}

	/**
	* @Description: 清除缓存
	* @param    
	* @return void 
	* @throws
	 */
	public void clearCache() 
	{
		clearCache(mCacheDir);
	}

	/**
	* @Description: 清除指定目录下的缓存
	* @param @param uniqueName   
	* @return void 
	* @throws
	 */
	public void clearCache(String uniqueName) 
	{
		File cacheDir = MySystemUtil.getAvailableDir(context, uniqueName);
		clearCache(cacheDir);
	}
}

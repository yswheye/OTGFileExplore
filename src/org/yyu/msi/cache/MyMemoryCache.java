package org.yyu.msi.cache;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;

import org.yyu.msi.utils.MyBitmapUtil;
import org.yyu.msi.utils.MyLog;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
* @ClassName: MyMemCache 
* @Description: 内存缓存，包括硬引用和软引用
* @author yan.yu 
* @date 2013-6-20 下午1:39:16 
*
 */
public class MyMemoryCache
{

	/*默认开辟8M硬缓存空间  */
    private int hardCachedSize = 4*1024*1024;   
    
    public static MyMemoryCache createMemCache(int hardCachedSize) 
	{
		return new MyMemoryCache(hardCachedSize);
	}
    
    public MyMemoryCache(int hardCachedSize)
    {
    	this.hardCachedSize = hardCachedSize;
    }
    
    public void setCacheSize(int hardCachedSize)
    {
    	this.hardCachedSize = hardCachedSize;
    }
    
    /*硬缓存*/
    private final LruCache<String, Bitmap> sHardBitmapCache = new LruCache<String, Bitmap>(hardCachedSize)
    {  
        @Override  
        public int sizeOf(String key, Bitmap bitmap)
        {  
            //return bitmap.getRowBytes() * bitmap.getHeight();  
        	/*获取bitmap占用内存大小*/
        	return MyBitmapUtil.sizeOfBitmap(bitmap, null);
        }  
        @Override  
        protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue)
        {  
            
           if(oldValue != null && !oldValue.isRecycled())
           {
        	   //MyLog.e("MyMemCache", "hard cache is full , push to soft cache");  
               /*硬引用缓存区满，将一个最不经常使用的oldvalue推入到软引用缓存区 */ 
        	   sSoftBitmapCache.put(key, new SoftReference<Bitmap>(oldValue));  
        	   //oldValue.recycle();
           }
        }  
    } ; 
    
    /*软缓存大小*/
    private static final int SOFT_CACHE_CAPACITY = 20;
	private final static LinkedHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache = 
    		new  LinkedHashMap<String, SoftReference<Bitmap>>(SOFT_CACHE_CAPACITY, 0.75f, true)
    {
		/** 
		* @Fields serialVersionUID : TODO
		*/ 
		private static final long serialVersionUID = -30993555723404479L;

		public SoftReference<Bitmap> put(String key, SoftReference<Bitmap> value)
		{  
            return super.put(key, value);  
		};
		
		@Override
		protected boolean removeEldestEntry(Entry<String, SoftReference<Bitmap>> eldest)
		{
			if(size() > SOFT_CACHE_CAPACITY)
			{  
				//MyLog.e("MyMemCache", "sSoftBitmapCache size:" + size());  
				/*String str = eldest.getKey();
				Bitmap bm = eldest.getValue().get();
				if(bm != null && !bm.isRecycled())
				{
					bm.recycle();
					bm = null; 
					MyLog.e("MyMemCache", "SoftReference recycle one!"); 
				}*/
				MyLog.e("MyMemCache", "SoftReference recycle one!"); 
                return true;  
            }  
			return false;  
		}
    };
    
    //缓存bitmap  
    public boolean putBitmap(String key, Bitmap bitmap)
    {  
        if(bitmap != null)
        {  
            synchronized(sHardBitmapCache)
            {  
                sHardBitmapCache.put(key, bitmap);  
            }  
            return true;  
        }         
        return false;  
    }  
    //从缓存中获取bitmap  
    public Bitmap getBitmap(String key)
    {  
    	if(key == null)
    		return null;
    	synchronized(sHardBitmapCache)
    	{  
            final Bitmap bitmap = sHardBitmapCache.get(key);  
            if(bitmap != null && !bitmap.isRecycled())  
                return bitmap;  
        }  
        //硬引用缓存区间中读取失败，从软引用缓存区间读取  
        synchronized(sSoftBitmapCache)
        {  
            SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(key);  
            if(bitmapReference != null)
            {  
                final Bitmap bitmap2 = bitmapReference.get();  
                if(bitmap2 != null && !bitmap2.isRecycled())  
                    return bitmap2;  
                else
                {  
                    MyLog.e("MyMemCache", "soft reference  has recycled!");  
                    sSoftBitmapCache.remove(key);  
                }  
            }  
        }  
        return null;  
    }  
    
    /**
    * @Description: 移除指定的bitmap
    * @param @param key   
    * @return void 
    * @throws
     */
    public void removeBitmap(String key)
    {
    	if(key == null)
    		return ;
		//synchronized(sHardBitmapCache)
    	{  
            Bitmap bitmap = sHardBitmapCache.remove(key);  
            if(bitmap != null && !bitmap.isRecycled())
            {
            	bitmap.recycle();
            	bitmap = null;
            	MyLog.e("***remove bitmap from hardchace!");
            }
        } 
		//硬引用缓存区间中读取失败，从软引用缓存区间读取  
       // synchronized(sSoftBitmapCache)
        {  
            SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(key);  
            if(bitmapReference != null)
            {  
            	Bitmap bitmap = sSoftBitmapCache.remove(key).get();  
            	if(bitmap != null && !bitmap.isRecycled())
                {
                	bitmap.recycle();
                	bitmap = null;
                	MyLog.e("!!!remove bitmap from softchace!");
                }
            }  
        } 
    }
    
    /**
    * @Description: 清除缓存
    * @param    
    * @return void 
    * @throws
     */
    public void clear()
    {
    	sHardBitmapCache.evictAll();
    }
}

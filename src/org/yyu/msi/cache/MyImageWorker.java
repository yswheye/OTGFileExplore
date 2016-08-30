package org.yyu.msi.cache;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.yyu.msi.R;
import org.yyu.msi.cache.MyImageCache.ImageCacheParams;
import org.yyu.msi.listener.IHandleCacheListener;
import org.yyu.msi.utils.MyBitmapUtil;
import org.yyu.msi.utils.MyFileUtil;
import org.yyu.msi.utils.MyLog;
import org.yyu.msi.utils.MyViewUtil;
import org.yyu.msi.view.PowerImageView;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.provider.MediaStore.Images.Thumbnails;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

/**
* @ClassName: MyImageWorker 
* @Description: 图片处理工具,做图片处理只需要这个类
* @author yan.yu 
* @date 2013-6-19 上午11:25:25 
*
 */
public enum MyImageWorker 
{
	INSTANCE;

	/*缓存对象*/
	public MyImageCache mImageCache = MyImageCache.createCache();

	private Context mContext;

	private Bitmap mLoadingBitmap;

	/*加载图片的线程池*/
	private ExecutorService searchThreadPool;
	
	/*保存图片处理参数的容器*/
	private HashMap<Integer, ImageCacheParams> params;
	private Handler mHandler;

	private ImageCacheParams cacheParams = null;
	
	/*显示图片的回调接口*/
	private IHandleCacheListener mIHandleCache;

	private boolean isStop = false;
	
	private int memorySize = 1024*1024*8;//默认分配8M内存缓存
	
	private int decode_fullview_width = 0;
	private int decode_fullview_height = 0;
	private int decode_thumbal_width = 0;
	private int decode_thumbal_height = 0;
	
	private MyImageWorker() 
	{
		mHandler = new Handler();
		searchThreadPool = Executors.newFixedThreadPool(3);
		
		mIHandleCache = new IHandleCacheListener() 
		{

			@Override
			public void onSetImage(final PowerImageView imageView,
					final Bitmap bitmap) 
			{
				mHandler.post(new Runnable() 
				{
					@Override
					public void run() 
					{
						/*显示图片*/
						try
						{
							if(!isStop && cacheParams.type == 0)//缩略图
							{
								imageView.setScaleType(ScaleType.CENTER_CROP);//适应ImageView大小填充
								if(!isStop && bitmap != null && !bitmap.isRecycled())
								{
									imageView.setImageBitmap(bitmap);
								}
								else if(!isStop)//解码失败
									imageView.setImageResource(R.drawable.file_image);
							}
							else if(!isStop && (cacheParams.type == 1))//全屏
							{
								
								if(!isStop && bitmap != null && !bitmap.isRecycled())
								{
									imageView.setImageBitmap(bitmap);
								}
								else if(!isStop)//解码失败
								{
									MyLog.e(MyImageWorker.class,"bitmap has recyccled!");
									imageView.setImageResource(R.drawable.file_image);
								}
							}
							else//停止或者参数不对
								imageView.setImageResource(R.drawable.file_image);
						} 
						catch (OutOfMemoryError e)
						{
							// TODO: handle exception
							MyLog.e(MyImageWorker.class,"error:"+e.getMessage());
						}
					}
				});
			}

			@Override
			public void onError(final PowerImageView imageView) 
			{
				mHandler.post(new Runnable() 
				{
					@Override
					public void run() 
					{
						if (imageView != null) 
						{
							/*显示错误图片*/
							if(cacheParams.type == 0)
								imageView.setImageResource(R.drawable.file_image);
							else if(cacheParams.type == 1)
								imageView.setImageResource(R.drawable.error);
						}
					}
				});
			}

			@Override
			public void onSetGif(final PowerImageView imageView, final InputStream is, final Bitmap bm)
			{
				// TODO Auto-generated method stub
				mHandler.post(new Runnable() 
				{
					@Override
					public void run() 
					{
						if (imageView != null) 
						{
							imageView.setImageBitmap(null);
							imageView.showGif(is, bm);
							if(bm != null)
								MyViewUtil.setRltViewParams(imageView, bm.getWidth(), bm.getHeight());
						}
					}
				});
			}
		};
	}

	public static MyImageWorker newInstance() 
	{
		return INSTANCE;
	}
	
	/**
	* @Description: 解码器初始化
	* @param @param context
	* @param @param screenWidth
	* @param @param screenHeight   
	* @return void 
	* @throws
	 */
	public void init(Context context)
	{
		mContext = context;
		
		decode_fullview_width = 720;
		decode_fullview_height = 1080;
		decode_thumbal_width = decode_fullview_width / 4;
		decode_thumbal_height = decode_fullview_height / 4;
		initParams();
	}
	
	/**
	* @Description: 加载图片
	* @param @param path 图片路径
	* @param @param imageView    
	* @return void 
	* @throws
	 */
	public void loadBitmap(final String path, final PowerImageView imageView) 
	{
		Bitmap result = null;
		if(cacheParams.type == 0)
			result = mImageCache.getBitmapFromMem(path);
		else if(cacheParams.type == 1)
			result = mImageCache.getBitmapFromMem(path + "full_view");
		if (!isStop && result != null && !result.isRecycled()) 
		{
			mIHandleCache.onSetImage(imageView, result);
		} 
		else if (cancelWork(imageView, path)) 
		{
			final SearchTask task = new SearchTask(path, imageView, mIHandleCache);
			
			/*在执行BitmapWorkerTask前，你需要创建一个AsyncDrawable并将之绑定到目标ImageView*/
			Drawable d = imageView.getDrawable();
			if (d != null) 
			{
				d.setCallback(null);
				d = null;
			}
			final AsyncDrawable asyncDrawable = new AsyncDrawable(mContext.getResources(), mLoadingBitmap, task);
			imageView.setImageDrawable(asyncDrawable);
			
			if (!searchThreadPool.isTerminated() && !searchThreadPool.isShutdown()) 
			{
				searchThreadPool.execute(task);
			}
		}
	}

	/**
	* @Description: 检测一个执行中的任务是否与ImageView有关联。
	* 				如果有关联，它将通过调用canceel()方法试图取消之前的任务。
	* 				在少数情况下，新的任务中的数据与现有的任务相匹配，因此不需要做什么
	* @param @param view
	* @param @param path
	* @param @return   
	* @return boolean 
	* @throws
	 */
	protected boolean cancelWork(final PowerImageView view, final String path) 
	{
		SearchTask task = getSearchTask(view);
		if (task != null) 
		{
			final String taskPath = task.getPath();
			if (TextUtils.isEmpty(taskPath) || !taskPath.equals(path)) 
			{
				//MyLog.e(MyImageWorker.class, "cancelWork");
				task.cancelWork();
			} 
			else 
			{
				//MyLog.e(MyImageWorker.class, "task is exist");
				return false;
			}
		} 
		else 
		{
			//MyLog.i("foyo", "new  task");
		}
		return true;
	}

	/**
	* @Description: 取消和当前ImageView关联的线程
	* @param @param imageView   
	* @return void 
	* @throws
	 */
	public void cancelWork(final PowerImageView imageView) 
	{
		final SearchTask bitmapWorkerTask = getSearchTask(imageView);
		if (bitmapWorkerTask != null) 
		{
			bitmapWorkerTask.cancelWork();
		}
	}

	/**
	* @Description: 检索和指定ImageView相关的任务
	* @param @param imageView
	* @param @return   
	* @return SearchTask 
	* @throws
	 */
	public SearchTask getSearchTask(final PowerImageView imageView) 
	{
		if (imageView != null) 
		{
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) 
			{
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getTask();
			}
		}
		return null;
	}

	/**
	* @Description: 设置默认显示图片
	* @param @param resId   
	* @return void 
	* @throws
	 */
	private void setLoadingImage(final int resId) 
	{
		try
		{
			InputStream is = mContext.getResources().openRawResource(resId);
			mLoadingBitmap = BitmapFactory.decodeStream(is);
		} 
		catch (OutOfMemoryError e)
		{
			// TODO: handle exception
		}
	}
	public void setLoadingImage(Bitmap bitmap) 
	{
		if(bitmap != null)
		{
			mLoadingBitmap = bitmap;
			
			mImageCache.addBitmapToCache("mLoadingBitmap", mLoadingBitmap);
		}
	}
	
	/**
	* @ClassName: SearchTask 
	* @Description: 更新BitmapWorkerTask中的onPostExecute()方法，
	* 				以便检测与ImageView关联的任务是否被取消或者与当前任务相匹配
	* @author yan.yu 
	* @date 2013-6-19 上午11:32:57 
	*
	 */
	public class SearchTask implements Runnable 
	{
		String path;
		volatile boolean stop = false;
		IHandleCacheListener mIHandleCache;

		private WeakReference<PowerImageView> mImageViewReference;

		// 停止掉任务
		public void cancelWork() 
		{
			stop = true;
		}

		public SearchTask(final String path, final PowerImageView imageView, final IHandleCacheListener mIHandleCache) 
		{
			this.path = path;
			mImageViewReference = new WeakReference<PowerImageView>(imageView);
			this.mIHandleCache = mIHandleCache;
		}

		public String getPath() 
		{
			return path;
		}

		@Override
		public void run() 
		{
			
			Bitmap bitmap = null;
			if(cacheParams.type == 0)
				bitmap = mImageCache.getBitmapFromDiskCache(path);
			else if(cacheParams.type == 1)
				bitmap = mImageCache.getBitmapFromDiskCache(path + "full_view");
			
			if (bitmap == null && mImageCache != null && !stop && getAttachedImageView() != null) 
			{
				if(!isStop && cacheParams.type == 0) //缩略图
				{
					if(MyFileUtil.isPicture(path))
					{
						//图片缩略图
						bitmap = MyBitmapUtil.getImageThumbnail(path, decode_thumbal_width, decode_thumbal_height);
					}
					
					if(MyFileUtil.isVideo(path))
					{
						//视频缩略图
						bitmap = MyBitmapUtil.getVideoThumbnail(path, decode_thumbal_width, decode_thumbal_height,
								Thumbnails.MINI_KIND);
					}
					if(bitmap != null && !bitmap.isRecycled())
						mImageCache.putBitmapToDiskCache(path, bitmap, 20);
				}
				else if(!isStop && cacheParams.type == 1) //全屏
				{
					if(!path.endsWith(".gif"))
					{
						bitmap = MyBitmapUtil.decodeLocal(path, decode_fullview_width, decode_fullview_height);
						if(bitmap != null && !bitmap.isRecycled())
							mImageCache.putBitmapToDiskCache(path + "full_view", bitmap, 200);
					}
					else
					{
						bitmap = MyBitmapUtil.decodeLocal(path, decode_fullview_width, decode_fullview_height);
						File file = new File(path);
						int size = (int)file.length();
						InputStream is;
						try
						{
							is = new BufferedInputStream(new FileInputStream(file), size);
							is.mark(size); 
							mIHandleCache.onSetGif(getAttachedImageView(), is, bitmap);
						} catch (FileNotFoundException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}

			if (bitmap != null && !bitmap.isRecycled() && mImageCache != null && !stop) 
			{
				PowerImageView imageView = getAttachedImageView();
				if(cacheParams.type == 0)
					mImageCache.addBitmapToCache(path, bitmap);
				else if(cacheParams.type == 1)
					mImageCache.addBitmapToCache(path + "full_view", bitmap);
				if (imageView != null && !stop) 
				{
					mIHandleCache.onSetImage(imageView, bitmap);
				} 
				else 
				{
					MyLog.e("", "recycle!!!!!!!!!");
					bitmap.recycle();
					bitmap = null;
				}
			}
		}

		/**
		* @Description: 当前任务还在执行时，获取和当前任务关联的ImageView
		* @param @return   
		* @return PowerImageView 
		* @throws
		 */
		private PowerImageView getAttachedImageView() 
		{
			final PowerImageView imageView = mImageViewReference.get();
			final SearchTask bitmapWorkerTask = getSearchTask(imageView);

			if (this == bitmapWorkerTask) 
			{
				return imageView;
			}
			return null;
		}
	}

	/**
	* @Description: 打开解码
	* @param @param tag 0,本地缩略图；1，本地全屏；2，网络缩略图；3，网络全屏   
	* @return void 
	* @throws
	 */
	public void openImageWorker(int tag)
	{
		restartThreadPool();//重启线程池
		cacheParams = getParams(tag);//获取解码参数
		if(cacheParams != null)
		{
			isStop = false;//允许加载
			mImageCache.setCacheParams(cacheParams, mContext);//设置解码参数
			setLoadingImage(getParams(tag).loadingResId);//设置默认图片
		}
	}
	
	/**
	* @Description: 关闭线程池，清除内存缓存，回收内存
	* @param    
	* @return void 
	* @throws
	 */
	public void closeImageWorker()
	{
		isStop = true;//禁止加载
		shutdownThreadPool();//关闭线程池
		recycleBitmap();
	}
	
	public void recycleBitmap()
	{
		mImageCache.clearCaches();//清除内存中的数据，回收内存
	}
	
	/**
	* @Description: 重启线程池
	* @param    
	* @return void 
	* @throws
	 */
	private void restartThreadPool() 
	{
		synchronized (searchThreadPool) 
		{
			if (searchThreadPool.isTerminated() || searchThreadPool.isShutdown()) 
			{
				searchThreadPool = null;
				searchThreadPool = Executors.newFixedThreadPool(3);
			}
		}
	}
	
	/**
	* @Description: 关闭线程池
	* @param    
	* @return void 
	* @throws
	 */
	private void shutdownThreadPool() 
	{
		searchThreadPool.shutdownNow();
		//searchThreadPool.shutdown();
	}

	/**
	* @Description: 初始化图片解码参数
	* @param    
	* @return void 
	* @throws
	 */
	private void initParams()
	{
		addParams(0, thumbalParam());//图片缩略图
		addParams(1, fullViewParam());//
	}
	
	/**
	* @Description: 设置缓存参数
	* @param @param tag
	* @param @param cacheParams   
	* @return void 
	* @throws
	 */
	@SuppressLint("UseSparseArrays")
	public void addParams(int tag, ImageCacheParams cacheParams) 
	{
		if (params == null) 
		{
			params = new HashMap<Integer, MyImageCache.ImageCacheParams>();
		}
		if(!params.containsKey(tag))
			params.put(tag, cacheParams);
		mImageCache.setCacheParams(getParams(tag),mContext);
		setLoadingImage(cacheParams.loadingResId);
		this.cacheParams = cacheParams;
	}
	
	/**
	* @Description: 获取参数
	* @param @param tag 0,本地缩略图；1，本地全屏；2，网络缩略图；3，网络全屏   
	* @param @return   
	* @return ImageCacheParams 
	* @throws
	 */
	public ImageCacheParams getParams(int tag) 
	{
		return params.get(tag);
	}

	/**
	* @ClassName: AsyncDrawable 
	* @Description: 用来存储worker task的引用。
	* 				在这种情况下，任务结束的时候BitmapDrawable可以取代图像占位符显示在ImageView中
	* @author yan.yu 
	* @date 2013-6-19 上午11:38:28 
	*
	 */
	public class AsyncDrawable extends BitmapDrawable 
	{
		private final WeakReference<SearchTask> task;

		public AsyncDrawable(Resources res, Bitmap bitmap, SearchTask searchTask) 
		{
			super(res, bitmap);
			task = new WeakReference<SearchTask>(searchTask);
		}

		public SearchTask getTask() 
		{
			return task.get();
		}
	}

	 /**
    * @Description: 设置本地缩略图参数
    * @param @param activity
    * @param @return   
    * @return ImageCacheParams 
    * @throws
     */
	private ImageCacheParams thumbalParam()
    {
    	ImageCacheParams params = new ImageCacheParams();
    	params.reqWidth =  decode_thumbal_width;//解码宽
    	params.reqHeight =  decode_thumbal_height;//解码高
    	params.clearDiskCacheOnStart = false;//开启之前是否清除本地缓存
    	params.loadingResId = R.drawable.file_image;//默认加载图片ID
    	params.memCacheSize = memorySize;//内存缓存大小
    	params.diskCacheEnabled = true;//是否开启本地缓存
    	params.memoryCacheEnabled = true;//是否开启内存缓存
    	params.type = 0;//解码类型：0，为缩略图；1，为大图；2，为网络图片
		return params;
    }
	private ImageCacheParams fullViewParam()
	{
		ImageCacheParams params = new ImageCacheParams();
		params.reqWidth =  decode_fullview_width;//解码宽
		params.reqHeight =  decode_fullview_height;//解码高
		params.clearDiskCacheOnStart = false;//开启之前是否清除本地缓存
		params.loadingResId = R.drawable.loading;//默认加载图片ID
		params.memCacheSize = memorySize;//内存缓存大小
		params.diskCacheEnabled = true;//是否开启本地缓存
		params.memoryCacheEnabled = true;//是否开启内存缓存
		params.type = 1;//解码类型：0，为缩略图；1，为大图；2，为网络图片
		return params;
	}
}

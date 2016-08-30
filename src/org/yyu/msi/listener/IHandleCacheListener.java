package org.yyu.msi.listener;

import java.io.FileOutputStream;
import java.io.InputStream;

import org.yyu.msi.view.PowerImageView;

import android.graphics.Bitmap;


/**
* @ClassName: IHandleCacheListener 
* @Description: 图片显示接口
* @author yan.yu 
* @date 2013-6-21 下午3:47:45 
*
 */
public interface IHandleCacheListener 
{
	/*解码成功显示图片*/
	void onSetImage(final PowerImageView imageView, final Bitmap bitmap);
	
	void onSetGif(final PowerImageView imageView, final InputStream is, final Bitmap bm);
	
	/*失败显示默认图片*/
	void onError(final PowerImageView imageView);

}


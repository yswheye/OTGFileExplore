/*  
* @Project: MobileFileExplorer 
* @User: Android 
* @Description: 社区商服项目
* @Author： yan.yu
* @Company：http://www.neldtv.org/
* @Date 2014-4-24 上午9:28:41 
* @Version V1.0   
*/ 

package org.yyu.msi.activity; 

import java.util.ArrayList;
import java.util.List;

import org.yyu.msi.R;
import org.yyu.msi.entity.Global;
import org.yyu.msi.utils.MyFileInfor;
import org.yyu.msi.utils.MyFileUtil;
import org.yyu.msi.utils.MyStringUtil;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

/** 
 * @ClassName: ImageViewActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-4-24 上午9:28:41  
 */
public class ImageViewActivity extends FragmentActivity
{

	private ViewPager viewPager = null;
	
	private ImageViewAdapter imageViewAdapter = null;
	private List<MyFileInfor> picList = new ArrayList<MyFileInfor>();
	
	private String path = null;
	private int curPage = 0;
	
	private final int MSG_GET_IMAGE_START = 0;
	private final int MSG_GET_IMAGE_FINISH = 1;
	Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == MSG_GET_IMAGE_START)
			{
				
			}
			else if(msg.what == MSG_GET_IMAGE_FINISH)
			{
				imageViewAdapter.notifyDataSetChanged();
				viewPager.setCurrentItem(curPage);
			}
		};
	}
	;
	/**
	*callbacks
	*/
	@Override
	protected void onCreate(Bundle arg0)
	{
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		
		setContentView(R.layout.activity_image_view);
		
		viewPager = (ViewPager)findViewById(R.id.pager_activity_image_view);
		imageViewAdapter = new ImageViewAdapter(getSupportFragmentManager());
		viewPager.setAdapter(imageViewAdapter);
		
		path = getIntent().getStringExtra("FILE_DIR");
		
		getPicList(path);
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		
		Global.imageWorker.openImageWorker(1);
		
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		
		Global.imageWorker.closeImageWorker();
	}
	
	private void getPicList(final String path)
	{
		new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				handler.sendEmptyMessage(MSG_GET_IMAGE_START);
				// TODO Auto-generated method stub
				MyFileUtil.getLocalFiles(picList, 0, MyStringUtil.getHeadByTag("/", path));
				for(int i=0;i<picList.size();i++)
				{
					MyFileInfor tempInfor = picList.get(i);
					if(tempInfor.getFileUrl().equals(path))
					{
						curPage = i;
						break;
					}
				}
				
				handler.sendEmptyMessage(MSG_GET_IMAGE_FINISH);
			}
		}).start();
	}
	
	class ImageViewAdapter extends FragmentStatePagerAdapter
	{

		/** 
		* <p>Title: </p> 
		* <p>Description: </p> 
		* @param fm 
		*/
		public ImageViewAdapter(FragmentManager fm)
		{
			super(fm);
			// TODO Auto-generated constructor stub
		}

		/**
		*callbacks
		*/
		@Override
		public Fragment getItem(int arg0)
		{
			ImageViewFragment fragment = ImageViewFragment.newInstance(picList.get(arg0), arg0);
			// TODO Auto-generated method stub
			return fragment;
		}

		/**
		*callbacks
		*/
		@Override
		public void destroyItem(ViewGroup container, int position, Object object)
		{
			ImageViewFragment fragment = (ImageViewFragment)object; 
			fragment.cancelWork();
			// TODO Auto-generated method stub
			super.destroyItem(container, position, object);
		}
		
		/**
		*callbacks
		*/
		@Override
		public int getItemPosition(Object object)
		{
			// TODO Auto-generated method stub
			return POSITION_NONE;
		}
		
		/**
		*callbacks
		*/
		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			return picList.size();
		}
	}
}
 

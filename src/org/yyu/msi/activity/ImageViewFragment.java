/*  
* @Project: MobileFileExplorer 
* @User: Android 
* @Description: 社区商服项目
* @Author： yan.yu
* @Company：http://www.neldtv.org/
* @Date 2014-4-24 上午9:29:09 
* @Version V1.0   
*/ 

package org.yyu.msi.activity; 

import org.yyu.msi.R;
import org.yyu.msi.entity.Global;
import org.yyu.msi.utils.MyFileInfor;
import org.yyu.msi.view.PowerImageView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/** 
 * @ClassName: ImageViewFragment 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-4-24 上午9:29:09  
 */
public class ImageViewFragment extends BaseFragment
{

	private MyFileInfor fileInfor = null;
	private int pageNum = 0;
	private PowerImageView imageView = null;
	
	public ImageViewFragment(MyFileInfor fileInfor, int pageNum)
	{
		this.fileInfor = fileInfor;
		this.pageNum = pageNum;
	}
	
	public static ImageViewFragment newInstance(MyFileInfor fileInfor, int pageNum)
	{
		return new ImageViewFragment(fileInfor, pageNum);
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	/**
	*callbacks
	*/
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_image_view, container, false);
		imageView = (PowerImageView)rootView.findViewById(R.id.piv_fragment_image_view);
		// TODO Auto-generated method stub
		return rootView;
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		if(getActivity() instanceof ImageViewActivity)
		{
			if(imageView != null)
				Global.imageWorker.loadBitmap(fileInfor.getFileUrl(), imageView);
		}
	}
	
	public void cancelWork()
	{
		if(imageView != null)
		{
			Global.imageWorker.cancelWork(imageView);
			imageView.setImageDrawable(null);
			imageView = null;
		}
	}
}
 

/*  
* @Project: MobileFileExplorer 
* @User: Android 
* @Description: neldtv手机云相册第二版本
* @Author： yan.yu
* @Company：http://www.neldtv.org/
* @Date 2014-4-9 下午6:17:55 
* @Version V2.0   
*/ 

package org.yyu.msi.activity; 

import org.yyu.msi.utils.MyToast;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/** 
 * @ClassName: BaseFragment 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-4-9 下午6:17:55  
 */
public class BaseFragment extends Fragment
{
	private MyToast mtToast = null;
	
	/**
	*callbacks
	*/
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		mtToast = new MyToast(getActivity());
	}
	public void showToast(String res)
	{
		mtToast.show(res);
	}
	public void showToast(int res)
	{
		mtToast.show(res);
	}
}
 

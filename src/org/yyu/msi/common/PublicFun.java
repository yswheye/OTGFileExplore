/*  
* @Project: SlideMenuFragment 
* @User: Android 
* @Description: neldtv手机云相册第二版本
* @Author： yan.yu
* @Company：http://www.neldtv.org/
* @Date 2014-3-4 上午11:44:41 
* @Version V2.0   
*/ 

package org.yyu.msi.common; 

import org.yyu.msi.R;

import android.app.Activity;
import android.view.Window;

/** 
 * @ClassName: PublicFun 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-3-4 上午11:44:41  
 */
public class PublicFun
{
	
	public void setActivityAnim(Activity activity, int id)
	{
		Window window = activity.getWindow();
		if(id == 0)
			window.setWindowAnimations(R.style.AnimLetfToRight);
			//window.setWindowAnimations(R.style.AnimTopToBottom);
		else if(id == 1)
			window.setWindowAnimations(R.style.AnimBottomToTop);
		else if(id == 2)
			window.setWindowAnimations(R.style.AnimTop);
		else if(id == 3)
			window.setWindowAnimations(R.style.AnimBottom);
		else if(id == 4)
			window.setWindowAnimations(R.style.AnimZoom);
		else if(id == 5)
			window.setWindowAnimations(R.style.AnimMoveAndZoom);
	}
}
 

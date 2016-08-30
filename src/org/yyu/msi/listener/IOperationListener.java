/*  
* @Project: MobileFileExplorer 
* @User: Android 
* @Description: 社区商服项目
* @Author： yan.yu
* @Company：http://www.neldtv.org/
* @Date 2014-4-25 下午2:07:08 
* @Version V1.0   
*/ 

package org.yyu.msi.listener; 
/** 
 * @ClassName: IOperationListener 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-4-25 下午2:07:08  
 */
public interface IOperationListener extends IDeleteListener
{
	public void onPrepare();
	
	public void onStart(long totalSize);
	
	public void onFinish(boolean cancel);
	
	public void onProgress(long progress);

	public void onSingleStart(String fileUrl);
	
	public void onSingleFinish(String fileUrl);

	public void onNoSpace();//空间不足
	
	public void onWritePermission();//没有写权限
	
}
 

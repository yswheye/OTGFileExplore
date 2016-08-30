/*  
* @Project: MobileFileExplorer 
* @User: Android 
* @Description: 社区商服项目
* @Author： yan.yu
* @Company：http://www.neldtv.org/
* @Date 2014-4-25 下午2:21:36 
* @Version V1.0   
*/ 

package org.yyu.msi.listener; 
/** 
 * @ClassName: IDeleteListener 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-4-25 下午2:21:36  
 */
public interface IDeleteListener
{
	
	public void onDeleteOne(String fileUrl, long progress);

	public void onFileNotFind(String fileUrl);
}
 

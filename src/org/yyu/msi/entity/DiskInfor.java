/*  
* @Project: SlideMenuFragment 
* @User: Android 
* @Description: neldtv手机云相册第二版本
* @Author： yan.yu
* @Company：http://www.neldtv.org/
* @Date 2014-3-1 上午9:44:09 
* @Version V2.0   
*/ 

package org.yyu.msi.entity; 
/** 
 * @ClassName: DiskInfor 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-3-1 上午9:44:09  
 */
public class DiskInfor
{
	private String diskName = "";//磁盘名称
	
	private String diskDir = "";//磁盘路径
	
	private String diskVolume = "";//磁盘容量
	
	private String diskRemain = "";//磁盘剩余空间
	
	private float progress = 0;
	
	private boolean isChecked = false;
	
	public void setDiskName(String diskName)
	{
		this.diskName = diskName;
	}
	public void setDiskDir(String diskDir)
	{
		this.diskDir = diskDir;
	}
	public void setDiskVolume(String diskVolume)
	{
		this.diskVolume = diskVolume;
	}
	public void setDiskRemain(String diskRemain)
	{
		this.diskRemain = diskRemain;
	}
	public void setDiskProgress(float percent)
	{
		this.progress = percent;
	}
	public void setIsChecked(boolean isChecked)
	{
		this.isChecked = isChecked;
	}
	
	public String getDiskName()
	{
		return diskName;
	}
	public String getDiskDir()
	{
		return diskDir;
	}
	public String getDiskVolume()
	{
		return diskVolume;
	}
	public String getDiskRemain()
	{
		return diskRemain;
	}
	public float getDiskProgress()
	{
		return progress;
	}
	public boolean isChecked()
	{
		return isChecked;
	}
}
 

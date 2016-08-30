/*  
* @Project: MyUtils 
* @User: Android 
* @Description: neldtv手机云相册第二版本
* @Author： yan.yu
* @Company：http://www.neldtv.org/
* @Date 2014-2-11 下午1:48:51 
* @Version V2.0   
*/ 

package org.yyu.msi.utils; 

import org.yyu.msi.entity.FileType;

import android.os.Parcel;
import android.os.Parcelable;


/** 
 * @ClassName: MyFileInfor 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-2-11 下午1:48:51  
 */
public class MyFileInfor implements Parcelable
{
	
	/*文件名字*/
	private String fileName = "";
	/*文件绝对路径*/
	private String fileUrl = "";
	/*文件大小*/
	private long fileSize = -1;
	
	private int fileType = 0;//文件类型
	
	private int fileImage = 0;//文件类型对应的图片
	
	private int subDirCount = -1;//子文件夹个数
	private int subFileCount = -1;//子文件个数
	
	private long modifyTime = 0;//修改时间
	
	private boolean isLoaded = false;//是否加载过
	
	private boolean isChecked = false;
	
	private boolean isHide = false;//隐藏
	
	private int[] styleIndex = null;
	
	public void setStyleIndex(int[] styleIndex)
	{
		this.styleIndex = styleIndex;
	}
	public int[] getStyleIndex()
	{
		return styleIndex;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}
	
	public void setFileUrl(String fileUrl)
	{
		this.fileUrl = fileUrl;
	}
	
	public void setFileSize(long fileSize)
	{
		this.fileSize = fileSize;
	}
	public void setFileType(int fileType)
	{
		this.fileType = fileType;
	}
	public void setFileImage(int fileImage)
	{
		this.fileImage = fileImage;
	}
	public void setSubDirCount(int subDirCount)
	{
		this.subDirCount = subDirCount;
	}
	public void setSubFileCount(int subFileCount)
	{
		this.subFileCount = subFileCount;
	}
	public void setModifyTime(long modifyTime)
	{
		this.modifyTime = modifyTime;
	}
	public void setIsLoad(boolean isLoaded)
	{
		this.isLoaded = isLoaded;
	}
	public void setIsChecked(boolean isChecked)
	{
		this.isChecked = isChecked;
	}
	public void setIsHide(boolean isHide)
	{
		this.isHide = isHide;
	}
	
	public String getFileName()
	{
		return fileName;
	}
	
	public String getFileUrl()
	{
		return fileUrl;
	}
	
	public long getFileSize()
	{
		return fileSize;
	}
	
	public int getFileType()
	{
		return fileType;
	}
	public int getFileImage()
	{
		return fileImage;
	}
	public int getSubDirCount()
	{
		return subDirCount;
	}
	public int getSubFileCount()
	{
		return subFileCount;
	}
	public long getModifyTime()
	{
		return modifyTime;
	}
	public boolean isLoaded()
	{
		return isLoaded;
	}
	public boolean isChecked()
	{
		return isChecked;
	}
	public boolean isHide()
	{
		return isHide;
	}
	
	
	public boolean isImage()
	{
		return fileType == FileType.TYPE_IMAGE;
	}
	public boolean isAudio()
	{
		return fileType == FileType.TYPE_AUDIO;
	}
	public boolean isVideo()
	{
		return fileType == FileType.TYPE_VIDEO;
	}
	public boolean isFolder()
	{
		return fileType == FileType.TYPE_FOLDER;
	}
	public boolean isText()
	{
		return fileType == FileType.TYPE_TEXT;
	}
	public boolean isZip()
	{
		return fileType == FileType.TYPE_ZIP;
	}
	public boolean isDoc()
	{
		return fileType == FileType.TYPE_DOC;
	}
	public boolean isPdf()
	{
		return fileType == FileType.TYPE_PDF;
	}
	public boolean isApk()
	{
		return fileType == FileType.TYPE_APK;
	}
	
	public static final Parcelable.Creator<MyFileInfor> CREATOR = new Creator<MyFileInfor>()
	{

		@Override
		public MyFileInfor createFromParcel(Parcel source)
		{
			// TODO Auto-generated method stub
			MyFileInfor fileInfor = new MyFileInfor();
			fileInfor.setFileName(source.readString());
			fileInfor.setFileUrl(source.readString());
			fileInfor.setFileSize(source.readLong());
			return fileInfor;
		}

		@Override
		public MyFileInfor[] newArray(int size)
		{
			// TODO Auto-generated method stub
			return new MyFileInfor[size];
		}
	};
	
	/**
	*callbacks
	*/
	@Override
	public int describeContents()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	*callbacks
	*/
	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		// TODO Auto-generated method stub
		dest.writeString(fileName);
		dest.writeString(fileUrl);
		dest.writeLong(fileSize);
	}
}
 

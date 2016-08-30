package org.yyu.msi.entity;

import java.io.File;
import java.util.List;

import org.yyu.msi.utils.MyFileInfor;
import org.yyu.msi.utils.MyFileUtil;

public class FileDeleteEntity extends FileOperation implements Runnable
{

	private long deletedSize = 0;
	
	public void startDelete()
	{
		isStop = false;
		deletedSize = 0;
		new Thread(this).start();
	}
	
	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		
		deleteFileOrFolder(getOperationList());
	}

	/**
	* @Description: 删除本地多个文件
	* @param @param files
	* @param @return   
	* @return int 0,删除成功；1，文件不存在；2，删除失败
	* @throws
	 */
	public int deleteFile(List<File> files)
	{
		int result = 0;
		for(int i=0;i<files.size();i++)
		{
			result = deleteFile(files.get(0));
			if(result != 0)
			{
				break;
			}
		}
		return result;
	}
	public int deleteFile(File file)
	{
		if(file == null || !file.exists())
			return 1;//文件不存在
		if(!file.canWrite())
			return 2;//没有写权限
		file.delete();
		return 0;
	}
	/**
	* @Description: 删除本地目录
	* @param @param file
	* @param @return   
	* @return int 0,删除成功；1，文件不存在；2，删除失败;3, 取消
	* @throws
	 */
	public int deleteFolder(File file)
	{
		if(isStop)
		{
			operationListener.onFinish(isStop);
			return 3;
		}
		if(file == null || !file.exists())
		{
			operationListener.onFileNotFind(file.getAbsolutePath());
			return 1;// 目录不存在
		}
		if(!file.canWrite())
		{
			operationListener.onWritePermission();
			return 2;//没有写权限
		}
		if(file.isFile())
		{
			deletedSize += file.length();
			file.delete();
			operationListener.onDeleteOne(file.getAbsolutePath(), deletedSize);
		}
		else if(file.isDirectory())
		{
			File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0)
            {
                file.delete();
                return 0;
            }
            for(File f : childFile)
            {
            	deleteFolder(f);
            }
            file.delete();
		}
		return 0;
	}
	
	/**
	* @Description: 删除文件或者文件夹
	* @param @param files
	* @param @return   
	* @return int 
	* @throws
	 */
	public int deleteFileOrFolder(List<MyFileInfor> files)
	{
		
		operationListener.onPrepare();
		
		int size = files.size();
		int result = 0;
		
		long total = MyFileUtil.getSize(getOperationList());
		operationListener.onStart(total);
		for(int i=0;i<size;i++)
		{
			if(isStop)
			{
				break;
			}
			MyFileInfor fileInfor = files.get(i);
			File file = new File(fileInfor.getFileUrl());
			if(!file.isDirectory())
			{
				if(!file.exists())
				{
					operationListener.onFileNotFind(file.getAbsolutePath());
					continue;
				}
				if(!file.canWrite())
				{
					operationListener.onWritePermission();
					continue;
				}
				
				deletedSize += file.length();
				file.delete();
				operationListener.onDeleteOne(file.getAbsolutePath(), deletedSize);
			}
			else
			{
				result = deleteFolder(file);
			}
		}
		operationListener.onFinish(isStop);
		return result;
	}
}

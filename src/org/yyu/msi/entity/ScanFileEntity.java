package org.yyu.msi.entity;

import java.util.List;

import org.yyu.msi.utils.MyFileInfor;

public class ScanFileEntity 
{

	private long size = 0;
	private List<MyFileInfor> list = null;
	private int folderCount = 0;
	private int fileCount = 0;
	
	public void setSize(long size)
	{
		this.size = size;
	}
	public void addSize(long size)
	{
		this.size += size;
	}
	public long getSize()
	{
		return size;
	}
	
	public void setList(List<MyFileInfor> list)
	{
		this.list = list;
	}
	public List<MyFileInfor> getList()
	{
		return list;
	}
	
	public void setFolderCount(int folderCount)
	{
		this.folderCount = folderCount;
	}
	public long getFolderCount()
	{
		return folderCount;
	}
	
	public void setFileCount(int fileCount)
	{
		this.fileCount = fileCount;
	}
	public long getFileCount()
	{
		return fileCount;
	}
}

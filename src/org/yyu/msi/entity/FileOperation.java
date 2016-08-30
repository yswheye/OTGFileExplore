package org.yyu.msi.entity;

import java.util.ArrayList;
import java.util.List;

import org.yyu.msi.listener.IOperationListener;
import org.yyu.msi.utils.MyFileInfor;

/**
 * 文件操作类：粘贴 压缩
 * @author Administrator
 *
 */
public class FileOperation
{
	private List<MyFileInfor> operateList = new ArrayList<MyFileInfor>();
	
	protected IOperationListener operationListener = null;
	
	protected boolean isStop = false;
	
	public void setOperationListener(IOperationListener operationListener)
	{
		this.operationListener = operationListener;
	}
	
	public void stop()
	{
		isStop = true;
	}
	public boolean isStop()
	{
		return isStop;
	}
	
	protected void reset()
	{
		isStop = false;
	}
	
	/**
	 * 设置所需操作的文件列表
	 * @param operateList
	 */
	public void setOperateList(List<MyFileInfor> operateList)
	{
		isStop = false;
		this.operateList.clear();
		this.operateList.addAll(operateList);
	}
	public List<MyFileInfor> getOperationList()
	{
		return operateList;
	}
}

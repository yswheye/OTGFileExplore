package org.yyu.msi.entity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.yyu.msi.utils.MyFileInfor;
import org.yyu.msi.utils.MyFileUtil;
import org.yyu.msi.utils.MyLog;
import org.yyu.msi.utils.MyStringUtil;
import org.yyu.msi.utils.MySystemUtil;

public class FileTransferEntity extends FileOperation implements Runnable
{

	private String dstDir = null;
	private String srcDir = null;
	
	private boolean isDeleteOld = false;
	private long finishedSize = 0;
	private final int UNIT_SIZE = 1024 * 4;
	
	/**
	 * 设置源文件所在目录 
	 * @param srcDir
	 */
	public void setSrcDir(String srcDir)
	{
		this.srcDir = srcDir;
	}
	public String getSrcDir()
	{
		return srcDir;
	}
	
	/**
	 * 设置目标目录
	 * @param dstDir
	 */
	public void setDstDir(String dstDir)
	{
		this.dstDir = dstDir;
	}
	public String getDstDir()
	{
		return dstDir;
	}
	
	public void startTransfer(boolean delete)
	{
		this.isDeleteOld = delete;
		finishedSize = 0;
		new Thread(this).start();
	}

	@Override
	public void run() 
	{
		
		operationListener.onPrepare();
		
		// TODO Auto-generated method stub
		long available = MySystemUtil.getAvailableExternalMemorySize();
		long requirSize = MyFileUtil.getSize(getOperationList());
		
		if(requirSize > available)
		{
			operationListener.onNoSpace();//空间不足
			return ;
		}
		
		operationListener.onStart(requirSize);
		if(requirSize == 0)
		{
			if(getOperationList().size() > 0)
				operationListener.onFileNotFind(null);
			return;
		}
		int size = getOperationList().size();
		for(int i=0;i<size;i++)
		{
			if(isStop())
				break;
			MyFileInfor fileInfor = getOperationList().get(i);
			if(fileInfor.isFolder())
			{
				copyFolder(fileInfor.getFileUrl(), dstDir, isDeleteOld);
			}
			else
			{
				copyFile(fileInfor.getFileUrl(), dstDir, isDeleteOld);
			}
		}
		operationListener.onFinish(isStop());
	}
	
	public boolean copyFolder(String oldPath, String newPath, boolean isDeleteOld) 
   { 
	   boolean isok = true;
       try 
       { 
    	   newPath = newPath + "/" + MyStringUtil.getLastByTag("/", oldPath);
    	   File newFile = new File(newPath);
    	   if(newFile.exists())//文件夹存在就创建复件
    	   {
    		   newPath = getCopyFile(newPath);
    		   newFile = new File(newPath);
    	   }
		   if(newFile.mkdir())
		   {
			   /*if(operationListener != null)
				   operationListener.onSingleFinish(newPath);*/
		   }
		   else
		   {
			   MyLog.e("create fail!");
			   return false;
		   }
           
           File oldFile = new File(oldPath); 
           String[] file = oldFile.list(); 
           File temp = null; 
           for (int i = 0; i < file.length; i++) 
           { 
        	   if(isStop)
        		   break;
        	   
               if(oldPath.endsWith(File.separator))
               { 
                   temp = new File(oldPath + file[i]); 
               } 
               else
               { 
                   temp = new File(oldPath+File.separator + file[i]); 
               } 

               if(temp.isFile())
               { 
                   FileInputStream input = new FileInputStream(temp); 
                   String tempUrl = newPath + "/" + (temp.getName()).toString();

                   if(new File(tempUrl).exists())//如果文件存在就创建复件
            	   {
                	   tempUrl = getCopyFile(tempUrl);
            	   }
                   MyFileUtil.createFile(tempUrl);
                   
                   FileOutputStream fos = new FileOutputStream(tempUrl); 
                   BufferedOutputStream bos = new BufferedOutputStream(fos);
                   
                   if(operationListener != null)
                	   operationListener.onSingleStart(tempUrl);
                   
                   byte[] b = new byte[UNIT_SIZE];
                   int len; 
                   while ( (len = input.read(b)) != -1) 
                   { 
                	   if(isStop)
                		   break;
                	   bos.write(b, 0, len); 
                	   finishedSize += len;
                	   if(operationListener != null)
                	   {
                		   operationListener.onProgress(finishedSize);
                	   }
                   } 
                   bos.flush(); 
                   bos.close(); 
                   input.close(); 
                   fos.close(); 
                   if(isDeleteOld)//删除文件
                	   MyFileUtil.deleteFile(temp);
                   if(operationListener != null)
                	   operationListener.onSingleFinish(tempUrl);
               } 
               else if(temp.isDirectory())
               {
            	   //如果是子文件夹 
                   copyFolder(oldPath + "/"+ file[i],newPath, isDeleteOld); 
               } 
           } 
           if(isDeleteOld)//删除目录
        	   MyFileUtil.deleteFolder(oldFile);
       } 
       catch (Exception e) 
       { 
    	    isok = false;
       } 
       return isok;
   }
	public boolean copyFile(String oldPath, String newPath, boolean isDelete) 
   { 
	   boolean isok = true;
       try 
       { 
           int byteread = 0; 
           File oldfile = new File(oldPath); 
           if (oldfile.exists()) 
           { 
        	   FileInputStream inStream = new FileInputStream(oldPath); //读入原文件 
        	   newPath = newPath + "/" +  MyStringUtil.getLastByTag("/", oldPath);
        	   File newFile = new File(newPath);
               if(newFile.exists())
               {
            	   newPath = getCopyFile(newPath);
               }
               MyFileUtil.createFile(newPath);
            	   
               FileOutputStream fos = new FileOutputStream(newPath); 
               BufferedOutputStream bos = new BufferedOutputStream(fos);
               
               if(isStop)
        		   return false;
               
               if(operationListener != null)
            	   operationListener.onSingleStart(newPath);
               byte[] buffer = new byte[UNIT_SIZE]; 
               while ( (byteread = inStream.read(buffer)) != -1) 
               { 
            	   if(isStop)
            		   break;
                   bos.write(buffer, 0, byteread); 
                   finishedSize += byteread;
                   if(operationListener != null)
                	   operationListener.onProgress(finishedSize);
               } 
               bos.flush(); 
               bos.close(); 
               inStream.close(); 
               fos.close();
               
               if(isDelete)
            	   oldfile.delete();
               
               if(operationListener != null)
            	   operationListener.onSingleFinish(newPath);
           }
           else
           {
        	   isok = false;
		   }
       } 
       catch (Exception e) 
       { 
           isok = false;
       } 
       return isok;
   }
		
	private String getCopyFile(String oldFile)
	{
		String flag = "复件";
		int i = 0;
		String end = MyStringUtil.getLastByTag("/", oldFile);
		String head = MyStringUtil.getHeadByTag("/", oldFile);
		String newFile = oldFile;
		while(true)
		{
			newFile = head + "/" + (flag + "(" + i + ")" + end);
			if(!new File(newFile).exists())
			{
				break;
			}
			i++;
		}
		
		return newFile;
	}	
}

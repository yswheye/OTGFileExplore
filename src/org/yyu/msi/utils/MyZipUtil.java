/*  
* @Project: MyUtils 
* @User: Android 
* @Description: neldtv手机云相册第二版本
* @Author： yan.yu
* @Company：http://www.neldtv.org/
* @Date 2014-2-26 下午1:34:29 
* @Version V2.0   
*/ 

package org.yyu.msi.utils; 

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Deflater;
import java.util.zip.ZipException;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;



/** 
 * @ClassName: MyZipUtil 
 * @Description: 文件压缩解压工具  依赖 ant.jar包
 * @author yan.yu 
 * @date 2014-2-26 下午1:34:29  
 */
public class MyZipUtil
{

	private final int BUFF_SIZE = 1024 * 1024;
	private boolean isStop = false;
	
	public void stop()
	{
		isStop = true;
	}
	public void start()
	{
		isStop = false;
	}
	/**
	* @Description: 采用apache包解压文件
	* @param @param archive
	* @param @param decompressDir
	* @param @throws IOException
	* @param @throws FileNotFoundException
	* @param @throws ZipException   
	* @return void 
	* @throws
	 */
	public void readByApacheZipFile(String archive, String decompressDir) throws IOException, FileNotFoundException, ZipException
	{
		BufferedInputStream bis = null;
		ZipFile zf = new ZipFile(archive, "GBK");
		Enumeration<?> e = zf.getEntries();
		while(e.hasMoreElements())
		{
			ZipEntry entry = (ZipEntry)e.nextElement();
			String entryName  = entry.getName();
			String path = decompressDir + "/" + entryName;
			if(entry.isDirectory())//文件类型
			{
				File decompressDirFile = new File(path);
				if (!decompressDirFile.exists()) //文件夹不存在就创建
				{
					decompressDirFile.mkdirs();
				}
			}
			else
			{
				String fileDir = path.substring(0, path.lastIndexOf("/"));//获取文件所在目录
				File fileDirFile = new File(fileDir);
				if (!fileDirFile.exists())
				{
					fileDirFile.mkdirs();
				}
				//获取输出流
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(decompressDir + "/" + entryName));
				//获取所需解压文件的输入流
				bis = new BufferedInputStream(zf.getInputStream(entry));
				byte[] readContent = new byte[1024];
				int readCount = bis.read(readContent);
				while (readCount != -1) 
				{
					bos.write(readContent, 0, readCount);
					readCount = bis.read(readContent);
				}
				bos.close();
				bis.close();
			}
		}
		zf.close();
	}
	
	
	/**
	* @Description: 
	* @param @param src
	* @param @param archive
	* @param @param comment
	* @param @throws FileNotFoundException
	* @param @throws IOException   
	* @return int 0,成功；1，找不到文件；2，空间不够;3,没有写权限
	* @throws
	 */
	public int writeByApacheZipOutputStream(ArrayList<MyFileInfor> operationList, String desPath, String comment) throws FileNotFoundException, IOException 
	{
		try
		{
			
			long available = MySystemUtil.getAvailableExternalMemorySize();
			long requirSize = 0;
			for(int i=0;i<operationList.size();i++)
			{
				requirSize = MyFileUtil.getFileSize(operationList.get(i).getFileUrl());
			}
			if(requirSize > available)
				return 2;
			
			File tempFile = new File(MyStringUtil.getHeadByTag("/", desPath));
			if(!tempFile.exists())
				tempFile.mkdirs();
			if(!tempFile.canWrite())
				return 3;
			
			File file = new File(getZipFile(desPath));
			int count = 0;
			String tag = null;
			while(file.exists())
			{
				count++;
				tag = "(" + count + ")"; 
				file = new File(getZipFile(desPath + tag));
			}
			
			CheckedOutputStream csum = new CheckedOutputStream(new FileOutputStream(file.getAbsolutePath()), new CRC32());
			ZipOutputStream zos = new ZipOutputStream(csum);
			//支持中文
			zos.setEncoding("GBK");
			//设置压缩包注释
			if(comment != null)
				zos.setComment(comment);
			//启用压缩
			zos.setMethod(ZipOutputStream.DEFLATED);
			//压缩级别为最强压缩，但时间要花得多一点
			zos.setLevel(Deflater.BEST_COMPRESSION);
			for(int i=0;i<operationList.size();i++)
			{
				if(isStop)
					break;
				writeRecursive(operationList.get(i).getFileUrl(), zos);
			}
			
			zos.close();
			csum.close();
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 1;
		}
		return 0;
	}
	private String getZipFile(String desPath)
	{
		String zipName = desPath;
		if(!zipName.endsWith(".zip"))
			zipName = zipName + ".zip";
		return zipName;
	}
	
	private void writeRecursive(String srcPath, ZipOutputStream zos) throws IOException
	{
		File srcFile = new File(srcPath);
		if(srcFile.isDirectory())
		{
			ZipEntry zipEntry = new ZipEntry(srcPath);
			String folerName = MyStringUtil.getLastByTag("/", srcPath);
			if (!"".equals(folerName))
			{
				zos.putNextEntry(zipEntry);
			}
			File srcFiles[] = srcFile.listFiles();
			for (int i = 0; i < srcFiles.length; i++) 
			{
				if(isStop)
					break;
				writeRecursive(srcFiles[i].getAbsolutePath(), zos);
			}
		}
		else
		{
			FileInputStream fis = new FileInputStream(srcPath);
			byte[] buff = new byte[BUFF_SIZE];
			String fileName = MyStringUtil.getLastByTag("/", srcPath);
			ZipEntry ze = new ZipEntry(fileName);
			zos.putNextEntry(ze);
			while (fis.read(buff) != -1)
			{
				if(isStop)
					break;
				zos.write(buff);
			}
			fis.close();
		}
	}
}
 

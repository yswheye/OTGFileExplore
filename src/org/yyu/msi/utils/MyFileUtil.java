/*  
* @Project: MyUtils 
* @User: Android 
* @Description: neldtv手机云相册第二版本
* @Author： yan.yu
* @Company：http://www.neldtv.org/
* @Date 2014-2-11 下午4:41:52 
* @Version V2.0   
*/ 

package org.yyu.msi.utils; 

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.taskdefs.condition.IsTrue;
import org.yyu.msi.activity.FileInforFragment;
import org.yyu.msi.common.Setting;
import org.yyu.msi.entity.FileType;
import org.yyu.msi.entity.ScanFileEntity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;
import android.util.Log;

/** 
 * @ClassName: MyFileUtils 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-2-11 下午4:41:52  
 */
public class MyFileUtil
{
	
	/**
	* @Description: 获取目录下的图片文件(媒体库中获取)
	* @param @param context
	* @param @return   
	* @return List<MyFileInfor> 
	* @throws
	 */
	public static List<MyFileInfor> getLocalImages(Context context)
    {
    	List<MyFileInfor> localPicList = new ArrayList<MyFileInfor>();
    	
    	int photoIndex,photoNameIndex,photoTitleIndex,photoIDIndex,photoSizeIndex,photoOrientation;
    	String columns[] = new String[]{Media.DATA,Media.TITLE,Media._ID,Media.DISPLAY_NAME,Media.SIZE,Media.ORIENTATION};
    	Cursor cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI,columns,null
    			,null,null);
    	if(null != cursor && cursor.getCount() > 0)
    	{
    		photoIndex = cursor.getColumnIndexOrThrow(Media.DATA);
        	photoNameIndex = cursor.getColumnIndexOrThrow(Media.DISPLAY_NAME);
        	photoIDIndex = cursor.getColumnIndexOrThrow(Media._ID);
        	photoTitleIndex = cursor.getColumnIndexOrThrow(Media.TITLE);
        	photoSizeIndex = cursor.getColumnIndexOrThrow(Media.SIZE);
        	photoOrientation = cursor.getColumnIndexOrThrow(Media.ORIENTATION);
        	for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
        	{
        		MyFileInfor fileInfor = new MyFileInfor();
        		String name = cursor.getString(photoNameIndex);
		    	String title = cursor.getString(photoTitleIndex);
		    	String path = cursor.getString(photoIndex);
		    	String LocalImgId = cursor.getString(photoIDIndex);
		    	long size = cursor.getLong(photoSizeIndex);
		    	float rotate = cursor.getFloat(photoOrientation);
		    	if(path.length()<5) continue;//图片名字最少4个字符
        		String folderPath = path.substring(0, path.lastIndexOf("/")); 
        		String picAlbum = null;
        		if(folderPath.contains("/"))
        			picAlbum = folderPath.substring(folderPath.lastIndexOf("/")+1);
        		else
        			picAlbum = folderPath;
        		
        		fileInfor.setFileUrl(folderPath);
        		fileInfor.setFileName(name);
        		fileInfor.setFileSize(size);
        		fileInfor.setFileUrl(path);
        		
        		File file = new File(path);
        		
        		if(file.exists() && file.length() > 0)
        		{
        			localPicList.add(0,fileInfor);
        		}
        	}
        }
    	if(cursor != null)
    		cursor.close();
        return localPicList;
    }
	
	/**
	* @Description: 获取目录下的文件(本地文件扫描方式)
	* @param @param fileList  获取的文件列表
	* @param @param type 0, 图片；1，音频；2，视频
	* @param @param path   目录路径
	* @return void 
	* @throws
	 */
	public static void getLocalFiles(final List<MyFileInfor> fileList, final int type, String path)
	{
		if(path == null || path.length() == 0)
			return ;
		File dirFile = new File(path);
		File[] files = dirFile.listFiles(new FileFilter()
		{
			@Override
			public boolean accept(File pathname)
			{
				// TODO Auto-generated method stub
				if(pathname.isDirectory() && !pathname.isHidden() && pathname.canRead())
				{
					getLocalFiles(fileList, type, pathname.getAbsolutePath());
					return false;
				}
				boolean result = false;
				if(type == 0)//图片
					result = isPicture(pathname.getAbsolutePath());
				else if(type == 1)//音频
					result = isMusic(pathname.getAbsolutePath());
				else if(type == 2)//视频
					result = isVideo(pathname.getAbsolutePath());
				else if(type == -1)
					result = true;
				MyFileInfor tempFile = new MyFileInfor();
				tempFile.setFileName(pathname.getName());
				tempFile.setFileUrl(pathname.getAbsolutePath());
				if(pathname.isDirectory())
				{
					tempFile.setFileType(FileType.TYPE_FOLDER);
					fileList.add(0, tempFile);
				}
				else
				{
					tempFile.setFileType(getFileType(pathname.getAbsolutePath()));
					if(type >= 0 && result)
					{
						fileList.add(tempFile);
					}
				}
				return false;
			}
		});
	}
	
	public static ScanFileEntity getLocalFiles(Context context, String path,
			boolean isHide, boolean isFilter)
	{
		ScanFileEntity entity = new ScanFileEntity();
		ArrayList<MyFileInfor> list = new ArrayList<MyFileInfor>();
		int folderCount = 0;
		int fileCount = 0;
		long size = 0;
		File rootFile = new File(path);
		if(rootFile.isDirectory())
		{
			File[] files = rootFile.listFiles();
			if(files != null)
			{
				for(File file : files)
				{
					if(!file.exists())
						continue;
					
					MyFileInfor fileInfor = new MyFileInfor();
					
					if(file.isHidden())//是否显示隐藏文件
					{
						if(isHide)
							fileInfor.setIsHide(true);
						else
							continue;
					}
					
					if(isPicture(file.getAbsolutePath()))//过滤小图片
					{
						if(isFilter && (file.length() < Constant.FILTER_SIZE))
							continue;
					}
					
					fileInfor.setFileUrl(file.getAbsolutePath());//文件路径
					fileInfor.setFileName(MyStringUtil.getLastByTag("/", file.getAbsolutePath()));//文件名字
					
					if(file.isDirectory())
					{
						fileInfor.setFileType(FileType.TYPE_FOLDER);//类型为文件夹
						list.add(0, fileInfor);
						folderCount ++;
					}
					else
					{
						fileInfor.setFileType(getFileType(file.getAbsolutePath()));//文件
						list.add(fileInfor);
						fileCount ++;
						size += file.length();
					}
				}
			}
		}
		entity.setList(list);
		entity.setFileCount(fileCount);
		entity.setFolderCount(folderCount);
		entity.setSize(size);
		return entity;
	}
	public static List<MyFileInfor> getLocalFiles(Context context, String path)
			{
		ArrayList<MyFileInfor> list = new ArrayList<MyFileInfor>();
		File rootFile = new File(path);
		if(rootFile.isDirectory())
		{
			File[] files = rootFile.listFiles();
			if(files != null)
			{
				for(File file : files)
				{
					
					if(!file.exists())
						continue;
					
					MyFileInfor fileInfor = new MyFileInfor();
					
					fileInfor.setFileUrl(file.getAbsolutePath());//文件路径
					fileInfor.setFileName(MyStringUtil.getLastByTag("/", file.getAbsolutePath()));//文件名字
					
					if(file.isDirectory())
					{
						fileInfor.setFileType(FileType.TYPE_FOLDER);//类型为文件夹
						list.add(0, fileInfor);
					}
					else
					{
						fileInfor.setFileType(getFileType(file.getAbsolutePath()));//文件
						list.add(fileInfor);
					}
				}
			}
		}
		return list;
			}
	
	public static boolean isFileEmpty(String fileUrl)
	{
		File file = new File(fileUrl);
		if(file.isDirectory())
		{
			File[] files = file.listFiles();
			if(files == null || files.length == 0)
				return true;
		}
		else
		{
			if(0 == file.length())
				return true;
		}
		return false;
	}
	
	private static int getFileType(String path)
	{
		int type = 0;
		if(isVideo(path))
			type = FileType.TYPE_VIDEO;
		else if(isMusic(path))
			type = FileType.TYPE_AUDIO;
		else if(isPicture(path))
			type = FileType.TYPE_IMAGE;
		else if(isZip(path))
			type = FileType.TYPE_ZIP;
		else if(isApk(path))
			type = FileType.TYPE_APK;
		else
			type = FileType.TYPE_TEXT;
		return type;
	}
	
	
	private static boolean isApk(String path)
	{
		if(path == null || path.length() == 0)
			return false;
		if(path.toLowerCase().endsWith(".apk"))
		{
			return true;
		}
		return false;
	}
	
	/**
	* @Description: 
	* @param @param path
	* @param @return   
	* @return boolean 
	* @throws
	 */
	public static boolean isVideo(String path)
	{
		if(path == null || path.length() == 0)
			return false;
		if(path.toLowerCase().endsWith(".mp4") || 
				path.endsWith(".264") ||
				path.endsWith(".3gp") ||
				path.endsWith(".avi") ||
				path.endsWith(".amv") ||
				path.endsWith(".dmv") ||
				path.endsWith(".dat") ||
				path.endsWith(".wmv") ||
				path.endsWith(".263") ||
				path.endsWith(".h264") ||
				path.endsWith(".rmvb") ||
				path.endsWith(".rm") ||
				path.endsWith(".mts") ||
				path.endsWith(".mov") ||
				path.endsWith(".mtv") ||
				path.endsWith(".flv"))
		{
			return true;
		}
		return false;
	}
	
	/**
	* @Description: MP3文件
	* @param @param path
	* @param @return   
	* @return boolean 
	* @throws
	 */
	private static boolean isMusic(String path)
	{
		if(path == null || path.length() == 0)
			return false;
		if(path.toLowerCase().endsWith(".mp3") || 
				path.endsWith(".wav") ||
				path.endsWith(".pcm") ||
				path.endsWith(".wma") ||
				path.endsWith(".aac"))
		{
			return true;
		}
		return false;
	}
	
	/**
	* @Description: 图片文件
	* @param @param path
	* @param @return   
	* @return boolean 
	* @throws
	 */
	public static boolean isPicture(String path)
	{
		if(path == null || path.length() == 0)
			return false;
		if(path.toLowerCase().endsWith(".jpg") || 
				path.endsWith(".png") ||
				path.endsWith(".bmp") ||
				path.endsWith(".gif"))
		{
			return true;
		}
		return false;
	}
	private static boolean isZip(String path)
	{
		if(path == null || path.length() == 0)
			return false;
		if(path.toLowerCase().endsWith(".zip") || 
				path.endsWith(".jar") ||
				path.endsWith(".tar") ||
				path.endsWith(".rar") ||
				path.endsWith(".cab") ||
				path.endsWith(".ace") ||
				path.endsWith(".7z") ||
				path.endsWith(".arj") ||
				path.endsWith(".lzh") ||
				path.endsWith(".uue") ||
				path.endsWith(".bz2") ||
				path.endsWith(".z") ||
				path.endsWith(".gz") ||
				path.endsWith(".iso"))
		{
			return true;
		}
		return false;
	}
	
	/**
	* @Description: 创建文件
	* @param @param fileUrl
	* @param @return   
	* @return int 
	* @throws
	 */
	public static int createFile(String fileUrl)
	{
		String tempStr = fileUrl;
		if(tempStr.contains("/"))
			tempStr = MyStringUtil.getHeadByTag("/", tempStr);
		File tempDir = new File(tempStr);
		if(!tempDir.exists())
		{
			if(!tempDir.mkdirs())
				return 1;
		}
		
		MySystemUtil.runCommand("chmod 777 " + tempDir);//给文件夹添加权限
		
		File tempFile = new File(fileUrl);
		if(tempFile.exists())
			tempFile.delete();
		try
		{
			if(tempFile.createNewFile())
				return 0;
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 1;
	}
	
	/**
	* @Description: 删除本地单个文件
	* @param @param file
	* @param @return   
	* @return int 0,删除成功；1，文件不存在；2，删除失败
	* @throws
	 */
	public static int deleteFile(File file)
	{
		if(file == null || !file.exists())
			return 1;//文件不存在
		if(!file.canWrite())
			return 2;//没有写权限
		file.delete();
		return 0;
	}
	/**
	* @Description: 删除本地多个文件
	* @param @param files
	* @param @return   
	* @return int 0,删除成功；1，文件不存在；2，删除失败
	* @throws
	 */
	public static int deleteFile(List<File> files)
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
	
	/**
	* @Description: 删除本地目录
	* @param @param file
	* @param @return   
	* @return int 0,删除成功；1，文件不存在；2，删除失败
	* @throws
	 */
	public static int deleteFolder(File file)
	{
		if(file == null || !file.exists())
			return 1;// 目录不存在
		if(!file.canWrite())
			return 2;//没有写权限
		if(file.isFile())
			file.delete();
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
	public static int deleteFileOrFolder(List<MyFileInfor> files)
	{
		int size = files.size();
		int result = 0;
		for(int i=0;i<size;i++)
		{
			MyFileInfor fileInfor = files.get(i);
			File file = new File(fileInfor.getFileUrl());
			if(!file.isDirectory())
			{
				if(file.exists() && file.canWrite())
					file.delete();
			}
			else
			{
				result = deleteFolder(file);
			}
		}
		return result;
	}
	
	/**
	* @Description: 从流中获取字节数组
	* @param @param is
	* @param @return
	* @param @throws IOException   
	* @return byte[] 
	* @throws
	 */
	public static byte[] getBytes(InputStream is) throws IOException 
	{  
		if(is == null)
			return null;
       ByteArrayOutputStream outstream = new ByteArrayOutputStream();  
       byte[] buffer = new byte[1024]; // 用数据装  
       int len = -1;  
       while ((len = is.read(buffer)) != -1) 
       {  
           outstream.write(buffer, 0, len);  
       }  
       if(outstream != null)
    	   outstream.close();  
       // 关闭流一定要记得。  
       return outstream.toByteArray();  
   }  
	
	/** 
     * 复制单个文件 
     * @param oldPath String 原文件路径 如：c:/fqf.txt 
     * @param newPath String 复制后路径 如：f:/fqf.txt 
     * @return boolean 
     */ 
   public static boolean copyFile(String oldPath, String newPath, boolean isDelete) 
   { 
	   boolean isok = true;
       try 
       { 
           int bytesum = 0; 
           int byteread = 0; 
           File oldfile = new File(oldPath); 
           if (oldfile.exists()) 
           { //文件存在时 
        	   FileInputStream inStream = new FileInputStream(oldPath); //读入原文件 
        	   newPath = newPath + "/" +  MyStringUtil.getLastByTag("/", oldPath);
        	   File newFile = new File(newPath);
               if(!newFile.exists())
            	   createFile(newPath);
               FileOutputStream fos = new FileOutputStream(newPath); 
               BufferedOutputStream bos = new BufferedOutputStream(fos);
               byte[] buffer = new byte[1024 * 4]; 
               while ( (byteread = inStream.read(buffer)) != -1) 
               { 
                   bytesum += byteread; //字节数 文件大小 
                   bos.write(buffer, 0, byteread); 
               } 
               bos.flush(); 
               bos.close(); 
               inStream.close(); 
               
               if(isDelete)
            	   oldfile.delete();
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

   /** 
     * 复制整个文件夹内容 
     * @param oldPath String 原文件路径 如：c:/fqf 
     * @param newPath String 复制后路径 如：f:/fqf/ff 
     * @return boolean 
     */ 
   public static boolean copyFolder(String oldPath, String newPath, boolean isDeleteOld) 
   { 
	   boolean isok = true;
       try 
       { 
    	   newPath = newPath + "/" + MyStringUtil.getLastByTag("/", oldPath);
    	   File newFile = new File(newPath);
    	   if(!newFile.exists())
    	   {
    		   if(newFile.mkdir())
    			   MyLog.e("create success!");
    		   else
    			   MyLog.e("create fail!");
    	   }
           
           File oldFile = new File(oldPath); 
           String[] file = oldFile.list(); 
           File temp = null; 
           for (int i = 0; i < file.length; i++) 
           { 
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
                   FileOutputStream fos = new FileOutputStream(newPath + "/" + 
                           (temp.getName()).toString()); 
                   BufferedOutputStream bos = new BufferedOutputStream(fos);
                   byte[] b = new byte[1024 * 4 ];
                   int len; 
                   while ( (len = input.read(b)) != -1) 
                   { 
                	   bos.write(b, 0, len); 
                   } 
                   bos.flush(); 
                   bos.close(); 
                   input.close(); 
                   if(isDeleteOld)//删除文件
                	   deleteFile(temp);
               } 
               if(temp.isDirectory())
               {
            	   //如果是子文件夹 
                   copyFolder(oldPath + "/"+ file[i],newPath, isDeleteOld); 
               } 
           } 
           if(isDeleteOld)//删除目录
        	   deleteFolder(oldFile);
       } 
       catch (Exception e) 
       { 
    	    isok = false;
       } 
       return isok;
   }
   
   /**
   * @Description: 文件重命名
   * @param @param oldFile
   * @param @param newFileName
   * @param @return   
   * @return int 
   * @throws
    */
   public static int renameFile(File oldFile, String newFileName)
   {
	   if(oldFile == null || !oldFile.exists() || newFileName == null || newFileName.length() == 0)
		   return 1;
	   String filePath = oldFile.getAbsolutePath();
	   String fileDir = filePath.substring(0, filePath.lastIndexOf("/"));
	   File newFile = new File(fileDir + newFileName);
	   oldFile.renameTo(newFile);
	   
	   return 0;
   }
   
   public static long getFileSize(String fileUrl)
   {

	   File file = new File(fileUrl);
	   long size = 0;
	   try 
	    {
		   if (file.exists())
		    {
			    FileInputStream fis = null;
				fis = new FileInputStream(file);
			    size = fis.available();
		    }
		    else
		    {
		    	file.createNewFile();
		    }
		}
	    catch (Exception e) 
	    {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	    return size;

   }
   
   public static long getFileSizes(String fileUrl)
   {

	   long size = 0;
	   File f = new File(fileUrl);
	   File flist[] = f.listFiles();
	   if(flist == null)
		   return 0;
	   for (int i = 0; i < flist.length; i++)
	   {
		   if (flist[i].isDirectory())
		   {
			   size = size + getFileSizes(flist[i].getAbsolutePath());
		   }
		   else
		   {
			   try {
				size =size + getFileSize(flist[i].getAbsolutePath());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   }
	   }
	
	   return size;

   }
   
   public static long getSize(List<MyFileInfor> list)
	{
		long size = 0;
		int lenght = list.size();
		for(int i=0;i<lenght;i++)
		{
			MyFileInfor fileInfor = list.get(i);
			if(fileInfor.isFolder())
				size += getFileSizes(fileInfor.getFileUrl());
			else
				size += getFileSize(fileInfor.getFileUrl());
		}
		return size;
	}
   
   /**
   * @Description: 写数据到文件中
   * @param @param filePath
   * @param @param content
   * @param @return   
   * @return int 
   * @throws
    */
   public static int writeDataToFile(String filePath, String content)
	{
		File file = new File(filePath);
		if(!file.exists())
		{
			int result = createFile(filePath);
			if(result != 0)
				return result;
		}
		
		try
		{
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = content.getBytes("utf-8");
			fos.write(buffer);
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 1;
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 1;
		}
		
		return 0;
	}
   
   public static InputStream readDataFromFile(String filePath)
   {
	   File file = new File(filePath);
	   if(!file.exists())
		   return null;
	   try
	   {
		   FileInputStream fis = new FileInputStream(file);
		   //byte[] buffer = new byte[(int) file.length()];
		   //fis.read(buffer);
		   //content = buffer.toString();
		   return fis;
	   } 
	   catch (FileNotFoundException e)
	   {
		// TODO Auto-generated catch block
		   e.printStackTrace();
		   return null;
	   } 
	   //return content;
   }
   
   /***********打开文件相关***********/
   
   public static Intent openFile(String filePath)
   {  
       File file = new File(filePath);  

       if ((file==null) || !file.exists() || file.isDirectory())
           return null;
       
       /* 取得扩展名 */  
       String end=file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length()).toLowerCase();   
       /* 依扩展名的类型决定MimeType */  
       if(end.equals("m4a")||end.equals("mp3")||end.equals("mid")||  
               end.equals("xmf")||end.equals("ogg")||end.equals("wav"))
       {  
           return getAudioFileIntent(filePath);  
       }
       else if(end.equals("3gp")||end.equals("mp4"))
       {  
           return getAudioFileIntent(filePath);  
       }
       else if(end.equals("jpg")||end.equals("gif")||end.equals("png")||  
               end.equals("jpeg")||end.equals("bmp"))
       {  
           return getImageFileIntent(filePath);  
       }
       else if(end.equals("apk"))
       {  
           return getApkFileIntent(filePath);  
       }
       else if(end.equals("ppt"))
       {  
           return getPptFileIntent(filePath);  
       }
       else if(end.equals("xls"))
       {  
           return getExcelFileIntent(filePath);  
       }
       else if(end.equals("doc"))
       {  
           return getWordFileIntent(filePath);  
       }
       else if(end.equals("pdf"))
       {  
           return getPdfFileIntent(filePath);  
       }
       else if(end.equals("chm"))
       {  
           return getChmFileIntent(filePath);  
       }
       else if(end.equals("txt"))
       {  
           return getTextFileIntent(filePath,false);  
       }
       else{  
           return getAllIntent(filePath);  
       }  
   } 
   
 //Android获取一个用于打开APK文件的intent  
   public static Intent getAllIntent( String param ) {  
 
       Intent intent = new Intent();    
       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    
       intent.setAction(android.content.Intent.ACTION_VIEW);    
       Uri uri = Uri.fromFile(new File(param ));  
       intent.setDataAndType(uri,"*/*");   
       return intent;  
   }  
   //Android获取一个用于打开APK文件的intent  
   public static Intent getApkFileIntent( String param ) {  
 
       Intent intent = new Intent();    
       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    
       intent.setAction(android.content.Intent.ACTION_VIEW);    
       Uri uri = Uri.fromFile(new File(param ));  
       intent.setDataAndType(uri,"application/vnd.android.package-archive");   
       return intent;  
   }  
 
   //Android获取一个用于打开VIDEO文件的intent  
   public static Intent getVideoFileIntent( String param ) {  
 
       Intent intent = new Intent("android.intent.action.VIEW");  
       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
       intent.putExtra("oneshot", 0);  
       intent.putExtra("configchange", 0);  
       Uri uri = Uri.fromFile(new File(param ));  
       intent.setDataAndType(uri, "video/*");  
       return intent;  
   }  
 
   //Android获取一个用于打开AUDIO文件的intent  
   public static Intent getAudioFileIntent( String param ){  
 
       Intent intent = new Intent("android.intent.action.VIEW");  
       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
       intent.putExtra("oneshot", 0);  
       intent.putExtra("configchange", 0);  
       Uri uri = Uri.fromFile(new File(param ));  
       intent.setDataAndType(uri, "audio/*");  
       return intent;  
   }  
 
   //Android获取一个用于打开Html文件的intent     
   public static Intent getHtmlFileIntent( String param ){  
 
       Uri uri = Uri.parse(param ).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(param ).build();  
       Intent intent = new Intent("android.intent.action.VIEW");  
       intent.setDataAndType(uri, "text/html");  
       return intent;  
   }  
 
   //Android获取一个用于打开图片文件的intent  
   public static Intent getImageFileIntent( String param ) {  
 
       Intent intent = new Intent("android.intent.action.VIEW");  
       intent.addCategory("android.intent.category.DEFAULT");  
       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
       Uri uri = Uri.fromFile(new File(param ));  
       intent.setDataAndType(uri, "image/*");  
       return intent;  
   }  
 
   //Android获取一个用于打开PPT文件的intent     
   public static Intent getPptFileIntent( String param ){    
 
       Intent intent = new Intent("android.intent.action.VIEW");     
       intent.addCategory("android.intent.category.DEFAULT");     
       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);     
       Uri uri = Uri.fromFile(new File(param ));     
       intent.setDataAndType(uri, "application/vnd.ms-powerpoint");     
       return intent;     
   }     
 
   //Android获取一个用于打开Excel文件的intent     
   public static Intent getExcelFileIntent( String param ){    
 
       Intent intent = new Intent("android.intent.action.VIEW");     
       intent.addCategory("android.intent.category.DEFAULT");     
       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);     
       Uri uri = Uri.fromFile(new File(param ));     
       intent.setDataAndType(uri, "application/vnd.ms-excel");     
       return intent;     
   }     
 
   //Android获取一个用于打开Word文件的intent     
   public static Intent getWordFileIntent( String param ){    
 
       Intent intent = new Intent("android.intent.action.VIEW");     
       intent.addCategory("android.intent.category.DEFAULT");     
       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);     
       Uri uri = Uri.fromFile(new File(param ));     
       intent.setDataAndType(uri, "application/msword");     
       return intent;     
   }     
 
   //Android获取一个用于打开CHM文件的intent     
   public static Intent getChmFileIntent( String param ){     
 
       Intent intent = new Intent("android.intent.action.VIEW");     
       intent.addCategory("android.intent.category.DEFAULT");     
       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);     
       Uri uri = Uri.fromFile(new File(param ));     
       intent.setDataAndType(uri, "application/x-chm");     
       return intent;     
   }     
 
   //Android获取一个用于打开文本文件的intent     
   public static Intent getTextFileIntent( String param, boolean paramBoolean){     
 
       Intent intent = new Intent("android.intent.action.VIEW");     
       intent.addCategory("android.intent.category.DEFAULT");     
       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);     
       if (paramBoolean){     
           Uri uri1 = Uri.parse(param );     
           intent.setDataAndType(uri1, "text/plain");     
       }else{     
           Uri uri2 = Uri.fromFile(new File(param ));     
           intent.setDataAndType(uri2, "text/plain");     
       }     
       return intent;     
   }    
   //Android获取一个用于打开PDF文件的intent     
   public static Intent getPdfFileIntent( String param ){     
 
       Intent intent = new Intent("android.intent.action.VIEW");     
       intent.addCategory("android.intent.category.DEFAULT");     
       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);     
       Uri uri = Uri.fromFile(new File(param ));     
       intent.setDataAndType(uri, "application/pdf");     
       return intent;     
   }  
   
   /**
    * @Description: 获取未安装apk的图标
    * @param @param context
    * @param @param apkPath
    * @param @return   
    * @return Drawable 
    * @throws
     */
    public static Drawable getApkIcon(Context context, String apkPath) 
    {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES);
        if (info != null) 
        {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try 
            {
                return appInfo.loadIcon(pm);
            } 
            catch (OutOfMemoryError e) 
            {
                Log.e("ApkIconLoader", e.toString());
            }
        }
        return null;
    }
}
 

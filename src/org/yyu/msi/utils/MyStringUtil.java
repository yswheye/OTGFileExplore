/*  
* @Project: MyUtils 
* @User: Android 
* @Description: neldtv手机云相册第二版本
* @Author： yan.yu
* @Company：http://www.neldtv.org/
* @Date 2014-2-11 上午11:54:03 
* @Version V2.0   
*/ 

package org.yyu.msi.utils; 

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;

/** 
 * @ClassName: StringUtils 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-2-11 上午11:54:03  
 */
public class MyStringUtil
{

	private static long TIMEMSUNIT = 1000;
	private static long TIMEUNIT = 60;
	
	private static long STOREUNIT = 1024;
	
	/**
	* @Description: 字符串空判断
	* @param @param value
	* @param @return   
	* @return boolean 
	* @throws
	 */
	public static boolean isEmpty(String value) 
	{
		return value == null || value.equals("");
	}

	/**
	* @Description: 根据tag将字符串分解
	* @param @param url
	* @param @return   
	* @return String[] 
	* @throws
	 */
	public static String[] splitUrls(String url, String tag) 
	{
		String[] urls = url.split(tag);
		return urls;
	}

	/**
	* @Description: 全角半角转换
	* @param @param input
	* @param @return   
	* @return String 
	* @throws
	 */
	public static String toDBC(String input) 
	{          
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) 
        {              
	        if (c[i] == 12288) 
	        {                 
		        c[i] = (char) 32;                  
		        continue;
	        }
	         if (c[i] > 65280 && c[i] < 65375)
	            c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }  
	
	/**
	* @Description: 字符过滤
	* @param @param str
	* @param @return   
	* @return String 
	* @throws
	 */
	public static String stringFilter(String str) 
	{
		str = str.replaceAll("【", "[").replaceAll("】", "]")
				.replaceAll("！", "!").replaceAll("：", ":");// 替换中文标号
		String regEx = "[『』]"; // 清除掉特殊字符
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}
	
	/**
    * @Description: 字符串转整形
    * @param @param str
    * @param @return   
    * @return int 
    * @throws
     */
    public static int stringToInt(String str)
    {
    	if(str == null)
    		return -1;
    	return Integer.parseInt(str);
    }
	
	public static String getHeadByTag(String tag, String body)
	{
		if(body == null || tag == null || tag.length() == 0 || body.length() == 0)
			return "";
		String tempStr = null;
		if(body.endsWith(tag))
			body = body.substring(0, body.lastIndexOf(tag));
		if(body.contains(tag))
		{
			tempStr = body.substring(0, body.lastIndexOf(tag));
		}
		if(tempStr == null)
			tempStr = "";
		return tempStr;
	}
	
	public static String getLastByTag(String tag, String body)
	{
		if(body == null || tag == null || tag.length() == 0 || body.length() == 0)
			return "";
		String tempStr = null;
		if(body.endsWith(tag))
			body = body.substring(0, body.lastIndexOf(tag));
		if(body.contains(tag))
		{
			tempStr = body.substring(body.lastIndexOf(tag) + 1);
		}
		if(tempStr == null)
			tempStr = "";
		return tempStr;
	}
	
	/**
	* @Description: 将字符串转为以endStr结尾
	* @param @param tag  
	* @param @param endStr
	* @param @param body
	* @param @return   
	* @return String 
	* @throws
	 */
	public static String getStringEndsWithStr(String tag, String endStr, String body)
	{
		String resulStr = body;
		if(!resulStr.endsWith(endStr))
		{
			resulStr = getHeadByTag(tag, body) + endStr;
		}
		return resulStr;
	}
	
	/**
    * @Description: 获取百分百
    * @param @param progress
    * @param @param total
    * @param @return   
    * @return int 
    * @throws
     */
    public static int getProgress(long progress, long total)
    {
    	if(total <= 0)
    		return 0;
    	float f = (float)progress/total;
    	if(f > 0)
    	{
    		BigDecimal bd = new BigDecimal(f);
    		bd = bd.setScale(2,BigDecimal.ROUND_UP);//取3.1415926小数点后面二位
    		float f1 = Float.parseFloat(bd+"");
    		float result = f1*100;
    		int l = (int)result;
    		return l;
    	}
		return 0;
    }
    public static int getProgress(int progress, int total)
    {
    	float f = (float)progress/total;
    	BigDecimal bd = new BigDecimal(f);
    	bd = bd.setScale(2,BigDecimal.ROUND_UP);//取3.1415926小数点后面二位
    	float f1 = Float.parseFloat(bd+"");
    	float result = f1*100;
    	int l = (int)result;
    	return l;
    }
    
    /**
	* @Description: 设置字符串颜色 如：
	* exam1:editText2.setText(Html.fromHtml(  "<font color=#E61A6B>红色代码</font> "+ "<i><font color=#1111EE>蓝色斜体代码</font></i>"
　　　　　　　　　　　　　　　　　　　　　　　　+"<u><i><font color=#1111EE>蓝色斜体加粗体下划线代码</font></i></u>"));
	* exam2:String temp = "name:<br /><font color=\"teal\">hello<small>title<b>activeBalance</b></small></font>"; 
	* 说明：<br />：表示换行，和“\n”一样。
			<small>content</small>：表示小字体。
			<font color=\"teal\">content</font>：设置颜色，teal是青色。
			<b>content</b>：表示粗体
			<u>content</u>：表示下横线
	* @param @param content  内容
	* @param @param color 颜色
	* @param @param bold 是否粗体
	* @param @param italic 是否斜体
	* @param @param underline 是否下划线
	* @param @param size 大小  0:small 1: big  
	* @param @return   
	* @return String  字符串的格式  通过Html.fromHtml(inforStyle)解析后再显示
	* @throws
	 */
	public static  String setTextStyle(String content, String color, boolean bold, boolean italic, boolean underline, int sizeType)
	{
		String result = "<font color=" + color + ">" + content + "</font>";
		if(bold)
			result = packageStrings("<b>", result, "</b>");
		if(italic)
			result = packageStrings("<i>", result, "</i>");
		if(underline)
			result = packageStrings("<u>", result, "</u>");
		if(sizeType == 0)
			result = packageStrings("<small>", result, "</small>");
		else if(sizeType == 1)
			result = packageStrings("<big>", result, "</big>");
		return result;
	}
	public static String packageStrings(String str1, String str, String str2)
	{
		if(str1 == null && str2 != null)
			return str + str2;
		if(str1 != null && str2 == null)
			return str1 + str;
		if(str1 == null && str2 == null)
			return null;
		return str1 + str + str2;
	}
	
	/**
    * @Description: 获取时间格式
    * @param @param time
    * @param @return   
    * @return String 
    * @throws
     */
    public static String getFormatTime(long time) 
    {
        double second = (double) time / TIMEMSUNIT;
        if (second < 1) 
        {
            return time + " MS";
        }

        double minute = second / TIMEUNIT;
        if (minute < 1) 
        {
            BigDecimal result = new BigDecimal(Double.toString(second));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " SEC";
        }

        double hour = minute / TIMEUNIT;
        if (hour < 1) 
        {
            BigDecimal result = new BigDecimal(Double.toString(minute));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " MIN";
        }

        BigDecimal result = new BigDecimal(Double.toString(hour));
        return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " H";
    }
    
    /**
	* @Description: 获取磁盘空间大小格式
	* @param @param size
	* @param @return   
	* @return String 
	* @throws
	 */
	public static String getFormatSize(double size) 
	{
        double kiloByte = size / STOREUNIT;
        if (kiloByte < 1) 
        {
            return size + " Byte";
        }

        double megaByte = kiloByte / STOREUNIT;
        if (megaByte < 1)
        {
            BigDecimal result = new BigDecimal(Double.toString(kiloByte));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " KB";
        }

        double gigaByte = megaByte / STOREUNIT;
        if (gigaByte < 1) 
        {
            BigDecimal result = new BigDecimal(Double.toString(megaByte));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " MB";
        }

        double teraBytes = gigaByte / STOREUNIT;
        if (teraBytes < 1)
        {
            BigDecimal result = new BigDecimal(Double.toString(gigaByte));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " GB";
        }
        BigDecimal result = new BigDecimal(teraBytes);
        return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " TB";
    }
    
    /**
	  * 判断是否是邮箱
	  * @param param
	  * @return
	  */
	 public static boolean isEmail(String param)
	 {
		 if(param == null)
			 return false;
		 if(param.length() <= 0)
			 return false;
		 boolean flag=false;
		 Pattern p = Pattern.compile("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$");
		 Matcher m = p.matcher(param);
		 flag=m.matches();
		 return flag;
	 } 
	 
	 /**
	* @Description: Base64加密  依赖commons-codec-1.6.jar包
	* @param @param plainText
	* @param @return   
	* @return String 
	* @throws
	 */
	public static String encodeStr(String plainText)
	{
		byte[] b=null;
		String s= null;
		try 
		{
			b = plainText.getBytes("UTF-8");
			Base64 base64=new Base64();
			b=base64.encode(b);
			s=new String(b,"UTF-8");
		}
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
		return s;
	}
	
	/**
	* @Description: Base64解密 依赖commons-codec-1.6.jar包
	* @param @param encodeStr
	* @param @return   
	* @return String 
	* @throws
	 */
	public static String decodeStr(String encodeStr)
	{
		byte[] b =null;
		String s=null;
		try
		{
			b= encodeStr.getBytes("UTF-8");
			Base64 base64=new Base64();
			b=base64.decode(b);
			s = new String(b,"UTF-8");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return s;
	}
}
 

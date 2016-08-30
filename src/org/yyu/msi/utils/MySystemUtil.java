/*
 * @Project: MyUtils
 * 
 * @User: Android
 * 
 * @Description: neldtv手机云相册第二版本
 * 
 * @Author： yan.yu
 * 
 * @Company：http://www.neldtv.org/
 * 
 * @Date 2014-2-11 下午1:41:42
 * 
 * @Version V2.0
 */

package org.yyu.msi.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.yyu.msi.entity.DiskInfor;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Environment;
import android.os.StatFs;

/**
 * @ClassName: MyStorageUtils
 * @Description: TODO
 * @author yan.yu
 * @date 2014-2-11 下午1:41:42
 */
public class MySystemUtil {

    // 执行linux命令并且输出结果
    public static Vector execRootCmd(String paramString) {
        Vector localVector = new Vector();
        try {
            Process localProcess = Runtime.getRuntime().exec("su ");// 经过Root处理的android系统即有su命令
            OutputStream localOutputStream = localProcess.getOutputStream();
            DataOutputStream localDataOutputStream = new DataOutputStream(localOutputStream);
            InputStream localInputStream = localProcess.getInputStream();
            DataInputStream localDataInputStream = new DataInputStream(localInputStream);
            String str1 = String.valueOf(paramString);
            String str2 = str1 + "\n";
            localDataOutputStream.writeBytes(str2);
            localDataOutputStream.flush();
            String str3 = localDataInputStream.readLine();
            localVector.add(str3);
            localDataOutputStream.writeBytes("exit\n");
            localDataOutputStream.flush();
            localProcess.waitFor();
            return localVector;
        } catch (Exception localException) {

            localException.printStackTrace();
        }
        return localVector;
    }

    // 执行linux命令但不关注结果输出
    public static String execRootCmdSilent(String paramString) {
        String result = "1";
        try {
            Process localProcess = Runtime.getRuntime().exec("su");
            Object localObject = localProcess.getOutputStream();
            DataOutputStream localDataOutputStream = new DataOutputStream((OutputStream) localObject);
            String str = String.valueOf(paramString);
            localObject = str + "\n";
            localDataOutputStream.writeBytes((String) localObject);
            localDataOutputStream.flush();
            localDataOutputStream.writeBytes("exit\n");
            localDataOutputStream.flush();
            localProcess.waitFor();
            localObject = localProcess.exitValue();
            // return localObject;
            result = "0";
        } catch (Exception localException) {
            localException.printStackTrace();
            result = localException.getMessage();
        }
        return result;
    }

    // 判断机器Android是否已经root，即是否获取root权限
    public static String haveRoot() {
        return execRootCmdSilent("echo test"); // 通过执行测试命令来检测
    }

    /**
     * @Description: 执行linux命令
     * @param @param command
     * @param @return
     * @return boolean
     * @throws
     */
    public static boolean runCommand(String command) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                process.destroy();
            } catch (Exception e) {}
        }
        return true;
    }

    /**
     * @Description: dip转px
     * @param @param context
     * @param @param dipValue
     * @param @return
     * @return int
     * @throws
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * @Description: px转dip
     * @param @param context
     * @param @param pxValue
     * @param @return
     * @return int
     * @throws
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * @Description: 获取屏幕大小
     * @param @param activity
     * @param @return
     * @return int[]
     * @throws
     */
    public static int[] getScreenSize(Activity activity) {
        int[] size = new int[2];

        int screenWidth = activity.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = activity.getResources().getDisplayMetrics().heightPixels;
        size[0] = screenWidth;
        size[1] = screenHeight;
        return size;
    }

    /**
     * @Description: 获取机身存储可以空间
     * @param @return
     * @return long
     * @throws
     */
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory(); // 获取数据目录
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * @Description: 获取机身总大小
     * @param @return
     * @return long
     * @throws
     */
    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * @Description: 检测是否有外部存储设备
     * @param @return
     * @return boolean
     * @throws
     */
    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * @Description: 获取外部存储设备可用空间
     * @param @return
     * @return long
     * @throws
     */
    public static long getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else {
            return getAvailableInternalMemorySize();
        }
    }

    /**
     * @Description: 获取外部存储设备总空间
     * @param @return
     * @return long
     * @throws
     */
    public static long getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        } else {
            return getTotalInternalMemorySize();
        }
    }

    public static DiskInfor getInforFromSD(String filePath) {
        DiskInfor diskInfor = new DiskInfor();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            StatFs stat = new StatFs(filePath);
            long blockSize = stat.getBlockSize();
            float totalBlocks = stat.getBlockCount();
            int sizeInMb = (int) (blockSize * totalBlocks) / 1024 / 1024;
            long availableBlocks = stat.getAvailableBlocks();
            float percent = availableBlocks / totalBlocks;
            percent = (int) (percent * 100);

            diskInfor.setDiskDir(filePath);
            diskInfor.setDiskName(MyStringUtil.getLastByTag("/", filePath));
            diskInfor.setDiskVolume(MyStringUtil.getFormatSize(totalBlocks * blockSize));
            diskInfor.setDiskRemain(MyStringUtil.getFormatSize(availableBlocks * blockSize));
            diskInfor.setDiskProgress(percent);
        }

        return diskInfor;
    }

    /**
     * @Description: 获取存储设备可用空间
     * @param @return
     * @return String
     * @throws
     */
    public static String getAvailabeMemorySize() {
        return MyStringUtil.getFormatSize(getAvailableExternalMemorySize());
    }

    /**
     * @Description: 获取存储设备总容量
     * @param @return
     * @return String
     * @throws
     */
    public static String getTotalMemorySize() {
        return MyStringUtil.getFormatSize(getTotalExternalMemorySize());
    }

    /**
     * @Description: 获取所有的存储设备列表
     * @param @return
     * @return ArrayList<String>
     * @throws
     */
    public static ArrayList<String> getStorageDirectoriesArrayList() {
        ArrayList<String> list = new ArrayList<String>();
        BufferedReader bufReader = null;
        try {
            bufReader = new BufferedReader(new FileReader("/proc/mounts"));
            list.add(Environment.getExternalStorageDirectory().getPath());
            String line;
            while ((line = bufReader.readLine()) != null) {
                if (line.contains("vfat") || line.contains("exfat") || line.contains("/mnt") || line.contains("/Removable")) {
                    StringTokenizer tokens = new StringTokenizer(line, " ");
                    String s = tokens.nextToken();
                    s = tokens.nextToken(); // Take the second token, i.e. mount point

                    if (list.contains(s)) continue;

                    if (line.contains("/dev/block/vold")) {
                        if (!line.startsWith("tmpfs") && !line.startsWith("/dev/mapper") && !s.startsWith("/mnt/secure") && !s.startsWith("/mnt/shell") && !s.startsWith("/mnt/asec") && !s.startsWith("/mnt/obb")) {
                            list.add(s);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {} catch (IOException e) {} finally {
            if (bufReader != null) {
                try {
                    bufReader.close();
                } catch (IOException e) {}
            }
        }
        return list;
    }

    /**
     * @Description: 判断当前目录是否可用
     * @param @param file
     * @param @return
     * @return boolean
     * @throws
     */
    public static boolean isStorageAvailable(File file) {
        if (getSDspace(file)[1] > 0) return true;
        return false;
    }

    /**
     * @Description: 获取本地缓存目录
     * @param @param uniqueName
     * @param @return
     * @return File
     * @throws
     */
    public static File getAvailableDir(Context context, String uniqueName) {
        /* 获取外部存储设备列表 */
        List<String> sdList = getStorageDirectoriesArrayList();

        /* 选择第一个有效的存储设备作为本地缓存 */
        for (int i = 0; i < sdList.size(); i++) {
            File file = new File(sdList.get(i));
            if (isStorageAvailable(file) && file.canWrite()) {
                if (uniqueName != null && uniqueName.length() > 0)
                    return new File(sdList.get(i) + File.separator + uniqueName);
                else
                    return new File(sdList.get(i) + File.separator);
            }
        }
        /* 如果没有外设就使用内部存储 */
        return context.getCacheDir();
    }

    /**
     * @Description: 获取本地存储设备存储空间
     * @param @param file
     * @param @return
     * @return long[]
     * @throws
     */
    public static long[] getSDspace(File file) {
        StatFs statfs = new StatFs(file.getAbsolutePath());

        long[] result = new long[3];

        long blocSize = statfs.getBlockSize();
        // 获取BLOCK数量
        long totalBlocks = statfs.getBlockCount();
        // 己使用的Block的数量
        long availaBlock = statfs.getAvailableBlocks();

        String total = MyStringUtil.getFormatSize(totalBlocks * blocSize);
        String availale = MyStringUtil.getFormatSize(availaBlock * blocSize);

        result[0] = blocSize;
        result[1] = totalBlocks;
        result[2] = availaBlock;

        return result;
    }


    /**
     * 获取当前android系统的sdk版本号
     * 
     * @return
     */
    public static int getAndroidSDKVersion() {
        int version = 0;
        version = Integer.valueOf(android.os.Build.VERSION.SDK);
        return version;
    }

    /**
     * @Description: 获取版本信息
     * @param @param activity
     * @param @return
     * @param @throws Exception
     * @return String
     * @throws
     */
    public static String getVersionName(Activity activity) throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = activity.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(activity.getPackageName(), 0);
        String version = packInfo.versionName;
        return version;
    }

    /**
     * @Description: 获取设备ip地址
     * @param @return
     * @return String
     * @throws
     */
    public static String getIP() {
        String IP = null;
        StringBuilder IPStringBuilder = new StringBuilder();
        try {
            Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaceEnumeration.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
                Enumeration<InetAddress> inetAddressEnumeration = networkInterface.getInetAddresses();
                while (inetAddressEnumeration.hasMoreElements()) {
                    InetAddress inetAddress = inetAddressEnumeration.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
                        IPStringBuilder.append(inetAddress.getHostAddress().toString() + "\n");
                    }
                }
            }
        } catch (SocketException ex) {

        }
        IP = IPStringBuilder.toString();
        return IP;
    }

    /**
     * @Description: 获取全部应用列表
     * 
     *               luncher 添加: <category android:name="android.intent.category.HOME" /> <category
     *               android:name="android.intent.category.DEFAULT" />
     * 
     * @param @param context
     * @param @return
     * @return List<ResolveInfo>
     * @throws
     */
    public static List<ResolveInfo> getAllApps(Context context) {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        return context.getPackageManager().queryIntentActivities(mainIntent, 0);
    }

    /**
     * @Description: 打开应用程序
     * @param @param context
     * @param @param info
     * @return void
     * @throws
     */
    public static void openApp(Context context, ResolveInfo info) {

        // 该应用的包名
        String pkg = info.activityInfo.packageName;
        // 应用的主activity类
        String cls = info.activityInfo.name;

        ComponentName componet = new ComponentName(pkg, cls);

        Intent i = new Intent();
        i.setComponent(componet);
        context.startActivity(i);
    }
}

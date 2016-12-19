
package com.example.mobilesafe01.utils;

import java.util.HashSet;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;

/**
 * ClassName:ProcessUtil <br/>
 * Function: 获取进程管理界面的有关数据 Date: 2016年9月16日 下午10:14:15 <br/>
 * 
 * @author ming001
 * @version
 */
public class ProcessUtil {

    /**
     * DESC : 获取正在运行进程数
     * 
     * @param context
     * @return
     */
    public static int getRunningProcessCount(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
        return appProcesses.size();
    }

    /**
     * DESC :  . 获取所有的进程数
     * @param context
     * @return
     */
    public static int getAllProcessCount(Context context) {
        // 声明一个集合承载集合 里面,可以到达去重的目的
        HashSet<String> hashSet = new HashSet<String>();
        PackageManager pm = context.getPackageManager();
        
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : installedPackages) {
            String packageName = packageInfo.packageName;
            hashSet.add(packageName);

            // 获取所有activity节点下的process
            ActivityInfo[] activities = packageInfo.activities;
            // 进行为空判断,防止空指针
            if (activities != null) {
                for (ActivityInfo activityInfo : activities) {
                    String processName = activityInfo.processName;
                    hashSet.add(processName);
                }
            }

            // 获取所有Service 节点下process
            ServiceInfo[] services = packageInfo.services;

            if (services != null) {
                for (ServiceInfo serviceInfo : services) {
                    String processName = serviceInfo.processName;
                    hashSet.add(processName);
                }
            }
            // 获取所有receiver节点下的process
            ActivityInfo[] receivers = packageInfo.receivers;

            if (receivers != null) {
                for (ActivityInfo activityInfo : receivers) {
                    String processName = activityInfo.processName;
                    hashSet.add(processName);
                }

            }
            // 获取所有provide节点下的process
            ProviderInfo[] providers = packageInfo.providers;

            if (providers != null) {
                for (ProviderInfo providerInfo : providers) {
                    String processName = providerInfo.processName;
                    hashSet.add(processName);
                }
            }
        }
        return hashSet.size();
    }
    
    /**
     * DESC :  获取内存占用的数据
     * @param context
     * @return
     */
    public static long[] getRamData(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo outInfo = new MemoryInfo();
        am.getMemoryInfo(outInfo);

        long totalMem = outInfo.totalMem;//总内存
        long availMem = outInfo.availMem;// 可用
        long useMem = totalMem - availMem;//已用
        
        long[] ramData = {totalMem,availMem,useMem};
        
        return ramData;
    }
    
    /**
     * DESC :  杀死所有后台进程
     * @param context
     */
    public static void killAllProcess(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        List<RunningAppProcessInfo> runningAppProcesses =
                am.getRunningAppProcesses();
        for (RunningAppProcessInfo info : runningAppProcesses) {
            // 如果当前进程是自己,就跳过
            if (context.getPackageName().equals(info.processName)){
                continue;
            }
            
            am.killBackgroundProcesses(info.processName);

        }
    }
}

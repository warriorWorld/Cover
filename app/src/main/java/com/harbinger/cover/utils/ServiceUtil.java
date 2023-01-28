package com.harbinger.cover.utils;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by acorn on 2022/10/31.
 */
public class ServiceUtil {
    private final static String TAG = "ServiceUtil";
    public static ActivityManager mActivityManager;
    private static List<String> homes;

    /**
     * 获得属于桌面的应用的应用包名称
     *
     * @return 返回包含所有包名的字符串列表
     */
    public static List<String> getHomes(Context context) {
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = context.getPackageManager();
        // 属性
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfo) {
            names.add(ri.activityInfo.packageName);
        }
//        if (names.size() > 0) {
//            for (int i = 0; i < names.size(); i++) {
//                Log.d(TAG, "home" + i + ":" + names.get(i));
//            }
//        }
        homes = names;
        return names;
    }

    /**
     * 判断当前界面是否是桌面
     */
    public static boolean isHome(Context context) {
        if (mActivityManager == null) {
            mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        }
        List<ActivityManager.RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
        if (rti.size() > 0) {
//            Log.d(TAG, "top:" + rti.get(0).topActivity.getPackageName());
            if (homes != null && homes.size() > 0) {
                return homes.contains(rti.get(0).topActivity.getPackageName());
            }
            return getHomes(context).contains(rti.get(0).topActivity.getPackageName());
        } else {
            Log.d(TAG, "rti is empty");
            return true;
        }
    }

    public static String getTopActivtyFromLolipopOnwards(Context context) {
        String topPackageName = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            // We get usage stats for the last 10 seconds
            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time);
            // Sort the stats by the last time used
            if (stats != null) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : stats) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    topPackageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                    Log.d(TAG, "TopPackage Name" + topPackageName);
                }
            }
        }
        return topPackageName;
    }

    public static String getTopActivity(Context context) {
        final ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityInfo aInfo = null;
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list.size() != 0) {
            ActivityManager.RunningTaskInfo topRunningTask = list.get(0);
            Log.d(TAG, "top:" + list.size() + " ," + topRunningTask.topActivity.getPackageName() + topRunningTask.topActivity.getClassName());
            return topRunningTask.topActivity.getPackageName() + topRunningTask.topActivity.getClassName();
        } else {
            return null;
        }
    }

    /**
     * 设备编码
     *
     * @return
     */
    public static String getDeviceSN() {
        String serial = "";
        try {
            Class c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serial;
    }

    public static float getV(float x) {
        return 0;
//        return (float) (Math.sqrt(PetElf.k / PetElf.m) * x * x);
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }
}

package com.yipin.basepj.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.device.DeviceManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.yipin.basepj.BaseApp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jkzhang
 * DATE : 2018/9/30
 * Description ：
 */
public class AppUtility {


    @SuppressWarnings("unused")
    private static final String TAG = "AppUtility";
    private static Context context;
    //private static Toast toast;

    public static void setContext(Application app) {
        context = app.getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }


    public static boolean isPhoneNum(String mobile) {
        if (mobile.startsWith("1") && mobile.length() == 11) {
            return true;
        }
        return false;
    }


    public static Map<String, String> getBaseParamsUploadLogMsg() {
        Map<String, String> baseParams = new HashMap<>();
        baseParams.put("platform", "Android");
        baseParams.put("appVersion", getVersionName());
        baseParams.put("packageName", getPackageName());
        baseParams.put("systemVersion", getSystemVersion());
        baseParams.put("macMode", getDeviceBrand() + "," + getSystemModel());
        baseParams.put("loginAccount", "");
        baseParams.put("machineCode", getIMSI());
        baseParams.put("sn", new DeviceManager().getDeviceId());
        return baseParams;
    }


    private static String getString(String key) {
        String result = "";
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            result = (String) get.invoke(c, key);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static String getHttpError(Throwable cause) {
        String message = "请求失败！";
        if (cause != null) {
            if (cause.toString().contains("java.net.SocketTimeoutException")) {
                message = "请求超时，请检查网络设置！";
            } else if (cause.toString().contains("java.net.ConnectException")) {
                message = "网络不给力，请检查网络设置！";
            } else if (cause.toString().contains("java.net.UnknownHostException")) {
                message = "网络未连接，请检查网络设置！";
            } else if (cause.toString().contains("Connection timed out")) {
                message = "网络异常，下载失败！";
            } else if (cause.toString().contains("Unable to resolve host")) {
                message = "网络未连接，下载失败！";
            }
        }
        return message;
    }

    /***
     * 判断网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context

                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
        } else {
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * 用来判断服务是否运行.
     *
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     */
    public static boolean isServiceRunning(String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) BaseApp.getInstance()
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(Integer.MAX_VALUE);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className)) {
                isRunning = true;
                return isRunning;
            }
        }
        return isRunning;
    }


    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机IMSI
     */
    public static String getIMSI() {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) BaseApp.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
            //获取IMSI号
            @SuppressLint("MissingPermission") String imsi = telephonyManager.getSubscriberId();
            if (null == imsi) {
                imsi = "";
            }
            return imsi;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }


    public static String getVersionName() {
        PackageManager packageManager = BaseApp.getInstance().getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        String version = "";
        try {
            packInfo = packageManager.getPackageInfo(BaseApp.getInstance().getPackageName(), 0);
            version = packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }


    public static String getPackageName() {
        PackageManager packageManager = BaseApp.getInstance().getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        String packageName = "";
        try {
            packInfo = packageManager.getPackageInfo(BaseApp.getInstance().getPackageName(), 0);
            packageName = packInfo.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageName;
    }


    public static int getVersionCode(Context mContext) throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = mContext.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
        int version = packInfo.versionCode;
        return version;
    }


    public static String getTopActivity(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        return cn.getClassName();
    }

    public static int getNetworkType(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = connManager.getActiveNetworkInfo();
        if (networkinfo == null)
            return 0;
        int type = networkinfo.getType();
        if (type == ConnectivityManager.TYPE_WIFI) {
            return 1;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            return 2;
        }
        return 0;
    }


    public static int getWeightByBarcode(String barcode) {
        String weightInfo = barcode.substring(7, 12);
        Integer weight = Integer.valueOf(weightInfo);
        return weight;
    }


}

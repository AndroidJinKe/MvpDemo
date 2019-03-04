package com.yipin.basepj.constant;

import android.os.Environment;

import com.yipin.basepj.BaseApp;

import java.io.File;

/**
 * Created by jkzhang
 * DATE : 2018/9/25
 * Description ：
 */
public class AppConstant {


    public final static String APP_ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + BaseApp.getInstance().getPackageName();
    public final static String DOWNLOAD_DIR = File.separator + "downlaod" + File.separator;

    public static final String SOCKET_TIME_OUT = "SocketTimeoutException";
    public static final String CONNECT_TIME_OUT = "Connection timed out";
    public static final String CONNECT_EXCEPTION = "ConnectException";
    public static final String UNKNOW_HOST = "UnknownHostException";
    public static final String UNABLE_TO_RESOLVE_HOST = "Unable to resolve host";

}

package com.yipin.basepj.utils;

import android.util.Log;

import com.orhanobut.logger.Logger;
import com.orhanobut.logger.LoggerListener;
import com.yipin.basepj.api.RetrofitFactory;
import com.yipin.basepj.api.base.BaseObserver;
import com.yipin.basepj.model.Data;

import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by jkzhang
 * DATE : 2018/11/5
 * Description ：
 */
public class LogUploadHelper {

    private static String TAG = LogUploadHelper.class.getSimpleName();

    private static int logUploadLine = 400;//日志上传行数
    private static LogUploadHelper sInstance;

    private int logLine;
    StringBuilder logString;


    public static LogUploadHelper getInstance() {
        if (sInstance == null) {
            synchronized (LogUploadHelper.class) {
                if (sInstance == null) {
                    sInstance = new LogUploadHelper();
                }
            }
        }
        return sInstance;
    }

    public LogUploadHelper() {
        this.logString = new StringBuilder();
        logLine = 0;
    }


    public LoggerListener getLoggerListener() {
        return mLoggerListener;
    }

    LoggerListener mLoggerListener = new LoggerListener() {
        @Override
        public void onLogger(String logMsg) {
            handlerLogger(logMsg);
        }
    };


    private void handlerLogger(String logMsg) {

        if (logLine < logUploadLine) {
            logLine++;
            logString.append(logMsg);
        }

        if (logLine == logUploadLine) {
            Log.d(TAG, "logLine: " + logLine);
            String log = logString.toString();
            logLine = 0;
            logString = new StringBuilder();
            uploadLog(log);
        }
    }


    public void uploadLog(){
        uploadLog(logString.toString());
    }


    private void uploadLog(String logString) {
        Logger.d(TAG + "start request uploadLog");
        Map<String, String> params = AppUtility.getBaseParamsUploadLogMsg();
        params.put("logMsg", logString);
        RetrofitFactory.getInstance().apiService()
                .uploadLog(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<Data>() {
                    @Override
                    protected void onSuccess(Data data) {
                        Logger.d(TAG + "： uploadLog onSuccess");
                    }

                    @Override
                    protected void onFailure(Throwable e, String errorMsg) {
                        Logger.d(TAG + "： uploadLog onFailure: " + errorMsg);
                    }

                    @Override
                    protected void onRequestStart() {
                        Logger.d(TAG + "：uploadLog onRequestStart");
                    }

                    @Override
                    protected void onRequestComplete() {
                        Logger.d(TAG + "：uploadLog onRequestComplete");

                    }
                });
    }


}

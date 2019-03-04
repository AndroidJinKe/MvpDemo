package com.yipin.basepj;

import android.app.Application;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.CsvFormatStrategy;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.yipin.basepj.utils.LogUploadHelper;

/**
 * Created by jkzhang
 * DATE : 2018/11/20
 * Description ：
 */
public class BaseApp extends Application {


    public static final String TAG = BaseApp.class.getSimpleName();

    private static BaseApp mInstance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        initLogger();
        Logger.d(TAG+": start app");
    }



    public static BaseApp getInstance() {
        return mInstance;
    }


    private void initLogger() {
        PrettyFormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)
                .methodCount(0)
                .methodOffset(2)
                .tag(TAG)
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));//根据上面的格式设置logger相应的适配器
        CsvFormatStrategy fileFormatStrategy = CsvFormatStrategy.newBuilder()
                .tag(TAG)
                .loggerListener(LogUploadHelper.getInstance().getLoggerListener())
                .build();
        Logger.addLogAdapter(new DiskLogAdapter(fileFormatStrategy));//保存到文件
    }


}

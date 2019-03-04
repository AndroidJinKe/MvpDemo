package com.yipin.basepj.api.interceptor;

import com.orhanobut.logger.Logger;

/**
 * Created by jkzhang
 * DATE : 2018/9/29
 * Description ：
 */

public class InterceptorUtil {
    public static String TAG = InterceptorUtil.class.getSimpleName() + ": ";

    //日志拦截器
    public static HttpLoggingInterceptor LogInterceptor() {
        return new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Logger.d(TAG + message);
            }
        }).setLevel(HttpLoggingInterceptor.Level.BODY);//设置打印数据的级别
    }


}

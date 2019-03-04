package com.yipin.basepj.api;

import com.yipin.basepj.api.config.HttpConfig;
import com.yipin.basepj.api.interceptor.InterceptorUtil;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jkzhang
 * DATE : 2018/9/21
 * Description ：
 */
public class RetrofitFactory {

    private static RetrofitFactory mRetrofitFactory;
    private static  ApiService mApiService;
    private RetrofitFactory(){
        OkHttpClient mOkHttpClient=new OkHttpClient.Builder()
                .connectTimeout(HttpConfig.HTTP_TIME, TimeUnit.SECONDS)
                .readTimeout(HttpConfig.HTTP_TIME, TimeUnit.SECONDS)
                .writeTimeout(HttpConfig.HTTP_TIME, TimeUnit.SECONDS)
                .addInterceptor(InterceptorUtil.LogInterceptor())
                .build();
        Retrofit mRetrofit=new Retrofit.Builder()
                .baseUrl(HttpConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())//添加gson转换器
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//添加rxjava转换器
                .client(mOkHttpClient)
                .build();
        mApiService=mRetrofit.create(ApiService.class);

    }

    public static RetrofitFactory getInstance(){
        if (mRetrofitFactory==null){
            synchronized (RetrofitFactory.class) {
                if (mRetrofitFactory == null)
                    mRetrofitFactory = new RetrofitFactory();
            }

        }
        return mRetrofitFactory;
    }
    public  ApiService apiService(){
        return mApiService;
    }

}

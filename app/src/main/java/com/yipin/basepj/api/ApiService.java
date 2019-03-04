package com.yipin.basepj.api;

import com.yipin.basepj.api.base.BaseResponse;
import com.yipin.basepj.model.Data;
import com.yipin.basepj.model.SampleEntity;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by jkzhang
 * DATE : 2018/9/21
 * Description ：
 */
public interface ApiService {


    /**
     * 示例
     *
     * https://www.apiopen.top/satinApi?type=1&page=1
     *
     * @param type
     * @param page
     * @return
     */
    @POST("satinApi?")
    Observable<BaseResponse<List<SampleEntity>>> getSampleData(@Query("type") int type, @Query("page") int page);

    /**
     * 检查更新接口
     *
     * @param
     * @return   http://192.168.43.23:9090/app/update
     */
    @Headers({"public_api:public"})
    @POST("/app/insertLog")
    Observable<BaseResponse<Data>> uploadLog(@Body Map<String, String> params);

    /**
     * @param start 从某个字节开始下载数据
     * @param url   文件下载的url
     * @return Observable
     * @Streaming 这个注解必须添加，否则文件全部写入内存，文件过大会造成内存溢出
     */
    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Header("RANGE") String start, @Url String url);


}

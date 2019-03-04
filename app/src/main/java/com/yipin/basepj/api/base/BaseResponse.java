package com.yipin.basepj.api.base;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by jkzhang
 * DATE : 2018/9/21
 * Description ：
 */
public class BaseResponse<T> implements Serializable {

    private static String SUCCESS_CODE = "200";//成功的code

    private String code;//
    private String msg;
    private T data;


    public boolean isSuccess() {
        return TextUtils.equals(getCode(), SUCCESS_CODE);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}

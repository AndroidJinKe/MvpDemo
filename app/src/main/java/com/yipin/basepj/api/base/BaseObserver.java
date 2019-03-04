package com.yipin.basepj.api.base;


import android.accounts.NetworkErrorException;

import com.orhanobut.logger.Logger;
import com.yipin.basepj.BaseApp;
import com.yipin.basepj.R;
import com.yipin.basepj.constant.AppConstant;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by jkzhang
 * DATE : 2018/9/21
 * Description ：
 */
public abstract class BaseObserver<T> implements Observer<BaseResponse<T>> {


    @Override
    public void onSubscribe(Disposable d) {
        onRequestStart();
    }

    @Override
    public void onNext(BaseResponse<T> baseResponse) {
        onRequestComplete();
        if (baseResponse.isSuccess()) {
            try {
                onSuccess(baseResponse.getData());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                onFailure(null, baseResponse.getMsg());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onError(Throwable e) {
        Logger.e(e.toString());
        onRequestComplete();
        try {
            if (e instanceof ConnectException
                    || e instanceof TimeoutException
                    || e instanceof NetworkErrorException
                    || e instanceof UnknownHostException
                    || e instanceof SocketTimeoutException) {
                onFailure(e, getHttpError(e));
            } else {
                onFailure(e, BaseApp.getInstance().getString(R.string.api_system_exception));
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onComplete() {

    }


    /**
     * 开始请求的回调
     */
    protected abstract void onRequestStart();

    /**
     * 返回成功
     *
     * @param t
     * @throws Exception
     */
    protected abstract void onSuccess(T t);


    /**
     * 返回失败
     *
     * @param e
     * @param errorMsg 错误信息
     * @throws Exception
     */
    protected abstract void onFailure(Throwable e, String errorMsg);

    /**
     * 请求结束回调
     */
    protected abstract void onRequestComplete();


    private String getHttpError(Throwable cause) {
        String message = BaseApp.getInstance().getString(R.string.request_fail);
        if (cause != null) {
            if (cause.toString().contains(AppConstant.SOCKET_TIME_OUT)
                    || cause.toString().contains(AppConstant.CONNECT_TIME_OUT)
                    || cause.toString().contains(AppConstant.CONNECT_EXCEPTION)) {
                message = BaseApp.getInstance().getString(R.string.net_work_not_good);
            } else if (cause.toString().contains(AppConstant.UNKNOW_HOST)
                    || cause.toString().contains(AppConstant.UNABLE_TO_RESOLVE_HOST)) {
                message = BaseApp.getInstance().getString(R.string.un_connect_net);
            }
        }
        return message;
    }


}

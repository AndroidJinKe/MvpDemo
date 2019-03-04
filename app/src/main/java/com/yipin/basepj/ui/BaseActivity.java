package com.yipin.basepj.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.yipin.basepj.R;
import com.yipin.basepj.download.DownloadListener;
import com.yipin.basepj.event.OnCheckVersionEvent;
import com.yipin.basepj.model.CheckVersionEntity;
import com.yipin.basepj.utils.ActivityManager;
import com.yipin.basepj.view.LoadingDialog;
import com.yipin.basepj.view.UpdateDownloadDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by jkzhang
 * DATE : 2018/9/22
 * Description ：
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected String TAG;

    protected Context mContext;
    private LoadingDialog mLoadingDialog;
    private UpdateDownloadDialog mDownloadDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        TAG = getClassName() + ": ";
        ActivityManager.getInstance().addActivity(this);
        Logger.d(getClassName() + ": onCreate");
    }


    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
        initView();
    }


    /**
     * 请求数据的线程转换
     *
     * @return
     */
    public ObservableTransformer schedulersTransformer() {
        return new ObservableTransformer() {
            @Override
            public ObservableSource apply(Observable upstream) {
                return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    protected abstract void initView();

    protected String getClassName() {
        String className = this.getClass().getName();
        className = className.substring(className.lastIndexOf(".") + 1);
        return className;
    }


    protected void showLoading() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            return;
        }
        mLoadingDialog = new LoadingDialog(mContext, R.style.LoadingDialog);
        mLoadingDialog.show();
    }

    protected void dismissLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCheckVersionEvent(OnCheckVersionEvent event) {
        showDownloadDialog(event.checkVersionEntity);
    }


    private void showDownloadDialog(CheckVersionEntity versionEntity) {
        if (mDownloadDialog != null && mDownloadDialog.isShowing()) {
            return;
        }
        mDownloadDialog = new UpdateDownloadDialog(this, R.style.CommonDialog)
                .setDownloadUrl(versionEntity.url)
                .setDownloadDesc(versionEntity.updateDesc)
                .setDownloadForce(versionEntity.forceUpdate)
                .setDownloadListener(mDownloadListener);
        mDownloadDialog.show();
    }


    DownloadListener mDownloadListener = new DownloadListener() {
        @Override
        public void onDownloadStart(int progress) {
            Logger.d(TAG+"onDownloadStart：  " + progress);
            updateProgress(progress);
        }

        @Override
        public void onProgress(int progress) {
            Logger.d(TAG+"onProgress：  " + progress);
            updateProgress(progress);
        }

        @Override
        public void onCompleted(File downloadFile) {
            Logger.d(TAG+"onCompleted");
            mDownloadDialog.refreshDownloadComplete();
            installApp(downloadFile);
        }

        @Override
        public void onError(String msg) {
            Logger.d(TAG+"onError:  " + msg);
            showToast(mContext, msg);
            dismissDownloadDialog();
        }
    };


    private void installApp(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }


    private void dismissDownloadDialog() {
        if (mDownloadDialog != null) {
            mDownloadDialog.dismiss();
        }
    }

    private void updateProgress(int progress) {
        if (mDownloadDialog != null && mDownloadDialog.isShowing()) {
            mDownloadDialog.refreshDownloadProgress(progress);
        }
    }


    protected void showToast(Context context, String string) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Logger.d(TAG + "点击了返回键");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.d(TAG + "onStop");
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.d(TAG + "onResume");
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.d(TAG + "onDestroy");
    }
}

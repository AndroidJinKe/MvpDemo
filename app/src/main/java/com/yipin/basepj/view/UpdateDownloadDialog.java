package com.yipin.basepj.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.yipin.basepj.BaseApp;
import com.yipin.basepj.R;
import com.yipin.basepj.constant.AppConstant;
import com.yipin.basepj.download.DownloadListener;
import com.yipin.basepj.download.DownloadManager;
import com.yipin.basepj.utils.AppPrefs;
import com.yipin.basepj.utils.handler.Dispatch;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jkzhang
 * DATE : 2018/10/27
 * Description ：
 */
public class UpdateDownloadDialog extends BaseDialog {

    private String mDownloadUrl;
    private boolean forceUpdate;

    private TextView updateDescTv;
    private LinearLayout downloadProgressLayout;
    private TextView downloadSure;
    private TextView downloadProgressTv;
    private TextView downloadDelayTv;
    private ProgressBar downloadPb;
    private LinearLayout downloadLayout;
    private View verticlaDivider;

    private DownloadManager mDownloadManager;
    private DownloadListener mDownloadListener;


    public UpdateDownloadDialog(Context context, int style) {
        super(context, style);
    }

    public UpdateDownloadDialog setDownloadUrl(String mDownloadUrl) {
        this.mDownloadUrl = mDownloadUrl;
        String downloadTip = BaseApp.getInstance().getString(haveDownloadCom() ? R.string.install_immediately : R.string.update_immediately);
        downloadSure.setText(downloadTip);
        return this;
    }

    public UpdateDownloadDialog setDownloadDesc(String downloadDesc) {
        updateDescTv.setText(downloadDesc);
        return this;
    }

    public UpdateDownloadDialog setDownloadForce(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
        downloadDelayTv.setVisibility(forceUpdate ? View.GONE : View.VISIBLE);
        verticlaDivider.setVisibility(forceUpdate ? View.GONE : View.VISIBLE);
        return this;
    }


    public UpdateDownloadDialog setDownloadListener(DownloadListener mDownloadListener) {
        this.mDownloadListener = mDownloadListener;
        return this;
    }

    @Override
    protected View getDefaultView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_download_pop, null);
        ButterKnife.bind(this, view);
        updateDescTv = view.findViewById(R.id.update_desc);
        downloadSure = view.findViewById(R.id.download_sure_force);
        downloadProgressTv = view.findViewById(R.id.progress_tv);
        downloadPb = view.findViewById(R.id.progress_bar);
        downloadProgressLayout = view.findViewById(R.id.progress_layout);
        downloadLayout = view.findViewById(R.id.download__layout);
        downloadDelayTv = view.findViewById(R.id.download__delay);
        verticlaDivider = view.findViewById(R.id.vertical_divider);
        return view;
    }


    @OnClick({R.id.download_sure_force, R.id.download__delay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.download_sure_force:
                Dispatch.getInstance().post(downloadRunnable);
                break;
            case R.id.download__delay:
                dismiss();
                break;
        }
    }

    Runnable downloadRunnable = new Runnable() {
        @Override
        public void run() {
            startDownload();
        }
    };


    public void refreshDownloadProgress(int progress) {
        downloadProgressLayout.setVisibility(View.VISIBLE);
        downloadLayout.setVisibility(View.GONE);
        downloadProgressTv.setText(progress + "%");
        downloadPb.setProgress(progress);
    }


    public void refreshDownloadComplete() {
        downloadProgressLayout.setVisibility(View.GONE);
        downloadLayout.setVisibility(View.VISIBLE);
        downloadSure.setText(BaseApp.getInstance().getString(R.string.install_immediately));
    }


    private void startDownload() {
        int position = getSpitDownloadUrlPosition(mDownloadUrl);
        String baseDownloadUrl = mDownloadUrl.substring(0, position + 1);
        String downloadUrlEnd = mDownloadUrl.substring(position + 1);
        String downloadFileName = mDownloadUrl.substring(mDownloadUrl.lastIndexOf('/') + 1);
        Logger.d("downloadUrl: " + mDownloadUrl + ", downloadFileName: " + downloadFileName);
        final File downloadFile = new File(AppConstant.APP_ROOT_PATH + AppConstant.DOWNLOAD_DIR + downloadFileName);
        long range = 0;
        if (downloadFile.exists()) {
            range = AppPrefs.get(BaseApp.getInstance()).getLong(downloadUrlEnd, 0);
            boolean downloadComplete = range == downloadFile.length();
            Logger.d("downloadComplete: " + downloadComplete);
            if (downloadComplete) {
                Logger.d("downloadComplete: true start install");
                Dispatch.getInstance().postByUIThread(new Runnable() {
                    @Override
                    public void run() {
                        mDownloadListener.onCompleted(downloadFile);
                    }
                });
                return;
            }
        }
        mDownloadManager = new DownloadManager(baseDownloadUrl);
        mDownloadManager.setDownloadListener(mDownloadListener);
        mDownloadManager.downloadFile(range, downloadUrlEnd, downloadFileName);
    }


    private boolean haveDownloadCom() {
        int position = getSpitDownloadUrlPosition(mDownloadUrl);
        String downloadUrlEnd = mDownloadUrl.substring(position + 1);
        String downloadFileName = mDownloadUrl.substring(mDownloadUrl.lastIndexOf('/') + 1);
        Logger.d("downloadUrl: " + mDownloadUrl + ", downloadFileName: " + downloadFileName);
        File downloadFile = new File(AppConstant.APP_ROOT_PATH + AppConstant.DOWNLOAD_DIR + downloadFileName);
        if (downloadFile.exists()) {
            long range = AppPrefs.get(BaseApp.getInstance()).getLong(downloadUrlEnd, 0);
            boolean downloadComplete = range == downloadFile.length();
            return downloadComplete;
        }
        return false;
    }


    private int getSpitDownloadUrlPosition(String apkUrl) {
        int position = -1;
        if (!TextUtils.isEmpty(apkUrl) && apkUrl.contains("//")) {
            String[] splitUrl = apkUrl.split("//");
            if (splitUrl.length == 2) {
                position = splitUrl[1].indexOf("/");
                position = splitUrl[0].length() + 2 + position;
                return position;
            }
        }
        return position;
    }

}

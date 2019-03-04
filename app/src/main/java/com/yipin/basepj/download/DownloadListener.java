package com.yipin.basepj.download;

import java.io.File;

/**
 * Created by jkzhang
 * DATE : 2018/10/25
 * Description ：下载监听
 */
public interface DownloadListener {

    void onDownloadStart(int progress);


    void onProgress(int progress);

    void onCompleted(File downloadFile);

    void onError(String msg);

}

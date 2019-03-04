package com.yipin.basepj.download;

import com.orhanobut.logger.Logger;
import com.yipin.basepj.BaseApp;
import com.yipin.basepj.api.ApiService;
import com.yipin.basepj.constant.AppConstant;
import com.yipin.basepj.utils.AppPrefs;
import com.yipin.basepj.utils.AppUtility;
import com.yipin.basepj.utils.handler.Dispatch;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jkzhang
 * DATE : 2018/10/25
 * Description ：
 */
public class DownloadManager {

    private static final int DEFAULT_TIMEOUT = 10;

    private DownloadListener mDownloadListener;

    private ApiService apiService;


    public DownloadManager(String baseUrl) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public void setDownloadListener(DownloadListener mDownloadListener) {
        this.mDownloadListener = mDownloadListener;
    }


    public void downloadFile(final long range, final String url, final String fileName) {
        //断点续传时请求的总长度
        File file = new File(AppConstant.APP_ROOT_PATH + AppConstant.DOWNLOAD_DIR, fileName);
        String totalLength = "-";
        if (file.exists()) {
            totalLength += file.length();
        }
        apiService.downloadFile("bytes=" + Long.toString(range) + totalLength, url)
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Logger.d("downloadFile onSubscribe");
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        Logger.d("downloadFile onNext");
                        if (mDownloadListener != null) {
                            Dispatch.getInstance().postByUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    mDownloadListener.onDownloadStart(0);
                                }
                            });
                        }
                        saveFile(range, responseBody, fileName, url);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.d("onError: " + e.getMessage());
                        String errorMsg = AppUtility.getHttpError(e);
                        if (mDownloadListener != null) {
                            mDownloadListener.onError(errorMsg);
                        }
                    }

                    @Override
                    public void onComplete() {
                        Logger.d("downloadFile onComplete");
                    }
                });
    }


    private void saveFile(long range, ResponseBody responseBody, String fileName, String url) {
        RandomAccessFile randomAccessFile = null;
        InputStream inputStream = null;
        long total = range;
        long responseLength = 0;
        try {
            byte[] buf = new byte[2 * 1024];
            int len = 0;
            responseLength = responseBody.contentLength();
            inputStream = responseBody.byteStream();
            String filePath = AppConstant.APP_ROOT_PATH + AppConstant.DOWNLOAD_DIR;
            final File file = new File(filePath, fileName);
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            randomAccessFile = new RandomAccessFile(file, "rwd");
            if (range == 0) {
                randomAccessFile.setLength(responseLength);
            }
            randomAccessFile.seek(range);

            int progress = 0;
            int lastProgress = 0;

            while ((len = inputStream.read(buf)) != -1) {
                randomAccessFile.write(buf, 0, len);
                total += len;
                lastProgress = progress;
                progress = (int) (total * 100 / randomAccessFile.length());
                if (progress > 0 && progress != lastProgress) {
                    if (mDownloadListener != null) {
                        final int finalProgress = progress;
                        Dispatch.getInstance().postByUIThread(new Runnable() {
                            @Override
                            public void run() {
                                mDownloadListener.onProgress(finalProgress);
                            }
                        });

                    }
                }
            }
            if (mDownloadListener != null) {
                Dispatch.getInstance().postByUIThread(new Runnable() {
                    @Override
                    public void run() {
                        mDownloadListener.onCompleted(file);
                    }
                });
            }
        } catch (final Exception e) {
            Logger.d(e.getMessage());
            if (mDownloadListener != null) {
                Dispatch.getInstance().postByUIThread(new Runnable() {
                    @Override
                    public void run() {
                        mDownloadListener.onError(e.getMessage());
                    }
                });
            }
            e.printStackTrace();
        } finally {
            try {
                AppPrefs.get(BaseApp.getInstance()).putLong(url, total);
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


}

package com.yipin.basepj.utils.handler;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Dispatch {

    private static String TAG = Dispatch.class.getSimpleName();
    private static Dispatch dispatch;
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private Handler mUiHandler;
    private HandlerThread mExecutorsThread;
    private Handler mExecutorsHandler;
    private MessageEntity mUiMessage;
    private MessageEntity mHandlerThreadMessage;
    private ExecutorService mExecutorService;

    private Dispatch() {
        mExecutorService = Executors.newFixedThreadPool(5);
        mUiMessage = new MessageEntity();
        mHandlerThreadMessage = new MessageEntity();
        mHandlerThread = new HandlerThread(TAG);
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                HandleListener listener = mHandlerThreadMessage.takeMessage(msg);
                if (null != listener) {
                    listener.handleMessage((Message) msg.obj);
                } else {
                    Log.e(TAG, "listener is null");
                }
            }
        };
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_DEFAULT);
            }
        });
        mUiHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                HandleListener listener = mUiMessage.takeMessage(msg);
                if (null != listener) {
                    listener.handleMessage((Message) msg.obj);
                }
            }

        };
        mUiHandler.post(new Runnable() {

            @Override
            public void run() {
                android.os.Process
                        .setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_DISPLAY);
            }
        });
        mExecutorsThread = new HandlerThread("ExecutorsThread");
        mExecutorsThread.start();
        mExecutorsHandler = new Handler(mExecutorsThread.getLooper()) {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mExecutorService.execute((Runnable) msg.obj);
            }

        };
        mExecutorsHandler.post(new Runnable() {

            @Override
            public void run() {
                android.os.Process
                        .setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            }
        });
    }

    /**
     * getInstance:获取Dispatch单例对象. <br/>
     *
     * @return
     */
    public static Dispatch getInstance() {
        if (dispatch == null) {
            dispatch = new Dispatch();
        }
        return dispatch;
    }


    public void sendMessage(int what,
                            HandleListener handleListener) {
        Message msg = Message.obtain();
        msg.what = what;
        this.sendMessage(msg, handleListener);
    }

    public void sendMessage(Message msg,
                            HandleListener handleListener) {
        this.sendMessage(msg, TAG, handleListener);
    }


    public void sendMessage(Message msg, String tag,
                            HandleListener handleListener) {
        this.sendMessageDelayed(msg, 0, tag, handleListener);
    }

    /**
     * sendMessageDelayed:通过Handler，进入ui线程处理消息. <br/>
     *
     * @param msg            消息参数
     * @param delayMillis    延时执行的时间
     * @param tag            消息的类别
     * @param handleListener 执行的回调方法
     */
    public void sendMessageDelayed(Message msg, long delayMillis, String tag,
                                   HandleListener handleListener) {
        if (handleListener == null) {
            return;
        }
        Message newMessage = mHandlerThreadMessage.obtainMessage(msg, tag, handleListener);
        mHandler.sendMessageDelayed(newMessage, delayMillis);
    }

    /**
     * sendMessageDelayed:通过Handler，进入ui线程处理消息. <br/>
     *
     * @param msg            消息参数
     * @param delayMillis    延时执行的时间
     * @param tag            消息的类别
     * @param handleListener 执行的回调方法
     */
    public void sendMessageDelayedUiThread(Message msg, long delayMillis,
                                           String tag, HandleListener handleListener) {
        if (handleListener == null) {
            Log.e(TAG, tag + " : HandleListener is null");
            return;
        }
        Message newMessage = mUiMessage.obtainMessage(msg, tag, handleListener);
        mUiHandler.sendMessageDelayed(newMessage, delayMillis);
    }

    /**
     * removeMessage:删除队列中所有tag类别的消息. <br/>
     *
     * @param tag
     */
    public void removeMessage(String tag) {
        mHandlerThreadMessage.removeMessage(tag);
        mUiMessage.removeMessage(tag);
    }

    /**
     * 抛到指定线程执行
     * @param r
     */
    public void post(Runnable r) {
        postDelayed(r,0);
    }
    /**
     * postDelayed:延迟调用线程. <br/>
     *
     * @param r
     * @param delayMillis
     */
    public void postDelayed(Runnable r, long delayMillis) {
        mHandler.postDelayed(r, delayMillis);
    }

    /**
     * removeRunnable:去掉队列中待执行的线程. <br/>
     *
     * @param r
     */
    public void removeRunnable(Runnable r) {
        mHandler.removeCallbacks(r);
    }



    /**
     * postByUIThread:在UI线程中调用 <br/>
     * @param r
     */
    public void postByUIThread(Runnable r) {
        postDelayedByUIThread(r,0);
    }

    /**
     * postDelayedInUIThread:在UI线程中延迟调用 <br/>
     *
     * @param r
     * @param delayMillis
     */
    public void postDelayedByUIThread(Runnable r, long delayMillis) {
        mUiHandler.postDelayed(r, delayMillis);
    }

    /**
     * removeRunnable:去掉UI队列中待执行的线程. <br/>
     *
     * @param r
     */
    public void removeRunnableByUIThread(Runnable r) {
        mUiHandler.removeCallbacks(r);
    }

    public void postRunnableByExecutors(Runnable r, long delayMillis) {
        mExecutorsHandler.postDelayed(r, delayMillis);
    }

    public void removeRunnableByExecutors(Runnable r) {
        mExecutorsHandler.removeCallbacks(r);
    }
}

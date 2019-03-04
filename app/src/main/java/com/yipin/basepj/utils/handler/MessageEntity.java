package com.yipin.basepj.utils.handler;

import android.os.Message;
import android.util.Log;


import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class MessageEntity {

    private static final String TAG = "MessageEntity";
    private static Integer MAX_LIST_SIZE = 1000;
    private ConcurrentHashMap<Integer, HandleListener> mHandlerListenerMap;
    private ConcurrentHashMap<String, ArrayList<Integer>> mTagListMap;
    private ConcurrentHashMap<Integer, String> mIdtoTagListMap;
    private Integer mMessageCount = 0;
    private Object mLock = new Object();

    public MessageEntity() {
        mHandlerListenerMap = new ConcurrentHashMap<Integer, HandleListener>();
        mTagListMap = new ConcurrentHashMap<String, ArrayList<Integer>>();
        mIdtoTagListMap = new ConcurrentHashMap<Integer, String>();
    }

    public synchronized Message obtainMessage(Message msg, String tag,
                                              HandleListener handleListener) {
        if (mHandlerListenerMap.size() == MAX_LIST_SIZE) {
            Log.e(TAG, "Dispatch message is over size !");
            return null;
        }
        //synchronized (mLock) {
            Message newMessage = new Message();
            newMessage.what = getMessageId();
            newMessage.obj = msg;
            mHandlerListenerMap.put(newMessage.what, handleListener);
            if (mTagListMap.contains(tag)) {
                mTagListMap.get(tag).add(newMessage.what);
            } else {
                ArrayList<Integer> messageIdList = new ArrayList<Integer>();
                messageIdList.add(newMessage.what);
                mTagListMap.put(tag, messageIdList);
            }
            mIdtoTagListMap.put(newMessage.what, tag);
            return newMessage;
        //}
    }

    private Integer getMessageId() {
        // DebugLog.d("msg", "msg " + mMessageCount);
        if (++mMessageCount == Integer.MAX_VALUE) {
            mMessageCount = 0;
        }
        while (mHandlerListenerMap.contains(mMessageCount)) {
            ++mMessageCount;
        }
        return mMessageCount;
    }

    public synchronized HandleListener takeMessage(Message message) {
        //synchronized (mLock) {
            HandleListener listenser = null;
            if (mHandlerListenerMap.containsKey(message.what)) {
                listenser = mHandlerListenerMap.get(message.what);
                String tag = mIdtoTagListMap.get(message.what);
                if (listenser == null || tag == null) {
                    Log.e(TAG, "ConcurrentHashMap object is not exist !");
                    return null;
                }
                if (!mTagListMap.containsKey(tag)) {
                    Log.e(TAG, "ConcurrentHashMap object is not exist !");
                    return null;
                }
                mTagListMap.get(tag).remove((Object) message.what);
                mHandlerListenerMap.remove(message.what);
                mIdtoTagListMap.remove(message.what);
            }
            return listenser;
        //}
    }
    
    /**
     * 
     * removeMessage:删除队列中所有tag类别的消息. <br/>
     * 
     * @param tag
     */
    public void removeMessage(String tag) {
        if (!mTagListMap.containsKey(tag)) {
            Log.d(TAG, tag + " message is not exist !");
            return;
        }
        ArrayList<Integer> idList = mTagListMap.get(tag);
        for (Integer ix : idList) {
            mHandlerListenerMap.remove(ix);
            mIdtoTagListMap.remove(ix);
        }
        mTagListMap.remove(tag);
        Log.d(TAG, tag + " message remove ok !");
    }

    public void clearAllMessage() {
        mHandlerListenerMap.clear();
        mTagListMap.clear();
        mIdtoTagListMap.clear();
    }
}

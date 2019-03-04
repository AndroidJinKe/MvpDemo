package com.yipin.basepj.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

/**
 * Created by jkzhang
 * DATE : 2018/9/29
 * Description ：
 */
public abstract class BasePop extends PopupWindow {

    protected Context mContext;
    protected View view;


    public BasePop(Context context) {
        super(context);
        mContext = context;
        initView();
        setContentView(view);
        setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(false);
        setFocusable(false);
        setTouchable(true);
        setBackgroundDrawable(new ColorDrawable(0));
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1.0f);
            }
        });
    }




    public void showCenter(Context context, int layoutId) {
        if (!isShowing()) {
            View rootView = LayoutInflater.from(context).inflate(layoutId, null);
            setBackgroundAlpha(0.5f);
            showAtLocation(rootView, Gravity.CENTER, 0, 0);
        }
    }

    public void showCenter(Context context, int layoutId, float alpha) {
        if (!isShowing()) {
            View rootView = LayoutInflater.from(context).inflate(layoutId, null);
            setBackgroundAlpha(0.5f);
            showAtLocation(rootView, Gravity.CENTER, 0, 0);
        }
    }

    protected abstract void initView();


    /**
     * 设置添加屏幕的背景透明度 *  * @param bgAlpha *
     * 屏幕透明度0.0-loading_01.0 1表示完全不透明
     */
    protected void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
        lp.alpha = bgAlpha;
        ((Activity) mContext).getWindow().setAttributes(lp);
    }


}

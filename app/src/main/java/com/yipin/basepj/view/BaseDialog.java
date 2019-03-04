package com.yipin.basepj.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.WindowManager;


public abstract class BaseDialog {
    protected Dialog mDialog;
    protected Context mContext;

    public BaseDialog(Context context, int style) {
        this.mContext = context;
        View view = getDefaultView(context);
        mDialog = createDialog(context, view, style);
        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        params.alpha = 1.0f;
        mDialog.getWindow().setAttributes(params);
    }

    public Dialog dialog() {
        return mDialog;
    }

    /**
     * 子类重写该方法，即可创建样式相同的对话框。
     *
     * @param context
     * @return
     */
    protected abstract View getDefaultView(Context context);

    private static Dialog createDialog(Context context, View v, int style) {
        Dialog dialog = new Dialog(context, style);
        dialog.setCancelable(false);
        dialog.setContentView(v);
        return dialog;
    }

    public BaseDialog force() {
        mDialog.setCancelable(false);
        return this;
    }

    public void show() {
        if (mDialog != null) {
            try {
                mDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void dismiss() {
        if (mDialog != null) {
            try {
                mDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isShowing() {
        if (mDialog != null) {
            return mDialog.isShowing();
        }
        return false;
    }

    public BaseDialog onCancel(DialogInterface.OnCancelListener listener) {
        if (mDialog != null) {
            mDialog.setOnCancelListener(listener);
        }
        return this;
    }

}

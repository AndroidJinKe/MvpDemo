package com.yipin.basepj.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.yipin.basepj.R;


/**
 * Created by jkzhang
 * DATE : 2018/9/29
 * Description ：
 */
public class LoadingDialog extends BaseDialog {


    private ImageView loadingImg;


    public LoadingDialog(Context context, int style) {
        super(context, style);
    }


    @Override
    protected View getDefaultView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_loading_pop, null);
        loadingImg = view.findViewById(R.id.loading_img);
        return view;
    }


    private void startAnim() {
        loadingImg.setImageResource(R.drawable.anim_loading);
        AnimationDrawable anim = (AnimationDrawable) loadingImg.getDrawable();
        anim.start();
    }

    private void stopAnim() {
        loadingImg.clearAnimation();
        loadingImg.setImageResource(R.drawable.loading_01);
    }

    @Override
    public void show() {
        startAnim();
        super.show();
    }


    @Override
    public void dismiss() {
        stopAnim();
        super.dismiss();
    }


}

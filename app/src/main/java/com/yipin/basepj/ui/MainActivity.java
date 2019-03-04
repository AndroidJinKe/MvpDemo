package com.yipin.basepj.ui;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yipin.basepj.R;
import com.yipin.basepj.api.RetrofitFactory;
import com.yipin.basepj.api.base.BaseObserver;
import com.yipin.basepj.model.SampleEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {


    @BindView(R.id.textView)
    TextView mtv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initView() {

    }


    @OnClick({R.id.btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn:
                getData();
                break;
        }
    }


    private void getData() {
        RetrofitFactory.getInstance().apiService()
                .getSampleData(1, 1)
                .compose(schedulersTransformer())
                .subscribe(new BaseObserver<List<SampleEntity>>() {
                    @Override
                    protected void onRequestStart() {
                        showLoading();
                    }

                    @Override
                    protected void onSuccess(List<SampleEntity> sampleEntities) {
                        mtv.setText(new Gson().toJson(sampleEntities));
                    }

                    @Override
                    protected void onFailure(Throwable e, String errorMsg) {

                    }

                    @Override
                    protected void onRequestComplete() {
                        dismissLoading();
                    }
                });

    }


}

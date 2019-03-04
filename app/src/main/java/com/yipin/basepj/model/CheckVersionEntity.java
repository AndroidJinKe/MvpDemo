package com.yipin.basepj.model;

import java.io.Serializable;

/**
 * Created by jkzhang
 * DATE : 2018/10/26
 * Description ：
 */
public class CheckVersionEntity implements Serializable {


    public boolean needUpdate;
    public boolean forceUpdate;
    public String updateDesc;
    public String url;

    @Override
    public String toString() {
        return "CheckVersionEntity{" +
                "needUpdate=" + needUpdate +
                ", forceUpdate=" + forceUpdate +
                ", updateDesc='" + updateDesc + '\'' +
                ", apkUrl='" + url + '\'' +
                '}';
    }
}

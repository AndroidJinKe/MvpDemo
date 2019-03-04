package com.yipin.basepj.event;


import com.yipin.basepj.model.CheckVersionEntity;

/**
 * Created by jkzhang
 * DATE : 2018/10/27
 * Description ：
 */
public class OnCheckVersionEvent {

    public CheckVersionEntity checkVersionEntity;

    public OnCheckVersionEvent(CheckVersionEntity checkVersionEntity) {
        this.checkVersionEntity = checkVersionEntity;
    }

}

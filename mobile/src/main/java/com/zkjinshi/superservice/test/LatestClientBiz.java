package com.zkjinshi.superservice.test;

import com.zkjinshi.superservice.vo.LatestClientVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 开发者：vincent
 * 日期：2015/9/30
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class LatestClientBiz {

    public static ArrayList<LatestClientVo> getLatestClients(){

        ArrayList<LatestClientVo> clientBeanList = new ArrayList<>();
        LatestClientVo clientBean = new LatestClientVo();
        clientBean.setTimeStamp(System.currentTimeMillis());
        clientBean.setShopID("120");
        clientBean.setUserID("557afb0604ed8");
        clientBean.setUserName("休止符");
        clientBean.setLocID(new Random().nextInt(10) + "");
        clientBeanList.add(clientBean);

        clientBean = new LatestClientVo();
        clientBean.setTimeStamp(System.currentTimeMillis());
        clientBean.setShopID("808");
        clientBean.setUserID("55d17e2c054cb");
        clientBean.setUserName("千里马");
        clientBean.setLocID(new Random().nextInt(10) + "");
        clientBeanList.add(clientBean);

        clientBean = new LatestClientVo();
        clientBean.setTimeStamp(System.currentTimeMillis());
        clientBean.setShopID("808");
        clientBean.setUserID("557cff54a9a97");
        clientBean.setUserName("金石小智");
        clientBean.setLocID(new Random().nextInt(10) + "");
        clientBeanList.add(clientBean);

        clientBean = new LatestClientVo();
        clientBean.setTimeStamp(System.currentTimeMillis());
        clientBean.setShopID("808");
        clientBean.setUserID("55d17e2c054cb");
        clientBean.setUserName("千里马");
        clientBean.setLocID(new Random().nextInt(10) + "");
        clientBeanList.add(clientBean);

        clientBean = new LatestClientVo();
        clientBean.setTimeStamp(System.currentTimeMillis());
        clientBean.setShopID("808");
        clientBean.setUserID("557cff54a9a97");
        clientBean.setUserName("金石小智");
        clientBean.setLocID(new Random().nextInt(10) + "");
        clientBeanList.add(clientBean);

        clientBean = new LatestClientVo();
        clientBean.setTimeStamp(System.currentTimeMillis());
        clientBean.setShopID("808");
        clientBean.setUserID("55d17e2c054cb");
        clientBean.setUserName("千里马");
        clientBean.setLocID(new Random().nextInt(10) + "");
        clientBeanList.add(clientBean);

        clientBean = new LatestClientVo();
        clientBean.setTimeStamp(System.currentTimeMillis());
        clientBean.setShopID("808");
        clientBean.setUserID("557cff54a9a97");
        clientBean.setUserName("金石小智");
        clientBean.setLocID(new Random().nextInt(10) + "");
        clientBeanList.add(clientBean);

        clientBean = new LatestClientVo();
        clientBean.setTimeStamp(System.currentTimeMillis());
        clientBean.setShopID("808");
        clientBean.setUserID("55d17e2c054cb");
        clientBean.setUserName("千里马");
        clientBean.setLocID(new Random().nextInt(10) + "");
        clientBeanList.add(clientBean);

        clientBean = new LatestClientVo();
        clientBean.setTimeStamp(System.currentTimeMillis());
        clientBean.setShopID("808");
        clientBean.setUserID("557cff54a9a97");
        clientBean.setUserName("金石小智");
        clientBean.setLocID(new Random().nextInt(10) + "");
        clientBeanList.add(clientBean);

        return clientBeanList;
    }

}

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

    public static List<LatestClientVo> getLatestClients(){

        List<LatestClientVo> clientBeanList = new ArrayList<>();

        LatestClientVo clientBean1 = new LatestClientVo();
        LatestClientVo clientBean2 = new LatestClientVo();
        LatestClientVo clientBean3 = new LatestClientVo();

        clientBean1.setTimeStamp(System.currentTimeMillis());
        clientBean1.setShopID("120");
        clientBean1.setUserID("557afb0604ed8");
        clientBean1.setUserName("休止符");
        clientBean1.setLocID(new Random().nextInt(10) + "");

        clientBean2.setTimeStamp(System.currentTimeMillis());
        clientBean2.setShopID("808");
        clientBean2.setUserID("55d17e2c054cb");
        clientBean2.setUserName("千里马");
        clientBean2.setLocID(new Random().nextInt(10) + "");

        clientBean3.setTimeStamp(System.currentTimeMillis());
        clientBean3.setShopID("808");
        clientBean3.setUserID("557cff54a9a97");
        clientBean3.setUserName("古天乐");
        clientBean3.setLocID(new Random().nextInt(10) + "");

        clientBeanList.add(clientBean1);
        clientBeanList.add(clientBean2);
        clientBeanList.add(clientBean3);

        return clientBeanList;
    }

}

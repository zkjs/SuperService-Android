package com.zkjinshi.superservice.test;

import com.zkjinshi.superservice.vo.ZoneVo;

import java.util.ArrayList;

/**
 * 开发者：杜健德
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ZoneBiz {

    public static ArrayList<ZoneVo> getZoneList(){

        ArrayList<ZoneVo> zoneList = new ArrayList<ZoneVo>();
        for(int i=1;i<11;i++){
            ZoneVo zoneVo = new ZoneVo();

            zoneList.add(zoneVo);
        }
        return zoneList;
    }
}

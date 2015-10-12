package com.zkjinshi.superservice.test;

import com.zkjinshi.superservice.vo.DepartmentVo;
import com.zkjinshi.superservice.vo.ZoneVo;

import java.util.ArrayList;

/**
 * 开发者：dujiande
 * 日期：2015/10/12
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class DepartmentBiz {


    public static ArrayList<DepartmentVo> getDepartmentList(){
        String names[] = {"市场部","前厅部","客服部","大厅部"};

        ArrayList<DepartmentVo> dptList = new ArrayList<DepartmentVo>();
        for(int i=0;i<4;i++){
            DepartmentVo departmentVo = new DepartmentVo();
            departmentVo.setId(i);
            departmentVo.setName(names[i]);
            dptList.add(departmentVo);
        }
        return dptList;
    }
}

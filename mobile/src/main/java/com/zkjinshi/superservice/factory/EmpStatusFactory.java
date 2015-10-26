package com.zkjinshi.superservice.factory;

import com.zkjinshi.superservice.entity.EmpStatusRecord;
import com.zkjinshi.superservice.vo.EmpStatusVo;

import java.util.ArrayList;

/**
 * 开发者：JimmyZhang
 * 日期：2015/10/14
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class EmpStatusFactory {

    private static EmpStatusFactory instance;

    private EmpStatusFactory(){};

    public synchronized static EmpStatusFactory getInstance(){
        if(null == instance){
            instance = new EmpStatusFactory();
        }
        return instance;
    }

    public EmpStatusVo buildEmpStatus(EmpStatusRecord empStatusRecord){
        EmpStatusVo empStatusVo = new EmpStatusVo();
        empStatusVo.setEmpId(empStatusRecord.getEmpid());
        empStatusVo.setEmpName(empStatusRecord.getEmpname());
        empStatusVo.setLoginTimestamp(empStatusRecord.getLogintimestamp() * 1000);
        empStatusVo.setWorkStatus(empStatusRecord.getWorkstatus());
        empStatusVo.setOnlineStatus(empStatusRecord.getOnlinestatus());
        return empStatusVo;
    }

    public ArrayList<EmpStatusVo> buildEmpStatusList(ArrayList<EmpStatusRecord> empStatusRecordList){
        ArrayList<EmpStatusVo> empStatusList = new ArrayList<EmpStatusVo>();
        for(EmpStatusRecord empStatusRecord: empStatusRecordList){
            empStatusList.add(buildEmpStatus(empStatusRecord));
        }
        return empStatusList;
    }

}

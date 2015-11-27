package com.zkjinshi.superservice.activity.set;

/**
 * 开发者：vincent
 * 日期：2015/10/26
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SessionIDBuilder {

    private final static String TAG = SessionIDBuilder.class.getSimpleName();

    private static SessionIDBuilder instance = null;

    private SessionIDBuilder(){
    }

    public synchronized static SessionIDBuilder getInstance(){
        if(instance == null){
            instance = new SessionIDBuilder();
        }
        return instance;
    }

    /**
     * 构建内部单聊sessionID
     * @param shopID
     * @param mUserID
     * @param empID
     * @return
     */
    public String buildSingleSessionID(String shopID, String mUserID, String empID) {
        if(mUserID.compareTo(empID) < 0){
            return "single_" + shopID + "_" + mUserID + "_" + empID;
        }else if(mUserID.compareTo(empID) > 0){
            return "single_" + shopID + "_" + empID + "_" + mUserID;
        } else {
            return null;
        }
    }

}

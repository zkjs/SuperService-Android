package com.zkjinshi.superservice.vo;

/**
 * 客户数据表操作对象
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientVo {

    private String clientID;        //客户ID 主键
    private String avatarName;      //头像图片名称
    private String avatarUrl;       //头像图片网络url
    private String clientName;      //客户姓名
    private String clientPhone;     //客户手机
    private String clientCompany;   //客户所在公司
    private String clientPosition;  //客户职位
    private OnAccountStatus onAccount;       //是否挂账会员 0:挂账会员 1 非挂账会员

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getClientCompany() {
        return clientCompany;
    }

    public void setClientCompany(String clientCompany) {
        this.clientCompany = clientCompany;
    }

    public String getClientPosition() {
        return clientPosition;
    }

    public void setClientPosition(String clientPosition) {
        this.clientPosition = clientPosition;
    }

    public OnAccountStatus isOnAccount() {
        return onAccount;
    }

    public void setOnAccount(OnAccountStatus onAccount) {
        this.onAccount = onAccount;
    }
}

package com.zkjinshi.superservice.utils;

import com.zkjinshi.base.config.ConfigUtil;

/**
 * 协议接口工具类
 * 开发者：dujiande
 * 日期：2015/9/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ProtocolUtil {

    /**
     * 管理员登陆
     * @return
     */
    public static String getAdminLoginUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"semp/semplogin";
    }

    /**
     * 服务员登陆
     * @return
     */
    public static String getSempLoginUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"semp/login";
    }

    /**
     * 服务员修改资料
     * @return
     */
    public static String getSempupdateUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"semp/sempupdate";
    }

    /**
     * 服务员获取商家整个区域列表
     * @return
     */
    public static String getZonelistUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"semp/shoplocation";
    }

    /**
     * 服务员修改自己管辖的区域通知
     * @return
     */
    public static String getSemplocationupdateUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"semp/semplocationupdate";
    }

    /**
     * 服务员获取自己的通知区域
     * @return
     */
    public static String getMySemplocationUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"semp/semplocation";
    }

    /**
     * 订单管理 获取订单详情 超级接口
     * @return
     */
    public static String getSempOrderUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"semp/order";
    }

    /**
     * 会员信息 获取客人基础信息
     * @return
     */
    public static String getClientBasicUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"semp/sempsuforphone";
    }

    /**
     * 会员信息 获取客人详细信息
     * @return
     */
    public static String getAddTagUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"semp/addtag";
    }

    /**
     * 会员信息 绑定会员
     * @return
     */
    public static String getAddUserUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"semp/adduser";
    }

    /**
     * 我的客户 获取我的客户列表
     * @return
     */
    public static String getShopUserListUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"semp/showuserlist";
    }

    /**
     * 我的团队 获取团队联系人列表
     * @return
     */
    public static String getTeamListUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"semp/teamlist";
    }

    /**
     * 支付方式列表
     * @return
     */
    public static String getSempPayListUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"semp/paylist";
    }

    /**
     * 到店通知查询用户区域/个人信息
     */
    public static String getSempNoticeUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"semp/notice";
    }

    /**
     * 查询部门列表
     */
    public static String getDeptListUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"shop/deptlist";
    }

    /**
     * 管理: 获取部门列表
     */
    public static String getShopDeptlistUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"shop/deptlist";
    }

    /**
     * 管理: 批量添加服务员
     */
    public static String getBatchAddClientUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"shop/importsemp";
    }

    /**
     * 员工批量修改部门
     */
    public static String getChangeDeptUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"shop/changedept";
    }

    /**
     * 管理: 添加/批量管理部门
     */
    public static String getBatchAddDeptUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"shop/adddept";
    }

    /**
     * 管理: 批量
     */
    public static String getBatchDeleteEmployeeUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"shop/deletesemp";
    }

    /**
     * 获得商品图片
     * @param imageUrl
     * @return
     */
    public static String getGoodImgUrl(String imageUrl){
        return ConfigUtil.getInst().getHttpDomain()+imageUrl;
    }

    /**
     * 获得图片
     * @param imageUrl
     * @return
     */
    public static String getImgUrl(String imageUrl){
        return ConfigUtil.getInst().getHttpDomain()+imageUrl;
    }

    /**
     * 获得用户头像
     * @param userid
     * @return
     */
    public static String getAvatarUrl(String userid){
        return ConfigUtil.getInst().getHttpDomain()+"uploads/users/"+userid+".jpg";
    }
    /**
     * 获得商家logo
     * @param shopID
     * @return
     */
    public static String getShopLogoUrl(String shopID){
        return ConfigUtil.getInst().getHttpDomain()+"uploads/shops/"+ shopID +".png";
    }

    /**
     * 获得商家背景
     * @param shopID
     * @return
     */
    public static String getShopBackUrl(String shopID){
        return ConfigUtil.getInst().getHttpDomain()+"uploads/shops/"+ shopID +"_bg.png";
    }

    /**
     * 获得商家主图
     * @param shopID
     * @return
     */
    public static String getShopMainUrl(String shopID){
        return ConfigUtil.getInst().getHttpDomain()+"uploads/shops/"+ shopID +".jpg";
    }

    /**
     * POST 服务员随机获取一个邀请码
     * @return
     */
    public static String getNewRandomInviteCodeUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"invitation/random";
    }

    /**
     * POST 服务员查看我的邀请码列表
     * @return
     */
    public static String getInviteCodeUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"invitation/sempcode";
    }

    /**
     * POST 服务员查看我的邀请码已使用记录列表
     * @return
     */
    public static String getCodeUserAllUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"invitation/codeuserall";
    }

    /**
     * POST 客户根据邀请码查询服务员
     * @return
     */
    public static String getEmpByInviteCodeUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"invitation/getcode";
    }

    /**
     * POST 超级身份输入邀请码动作
     * @return
     */
    public static String getUserBindInviteCodeURrl(){
        return ConfigUtil.getInst().getHttpDomain()+"invitation/bdcode";
    }

    /**
     * POST 超级服务生产邀请注册链接
     * @return
     */
    public static String getMakeInviteCodeUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"invitation/makeurl";
    }

    /**
     * POST
     * @return
     */
    public static String getCodeUserListUrl(){
        return ConfigUtil.getInst().getHttpDomain()+" invitation/codeuser";
    }

    /**
     * 退出appHttp连接
     * @return
     */
    public static String getLogoutUrl(String userID) {
        return ConfigUtil.getInst().getHttpDomain()+"user/logout?userid=" + userID;
    }

    /**
     * 获取用户信息
     * @return
     */
    public static String getUserInfoUrl(){
        return  ConfigUtil.getInst().getHttpDomain()+"v10/user";
    }

    /**
     * 添加用户发票列表
     * @return
     */
    public static String addTicketUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"user/fpadd";
    }

    /**
     * 获取用户发票列表
     * @return
     */
    public static String geTicketListUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"user/fplist";
    }

    /**
     * 获取环信群成员
     * @return
     */
    public static String getGroupMemberUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"hxim/member";
    }

    /** 订单操作相关 */
    /**
     * 订单处理1,商家锁定/增加订单
     * Method: POST
     * Input Params:
     * {category: (0 酒店 1 KTV 2 其他),
     * data: OrderDetailForDisplay序列化为JSON(数据格式参照13)}
     * @return
     * {"data":"H14513531753485236","result":true}
     */
    public static String getAddOrderUrl(){
        return ConfigUtil.getInst().getApiDomain()+"order/add";
    }

    /**
     * 订单修改/确认/改变状态
     * Method: POST
     * Input Params:
     * data: OrderDetailForDisplay序列化为JSON(数据格式参照13)
     * @return
     * {"data":"H14513531753485236","result":true}
     * @return
     */
    public static String getUpdateOrderUrl(){
        return ConfigUtil.getInst().getApiDomain()+"order/update";
    }

    /**
     * 订单管理 获取订单列表
     * @return
     */
    public static String getOrderListUrl(String shopID, String userID, int page, int pageSize){
        return ConfigUtil.getInst().getApiDomain()+"order/list/"+shopID+"/"+userID+"/"+page+"/"+pageSize;
    }

    /**
     * 获取订单详情
     * Method: GET
     * @return
     */
    public static String getOrderDetailUrl(String orderNO){
        return ConfigUtil.getInst().getApiDomain()+"order/get/"+orderNO;
    }

    /**
     *获取订单评论
     * Method: GET
     * @return
     */
    public static String getCommentShow(String orderNO, int page, int pageSize){
        return ConfigUtil.getInst().getApiDomain()+"order/evaluation/get/"+orderNO+"/"+page+"/"+pageSize;
    }

    /**
     * 查询用户(服务员)简单信息
     * @return
     */
    public static String getClientInfoUrl() {
        return ConfigUtil.getInst().getHttpDomain()+"v10/user";
    }

    /**
     * 获取商品列表 URL
     * @param shopId
     * @return
     */
    public static String getGoodListUrl(String shopId){
        return ConfigUtil.getInst().getApiDomain()+"goods/get/"+shopId;
    }

}

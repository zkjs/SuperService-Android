package com.zkjinshi.superservice.utils;

import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.util.*;
import com.zkjinshi.base.util.Constants;

/**
 * 协议接口工具类
 * 开发者：dujiande
 * 日期：2015/9/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ProtocolUtil {

    /**
     * 服务员获取商家整个区域列表
     * @return
     */
    public static String getZonelistUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"semp/shoplocation";
    }

    /**
     * 服务员修改自己管辖的区域通知
     * @return
     */
    public static String getSemplocationupdateUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"semp/semplocationupdate";
    }

    /**
     * 服务员获取自己的通知区域
     * @return
     */
    public static String getMySemplocationUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"semp/semplocation";
    }

    /**
     * 订单管理 获取订单详情 超级接口
     * @return
     */
    public static String getSempOrderUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"semp/order";
    }

    /**
     * 会员信息 获取客人详细信息
     * @return
     */
    public static String getAddTagUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"semp/addtag";
    }

    /**
     * 会员信息 绑定会员
     * @return
     */
    public static String getAddFuserUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"semp/addfuser";
    }

    /**
     * 我的客户 获取我的客户列表
     * @return
     */
    public static String getShopUserListUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"semp/showuserlist";
    }

    /**
     * 我的团队 获取团队联系人列表
     * @return
     */
    public static String getTeamListUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"semp/teamlist";
    }

    /**
     * 支付方式列表
     * @return
     */
    public static String getSempPayListUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"semp/paylist";
    }

    /**
     * 到店通知查询用户区域/个人信息
     */
    public static String getSempNoticeUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"semp/notice";
    }

    /**
     * 查询部门列表
     */
    public static String getDeptListUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"shop/deptlist";
    }

    /**
     * 管理: 获取部门列表
     */
    public static String getShopDeptlistUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"shop/deptlist";
    }

    /**
     * 员工批量修改部门
     */
    public static String getChangeDeptUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"shop/changedept";
    }

    /**
     * 管理: 添加/批量管理部门
     */
    public static String getBatchAddDeptUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"shop/adddept";
    }

    /**
     * 管理: 批量
     */
    public static String getBatchDeleteEmployeeUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"shop/deletesemp";
    }

    /**
     * POST 服务员随机获取一个邀请码
     * @return
     */
    public static String getNewRandomInviteCodeUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"invitation/random";
    }

    /**
     * POST 服务员查看我的邀请码列表
     * @return
     */
    public static String getInviteCodeUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"invitation/sempcode";
    }

    /**
     * POST 服务员查看我的邀请码已使用记录列表
     * @return
     */
    public static String getCodeUserAllUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"invitation/codeuserall";
    }

    /**
     * POST 超级服务生产邀请注册链接
     * @return
     */
    public static String getMakeInviteCodeUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"invitation/makeurl";
    }

    /**
     * POST
     * @return
     */
    public static String getCodeUserListUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"invitation/codeuser";
    }

    /**
     * 退出appHttp连接
     * @return
     */
    public static String getLogoutUrl(String userID) {
        return ConfigUtil.getInst().getPhpDomain()+"user/logout?userid=" + userID;
    }


    /**
     * 添加用户发票列表
     * @return
     */
    public static String addTicketUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"user/fpadd";
    }

    /**
     * 获取用户发票列表
     * @return
     */
    public static String geTicketListUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"user/fplist";
    }

    /**
     * 获取环信群成员
     * @return
     */
    public static String getGroupMemberUrl(){
        return ConfigUtil.getInst().getPhpDomain()+"hxim/member";
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
        return ConfigUtil.getInst().getJavaDomain()+"order/add";
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
        return ConfigUtil.getInst().getJavaDomain()+"order/update";
    }

    /*
    确认订单
    RequestMethod: POST
    INPUT： orderno(string)，status(int)
    OUTPUT：{"result":true,"orderno":"H14513583275501780"}
     */
    public static String getConfirmOrderUrl(){
        return ConfigUtil.getInst().getJavaDomain()+"order/confirm";
    }

    /**
     * 订单管理 获取订单列表
     * @return
     */
    public static String getOrderListUrl(String shopID, String userID, int page, int pageSize){
        return ConfigUtil.getInst().getJavaDomain()+"order/list/"+shopID+"/"+userID+"/"+page+"/"+pageSize;
    }

    /**
     * 获取订单详情
     * Method: GET
     * @return
     */
    public static String getOrderDetailUrl(String orderNO){
        return ConfigUtil.getInst().getJavaDomain()+"order/get/"+orderNO;
    }

    /**
     * 查询用户(服务员)简单信息
     * @return
     */
    public static String getClientInfoUrl() {
        return ConfigUtil.getInst().getPhpDomain()+"v10/user";
    }

    /**
     * 获取商品列表 URL
     * @param shopId
     * @return
     */
    public static String getGoodListUrl(String shopId){
        return ConfigUtil.getInst().getJavaDomain()+"goods/get/"+shopId;
    }

    /**
     * 获得到店通知URL
     * @param shopId
     * @param locId
     * @param page
     * @param page_size
     * @return
     */
    public static String getNoticeUrl(String shopId,String locId,String page,String page_size){
        return  ConfigUtil.getInst().getPyxDomain()+"lbs/v1/loc/beacon/"+shopId+"/"+locId+"?roles=USER&page="+page+"&page_size="+page_size;
    }

    /**
     * 拼接图片路径
     * @param apiUrl
     * @return
     */
    public static String getHostImgUrl(String apiUrl){
        return ConfigUtil.getInst().getImgDomain()+apiUrl;
    }

    /**
     * 获得用户头像
     * @param userid
     * @return
     */
    public static String getAvatarUrl(String userid){
        return ConfigUtil.getInst().getImgDomain()+"uploads/users/"+userid+".jpg";
    }

    /**
     * 查询用户是否是邀请码和指定商家是否绑定销售
     * @return
     */
    public static String getBindUserInfo(){
        return ConfigUtil.getInst().getPhpDomain()+"semp/aubdts";
    }

    /**
     * 获取手机验证码
     * @return
     */
    public static String ssoVcode(){
        return ConfigUtil.getInst().getPavDomain()+"sso/vcode/v1/ss?source=login";
    }

    /**
     * 获取手机验证码
     * @return
     */
    public static String ssoToken(){
        return  ConfigUtil.getInst().getPavDomain()+"sso/token/v1/phone/ss";
    }

    /**
     * 使用用户名密码创建Token
     * @return
     */
    public static String ssoPasswordGetToken(){
        return  ConfigUtil.getInst().getPavDomain()+"sso/token/v1/name/ss";
    }

    /**
     * 获取用户资料-批量，所有用户
     * @return
     */
    public static String getUserInfoAll(){
        return ConfigUtil.getInst().getForDomain()+"res/v1/query/user/all";
    }

    /**
     * 登陆后更新用户资料
     * @return
     */
    public static String updateUserInfo(){
        return ConfigUtil.getInst().getForDomain()+"res/v1/update/user";
    }

    /**
     * 我的客人（ss），查询销售的客人资料
     * @return
     */
    public static String getClientList(){
        return ConfigUtil.getInst().getForDomain()+"res/v1/query/sis/";
    }

    /**
     * 批量注册SS用户
     * @return
     */
    public static String registerSSusers(){
        return ConfigUtil.getInst().getForDomain()+"res/v1/register/users";
    }

    /**
     * 根据shopid获取客服成员-按角色区分
     * @return
     */
    public static String getEmpployeeList(){
        return ConfigUtil.getInst().getForDomain()+"res/v1/query/sss";
    }

    /**
     * 获取当前商家Beacon列表
     * @return
     */
    public static String getZoneList(){
        return ConfigUtil.getInst().getPyxDomain()+"lbs/v1/loc/beacon/subscription";
    }

    /**
     * 获得刷新token Url
     * @return
     */
    public static String getTokenRefreshUrl(){
        return ConfigUtil.getInst().getPavDomain()+"sso/token/v1";
    }

    /**
     * 创建邀请码
     * @return
     */
    public static String getSaleCodeUrl(){
        return ConfigUtil.getInst().getForDomain()+"res/v1/salecode/get/salecode";
    }

    /**
     * 获取邀请码列表
     * @param type 1：已使用的邀请码， 0:未使用的邀请码
     * @param pageSize 每个个数
     * @param pageNo 第几页
     * @return
     */
    public static String getSaleCodeListUrl(String type,String pageSize,String pageNo){
        return ConfigUtil.getInst().getForDomain()+"res/v1/salecode/salecodewithsi/"+type+"?page="+pageNo+"&page_size="+pageSize;
    }

    /**
     * 获取邀请码分享链接
     * @return
     */
    public static String getSalesCodeShareUrl(){
        return ConfigUtil.getInst().getForDomain()+"res/v1/link/joinpage";
    }

}

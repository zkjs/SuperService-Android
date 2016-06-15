package com.zkjinshi.superservice.pad.utils;

import android.content.Context;

import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.util.*;

/**
 * 协议接口工具类
 * 开发者：dujiande
 * 日期：2015/9/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ProtocolUtil {

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
    public static String getGroupMemberUrl(String groupId){
        return ConfigUtil.getInst().getForDomain()+"res/v1/im/members/"+groupId;
    }

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
     * 获得到店通知URL
     * @param shopId
     * @param locId
     * @param lastid
     * @param page
     * @param pageSize
     * @return
     */
    public static String getNoticeUrl(String shopId,String locId,String lastid,String page, String pageSize){
        return  ConfigUtil.getInst().getForDomain()+"lbs/v1/loc/beacon/"+shopId+"/"+locId+"?roles=USER&lastid="+lastid+"&page="+page+"&page_size="+pageSize;
    }

    /**
     * 拼接图片路径
     * @param apiUrl
     * @return
     */
    public static String getHostImgUrl(String apiUrl){
        return ConfigUtil.getInst().getCdnDomain()+apiUrl;
    }

    /**
     * 根据尺寸获取图片路径
     * @param context
     * @param apiUrl
     * @param w
     * @param h
     * @return
     */
    public static String getImageUrlByScale(Context context, String apiUrl, int w, int h){
        w = DisplayUtil.dip2px(context,w);
        h = DisplayUtil.dip2px(context,h);
        String domain =  ConfigUtil.getInst().getPcdDomain();
        return domain+apiUrl+"@"+w+"w_"+h+"h";
    }

    public static String getImageUrlByWidth(Context context, String apiUrl, int w){
        w = DisplayUtil.dip2px(context,w);
        String domain =  ConfigUtil.getInst().getPcdDomain();
        return domain+apiUrl+"@"+w+"w";
    }

    public static String getImageUrlByHeight(Context context, String apiUrl, int h){
        h = DisplayUtil.dip2px(context,h);
        String domain =  ConfigUtil.getInst().getPcdDomain();
        return domain+apiUrl+"@"+h+"h";
    }

    public static String getAvatarUrl(Context context, String apiUrl){
        return getImageUrlByScale(context,apiUrl,60,60);
    }


    /**
     * 获得用户头像
     * @param userid
     * @return
     */
    public static String getAvatarUrl(String userid){
        return ConfigUtil.getInst().getCdnDomain()+"uploads/users/"+userid+".jpg";
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
     * 注册更新用户资料
     * @return
     */
    public static String registerUpdateUserInfo(){
        return ConfigUtil.getInst().getForDomain()+"res/v1/register/update/ss";
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
        return ConfigUtil.getInst().getPyxDomain()+"lbs/v1/loc/beacon";
    }

    /**
     * 获取商家的商品
     * @param shopid
     * @param page
     * @param pageSize
     * @return
     */
    public static String getGoodListByCity(String shopid,int page,int pageSize){
        return ConfigUtil.getInst().getForDomain()+"res/v1/shop/goods/"+shopid+"?page="+page+"&page_size="+pageSize;
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
     * 获取白名单列表
     * @param pageSize
     * @param pageNo
     * @return
     */
    public static String getWhiteUserListUrl(String pageNo,String pageSize){
        return ConfigUtil.getInst().getForDomain()+"res/v1/whiteuser/info?page="+pageNo+"&page_size="+pageSize;
    }

    /**
     * 获取邀请码分享链接
     * @return
     */
    public static String getSalesCodeShareUrl(){
        return ConfigUtil.getInst().getForDomain()+"res/v1/link/joinpage";
    }

    /**
     * 获取用户信息
     * @param userIds
     * @return
     */
    public static String getUsersInfoUrl(String userIds){
        return ConfigUtil.getInst().getForDomain()+"res/v1/query/user/all?userids="+userIds;
    }

    /**
     * 登陆后更新用户资料
     * @return
     */
    public static String loginUpdateSs(){
        return ConfigUtil.getInst().getForDomain()+"res/v1/update/user";
    }

    /**
     * 获取用户标签集合信息
     * @param clientId
     * @return
     */
    public static String getClientTagsUrl(String clientId){
        return ConfigUtil.getInst().getForDomain()+"res/v1/query/user/tags?si_id="+clientId;
    }

    /**
     * 更新用户标签信息
     * @return
     */
    public static String getUpdateClientTagsUrl(){
        return ConfigUtil.getInst().getForDomain()+"res/v1/update/user/tags";
    }

    /**
     * 验证原始密码是否正确
     * @return
     */
    public static String verifyLoginpassword(){
        return ConfigUtil.getInst().getForDomain()+"res/v1/verify/ss/loginpassword";
    }

    /**
     * 修改密码接口
     * @return
     */
    public static String updateLoginpassword(){
        return ConfigUtil.getInst().getForDomain()+"res/v1/update/ss/loginpassword";
    }

    /**
     * 删除白名单用户
     * @return
     */
    public static String getDeleteWhiteUserUrl(){
        return ConfigUtil.getInst().getForDomain()+"res/v1/whiteuser";
    }

    /**
     * 新增白名单
     * @return
     */
    public static String getAddWhiteUserUrl(){
        return ConfigUtil.getInst().getForDomain()+"res/v1/whiteuser";
    }

    /**
     * 获取升级版本信息
     * @param apptype 1 (int required) - 应用类型： 1 超级身份 2 超级服务
     * @param devicetype IOS (String required) - 设备类型：IOS ANDROID
     * @param verno 1.0.0.1 (String required) - 当前客户端版本号
     * @return
     */
    public static String upgradeNewestVersion(int apptype,String devicetype,String verno){
        return ConfigUtil.getInst().getForDomain()+"/res/v1/systempub/upgrade/newestversion/"+apptype+"/"+devicetype+"/"+verno;
    }

    /**
     * 删除员工接口
     * @param ids 支持批量删除，用英文逗号隔开
     * @return
     */
    public static String deleteEmployee(String ids){
        return ConfigUtil.getInst().getForDomain()+"/res/v1/delete/ss?userids="+ids;
    }

}

package com.zkjinshi.superservice.vo;


/**
 * 客户数据表操作对象
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientVo {

    private int    id;
    private String userid;
    private String shopid;
    private String salesid;
    private int    user_level;
    private String level_desc;
    private String card_no;
    private String is_special;
    private String nationality;
    private String like_desc;
    private String taboo_desc;
    private String other_desc;
    private String created;
    private long   modified;
    private String username;
    private String phone;
    private String company;
    private String position;
    private int    is_bill;
    private ContactType contactType;
    private SexType     sex;
    private int         order_count;
    private String      tags;
    private OnlineStatus    isOnline;
    private int             bgDrawableRes;

    public int getUser_level() {
        return user_level;
    }

    public void setUser_level(int user_level) {
        this.user_level = user_level;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getSalesid() {
        return salesid;
    }

    public void setSalesid(String salesid) {
        this.salesid = salesid;
    }

    public String getLevel_desc() {
        return level_desc;
    }

    public void setLevel_desc(String level_desc) {
        this.level_desc = level_desc;
    }

    public String getCard_no() {
        return card_no;
    }

    public void setCard_no(String card_no) {
        this.card_no = card_no;
    }

    public String getIs_special() {
        return is_special;
    }

    public void setIs_special(String is_special) {
        this.is_special = is_special;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getLike_desc() {
        return like_desc;
    }

    public void setLike_desc(String like_desc) {
        this.like_desc = like_desc;
    }

    public String getTaboo_desc() {
        return taboo_desc;
    }

    public void setTaboo_desc(String taboo_desc) {
        this.taboo_desc = taboo_desc;
    }

    public String getOther_desc() {
        return other_desc;
    }

    public void setOther_desc(String other_desc) {
        this.other_desc = other_desc;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public long getModified() {
        return modified;
    }

    public void setModified(long modified) {
        this.modified = modified;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getIs_bill() {
        return is_bill;
    }

    public void setIs_bill(int is_bill) {
        this.is_bill = is_bill;
    }

    public ContactType getContactType() {
        return contactType;
    }

    public void setContactType(ContactType contactType) {
        this.contactType = contactType;
    }

    public SexType getSex() {
        return sex;
    }

    public void setSex(SexType sex) {
        this.sex = sex;
    }

    public int getOrder_count() {
        return order_count;
    }

    public void setOrder_count(int order_count) {
        this.order_count = order_count;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public OnlineStatus getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(OnlineStatus isOnline) {
        this.isOnline = isOnline;
    }

    public int getBgDrawableRes() {
        return bgDrawableRes;
    }

    public void setBgDrawableRes(int bgDrawableRes) {
        this.bgDrawableRes = bgDrawableRes;
    }
}

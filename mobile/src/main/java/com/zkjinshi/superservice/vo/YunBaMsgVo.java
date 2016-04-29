package com.zkjinshi.superservice.vo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 开发者：JimmyZhang
 * 日期：2016/2/26
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class YunBaMsgVo implements Serializable {

    /**
     {"arrivalTime":"98","userName":"李嘉诚","type":"GPS","userId":"c_192541b79e79a49e","content":"客人李嘉诚距离您还有500米"}
     */

    private String type;//GPS/BEACON
    private String title;
    private String content;
    private String webUrl;
    private String userId;
    private String userName;
    private String userImage;
    private String arrivalTime;//预计达到时间
    private String alert;
    private ArrayList<ItemTagVo> tags;

    public ArrayList<ItemTagVo> getTags() {
        return tags;
    }

    public void setTags(ArrayList<ItemTagVo> tags) {
        this.tags = tags;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}

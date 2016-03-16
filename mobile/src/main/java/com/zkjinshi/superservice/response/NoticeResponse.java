package com.zkjinshi.superservice.response;

import com.zkjinshi.superservice.vo.NoticeVo;
import com.zkjinshi.superservice.vo.ZoneVo;

import java.util.ArrayList;

/**
 * 区域列表响应实体
 * 开发者：JimmyZhang
 * 日期：2016/3/15
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class NoticeResponse extends BaseResponse {

    private ArrayList<NoticeVo> users;

    public ArrayList<NoticeVo> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<NoticeVo> users) {
        this.users = users;
    }
}

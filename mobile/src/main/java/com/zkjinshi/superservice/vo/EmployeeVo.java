package com.zkjinshi.superservice.vo;

import java.io.Serializable;

/**
 * Created by dujiande on 2016/3/11.
 */
public class EmployeeVo implements Serializable {

    private String username;
    private String phone;

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
}

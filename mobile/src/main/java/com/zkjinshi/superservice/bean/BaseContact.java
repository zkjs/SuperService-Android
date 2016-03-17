package com.zkjinshi.superservice.bean;

import java.io.Serializable;

/**
 * 联系人父类对象
 * 开发者：WinkyQin
 * 日期：2015/12/15
 */
public class BaseContact implements Serializable{

    private String sortKey;    //姓名全字母显示
    private String firstLetter;//排序首字母

    public String getSortKey() {
        return sortKey;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }

    public String getFirstLetter() {
        return firstLetter;
    }

    public void setFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter;
    }
}

package com.zkjinshi.filechoser.vo;

/**
 * 文件排序枚举
 * 开发者：JimmyZhang
 * 日期：2015/10/10
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public enum FileSort {

    MODIFY_TIME(0),//修改时间
    FILE_NAME(1);//文件名称

    private FileSort(int value){
        this.value = value;
    }

    private int value;

    public int getValue(){
        return value;
    }
}

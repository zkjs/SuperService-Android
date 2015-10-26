package com.zkjinshi.filechoser.filter;

import java.io.File;
import java.io.FileFilter;

/**
 * 电子表格文件过滤器
 * 开发者：JimmyZhang
 * 日期：2015/10/10
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class XlsFileFilter implements FileFilter {

    @Override
    public boolean accept(File file) {
        if(file.isDirectory()){
            return true;
        }else{
            String fileName = file.getName();
            if(fileName.endsWith(".xls") || fileName.endsWith(".xlsx")){
                return true;
            }
        }
        return false;
    }
}

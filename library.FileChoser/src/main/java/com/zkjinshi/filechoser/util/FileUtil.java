package com.zkjinshi.filechoser.util;

import java.text.SimpleDateFormat;

/**
 * 文件工具类
 * 开发者：JimmyZhang
 * 日期：2015/10/10
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class FileUtil {

    /**
     * 文件大小
     * @param size
     * @return
     */
    public static final String getFileSize(final long size) {
        if (size > 1073741824) {
            return String.format("%.2f", size / 1073741824.0) + " GB";
        } else if (size > 1048576) {
            return String.format("%.2f", size / 1048576.0) + " MB";
        } else if (size > 1024) {
            return String.format("%.2f", size / 1024.0) + " KB";
        } else {
            return size + " B";
        }

    }

    /**
     * 格式化文件时间
     * @param time
     * @return
     */
    public static final String getFileDate(final long time) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
    }

    /**
     * 判断文件是否为空
     * @param array
     * @return
     */
    public static boolean isNotEmpty(final Object[] array) {
        return length(array) > 0;
    }

    public static int length(final Object[] arr) {
        return arr != null ? arr.length : 0;
    }

}

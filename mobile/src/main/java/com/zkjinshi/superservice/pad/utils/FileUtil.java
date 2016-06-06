package com.zkjinshi.superservice.pad.utils;

import android.os.Environment;
import android.text.TextUtils;
import java.io.File;

/**
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class FileUtil {
    private FileUtil(){}

    private static FileUtil instance;

    public synchronized static FileUtil getInstance(){
        if(null ==  instance){
            instance = new FileUtil();
        }
        return instance;
    }

    public static final String FOLDER_SVIP = "com.zkjinshi.superservice.pad";
    public static final String FOLDER_AUDIO = "audios";
    public static final String FOLDER_IMAGE = "images";
    public static final String FOLDER_APPLICATION = "applications";
    public static final String FOLDER_VIDEO = "videos";
    public static final String FOLDER_TEMP_IMAGE = "temps";

    public static final String DIRPATH_AUDIO = Environment
            .getExternalStorageDirectory().getPath()
            + "/"
            + FOLDER_SVIP
            + "/"
            + FOLDER_AUDIO + "/";

    /**
     * 获取语音文件本地路径
     * @return
     */
    public String getAudioPath(){
        File file = new File(DIRPATH_AUDIO);
        if (!file.exists()) {
            file.mkdirs();
        }
        return DIRPATH_AUDIO;
    }

    public static final String DIRPATH_VIDEO = Environment
            .getExternalStorageDirectory().getPath()
            + "/"
            + FOLDER_SVIP
            + "/"
            + FOLDER_VIDEO + "/";

    /**
     * 获取语音文件本地路径
     * @return
     */
    public String getVideoPath(){
        File file = new File(DIRPATH_VIDEO);
        if (!file.exists()) {
            file.mkdirs();
        }
        return DIRPATH_VIDEO;
    }

    public static final String DIRPATH_IMAGE = Environment
            .getExternalStorageDirectory().getPath()
            + "/"
            + FOLDER_SVIP
            + "/"
            + FOLDER_IMAGE + "/";

    /**
     * 获取图片文件本地路径
     * @return
     */
    public String getImagePath(){
        File file = new File(DIRPATH_IMAGE);
        if (!file.exists()) {
            file.mkdirs();
        }
        return DIRPATH_IMAGE;
    }

    public static final String DIRPATH_TEMP_IMAGE = Environment
            .getExternalStorageDirectory().getPath()
            + "/"
            + FOLDER_SVIP
            + "/"
            + FOLDER_TEMP_IMAGE + "/";

    /**
     * 获取图片文件本地临时路径
     * @return
     */
    public String getImageTempPath(){
        File file = new File(DIRPATH_TEMP_IMAGE);
        if (!file.exists()) {
            file.mkdirs();
        }
        return DIRPATH_TEMP_IMAGE;
    }

    public static final String DIRPATH_APPLICATION = Environment
            .getExternalStorageDirectory().getPath()
            + "/"
            + FOLDER_SVIP
            + "/"
            + FOLDER_APPLICATION + "/";

    /**
     * 获取application文件路径
     * @return
     */
    public String getApplicationPath(){
        File file = new File(DIRPATH_APPLICATION);
        if (!file.exists()) {
            file.mkdirs();
        }
        return DIRPATH_APPLICATION;
    }


    public static final String DIRPATH_CAMERA =  Environment
            .getExternalStorageDirectory().getPath()+"/DCIM/Camera/";

    /**
     * 获取拍照相册路径
     * @return
     */
    public String getCameraPath(){
        File file = new File(DIRPATH_CAMERA);
        if (!file.exists()) {
            file.mkdirs();
        }
        return DIRPATH_CAMERA;
    }

    /**
     * 根据文件路径获取文件名称
     * @param filePath
     * @return
     */
    public String getFileName(String filePath){
        if (!TextUtils.isEmpty(filePath)) {
            return filePath.substring(filePath.lastIndexOf("/") + 1);
        }
        return null;
    }

}

package com.zkjinshi.superservice;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.superservice.utils.CacheUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 超级服务入口
 * 开发者：JimmyZhang
 * 日期：2015/9/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ServiceApplication extends Application{

    public static final String TAG = "ServiceApplication";

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this.getApplicationContext();
        saveConfig();
        initImageLoader();
        initCache();
    }

    public static Context getContext(){
        return mContext;
    }

    /**
     * 初始化配置
     */
    private void saveConfig(){
        try {
            File f = new File(this.getFilesDir(), "config.xml");
            InputStream is;
            if (f.exists()) {
                is = new FileInputStream(f);
                ConfigUtil.getInst(is);
            } else {
                is = this.getResources().getAssets()
                        .open("config.xml");
                ConfigUtil.getInst(is);
                ConfigUtil.getInst().save(this);
            }
        } catch (IOException e) {
            Log.e(TAG, "找不到assets/目录下的config.xml配置文件", e);
            e.printStackTrace();
        }
    }

    /**
     * 初始化ImageLoader
     */
    private void initImageLoader() {
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
    }

    /**
     * 初始化缓存
     */
    private void initCache(){
        CacheUtil.getInstance().init(this);
    }
}

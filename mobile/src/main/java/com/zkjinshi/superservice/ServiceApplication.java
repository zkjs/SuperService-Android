package com.zkjinshi.superservice;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.view.Gravity;

import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMGroupManager;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.testin.agent.TestinAgent;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.log.LogConfig;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogSwitch;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.BaseContext;
import com.zkjinshi.base.util.DeviceUtils;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.base.BaseApplication;
import com.zkjinshi.superservice.blueTooth.BlueToothManager;
import com.zkjinshi.superservice.emchat.EasemobIMHelper;
import com.zkjinshi.superservice.emchat.observer.EGroupReomveListener;
import com.zkjinshi.superservice.service.ActiveService;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.EmotionUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import io.yunba.android.manager.YunBaManager;

import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

/**
 * 超级服务入口
 * 开发者：JimmyZhang
 * 日期：2015/9/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ServiceApplication extends BaseApplication {

    public static final String TAG = ServiceApplication.class.getSimpleName();

    public int currentNetConfigVersion = 1;//网络配置项
    private BackgroundPowerSaver backgroundPowerSaver;
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this.getApplicationContext();
        initContext();
        initYunBa();
        initEmchat();
        initCache();
        saveConfig();
        initLog();
        initDevice();
        initFace();
        initImageLoader();
        initTest();
        //initActive();
        Fresco.initialize(this);

        BlueToothManager.getInstance().init(this);
        backgroundPowerSaver = new BackgroundPowerSaver(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Context getContext(){
        return mContext;
    }

    /**
     * 初始化云巴区域推送
     */
    private void initYunBa(){
        YunBaManager.start(getApplicationContext());
    }

    /**
     * 初始化环信聊天
     */
    private void initEmchat(){
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果app启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回
        if (processAppName == null ||!processAppName.equalsIgnoreCase(getPackageName())) {
            Log.e(TAG, "enter the service process!");
            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }
        EMChat.getInstance().init(this);
        //在做打包混淆时，要关闭debug模式，避免消耗不必要的资源
        EMChat.getInstance().setDebugMode(false);
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        // 默认环信是不维护好友关系列表的，如果app依赖环信的好友关系，把这个属性设置为true
        options.setUseRoster(false);
        // 设置是否需要已读回执
        options.setRequireAck(true);
        // 设置是否需要已送达回执
        options.setRequireDeliveryAck(true);
        // 设置从db初始化加载时, 每个conversation需要加载msg的个数
        options.setNumberOfMessagesLoaded(10);
        EasemobIMHelper.getInstance().initConnectionListener();
        EMGroupManager.getInstance().addGroupChangeListener(new EGroupReomveListener());
    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
               e.printStackTrace();
            }
        }
        return processName;
    }

    public void initContext(){
        BaseContext.getInstance().init(this);
    }

    /**
     * 初始化配置
     */
    private void saveConfig(){
        try {
            File f = new File(this.getFilesDir(), "config.xml");
            InputStream is;
            int netConfigVersion = CacheUtil.getInstance().getNetConfigVersion();
            if (f.exists()) {
                if(netConfigVersion != currentNetConfigVersion){
                    f.delete();
                    is = this.getResources().getAssets()
                            .open("config.xml");
                    ConfigUtil.getInst(is);
                    ConfigUtil.getInst().save(this);
                    CacheUtil.getInstance().setNetConfigVersion(currentNetConfigVersion);
                }else {
                    is = new FileInputStream(f);
                    ConfigUtil.getInst(is);
                }
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
     * 初始化缓存
     */
    private void initCache(){
        CacheUtil.getInstance().init(this);
    }

    /**
     * 初始化Log配置项
     */
    private void initLog() {
        LogConfig config = new LogConfig();
        config.setContext(this)
                .setLogSwitch(LogSwitch.OPEN)
                .setLogPath(
                        Environment.getExternalStorageDirectory() + "/"
                                + this.getPackageName() + "/log/");
        LogUtil.getInstance().init(config);
    }

    /**
     * 初始化ImageLoader
     */
    private void initImageLoader() {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(this);
        config.memoryCacheExtraOptions(1080, 780); // maxwidth, max height，即保存的每个缓存文件的最大长宽
        config.threadPoolSize(8);//线程池内加载的数量
        config.defaultDisplayImageOptions(DisplayImageOptions.createSimple());
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.memoryCache(new LruMemoryCache(2 * 1024 * 1024));
        config.memoryCacheSize(2 * 1024 * 1024);
        config.memoryCacheSizePercentage(13);
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(500 * 1024 * 1024);
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs();
        ImageLoader.getInstance().init(config.build());
    }

    /**
     * 初始化设备信息
     */
    private void initDevice(){
        DeviceUtils.init(this);
    }

    /**
     * 初始化云测试
     */
    private void initTest(){
        TestinAgent.init(this);
    }

    /**
     * 启动激活服务
     */
    private void initActive(){
        Intent intent = new Intent(this, ActiveService.class);
        intent.setAction("com.zkjinshi.superservice.ACTION_ACTIVE");
        startService(intent);
    }

    /**
     * 初始化表情库
     */
    private void initFace(){
        EmotionUtil.getInstance().initEmotion();
    }

}

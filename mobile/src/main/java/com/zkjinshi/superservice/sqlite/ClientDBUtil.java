package com.zkjinshi.superservice.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.superservice.ServiceApplication;
import com.zkjinshi.superservice.factory.ClientFactory;
import com.zkjinshi.superservice.vo.ClientVo;

/**
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientDBUtil {

    private final static String TAG = ClientDBUtil.class.getSimpleName();

    private Context      mContext;
    private DBOpenHelper helper;

    private static ClientDBUtil instance = null;

    private ClientDBUtil(){
    }

    public synchronized static ClientDBUtil getInstance(){
        if(instance == null){
            instance = new ClientDBUtil();
            instance.init();
        }
        return instance;
    }

    private void init() {
        mContext = ServiceApplication.getContext();
        helper   = new DBOpenHelper(mContext);
    }

    public long addClient(ClientVo clientVo){
        ContentValues values = ClientFactory.getInstance().buildAddContentValues(clientVo);
        long addResult    = -1;
        SQLiteDatabase db = null;
        try {
            db        = helper.getWritableDatabase();
            addResult = db.insert(DBOpenHelper.CLIENT_TBL, null, values);
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".addClient->" + e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return addResult;
    }
}

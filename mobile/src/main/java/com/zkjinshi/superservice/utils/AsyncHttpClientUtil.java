package com.zkjinshi.superservice.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.zkjinshi.superservice.activity.common.LoginActivity;
import com.zkjinshi.superservice.base.BaseApplication;
import com.zkjinshi.superservice.emchat.EasemobIMHelper;
import com.zkjinshi.superservice.manager.YunBaSubscribeManager;


/**
 * Created by dujiande on 2016/3/21.
 */
public class AsyncHttpClientUtil {

    public static void onFailure(Context context, int statusCode){
        if(statusCode == 401){
            BaseApplication.getInst().clear();
            CacheUtil.getInstance().setLogin(false);
            //移除云巴订阅推送
            YunBaSubscribeManager.getInstance().unSubscribe(context,null);
            //取消订阅别名
            YunBaSubscribeManager.getInstance().cancelAlias(context);
            //环信接口退出
            EasemobIMHelper.getInstance().logout();
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
            Toast.makeText(context,"Token 失效，请重新登录",Toast.LENGTH_SHORT).show();
        }else if(statusCode == 0){
            Toast.makeText(context,"网络超时",Toast.LENGTH_SHORT).show();
        }else if(statusCode == 408){
            Toast.makeText(context,"请求超时",Toast.LENGTH_SHORT).show();
        }else if(statusCode == 504){
            Toast.makeText(context,"网关超时",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context,"API 错误："+statusCode,Toast.LENGTH_SHORT).show();
        }

    }
}

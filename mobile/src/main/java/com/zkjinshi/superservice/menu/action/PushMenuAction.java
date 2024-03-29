package com.zkjinshi.superservice.menu.action;

import android.widget.Toast;

import com.zkjinshi.superservice.ServiceApplication;

/**
 * 消息推送指令
 * 开发者：JimmyZhang
 * 日期：2015/8/22
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class PushMenuAction implements MenuAction{

    @Override
    public void executeAction() {
        Toast.makeText(ServiceApplication.getContext(), "触发推送最新住房信息指令", Toast.LENGTH_LONG).show();
    }
}

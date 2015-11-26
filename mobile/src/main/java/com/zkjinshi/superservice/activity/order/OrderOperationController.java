package com.zkjinshi.superservice.activity.order;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.zkjinshi.superservice.R;

/**
 * 开发者：WinkyQin
 * 日期：2015/11/26
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderOperationController {

    private static OrderOperationController instance;

    private OrderOperationController(){}

    public static synchronized OrderOperationController getInstance(){
        if(null ==  instance){
            instance = new OrderOperationController();
        }
        return  instance;
    }

    private Context context;

    public void init(Context context){
        this.context = context;
    }

    /**
     * 显示选择图片对话框
     */
    public void showOrderOperationDialog(){

        final Dialog dlg = new Dialog(context, R.style.ActionTheme_DataSheet);
        LinearLayout layout = (LinearLayout) LayoutInflater.from(context).inflate(
                                            R.layout.dialog_order_operation, null);
        final int cFullFillWidth = 10000;
        layout.setMinimumWidth(cFullFillWidth);
        Button btnMyOrders = (Button) layout.findViewById(R.id.btn_my_orders);
        Button btnOrderNew = (Button) layout.findViewById(R.id.btn_order_new);
        Button btnCancel   = (Button) layout.findViewById(R.id.btn_cancel);

        //我的订单
        btnMyOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent orderNew = new Intent(context, OrderNewActivity.class );
                context.startActivity(orderNew);
                dlg.dismiss();
            }
        });

        //订单新增
        btnOrderNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent orderNew = new Intent(context, OrderNewActivity.class );
                context.startActivity(orderNew);
                dlg.dismiss();
            }
        });

        //取消
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });

        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(layout);
        dlg.show();
    }

}

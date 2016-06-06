package com.zkjinshi.superservice.pad.activity.set;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.superservice.pad.R;

/**
 * 开发者：WinkyQin
 * 日期：2016/1/26 0026
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientSelectOperator {

    private static ClientSelectOperator instance;

    private Dialog dlg;
    private ClientSelectOperator(){}

    public static synchronized ClientSelectOperator getInstance(){
        if(null ==  instance){
            instance = new ClientSelectOperator();
        }
        return  instance;
    }

    public void showOperationDialog(final Context context, String msg){

        dlg = new Dialog(context, R.style.window_dialog_style);
        LinearLayout layout = (LinearLayout) LayoutInflater.from(context).inflate(
                                                 R.layout.client_add_dialog, null);
        final int cFullFillWidth = 10000;
        layout.setMinimumWidth(cFullFillWidth);
        TextView tvErrMsg   = (TextView) layout.findViewById(R.id.tv_err_msg);
        Button   btnConfirm = (Button) layout.findViewById(R.id.btn_confirm);

        if(!TextUtils.isEmpty(msg)){
            tvErrMsg.setText(msg);
        }

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });

        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.horizontalMargin = DisplayUtil.dip2px(context, 20);
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(layout);
        dlg.show();
    }

}

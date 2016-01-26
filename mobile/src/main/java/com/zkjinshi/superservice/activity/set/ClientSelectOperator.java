package com.zkjinshi.superservice.activity.set;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.ProtocolUtil;

import org.dom4j.Text;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

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

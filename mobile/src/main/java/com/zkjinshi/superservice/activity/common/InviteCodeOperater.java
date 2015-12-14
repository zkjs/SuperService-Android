package com.zkjinshi.superservice.activity.common;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.tencent.mm.sdk.modelmsg.GetMessageFromWX;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.order.OrderNewActivity;
import com.zkjinshi.superservice.activity.set.TeamContactsActivity;
import com.zkjinshi.superservice.utils.Constants;

/**
 * 开发者：WinkyQin
 * 日期：2015/11/26
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class InviteCodeOperater {

    private static InviteCodeOperater instance;
    private IWXAPI  mWxApi;
    private InviteCodeOperater(){}

    public static synchronized InviteCodeOperater getInstance(){
        if(null ==  instance){
            instance = new InviteCodeOperater();
        }
        return  instance;
    }

    public void showOperationDialog(final Context context, final String inviteCode){

        final Dialog dlg   = new Dialog(context, R.style.ActionTheme_DataSheet);
        LinearLayout layout = (LinearLayout) LayoutInflater.from(context).inflate(
                                            R.layout.dialog_invite_code_operation, null);
        final int cFullFillWidth = 10000;
        layout.setMinimumWidth(cFullFillWidth);
        Button btnSMS    = (Button) layout.findViewById(R.id.btn_short_message);
        Button btnWeChat = (Button) layout.findViewById(R.id.btn_we_chat);
        Button btnCancel = (Button) layout.findViewById(R.id.btn_cancel);
        final String sms =  context.getString(R.string.your_invite_code)
                             + inviteCode + " 。"
                             + context.getString(R.string.share_invite_code)
                             + context.getString(R.string.download_link)
                             + "http://android.myapp.com/myapp/detail.htm?apkName=com.zkjinshi.svip";

        btnSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri smsToUri = Uri.parse("smsto:");
                Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
                intent.putExtra("sms_body", sms);
                context.startActivity(intent);
                dlg.dismiss();
            }
        });

        btnWeChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWxApi = WXAPIFactory.createWXAPI(context, Constants.WECHAT_APP_ID);
                mWxApi.registerApp(Constants.WECHAT_APP_ID);
                //TODO：判断是否预装微信app
                if(!mWxApi.isWXAppInstalled()){
                    DialogUtil.getInstance().showCustomToast(context, "尚未安装微信app", 0);
                    return ;
                }
                if(!mWxApi.isWXAppSupportAPI()){
                    DialogUtil.getInstance().showCustomToast(context, "您的当前的安卓版本不支持朋友圈发送", 0);
                    return ;
                }

                // 初始化一个WXTextObject对象
                WXTextObject textObj = new WXTextObject();
                textObj.text = sms;
                // 用WXTextObject对象初始化一个WXMediaMessage对象
                WXMediaMessage msg = new WXMediaMessage();
                msg.mediaObject = textObj;
                // 发送文本类型的消息时，title字段不起作用
                // msg.title = "Will be ignored";
                msg.description = sms;

                // 构造一个Req
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
                req.message = msg;
                req.scene   = SendMessageToWX.Req.WXSceneSession;
                // 调用api接口发送数据到微信
                mWxApi.sendReq(req);
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

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

}

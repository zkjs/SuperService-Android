package com.zkjinshi.superservice.pad.activity.common;

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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.pad.net.ExtNetRequestListener;
import com.zkjinshi.superservice.pad.net.MethodType;
import com.zkjinshi.superservice.pad.net.NetRequest;
import com.zkjinshi.superservice.pad.net.NetRequestTask;
import com.zkjinshi.superservice.pad.net.NetResponse;
import com.zkjinshi.superservice.pad.utils.CacheUtil;
import com.zkjinshi.superservice.pad.utils.Constants;
import com.zkjinshi.superservice.pad.utils.ProtocolUtil;
import com.zkjinshi.superservice.pad.vo.InviteCodeShareVo;
import com.zkjinshi.superservice.pad.R;
import com.zkjinshi.superservice.pad.response.InviteCodeShareResponse;

import java.io.ByteArrayOutputStream;

/**
 * 开发者：WinkyQin
 * 日期：2015/11/26
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class InviteCodeOperater {

    private static InviteCodeOperater instance;
    private IWXAPI mWxApi;
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
                if(TextUtils.isEmpty(inviteCode)){
                    DialogUtil.getInstance().showCustomToast(context, "邀请码不能为空", 0);
                    return ;
                } else {
                    makeInviteCodeUrl(context, inviteCode);
                    dlg.dismiss();
                }

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

    /**
     * 暂时不生成邀请码链接
     * @param inviteCode
     */
    private void makeInviteCodeUrl(final Context context, final String inviteCode) {

        NetRequest netRequest = new NetRequest(ProtocolUtil.getSalesCodeShareUrl());
        NetRequestTask netRequestTask = new NetRequestTask(context, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(context) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                super.onNetworkRequestError(errorCode, errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {
                super.onNetworkRequestCancelled();
            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                try {
                    InviteCodeShareResponse inviteCodeShareResponse = new Gson().fromJson(result.rawResult,InviteCodeShareResponse.class);
                    if(null != inviteCodeShareResponse){
                        int resultCode = inviteCodeShareResponse.getRes();
                        if(0 == resultCode){
                            InviteCodeShareVo inviteCodeShareVo = inviteCodeShareResponse.getData();
                            if(null != inviteCodeShareVo){
                                String shareUrl = inviteCodeShareVo.getJoinpage();
                                if(!TextUtils.isEmpty(shareUrl)){
                                    WXWebpageObject webpage = new WXWebpageObject();
                                    webpage.webpageUrl = shareUrl;
                                    WXMediaMessage msg = new WXMediaMessage(webpage);
                                    msg.title = CacheUtil.getInstance().getUserName() + "邀请您激活超级身份";
                                    msg.description = "激活超级身份，把您在某商家的会员保障扩展至全国，超过百家顶级商户和1000+名人工客服将竭诚为您服务。";

                                    int WX_THUMB_SIZE = 120;
                                    Bitmap thumb = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
                                    if(thumb == null){
                                        Toast.makeText(context, "图片不能为空", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Bitmap thumbBmp = Bitmap.createScaledBitmap(thumb, WX_THUMB_SIZE, WX_THUMB_SIZE, true);
                                        thumb.recycle();
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        thumbBmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                        msg.thumbData = baos.toByteArray();
                                    }

                                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                                    req.transaction = buildTransaction("webpage");
                                    req.message = msg;
                                    req.scene   = SendMessageToWX.Req.WXSceneSession;
                                    mWxApi.sendReq(req);
                                }
                            }
                        }else {
                            String resoutMsg = inviteCodeShareResponse.getResDesc();
                            if(!TextUtils.isEmpty(resoutMsg)){
                                DialogUtil.getInstance().showCustomToast(context,resoutMsg,Gravity.CENTER);
                            }
                        }
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void beforeNetworkRequestStart() {
                super.beforeNetworkRequestStart();
            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }
}

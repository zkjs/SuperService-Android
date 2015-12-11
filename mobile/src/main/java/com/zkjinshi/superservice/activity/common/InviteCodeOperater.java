package com.zkjinshi.superservice.activity.common;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.order.OrderNewActivity;
import com.zkjinshi.superservice.activity.set.TeamContactsActivity;

/**
 * 开发者：WinkyQin
 * 日期：2015/11/26
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class InviteCodeOperater {

    private static InviteCodeOperater instance;

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

        btnSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri smsToUri = Uri.parse("smsto:");
                Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
                intent.putExtra("sms_body", inviteCode);
                context.startActivity(intent);
            }
        });

        btnWeChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent orderNew = new Intent(context, OrderNewActivity.class );
//                context.startActivity(orderNew);
//                dlg.dismiss();
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

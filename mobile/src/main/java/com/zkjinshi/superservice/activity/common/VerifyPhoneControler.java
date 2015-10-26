package com.zkjinshi.superservice.activity.common;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.utils.SmsUtil;
import com.zkjinshi.superservice.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 开发者：dujiande
 *  手机验证
 * 日期：2015/9/22
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class VerifyPhoneControler {

    private final static int SMS_UNSEND         = 0;  //默认状态，尚未申请点击发送验证码
    private final static int SMS_SENDED         = 1;  //请求点击发送验证码状态
    private final static int SMS_VERIFY_SUCCESS = 2;  //用户验证码输入成功状态

    /** handler用于处理的消息代号 */
    private final static int SMS_COUNTING_DOWN  = 10; //倒计时进行中
    private final static int SMS_COUNT_OVER     = 11; //倒计时结束
    private final static int SEND_SMS_VERIFY    = 12; //发送短信验证码

    private int       mSmsVerifyStatus = SMS_UNSEND;//初始状态
    private int       mSmsCountSeconds = 60;//短信倒计时
    private Timer mTimer;//计数器
    private TimerTask mSmsCountTask;//执行倒计时

    private VerifyPhoneControler(){}
    private static VerifyPhoneControler instance;
    private Context context;
    private Activity activity;

    private EditText mInputPhone;
    private EditText  mVerifyCode;
    private Button mBtnConfirm;
    private ImageView mImgPhoneStatus;
    private ImageView mImgVerifyStatus;

    private Boolean   mSmsVerifySuccess = false;            //短信验证是否正确
    private Map<String, String> mPhoneVerifyMap;//指定手机对应验证码
    private Map<String, Object>       mResultMap;

    private SuccessCallBack successCallBack;

    public interface SuccessCallBack{
        public void verrifySuccess();
    }

    public void setSuccessCallBack(SuccessCallBack successCallBack) {
        this.successCallBack = successCallBack;
    }

    public static synchronized VerifyPhoneControler getInstance(){
        if(null ==  instance){
            instance = new VerifyPhoneControler();
        }
        return instance;
    }

    public void init(Context context){
        this.context = context;
        this.activity = (Activity)context;
        mSmsCountSeconds = 60;

        mInputPhone     = (EditText)    activity.findViewById(R.id.et_input_phone);
        mVerifyCode     = (EditText)    activity.findViewById(R.id.et_verify_code);
        mBtnConfirm     = (Button)      activity.findViewById(R.id.btn_send);
        mImgPhoneStatus  = (ImageView)  activity.findViewById(R.id.iv_phone_status);
        mImgVerifyStatus = (ImageView)  activity.findViewById(R.id.iv_verify_status);


        //注册按钮及发送验证码点击事件
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputPhone = mInputPhone.getText().toString();
                if (mSmsVerifySuccess) {
                    successCallBack.verrifySuccess();
                } else {
                    mVerifyCode.setHint("请输入验证码");//1.请求验证码
                    mVerifyCode.setFocusable(true);
                    mVerifyCode.setFocusableInTouchMode(true);
                    mVerifyCode.requestFocus();
                    mBtnConfirm.setText("正在发送中...");
                    sendVerifyCodeForPhone(inputPhone);//发送验证码
                }
            }
        });

        //手机号输入监听事件
        mInputPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mImgPhoneStatus.setVisibility(View.GONE);//图标隐藏

            }

            @Override
            public void afterTextChanged(Editable strPhone) {
                //1.监听手机输入
                String phoneNumber = strPhone.toString();
                if (phoneNumber.length() != 11) {
                    mBtnConfirm.setEnabled(false);
                } else {
                    if (!StringUtil.isPhoneNumber(phoneNumber)) {
                        mImgPhoneStatus.setVisibility(View.VISIBLE);
                        mImgPhoneStatus.setImageResource(R.mipmap.img_input_warning);
                        mBtnConfirm.setEnabled(false);
                    } else {
                        mImgPhoneStatus.setVisibility(View.VISIBLE);
                        mImgPhoneStatus.setImageResource(R.mipmap.img_input_right);
                        //手机号输入正确并且当前没有进入倒计时
                        if (mSmsCountSeconds >= 60) {
                            mBtnConfirm.setEnabled(true);
                        } else {
                            mBtnConfirm.setEnabled(false);
                        }
                    }
                }
            }
        });

        //验证码输入框添加输入监听事件
        mVerifyCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mImgVerifyStatus.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable inputVerifyCode) {
                String verifyCode = inputVerifyCode.toString();
                String phoneNumber = mInputPhone.getText().toString();
                if (inputVerifyCode.length() == 6) {
                    //确认手机号对应的验证码
                    if (StringUtil.isEquals(verifyCode, mPhoneVerifyMap.get(phoneNumber))) {
                        //设置验证码输入正确后的图标
                        mImgVerifyStatus.setVisibility(View.VISIBLE);
                        mImgVerifyStatus.setImageResource(R.mipmap.img_input_right);
                        mSmsVerifySuccess = true;//verify success
                        mSmsVerifyStatus = SMS_VERIFY_SUCCESS;
                        mBtnConfirm.setEnabled(true);
                        mBtnConfirm.setText("确定");
                        //输入正确切换按钮状态,关闭倒计时
                        if (mTimer != null) {
                            mTimer.cancel();//停止倒计时
                        }
                    } else {
                        mSmsVerifySuccess = false;//verify failed
                        mImgVerifyStatus.setVisibility(View.VISIBLE);
                        mImgVerifyStatus.setImageResource(R.mipmap.img_input_warning);
                    }
                }
            }
        });
    }

    /**
     * 向手机发送验证码
     * @param phoneNumber
     */
    private void sendVerifyCodeForPhone(final String phoneNumber) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //生成成员变量随机验证码
                String verifyCode = SmsUtil.getInstance().generateVerifyCode();
                Map<String, Object> result =  SmsUtil.getInstance().sendTemplateSMS(
                        phoneNumber, verifyCode);
                Message msg = Message.obtain();
                msg.what    = SEND_SMS_VERIFY;
                msg.obj     = result;
                Bundle bundle = new Bundle();
                bundle.putString("phone_number", phoneNumber);
                bundle.putString("verify_code", verifyCode);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }).start();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case  SEND_SMS_VERIFY:
                    mResultMap = (Map<String, Object>) msg.obj;
                    String statusCode = (String) mResultMap.get("statusCode");
                    //LogUtil.getInstance().info(LogLevel.INFO,  "短信验证码验证状态" + statusCode);
                    if("000000".equals(statusCode)){
                        //验证发送成功
                        mBtnConfirm.setEnabled(false);
                        if(mPhoneVerifyMap == null){
                            mPhoneVerifyMap = new HashMap<>();
                        } else {
                            mPhoneVerifyMap.clear();//清空之前数据
                        }

                        Bundle bundle = msg.getData();//获得手机和对应验证码
                        if(null != bundle){
                            String phoneNumber = (String) bundle.get("phone_number");
                            String verifyCode  = (String) bundle.get("verify_code");
                            mPhoneVerifyMap.put(phoneNumber, verifyCode);
                        }

                        mSmsVerifyStatus = SMS_SENDED;//验证码请求成功开启倒计时
                        if(mTimer != null){
                            mTimer.cancel();
                            mTimer = null;
                        }
                        if(mSmsCountTask != null){
                            mSmsCountTask.cancel();
                            mSmsCountTask = null;
                        }
                        mTimer = new Timer();
                        mSmsCountTask = new SmsCountTask();
                        mTimer.schedule(mSmsCountTask, 1000, 1000);
                    } else {
                        //验证码发送失败
                        if(mInputPhone.getText().toString().length() >= 11){
                            mBtnConfirm.setEnabled(true);
                        }
                        mSmsCountSeconds = 60;//重新置为60s
                        mBtnConfirm.setText("发送失败，重新发送？");
                    }
                    break;
                case SMS_COUNTING_DOWN:
                    int countSeconds = msg.arg1;
                    mBtnConfirm.setText("倒计时:"+countSeconds+"s");
                    break;
                case SMS_COUNT_OVER:
                    if(mTimer != null){
                        mTimer.cancel();//停止
                    }
                    mSmsCountSeconds = 60;//重新置为60s
                    mBtnConfirm.setText("重新发送？");
                    //验证码发送失败
                    if(mInputPhone.getText().toString().length() >= 11){
                        mBtnConfirm.setEnabled(true);
                    }
                    break;
            }
        }
    };

   public void stopTimer(){
        if(mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }
        if(mSmsCountTask != null){
            mSmsCountTask.cancel();
            mSmsCountTask = null;
        }
    }

    /**
     * 短信倒数计时器
     */
    class SmsCountTask extends TimerTask{
        @Override
        public void run() {
            //当前验证码已发送，倒数计时中
            if(mSmsCountSeconds > 0 && mSmsVerifyStatus == SMS_SENDED){
                mSmsCountSeconds--;
                Message msg = Message.obtain();
                msg.what    = SMS_COUNTING_DOWN;
                msg.arg1    = mSmsCountSeconds;//倒数时间
                handler.sendMessage(msg);
            } else {
                Message msg = Message.obtain();
                msg.what    = SMS_COUNT_OVER;
                handler.sendMessage(msg);
                mSmsCountSeconds = 0;//短信倒数置为0
            }
        }
    }

}

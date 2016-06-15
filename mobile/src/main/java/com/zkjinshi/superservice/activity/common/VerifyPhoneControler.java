package com.zkjinshi.superservice.activity.common;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.manager.SSOManager;
import com.zkjinshi.superservice.manager.YunBaSubscribeManager;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.response.BasePavoResponse;
import com.zkjinshi.superservice.sqlite.DBOpenHelper;
import com.zkjinshi.superservice.utils.AESUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.PavoUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.utils.SmsUtil;
import com.zkjinshi.superservice.utils.StringUtil;
import com.zkjinshi.superservice.vo.IdentityType;
import com.zkjinshi.superservice.vo.PayloadVo;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 开发者：dujiande
 *  手机验证
 * 日期：2015/9/22
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class VerifyPhoneControler {

    private final static String TAG = VerifyPhoneControler.class.getSimpleName();

    private final static int SMS_UNSEND         = 0;  //默认状态，尚未申请点击发送验证码
    private final static int SMS_SENDED         = 1;  //请求点击发送验证码状态
    private final static int SMS_VERIFY_SUCCESS = 2;  //用户验证码输入成功状态

    /** handler用于处理的消息代号 */
    private final static int SMS_COUNTING_DOWN  = 10; //倒计时进行中
    private final static int SMS_COUNT_OVER     = 11; //倒计时结束
    private final static int SEND_SMS_VERIFY    = 12; //发送短信验证码
    private final static int SEND_SMS_RECEIVE    = 13; //获取短信验证码

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

    private Boolean   mSmsVerifySuccess;            //短信验证是否正确

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
        mSmsVerifySuccess = false;

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
                String code = mVerifyCode.getText().toString();
                if (mSmsVerifySuccess) {
                    getToken(inputPhone,code);
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
                if(!TextUtils.isEmpty(phoneNumber)){
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

                }
            }
        });
    }

    /**
     * 向手机发送验证码
     * @param phoneNumber
     */
    private void sendVerifyCodeForPhone(final String phoneNumber) {
        try{
            String url = ProtocolUtil.ssoVcode();
            NetRequest netRequest = new NetRequest(url);
            HashMap<String,Object> bizMap = new HashMap<String,Object>();
            String phoneStr = AESUtil.encrypt(phoneNumber, AESUtil.PAVO_KEY);
            bizMap.put("phone",phoneStr);
            netRequest.setObjectParamMap(bizMap);
            NetRequestTask netRequestTask = new NetRequestTask(context,netRequest, NetResponse.class);
            netRequestTask.methodType = MethodType.JSONPOST;
            netRequestTask.setNetRequestListener(new ExtNetRequestListener(context) {
                @Override
                public void onNetworkRequestError(int errorCode, String errorMessage) {
                    Log.i(TAG, "errorCode:" + errorCode);
                    Log.i(TAG, "errorMessage:" + errorMessage);
                    sendCodeFail();
                }

                @Override
                public void onNetworkRequestCancelled() {

                }

                @Override
                public void onNetworkResponseSucceed(NetResponse result) {
                    super.onNetworkResponseSucceed(result);
                    try{
                        BasePavoResponse basePavoResponse = new Gson().fromJson(result.rawResult,BasePavoResponse.class);
                        if(basePavoResponse != null){
                            if(basePavoResponse.getRes() == 0){
                                handler.sendEmptyMessage(SEND_SMS_VERIFY);
                            }else{
                                PavoUtil.showErrorMsg(context,basePavoResponse.getResDesc());
                                sendCodeFail();
                            }
                        }

                    }catch (Exception e){
                        sendCodeFail();
                        e.printStackTrace();
                    }
                }

                @Override
                public void beforeNetworkRequestStart() {

                }
            });
            netRequestTask.isShowLoadingDialog = true;
            netRequestTask.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendCodeFail(){
        //验证码发送失败
        if(mInputPhone.getText().toString().length() >= 11){
            mBtnConfirm.setEnabled(true);
        }
        mSmsCountSeconds = 60;//重新置为60s
        mBtnConfirm.setText("发送失败，重新发送？");
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case  SEND_SMS_VERIFY:
                    //验证发送成功
                    mBtnConfirm.setEnabled(false);
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
                case SEND_SMS_RECEIVE:
                    if(null != mVerifyCode){
                        String vertifyCode = msg.obj.toString();
                        mVerifyCode.setText(vertifyCode);
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

    private String strContent;
    private String patternCoder = "(?<!--\\d)\\d{6}(?!\\d)";

    /**
     * 匹配短信中间的6个数字（验证码等）
     *
     * @param patternContent
     * @return
     */
    private String patternCode(String patternContent) {
        if (TextUtils.isEmpty(patternContent)) {
            return null;
        }
        Pattern p = Pattern.compile(patternCoder);
        Matcher matcher = p.matcher(patternContent);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    /**
     * 使用手机验证码创建Token
     * @param phone
     * @param code
     */
    private void getToken(final String phone,final String code){
        LoginController.getInstance().getTokenByCode(context, phone, code, new LoginController.CallBackListener() {
            @Override
            public void successCallback(JSONObject response) {
                try {
                    String token = response.getString("token");
                    CacheUtil.getInstance().setExtToken(token);
                    PayloadVo payloadVo = SSOManager.getInstance().decodeToken(token);
                    if(null != payloadVo){
                        Set<String> featureSet = payloadVo.getFeatures();
                        if(null != featureSet){
                            CacheUtil.getInstance().setFeatures(featureSet);
                        }
                    }
                    CacheUtil.getInstance().setUserId(payloadVo.getSub());
                    CacheUtil.getInstance().setLoginIdentity(IdentityType.WAITER);
                    DBOpenHelper.DB_NAME = payloadVo.getSub() + ".db";
                    successCallBack.verrifySuccess();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

}

package com.zkjinshi.superservice.activity.set;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestListener;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 开发者：vincent
 * 日期：2015/10/8
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class TagAddActivity extends Activity {

    private final static String TAG = TagAddActivity.class.getSimpleName();

    private EditText mEtInputTag;
    private Button   mBtnConfirm;
    private Button   mBtnCancel;

    private String   mPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_add);

        initView();
        initData();
        initListener();
    }

    private void initListener() {
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputTag = mEtInputTag.getText().toString().trim();
                if(TextUtils.isEmpty(inputTag)){
                    DialogUtil.getInstance().showToast(TagAddActivity.this, getString(R.string.please_input_the_tag_you_want));
                    return ;
                }

                if(!TextUtils.isEmpty(mPhoneNumber)){
                    addNewTag(mPhoneNumber, inputTag);
                }
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TagAddActivity.this.finish();
            }
        });
    }

    private void initView() {
        mEtInputTag = (EditText)findViewById(R.id.et_input_tag);
        mBtnConfirm = (Button)findViewById(R.id.btn_confirm);
        mBtnCancel  = (Button)findViewById(R.id.btn_cancel);
    }

    private void initData() {
        mPhoneNumber = getIntent().getStringExtra("phone_number");
    }

    //提交资料
    public void addNewTag(String phone, final String tag){

        String userID = CacheUtil.getInstance().getUserId();
        String token  = CacheUtil.getInstance().getToken();

        NetRequest netRequest = new NetRequest(ProtocolUtil.getAddTagUrl());
        HashMap<String,String> bizMap = new HashMap<>();
        bizMap.put("salesid", userID);
        bizMap.put("token", token);
        bizMap.put("phone", phone);
        bizMap.put("tag", tag);
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new NetRequestListener() {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                DialogUtil.getInstance().cancelProgressDialog();
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {
                DialogUtil.getInstance().cancelProgressDialog();

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                DialogUtil.getInstance().cancelProgressDialog();
                String jsonResult = result.rawResult;
                try {
                    JSONObject json = new JSONObject(jsonResult);
                    Boolean setResult = json.getBoolean("set");
                    if (setResult) {
                        // TODO:弹出添加对话框
                        DialogUtil.getInstance().showToast(TagAddActivity.this, TagAddActivity.this.
                                                             getString(R.string.add_tag_successed));
                    } else {
                        //弹出添加失败提醒
                        DialogUtil.getInstance().showToast(TagAddActivity.this, TagAddActivity.this.
                                                                 getString(R.string.add_tag_failed));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void beforeNetworkRequestStart() {
                //网络请求前
            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }
}

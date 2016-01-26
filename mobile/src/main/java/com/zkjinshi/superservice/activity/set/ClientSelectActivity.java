package com.zkjinshi.superservice.activity.set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.bean.ClientBaseBean;
import com.zkjinshi.superservice.bean.ClientBindBean;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;

import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 新增客户联系人
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientSelectActivity extends Activity {

    private final static String TAG = ClientSelectActivity.class.getSimpleName();

    private String          mShopID;
    private RelativeLayout  mRlBack;
    private TextView        mTvTitle;
    private EditText        mEtClientPhone;
    private Button          mBtnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_select);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mRlBack = (RelativeLayout) findViewById(R.id.rl_back);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvTitle.setText(getString(R.string.add_clients));
        mEtClientPhone = (EditText) findViewById(R.id.et_client_phone);
        mBtnSearch = (Button) findViewById(R.id.btn_search_client);
    }

    private void initData() {
        mShopID = CacheUtil.getInstance().getShopID();
    }

    private void initListener() {

        mRlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientSelectActivity.this.finish();
            }
        });

        /**
         * 执行客户查询
         */
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mEtClientPhone.getText().toString().trim();
                checkPhone(phone);

                //查询客户信息，判断是否存在专属客服
                ClientController.getInstance().getBindUserInfo(
                    ClientSelectActivity.this,
                    mShopID,
                    phone,
                    new ExtNetRequestListener(ClientSelectActivity.this) {
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

                            Log.i(TAG, "result.rawResult:" + result.rawResult);
                            DialogUtil.getInstance().cancelProgressDialog();
                            String jsonResult = result.rawResult;

                            if(!TextUtils.isEmpty(jsonResult)) {
                                Gson gson = new Gson();
                                ClientBindBean clientBean = gson.fromJson(jsonResult, ClientBindBean.class);
                                if(clientBean != null && clientBean.isSet()){
                                    //是否使用过邀请码
                                    boolean isBind = clientBean.is_bd();
                                   if(isBind){
                                       //专属客户ID
                                       String salesID = clientBean.getSalesid();
                                       if(TextUtils.isEmpty(salesID)){
                                           //本店尚未绑定专属客服 进入绑定界面
                                           Intent clienBind = new Intent(ClientSelectActivity.this,
                                                                         ClientBindActivity.class);
                                           clienBind.putExtra("client_bean", clientBean);
                                           ClientSelectActivity.this.startActivity(clienBind);
                                           ClientSelectActivity.this.finish();
                                       } else {
                                           //本店已存在专属客服，不可以绑定
                                           ClientSelectOperator.getInstance().showOperationDialog(
                                           ClientSelectActivity.this,
                                           getString(R.string.client_already_be_added));
                                       }

                                   } else {
                                       //客户尚未使用邀请码
                                       ClientSelectOperator.getInstance().showOperationDialog(
                                       ClientSelectActivity.this,
                                       getString(R.string.please_use_invite_code_to_add_the_client));
                                   }
                                } else {
                                    //查询客户失败失败
                                    ClientSelectOperator.getInstance().showOperationDialog(
                                    ClientSelectActivity.this,
                                    getString(R.string.select_client_failed_try_it_later));
                                }
                            }
                        }

                        @Override
                        public void beforeNetworkRequestStart() {
                            super.beforeNetworkRequestStart();
                        }
                    }
                );
            }
        });
    }

    /**
     * 手机号码检查格式
     * @param phone
     */
    private void checkPhone(String phone) {
        if(TextUtils.isEmpty(phone)){
            DialogUtil.getInstance().showCustomToast(
                    ClientSelectActivity.this,
                    "手机号不能为空!", Gravity.CENTER);
            return ;
        }else if(!StringUtil.isPhoneNumber(phone)){
            DialogUtil.getInstance().showCustomToast(
                    ClientSelectActivity.this,
                    "手机号格式不正确!", Gravity.CENTER);
            return ;
        }
    }

}

package com.zkjinshi.superservice.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.sqlite.ClientDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.view.CircleImageView;
import com.zkjinshi.superservice.vo.ClientVo;
import com.zkjinshi.superservice.vo.OnAccountStatus;

/**
 * 新增客户联系人
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientAddActivity extends Activity {

    private final static String TAG = ClientAddActivity.class.getSimpleName();

    private final static int CLIENT_ADD_RESULT = 0x00;

    private CircleImageView mCivClientAvatar;
    private EditText        mEtClientName;
    private EditText        mEtClientPhone;
    private EditText        mEtClientCompany;
    private EditText        mEtClientPosition;
    private CheckBox        mCbMemberOnAccount;
    private Button          mBtnConfirm;

    private ClientVo        mClientVo;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CLIENT_ADD_RESULT:
                    Bundle bundle = msg.getData();
                    long addResult = bundle.getLong("add_result");
                    if(addResult > 0){
                        DialogUtil.getInstance().showToast(ClientAddActivity.this, "添加新客户成功!");
                    } else {
                        DialogUtil.getInstance().showToast(ClientAddActivity.this, "添加失败! 请重新添加");
                    }
                    break ;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_add);

        initView();
        initData();
        initListener();
    }

    private void initView() {

        mCivClientAvatar    = (CircleImageView) findViewById(R.id.civ_client_avatar);
        mEtClientName       = (EditText) findViewById(R.id.et_client_name);
        mEtClientPhone      = (EditText) findViewById(R.id.et_client_phone);
        mEtClientCompany    = (EditText) findViewById(R.id.et_client_company);
        mEtClientPosition   = (EditText) findViewById(R.id.et_client_position);
        mCbMemberOnAccount  = (CheckBox) findViewById(R.id.cb_member_on_account);
        mBtnConfirm         = (Button) findViewById(R.id.btn_confirm);
    }

    private void initData() {

        mClientVo = new ClientVo();
        mClientVo.setOnAccount(OnAccountStatus.ISONACCOUNT);
        CacheUtil.getInstance().init(this);
        AvatarChooseController.getInstance().init(this);
    }

    private void initListener() {
        mCivClientAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出图片选项 1.相册 2. 拍照
                AvatarChooseController.getInstance().showChoosePhotoDialog();
            }
        });

        mCbMemberOnAccount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mClientVo.setOnAccount(OnAccountStatus.ISONACCOUNT);
                } else {
                    mClientVo.setOnAccount(OnAccountStatus.NOTONACCOUNT);
                }
            }
        });

        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 1.check the must input data
                checkInput();
                mClientVo.setClientID(System.currentTimeMillis() + "");//临时客户ID，服务器获取后更新
                //TODO 2. add the client into the db by http request
                //TODO 3.add to local db
                long addResult = ClientDBUtil.getInstance().addClient(mClientVo);
                Message msg = Message.obtain();
                msg.what = CLIENT_ADD_RESULT;
                Bundle bundle = new Bundle();
                bundle.putLong("add_result", addResult);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AvatarChooseController.getInstance().onActivityResult(requestCode, resultCode, data, mCivClientAvatar);
    }

    /** check the must input data */
    private void checkInput() {

        String clientName = mEtClientName.getText().toString();
        if(TextUtils.isEmpty(clientName)){
            DialogUtil.getInstance().showToast(ClientAddActivity.this, getString(R.string.name) +
                                               getString(R.string.not_input_yet));
            return ;
        }
        mClientVo.setClientName(clientName);

        String clientPhone = mEtClientPhone.getText().toString();
        if(TextUtils.isEmpty(clientPhone)){
            DialogUtil.getInstance().showToast(ClientAddActivity.this, getString(R.string.phone) +
                                               getString(R.string.not_input_yet));
            return ;
        }
        mClientVo.setClientPhone(clientPhone);

        String clientCompany = mEtClientCompany.getText().toString();
        if(!TextUtils.isEmpty(clientCompany)){
            mClientVo.setClientCompany(clientCompany);
        }

        String clientPosition = mEtClientPosition.getText().toString();
        if(!TextUtils.isEmpty(clientPosition)){
            mClientVo.setClientPosition(clientPosition);
        }
    }

}

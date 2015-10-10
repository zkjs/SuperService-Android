package com.zkjinshi.superservice.activity.set;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.sqlite.UnRegClientDBUtil;
import com.zkjinshi.superservice.vo.IsBill;
import com.zkjinshi.superservice.vo.UnRegClientVo;

/**
 * 新增客户联系人
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientAddActivity extends Activity {

    private final static String TAG = ClientAddActivity.class.getSimpleName();

    private final static int CLIENT_ADD_RESULT    = 0x00;
    private final static int CLIENT_UPDATE_RESULT = 0x01;

    private UnRegClientVo   mUnRegClient;
    private ImageButton     mIbtnBack;
    private EditText        mEtClientName;
    private TextView        mTvClientPhone;
    private EditText        mEtClientCompany;
    private EditText        mEtClientPosition;
    private EditText        mEtClientRemark;
    private CheckBox        mCbMemberOnAccount;
    private Button          mBtnConfirm;

    private String          mPhoneNumber;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CLIENT_ADD_RESULT:
                    Bundle addBundle = msg.getData();
                    long addResult   = addBundle.getLong("add_result");
                    if(addResult > 0){
                        DialogUtil.getInstance().showToast(ClientAddActivity.this, "添加新客户成功!");
                        ClientAddActivity.this.finish();
                    } else {
                        DialogUtil.getInstance().showToast(ClientAddActivity.this, "添加失败! 请重试");
                    }
                    break ;

                case CLIENT_UPDATE_RESULT:
                    Bundle updateBundle = msg.getData();
                    long updateResult = updateBundle.getLong("update_result");
                    if(updateResult > 0){
                        DialogUtil.getInstance().showToast(ClientAddActivity.this, "更新客户信息成功!");
                        ClientAddActivity.this.finish();
                    } else {
                        DialogUtil.getInstance().showToast(ClientAddActivity.this, "更新失败! 请重试");
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
        mIbtnBack           = (ImageButton) findViewById(R.id.ibtn_back);
        mEtClientName       = (EditText) findViewById(R.id.et_client_name);
        mTvClientPhone      = (TextView) findViewById(R.id.tv_client_phone);
        mEtClientCompany    = (EditText) findViewById(R.id.et_client_company);
        mEtClientPosition   = (EditText) findViewById(R.id.et_client_position);
        mEtClientRemark     = (EditText) findViewById(R.id.et_client_remark);
        mCbMemberOnAccount  = (CheckBox) findViewById(R.id.cb_member_on_account);
        mBtnConfirm         = (Button) findViewById(R.id.btn_confirm);
    }

    private void initData() {
        mPhoneNumber = getIntent().getStringExtra("phone_number");
        if(!TextUtils.isEmpty(mPhoneNumber)){
            mTvClientPhone.setText(mPhoneNumber);
        }
        if(UnRegClientDBUtil.getInstance().isUnRegClientExistByPhone(mPhoneNumber)){
            UnRegClientVo unRegClient = UnRegClientDBUtil.getInstance().findUnRegClientByPhone(mPhoneNumber);
            showUnRegClient(unRegClient);
        }
    }

    /**
     * 显示当前信息
     * @param unRegClient
     */
    private void showUnRegClient(UnRegClientVo unRegClient) {
        mEtClientName.setText(unRegClient.getUsername());
        mEtClientCompany.setText(unRegClient.getCompany());
        mEtClientPosition.setText(unRegClient.getPosition());
        mEtClientRemark.setText(unRegClient.getOther_desc());
        if(unRegClient.getIs_bill() == IsBill.ISONACCOUNT.getValue()){
            mCbMemberOnAccount.setChecked(true);
        }else {
            mCbMemberOnAccount.setChecked(false);
        }
    }

    private void initListener() {
        //返回上一页
        mIbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientAddActivity.this.finish();
            }
        });

        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String clientName = mEtClientName.getText().toString().trim();
                if(TextUtils.isEmpty(clientName)){
                    DialogUtil.getInstance().showToast(ClientAddActivity.this, getString(R.string.name) +
                                                       getString(R.string.not_input_yet));
                    return ;
                }
                String clientCompany  = mEtClientCompany.getText().toString();
                String clientPosition = mEtClientPosition.getText().toString();
                String clientRemark   = mEtClientRemark.getText().toString();
                int  isBill;
                if(!mCbMemberOnAccount.isChecked()){
                    isBill = IsBill.NOTONACCOUNT.getValue();
                }else {
                    isBill = IsBill.ISONACCOUNT.getValue();
                }
                mUnRegClient = new UnRegClientVo();
                mUnRegClient.setPhone(mPhoneNumber);
                mUnRegClient.setUsername(clientName);
                mUnRegClient.setCompany(clientCompany);
                mUnRegClient.setPosition(clientPosition);
                mUnRegClient.setOther_desc(clientRemark);
                mUnRegClient.setIs_bill(isBill);
                addNewClient(mUnRegClient);
            }
        });
    }

    /**
     * 添加尚未注册的用户
     * @param unRegClientVoclient
     */
    private void addNewClient(UnRegClientVo unRegClientVoclient) {
        // 1.add the unregister client to local db
        if(!UnRegClientDBUtil.getInstance().isUnRegClientExistByPhone(unRegClientVoclient.getPhone())){
            long addResult = UnRegClientDBUtil.getInstance().addUnRegClient(unRegClientVoclient);
            Message msg = Message.obtain();
            msg.what = CLIENT_ADD_RESULT;
            Bundle bundle = new Bundle();
            bundle.putLong("add_result", addResult);
            msg.setData(bundle);
            handler.sendMessage(msg);
        } else {
            long updateResult = UnRegClientDBUtil.getInstance().updateUnRegClient(unRegClientVoclient);
            Message msg = Message.obtain();
            msg.what = CLIENT_UPDATE_RESULT;
            Bundle bundle = new Bundle();
            bundle.putLong("update_result", updateResult);
            msg.setData(bundle);
            handler.sendMessage(msg);
        }

    }
}

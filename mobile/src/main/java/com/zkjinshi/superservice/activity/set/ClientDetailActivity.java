package com.zkjinshi.superservice.activity.set;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.common.MainActivity;
import com.zkjinshi.superservice.activity.common.MoreActivity;
import com.zkjinshi.superservice.activity.common.TagAddActivity;
import com.zkjinshi.superservice.bean.ClientDetailBean;
import com.zkjinshi.superservice.bean.SempLoginBean;
import com.zkjinshi.superservice.factory.UserFactory;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestListener;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.net.RequestUtil;
import com.zkjinshi.superservice.sqlite.ClientDBUtil;
import com.zkjinshi.superservice.sqlite.DBOpenHelper;
import com.zkjinshi.superservice.sqlite.UserDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.view.CircleImageView;
import com.zkjinshi.superservice.vo.UserVo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客人详细信息
 * 开发者：vincent
 * 日期：2015/10/5
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientDetailActivity extends Activity {

    private final static String TAG = ClientDetailActivity.class.getSimpleName();

    private String      mPhoneNumber;

    private ImageButton mIbtnBack;
    private ImageButton mIbtnDianhua;
    private ImageButton mIbtnDuiHua;
    private TextView    mTvMemberName;
    private TextView    mTvMemberPhone;
    private TextView    mTvMemberLevel;
    private TextView    mTvMemberType;
    private TextView    mTvRecordTimes;
    private GridView    mGvPreference;
    private GridView    mGvPrivilegeInfo;
    private GridView    mGvTags;
    private EditText    mEtRemark;

    private CircleImageView mCivMemberAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_detail);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mIbtnBack        = (ImageButton)     findViewById(R.id.ibtn_back);
        mCivMemberAvatar = (CircleImageView) findViewById(R.id.civ_member_avatar);
        mTvMemberName    = (TextView)        findViewById(R.id.tv_member_name);
        mTvMemberPhone   = (TextView)        findViewById(R.id.tv_member_phone);
        mIbtnDianhua     = (ImageButton)     findViewById(R.id.ibtn_dianhua);
        mTvMemberLevel   = (TextView)        findViewById(R.id.tv_member_level);
        mIbtnDuiHua      = (ImageButton)     findViewById(R.id.ibtn_duihua);
        mTvMemberType    = (TextView)        findViewById(R.id.tv_member_type);
        mTvRecordTimes   = (TextView)        findViewById(R.id.tv_record_times);
        mGvPreference    = (GridView)        findViewById(R.id.gv_preference_info);
        mGvPrivilegeInfo = (GridView)        findViewById(R.id.gv_privilege_info);
        mGvTags          = (GridView)        findViewById(R.id.gv_tags);
        mEtRemark        = (EditText)        findViewById(R.id.et_remark);
    }

    private void initData() {
        mPhoneNumber  = getIntent().getStringExtra("phone_number");
        DialogUtil.getInstance().showProgressDialog(ClientDetailActivity.this);
        getClientDetail(mPhoneNumber);
    }

    private void getClientDetail(String mPhoneNumber) {
        String userID = CacheUtil.getInstance().getUserId();
        String token  = CacheUtil.getInstance().getToken();

        NetRequest netRequest = new NetRequest(ProtocolUtil.getClientDetailUrl());
        HashMap<String,String> bizMap = new HashMap<>();
        bizMap.put("empid", userID);
        bizMap.put("token", token);
        bizMap.put("phone", mPhoneNumber);
        bizMap.put("set", "9");
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.POST;
        netRequestTask.setNetRequestListener(new NetRequestListener() {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
                //网络请求异常
                DialogUtil.getInstance().showToast(ClientDetailActivity.this, "获取用户详细信息失败");
                ClientDetailActivity.this.finish();
                DialogUtil.getInstance().cancelProgressDialog();
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                LogUtil.getInstance().info(LogLevel.INFO, "result:" + result.rawResult);
                Gson gson = new Gson();
                ClientDetailBean client = gson.fromJson(result.rawResult, ClientDetailBean.class);
                DialogUtil.getInstance().cancelProgressDialog();
                //显示用户详细信息
                showClient(client);
            }

            @Override
            public void beforeNetworkRequestStart() {
                //网络请求前
            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

    /**
     * 显示会员信息在界面上
     * @param client
     */
    private void showClient(ClientDetailBean client) {
        String userID = client.getUserid();
        if(!TextUtils.isEmpty(userID)) {
            String imageUrl = Constants.GET_USER_AVATAR + userID + ".jpg";
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.img_hotel_zhanwei)
                .showImageForEmptyUri(R.drawable.img_hotel_zhanwei)
                .showImageOnFail(R.drawable.img_hotel_zhanwei)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
            ImageLoader.getInstance().displayImage(imageUrl, mCivMemberAvatar, options);
        }
        mTvMemberName.setText(client.getUsername());
        mTvMemberPhone.setText(client.getPhone());
        if(client.getIs_bill() == 1) {
            mTvMemberType.setText(getString(R.string.debt_member));
        } else {
            mTvMemberType.setText(getString(R.string.not_debt_member));
        }
        mTvRecordTimes.setText(client.getOrder_count()+"");
        final List<ClientDetailBean.Tag> tags = client.getTags();

        if(null != tags && !tags.isEmpty()){
            ClientDetailBean.Tag tag = new ClientDetailBean().new Tag();
            tag.tag = "add";
            tags.add(tag);
            mGvTags.setAdapter(new BaseAdapter() {
                @Override
                public int getCount() {
                    return tags.size();
                }

                @Override
                public Object getItem(int position) {
                    return null;
                }

                @Override
                public long getItemId(int position) {
                    return 0;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    TextView text = new TextView(ClientDetailActivity.this);
                    text.setText(tags.get(position).tag);
                    text.setGravity(Gravity.CENTER);
                    return text;
                }
            });

            mGvTags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == tags.size()-1) {
                        //TODO:
                        Intent addNewTag= new Intent(ClientDetailActivity.this, TagAddActivity.class);
                        ClientDetailActivity.this.startActivity(addNewTag);
                    }
                }
            });

        }
    }

    private void initListener() {
        mIbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientDetailActivity.this.finish();
            }
        });

        mIbtnDianhua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //TODO: 打电话
            }
        });

        mIbtnDuiHua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: 发起聊天
            }
        });


    }
}

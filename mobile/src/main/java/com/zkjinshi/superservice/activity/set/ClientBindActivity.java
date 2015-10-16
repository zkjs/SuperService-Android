package com.zkjinshi.superservice.activity.set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.bean.ClientDetailBean;
//import com.zkjinshi.superservice.factory.UnRegClientFactory;
import com.zkjinshi.superservice.factory.ClientFactory;
import com.zkjinshi.superservice.factory.UnRegClientFactory;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestListener;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.sqlite.ClientDBUtil;
import com.zkjinshi.superservice.sqlite.UnRegClientDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.view.CircleImageView;
import com.zkjinshi.superservice.vo.ClientVo;
import com.zkjinshi.superservice.vo.ContactType;
import com.zkjinshi.superservice.vo.UnRegClientVo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;

/**
 * 客人绑定服务员操作
 * 开发者：vincent
 * 日期：2015/10/5
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientBindActivity extends Activity {

    private final static String TAG = ClientBindActivity.class.getSimpleName();

    private String mUserID;
    private String mToken;
    private String mShopID;

    private ImageButton mIbtnBack;
    private TextView    mTvTitle;
    private ImageButton mIbtnDianhua;
    private ImageButton mIbtnDuiHua;
    private TextView    mTvMemberName;
    private TextView    mTvMemberPhone;
    private TextView    mTvMemberLevel;
    private TextView    mTvMemberType;
    private TextView    mTvRecordTimes;
    private TagView     mTvTagPreference;
    private TagView     mTvTagPrivilege;
    private Button      mBtnConfirm;
    private Button      mBtnCancell;

    private CircleImageView  mCivMemberAvatar;
    private ClientDetailBean mClientBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_bind);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mIbtnBack        = (ImageButton)     findViewById(R.id.ibtn_back);
        mTvTitle         = (TextView)        findViewById(R.id.tv_title);
        mTvTitle.setText(getString(R.string.member_bind));
        mCivMemberAvatar = (CircleImageView) findViewById(R.id.civ_member_avatar);
        mTvMemberName    = (TextView)        findViewById(R.id.tv_member_name);
        mTvMemberPhone   = (TextView)        findViewById(R.id.tv_member_phone);
        mIbtnDianhua     = (ImageButton)     findViewById(R.id.ibtn_dianhua);
        mTvMemberLevel   = (TextView)        findViewById(R.id.tv_member_level);
        mIbtnDuiHua      = (ImageButton)     findViewById(R.id.ibtn_duihua);
        mTvMemberType    = (TextView)        findViewById(R.id.tv_member_type);
        mTvRecordTimes   = (TextView)        findViewById(R.id.tv_record_times);
        mTvTagPreference = (TagView)         findViewById(R.id.tv_preference_tag);
        mTvTagPrivilege  = (TagView)         findViewById(R.id.tv_privilege_tag);
        mBtnConfirm      = (Button)         findViewById(R.id.btn_confirm);
        mBtnCancell      = (Button)         findViewById(R.id.btn_cancel);
    }

    private void initData() {

        mUserID = CacheUtil.getInstance().getUserId();
        mToken  = CacheUtil.getInstance().getToken();
        mShopID = CacheUtil.getInstance().getShopID();

        mClientBean = (ClientDetailBean) getIntent().getSerializableExtra("client_bean");
        showClient(mClientBean);
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
        mTvRecordTimes.setText(client.getOrder_count() + "");

        //TODO: 1.客户偏好标签处理
        //TODO: 2.特权标签处理
    }

    private void initListener() {
        mIbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientBindActivity.this.finish();
            }
        });

        mIbtnDianhua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mClientBean.getPhone())) {
                    //打电话
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mClientBean.getPhone()));
                    ClientBindActivity.this.startActivity(intent);
                }
            }
        });

        mIbtnDuiHua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: 发起聊天
                DialogUtil.getInstance().showToast(ClientBindActivity.this, "TODO: 会话聊天");
            }
        });

        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClientDBUtil.getInstance().isClientExistByPhone(mClientBean.getPhone())){
                    DialogUtil.getInstance().showCustomToast(ClientBindActivity.this, "客户已存在,请勿重复添加.", Gravity.CENTER);
                }else if (null != mClientBean) {
                    //绑定客户
                    bindClient(mUserID, mToken, mShopID, mClientBean);
                }
            }
        });

        mBtnCancell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientBindActivity.this.finish();
            }
        });
    }

    /**
     * * 为服务员绑定客户
     * @param userID
     * @param token
     * @param shopID
     * @param mClientBean
     */
    private void bindClient(String userID, String token, String shopID, ClientDetailBean mClientBean) {
        NetRequest netRequest = new NetRequest(ProtocolUtil.getAddUserUrl());
        HashMap<String,String> bizMap = new HashMap<>();
        bizMap.put("salesid", userID);
        bizMap.put("token", token);
        bizMap.put("shopid", shopID);
        bizMap.put("userid", mClientBean.getUserid());
        bizMap.put("phone", mClientBean.getPhone());
        bizMap.put("username", mClientBean.getUsername());
        bizMap.put("position", mClientBean.getPosition());
        bizMap.put("company", mClientBean.getCompany());
        bizMap.put("other_desc", "");
        bizMap.put("is_bill", mClientBean.getIs_bill() + "");
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new NetRequestListener() {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
                //网络请求异常
                DialogUtil.getInstance().cancelProgressDialog();
                ClientBindActivity.this.finish();
                DialogUtil.getInstance().showToast(ClientBindActivity.this, "网络异常");
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
                    JSONObject jsonObject = new JSONObject(jsonResult);
                    if(jsonObject.getBoolean("set")){
                        //TODO add to local db
                        showSuccessDialog();
                    } else {
                        //TODO add to local db without salesid
                        int errCode = jsonObject.getInt("err");
                        //验证没通过
                        if (300 == errCode) {
                            showFailedDialog();
                        }
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

    /**
     * 邀请对话框
     */
    private void showSuccessDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage("绑定当前用户成功！继续绑定？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ClientBindActivity.this.finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ClientBindActivity.this.finish();
            }
        });
        builder.create().show();
    }

    /**
     * 邀请对话框
     */
    private void showFailedDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage("此用户已被绑定，只能添加为本地联系人。确认添加？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String phone = mClientBean.getPhone();
                ClientVo clientVo = ClientFactory.getInstance().convertClientDetailBean2ClientVO(mClientBean);
                clientVo.setContactType(ContactType.UNNORMAL);
                if(!ClientDBUtil.getInstance().isClientExistByPhone(phone)){
                    long addResult = ClientDBUtil.getInstance().addClient(clientVo);
                    if(addResult > 0){
                        DialogUtil.getInstance().showToast(ClientBindActivity.this, "添加为本地联系人成功！");
                    }
                }else {
                    long updateResult =ClientDBUtil.getInstance().updateClient(clientVo);
                    if(updateResult > 0){
                        DialogUtil.getInstance().showToast(ClientBindActivity.this, "更新本地联系人成功！");
                    }
                }
                ClientBindActivity.this.finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ClientBindActivity.this.finish();
            }
        });
        builder.create().show();
    }

    /**
     * 创建新Tag
     * @param tagstr
     * @return
     */
    private Tag createTag(String tagstr){
        return createTag(tagstr, null);
    }

    private Tag createTag(String tagstr, Drawable background){
        return createTag(0, tagstr, background);
    }

    private Tag createTag(int id, String tagstr, Drawable background){
        Tag tag = new Tag(id, tagstr);
        tag.tagTextColor         = Color.BLACK;
        tag.layoutColor          = Color.WHITE;
        tag.layoutColorPress     = Color.parseColor("#DDDDDD");
        tag.background           = background;
        tag.radius               = 40f;
        tag.tagTextSize          = 18f;
        tag.layoutBorderSize     = 1f;
        tag.layoutBorderColor    = Color.parseColor("#bbbbbb");
        tag.deleteIndicatorColor = Color.parseColor("#ff0000");
        tag.deleteIndicatorSize  = 18f;
        tag.isDeletable          = false;
        return tag;
    }
}

package com.zkjinshi.superservice.activity.common;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.bean.BaseBean;
import com.zkjinshi.superservice.bean.SempLoginBean;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.sqlite.DBOpenHelper;
import com.zkjinshi.superservice.sqlite.UserDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.utils.task.ImgAsyncTask;
import com.zkjinshi.superservice.view.CircleImageView;
import com.zkjinshi.superservice.vo.SexType;
import com.zkjinshi.superservice.vo.UserVo;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import me.nereo.multi_image_selector.MultiImageSelectorFragment;

/**
 * 客服注册完善信息页面
 * 开发者：dujiande
 * 日期：2015/9/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MoreActivity extends FragmentActivity implements MultiImageSelectorFragment.Callback{

    private final static String TAG = MoreActivity.class.getSimpleName();

    private CircleImageView avatarCiv;
    private TextView nameTv;
    private EditText inputNameEt;
    private CheckBox sexCbx;

    public static int REQUEST_IMAGE = 1;
    private String picPath = null;
    private UserVo userVo = null;
    SempLoginBean sempLoginbean = null;

    @Override
    public void onSingleImageSelected(String path) {
        // 当选择模式设定为 单选/MODE_SINGLE, 这个方法就会接受到Fragment返回的数据
        setAvatar(path);
    }

    @Override
    public void onImageSelected(String path) {
        // 一个图片被选择是触发，这里可以自定义的自己的Actionbar行为
    }

    @Override
    public void onImageUnselected(String path) {
        // 一个图片被反选是触发，这里可以自定义的自己的Actionbar行为
    }

    @Override
    public void onCameraShot(File imageFile) {
        // 当设置了使用摄像头，用户拍照后会返回照片文件
        if(imageFile != null) {
           setAvatar(imageFile.getAbsolutePath());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //屏蔽输入法自动弹出
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setContentView(R.layout.activity_more);
        initFragment();
        initView();
        initData();
        initListener();
    }

    private void initFragment() {
        Bundle bundle = new Bundle();
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_COUNT, 1);
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
        bundle.putBoolean(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.image_grid, Fragment.instantiate(this, MultiImageSelectorFragment.class.getName(), bundle))
                .commit();
    }

    private void initView() {
        avatarCiv = (CircleImageView)findViewById(R.id.avatar);
        nameTv = (TextView)findViewById(R.id.org_username_tv);
        inputNameEt = (EditText)findViewById(R.id.new_username_et);
        sexCbx = (CheckBox)findViewById(R.id.sex_cbx);

        findViewById(R.id.back_btn).setVisibility(View.GONE);
    }

    private void initData() {
        DBOpenHelper.DB_NAME = CacheUtil.getInstance().getUserId() + ".db";
        userVo = UserDBUtil.getInstance().queryUserById(CacheUtil.getInstance().getUserId());
        if(getIntent().getSerializableExtra("sempLoginbean") != null){
            sempLoginbean = (SempLoginBean)getIntent().getSerializableExtra("sempLoginbean");
        }
        nameTv.setText(CacheUtil.getInstance().getUserName());
        if(userVo.getSex() == SexType.FEMALE){
            sexCbx.setChecked(false);
        }else{
            sexCbx.setChecked(true);
        }
        ImageLoader.getInstance().displayImage(CacheUtil.getInstance().getUserPhotoUrl(), avatarCiv);
    }

    private void initListener() {
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!getIntent().getBooleanExtra("from_setting",false)){
                    startActivity(new Intent(MoreActivity.this, LoginActivity.class));
                }
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });

        findViewById(R.id.go_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //完善资料api接口
                sempupdate();
            }
        });

        avatarCiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MoreActivity.this, MultiImageSelectorActivity.class);
                // 是否显示调用相机拍照
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
                // 最大图片选择数量
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
                // 设置模式 (支持 单选/MultiImageSelectorActivity.MODE_SINGLE 或者 多选/MultiImageSelectorActivity.MODE_MULTI)
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });
    }

    /*
    * 资料更新
    * */
    private void sempupdate() {
        if(picPath == null){
            if(sempLoginbean!= null && TextUtils.isEmpty(sempLoginbean.getUrl())){
                DialogUtil.getInstance().showToast(this,"请上传头像");
                return;
            }
        }

        String input = inputNameEt.getText().toString();
        final String name = TextUtils.isEmpty(input)? nameTv.getText().toString() : input;
        final String sex = sexCbx.isChecked() ? "1" : "0";

        NetRequest netRequest = new NetRequest(ProtocolUtil.getSempupdateUrl());
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("salesid", CacheUtil.getInstance().getUserId());
        bizMap.put("token",CacheUtil.getInstance().getToken());
        bizMap.put("name", name);
        bizMap.put("sex", sex);
        netRequest.setBizParamMap(bizMap);

        if(picPath != null){
            HashMap<String,File> fileMap = new HashMap<String, File>();
            fileMap.put("file", new File(picPath));
            netRequest.setFileMap(fileMap);
            ImageLoader.getInstance().clearDiskCache();
            ImageLoader.getInstance().clearMemoryCache();
        }
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(this) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);

                Log.i(TAG, "result.rawResult:" + result.rawResult);
                BaseBean baseBean = new Gson().fromJson(result.rawResult, BaseBean.class);
                if (baseBean.isSet()) {
                    userVo.setSex(sexCbx.isChecked() ? SexType.MALE : SexType.FEMALE);
                    userVo.setUserName(name);
                    String avatarUrl = Constants.GET_USER_AVATAR+userVo.getUserId()+".jpg";
                    userVo.setPhotoUrl(avatarUrl);
                    CacheUtil.getInstance().saveUserPhotoUrl(avatarUrl);
                    CacheUtil.getInstance().setUserName(name);
                    UserDBUtil.getInstance().addUser(userVo);
                    if(!getIntent().getBooleanExtra("from_setting",false)){
                        startActivity(new Intent(MoreActivity.this, ZoneActivity.class));
                    }
                    finish();
                    overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
                } else {
                    DialogUtil.getInstance().showToast(MoreActivity.this, "error ");
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE){
            if(resultCode == RESULT_OK){
                // 获取返回的图片列表
                List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                Log.e(TAG,path.toString());

                String photoFilePath = path.get(0);
                setAvatar(photoFilePath);

            }
        }
    }

    private void setAvatar(String photoFilePath){
       ImgAsyncTask imgAsyncTask = new ImgAsyncTask(this,photoFilePath,avatarCiv,
        new ImgAsyncTask.CallBack() {
            @Override
            public void getNewPath(String path) {
                picPath = path;
            }
        });
       imgAsyncTask.execute();
    }


}

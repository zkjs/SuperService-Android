package com.zkjinshi.superservice.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.zkjinshi.base.util.ImageUtil;
import com.zkjinshi.superservice.R;

import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 设置Fragment页面
 * 开发者：杜健德
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SetFragment extends Fragment {

    private final static String TAG = SetFragment.class.getSimpleName();
    public static int REQUEST_IMAGE = 1;

    private ImageView avatarIv;
    private TextView usernameTv;
    private TextView shopnameTv;
    private CheckBox onlineCbx;


    private void initView(View view){
        avatarIv = (ImageView)view.findViewById(R.id.avatar_iv);
        usernameTv = (TextView)view.findViewById(R.id.username_tv);
        shopnameTv = (TextView)view.findViewById(R.id.shop_name_tv);
        onlineCbx = (CheckBox)view.findViewById(R.id.online_cbx);

        //设置在线或离线
        onlineCbx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });

        //编辑头像按钮点击事件
        view.findViewById(R.id.edit_avatar_ibtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MultiImageSelectorActivity.class);
                // 是否显示调用相机拍照
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
                // 最大图片选择数量
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
                // 设置模式 (支持 单选/MultiImageSelectorActivity.MODE_SINGLE 或者 多选/MultiImageSelectorActivity.MODE_MULTI)
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });
        //团队联系人点击事件
        view.findViewById(R.id.team_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //我的客人点击事件
        view.findViewById(R.id.client_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //设置点击事件
        view.findViewById(R.id.setting_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //退出点击事件
        view.findViewById(R.id.exit_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //关于我们点击事件
        view.findViewById(R.id.about_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE){
            if(resultCode == Activity.RESULT_OK){
                // 获取返回的图片列表
                List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                Log.e(TAG, path.toString());
                String photoFilePath = path.get(0);
                setAvatar(photoFilePath);
            }
        }
    }

    private void setAvatar(String photoFilePath){
        Bitmap displayBitmap = BitmapFactory.decodeFile(photoFilePath);
        if (displayBitmap != null) {
            displayBitmap = ImageUtil.cropThumbBitmap(displayBitmap);
            displayBitmap = ImageUtil.loadThumbBitmap(getActivity(), displayBitmap);
            avatarIv.setImageBitmap(displayBitmap);
        }
        // CacheUtil.getInstance().savePicPath(photoFilePath);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set, container,false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}

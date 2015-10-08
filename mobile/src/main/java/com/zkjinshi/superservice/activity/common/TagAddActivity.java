package com.zkjinshi.superservice.activity.common;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import com.zkjinshi.superservice.R;

/**
 * 开发者：vincent
 * 日期：2015/10/8
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class TagAddActivity extends Activity {

    private EditText mEtInputTag;
    private Button   mBtnConfirm;
    private Button   mBtnCancel;

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

    }

    private void initView() {
        mEtInputTag = (EditText)findViewById(R.id.et_input_tag);
        mBtnConfirm = (Button)findViewById(R.id.btn_confirm);
        mBtnCancel  = (Button)findViewById(R.id.btn_cancel);
    }

    private void initData() {
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:check 是否可以添加标签
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:取消标签添加
            }
        });
    }

    //提交资料
    public void addNewTag(final String tag){

    }
}

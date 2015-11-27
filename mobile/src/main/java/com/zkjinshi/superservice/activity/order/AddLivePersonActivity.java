package com.zkjinshi.superservice.activity.order;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.superservice.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 说明：手动添加入住人
 * 开发者：WinkyQin
 * 日期：2015/11/27
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class AddLivePersonActivity extends AppCompatActivity {

    private Toolbar        mToolbar;
    private TextView       mTvCenterTitle;
    private LinearLayout   mLlContainer;
    private Button         mBtnConfirm;
    private EditText       mEtLivePerson;
    private List<EditText> mEtLivePersonsList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_live_person);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.mipmap.ic_fanhui);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTvCenterTitle = (TextView) findViewById(R.id.tv_center_title);
        mTvCenterTitle.setText(getString(R.string.edit_line_person));

        mLlContainer = (LinearLayout) findViewById(R.id.ll_container);
        mBtnConfirm  = (Button) findViewById(R.id.btn_confirm);
    }

    private void initData() {
        mEtLivePersonsList = new ArrayList<>();
        mEtLivePerson = getEtLivePerson(AddLivePersonActivity.this);
        mLlContainer.addView(mEtLivePerson);
        mEtLivePersonsList.add(mEtLivePerson);
    }

    private void initListener() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddLivePersonActivity.this.finish();
            }
        });

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(R.id.menu_add_live_person == item.getItemId()){
                    EditText etLivePerson = getEtLivePerson(AddLivePersonActivity.this);
                    mLlContainer.addView(etLivePerson);
                    mEtLivePersonsList.add(etLivePerson);
                }
                return true;
            }
        });

        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != mEtLivePersonsList && !mEtLivePersonsList.isEmpty()){
                    ArrayList<String> nameList = null;
                    for(EditText editText : mEtLivePersonsList){
                        if(nameList == null){
                            nameList = new ArrayList<>();
                        }
                        String textName = editText.getText().toString();
                        if(!TextUtils.isEmpty(textName)){
                            nameList.add(textName);
                        }
                    }

                    Intent data = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("live_persons", nameList);
                    data.putExtras(bundle);
                    setResult(RESULT_OK, data);
                    AddLivePersonActivity.this.finish();
                }else {
                    DialogUtil.getInstance().showCustomToast(AddLivePersonActivity.this, "请填写入住人", Gravity.CENTER);
                    return ;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_live_person, menu);
        return true;
    }

    /**
     * 生成入住人EditText
     * @param activity
     * @return
     */
    private EditText getEtLivePerson(Activity activity){
        EditText etLivePerson = new EditText(activity);
        int paddingSize = DisplayUtil.dip2px(AddLivePersonActivity.this, 10f);
        etLivePerson.setPadding(paddingSize, paddingSize, paddingSize, paddingSize);
        etLivePerson.setHint(R.string.please_input_the_name);
        etLivePerson.setTextColor(Color.BLACK);
        etLivePerson.setHintTextColor(Color.GRAY);
        etLivePerson.setSingleLine(true);
        return etLivePerson;
    }

}
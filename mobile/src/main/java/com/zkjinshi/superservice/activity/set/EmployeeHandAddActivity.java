package com.zkjinshi.superservice.activity.set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;

import com.zkjinshi.superservice.utils.DepartmentDialog;
import com.zkjinshi.superservice.vo.DepartmentVo;
import com.zkjinshi.superservice.vo.ShopEmployeeVo;

/**
 * 手动添加成员
 * 开发者：dujiande
 * 日期：2015/10/14
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class EmployeeHandAddActivity extends Activity {

    private final static String TAG = EmployeeHandAddActivity.class.getSimpleName();

    public static final String CREATE_RESULT = "create_result";

    private EditText phoneEt;
    private EditText nameEt;
    private TextView deptText;
    private EditText remarkEt;
    private CheckBox adminCbx;

    private DepartmentVo selectDepartmentVo = null;
    private int deptId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_hand_add);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        phoneEt = (EditText)findViewById(R.id.et_input_name);
        nameEt = (EditText)findViewById(R.id.et_input_name);
        deptText = (TextView)findViewById(R.id.tv_dept);
        remarkEt = (EditText)findViewById(R.id.et_input_name);
        adminCbx = (CheckBox)findViewById(R.id.cbx_admin);
    }

    private void initData() {

    }

    private void initListener() {

        deptText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //弹出选择部门对话框
                DepartmentDialog dialog = new DepartmentDialog(EmployeeHandAddActivity.this,deptId);
                dialog.setClickOneListener(new DepartmentDialog.ClickOneListener() {
                    @Override
                    public void clickOne(DepartmentVo departmentVo) {
                        selectDepartmentVo = departmentVo;
                        deptText.setText(selectDepartmentVo.getDept_name());
                        deptId = selectDepartmentVo.getDeptid();
                    }
                });
                dialog.show();
            }
        });

        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEmployee();
            }
        });

        findViewById(R.id.header_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void createEmployee() {
        String phone = phoneEt.getText().toString();
        String name = nameEt.getText().toString();
        String remark = remarkEt.getText().toString();

        if(TextUtils.isEmpty(phone)){
            DialogUtil.getInstance().showToast(this,"手机号码不能为空");
            return;
        } else if(TextUtils.isEmpty(name)){
            DialogUtil.getInstance().showToast(this,"姓名不能为空");
            return;
        }

        ShopEmployeeVo shopEmployeeVo = new ShopEmployeeVo();
        shopEmployeeVo.setName(name);
        shopEmployeeVo.setPhone(phone);

        if(adminCbx.isChecked()){
            shopEmployeeVo.setRoleid(1);
        }else{
            shopEmployeeVo.setRoleid(2);
        }

        Intent intent = new Intent();
        intent.putExtra(EmployeeHandAddActivity.CREATE_RESULT,shopEmployeeVo);
        setResult(RESULT_OK, intent);
        finish();
    }

}

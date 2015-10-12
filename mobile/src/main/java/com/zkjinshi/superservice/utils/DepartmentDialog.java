package com.zkjinshi.superservice.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.DepartmentAdapter;
import com.zkjinshi.superservice.test.DepartmentBiz;
import com.zkjinshi.superservice.vo.DepartmentVo;


/**
 * 开发者：dujiande
 * 日期：2015/10/12
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class DepartmentDialog  extends Dialog {

    private Context context;
    private DepartmentAdapter departmentAdapter = null;
    private DepartmentAdapter.ClickRadioButtonInterface clickRadioButtonInterface = null;



    public DepartmentDialog(Context context) {
        super(context);
        this.context = context;
    }


    public void setClickRadioButtonInterface(DepartmentAdapter.ClickRadioButtonInterface clickRadioButtonInterface) {
        this.clickRadioButtonInterface = clickRadioButtonInterface;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(true);
        getWindow().setGravity(Gravity.CENTER);
        init();

    }

    private void init(){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_department, null);
        setContentView(view);

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.rcv_department);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        departmentAdapter = new DepartmentAdapter(DepartmentBiz.getDepartmentList(),1);
        departmentAdapter.setClickRadioButtonInterface(new DepartmentAdapter.ClickRadioButtonInterface() {
            @Override
            public void clickItem(DepartmentVo selectDepartmentVo) {
                if (clickRadioButtonInterface != null) {
                    clickRadioButtonInterface.clickItem(selectDepartmentVo);
                    cancel();
                }
            }
        });
        recyclerView.setAdapter(departmentAdapter);

        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = DisplayUtil.dip2px(context, 300);
        lp.height = DisplayUtil.dip2px(context, 57)* departmentAdapter.getItemCount();
        dialogWindow.setAttributes(lp);

    }
}

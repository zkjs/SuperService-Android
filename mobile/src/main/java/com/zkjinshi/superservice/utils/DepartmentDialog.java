package com.zkjinshi.superservice.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.DeptAdapter;
import com.zkjinshi.superservice.test.DepartmentBiz;
import com.zkjinshi.superservice.vo.DepartmentVo;

import java.util.ArrayList;


/**
 * 开发者：dujiande
 * 日期：2015/10/12
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class DepartmentDialog  extends Dialog {

    private Context context;
    private ClickOneListener clickOneListener;
    private ListView listView;

    private int selectid = 0;
    DeptAdapter deptAdapter;

    public interface ClickOneListener{
        public void clickOne(DepartmentVo departmentVo);
    }

    public void setClickOneListener(ClickOneListener clickOneListener) {
        this.clickOneListener = clickOneListener;
    }

    public DepartmentDialog(Context context) {
        super(context);
        this.context = context;
    }

    public DepartmentDialog(Context context,int selectid) {
        super(context);
        this.context = context;
        this.selectid = selectid;
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

        listView = (ListView)view.findViewById(R.id.dept_listview);
        deptAdapter = new DeptAdapter(context,DepartmentBiz.getDepartmentList(),this.selectid);
        listView.setAdapter(deptAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayList<DepartmentVo> dataList = deptAdapter.getdataList();
                clickOneListener.clickOne(dataList.get(i));
                cancel();
            }
        });

        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = DisplayUtil.dip2px(context, 300);
        lp.height = DisplayUtil.dip2px(context, 58)* deptAdapter.getCount();
        dialogWindow.setAttributes(lp);

    }
}

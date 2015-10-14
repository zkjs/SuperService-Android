package com.zkjinshi.superservice.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.DeptAdapter;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestListener;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.test.DepartmentBiz;
import com.zkjinshi.superservice.vo.DepartmentVo;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * 开发者：dujiande
 * 日期：2015/10/12
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class DepartmentDialog  extends Dialog {

    private final static String TAG = DepartmentDialog.class.getSimpleName();

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
        loadDepartments();



    }

    private void loadDepartments() {
        String url = ProtocolUtil.getShopDeptlistUrl();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("salesid",CacheUtil.getInstance().getUserId());
        bizMap.put("token", CacheUtil.getInstance().getToken());
        bizMap.put("shopid", CacheUtil.getInstance().getShopID());
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(context,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new NetRequestListener() {
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
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try{
                    ArrayList<DepartmentVo> depts = new Gson().fromJson(result.rawResult, new TypeToken< ArrayList<DepartmentVo>>(){}.getType());
                    initListByData(depts);
                }catch (Exception e){
                    Log.e(TAG,e.getMessage());
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

    private void initListByData(ArrayList<DepartmentVo> depts) {

        deptAdapter = new DeptAdapter(context,depts,this.selectid);
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

package com.zkjinshi.superservice.pad.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.pad.R;
import com.zkjinshi.superservice.pad.adapter.DeptAdapter;
import com.zkjinshi.superservice.pad.net.ExtNetRequestListener;
import com.zkjinshi.superservice.pad.net.MethodType;
import com.zkjinshi.superservice.pad.net.NetRequest;
import com.zkjinshi.superservice.pad.net.NetRequestTask;
import com.zkjinshi.superservice.pad.net.NetResponse;
import com.zkjinshi.superservice.pad.vo.DepartmentVo;
import com.zkjinshi.superservice.pad.vo.GetDeptsVo;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


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

    private String selectid = "";
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

    public DepartmentDialog(Context context,String selectid) {
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
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            if(CacheUtil.getInstance().isLogin()){
                client.addHeader("Token",CacheUtil.getInstance().getExtToken());
            }
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString(),"UTF-8");
            String url = ProtocolUtil.getshopdepts();
            client.get(context,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                    DialogUtil.getInstance().showAvatarProgressDialog(context,"");
                }

                public void onFinish(){
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        GetDeptsVo getDeptsVo = new Gson().fromJson(response,GetDeptsVo.class);
                        if(getDeptsVo == null){
                            return;
                        }
                        if(getDeptsVo.getRes() == 0){
                            initListByData(getDeptsVo.getData());
                        }else{
                            Toast.makeText(context,"API错误："+getDeptsVo.getResDesc(),Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    AsyncHttpClientUtil.onFailure(context,statusCode);
                }
            });
        }catch (Exception e){

            e.printStackTrace();
        }
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
//        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        lp.width = DisplayUtil.dip2px(context, 300);
//        lp.height = DisplayUtil.dip2px(context, 58)* deptAdapter.getCount();
//        dialogWindow.setAttributes(lp);
    }
}

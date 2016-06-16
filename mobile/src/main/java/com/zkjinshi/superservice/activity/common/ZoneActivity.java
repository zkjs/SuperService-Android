package com.zkjinshi.superservice.activity.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.ZoneAdapter;
import com.zkjinshi.superservice.base.BaseActivity;
import com.zkjinshi.superservice.bean.ZoneBean;
import com.zkjinshi.superservice.manager.CheckoutInnerManager;
import com.zkjinshi.superservice.manager.UnSubscribeCallback;
import com.zkjinshi.superservice.manager.YunBaSubscribeManager;
import com.zkjinshi.superservice.response.GetZoneListResponse;
import com.zkjinshi.superservice.utils.AsyncHttpClientUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.BeaconVo;

import org.dom4j.tree.BackedList;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * 开发者：dujiande
 * 日期：2015/9/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ZoneActivity extends BaseActivity {

    private String ZONE_CACHE_KEY = "zoneBeanList"+CacheUtil.getInstance().getUserId();
    private ListView zoneLv;
    private ZoneAdapter zoneAdapter;
    private Context mContext;
    private ArrayList<ZoneBean> zoneList,requestZoneList,payZoneList;
    private ArrayList<BeaconVo> beaconList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone);
        mContext = this;

        initView();
        initData();
        initListener();
    }

    private void initView() {
        zoneLv = (ListView)findViewById(R.id.zone_listview);
    }

    private void initData() {

        zoneAdapter = new ZoneAdapter(ZoneActivity.this, new ArrayList<ZoneBean>());
        zoneLv.setAdapter(zoneAdapter);
        getZoneList();

    }

    private void getZoneList(){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token",CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.getZoneList();
            client.get(mContext,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                    DialogUtil.getInstance().showAvatarProgressDialog(mContext,"");
                }

                public void onFinish(){
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        GetZoneListResponse getZoneListResponse = new Gson().fromJson(response,GetZoneListResponse.class);
                        if (getZoneListResponse == null){
                            return;
                        }
                        if(getZoneListResponse.getRes() == 0){
                            requestZoneList= getZoneListResponse.getData();
                            if(!requestZoneList.isEmpty()){
                                zoneList = getZoneList(requestZoneList);
                                payZoneList = getPayZoneList(requestZoneList);
                                zoneAdapter.setZoneList(zoneList);
                                //设置缓存的选择记录
                                String listStr = CacheUtil.getInstance().getListStrCache(ZONE_CACHE_KEY);
                                if(!TextUtils.isEmpty(listStr)){
                                    Type listType = new TypeToken<ArrayList<ZoneBean>>(){}.getType();
                                    Gson gson = new Gson();
                                    ArrayList<ZoneBean> myZoneList = gson.fromJson(listStr, listType);
                                    if (null != myZoneList && !myZoneList.isEmpty()) {
                                        zoneAdapter.setCheckedZone(myZoneList);
                                    }
                                }
                                zoneAdapter.notifyDataSetChanged();
                            }


                        }else{
                            Toast.makeText(mContext,getZoneListResponse.getResDesc(),Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    AsyncHttpClientUtil.onFailure(mContext,statusCode);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initListener() {

        findViewById(R.id.go_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CacheUtil.getInstance().saveListCache(ZONE_CACHE_KEY, zoneAdapter.getSelectZoneBeanList());
                String checkedIds =  zoneAdapter.getCheckedIds();
                String payIds = getPeyIds(payZoneList);
                beaconList = getBeaconList();
                CheckoutInnerManager.getInstance().saveBeaconCache(beaconList);
                if(!TextUtils.isEmpty(checkedIds)){
                    CacheUtil.getInstance().setAreaInfo(checkedIds);
                    CacheUtil.getInstance().setPayInfo(payIds);
                    String[] locIds = checkedIds.split(",");
                    if(null != locIds && locIds.length > 0){
                        final String[] subscribes = new String[locIds.length];
                        for(int i = 0; i<locIds.length; i++){
                            subscribes[i] =  CacheUtil.getInstance().getShopID()+"_BLE_"+locIds[i];
                        }
                        YunBaSubscribeManager.getInstance().unSubscribe(ZoneActivity.this, new UnSubscribeCallback() {
                            @Override
                            public void onSuccess() {
                                YunBaSubscribeManager.getInstance().subscribe(subscribes);
                            }

                            @Override
                            public void onFailure() {
                                YunBaSubscribeManager.getInstance().subscribe(subscribes);
                            }
                        });

                    }
                }
                if (!getIntent().getBooleanExtra("from_setting", false)) {
                    startActivity(new Intent(ZoneActivity.this, MainActivity.class));
                }
                finish();
                overridePendingTransition(R.anim.activity_new, R.anim.activity_out);

            }
        });

        zoneLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int added =  zoneAdapter.getZoneList().get(i).getSubscribed();
                if(added == 0){
                    zoneAdapter.getZoneList().get(i).setSubscribed(1);
                }else{
                    zoneAdapter.getZoneList().get(i).setSubscribed(0);
                }
                zoneAdapter.notifyDataSetChanged();
            }
        });


    }

    /**
     * 获取区域选择列表
     * @return
     */
    private ArrayList<ZoneBean> getZoneList(ArrayList<ZoneBean> resultZoneList){
        ArrayList<ZoneBean> zoneList = new ArrayList<ZoneBean>();
        int supportStatus = 0;// 0、不支持支付, 1、支持支付
        for(ZoneBean zoneBean : resultZoneList){
            supportStatus = zoneBean.getPayment_support();
            if(0 == supportStatus){
                zoneList.add(zoneBean);
            }
        }
        return zoneList;
    }

    /**
     * 获取收银台列表
     * @param resultZoneList
     * @return
     */
    private ArrayList<ZoneBean> getPayZoneList(ArrayList<ZoneBean> resultZoneList){
        ArrayList<ZoneBean> payZoneList = new ArrayList<ZoneBean>();
        int supportStatus = 1;// 0、不支持支付, 1、支持支付
        for(ZoneBean zoneBean : resultZoneList){
            supportStatus = zoneBean.getPayment_support();
            if(1 == supportStatus){
                payZoneList.add(zoneBean);
            }
        }
        return payZoneList;
    }

    /**
     * 获取收银台locId
     * @return
     */
    public String getPeyIds(ArrayList<ZoneBean> payZoneList){
        String ids = "";
        if(payZoneList == null){
            return  ids;
        }
        for(int i=0;i<payZoneList.size();i++){
            ZoneBean zoneBean = payZoneList.get(i);
            if(zoneBean.getPayment_support() == 1){
                if(TextUtils.isEmpty(ids)){
                    ids = ids+zoneBean.getLocid();
                }else{
                    ids = ids+","+zoneBean.getLocid();
                }
            }

        }
        return  ids;
    }

    /**
     * 获取收款台相关的beacon信息
     * @return
     */
    public ArrayList<BeaconVo> getBeaconList(){
        ArrayList<BeaconVo> beaconList = new ArrayList<BeaconVo>();
        if(null != payZoneList && !payZoneList.isEmpty()){
            for(int i=0;i<payZoneList.size();i++){
                ZoneBean zoneBean = payZoneList.get(i);
                if(zoneBean.getPayment_support() == 1){
                    beaconList = zoneBean.getBeacons();
                }
            }
        }
        return beaconList;
    }

}

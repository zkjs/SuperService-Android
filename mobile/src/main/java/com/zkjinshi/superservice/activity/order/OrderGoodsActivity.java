package com.zkjinshi.superservice.activity.order;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.util.DeviceUtils;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.GoodAdapter;
import com.zkjinshi.superservice.bean.GoodBean;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.sqlite.DBOpenHelper;
import com.zkjinshi.superservice.sqlite.UserDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.view.RefreshLayout;
import com.zkjinshi.superservice.vo.UserVo;

import java.util.ArrayList;

/**
 * 确定房型页面
 * 开发者：dujiande
 * 日期：2015/9/30
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderGoodsActivity extends Activity implements AdapterView.OnItemClickListener{
    private final static String TAG = OrderGoodsActivity.class.getSimpleName();
    private UserVo userVo;
    private RefreshLayout swipeLayout;
    private ListView goodLv;
    private GoodAdapter goodAdapter;
    private TextView roomText;
    private int roomNum;
    private int selelectId;
    private GoodBean selectGood = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_goods);

        String userid = CacheUtil.getInstance().getUserId();
        initDBName();
        userVo = UserDBUtil.getInstance().queryUserById(userid);

        roomNum = getIntent().getIntExtra("roomNum", 2);
        selelectId = getIntent().getIntExtra("selelectId",0);

        initView();
        initData();
        initListener();
    }

    /**
     * 初始化数据库名称
     */
    private void initDBName(){
        if(!TextUtils.isEmpty(CacheUtil.getInstance().getUserId())){
            DBOpenHelper.DB_NAME = CacheUtil.getInstance().getUserId() + ".db";
        }
    }

    private void initView() {
        goodLv = (ListView)findViewById(R.id.goods_listview);
        roomText = (TextView)findViewById(R.id.room_num);
    }

    private void initData() {
        getGoodsList();
        roomText.setText(roomNum + "间");
    }

    private void initListener() {

        findViewById(R.id.room_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRoomNumChooseDialog();
            }
        });

        findViewById(R.id.back_btn_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        findViewById(R.id.go_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectGood != null) {
                    Intent data = new Intent();
                    data.putExtra("roomNum", roomNum);
                    data.putExtra("selectGood", selectGood);
                    setResult(RESULT_OK, data);
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                } else {
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
            }
        });
    }

    private void getGoodsList(){
        NetRequest netRequest = new NetRequest(ProtocolUtil.getGoodslistUrl(userVo.getShopId()));
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener() {
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
                try {
                    ArrayList<GoodBean> goodList = new Gson().fromJson(result.rawResult, new TypeToken<ArrayList<GoodBean>>() {
                    }.getType());
                    for (GoodBean goodBean : goodList) {
                        if (goodBean.getId() == selelectId) {
                            selectGood = goodBean;
                        }
                    }
                    goodAdapter = new GoodAdapter(OrderGoodsActivity.this, goodList, selelectId);
                    goodLv.setAdapter(goodAdapter);
                    goodLv.setOnItemClickListener(OrderGoodsActivity.this);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Log.e(TAG, "positoin" + position);
        goodAdapter.setCheckidByPosition(position);
        goodAdapter.notifyDataSetChanged();
        selectGood = goodAdapter.getGoodByPosition(position);
    }

    //显示房间数量选择对话框
    private void showRoomNumChooseDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_room_number);

        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        int width = DeviceUtils.getScreenWidth(this);

        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (width*0.8); // 宽度
        // lp.height = 300; // 高度
        //lp.alpha = 0.7f; // 透明度
        dialogWindow.setAttributes(lp);
        dialog.show();
        RadioGroup group = (RadioGroup)dialog.findViewById(R.id.gendergroup);
        RadioButton mRadio1 = (RadioButton) dialog.findViewById(R.id.rbtn_one_room);
        RadioButton mRadio2 = (RadioButton) dialog.findViewById(R.id.rbtn_two_room);
        RadioButton mRadio3 = (RadioButton) dialog.findViewById(R.id.rbtn_three_room);
        if(roomNum == 1){
            mRadio1.setChecked(true);
        }
        else  if(roomNum == 2){
            mRadio2.setChecked(true);
        }
        else  if(roomNum == 3){
            mRadio3.setChecked(true);
        }
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId ==R.id.rbtn_one_room) {
                    roomNum = 1;
                }
                else if (checkedId ==R.id.rbtn_two_room) {
                    roomNum = 2;
                }else{
                    roomNum = 3;
                }
                roomText.setText(roomNum+"间");
                dialog.cancel();
            }
        });
    }
}

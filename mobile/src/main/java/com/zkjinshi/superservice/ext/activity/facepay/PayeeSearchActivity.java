package com.zkjinshi.superservice.ext.activity.facepay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.SoftInputUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.ext.adapter.NearbyAdapter;
import com.zkjinshi.superservice.ext.response.NearbyUserResponse;
import com.zkjinshi.superservice.ext.vo.NearbyUserVo;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;

import java.util.ArrayList;

/**
 * 收款人搜索页面(完成)
 * 开发者：JimmyZhang
 * 日期：2016/3/7
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class PayeeSearchActivity extends Activity {

    public static final String TAG = PayeeSearchActivity.class.getSimpleName();

    private Button cancelBtn;
    private ListView nearbyListView;
    private ArrayList<NearbyUserVo> nearbyUserList;
    private NearbyAdapter nearbyAdapter;
    private EditText nearbyEtv;
    private ImageButton clearTxtIBtn;
    private TextView noResultTv;
    private RelativeLayout resultLayout;
    private TextView hiddleLayout;

    private void initView(){
        cancelBtn = (Button) findViewById(R.id.btn_cancel_search);
        nearbyListView = (ListView)findViewById(R.id.pay_search_list_view);
        nearbyEtv = (EditText)findViewById(R.id.et_search_keyword);
        clearTxtIBtn = (ImageButton)findViewById(R.id.ib_clear_text);
        noResultTv = (TextView)findViewById(R.id.no_result);
        resultLayout = (RelativeLayout)findViewById(R.id.result_layout);
        hiddleLayout = (TextView)findViewById(R.id.hiddle_layout_tv);
    }

    private void initData(){
        nearbyAdapter = new NearbyAdapter(this,nearbyUserList);
        nearbyListView.setAdapter(nearbyAdapter);
        nearbyListView.setEmptyView(noResultTv);
        resultLayout.setVisibility(View.GONE);
        hiddleLayout.setVisibility(View.VISIBLE);
    }

    private void initListeners(){

        //取消
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoftInputUtil.hideSoftInputMode(view.getContext(),nearbyEtv);
                finish();
                overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
            }
        });

        nearbyEtv.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    SoftInputUtil.hideSoftInputMode(PayeeSearchActivity.this,nearbyEtv);
                   //执行搜索操作
                    String phoneNumStr = nearbyEtv.getText().toString();
                    if(!TextUtils.isEmpty(phoneNumStr)){
                        requestNearbyByPhoneNumTask(phoneNumStr);
                    }else {
                        DialogUtil.getInstance().showCustomToast(PayeeSearchActivity.this,"手机号码不能为空", Gravity.CENTER);
                    }
                    return true;
                }
                return false;
            }
        });

        nearbyEtv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String inputStr = editable.toString();
                if (inputStr.length() <= 0) {
                    clearTxtIBtn.setVisibility(View.GONE);
                }else{
                    clearTxtIBtn.setVisibility(View.VISIBLE);
                }
            }
        });

        nearbyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                NearbyUserVo nearbyUserVo = (NearbyUserVo) nearbyAdapter.getItem(position);
                Intent intent = new Intent(PayeeSearchActivity.this,PayRequestActivity.class);
                intent.putExtra("nearbyUserVo",nearbyUserVo);
                startActivity(intent);
                finish();
            }
        });

        clearTxtIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nearbyEtv.setText("");
            }
        });

}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payee_search);
        initView();
        initData();
        initListeners();
    }

    /**
     * 根据手机号码获取用户名
     *
     */
    private void requestNearbyByPhoneNumTask(String phoneNum){
        String url = ConfigUtil.getInst().getForDomain()+"res/v1/query/si/all?phone="+phoneNum;
        //String url = "http://p.zkjinshi.com/for/res/v1/query/si/all";
        NetRequest netRequest = new NetRequest(url);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(this) {
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
                super.onNetworkResponseSucceed(result);
                if(null != result && !TextUtils.isEmpty(result.rawResult)){
                    resultLayout.setVisibility(View.VISIBLE);
                    hiddleLayout.setVisibility(View.GONE);
                    NearbyUserResponse nearbyUserResponse = new Gson().fromJson(result.rawResult,NearbyUserResponse.class);
                    if(null != nearbyUserResponse){
                        int resultFlag = nearbyUserResponse.getRes();
                        if(0 == resultFlag){
                            nearbyUserList =  nearbyUserResponse.getData();
                            if(null != nearbyUserList && !nearbyUserList.isEmpty()){
                                nearbyAdapter.setNearbyUserList(nearbyUserList);
                            }
                        }
                    }
                }
            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

}

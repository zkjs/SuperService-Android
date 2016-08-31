package com.zkjinshi.superservice.activity.label;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.base.BaseActivity;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by qinyejun on 7/27/16.
 */
public class GuestPaymentAddActivity  extends BaseActivity {
    public static final String TAG = "GuestPaymentAddActivity";

    private String clientId = "";

    private ImageButton backIBtn;
    private TextView titleTv;
    private Button mBtnConfirm;
    private EditText etAmount;
    private EditText etRemark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_payment_add);

        initView();
        initData();
        initListeners();
    }

    private void initView(){
        //labelGridView = (LabelGridView) findViewById(R.id.client_label_gv);
        backIBtn = (ImageButton)findViewById(R.id.header_bar_btn_back);
        titleTv = (TextView)findViewById(R.id.header_bar_tv_title);
        mBtnConfirm = (Button)findViewById(R.id.btn_confirm);
        etAmount = (EditText)findViewById(R.id.et_payment_amount);
        etRemark = (EditText)findViewById(R.id.et_payment_remark);
    }

    private void initData(){
        //sureBtn.setVisibility(View.GONE);
        backIBtn.setVisibility(View.VISIBLE);
        titleTv.setText("新增消费记录");

        if(null != getIntent() && null != getIntent().getSerializableExtra("clientId")) {
            clientId = (String) getIntent().getSerializableExtra("clientId");
        }
    }

    private void initListeners(){

        //返回
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //添加消费记录
        mBtnConfirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (null == clientId) { return; }
                if (StringUtil.isEmpty(etAmount.getText().toString())) {
                    DialogUtil.getInstance().showCustomToast(GuestPaymentAddActivity.this,"金额不能为空", Gravity.CENTER);
                    return;
                }
                if (!StringUtil.isDecimal(etAmount.getText().toString())) {
                    DialogUtil.getInstance().showCustomToast(GuestPaymentAddActivity.this,"金额只能是数字", Gravity.CENTER);
                    return;
                }
                double amount = Double.parseDouble(etAmount.getText().toString());
                if ( amount <= 0 ) {
                    DialogUtil.getInstance().showCustomToast(GuestPaymentAddActivity.this,"金额必须大于零", Gravity.CENTER);
                    return;
                }
                if (etAmount.getText().toString().length() > 8) {
                    DialogUtil.getInstance().showCustomToast(GuestPaymentAddActivity.this,"金额过大", Gravity.CENTER);
                    return;
                }
                if (StringUtil.isEmpty(etRemark.getText().toString())) {
                    DialogUtil.getInstance().showCustomToast(GuestPaymentAddActivity.this,"备注不能为空", Gravity.CENTER);
                    return;
                }
                if (etRemark.getText().toString().length() > 6) {
                    DialogUtil.getInstance().showCustomToast(GuestPaymentAddActivity.this,"备注长度超过限制", Gravity.CENTER);
                    return;
                }

                addPayment((int)(amount * 100), etRemark.getText().toString(), clientId);
            }
        });

    }


    //提交资料
    public void addPayment(int amount, String remark, final String clientId){

        String token  = CacheUtil.getInstance().getToken();

        Log.i("AddPaymentUrl",ProtocolUtil.addClientPaymentUrl());

        NetRequest netRequest = new NetRequest(ProtocolUtil.addClientPaymentUrl());
        HashMap<String,Object> bizMap = new HashMap<>();
        //bizMap.put("token", token);
        bizMap.put("amount", amount);
        bizMap.put("remark", remark);
        bizMap.put("userid", clientId);
        netRequest.setObjectParamMap(bizMap);

        Log.i("Arguments",bizMap.toString());

        NetRequestTask netRequestTask = new NetRequestTask(this, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.JSONPOST;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(this) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                DialogUtil.getInstance().cancelProgressDialog();
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
                DialogUtil.getInstance().showCustomToast(GuestPaymentAddActivity.this,
                        GuestPaymentAddActivity.this.getString(R.string.net_exception_please_try_it_later),
                        Gravity.CENTER);
            }

            @Override
            public void onNetworkRequestCancelled() {
                DialogUtil.getInstance().cancelProgressDialog();
            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);

                Log.i(TAG, "result.rawResult:" + result.rawResult);
                DialogUtil.getInstance().cancelProgressDialog();
                String jsonResult = result.rawResult;
                try {
                    JSONObject json = new JSONObject(jsonResult);
                    int res = json.getInt("res");
                    if (res == 0) {
                        DialogUtil.getInstance().showToast(GuestPaymentAddActivity.this, GuestPaymentAddActivity.this.
                                getString(R.string.add_successed));
                        GuestPaymentAddActivity.this.setResult(RESULT_OK);
                        GuestPaymentAddActivity.this.finish();
                    } else {
                        //弹出添加失败提醒
                        DialogUtil.getInstance().showToast(GuestPaymentAddActivity.this, GuestPaymentAddActivity.this.
                                getString(R.string.add_failed));
                        //GuestPaymentAddActivity.this.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void beforeNetworkRequestStart() {
                //网络请求前
            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

}

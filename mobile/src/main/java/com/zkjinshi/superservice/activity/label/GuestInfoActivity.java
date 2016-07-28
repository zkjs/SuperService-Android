package com.zkjinshi.superservice.activity.label;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.ClientLabelAdapter;
import com.zkjinshi.superservice.adapter.GuestArrivingAdapter;
import com.zkjinshi.superservice.adapter.GuestHabitAdapter;
import com.zkjinshi.superservice.adapter.GuestPaymentAdapter;
import com.zkjinshi.superservice.adapter.LocNotificationAdapter;
import com.zkjinshi.superservice.base.BaseActivity;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.response.BaseResponse;
import com.zkjinshi.superservice.response.ClientArrivingResponse;
import com.zkjinshi.superservice.response.ClientPaymentResponse;
import com.zkjinshi.superservice.response.ClientTagResponse;
import com.zkjinshi.superservice.response.NoticeResponse;
import com.zkjinshi.superservice.utils.AsyncHttpClientUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.view.CustomInputDialog;
import com.zkjinshi.superservice.view.LabelGridView;
import com.zkjinshi.superservice.vo.ClientArrivingVo;
import com.zkjinshi.superservice.vo.ClientPaymentVo;
import com.zkjinshi.superservice.vo.ClientTagVo;
import com.zkjinshi.superservice.vo.ItemTagVo;
import com.zkjinshi.superservice.vo.NoticeVo;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by qinyejun on 7/26/16.
 */
public class GuestInfoActivity extends BaseActivity {
    public static final String TAG = "GuestInfoActivity";
    public static final int ADD_PAYMENT_REQUEST = 1;
    public static final int ADD_HABIT_REQUEST = 2;

    private NoticeVo noticeVo;
    private String clientId = "";

    private ImageButton backIBtn;
    private TextView titleTv;
    private ImageButton addIBtn;
    private ListView listView;
    private TabLayout tab;
    private TextView tvName;
    private TextView tvGender;
    private SimpleDraweeView clientPhotoView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private int currentTab = 0;
    private int currentPage = 0;
    private int canoptcnt;
    private ArrayList<ItemTagVo> habitList = new ArrayList<>();
    private ArrayList<ClientArrivingVo> arrivingList = new ArrayList<>();
    private ArrayList<ClientPaymentVo> paymentList = new ArrayList<>();
    private GuestHabitAdapter habitAdapter;
    private GuestArrivingAdapter arrivingAdapter;
    private GuestPaymentAdapter paymentAdapter;

    public static final int PAGE_SIZE = 20;
    public String[] tabTitles = {"喜好标签","到店记录","消费记录"};
    public static final int HABIT_POS = 0;
    public static final int ARRIVE_POS = 1;
    public static final int PAY_POS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_info);

        habitAdapter = new GuestHabitAdapter(GuestInfoActivity.this, R.layout.list_row_habit, habitList);
        arrivingAdapter = new GuestArrivingAdapter(GuestInfoActivity.this, R.layout.list_row_arriving, arrivingList);
        paymentAdapter = new GuestPaymentAdapter(GuestInfoActivity.this, R.layout.list_row_payment, paymentList);

        listView = (ListView) findViewById(R.id.listview);
        listView.addHeaderView(buildHeader());
        listView.setAdapter(null);

        initView();
        initData();
        initListeners();
        loadData(currentTab, currentPage);
    }

    private View buildHeader() {

        View headerView = getLayoutInflater().inflate(
                R.layout.header_guest_info, null);
        tvName = (TextView) headerView.findViewById(R.id.client_user_name_tv);
        tvGender = (TextView) headerView.findViewById(R.id.client_user_sex_tv);
        clientPhotoView = (SimpleDraweeView) headerView.findViewById(R.id.client_user_photo_dv);
        tab = (TabLayout) headerView.findViewById(R.id.tab_profile);

        for (int i = 0; i < tabTitles.length; i++) {
            TabLayout.Tab tlt = tab.newTab().setText(tabTitles[i]);
            tab.addTab(tlt);
        }

        tab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //Toast.makeText(GuestInfoActivity.this, tab.getText(), Toast.LENGTH_SHORT).show();
                currentTab = tab.getPosition();
                currentPage = 0;

                switch (currentTab) {
                    case HABIT_POS:
                        addIBtn.setVisibility(View.VISIBLE);
                        listView.setAdapter(habitAdapter);
                        break;
                    case ARRIVE_POS:
                        addIBtn.setVisibility(View.GONE);
                        listView.setAdapter(arrivingAdapter);
                        break;
                    case PAY_POS:
                        addIBtn.setVisibility(View.VISIBLE);
                        listView.setAdapter(paymentAdapter);
                        break;
                }

                loadData(currentTab, currentPage);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        return headerView;
    }

    private void initView(){
        //labelGridView = (LabelGridView) findViewById(R.id.client_label_gv);
        backIBtn = (ImageButton)findViewById(R.id.header_bar_btn_back);
        titleTv = (TextView)findViewById(R.id.header_bar_tv_title);
        addIBtn = (ImageButton)findViewById(R.id.header_bar_btn_add);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_profile_list);
        addIBtn.setVisibility(View.VISIBLE);
    }

    private void initData(){
        //sureBtn.setVisibility(View.GONE);
        backIBtn.setVisibility(View.VISIBLE);
        titleTv.setText("客户信息");

        //habitAdapter = new GuestHabitAdapter(GuestInfoActivity.this, R.layout.list_row_habit, habitList);
        //arrivingAdapter = new GuestArrivingAdapter(GuestInfoActivity.this, R.layout.list_row_arriving, arrivingList);
        listView.setAdapter(habitAdapter);

        //mSelectMap = new HashMap<Integer, Boolean>();
        //clientLabelAdapter = new ClientLabelAdapter(this,tagList);
        //labelGridView.setAdapter(clientLabelAdapter);
        //clientLabelAdapter.setSelectMap(mSelectMap);
        //labelGridView.setTag(tagList);
        if(null != getIntent() && null != getIntent().getSerializableExtra("noticeVo")){
            noticeVo = (NoticeVo) getIntent().getSerializableExtra("noticeVo");
            if(null != noticeVo){
                clientId = noticeVo.getUserid();
                //sureBtn.setTag(clientId);
                String userImg = noticeVo.getUserimage();
                if(!TextUtils.isEmpty(userImg)){
                    Uri userImgUri = Uri.parse(ProtocolUtil.getImageUrlByScale(GuestInfoActivity.this,userImg,90,90));
                    clientPhotoView.setImageURI(userImgUri);
                }
                String userName = noticeVo.getUsername();
                if(!TextUtils.isEmpty(userName)){
                    tvName.setText(userName);
                }
                int sexId = noticeVo.getSex();
                if(-1 == sexId){
                    tvGender.setVisibility(View.GONE);
                }else {
                    tvGender.setVisibility(View.VISIBLE);
                    if(0 == sexId){
                        tvGender.setText("女");
                    }else {
                        tvGender.setText("男");
                    }
                }
            }
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

        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 0;
                loadData(currentTab, currentPage);
            }
        });

        //添加消费记录
        addIBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(currentTab == PAY_POS) {
                    Intent intent = new Intent(GuestInfoActivity.this, GuestPaymentAddActivity.class);
                    intent.putExtra("noticeVo", noticeVo);
                    startActivityForResult(intent, ADD_PAYMENT_REQUEST);
                } else if (currentTab == HABIT_POS) {
                    showAddLabelDialog();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(GuestInfoActivity.this, "" + i , Toast.LENGTH_LONG);
                if (currentTab != HABIT_POS) { return; }
                //ItemTagVo item = ((ItemTagVo)adapterView.getAdapter().getItem(i));

                Intent intent = new Intent(GuestInfoActivity.this, GuestLabelEditActivity.class);
                intent.putExtra("noticeVo",noticeVo);
                intent.putExtra(GuestLabelEditActivity.EXTRA_CANOPT_COUNT, canoptcnt);
                intent.putExtra(GuestLabelEditActivity.EXTRA_HABITS, habitList);
                startActivityForResult(intent, ADD_HABIT_REQUEST);

            }
        });

    }

    private void loadData(int tab, int page) {
        switch (tab) {
            case HABIT_POS:
                loadHabits(page);
                break;
            case ARRIVE_POS:
                loadArriving(page);
                break;
            case PAY_POS:
                loadPayment(page);
                break;
            default:
                break;
        }
    }

    //获取用户喜好标签信息
    private void loadHabits(final int page) {
        Log.i(TAG, "loadHabits:" + page);

        if(null == clientId || clientId.isEmpty()) { return; }
        //获取用户标签信息
        ClientLabelController.getInstance().requestGetClientTagsTask(clientId, this, new ExtNetRequestListener(this) {
            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                Log.i("HabitsRaw",result.rawResult);
                swipeRefreshLayout.setRefreshing(false);
                if(null != result && !TextUtils.isEmpty(result.rawResult)) {
                    ClientTagResponse clientTagResponse = new Gson().fromJson(result.rawResult,ClientTagResponse.class);
                    if(null != clientTagResponse){
                        int resultCode = clientTagResponse.getRes();
                        if(0 == resultCode){
                            ClientTagVo clientTagVo = clientTagResponse.getData();
                            if(null != clientTagVo){
                                canoptcnt = clientTagVo.getCanoptcnt();
                                habitList.clear();
                                for (ItemTagVo tag : clientTagVo.getTags()) {
                                    habitList.add(tag);
                                }
                                habitAdapter.notifyDataSetChanged();

                                if (habitList.size() == 0) {
                                    DialogUtil.getInstance().showCustomToast(GuestInfoActivity.this,"没有喜好记录", Gravity.CENTER);
                                }
                            }
                        }else {
                            String resultMsg = clientTagResponse.getResDesc();
                            if(!TextUtils.isEmpty(resultMsg)){
                                DialogUtil.getInstance().showCustomToast(GuestInfoActivity.this,resultMsg, Gravity.CENTER);
                            }
                        }
                    }
                }
            }

            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                super.onNetworkRequestError(errorCode, errorMessage);
            }
        });
    }

    //获取到店记录列表
    private void loadArriving(final int page) {
        Log.i(TAG, "loadArriving:" + page);

        if(null == clientId || clientId.isEmpty()) { return; }
        ClientLabelController.getInstance().requestGetClientArriving(clientId, this, new ExtNetRequestListener(this) {
            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                Log.i("ArrivingRaw",result.rawResult);
                swipeRefreshLayout.setRefreshing(false);
                if(null != result && !TextUtils.isEmpty(result.rawResult)) {
                    ClientArrivingResponse response = new Gson().fromJson(result.rawResult,ClientArrivingResponse.class);
                    if(null != response){
                        int resultCode = response.getRes();
                        if(0 == resultCode){
                            if(null != response.getData()){
                                arrivingList.clear();
                                for (ClientArrivingVo item : response.getData()) {
                                    arrivingList.add(item);
                                }
                                arrivingAdapter.notifyDataSetChanged();

                                if (arrivingList.size() == 0) {
                                    DialogUtil.getInstance().showCustomToast(GuestInfoActivity.this,"没有到店记录", Gravity.CENTER);
                                }
                            }
                        }else {
                            String resultMsg = response.getResDesc();
                            if(!TextUtils.isEmpty(resultMsg)){
                                DialogUtil.getInstance().showCustomToast(GuestInfoActivity.this,resultMsg, Gravity.CENTER);
                            }
                        }
                    }
                }
            }

            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                super.onNetworkRequestError(errorCode, errorMessage);
            }
        });
    }

    //获取消费记录列表
    private void loadPayment(final int page) {
        Log.i(TAG, "loadPayment:" + page);

        if(null == clientId || clientId.isEmpty()) { return; }
        ClientLabelController.getInstance().requestGetClientPayment(clientId, this, new ExtNetRequestListener(this) {
            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                Log.i("PaymentRaw",result.rawResult);
                swipeRefreshLayout.setRefreshing(false);
                if(null != result && !TextUtils.isEmpty(result.rawResult)) {
                    ClientPaymentResponse response = new Gson().fromJson(result.rawResult,ClientPaymentResponse.class);
                    if(null != response){
                        int resultCode = response.getRes();
                        if(0 == resultCode){
                            if(null != response.getData()){
                                paymentList.clear();
                                for (ClientPaymentVo item : response.getData()) {
                                    paymentList.add(item);
                                }
                                paymentAdapter.notifyDataSetChanged();

                                if (paymentList.size() == 0) {
                                    DialogUtil.getInstance().showCustomToast(GuestInfoActivity.this,"没有支付记录", Gravity.CENTER);
                                }
                            }
                        }else {
                            String resultMsg = response.getResDesc();
                            if(!TextUtils.isEmpty(resultMsg)){
                                DialogUtil.getInstance().showCustomToast(GuestInfoActivity.this,resultMsg, Gravity.CENTER);
                            }
                        }
                    }
                }
            }

            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                super.onNetworkRequestError(errorCode, errorMessage);
            }
        });
    }

    // 选择标签 | 增加消费记录 返回后刷新列表
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_PAYMENT_REQUEST) {
            if (resultCode == RESULT_OK) {
                loadPayment(0);
            }
        } else if (requestCode == ADD_HABIT_REQUEST) {
            if (resultCode == RESULT_OK) {
                loadHabits(0);
            }
        }
    }

   // 弹出添加标签窗口
    private void showAddLabelDialog(){
        final CustomInputDialog.Builder customBuilder = new CustomInputDialog.Builder(this);
        customBuilder.setTitle("添加喜好标签");
        customBuilder.setTint("请输入标签(字数不超过8个字)");
        customBuilder.setGravity(Gravity.CENTER);
        customBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        customBuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                EditText inputEt = customBuilder.inputEt;
                String inputStr = inputEt.getText().toString();
                if(!TextUtils.isEmpty(inputStr)){
                    if(inputStr.length() <= 8){
                        requestAddLabelTask(inputStr);
                    }else {
                        DialogUtil.getInstance().showCustomToast(GuestInfoActivity.this,"标签不能超过8个字",Gravity.CENTER);
                    }
                }else {
                    DialogUtil.getInstance().showCustomToast(GuestInfoActivity.this,"标签不能为空",Gravity.CENTER);
                }
            }
        });
        customBuilder.create().show();
    }

    // 添加标签请求
    private void requestAddLabelTask(String labelName){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            if(CacheUtil.getInstance().isLogin()){
                client.addHeader("Token",CacheUtil.getInstance().getExtToken());
            }
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.getAddLabelUrl(labelName);
            client.post(this,url, stringEntity, "application/json", new JsonHttpResponseHandler(){
                public void onStart(){
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(GuestInfoActivity.this,"");
                }

                public void onFinish(){
                    super.onFinish();
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                    super.onSuccess(statusCode,headers,response);
                    try {
                        BaseResponse baseResponse = new Gson().fromJson(response.toString(),BaseResponse.class);
                        if(null != baseResponse){
                            if(baseResponse.getRes() == 0){
                                DialogUtil.getInstance().showCustomToast(GuestInfoActivity.this,"添加一级标签成功", Gravity.CENTER);
                                if(!TextUtils.isEmpty(clientId)){
                                    loadHabits(0);
                                }
                            }else{
                                Toast.makeText(GuestInfoActivity.this,baseResponse.getResDesc(),Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse){
                    super.onFailure(statusCode,headers,throwable,errorResponse);
                    try {
                        JSONObject response = new JSONObject();
                        response.put("statusCode",statusCode);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    AsyncHttpClientUtil.onFailure(GuestInfoActivity.this,statusCode);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

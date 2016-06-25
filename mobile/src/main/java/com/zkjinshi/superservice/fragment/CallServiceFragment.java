package com.zkjinshi.superservice.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.set.AppointActivity;
import com.zkjinshi.superservice.activity.set.DetailTaskActivity;
import com.zkjinshi.superservice.adapter.CallServiceAdapter;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.response.RoleListResponse;
import com.zkjinshi.superservice.response.ServiceTaskListResponse;
import com.zkjinshi.superservice.utils.AsyncHttpClientUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.ServiceTaskVo;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * 呼叫服务Fragment页面
 * 开发者：JimmyZhang
 * 日期：2016/6/21
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class CallServiceFragment extends Fragment implements CallServiceAdapter.ServiceOptionListener{

    public static int PAGE_NO = 0;
    public static final int PAGE_SIZE = 10;

    private RelativeLayout emptyLayout;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private CallServiceAdapter callServiceAdapter;
    private TextView emptyTips;
    private ArrayList<ServiceTaskVo> serviceList = new ArrayList<ServiceTaskVo>();
    private ArrayList<ServiceTaskVo> requestServiceList;
    private boolean isLoadMoreAble = true;
    private int isower = 0;//0: 获取所有者列表, 1: 获取非所有者列表
    private ServiceReceiver serviceReceiver;
    private IntentFilter serviceIntentFilter;

    private void initView(View view){
        emptyLayout = (RelativeLayout)view.findViewById(R.id.call_empty_layout);
        refreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.call_swipe_refresh_layout);
        recyclerView = (RecyclerView)view.findViewById(R.id.call_recycler_view);
        emptyTips = (TextView) view.findViewById(R.id.empty_tips);
    }

    private void initData(){
        emptyTips.setText("暂无呼叫通知");
        callServiceAdapter = new CallServiceAdapter(getActivity(),serviceList);
        callServiceAdapter.setOnOptionListener(this);
        recyclerView.setAdapter(callServiceAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        serviceReceiver = new ServiceReceiver();
        serviceIntentFilter = new IntentFilter();
        serviceIntentFilter.addAction(Constants.ACTION_SERVICE);
        getActivity().registerReceiver(serviceReceiver,serviceIntentFilter);
    }

    private void initListeners(){

        //下拉刷新
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                PAGE_NO = 0;
                requestCallServiceTask(true);
            }
        });

        //自动加载更多
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            boolean isSlidingToLast = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    int lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = linearLayoutManager.getItemCount();
                    if (lastVisibleItem == (totalItemCount -1) && isSlidingToLast) {
                        if(isLoadMoreAble){
                            isLoadMoreAble = false;
                            refreshLayout.setRefreshing(true);
                            requestCallServiceTask(false);
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //dx用来判断横向滑动方向，dy用来判断纵向滑动方向
                if(dy > 0){
                    //大于0表示，正在向右滚动
                    isSlidingToLast = true;
                }else{
                    //小于等于0 表示停止或向左滚动
                    isSlidingToLast = false;
                }
            }
        });

        //单选列表
        callServiceAdapter.setOnItemClickListener(new RecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ServiceTaskVo serviceTaskVo = serviceList.get(position);
                String taskId = serviceTaskVo.getTaskid();
                Intent intent = new Intent(getActivity(),DetailTaskActivity.class);
                intent.putExtra("taskId",taskId);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });
    }

    public static CallServiceFragment newInstance() {
        return new CallServiceFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call_service, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
        initListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        PAGE_NO = 0;
        requestCallServiceTask(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null != serviceReceiver){
            getActivity().unregisterReceiver(serviceReceiver);
        }
    }

    @Override
    public void executeCancel(ServiceTaskVo taskVo) {//取消呼叫服务
        String taskId = taskVo.getTaskid();
        //指派(2), 就绪(3), 取消(4), 完成(5), 评价(6)
        int taskAction = 4;
        String target = "";
        CallServiceNetController.getInstance().requestUpdateServiceTask(taskId, taskAction, target, getActivity(), new CallServiceNetController.NetCallBack() {
            @Override
            public void onSuccess() {
                DialogUtil.getInstance().showCustomToast(getActivity(),"取消成功",Gravity.CENTER);
                PAGE_NO = 0;
                requestCallServiceTask(true);
            }
        });
    }

    @Override
    public void executeFinish(ServiceTaskVo taskVo) {//完成呼叫服务
        String taskId = taskVo.getTaskid();
        //指派(2), 就绪(3), 取消(4), 完成(5), 评价(6)
        int taskAction = 5;
        String target = "";
        CallServiceNetController.getInstance().requestUpdateServiceTask(taskId, taskAction, target, getActivity(), new CallServiceNetController.NetCallBack() {
            @Override
            public void onSuccess() {
                DialogUtil.getInstance().showCustomToast(getActivity(),"完成成功",Gravity.CENTER);
                PAGE_NO = 0;
                requestCallServiceTask(true);
            }
        });
    }

    @Override
    public void executeAppoint(ServiceTaskVo taskVo) {//指派呼叫服务
        Intent intent = new Intent(getActivity(),AppointActivity.class);
        intent.putExtra("taskId",taskVo.getTaskid());
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    @Override
    public void executeReady(ServiceTaskVo taskVo) {//就绪呼叫服务
        String taskId = taskVo.getTaskid();
        //指派(2), 就绪(3), 取消(4), 完成(5), 评价(6)
        int taskAction = 3;
        String target = "";
        CallServiceNetController.getInstance().requestUpdateServiceTask(taskId, taskAction, target, getActivity(), new CallServiceNetController.NetCallBack() {
            @Override
            public void onSuccess() {
                DialogUtil.getInstance().showCustomToast(getActivity(),"就绪成功",Gravity.CENTER);
                PAGE_NO = 0;
                requestCallServiceTask(true);
            }
        });
    }

    /**
     * 切换接受任务/指派任务
     * @param isowner
     */
    public void chooseTaskTab(int isowner){
        this.isower = isowner;
        PAGE_NO = 0;
        requestCallServiceTask(true);
    }

    /**
     * 获取呼叫服务列表
     * @param isRefresh
     */
    private void requestCallServiceTask(final boolean isRefresh){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token", CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String noticesUrl = ProtocolUtil.getServiceListUrl(isower,""+PAGE_NO,""+PAGE_SIZE);
            client.get(getActivity(),noticesUrl, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                    super.onStart();
                }

                public void onFinish(){
                    super.onFinish();
                    if (null != refreshLayout) {
                        refreshLayout.setRefreshing(false);
                    }
                    isLoadMoreAble = true;
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String result = new String(responseBody,"utf-8");
                        isLoadMoreAble = true;
                        if (null != refreshLayout) {
                            refreshLayout.setRefreshing(false);
                        }
                        ServiceTaskListResponse serviceTaskListResponse = new Gson().fromJson(result,ServiceTaskListResponse.class);
                        if(null != serviceTaskListResponse){
                            int resultCode = serviceTaskListResponse.getRes();
                            if(0 == resultCode){
                                requestServiceList = serviceTaskListResponse.getData();
                                if(isRefresh){
                                    serviceList = requestServiceList;
                                }else {
                                    serviceList.addAll(requestServiceList);
                                }
                                callServiceAdapter.setServiceList(serviceList);
                                if(null != requestServiceList && !requestServiceList.isEmpty()){
                                    PAGE_NO++;
                                }else {
                                    DialogUtil.getInstance().showCustomToast(getActivity(),"再无更多数据", Gravity.CENTER);
                                }
                            }else {
                                String resultMsg = serviceTaskListResponse.getResDesc();
                                if(!TextUtils.isEmpty(resultMsg)){
                                    DialogUtil.getInstance().showCustomToast(getActivity(),resultMsg,Gravity.CENTER);
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    if (null != refreshLayout) {
                        refreshLayout.setRefreshing(false);
                    }
                    isLoadMoreAble = true;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 服务通知自动刷新
     *
     */
    class ServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(null != intent){
                String action = intent.getAction();
                if(!TextUtils.isEmpty(action) && action.equals(Constants.ACTION_SERVICE)){
                    PAGE_NO = 0;
                    requestCallServiceTask(true);
                }
            }
        }
    }
}

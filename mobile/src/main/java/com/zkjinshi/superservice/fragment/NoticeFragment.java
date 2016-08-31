package com.zkjinshi.superservice.fragment;

import android.app.Activity;
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
import android.widget.TextView;

import com.google.gson.Gson;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.label.ClientLabelActivity;
import com.zkjinshi.superservice.activity.label.GuestInfoActivity;
import com.zkjinshi.superservice.adapter.LocNotificationAdapter;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.response.NoticeResponse;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.NoticeVo;

import java.util.ArrayList;

/**
 * 到店通知Fragment页面
 * 开发者：JimmyZhang
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class NoticeFragment extends Fragment {

    public static final String TAG = "NoticeFragment";
    public static String LAST_ID = "0";
    public static String LAST_PAGE = "0";
    public static int PAGE_NO = 0;
    public static final int PAGE_SIZE = 10;
    private Activity activity;
    private RecyclerView notityRecyclerView;
    private LinearLayoutManager    notifyLayoutManager;
    private LocNotificationAdapter notificationAdapter;
    private SwipeRefreshLayout     swipeRefreshLayout;
    private ArrayList<NoticeVo>  noticeList = new ArrayList<NoticeVo>();
    private ArrayList<NoticeVo> requestNoticeList;
    private TextView emptyTips;
    private boolean isLoadMoreAble = true;
    private NoticeReceiver noticeReceiver;
    private IntentFilter noticeFilter;

    public static NoticeFragment newInstance() {
        return new NoticeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        emptyTips = (TextView) view.findViewById(R.id.empty_tips);
        emptyTips.setText("暂无到店通知");
        notityRecyclerView = (RecyclerView) view.findViewById(R.id.rcv_notice);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_notice);
    }

    private void initData() {
        activity = this.getActivity();
        notificationAdapter = new LocNotificationAdapter(activity, noticeList);
        notityRecyclerView.setAdapter(notificationAdapter);
        notityRecyclerView.setHasFixedSize(true);
        notityRecyclerView.setNestedScrollingEnabled(false);
        notifyLayoutManager = new LinearLayoutManager(activity);
        notifyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        notityRecyclerView.setLayoutManager(notifyLayoutManager);
        noticeReceiver = new NoticeReceiver();
        noticeFilter = new IntentFilter();
        noticeFilter.addAction(Constants.ACTION_NOTICE);
        getActivity().registerReceiver(noticeReceiver,noticeFilter);
    }

    private void initListeners() {

        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                noticeList = new ArrayList<NoticeVo>();
				PAGE_NO = 0;
                LAST_ID = "0";
                LAST_PAGE = "0";
                requestNoticesTask(true);
            }
        });

        //自动加载更多
        notityRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            //加载更多功能的代码
                            isLoadMoreAble = false;
                            swipeRefreshLayout.setRefreshing(true);
                            requestNoticesTask(false);
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

        notificationAdapter.setOnItemClickListener(new RecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //获取订单信息并显示基本信息
               /* NoticeVo noticeVo = noticeList.get(position);
                ArrayList<OrderVo> orderList = noticeVo.getOrders();
                if(null != orderList && !orderList.isEmpty()){
                    final OrderVo orderVo = orderList.get(0);
                    if(null != orderVo){
                        String orderNO = orderVo.getOrderno();
                        if(!TextUtils.isEmpty(orderNO)){
                            Intent intent = new Intent();
                            if(orderNO.startsWith("H")){
                                intent.setClass(activity, HotelDealActivity.class);
                                intent.putExtra("orderNo",orderNO);
                            }else if(orderNO.startsWith("K")){
                                intent.setClass(activity, KTVDealActivity.class);
                                intent.putExtra("orderNo",orderNO);
                            }
                            else if(orderNO.startsWith("O")){
                                intent.setClass(activity, NormalDealActivity.class);
                                intent.putExtra("orderNo",orderNO);
                            }
                            activity.startActivity(intent);
                        }
                    }
                }*/
                //跳转到用户详情页面
                NoticeVo noticeVo = noticeList.get(position);
                //Intent intent = new Intent(getActivity(),ClientLabelActivity.class);
                Intent intent = new Intent(getActivity(),GuestInfoActivity.class);
                intent.putExtra("noticeVo",noticeVo);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
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
        initNoticesData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null != noticeReceiver){
            getActivity().unregisterReceiver(noticeReceiver);
        }
    }

    /**
     * 请求到店通知
     */
    private void initNoticesData() {
        String locIds = CacheUtil.getInstance().getAreaInfo();
        if (TextUtils.isEmpty(locIds)) {
            if (null != swipeRefreshLayout) {
                swipeRefreshLayout.setRefreshing(false);
            }
            noticeList = new ArrayList<NoticeVo>();
            notificationAdapter.setNoticeList(noticeList);
        }else {
            LAST_ID = "0";
            LAST_PAGE = "0";
			PAGE_NO = 0;
            requestNoticesTask(true);
        }
    }

    /**
     * 获取多个到店区域通知
     */
    public void requestNoticesTask(final boolean isRefresh) {
        String locIds = CacheUtil.getInstance().getAreaInfo();
        String shopId = CacheUtil.getInstance().getShopID();
        String noticesUrl = ProtocolUtil.getNoticeUrl(shopId,locIds,""+LAST_ID,LAST_PAGE,""+PAGE_SIZE);
        Log.i(TAG,noticesUrl);
        NetRequest netRequest = new NetRequest(noticesUrl);
        NetRequestTask netRequestTask = new NetRequestTask(getActivity(), netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(getActivity()) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
                if (null != swipeRefreshLayout) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                isLoadMoreAble = true;
            }

            @Override
            public void onNetworkRequestCancelled() {
                isLoadMoreAble = true;
            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                try {
                    Log.i(TAG, "result.rawResult:" + result.rawResult);
                    isLoadMoreAble = true;
                    if (null != swipeRefreshLayout) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    NoticeResponse noticeResponse = new Gson().fromJson(result.rawResult,NoticeResponse.class);
                    if(null != noticeResponse){
                        int resultCode = noticeResponse.getRes();
                        if(0 == resultCode){
                            requestNoticeList = noticeResponse.getData();
                            if(isRefresh){
                                noticeList = requestNoticeList;
                            }else {
                                noticeList.addAll(requestNoticeList);
                            }
                            if(null != requestNoticeList && !requestNoticeList.isEmpty()){
                                LAST_ID = noticeResponse.getLastid();
                                LAST_PAGE = noticeResponse.getLastpage();
                                PAGE_NO++;
                            }else {
                                if(!isRefresh){
                                    DialogUtil.getInstance().showCustomToast(getActivity(),"再无更多数据",Gravity.CENTER);
                                }
                            }
                            notificationAdapter.setNoticeList(noticeList);
                        }else {
                            String resultMsg = noticeResponse.getResDesc();
                            if(!TextUtils.isEmpty(resultMsg)){
                                DialogUtil.getInstance().showCustomToast(getActivity(),resultMsg,Gravity.CENTER);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.execute();
    }

    /**
     * 到店通知自动刷新
     *
     */
    class NoticeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(null != intent){
                String action = intent.getAction();
                if(!TextUtils.isEmpty(action) && action.equals(Constants.ACTION_NOTICE)){
                    noticeList = new ArrayList<NoticeVo>();
                    LAST_ID = "0";
                    LAST_PAGE = "0";
					PAGE_NO = 0;
                    requestNoticesTask(true);
                }
            }
        }
    }

}
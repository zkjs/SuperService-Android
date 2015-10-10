package com.zkjinshi.superservice.activity.set;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.ContactsSortAdapter;
import com.zkjinshi.superservice.adapter.TeamContactsSortAdapter;
import com.zkjinshi.superservice.bean.TeamContactBean;
import com.zkjinshi.superservice.factory.SortModelFactory;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestListener;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.CharacterParser;
import com.zkjinshi.superservice.utils.PinyinComparator;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.utils.SortKeyUtil;
import com.zkjinshi.superservice.view.AutoSideBar;
import com.zkjinshi.superservice.vo.ContactType;
import com.zkjinshi.superservice.vo.SortModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * 团队联系人显示界面
 * 开发者：vincent
 * 日期：2015/10/9
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class TeamContactsActivity extends Activity{

    private final static String TAG = TeamContactsActivity.class.getSimpleName();

    private ImageButton     mIbtnBack;
    private RecyclerView    mRvTeamContacts;
    private RelativeLayout  mRlSideBar;
    private TextView        mTvDialog;
    private AutoSideBar     mAutoSideBar;

    private LinearLayoutManager     mLayoutManager;
    private TeamContactsSortAdapter mTeamContactAdapter;

    private SortModel               mShopSortModel;
    private List<SortModel>         mTeamSortModels;

    private String mUserID;
    private String mShopID;
    private String mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_contacts);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mIbtnBack       = (ImageButton)      findViewById(R.id.ibtn_back);
        mRvTeamContacts = (RecyclerView)     findViewById(R.id.rcv_team_contacts);
        mRlSideBar      = (RelativeLayout)   findViewById(R.id.rl_side_bar);
        mTvDialog       = (TextView)   findViewById(R.id.tv_dialog);
        mAutoSideBar    = new AutoSideBar(TeamContactsActivity.this);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(DisplayUtil.dip2px(TeamContactsActivity.this, 30),
                                                                         ViewGroup.LayoutParams.MATCH_PARENT);
        mAutoSideBar.setTextView(mTvDialog);
        mAutoSideBar.setLayoutParams(layoutParams);

    }

    private void initData() {
        mUserID     = CacheUtil.getInstance().getUserId();
        mToken      = CacheUtil.getInstance().getToken();
        mShopID     = CacheUtil.getInstance().getShopID();

        // 创建商店排序对象
        mShopSortModel = new SortModel();
        mShopSortModel.setContactType(ContactType.SERVER);
        mShopSortModel.setName(CacheUtil.getInstance().getShopFullName());
        if(!TextUtils.isEmpty(mShopID)){
            mShopSortModel.setAvatarUrl(ProtocolUtil.getShopLogoUrl(mShopID));
        }
        String sortLetter = SortKeyUtil.getSortLetter(CacheUtil.getInstance().getShopFullName(), CharacterParser.getInstance());
        mShopSortModel.setSortKey(sortLetter);
        mShopSortModel.setSortLetters(sortLetter); //此处为角色部门全称

        mTeamSortModels = new ArrayList<SortModel>();
        mTeamSortModels.add(0, mShopSortModel);

        mRvTeamContacts.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvTeamContacts.setLayoutManager(mLayoutManager);

        getTeamList(mUserID, mToken, mShopID);
    }

    private void initListener() {
        //返回上一页
        mIbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeamContactsActivity.this.finish();
            }
        });

        mAutoSideBar.setOnTouchingLetterChangedListener(new AutoSideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = mTeamContactAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mRvTeamContacts.scrollToPosition(position);
                }
            }
        });

    }

    /**
     * 获取团队联系人列表
     * @param userID
     * @param token
     * @param shopID
     */
    public void getTeamList(String userID, String token, String shopID) {
        DialogUtil.getInstance().showProgressDialog(this);
        NetRequest netRequest = new NetRequest(ProtocolUtil.getTeamListUrl());
        HashMap<String,String> bizMap = new HashMap<>();
        bizMap.put("salesid", userID);
        bizMap.put("token", token);
        bizMap.put("shopid", shopID);
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new NetRequestListener() {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                DialogUtil.getInstance().cancelProgressDialog();
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
                DialogUtil.getInstance().showToast(TeamContactsActivity.this, "网络访问失败，稍候再试。");
            }

            @Override
            public void onNetworkRequestCancelled() {
                DialogUtil.getInstance().cancelProgressDialog();
            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                DialogUtil.getInstance().cancelProgressDialog();
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                String jsonResult = result.rawResult;
                if (result.rawResult.contains("set") || jsonResult.contains("err")) {
                    //TODO:获取团队联系人失败
                    DialogUtil.getInstance().showToast(TeamContactsActivity.this, "获取团队联系人失败");

                } else {
                    Gson gson = new Gson();
                    List<TeamContactBean> teamContactBeans = gson.fromJson(jsonResult,
                            new TypeToken<ArrayList<TeamContactBean>>() {
                            }.getType());
                    if (null != mTeamContactAdapter) {
                        mTeamContactAdapter = null;
                    }
                    List<SortModel> sortModels = SortModelFactory.getInstance().convertTeamContacts2SortModels(teamContactBeans);
                    if (null != sortModels && !sortModels.isEmpty()) {

                        mTeamSortModels.addAll(sortModels);
                        List<String> strLetters = new ArrayList<String>();
                        for (SortModel sortModel : mTeamSortModels) {
                            String sortLetter = sortModel.getSortLetters().substring(0, 1);
                            //商家本身
                            if (sortModel == mTeamSortModels.get(0)) {
                                continue;
                            }
                            //部门分类并消除相同部门
                            if (!TextUtils.isEmpty(sortLetter) && !strLetters.contains(sortLetter)) {
                                strLetters.add(sortLetter);
                            } else {
                                continue;
                            }
                        }

                        String[] sortArray = strLetters.toArray(new String[strLetters.size()]);
                        if (sortArray.length > 0) {
                            mAutoSideBar.setSortArray(sortArray);
                            mRlSideBar.addView(mAutoSideBar);
                        }
                    }

                    mTeamContactAdapter = new TeamContactsSortAdapter(TeamContactsActivity.this, mTeamSortModels);
                    mRvTeamContacts.setAdapter(mTeamContactAdapter);
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

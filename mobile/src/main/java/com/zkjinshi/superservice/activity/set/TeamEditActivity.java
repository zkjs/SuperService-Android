package com.zkjinshi.superservice.activity.set;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.net.core.WebSocketManager;
import com.zkjinshi.base.net.protocol.ProtocolMSG;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.TeamEditContactsAdapter;
import com.zkjinshi.superservice.bean.TeamContactBean;
import com.zkjinshi.superservice.entity.MsgEmpStatus;
import com.zkjinshi.superservice.factory.ShopEmployeeFactory;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestListener;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.sqlite.ShopEmployeeDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.DepartmentDialog;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.DepartmentVo;
import com.zkjinshi.superservice.vo.ShopEmployeeVo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 团队编辑页面
 * 开发者：vincent
 * 日期：2015/10/12
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class TeamEditActivity extends Activity{

    private final static String TAG = TeamEditActivity.class.getSimpleName();

    private List<Boolean> mCheckedList;
    private ImageButton   mIbtnBack;
    private TextView      mTvTitle;
    private RecyclerView  mRcvTeamContacts;
    private RelativeLayout  mRlChangeDepartment;
    private RelativeLayout  mRlDelete;
    private LinearLayoutManager     mLayoutManager;
    private TeamEditContactsAdapter mContactsAdapter;
    private List<ShopEmployeeVo>    mShopEmployeeVos;

    private String mUserID;
    private String mShopID;
    private String mToken;

    public TeamEditActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_edit);
        
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mIbtnBack = (ImageButton) findViewById(R.id.ibtn_back);
        mTvTitle  = (TextView)    findViewById(R.id.tv_title);
        mRcvTeamContacts     = (RecyclerView) findViewById(R.id.rcv_team_contacts);
        mRlChangeDepartment  = (RelativeLayout) findViewById(R.id.rl_change_department);
        mRlDelete            = (RelativeLayout) findViewById(R.id.rl_delete);
    }

    private void initData() {
        mUserID = CacheUtil.getInstance().getUserId();
        mToken  = CacheUtil.getInstance().getToken();
        mShopID = CacheUtil.getInstance().getShopID();


        mCheckedList = new ArrayList<>();
        mTvTitle.setText(getString(R.string.team_edit));

        mRcvTeamContacts.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRcvTeamContacts.setLayoutManager(mLayoutManager);

        mShopEmployeeVos = ShopEmployeeDBUtil.getInstance().queryAll();
        if(null != mShopEmployeeVos && !mShopEmployeeVos.isEmpty()){
            mContactsAdapter = new TeamEditContactsAdapter(TeamEditActivity.this, mShopEmployeeVos);
            mRcvTeamContacts.setAdapter(mContactsAdapter);

            //设置默认勾选中的数据
            for(int i=0; i< mShopEmployeeVos.size(); i++){
                mCheckedList.add(i, false);
            }
        }
    }

    private void initListener() {
        mIbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeamEditActivity.this.finish();
            }
        });

        mContactsAdapter.setOnItemClickListener(new RecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                mCheckedList.set(postion, !mCheckedList.get(postion));
            }
        });

        mRlChangeDepartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** 1.获得待操作数据 */
                List<String> empIDs = getCheckedEmpIDs(mCheckedList);
                if (null != empIDs && !empIDs.isEmpty()) {
                    //ToDO:弹出提示框 是否更换部门
                    showChangeDepartmentDialog(empIDs);
                } else {
                    /** 尚未选择员工 */
                    DialogUtil.getInstance().showCustomToast(TeamEditActivity.this,
                            getString(R.string.not_choose_emp_object_yet),
                            Gravity.CENTER);
                }
            }
        });

        mRlDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** 获得待删除数据 */
                List<String> shopEmployeeVos = getCheckedEmpIDs(mCheckedList);
                if(null == shopEmployeeVos && shopEmployeeVos.isEmpty()) {
                    //TODO:弹出提示框 是否确定删除

                } else {
                    DialogUtil.getInstance().showCustomToast(TeamEditActivity.this,
                                      getString(R.string.not_choose_emp_object_yet),
                                      Gravity.CENTER);
                }
            }
        });

    }

    /**
     * 获得已选中员工对象
     * @param mCheckedList
     * @return
     */
    private List<String> getCheckedEmpIDs(List<Boolean> mCheckedList) {
        List<String> checkedEmpIDs = null;
        for(int i=0; i<mCheckedList.size(); i++){
            if(null == checkedEmpIDs){
                checkedEmpIDs = new ArrayList<>();
            }
            if(mCheckedList.get(i)){
                String empID = mShopEmployeeVos.get(i).getEmpid();
                if(!checkedEmpIDs.contains(empID)){
                    checkedEmpIDs.add(empID);
                }
            }
        }
        return checkedEmpIDs;
    }

    private void showChangeDepartmentDialog(final List<String> empIDs){
        //弹出选择部门对话框
        DepartmentDialog dialog = new DepartmentDialog(TeamEditActivity.this);
        dialog.setClickOneListener(new DepartmentDialog.ClickOneListener() {
            @Override
            public void clickOne(DepartmentVo departmentVo) {
                int deptID = departmentVo.getDeptid();
                postChangeDept(mUserID, mToken, mShopID, deptID, empIDs);


            }
        });
        dialog.show();
    }

    /**
     * 员工批量修改部门
     * @param userID
     * @param token
     * @param shopID
     */
    public void postChangeDept(String userID, String token, final String shopID, int deptID, List<String> empIDLists) {

        final String[] userIDArray = empIDLists.toArray(new String[empIDLists.size()]);
        DialogUtil.getInstance().showProgressDialog(this);
        NetRequest netRequest = new NetRequest(ProtocolUtil.getChangeDeptUrl());
        HashMap<String,String> bizMap = new HashMap<>();
        bizMap.put("salesid", userID);
        bizMap.put("token", token);
        bizMap.put("shopid", shopID);
        bizMap.put("deptid", deptID+"");
        bizMap.put("changelist", Arrays.toString(userIDArray));
        LogUtil.getInstance().info(LogLevel.INFO, "changelist:"+Arrays.toString(userIDArray));

        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new NetRequestListener() {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                DialogUtil.getInstance().cancelProgressDialog();
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
                DialogUtil.getInstance().showToast(TeamEditActivity.this, "网络访问失败，稍候再试。");
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
//                    DialogUtil.getInstance().showToast(TeamContactsActivity.this, "获取团队联系人失败");

                } else {
                    //TODO: 更新部门成功后更新界面
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

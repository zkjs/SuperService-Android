package com.zkjinshi.superservice.activity.set;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.TeamEditContactsAdapter;
import com.zkjinshi.superservice.bean.TeamContactBean;
import com.zkjinshi.superservice.factory.ShopEmployeeFactory;
import com.zkjinshi.superservice.listener.GetTeamContactsListener;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.sqlite.ShopEmployeeDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.DepartmentDialog;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.DepartmentVo;
import com.zkjinshi.superservice.vo.ShopEmployeeVo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 团队编辑页面
 * 开发者：vincent
 * 日期：2015/10/12
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class TeamEditActivity extends Activity {

    private static final String TAG = TeamEditActivity.class.getSimpleName();

    private List<String>    mCheckedList;
    private RelativeLayout  mRlBack;
    private TextView        mTvTitle;
    private RecyclerView    mRcvTeamContacts;
    private RelativeLayout  mRlChangeDepartment;
    private RelativeLayout  mRlDelete;

    private LinearLayoutManager     mLayoutManager;
    private TeamEditContactsAdapter mContactsAdapter;
    private List<ShopEmployeeVo>    mShopEmployeeVos;

    private String mUserID;
    private String mShopID;
    private String mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_edit);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mRlBack   = (RelativeLayout) findViewById(R.id.rl_back);
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

        mShopEmployeeVos = ShopEmployeeDBUtil.getInstance().queryAllByDeptIDAsc();

        mContactsAdapter = new TeamEditContactsAdapter(TeamEditActivity.this, mShopEmployeeVos);
        mRcvTeamContacts.setAdapter(mContactsAdapter);
    }

    private void initListener() {

        mRlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeamEditActivity.this.finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        mContactsAdapter.setOnItemClickListener(new RecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                ShopEmployeeVo shopEmployeeVo = mShopEmployeeVos.get(postion);
                String empID = shopEmployeeVo.getEmpid();
                if (mCheckedList.contains(empID)) {
                    mCheckedList.remove(empID);
                } else {
                    mCheckedList.add(empID);
                }
                mRlChangeDepartment.setTag(mCheckedList);
                mRlDelete.setTag(mCheckedList);
            }
        });

        mRlChangeDepartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** 1.获得待操作数据 */
                mCheckedList = (ArrayList<String>)v.getTag();
                if (null != mCheckedList && !mCheckedList.isEmpty()) {
                    showChangeDepartmentDialog(mCheckedList);
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
                mCheckedList = (ArrayList<String>)v.getTag();
                /** 获得待删除数据 */
                if (null != mCheckedList && !mCheckedList.isEmpty()) {
                    deleteEmployees(mUserID, mToken, mShopID, mCheckedList);
                } else {
                    DialogUtil.getInstance().showCustomToast(TeamEditActivity.this,
                            getString(R.string.not_choose_emp_object_yet),
                            Gravity.CENTER);
                }
            }
        });
    }

    /**
     * batch delete the employee
     * @param userID
     * @param token
     * @param shopID
     * @param shopEmployeeVos
     */
    private void deleteEmployees(String userID, String token, final String shopID, final List<String> shopEmployeeVos) {
        String deleteList = convertList2String(shopEmployeeVos);

        DialogUtil.getInstance().showProgressDialog(this);
        NetRequest netRequest = new NetRequest(ProtocolUtil.getBatchDeleteEmployeeUrl());
        HashMap<String,String> bizMap = new HashMap<>();
        bizMap.put("salesid", userID);
        bizMap.put("token", token);
        bizMap.put("shopid", shopID);
        bizMap.put("deletelist", deleteList);

        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(this) {
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
                try {
                    JSONObject jsonObject = new JSONObject(jsonResult);
                    if (jsonObject.getBoolean("set")) {
                        long delResult = -1;
                        delResult = ShopEmployeeDBUtil.getInstance().deleteShopEmployeeByEmpIDs(shopEmployeeVos);
                        LogUtil.getInstance().info(LogLevel.INFO, "delResult" + delResult);
                           if(delResult > 0){
                               DialogUtil.getInstance().showToast(TeamEditActivity.this, "删除成功");
                       }
                        mShopEmployeeVos = ShopEmployeeDBUtil.getInstance().queryAllByDeptIDAsc();
                        mContactsAdapter.updateListView(mShopEmployeeVos);
                    } else {
                        DialogUtil.getInstance().showToast(TeamEditActivity.this, "删除失败，请稍后再试。");
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

    /**
     * 弹出选择部门对话框
     * @param shopEmployeeVos
     */
    private void showChangeDepartmentDialog(final List<String> shopEmployeeVos){
        //弹出选择部门对话框
        DepartmentDialog dialog = new DepartmentDialog(TeamEditActivity.this);
        dialog.setClickOneListener(new DepartmentDialog.ClickOneListener() {
            @Override
            public void clickOne(DepartmentVo departmentVo) {
                int deptID = departmentVo.getDeptid();
                String changeList = convertList2String(shopEmployeeVos);
                postChangeDept(mUserID, mToken, mShopID, deptID, changeList);
            }
        });
        dialog.show();
    }

    /**
     * convert the string list to String
     * @param shopEmployeeVos
     * @return
     */
    private String convertList2String(List<String> shopEmployeeVos) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<shopEmployeeVos.size(); i++){
            if(i == shopEmployeeVos.size()-1){
                sb.append(shopEmployeeVos.get(i));
            }else {
                sb.append(shopEmployeeVos.get(i)+  ",");
            }
        }
        return sb.toString();
    }

    /**
     * 员工批量修改部门
     * @param userID
     * @param token
     * @param shopID
     */
    public void postChangeDept(String userID, String token, final String shopID, int deptID, String changeList) {

        DialogUtil.getInstance().showProgressDialog(this);
        NetRequest netRequest = new NetRequest(ProtocolUtil.getChangeDeptUrl());
        HashMap<String,String> bizMap = new HashMap<>();
        bizMap.put("salesid", userID);
        bizMap.put("token", token);
        bizMap.put("shopid", shopID);
        bizMap.put("deptid", deptID + "");
        bizMap.put("changelist", changeList);
        LogUtil.getInstance().info(LogLevel.INFO, "changelist:" + changeList);

        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(this) {
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
                System.out.print(":" + result.rawResult);
                String jsonResult = result.rawResult;
                try {
                    JSONObject jsonObject = new JSONObject(jsonResult);
                    if (jsonObject.getBoolean("set")) {
                        DialogUtil.getInstance().showProgressDialog(TeamEditActivity.this);
                        TeamContactsController.getInstance().getTeamContacts(
                                TeamEditActivity.this,
                                mUserID,
                                mToken,
                                mShopID,
                                new GetTeamContactsListener() {
                                    @Override
                                    public void getContactsDone(List<TeamContactBean> teamContacts) {
                                        List<ShopEmployeeVo> shopEmployeeVos =  ShopEmployeeFactory.getInstance().buildShopEmployees(teamContacts);
                                        mShopEmployeeVos.removeAll(mShopEmployeeVos);
                                        mShopEmployeeVos.addAll(shopEmployeeVos);

                                        mContactsAdapter.updateListView(mShopEmployeeVos);
                                        DialogUtil.getInstance().cancelProgressDialog();
                                        DialogUtil.getInstance().showCustomToast(TeamEditActivity.this,
                                                       "change the department success", Gravity.CENTER);
                                    }

                                    @Override
                                    public void getContactsFailed() {
                                        DialogUtil.getInstance().cancelProgressDialog();
                                    }
                                });
                    } else {
                        DialogUtil.getInstance().showToast(TeamEditActivity.this, "change the department fails");
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

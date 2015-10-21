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
import com.zkjinshi.base.net.observer.IMessageObserver;
import com.zkjinshi.base.net.observer.MessageSubject;
import com.zkjinshi.base.net.protocol.ProtocolMSG;
import com.zkjinshi.base.util.Constants;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.TeamEditContactsAdapter;
import com.zkjinshi.superservice.bean.TeamContactBean;
import com.zkjinshi.superservice.entity.EmpStatusRecord;
import com.zkjinshi.superservice.entity.MsgEmpStatus;
import com.zkjinshi.superservice.entity.MsgEmpStatusRSP;
import com.zkjinshi.superservice.factory.ShopEmployeeFactory;
import com.zkjinshi.superservice.listener.GetTeamContactsListener;
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
import com.zkjinshi.superservice.vo.OnlineStatus;
import com.zkjinshi.superservice.vo.ShopEmployeeVo;
import com.zkjinshi.superservice.vo.WorkStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 团队编辑页面
 * 开发者：vincent
 * 日期：2015/10/12
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class TeamEditActivity extends Activity implements IMessageObserver{

    private final static String TAG = TeamEditActivity.class.getSimpleName();

    private List<ShopEmployeeVo> mCheckedList;

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

        addObservers();
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

        mShopEmployeeVos = ShopEmployeeDBUtil.getInstance().queryAllByDeptIDAsc();
        LogUtil.getInstance().info(LogLevel.INFO, "mShopEmployeeVos:"+mShopEmployeeVos);

        //需要获得在线状态的用户
        List<String> empids = new ArrayList<>();
        Iterator<ShopEmployeeVo> shopEmployeeVoIterator = mShopEmployeeVos.iterator();
        while (shopEmployeeVoIterator.hasNext()) {
            ShopEmployeeVo shopEmployeeVo = shopEmployeeVoIterator.next();
            String empID = shopEmployeeVo.getEmpid();
            if(empID.equals(mUserID)){
                shopEmployeeVoIterator.remove();
                continue;
            }
            empids.add(empID);
        }

        mContactsAdapter = new TeamEditContactsAdapter(TeamEditActivity.this, mShopEmployeeVos);
        mRcvTeamContacts.setAdapter(mContactsAdapter);

        //批量请求客户是否在线
        MsgEmpStatus msgEmpStatus = new MsgEmpStatus();
        msgEmpStatus.setType(ProtocolMSG.MSG_ShopEmpStatus);
        msgEmpStatus.setTimestamp(System.currentTimeMillis());
        msgEmpStatus.setShopid(mShopID);
        msgEmpStatus.setEmps(empids);
        sendEmpStatusRequest(msgEmpStatus);
    }

    /**
     * 发送请求在线状态
     * @param msgEmpStatus
     */
    private void sendEmpStatusRequest(MsgEmpStatus msgEmpStatus) {
        Gson gson = new Gson();
        String jsonMsgEmpStatus = gson.toJson(msgEmpStatus, MsgEmpStatus.class);
        LogUtil.getInstance().info(LogLevel.INFO, "jsonMsgEmpStatus:" + jsonMsgEmpStatus);
        WebSocketManager.getInstance().sendMessage(jsonMsgEmpStatus);
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
                ShopEmployeeVo shopEmployeeVo = mShopEmployeeVos.get(postion);
                if (mCheckedList.contains(shopEmployeeVo)) {
                    mCheckedList.remove(shopEmployeeVo);
                } else {
                    mCheckedList.add(shopEmployeeVo);
                }
            }
        });

        mRlChangeDepartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** 1.获得待操作数据 */
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
    private void deleteEmployees(String userID, String token, final String shopID, List<ShopEmployeeVo> shopEmployeeVos) {
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
                try {
                    JSONObject jsonObject = new JSONObject(jsonResult);
                    if (jsonObject.getBoolean("set")) {
                        for(ShopEmployeeVo shopEmployeeVo : mCheckedList){
                            mShopEmployeeVos.remove(shopEmployeeVo);
                            long delResult = -1;
                            String empID = shopEmployeeVo.getEmpid();
                            delResult = ShopEmployeeDBUtil.getInstance().deleteShopEmployeeByEmpID(empID);
                            LogUtil.getInstance().info(LogLevel.INFO, "delResult" + delResult);
                           if(delResult > 0){
                               DialogUtil.getInstance().showToast(TeamEditActivity.this, "删除成功");
                           }
                        }
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

    private void addObservers() {
        MessageSubject.getInstance().addObserver(this, ProtocolMSG.MSG_ShopEmpStatus_RSP);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageSubject.getInstance().removeObserver(TeamEditActivity.this, ProtocolMSG.MSG_ShopEmpStatus_RSP);
    }

    @Override
    public void receive(String message) {
        System.out.print("message:" + message);
        if(TextUtils.isEmpty(message)){
            return ;
        }

        Gson gson = null;
        if(null == gson )
            gson = new Gson();

        try {
            JSONObject messageObj = new JSONObject(message);
            int type = messageObj.getInt("type");

            if (type == ProtocolMSG.MSG_ShopEmpStatus_RSP) {
                MsgEmpStatusRSP msgEmpStatusRSP = gson.fromJson(message, MsgEmpStatusRSP.class);
                if(null != msgEmpStatusRSP && mShopID.equals(msgEmpStatusRSP.getShopid())){
                    List<EmpStatusRecord> empStatusRecords = msgEmpStatusRSP.getResult();
                    Map<String, EmpStatusRecord> empStatusRecordMap = new HashMap<>();

                    //接收服务器返回在线员工数组
                    for(EmpStatusRecord empStatusRecord : empStatusRecords){
                        empStatusRecordMap.put(empStatusRecord.getEmpid(), empStatusRecord);
                    }

                    //接收服务器返回在线员工数组
                    for(int i=0; i< mShopEmployeeVos.size(); i++){
                        ShopEmployeeVo shopEmployeeVo = mShopEmployeeVos.get(i);
                        String empID = shopEmployeeVo.getEmpid();
                        if(empStatusRecordMap.containsKey(empID)){
                            EmpStatusRecord empStatusRecord = empStatusRecordMap.get(empID);

                            //获得员工是否服务器在线
                            if(empStatusRecord.getOnlinestatus() == OnlineStatus.ONLINE.getValue()){
                                shopEmployeeVo.setOnline_status(OnlineStatus.ONLINE);

                                //获得登录时间
                                Long lastLoginTime = empStatusRecord.getLogintimestamp();
                                shopEmployeeVo.setLastOnLineTime(lastLoginTime);

                            } else {
                                shopEmployeeVo.setOnline_status(OnlineStatus.OFFLINE);
                            }

                            //获得员工是否工作中
                            if(empStatusRecord.getWorkstatus() == WorkStatus.ONWORK.getValue()){
                                shopEmployeeVo.setWork_status(WorkStatus.ONWORK);
                            } else {
                                shopEmployeeVo.setWork_status(WorkStatus.OFFWORK);
                            }
                            //更新数据库
                            ShopEmployeeDBUtil.getInstance().addShopEmployee(shopEmployeeVo);
                            mShopEmployeeVos.remove(i);
                            mShopEmployeeVos.add(i, shopEmployeeVo);
                        }
                    }
                    mContactsAdapter.updateListView(mShopEmployeeVos);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(Constants.ZKJINSHI_BASE_TAG, TAG + ".onNetReceiveSucceed()->message:" + message);
    }

    /**
     * 弹出选择部门对话框
     * @param shopEmployeeVos
     */
    private void showChangeDepartmentDialog(final List<ShopEmployeeVo> shopEmployeeVos){
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
    private String convertList2String(List<ShopEmployeeVo> shopEmployeeVos) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<shopEmployeeVos.size(); i++){
            if(i == shopEmployeeVos.size()-1){
                sb.append(shopEmployeeVos.get(i).getEmpid());
            }else {
                sb.append(shopEmployeeVos.get(i).getEmpid() +  ",");
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
        bizMap.put("deptid", deptID+"");
        bizMap.put("changelist", changeList);
        LogUtil.getInstance().info(LogLevel.INFO, "changelist:" + changeList);

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
                                        DialogUtil.getInstance().showCustomToast(TeamEditActivity.this,
                                                       "change the department success", Gravity.CENTER);
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

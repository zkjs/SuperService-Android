package com.zkjinshi.superservice.activity.set;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.TeamEditContactsAdapter;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.sqlite.ShopEmployeeDBUtil;
import com.zkjinshi.superservice.utils.DepartmentDialog;
import com.zkjinshi.superservice.vo.DepartmentVo;
import com.zkjinshi.superservice.vo.ShopEmployeeVo;

import java.util.ArrayList;
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

    private String        mShopID;
    private List<Boolean> mCheckedList;
    private ImageButton   mIbtnBack;
    private TextView      mTvTitle;
    private RecyclerView  mRcvTeamContacts;
    private RelativeLayout  mRlChangeDepartment;
    private RelativeLayout  mRlDelete;
    private LinearLayoutManager     mLayoutManager;
    private TeamEditContactsAdapter mContactsAdapter;
    private List<ShopEmployeeVo>    mShopEmployeeVos;

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
        mShopID = getIntent().getStringExtra("shop_id");
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

                /** 1.获得待删除数据 */
                List<ShopEmployeeVo> shopEmployeeVos = getCheckedEmps(mCheckedList);
                if(null == shopEmployeeVos && shopEmployeeVos.isEmpty()) {
                    //ToDO:弹出提示框 是否更换部门
                    showChangeDepartmentDialog();
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
                List<ShopEmployeeVo> shopEmployeeVos = getCheckedEmps(mCheckedList);
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
    private List<ShopEmployeeVo> getCheckedEmps(List<Boolean> mCheckedList) {
        List<ShopEmployeeVo> deleteEmps = new ArrayList<>();
        for(Boolean isChecked : mCheckedList){
            if(isChecked){
                int index = mCheckedList.indexOf(isChecked);
                deleteEmps.add(mShopEmployeeVos.get(index));
            }
        }
        return deleteEmps;
    }

    private void showChangeDepartmentDialog(){
        //弹出选择部门对话框
        DepartmentDialog dialog = new DepartmentDialog(TeamEditActivity.this);
        dialog.setClickOneListener(new DepartmentDialog.ClickOneListener() {
            @Override
            public void clickOne(DepartmentVo departmentVo) {

            }

        });
        dialog.show();
    }

}

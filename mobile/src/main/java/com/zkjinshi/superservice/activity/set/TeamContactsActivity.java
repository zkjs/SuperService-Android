package com.zkjinshi.superservice.activity.set;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.chat.single.ChatActivity;
import com.zkjinshi.superservice.adapter.TeamContactsAdapter;
import com.zkjinshi.superservice.bean.TeamContactBean;
import com.zkjinshi.superservice.listener.GetTeamContactsListener;
import com.zkjinshi.superservice.sqlite.ShopEmployeeDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.view.AutoSideBar;
import com.zkjinshi.superservice.vo.IdentityType;
import com.zkjinshi.superservice.vo.OnlineStatus;
import com.zkjinshi.superservice.vo.ShopEmployeeVo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 团队联系人显示界面
 * 开发者：vincent
 * 日期：2015/10/9
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class TeamContactsActivity extends AppCompatActivity{

    private final static String TAG = TeamContactsActivity.class.getSimpleName();

    public static final int ADD_REQUEST_CODE = 1;

    private Toolbar         mToolbar;
    private TextView        mTvCenterTitle;
    private ListView        mRvTeamContacts;
    private RelativeLayout  mRlSideBar;
    private TextView        mTvDialog;
    private AutoSideBar     mAutoSideBar;

    private TeamContactsAdapter     mTeamContactAdapter;
    private List<ShopEmployeeVo>    mShopEmployeeVos;
    private ShopEmployeeVo          mFirstShopEmployee;

    private String mUserID;
    private String mShopID;
    private String mToken;

    private IdentityType mUserType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_contacts);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.mipmap.ic_fanhui);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTvCenterTitle = (TextView) findViewById(R.id.tv_center_title);
        mTvCenterTitle.setText(getString(R.string.team));

        mRvTeamContacts = (ListView)     findViewById(R.id.rcv_team_contacts);
        mRlSideBar      = (RelativeLayout)   findViewById(R.id.rl_side_bar);
        mTvDialog       = (TextView)         findViewById(R.id.tv_dialog);
        mAutoSideBar    = new AutoSideBar(TeamContactsActivity.this);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                DisplayUtil.dip2px(TeamContactsActivity.this, 30),
                ViewGroup.LayoutParams.MATCH_PARENT);
        mAutoSideBar.setTextView(mTvDialog);
        mAutoSideBar.setLayoutParams(layoutParams);
    }

    private void initData() {

        mUserID     = CacheUtil.getInstance().getUserId();
        mToken      = CacheUtil.getInstance().getToken();
        mShopID     = CacheUtil.getInstance().getShopID();
        mUserType   = CacheUtil.getInstance().getLoginIdentity();

        // 创建商店排序对象
        mFirstShopEmployee = new ShopEmployeeVo();
        String shopName    = CacheUtil.getInstance().getShopFullName();
        mFirstShopEmployee.setName(shopName);
        mFirstShopEmployee.setDept_name(shopName);
        mFirstShopEmployee.setEmpid(System.currentTimeMillis() + "");

        mShopEmployeeVos    = new ArrayList<>();
        mTeamContactAdapter = new TeamContactsAdapter(TeamContactsActivity.this,
                                                               mShopEmployeeVos);
        mRvTeamContacts.setAdapter(mTeamContactAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showDataList();
    }

    /**
     * 初始化待显示数据并展示
     */
    private void showDataList() {
        //获取团队列表
        DialogUtil.getInstance().showProgressDialog(TeamContactsActivity.this);
        TeamContactsController.getInstance().getTeamContacts(
                TeamContactsActivity.this,
                mUserID, mToken, mShopID, new GetTeamContactsListener() {
                    @Override
                    public void getContactsDone(List<TeamContactBean> teamContacts) {
                        List<ShopEmployeeVo> shopEmployeeVos = ShopEmployeeDBUtil.getInstance().queryTeamByShopID(mShopID);
                        List<String> strLetters = new ArrayList<>();//首字母显示数组
                        List<String> empids     = new ArrayList<>();//员工ID数组

                        if (null != shopEmployeeVos && !shopEmployeeVos.isEmpty()) {
                            Iterator<ShopEmployeeVo> shopEmployeeVoIterator = shopEmployeeVos.iterator();
                            while (shopEmployeeVoIterator.hasNext()) {
                                ShopEmployeeVo shopEmployeeVo = shopEmployeeVoIterator.next();
                                String empID = shopEmployeeVo.getEmpid();
                                if (empID.equals(mUserID)) {
                                    shopEmployeeVoIterator.remove();
                                } else {
                                    shopEmployeeVo.setOnline_status(OnlineStatus.OFFLINE);
                                    continue;
                                }
                            }

                            if(null != mShopEmployeeVos && !mShopEmployeeVos.isEmpty()){
                                mShopEmployeeVos.removeAll(mShopEmployeeVos);
                            }
                            mShopEmployeeVos.add(0, mFirstShopEmployee);
                            mShopEmployeeVos.addAll(shopEmployeeVos);

                            //获取部门首字母进行排序
                            for (ShopEmployeeVo shopEmployeeVo : shopEmployeeVos) {
                                empids.add(shopEmployeeVo.getEmpid());
                                if (TextUtils.isEmpty(shopEmployeeVo.getDept_name())) {
                                    shopEmployeeVo.setDept_name("#");
                                }
                                String sortLetter = shopEmployeeVo.getDept_name().substring(0, 1);
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
                                //移除之前设置
                                mRlSideBar.removeAllViews();
                                mRlSideBar.addView(mAutoSideBar);
                            }

                            DialogUtil.getInstance().cancelProgressDialog();
                            mTeamContactAdapter.setData(mShopEmployeeVos);
                        }
                        //更新数据库数据
                        ShopEmployeeDBUtil.getInstance().batchAddShopEmployees(shopEmployeeVos);
                    }

                    @Override
                    public void getContactsFailed() {
                        //获取在线数据失败更新
                        List<ShopEmployeeVo> shopEmployeeVos = ShopEmployeeDBUtil.getInstance().queryTeamByShopID(mShopID);
                        if(shopEmployeeVos != null && !shopEmployeeVos.isEmpty()){
                            if(null != mShopEmployeeVos && !mShopEmployeeVos.isEmpty()){
                                mShopEmployeeVos.removeAll(mShopEmployeeVos);
                            }
                            mShopEmployeeVos.add(0, mFirstShopEmployee);
                            mShopEmployeeVos.addAll(shopEmployeeVos);
                        }
                        mTeamContactAdapter.setData(mShopEmployeeVos);
                        DialogUtil.getInstance().cancelProgressDialog();
                    }
                }
        );
    }

    private void initListener() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeamContactsActivity.this.finish();
            }
        });

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(android.view.MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_team_search:
                        //TODO: search 团队成员检索
                        break;

                    case R.id.menu_team_jia:
                        Intent intent = new Intent(TeamContactsActivity.this, EmployeeAddActivity.class);
                        startActivityForResult(intent, ADD_REQUEST_CODE);
                        overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
                        break;

                    case R.id.menu_team_edit:
                        Intent teamEdit = new Intent(TeamContactsActivity.this, TeamEditActivity.class);
                        teamEdit.putExtra("shop_id", mShopID);
                        TeamContactsActivity.this.startActivity(teamEdit);
                        break;
                }
                return true;
            }
        });

        mAutoSideBar.setOnTouchingLetterChangedListener(new AutoSideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = mTeamContactAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mRvTeamContacts.setSelection(position);
                }
            }
        });

        mRvTeamContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShopEmployeeVo shopEmployeeVo = mShopEmployeeVos.get(position);
                String userId = shopEmployeeVo.getEmpid();
                String toName = shopEmployeeVo.getName();
                String shopName = CacheUtil.getInstance().getShopFullName();
                Intent intent = new Intent(TeamContactsActivity.this, ChatActivity.class);
                intent.putExtra(com.zkjinshi.superservice.utils.Constants.EXTRA_USER_ID, userId);
                if (!TextUtils.isEmpty(mShopID)) {
                    intent.putExtra(com.zkjinshi.superservice.utils.Constants.EXTRA_SHOP_ID,mShopID);
                }
                intent.putExtra(com.zkjinshi.superservice.utils.Constants.EXTRA_SHOP_NAME, shopName);
                if(!TextUtils.isEmpty(toName)){
                    intent.putExtra(com.zkjinshi.superservice.utils.Constants.EXTRA_TO_NAME, toName);
                }
                intent.putExtra(com.zkjinshi.superservice.utils.Constants.EXTRA_FROM_NAME, CacheUtil.getInstance().getUserName());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if (ADD_REQUEST_CODE == requestCode) {
                showDataList();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(mUserType == IdentityType.BUSINESS){
            getMenuInflater().inflate(R.menu.menu_team_for_business, menu);
        }else {
            getMenuInflater().inflate(R.menu.menu_team_for_waiter, menu);
        }
        return true;
    }

}

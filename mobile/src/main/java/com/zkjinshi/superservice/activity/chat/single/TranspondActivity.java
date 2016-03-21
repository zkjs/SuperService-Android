package com.zkjinshi.superservice.activity.chat.single;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.set.TeamContactsController;
import com.zkjinshi.superservice.adapter.TranspondAdapter;
import com.zkjinshi.superservice.base.BaseAppCompatActivity;
import com.zkjinshi.superservice.bean.BookOrderBean;
import com.zkjinshi.superservice.bean.TeamContactBean;
import com.zkjinshi.superservice.factory.ShopEmployeeFactory;
import com.zkjinshi.superservice.listener.GetTeamContactsListener;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.sqlite.ShopEmployeeDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.view.AutoSideBar;
import com.zkjinshi.superservice.vo.OnlineStatus;
import com.zkjinshi.superservice.vo.OrderDetailForDisplay;
import com.zkjinshi.superservice.vo.EmployeeVo;
import java.util.ArrayList;;
import java.util.Iterator;
import java.util.List;

/**
 * 消息转发页面
 * 开发者：JimmyZhang
 * 日期：2015/11/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class TranspondActivity extends BaseAppCompatActivity {

    private final static String TAG = TranspondActivity.class.getSimpleName();

    private Toolbar    mToolbar;
    private TextView   mTvCenterTitle;
    private ListView   mRvTeamContacts;
    private RelativeLayout mRlSideBar;
    private TextView       mTvDialog;
    private AutoSideBar    mAutoSideBar;

    private TranspondAdapter mTeamContactAdapter;
    private String mUserID;
    private String mShopID;
    private String mToken;

    private OrderDetailForDisplay bookOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transpond);

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
        mTvCenterTitle.setText("发送到");

        mRvTeamContacts = (ListView)     findViewById(R.id.rcv_team_contacts);
        mRlSideBar      = (RelativeLayout)   findViewById(R.id.rl_side_bar);
        mTvDialog       = (TextView)         findViewById(R.id.tv_dialog);
        mAutoSideBar    = new AutoSideBar(TranspondActivity.this);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                DisplayUtil.dip2px(TranspondActivity.this, 30),
                ViewGroup.LayoutParams.MATCH_PARENT);
        mAutoSideBar.setTextView(mTvDialog);
        mAutoSideBar.setLayoutParams(layoutParams);
    }

    private void initData() {

        if(null != getIntent() && null != getIntent().getSerializableExtra("bookOrder")){
            bookOrder = (OrderDetailForDisplay)getIntent().getSerializableExtra("bookOrder");
        }

        mUserID     = CacheUtil.getInstance().getUserId();
        mToken      = CacheUtil.getInstance().getToken();
        mShopID     = CacheUtil.getInstance().getShopID();

        mTeamContactAdapter = new TranspondAdapter(TranspondActivity.this, new ArrayList<EmployeeVo>());
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
        TranspondController.getInstance().getTeamContacts(
                TranspondActivity.this,
                mUserID, mToken, mShopID, new GetTeamContactsListener() {
                    @Override
                    public void getContactsDone(List<EmployeeVo> shopEmployeeVos) {

                        List<String> strLetters = new ArrayList<>();//首字母显示数组
                        List<String> empids     = new ArrayList<>();//员工ID数组

                        if (null != shopEmployeeVos && !shopEmployeeVos.isEmpty()) {
                            Iterator<EmployeeVo> shopEmployeeVoIterator = shopEmployeeVos.iterator();
                            while (shopEmployeeVoIterator.hasNext()) {
                                EmployeeVo shopEmployeeVo = shopEmployeeVoIterator.next();
                                String empID = shopEmployeeVo.getUserid();
                                if (empID.equals(mUserID)) {
                                    shopEmployeeVoIterator.remove();
                                } else {
                                    //shopEmployeeVo.setOnline_status(OnlineStatus.OFFLINE);
                                    continue;
                                }
                            }

                            //获取部门首字母进行排序
                            for (EmployeeVo shopEmployeeVo : shopEmployeeVos) {
                                empids.add(shopEmployeeVo.getUserid());
                                String deptName = shopEmployeeVo.getRolename();
                                if (TextUtils.isEmpty(deptName)) {
                                    shopEmployeeVo.setRolename("#");
                                }else {
                                    String sortLetter = deptName.substring(0, 1);
                                    //部门分类并消除相同部门
                                    if (!TextUtils.isEmpty(sortLetter) && !strLetters.contains(sortLetter)) {
                                        strLetters.add(sortLetter);
                                    }
                                }
                            }

                            String[] sortArray = strLetters.toArray(new String[strLetters.size()]);
                            if (sortArray.length > 0) {
                                mAutoSideBar.setSortArray(sortArray);
                                //移除之前设置
                                mRlSideBar.removeAllViews();
                                mRlSideBar.addView(mAutoSideBar);
                            }

                            mTeamContactAdapter.setData(shopEmployeeVos);
                            ShopEmployeeDBUtil.getInstance().batchAddShopEmployees(shopEmployeeVos);
                        }
                    }

                    @Override
                    public void getContactsFailed() {
                        DialogUtil.getInstance().cancelProgressDialog();
                    }
                }
        );
    }

    private void initListener() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TranspondActivity.this.finish();
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
                EmployeeVo shopEmployeeVo = mTeamContactAdapter.mDatas.get(position);
                if(null != shopEmployeeVo){
                    showTranspondDialog(TranspondActivity.this, shopEmployeeVo);
                }
            }
        });
    }

    /**
     * 显示订单转发对话框
     */
    private void showTranspondDialog(Context context,EmployeeVo shopEmployeeVo){
        final String userId = shopEmployeeVo.getUserid();
        final String toName = shopEmployeeVo.getUsername();
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
        customBuilder.setTitle("提示");
        customBuilder.setMessage("确认发送给"+toName+"?");
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
                String shopName = CacheUtil.getInstance().getShopFullName();
                Intent intent = new Intent(TranspondActivity.this, ChatActivity.class);
                intent.putExtra(com.zkjinshi.superservice.utils.Constants.EXTRA_USER_ID, userId);
                if (!TextUtils.isEmpty(mShopID)) {
                    intent.putExtra(com.zkjinshi.superservice.utils.Constants.EXTRA_SHOP_ID, mShopID);
                }
                intent.putExtra(com.zkjinshi.superservice.utils.Constants.EXTRA_SHOP_NAME, shopName);
                if (!TextUtils.isEmpty(toName)) {
                    intent.putExtra(com.zkjinshi.superservice.utils.Constants.EXTRA_TO_NAME, toName);
                }
                if (null != bookOrder) {
                    intent.putExtra("bookOrder", bookOrder);
                }
                intent.putExtra(com.zkjinshi.superservice.utils.Constants.EXTRA_FROM_NAME, CacheUtil.getInstance().getUserName());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        customBuilder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}

package com.zkjinshi.superservice.activity.set;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.ContactAdapter;
import com.zkjinshi.superservice.base.BaseAppCompatActivity;
import com.zkjinshi.superservice.utils.CharacterParser;
import com.zkjinshi.superservice.utils.PinyinComparator;
import com.zkjinshi.superservice.view.ContactPinyinComparator;
import com.zkjinshi.superservice.view.ContactSideBar;
import com.zkjinshi.superservice.vo.GuestVo;
import com.zkjinshi.superservice.vo.MemberVo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 开发者：jimmyzhang
 * 日期：16/7/1
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ContactActivity extends BaseAppCompatActivity {

    private Toolbar toolbar;
    private TextView titleIv;
    private GuestVo guestVo;
    private ArrayList<MemberVo> memberList;
    private ArrayList<MemberVo> selectMemberList;
    private ListView contactListView;
    private LinearLayout topLayout;
    private TextView topChatTv;
    private ContactSideBar sideBarLayout;
    private CharacterParser characterParser;
    private ContactAdapter contactAdapter;
    private ContactPinyinComparator pinyinComparator;
    private int lastFirstVisibleItem = -1;
    private Map<String, Boolean> selectMap;
    private boolean isAll;

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleIv = (TextView) findViewById(R.id.tv_center_title);
        contactListView = (ListView)findViewById(R.id.contact_list_view);
        topLayout = (LinearLayout)findViewById(R.id.top_layout);
        topChatTv = (TextView)findViewById(R.id.top_char);
        sideBarLayout = (ContactSideBar)findViewById(R.id.side_bar_layout);
    }

    private void initData(){
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.mipmap.ic_fanhui);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        titleIv.setText("联系人");
        selectMap = new HashMap<String, Boolean>();
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new ContactPinyinComparator();
        if(null != getIntent() && null != getIntent().getSerializableExtra("guestVo")){
            guestVo = (GuestVo) getIntent().getSerializableExtra("guestVo");
            if(null != guestVo){
                memberList = guestVo.getMember();
                String roleNameStr = guestVo.getRolename();
                if(!TextUtils.isEmpty(roleNameStr)){
                    titleIv.setText(roleNameStr);
                }
            }
            isAll = getIntent().getBooleanExtra("isAll",false);
            selectMemberList = (ArrayList<MemberVo>) getIntent().getSerializableExtra("chooseMemberList");
        }
        memberList = filledData(memberList);
        if(null == selectMemberList){
            selectMemberList = new ArrayList<MemberVo>();
        }
        if(null != selectMemberList && !selectMemberList.isEmpty()){
            for (MemberVo memberVo : selectMemberList){
                selectMap.put(memberVo.getUserid(),true);
            }
        }
        Collections.sort(memberList, pinyinComparator);
        contactAdapter = new ContactAdapter(memberList,this);
        contactAdapter.setSelectMap(selectMap);
        contactListView.setAdapter(contactAdapter);
    }

    private void initListeners(){

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(android.view.MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_contact_sure://确定
                        Intent intent = getIntent();
                        int selectCount = selectMemberList == null ? 0 : selectMemberList.size();
                        int totalCount = memberList.size();
                        if(selectCount == totalCount){
                            intent.putExtra("isAll",true);
                        }else {
                            intent.putExtra("isAll",false);
                        }
                        guestVo.setMember(selectMemberList);
                        intent.putExtra("guestVo",guestVo);
                        setResult(RESULT_OK,intent);
                        finish();
                        break;
                }
                return true;
            }
        });

        sideBarLayout.setOnTouchingLetterChangedListener(new ContactSideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                int position = contactAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    contactListView.setSelection(position);
                }
            }
        });

        /**
         * 设置滚动监听， 实时跟新悬浮的字母的值
         */
        contactListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                int section = contactAdapter.getSectionForPosition(firstVisibleItem);
                int nextSecPosition = contactAdapter
                        .getPositionForSection(section + 1);
                if (firstVisibleItem != lastFirstVisibleItem) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) topLayout
                            .getLayoutParams();
                    params.topMargin = 0;
                    topLayout.setLayoutParams(params);
                    topChatTv.setText(String.valueOf((char) section));
                }
                if (nextSecPosition == firstVisibleItem + 1) {
                    View childView = view.getChildAt(0);
                    if (childView != null) {
                        int titleHeight = topLayout.getHeight();
                        int bottom = childView.getBottom();
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) topLayout
                                .getLayoutParams();
                        if (bottom < titleHeight) {
                            float pushedDistance = bottom - titleHeight;
                            params.topMargin = (int) pushedDistance;
                            topLayout.setLayoutParams(params);
                        } else {
                            if (params.topMargin != 0) {
                                params.topMargin = 0;
                                topLayout.setLayoutParams(params);
                            }
                        }
                    }
                }
                lastFirstVisibleItem = firstVisibleItem;
            }
        });

        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MemberVo memberVo = (MemberVo) parent.getAdapter().getItem(position);
                String memberId = memberVo.getUserid();
                if (selectMap != null
                        && selectMap.containsKey(memberId)
                        && selectMap.get(memberId)) {
                    selectMap.put(memberId, false);
                    Iterator<MemberVo> iterator = selectMemberList.iterator();
                    while(iterator.hasNext()){
                        MemberVo member = iterator.next();
                        if(member.getUserid().equals(memberId)){
                            iterator.remove();
                        }
                    }
                } else {
                    selectMap.put(memberId, true);
                    boolean isChoose = false;
                    if(null != selectMemberList && !selectMemberList.isEmpty()){
                        for (MemberVo member : selectMemberList){
                            if(member.getUserid().equals(memberId)){
                                isChoose = true;
                            }
                        }
                    }
                    if(!isChoose){
                        selectMemberList.add(memberVo);
                    }
                }
                contactAdapter.setSelectMap(selectMap);
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        initView();
        initData();
        initListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact, menu);
        return true;
    }


    /**
     * 填充数据
     * @param memberList
     * @return
     */
    private ArrayList<MemberVo> filledData(ArrayList<MemberVo> memberList) {
        ArrayList<MemberVo> mSortList = new ArrayList<MemberVo>();

        for (int i = 0; i < memberList.size(); i++) {
            MemberVo memberVo = memberList.get(i);
            String pinyin = characterParser.getSelling(memberVo.getUsername());
            String sortString = pinyin.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {
                memberVo.setSortLetter(sortString.toUpperCase());
            } else {
                memberVo.setSortLetter("#");
            }
            mSortList.add(memberVo);
        }
        return mSortList;

    }
}

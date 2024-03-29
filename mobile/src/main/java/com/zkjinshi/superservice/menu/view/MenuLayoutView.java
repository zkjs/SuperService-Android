package com.zkjinshi.superservice.menu.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.zkjinshi.superservice.menu.vo.MenuGroup;

import java.util.ArrayList;

/**
 * 菜单容器View
 * 开发者：JimmyZhang
 * 日期：2015/8/22
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MenuLayoutView extends LinearLayout{

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<MenuGroup> menuGroupList;
    private MenuGroupView menuGroupView;
    private LayoutParams layoutParams;
    private ChatMenuType chatMenuType = ChatMenuType.SINGLE;

    @SuppressLint("NewApi")
    public MenuLayoutView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MenuLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MenuLayoutView(Context context) {
        super(context);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public MenuLayoutView(Context context,ChatMenuType chatMenuType) {
        this(context);
        this.chatMenuType = chatMenuType;
    }

    public MenuLayoutView(Context context, ArrayList<MenuGroup> menuGroupList) {
        super(context);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.menuGroupList = menuGroupList;
    }

    public MenuLayoutView(Context context, ArrayList<MenuGroup> menuGroupList,ChatMenuType chatMenuType){
        this(context,menuGroupList);
        this.chatMenuType = chatMenuType;
    }


    public void init(){
        layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT , 1.0f);
        if(null != menuGroupList && !menuGroupList.isEmpty()){
            if(null != menuGroupList && !menuGroupList.isEmpty()){
                menuGroupView = new MenuGroupView(context, menuGroupList.get(0),true,0,PostionType.LEFT,chatMenuType);
                menuGroupView.setLayoutParams(layoutParams);
                addView(menuGroupView);
                for(int i= 1; i< menuGroupList.size()-1; i++){
                    menuGroupView = new MenuGroupView(context, menuGroupList.get(i),true,i,PostionType.CENTER,chatMenuType);
                    menuGroupView.setLayoutParams(layoutParams);
                    addView(menuGroupView);
                }
                menuGroupView = new MenuGroupView(context, menuGroupList.get(menuGroupList.size()-1), false,menuGroupList.size()-1,PostionType.RIGHT,chatMenuType);
                menuGroupView.setLayoutParams(layoutParams);
                addView(menuGroupView);
            }
        }
        invalidate();
    }
}

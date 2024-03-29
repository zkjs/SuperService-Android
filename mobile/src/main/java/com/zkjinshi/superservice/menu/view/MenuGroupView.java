package com.zkjinshi.superservice.menu.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.menu.action.ChatGroupMenuAction;
import com.zkjinshi.superservice.menu.action.ChatMenuAction;
import com.zkjinshi.superservice.menu.action.MenuAction;
import com.zkjinshi.superservice.menu.action.PushMenuAction;
import com.zkjinshi.superservice.menu.vo.ActionType;
import com.zkjinshi.superservice.menu.vo.MenuGroup;
import com.zkjinshi.superservice.menu.vo.MenuItem;
import com.zkjinshi.superservice.menu.vo.MenuType;

import java.util.ArrayList;

/**
 * 菜单组View
 * 开发者：JimmyZhang
 * 日期：2015/8/22
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MenuGroupView extends LinearLayout {
    private Context context;
    private LayoutInflater inflater;
    private MenuGroup menuGroup;
    private ArrayList<MenuItem> menuItemList;
    private String url;
    private String menuName;
    private TextView menuTv;
    private ImageView menuTipIv;
    private MenuType menuType;
    private RelativeLayout menuBtnLayout;
    private TextView cutlineTv;
    private MenuItemView menuItemView;
    private LayoutParams layoutParams;
    private int position;
    private PostionType postionType;
    private MenuAction menuAction;
    private ChatMenuType chatMenuType = ChatMenuType.SINGLE;

    @SuppressLint("NewApi")
    public MenuGroupView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public MenuGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public MenuGroupView(Context context) {
        super(context);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public MenuGroupView(Context context, MenuGroup menuGroup) {
        this(context);
        this.menuGroup = menuGroup;
    }

    public MenuGroupView(Context context, MenuGroup menuGroup,
                           boolean isShowCutline) {
        this(context, menuGroup);
    }

    public MenuGroupView(Context context, MenuGroup menuGroup,
                           boolean isShowCutline,int position) {
        this(context, menuGroup, isShowCutline);
        this.position = position;
    }

    public MenuGroupView(Context context, MenuGroup menuGroup,
                         boolean isShowCutline,int position,PostionType type) {
        this(context, menuGroup, isShowCutline);
        this.position = position;
        this.postionType = type;
        initView(isShowCutline);
        initListeners();
    }

    public MenuGroupView(Context context, MenuGroup menuGroup,
                         boolean isShowCutline,int position,PostionType type,ChatMenuType chatMenuType) {
        this(context, menuGroup, isShowCutline);
        this.chatMenuType = chatMenuType;
        this.position = position;
        this.postionType = type;
        initView(isShowCutline);
        initListeners();
    }

    public void initView(boolean isShowCutline) {
        menuBtnLayout =  (RelativeLayout) inflater.inflate(R.layout.menu_group_btn, null);
        menuTv = (TextView) menuBtnLayout.findViewById(R.id.menu_group_tv);
        menuTipIv = (ImageView)menuBtnLayout.findViewById(R.id.menu_group_tip_iv);
        layoutParams = new LayoutParams(0, DisplayUtil.dip2px(context, 47f), 1.0f);
        url = menuGroup.getUrl();
        menuName = menuGroup.getMenuName();
        menuTv.setText(menuName);
        menuTv.setVisibility(View.VISIBLE);
        menuBtnLayout.setLayoutParams(layoutParams);
        cutlineTv = new TextView(context);
        cutlineTv.setBackgroundResource(R.color.menu_cut_line);
        layoutParams = new LayoutParams(DisplayUtil.dip2px(context, 0.5f), LayoutParams.MATCH_PARENT);
        cutlineTv.setGravity(Gravity.CENTER_VERTICAL);
        cutlineTv.setLayoutParams(layoutParams);
        addView(menuBtnLayout);
        if (isShowCutline) {
            addView(cutlineTv);
        }
        menuType = menuGroup.getMenuType();
        if(null != menuType && menuType == MenuType.MULTI){
            menuTipIv.setVisibility(View.VISIBLE);
        }else{
            menuTipIv.setVisibility(View.GONE);
        }
        invalidate();
    }

    public void initListeners() {
        menuItemList = menuGroup.getMenuItemList();
        if (null != menuType && menuType == MenuType.MULTI) {
            menuItemView = new MenuItemView(context, menuItemList,chatMenuType);
            menuTv.setTag(menuItemView);
            menuTv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    menuItemView = (MenuItemView) view.getTag();
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    int widthSpec = MeasureSpec.makeMeasureSpec(0,
                            MeasureSpec.UNSPECIFIED);
                    int heightSpec = MeasureSpec.makeMeasureSpec(0,
                            MeasureSpec.UNSPECIFIED);
                    menuItemView.getMenuLayout().measure(widthSpec, heightSpec);
                    layoutParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
                    int popupHeight = menuItemView.getMenuLayout().getMeasuredHeight();
                    int popupWidth = menuItemView.getMenuLayout().getMeasuredWidth();
                    int viewWidth = view.getMeasuredWidth();
                    if (postionType == PostionType.LEFT) {
                        menuItemView.showAtLocation(view,
                                Gravity.NO_GRAVITY, location[0] + 20, location[1] -
                                        popupHeight);
                    } else if (postionType == PostionType.CENTER) {
                        menuItemView.showAtLocation(view,
                                Gravity.NO_GRAVITY, location[0], location[1] -
                                        popupHeight);
                    } else if (postionType == PostionType.RIGHT) {
                        if (popupWidth > viewWidth) {
                            menuItemView.showAtLocation(view,
                                    Gravity.NO_GRAVITY, location[0] - 20 - (popupWidth - viewWidth), location[1] -
                                            popupHeight);
                        } else {
                            menuItemView.showAtLocation(view,
                                    Gravity.NO_GRAVITY, location[0], location[1] -
                                            popupHeight);
                        }
                    }
                }
            });
        } else {
                menuTv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        //执行按钮动作
                        ActionType actionType = menuGroup.getActionType();
                        if (actionType == ActionType.CHAT) {//进行预订聊天
                            if(chatMenuType == ChatMenuType.SINGLE){
                                menuAction = new ChatMenuAction(menuName);
                            }else {
                                menuAction = new ChatGroupMenuAction(menuName);
                            }
                            menuAction.executeAction();
                        } else if (actionType == ActionType.PUSH) {//推送最新预定信息
                            menuAction = new PushMenuAction();
                            menuAction.executeAction();
                        }
                    }
                });
            }
        }

}

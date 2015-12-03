package com.zkjinshi.superservice.activity.common;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.set.ClientActivity;
import com.zkjinshi.superservice.activity.set.TeamContactsActivity;
import com.zkjinshi.superservice.adapter.ViewPagerAdapter;
import com.zkjinshi.superservice.view.Fab;

/**
 * 开发者：JimmyZhang
 * 日期：2015/9/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MainActivityController implements View.OnClickListener{

    private MainActivity activity;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private MaterialSheetFab materialSheetFab;
    private int statusBarColor;
    private DisplayImageOptions options;
    private Intent intent;

    ViewPagerAdapter viewPagerAdapter;

    public MainActivityController(MainActivity activity){
        this.activity = activity;
        this.intent = activity.getIntent();
        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_main_user_default_photo_nor)// 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.ic_main_user_default_photo_nor)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.ic_main_user_default_photo_nor)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();
    }

    /**
     * 设置头像图片
     * @param photoUrl
     * @param photoImageView
     */
    public void setUserPhoto(String photoUrl,ImageView photoImageView){
        if(!TextUtils.isEmpty(photoUrl) && photoImageView != null){
            ImageLoader.getInstance().displayImage(photoUrl, photoImageView, options);
        }
    }

    public void clearImageChache(){
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
    }

    public void onCreate() {
        setupActionBar();
        setupDrawer();
        setupFab();
        setupTabs();
    }

    public void onPostCreate() {
        drawerToggle.syncState();
    }

    public boolean onBackPressed() {
        if (materialSheetFab.isSheetVisible()) {
            materialSheetFab.hideSheet();
            return false;
        }
        return true;
    }

    /**
     * 设置action bar
     */
    private void setupActionBar() {
        activity.setSupportActionBar((Toolbar) activity.findViewById(R.id.toolbar));
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 设置导航侧滑栏
     */
    private void setupDrawer() {
        drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(activity, drawerLayout, R.string.opendrawer,
                R.string.closedrawer);
        drawerLayout.setDrawerListener(drawerToggle);

    }

    /**
     * 设置tabs.
     */
    private void setupTabs() {
        // 设置ViewPager
        ViewPager viewpager = (ViewPager) activity.findViewById(R.id.viewpager);
       viewPagerAdapter = new ViewPagerAdapter(activity, activity.getSupportFragmentManager());
        viewpager.setAdapter(viewPagerAdapter);
        viewpager.setOffscreenPageLimit(ViewPagerAdapter.NUM_ITEMS);
        updatePage(viewpager.getCurrentItem());
        // 设置导航菜单
        TabLayout tabLayout = (TabLayout) activity.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewpager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(viewPagerAdapter.getTabView(i));
        }
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                updatePage(i);
                //setMessageNum(i,0);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        int currentItem = intent.getIntExtra("currentItem",0);
        viewpager.setCurrentItem(currentItem);

    }

    public void setMessageNum(int postion,int num){
        viewPagerAdapter.setNumText(postion,num);
    }

    /**
     * 设置悬浮菜单按钮
     */
    private void setupFab() {

        Fab fab = (Fab)activity. findViewById(R.id.fab);
        View sheetView = activity.findViewById(R.id.fab_sheet);
        View overlay = activity.findViewById(R.id.overlay);
        int sheetColor = activity.getResources().getColor(R.color.background_card);
        int fabColor = activity.getResources().getColor(R.color.text_black_87);

        //初始化悬浮按钮
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay, sheetColor, fabColor);

        //悬浮按钮设置事件监听
        materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onShowSheet() {
                statusBarColor = getStatusBarColor();
                setStatusBarColor(activity.getResources().getColor(R.color.text_black_87));
            }

            @Override
            public void onHideSheet() {
                setStatusBarColor(statusBarColor);
            }
        });

        // 悬浮item栏设置事件监听
        activity.findViewById(R.id.fab_sheet_item_recording).setOnClickListener(this);
        activity.findViewById(R.id.fab_sheet_item_reminder).setOnClickListener(this);
    }

    /**
     * 更新当前页数据
     * @param selectedPage
     */
    private void updatePage(int selectedPage) {
        updateFab(selectedPage);
    }

    /**
     * 更新当Fab悬浮按钮数据
     * @param selectedPage
     */
    private void updateFab(int selectedPage) {
        switch (selectedPage) {
            case ViewPagerAdapter.ALL_POS:
                materialSheetFab.hideSheetThenFab();
                break;
            case ViewPagerAdapter.SHARED_POS:
                materialSheetFab.showFab();
                break;
            case ViewPagerAdapter.FAVORITES_POS:
                materialSheetFab.hideSheetThenFab();
                break;
            default:
        }
    }

    /**
     * 切换侧滑栏
     */
    public void toggleDrawer() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    /**
     * 获取状态栏颜色
     * @return
     */
    private int getStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return activity.getWindow().getStatusBarColor();
        }
        return 0;
    }

    /**
     * 设置状态栏颜色
     * @param color
     */
    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(color);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_sheet_item_recording://与团队对话
                {
                    Intent intent = new Intent(activity, TeamContactsActivity.class);
                    activity.startActivity(intent);
                }
                break;
            case R.id.fab_sheet_item_reminder://与客人对话
                {
                    Intent intent = new Intent(activity, ClientActivity.class);
                    activity.startActivity(intent);
                }
                break;
            default:
                break;
        }
        materialSheetFab.hideSheet();
    }
}

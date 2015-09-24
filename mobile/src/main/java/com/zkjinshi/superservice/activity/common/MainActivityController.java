package com.zkjinshi.superservice.activity.common;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;
import com.zkjinshi.superservice.R;
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

    public MainActivityController(MainActivity activity){
        this.activity = activity;
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

    public void onBackPressed() {
        if (materialSheetFab.isSheetVisible()) {
            materialSheetFab.hideSheet();
        } else {
            activity.onBackPressed();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = activity.getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return activity.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                toggleDrawer();
                return true;
            default:
                return activity.onOptionsItemSelected(item);
        }
    }

    /**
     * Sets up the action bar.
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
        viewpager.setAdapter(new ViewPagerAdapter(activity, activity.getSupportFragmentManager()));
        viewpager.setOffscreenPageLimit(ViewPagerAdapter.NUM_ITEMS);
        updatePage(viewpager.getCurrentItem());

        // 设置导航菜单
        TabLayout tabLayout = (TabLayout) activity.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewpager);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                updatePage(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    /**
     * 设置悬浮菜单按钮
     */
    private void setupFab() {

        Fab fab = (Fab)activity. findViewById(R.id.fab);
        View sheetView = activity.findViewById(R.id.fab_sheet);
        View overlay = activity.findViewById(R.id.overlay);
        int sheetColor = activity.getResources().getColor(R.color.background_card);
        int fabColor = activity.getResources().getColor(R.color.theme_accent);

        // Create material sheet FAB
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay, sheetColor, fabColor);

        // Set material sheet event listener
        materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onShowSheet() {
                // Save current status bar color
                statusBarColor = getStatusBarColor();
                // Set darker status bar color to match the dim overlay
                setStatusBarColor(activity.getResources().getColor(R.color.theme_primary_dark2));
            }

            @Override
            public void onHideSheet() {
                // Restore status bar color
                setStatusBarColor(statusBarColor);
            }
        });

        // Set material sheet item click listeners
        activity.findViewById(R.id.fab_sheet_item_recording).setOnClickListener(this);
        activity.findViewById(R.id.fab_sheet_item_reminder).setOnClickListener(this);
        activity.findViewById(R.id.fab_sheet_item_photo).setOnClickListener(this);
        activity.findViewById(R.id.fab_sheet_item_note).setOnClickListener(this);
    }

    /**
     * Called when the selected page changes.
     *
     * @param selectedPage selected page
     */
    private void updatePage(int selectedPage) {
        updateFab(selectedPage);
        updateSnackbar(selectedPage);
    }

    /**
     * Updates the FAB based on the selected page
     *
     * @param selectedPage selected page
     */
    private void updateFab(int selectedPage) {
        switch (selectedPage) {
            case ViewPagerAdapter.ALL_POS:
                materialSheetFab.showFab();
                break;
            case ViewPagerAdapter.SHARED_POS:
                materialSheetFab.showFab(0,
                        -activity.getResources().getDimensionPixelSize(R.dimen.snackbar_height));
                break;
            case ViewPagerAdapter.FAVORITES_POS:
            default:
                materialSheetFab.hideSheetThenFab();
                break;
        }
    }

    /**
     * Updates the snackbar based on the selected page
     *
     * @param selectedPage selected page
     */
    private void updateSnackbar(int selectedPage) {
        View snackbar = activity.findViewById(R.id.snackbar);
        switch (selectedPage) {
            case ViewPagerAdapter.SHARED_POS:
                snackbar.setVisibility(View.VISIBLE);
                break;
            case ViewPagerAdapter.ALL_POS:
            case ViewPagerAdapter.FAVORITES_POS:
            default:
                snackbar.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * Toggles opening/closing the drawer.
     */
    public void toggleDrawer() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    private int getStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return activity.getWindow().getStatusBarColor();
        }
        return 0;
    }

    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(color);
        }
    }


    @Override
    public void onClick(View v) {
        Toast.makeText(activity, R.string.sheet_item_pressed, Toast.LENGTH_SHORT).show();
        materialSheetFab.hideSheet();
    }
}

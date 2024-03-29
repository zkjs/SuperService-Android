package com.zkjinshi.superservice.activity.chat.single.actions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.order.OrderOperationController;
import com.zkjinshi.superservice.adapter.MoreAdapter;
import com.zkjinshi.superservice.adapter.MorePageAdapter;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.FileUtil;
import com.zkjinshi.superservice.view.CirclePageIndicator;
import com.zkjinshi.superservice.view.JazzyViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 更多栏管理器
 * 开发者：JimmyZhang
 * 日期：2015/08/03
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MoreViewPagerManager extends Handler {
	
	public static final String TAG = "MoreViewPagerManager";

	private int CURRENT_MORE_PAGE = 0; // 当前更多页

	private static final int CHOOSE_IMAGE = 0;// 图片
	private static final int TAKE_PHOTO  = 1;// 拍照
	private static final int ORDER_DEAL  = 2;// 订单

	private Context context;
	private JazzyViewPager moreViewPager;
	private LinearLayout moreLinearLayout;


	public MoreViewPagerManager(Context context, LinearLayout moreLinearLayout) {
		this.context = context;
		this.moreLinearLayout = moreLinearLayout;
	}

	public void init() {
		initView((Activity) context);
		initMorePage();
        OrderOperationController.getInstance().init(context);
	}

	private void initView(Activity activity) {
		moreViewPager = (JazzyViewPager) activity.findViewById(R.id.more_pager);
	}

	private void initMorePage() {
		List<View> lv = new ArrayList<View>();
		for (int i = 0; i < 1; ++i)
			lv.add(getMoreGridView(i));
		MorePageAdapter adapter = new MorePageAdapter(lv, moreViewPager);
		moreViewPager.setAdapter(adapter);
		moreViewPager.setCurrentItem(CURRENT_MORE_PAGE);
		CirclePageIndicator indicator = (CirclePageIndicator) ((Activity) context)
				.findViewById(R.id.more_indicator);
		indicator.setViewPager(moreViewPager);
		adapter.notifyDataSetChanged();
		moreLinearLayout.setVisibility(View.GONE);
		indicator.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				CURRENT_MORE_PAGE = arg0;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

	}

	private GridView getMoreGridView(int i) {
		GridView gv = new GridView(context);
		gv.setNumColumns(4);
		gv.setSelector(new ColorDrawable(Color.TRANSPARENT));// 屏蔽GridView默认点击效果
		gv.setBackgroundColor(Color.TRANSPARENT);
		gv.setCacheColorHint(Color.TRANSPARENT);
		gv.setHorizontalSpacing(1);
		gv.setVerticalSpacing(1);
		gv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		gv.setGravity(Gravity.CENTER);
		gv.setAdapter(new MoreAdapter(context, i));
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case CHOOSE_IMAGE: {// 发送图片
					Intent intent = new Intent(context, MultiImageSelectorActivity.class);
					intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
					intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
					intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
					((Activity)context).startActivityForResult(intent, Constants.FLAG_CHOOSE_IMG);
					}
					break;
				case TAKE_PHOTO: {// 拍照
					Intent i = new Intent(
							MediaStore.ACTION_IMAGE_CAPTURE);
					String photoFileName = System.currentTimeMillis() + ".jpg";
					CacheUtil.getInstance().savePicName(photoFileName);
					i.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(new File(FileUtil.getInstance().getImageTempPath() + photoFileName)));
					((Activity)context).startActivityForResult(i, Constants.FLAG_CHOOSE_PHOTO);
					}
					break;

				case ORDER_DEAL:

                    OrderOperationController.getInstance().showOrderOperationDialog();
					break;

				default:
					break;
				}
			}
		});
		return gv;
	}

	public void showMoreViewPager() {
		postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				moreLinearLayout.setVisibility(View.VISIBLE);
			}
		}, 100);
	}

}

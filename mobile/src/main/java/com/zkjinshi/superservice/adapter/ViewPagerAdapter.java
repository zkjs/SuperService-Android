package com.zkjinshi.superservice.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.fragment.MessageFragment;
import com.zkjinshi.superservice.fragment.NoticeFragment;
import com.zkjinshi.superservice.fragment.OrderFragment;

import java.util.ArrayList;

/**
 * 开发者：JimmyZhang
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    public static final int NUM_ITEMS = 2;
   // public static final int NUM_ITEMS = 3;
    public static final int ALL_POS = 0;
    public static final int SHARED_POS = 1;
    //public static final int FAVORITES_POS = 2;

    public String[] tabTitles = {"到店通知","消息通知"};
    //public String[] tabTitles = {"到店通知","消息通知","订单处理"};
    public ArrayList<ViewHolder> myTagList = new ArrayList<ViewHolder>();

    private Context context;

    public ViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        myTagList = new ArrayList<ViewHolder>();

    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case ALL_POS:
                return NoticeFragment.newInstance();
            case SHARED_POS:
                return MessageFragment.newInstance();
           // case FAVORITES_POS:
             //   return new OrderFragment();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case ALL_POS:
                return context.getString(R.string.coming_notice);
            case SHARED_POS:
                return context.getString(R.string.message_notice);
            //case FAVORITES_POS:
              //  return context.getString(R.string.deal_with_order);
            default:
                return "";
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    public View getTabView(int position){
        View view = LayoutInflater.from(context).inflate(R.layout.tab_item, null);
        TextView title = (TextView) view.findViewById(R.id.main_title_tv);
        title.setText(tabTitles[position]);

        TextView num= (TextView) view.findViewById(R.id.main_msg_num_tv);
        num.setText("0");
        num.setVisibility(View.GONE);

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.title = title;
        viewHolder.num = num;
        myTagList.add(viewHolder);

        return view;
    }

    public void setNumText(int position,int num){
        ViewHolder viewHolder = myTagList.get(position);
        if(num > 0){
            viewHolder.num.setVisibility(View.VISIBLE);
            viewHolder.num.setText(num+"");
        }else{
            viewHolder.num.setVisibility(View.GONE);
        }
    }


    public class ViewHolder{
        public TextView  title;
        public TextView  num;
    }
}

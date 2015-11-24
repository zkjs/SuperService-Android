package com.zkjinshi.superservice.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.bean.GoodBean;
import com.zkjinshi.superservice.utils.ProtocolUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 商品列表adapter
 * 开发者：dujiande
 * 日期：2015/9/30
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class GoodAdapter extends BaseAdapter{
    private final static String TAG = GoodAdapter.class.getSimpleName();

    private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局
    private ArrayList<GoodBean> goodList = new ArrayList<GoodBean>();
    private int checkid = 0;

    public GoodAdapter(Context context,ArrayList<GoodBean> goodList, int checkid) {
        this.mInflater = LayoutInflater.from(context);
        this.goodList = goodList;
        this.checkid = checkid;
    }

    public GoodBean getGoodByPosition(int position){
        return goodList.get(position);
    }

    public void setCheckidByPosition(int position) {
        this.checkid = goodList.get(position).getId();
    }

    private boolean ischecked(int id){
        return this.checkid == id ? true : false;
    }

    @Override
    public int getCount() {
        return goodList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.good_item, null);
            holder = new ViewHolder();
            holder.img = (ImageView)convertView.findViewById(R.id.zone_img_iv);
            holder.name = (TextView)convertView.findViewById(R.id.zone_name_tv);
            holder.icon      = (ImageView)convertView.findViewById(R.id.icon);
            convertView.setTag(holder);//绑定ViewHolder对象
        }else{
            holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
        }
        holder.name.setText(goodList.get(position).getRoom() + goodList.get(position).getType());
        ImageLoader.getInstance().displayImage(ProtocolUtil.getImgUrl(goodList.get(position).getImgurl()),holder.img);

        int id = goodList.get(position).getId();
        boolean c = ischecked(id);
        if(c){
            holder.icon.setImageResource(R.mipmap.ic_jia_pre);
        }else{
            holder.icon.setImageResource(R.mipmap.ic_jia_nor);
        }
        return convertView;
    }



    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount();
    }


    /*存放控件*/
    public final class ViewHolder{
        public ImageView img;
        public TextView  name;
        public ImageView icon;
    }


}

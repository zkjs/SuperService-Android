package com.zkjinshi.superservice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.view.CircleImageView;
import com.zkjinshi.superservice.vo.ShopEmployeeVo;

import java.util.ArrayList;

/**
 * 单聊详情页适配器
 * 开发者：JimmyZhang
 * 日期：2015/11/26
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ChatDetailAdapter extends BaseAdapter {

    private DisplayImageOptions options;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<ShopEmployeeVo> employeeList;

    public ChatDetailAdapter(Context context,ArrayList<ShopEmployeeVo> employeeList) {
        this.context = context;
        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_launcher)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        this.inflater = LayoutInflater.from(context);
        this.setEmployeeList(employeeList);
    }

    public void setEmployeeList(ArrayList<ShopEmployeeVo> employeeList){
        if(null == employeeList){
            this.employeeList = new ArrayList<ShopEmployeeVo>();
        }else{
            this.employeeList = employeeList;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (employeeList == null)
            return 0;
        else if (employeeList.size() + 1 > 12)
            return 12;
        else
            return employeeList.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_chat_detail_list, null, false);
            vh.iconIv = (CircleImageView) convertView.findViewById(R.id.more_iv_icon);
            vh.nameTv = (TextView) convertView.findViewById(R.id.more_tv_name);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        if (position == getCount() - 1) {
            vh.iconIv.setImageResource(R.mipmap.ic_jiatouxiang);
            vh.nameTv.setText("添加");
        } else {
            ShopEmployeeVo shopEmployeeVo = employeeList.get(position);
            String userName = shopEmployeeVo.getName();
            String userId = shopEmployeeVo.getEmpid();
            String url = ProtocolUtil.getAvatarUrl(userId);
            ImageLoader.getInstance().displayImage(url, vh.iconIv, options);
            vh.nameTv.setText(userName);
        }

        return convertView;
    }

    static class ViewHolder {
        CircleImageView iconIv;
        TextView nameTv;
    }

}

package com.zkjinshi.superservice.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.vo.ServiceTagVo;

import java.util.ArrayList;

/**
 * 服务标签适配器
 * 开发者：jimmyzhang
 * 日期：16/6/22
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ServiceTagAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<ServiceTagVo> firstTagList;

    public void setFirstTagList(ArrayList<ServiceTagVo> firstTagList) {
        if(null == firstTagList){
            this.firstTagList = new ArrayList<ServiceTagVo>();
        }else {
            this.firstTagList = firstTagList;
        }
        notifyDataSetChanged();
    }

    public ServiceTagAdapter(Context context,ArrayList<ServiceTagVo> firstTagList){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.setFirstTagList(firstTagList);
    }

    @Override
    public int getCount() {
        return firstTagList.size();
    }

    @Override
    public Object getItem(int i) {
        return firstTagList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(null == convertView){
            convertView = inflater.inflate(R.layout.item_service_tag,null);
            viewHolder = new ViewHolder();
            viewHolder.firstTagTv = (TextView)convertView.findViewById(R.id.first_tag_name_tv);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ServiceTagVo serviceTagVo = firstTagList.get(position);
        if(null != serviceTagVo){
            String firstTagNameStr = serviceTagVo.getFirstSrvTagName();
            if(!TextUtils.isEmpty(firstTagNameStr)){
                viewHolder.firstTagTv.setText(firstTagNameStr);
            }
        }
        return convertView;
    }

    class ViewHolder{
        TextView firstTagTv;
    }
}

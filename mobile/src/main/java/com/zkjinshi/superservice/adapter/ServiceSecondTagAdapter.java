package com.zkjinshi.superservice.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.vo.SecondServiceTagVo;

import java.util.ArrayList;

/**
 * 服务标签适配器
 * 开发者：jimmyzhang
 * 日期：16/6/22
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ServiceSecondTagAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<SecondServiceTagVo> secondTagList;

    public void setSecondTagList(ArrayList<SecondServiceTagVo> secondTagList) {
        if(null == secondTagList){
            this.secondTagList = new ArrayList<SecondServiceTagVo>();
        }else {
            this.secondTagList = secondTagList;
        }
        notifyDataSetChanged();
    }

    public ServiceSecondTagAdapter(Context context, ArrayList<SecondServiceTagVo> secondTagList){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.setSecondTagList(secondTagList);
    }

    @Override
    public int getCount() {
        return secondTagList.size();
    }

    @Override
    public Object getItem(int i) {
        return secondTagList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(null == convertView){
            convertView = inflater.inflate(R.layout.item_second_service_tag,null);
            viewHolder = new ViewHolder();
            viewHolder.secondTagTv = (TextView)convertView.findViewById(R.id.second_tag_name_tv);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        SecondServiceTagVo secondServiceTagVo = secondTagList.get(position);
        if(null != secondServiceTagVo){
            String secondTagNameStr = secondServiceTagVo.getSecondSrvTagName();
            if(!TextUtils.isEmpty(secondTagNameStr)){
                viewHolder.secondTagTv.setText(secondTagNameStr);
            }
        }
        return convertView;
    }

    class ViewHolder{
        TextView secondTagTv;
    }
}

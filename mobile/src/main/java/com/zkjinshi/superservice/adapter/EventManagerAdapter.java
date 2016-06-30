package com.zkjinshi.superservice.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.vo.EventVo;
import com.zkjinshi.superservice.vo.ServiceTagVo;

import java.util.ArrayList;

/**
 * 活动管理适配器
 * 开发者：jimmyzhang
 * 日期：16/6/22
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class EventManagerAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<EventVo> eventList;

    public void setEventList(ArrayList<EventVo> eventList) {
        if(null == eventList){
            this.eventList = new ArrayList<EventVo>();
        }else {
            this.eventList = eventList;
        }
        notifyDataSetChanged();
    }

    public EventManagerAdapter(Context context, ArrayList<EventVo> eventList){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.setEventList(eventList);
    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Object getItem(int i) {
        return eventList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(null == convertView){
            convertView = inflater.inflate(R.layout.item_event_manager,null);
            viewHolder = new ViewHolder();
            viewHolder.eventNameTv = (TextView)convertView.findViewById(R.id.event_name_tv);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        EventVo eventVo = eventList.get(position);
        if(null != eventVo){
            String eventName = eventVo.getActname();
            if(!TextUtils.isEmpty(eventName)){
                viewHolder.eventNameTv.setText(eventName);
            }
        }
        return convertView;
    }

    class ViewHolder{
        TextView eventNameTv;
    }
}

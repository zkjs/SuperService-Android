package com.zkjinshi.superservice.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.vo.EventUserVo;

import java.util.ArrayList;

/**
 * 开发者：jimmyzhang
 * 日期：16/7/1
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class GuestAdapter extends BaseAdapter {

    private ArrayList<EventUserVo> eventUserList;
    private Context context;
    private LayoutInflater inflater;

    public GuestAdapter(Context context,ArrayList<EventUserVo> eventUserList){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.setEventUserList(eventUserList);
    }

    public void setEventUserList(ArrayList<EventUserVo> eventUserList) {
        if(null == eventUserList){
            this.eventUserList = new ArrayList<EventUserVo>();
        }else {
            this.eventUserList = eventUserList;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return eventUserList.size();
    }

    @Override
    public Object getItem(int i) {
        return eventUserList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(null == convertView){
            convertView = inflater.inflate(R.layout.item_invite_guest,null);
            viewHolder = new ViewHolder();
            viewHolder.guestNameTv = (TextView) convertView.findViewById(R.id.guest_name_tv);
            viewHolder.guestCountTv = (TextView) convertView.findViewById(R.id.guest_count_tv);
            viewHolder.guestStatusTv = (TextView) convertView.findViewById(R.id.guest_status_tv);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        EventUserVo userVo = eventUserList.get(position);
        String userName = userVo.getUsername();
        if(!TextUtils.isEmpty(userName)){
            viewHolder.guestNameTv.setText(userName);
        }
        int count = userVo.getTakeperson();
        if(count > 0){
            viewHolder.guestCountTv.setText(""+count+"人");
        }else {
            viewHolder.guestCountTv.setText("");
        }
        int statusCode = userVo.getConfirmstatusCode();
        String status = userVo.getConfirmstatus();
        if(!TextUtils.isEmpty(status)){
            viewHolder.guestStatusTv.setText(status);
        }
        if(2 == statusCode){
            viewHolder.guestStatusTv.setTextColor(context.getResources().getColor(R.color.light_gray));
        }else if(0 == statusCode){
            viewHolder.guestStatusTv.setTextColor(context.getResources().getColor(R.color.light_red));
        }else {
            viewHolder.guestStatusTv.setTextColor(context.getResources().getColor(R.color.light_blue));
        }
        return convertView;
    }

    class ViewHolder{
        TextView guestNameTv,guestCountTv,guestStatusTv;
    }
}

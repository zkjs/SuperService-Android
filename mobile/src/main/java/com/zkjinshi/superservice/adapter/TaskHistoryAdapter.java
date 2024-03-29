package com.zkjinshi.superservice.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.ServiceHistoryVo;

import java.util.ArrayList;

/**
 * 任务追踪适配器
 * 开发者：jimmyzhang
 * 日期：16/6/25
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class TaskHistoryAdapter extends BaseAdapter {

    private ArrayList<ServiceHistoryVo> historyList;
    private Context context;
    private LayoutInflater inflater;

    public void setHistoryList(ArrayList<ServiceHistoryVo> historyList) {
        if(null == historyList){
            this.historyList = new ArrayList<ServiceHistoryVo>();
        }else {
            this.historyList = historyList;
        }
    }

    public TaskHistoryAdapter(Context context, ArrayList<ServiceHistoryVo> historyList){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.setHistoryList(historyList);
    }

    @Override
    public int getCount() {
        return historyList.size();
    }

    @Override
    public Object getItem(int i) {
        return historyList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(null == convertView){
            convertView = inflater.inflate(R.layout.item_detail_task,null);
            viewHolder = new ViewHolder();
            viewHolder.updateTimeTv = (TextView)convertView.findViewById(R.id.task_time);
            viewHolder.timeAxisTopTv = (TextView)convertView.findViewById(R.id.task_time_axis_top);
            viewHolder.timeAxisBottomTv = (TextView)convertView.findViewById(R.id.task_time_axis_bottom);
            viewHolder.actionDescTv = (TextView)convertView.findViewById(R.id.task_action_desc_tv);
            viewHolder.userImageSdv = (SimpleDraweeView)convertView.findViewById(R.id.task_user_icon_sdv);
            viewHolder.timeIcon = (ImageView)convertView.findViewById(R.id.task_time_icon);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ServiceHistoryVo serviceHistoryVo = historyList.get(position);
        if(null != serviceHistoryVo){
            String timeStr = serviceHistoryVo.getCreatetime();
            if(!TextUtils.isEmpty(timeStr)){
                viewHolder.updateTimeTv.setText(TimeUtil.getNoticeTime(timeStr));
            }
            String acionDescStr = serviceHistoryVo.getActiondesc();
            if(!TextUtils.isEmpty(acionDescStr)){
                viewHolder.actionDescTv.setText(acionDescStr);
            }
            int statusCode = serviceHistoryVo.getActioncode();
            /**
             未指派(1) 指派(2), 就绪(3), 取消(4), 完成(5), 评价(6)
             */
            if(1 == statusCode){
                viewHolder.timeIcon.setImageResource(R.mipmap.ic_round_blue);
            }else if(5 == statusCode){
                viewHolder.timeIcon.setImageResource(R.mipmap.ic_gou_r);
            }else if(6 == statusCode){
                viewHolder.timeIcon.setImageResource(R.mipmap.ic_pingjia);
            }else {
                viewHolder.timeIcon.setImageResource(R.mipmap.ic_shang_r);
            }
            String userImgStr = serviceHistoryVo.getUserimage();
            if (!TextUtils.isEmpty(userImgStr)) {
                String userImgUrl = ProtocolUtil.getAvatarUrl(context,userImgStr);
                viewHolder.userImageSdv.setImageURI(Uri.parse(userImgUrl));
            }
        }
        if(position == 0){
            viewHolder.timeAxisTopTv.setVisibility(View.INVISIBLE);
            viewHolder.timeAxisBottomTv.setVisibility(View.VISIBLE);
        }else if(position == getCount()-1){
            viewHolder.timeAxisTopTv.setVisibility(View.VISIBLE);
            viewHolder.timeAxisBottomTv.setVisibility(View.INVISIBLE);
        }else{
            viewHolder.timeAxisTopTv.setVisibility(View.VISIBLE);
            viewHolder.timeAxisBottomTv.setVisibility(View.VISIBLE);
        }
        if(getCount() == 1){
            viewHolder.timeAxisTopTv.setVisibility(View.INVISIBLE);
            viewHolder.timeAxisBottomTv.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    static class ViewHolder{
        TextView updateTimeTv,timeAxisTopTv,timeAxisBottomTv,userNameTv,actionDescTv;
        SimpleDraweeView userImageSdv;
        ImageView timeIcon;
    }
}

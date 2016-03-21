package com.zkjinshi.superservice.ext.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.ext.util.MathUtil;
import com.zkjinshi.superservice.ext.vo.AmountStatusVo;
import com.zkjinshi.superservice.utils.ProtocolUtil;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * 收款记录适配器
 * 开发者：JimmyZhang
 * 日期：2016/3/8
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class AmountAdapter extends BaseAdapter {

    private ArrayList<AmountStatusVo> amountStatusList;
    private Context context;
    private LayoutInflater inflater;


    public AmountAdapter(Context context,ArrayList<AmountStatusVo> amountStatusList){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.setAmountStatusList(amountStatusList);
    }

    public void setAmountStatusList(ArrayList<AmountStatusVo> amountStatusList) {
        if(null == amountStatusList){
            this.amountStatusList = new ArrayList<AmountStatusVo>();
        }else {
            this.amountStatusList = amountStatusList;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return amountStatusList.size();
    }

    @Override
    public Object getItem(int i) {
        return amountStatusList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(null == convertView){
            convertView = inflater.inflate(R.layout.item_record_amount,null);
            viewHolder = new ViewHolder();
            viewHolder.userPhotoDv = (SimpleDraweeView)convertView.findViewById(R.id.iv_user_photo);
            viewHolder.userNameTv = (TextView)convertView.findViewById(R.id.tv_user_name);
            viewHolder.amountPriceTv = (TextView)convertView.findViewById(R.id.tv_amount_price);
            viewHolder.createTimeTv = (TextView)convertView.findViewById(R.id.tv_create_time);
            viewHolder.amountStatusTv = (TextView)convertView.findViewById(R.id.tv_amount_status);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        AmountStatusVo amountStatusVo = amountStatusList.get(position);
        String userIcon = amountStatusVo.getUserimage();
        if(!TextUtils.isEmpty(userIcon)){
            String path = ConfigUtil.getInst().getImgDomain()+userIcon;
            if(!TextUtils.isEmpty(path)){
                viewHolder.userPhotoDv.setImageURI(Uri.parse(path));
            }
        }
        String userNameStr = amountStatusVo.getUsername();
        if(!TextUtils.isEmpty(userNameStr)){
            viewHolder.userNameTv.setText(userNameStr);
        }
        double totalPrice = amountStatusVo.getAmount();
        String amountPriceStr = "¥"+ MathUtil.convertStr(totalPrice);
        if(!TextUtils.isEmpty(amountPriceStr)){
            viewHolder.amountPriceTv.setText(amountPriceStr);
        }
        String createTimeStr = amountStatusVo.getCreatetime();
        if(!TextUtils.isEmpty(createTimeStr)){
            viewHolder.createTimeTv.setText(createTimeStr);
        }
        String amountStatusStr = amountStatusVo.getStatusdesc();
        if(!TextUtils.isEmpty(amountStatusStr)){
            viewHolder.amountStatusTv.setText(amountStatusStr);
        }
        int amountStatus = amountStatusVo.getStatus(); //0-待确认, 1-已拒绝, 2-已确认
        if(0 == amountStatus){
            viewHolder.amountStatusTv.setTextColor(context.getResources().getColor(R.color.red));
        }else if(1 == amountStatus){
            viewHolder.amountStatusTv.setTextColor(context.getResources().getColor(R.color.dark_yellow));
        }else if(2 == amountStatus){
            viewHolder.amountStatusTv.setTextColor(context.getResources().getColor(R.color.black));
        }

        return convertView;
    }

    class ViewHolder{
        SimpleDraweeView userPhotoDv;
        TextView userNameTv,createTimeTv,amountStatusTv,amountPriceTv;
    }
}

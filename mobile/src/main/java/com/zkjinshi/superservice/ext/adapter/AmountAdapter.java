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

    private static final int VIEW_TYPE_COUNT = 3; // 总数
    private static final int TYPE_WAIT_ITEM = 0;//待确认
    private static final int TYPE_FAIL_ITEM = 1;//已拒绝
    private static final int TYPE_SUCC_ITEM = 2;//已支付


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

        SuccViewHolder succViewHolder = null;
        FailViewHolder failViewHolder = null;
        WaitViewHolder waitViewHolder = null;
        int itemType = getItemViewType(position);
        if(null == convertView){
            switch (itemType){
                case TYPE_SUCC_ITEM:
                    convertView = inflater.inflate(R.layout.item_record_amount_succ,parent, false);
                    succViewHolder = new SuccViewHolder();
                    setViewHolder(succViewHolder,convertView);
                    convertView.setTag(succViewHolder);
                    break;
                case TYPE_FAIL_ITEM:
                    convertView = inflater.inflate(R.layout.item_record_amount_fail,parent, false);
                    failViewHolder = new FailViewHolder();
                    setViewHolder(failViewHolder,convertView);
                    convertView.setTag(failViewHolder);
                    break;
                case TYPE_WAIT_ITEM:
                    convertView = inflater.inflate(R.layout.item_record_amount_wait,parent,false);
                    waitViewHolder = new WaitViewHolder();
                    setViewHolder(waitViewHolder,convertView);
                    convertView.setTag(waitViewHolder);
                    break;
            }

        }else {
            switch (itemType){
                case TYPE_SUCC_ITEM:
                    succViewHolder = (SuccViewHolder) convertView.getTag();
                    break;
                case TYPE_FAIL_ITEM:
                    failViewHolder = (FailViewHolder)convertView.getTag();
                    break;
                case TYPE_WAIT_ITEM:
                    waitViewHolder = (WaitViewHolder)convertView.getTag();
                    break;
            }
        }
        AmountStatusVo amountStatusVo = amountStatusList.get(position);
        switch (itemType){
            case TYPE_SUCC_ITEM:
                setViewValues(succViewHolder,amountStatusVo);
                break;
            case TYPE_FAIL_ITEM:
                setViewValues(failViewHolder,amountStatusVo);
                break;
            case TYPE_WAIT_ITEM:
                setViewValues(waitViewHolder,amountStatusVo);
                break;
        }

        return convertView;
    }

    private void setViewHolder(ViewHolder viewHolder, View convertView) {
        viewHolder.userPhotoDv = (SimpleDraweeView)convertView.findViewById(R.id.iv_user_photo);
        viewHolder.userNameTv = (TextView)convertView.findViewById(R.id.tv_user_name);
        viewHolder.amountPriceTv = (TextView)convertView.findViewById(R.id.tv_amount_price);
        viewHolder.createTimeTv = (TextView)convertView.findViewById(R.id.tv_create_time);
        viewHolder.amountStatusTv = (TextView)convertView.findViewById(R.id.tv_amount_status);
    }

    private void setViewValues(ViewHolder viewHolder,AmountStatusVo amountStatusVo){
        String userIcon = amountStatusVo.getUserimage();
        String userNameStr = amountStatusVo.getUsername();
        double totalPrice = amountStatusVo.getAmount();
        String amountPriceStr = "¥"+ MathUtil.convertStr(totalPrice);
        String createTimeStr = amountStatusVo.getCreatetime();
        String amountStatusStr = amountStatusVo.getStatusdesc();
        if(!TextUtils.isEmpty(userIcon)){
            String path = ProtocolUtil.getAvatarUrl(context,userIcon);
            if(!TextUtils.isEmpty(path)){
                viewHolder.userPhotoDv.setImageURI(Uri.parse(path));
            }
        }
        if(!TextUtils.isEmpty(userNameStr)){
            viewHolder.userNameTv.setText(userNameStr);
        }
        if(!TextUtils.isEmpty(amountPriceStr)){
            viewHolder.amountPriceTv.setText(amountPriceStr);
        }
        if(!TextUtils.isEmpty(createTimeStr)){
            viewHolder.createTimeTv.setText(createTimeStr);
        }
        if(!TextUtils.isEmpty(amountStatusStr)){
            viewHolder.amountStatusTv.setText(amountStatusStr);
        }
    }

    @Override
    public int getItemViewType(int position) {
        AmountStatusVo amountStatusVo = amountStatusList.get(position);
        int status = 0;
        if(null != amountStatusVo){
            status = amountStatusVo.getStatus();
        }
        return status;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    class WaitViewHolder extends ViewHolder{}

    class FailViewHolder extends ViewHolder{}

    class SuccViewHolder extends ViewHolder{}

    class ViewHolder{
        SimpleDraweeView userPhotoDv;
        TextView userNameTv,createTimeTv,amountStatusTv,amountPriceTv;
    }
}

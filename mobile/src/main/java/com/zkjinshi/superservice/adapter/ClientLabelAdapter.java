package com.zkjinshi.superservice.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.view.RoundProgressBar;
import com.zkjinshi.superservice.vo.ItemTagVo;

import java.util.ArrayList;
import java.util.Map;

/**
 * 开发者：JimmyZhang
 * 日期：2016/4/27
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientLabelAdapter extends BaseAdapter {

    private ArrayList<ItemTagVo> tagList;
    private Context context;
    private LayoutInflater inflater;
    private Map<Integer, Boolean> mSelectMap;

    public ClientLabelAdapter(Context context, ArrayList<ItemTagVo> tagList){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.setTagList(tagList);
    }

    public void setTagList(ArrayList<ItemTagVo> tagList) {
        if(null == tagList){
            this.tagList = new ArrayList<>();
        }else {
            this.tagList = tagList;
        }
        notifyDataSetChanged();
    }

    public void setSelectMap(Map<Integer, Boolean> selectMap) {
        mSelectMap = selectMap;
    }

    @Override
    public int getCount() {
        return tagList.size();
    }

    @Override
    public Object getItem(int position) {
        return tagList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_client_label,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.roundProgressBar = (RoundProgressBar)convertView.findViewById(R.id.client_label_progress);
            viewHolder.roundLabelIv = (ImageView)convertView.findViewById(R.id.client_label_iv);
            viewHolder.contentLayout = (RelativeLayout)convertView.findViewById(R.id.content_layout);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ItemTagVo itemTagVo = tagList.get(position);
        String tagName =  itemTagVo.getTagname();
        int value = itemTagVo.getCount();
        viewHolder.roundProgressBar.setAnimDuration(2000);
        viewHolder.roundProgressBar.setInterpolator(new AccelerateDecelerateInterpolator());
        viewHolder.roundProgressBar.setSweepValue(value);
        if(!TextUtils.isEmpty(tagName)){
            viewHolder.roundProgressBar.setValueText(tagName);
        }
        viewHolder.roundProgressBar.anim();
        int tagId = itemTagVo.getTagid();
        if (mSelectMap != null
                && mSelectMap.containsKey(tagId)
                && mSelectMap.get(tagId)) {
            viewHolder.roundLabelIv.setVisibility(View.VISIBLE);
        } else {
            viewHolder.roundLabelIv.setVisibility(View.GONE);
        }
        RelativeLayout.LayoutParams contentLayoutParams = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        int marginDp = DisplayUtil.dip2px(context, 20);
        if(position == 0){
            contentLayoutParams.setMargins(0, marginDp,0, 0);
        }else if(position == getCount()-1){
            contentLayoutParams.setMargins(0, 0, 0, 0);
        }else{
            contentLayoutParams.setMargins(0, 0, 0, marginDp);
        }
       // viewHolder.contentLayout.setLayoutParams(contentLayoutParams);
        return convertView;
    }

    class ViewHolder{
        RoundProgressBar roundProgressBar;
        ImageView roundLabelIv;
        RelativeLayout contentLayout;
    }

}

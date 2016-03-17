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
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.ext.vo.NearbyUserVo;
import com.zkjinshi.superservice.utils.ProtocolUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * 开发者：JimmyZhang
 * 日期：2016/3/7
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class NearbyAdapter extends BaseAdapter {

    private ArrayList<NearbyUserVo> nearbyUserList;
    private Context context;
    private LayoutInflater inflater;

    public void setNearbyUserList(ArrayList<NearbyUserVo> nearbyUserList) {
        if(null == nearbyUserList){
            this.nearbyUserList = new ArrayList<NearbyUserVo>();
        }else {
            this.nearbyUserList = nearbyUserList;
        }
        notifyDataSetChanged();
    }

    public NearbyAdapter(Context context,ArrayList<NearbyUserVo> nearbyUserList){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.setNearbyUserList(nearbyUserList);
    }

    @Override
    public int getCount() {
        return nearbyUserList.size();
    }

    @Override
    public Object getItem(int i) {
        return nearbyUserList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(null == convertView){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_nearyby_user,null);
            viewHolder.userNameTv = (TextView)convertView.findViewById(R.id.tv_user_name);
            viewHolder.userPhotoIv = (SimpleDraweeView)convertView.findViewById(R.id.iv_user_photo);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        NearbyUserVo nearbyUserVo = nearbyUserList.get(position);
        String userId = nearbyUserVo.getUserid();
        if(!TextUtils.isEmpty(userId)){
            String path = ProtocolUtil.getAvatarUrl(userId);
            if(!TextUtils.isEmpty(path)){
                viewHolder.userPhotoIv.setImageURI(Uri.parse(path));
            }
        }
        String userName = nearbyUserVo.getUsername();
        if(!TextUtils.isEmpty(userName)){
            viewHolder.userNameTv.setText(userName);
        }
        return convertView;
    }

    class ViewHolder{
        SimpleDraweeView userPhotoIv;
        TextView userNameTv;
    }

}

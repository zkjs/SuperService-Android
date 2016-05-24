package com.zkjinshi.superservice.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.WhiteUserVo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 开发者：JimmyZhang
 * 日期：2016/5/24
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */

public class VipUserAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<WhiteUserVo> vipUserList;
    private HashMap<String,Boolean> selectMap;

    public void setVipUserList(ArrayList<WhiteUserVo> vipUserList) {
        if(null == vipUserList){
            this.vipUserList = new ArrayList<WhiteUserVo>();
        }else {
            this.vipUserList = vipUserList;
        }
        notifyDataSetChanged();
    }

    public HashMap<String, Boolean> getSelectMap() {
        return selectMap;
    }

    public void setSelectMap(HashMap<String, Boolean> selectMap) {
        this.selectMap = selectMap;
        notifyDataSetChanged();
    }

    public VipUserAdapter(Context context, ArrayList<WhiteUserVo> vipUserList){
        this.setVipUserList(vipUserList);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return vipUserList.size();
    }

    @Override
    public Object getItem(int position) {
        return vipUserList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(null == convertView){
            convertView = inflater.inflate(R.layout.item_vip_user,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.userPhotoDv = (SimpleDraweeView)convertView.findViewById(R.id.svip_user_photo_sv);
            viewHolder.userNameTv = (TextView)convertView.findViewById(R.id.svip_user_name_tv);
            viewHolder.telPhoneTv = (TextView)convertView.findViewById(R.id.tel_phone_tv);
            viewHolder.loginStatusTv = (TextView)convertView.findViewById(R.id.login_status_tv);
            viewHolder.remarkTv = (TextView)convertView.findViewById(R.id.remark_tv);
            viewHolder.visitTv = (TextView)convertView.findViewById(R.id.visit_time_tv);
            viewHolder.remarkLayout = (LinearLayout)convertView.findViewById(R.id.remark_layout);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        WhiteUserVo userVo = vipUserList.get(position);
        String userImage = userVo.getUserimage();
        int width = (int)context.getResources().getDimension(R.dimen.user_photo_width);
        String imageUrl = ProtocolUtil.getImageUrlByWidth(context,userImage,width);
        if(!TextUtils.isEmpty(imageUrl)){
            viewHolder.userPhotoDv.setImageURI(Uri.parse(imageUrl));
        }
        String userName = userVo.getUsername();
        if(!TextUtils.isEmpty(userName)){
            viewHolder.userNameTv.setText(userName);
        }
        String telPhone = userVo.getPhone();
        if(!TextUtils.isEmpty(telPhone)){
            viewHolder.telPhoneTv.setText(telPhone);
        }
        int loginStatus = userVo.getLoginstatus();
        if(1 == loginStatus){
            viewHolder.loginStatusTv.setText("已登陆");
            viewHolder.loginStatusTv.setTextColor(context.getResources().getColor(R.color.light_blue));
        }else {
            viewHolder.loginStatusTv.setText("未登陆");
            viewHolder.loginStatusTv.setTextColor(context.getResources().getColor(R.color.light_red));
        }
        String remark = userVo.getRmk();
        if(!TextUtils.isEmpty(remark)){
            viewHolder.remarkTv.setText(remark);
        }
        String lastvisit = userVo.getLastvisit();
        if(!TextUtils.isEmpty(lastvisit)){
            viewHolder.visitTv.setText(lastvisit);
        }
        String userId = userVo.getUserid();
        if (selectMap != null
                && selectMap.containsKey(userId)
                && selectMap.get(userId)) {
            viewHolder.remarkLayout.setVisibility(View.VISIBLE);
        } else {
            viewHolder.remarkLayout.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder{
        SimpleDraweeView userPhotoDv;
        TextView userNameTv,telPhoneTv,loginStatusTv,remarkTv,visitTv;
        LinearLayout remarkLayout;
    }
}

package com.zkjinshi.superservice.pad.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zkjinshi.superservice.pad.R;
import com.zkjinshi.superservice.pad.utils.ProtocolUtil;
import com.zkjinshi.superservice.pad.vo.MemberVo;

import java.util.ArrayList;
import java.util.Map;

/**
 * 开发者：jimmyzhang
 * 日期：16/7/1
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ContactAdapter extends BaseAdapter {

    private ArrayList<MemberVo> memberList;
    private Context context;
    private LayoutInflater inflater;
    private Map<String, Boolean> selectMap;

    public ContactAdapter(ArrayList<MemberVo> memberList,Context context){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.setMemberList(memberList);
    }

    public void setSelectMap(Map<String, Boolean> selectMap) {
        this.selectMap = selectMap;
        notifyDataSetChanged();
    }

    public void setMemberList(ArrayList<MemberVo> memberList) {
        if(null == memberList){
            this.memberList = new ArrayList<MemberVo>();
        }else {
            this.memberList = memberList;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return memberList.size();
    }

    @Override
    public Object getItem(int i) {
        return memberList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(null == convertView){
            convertView = inflater.inflate(R.layout.item_contact,null);
            viewHolder = new ViewHolder();
            viewHolder.sortLetterLayout = (LinearLayout) convertView.findViewById(R.id.sort_letter_layout);
            viewHolder.sortLetterTv = (TextView)convertView.findViewById(R.id.sort_letter_tv);
            viewHolder.chooseIv = (ImageView)convertView.findViewById(R.id.contact_choose_cb);
            viewHolder.contactPhotoSdv = (SimpleDraweeView)convertView.findViewById(R.id.contact_photo_sdv);
            viewHolder.contactTv = (TextView)convertView.findViewById(R.id.contact_name_tv);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MemberVo memberVo = memberList.get(position);
        String memberId = memberVo.getUserid();
        if (selectMap != null && selectMap.containsKey(memberId) && selectMap.get(memberId)) {
            viewHolder.chooseIv.setImageResource(R.mipmap.list_checkbox_selected);
        } else {
            viewHolder.chooseIv.setImageResource(R.mipmap.list_checkbox);
        }
        int section = getSectionForPosition(position);
        if (position == getPositionForSection(section)) {
            viewHolder.sortLetterLayout.setVisibility(View.VISIBLE);
            viewHolder.sortLetterTv.setText(memberVo.getSortLetter());
        } else {
            viewHolder.sortLetterLayout.setVisibility(View.GONE);
        }
        String userImage = memberVo.getUserimage();
        int width = (int)context.getResources().getDimension(R.dimen.contact_logo_height);
        if(!TextUtils.isEmpty(userImage)){
            String path = ProtocolUtil.getImageUrlByWidth(context,userImage,width);
            if(!TextUtils.isEmpty(path)){
                viewHolder.contactPhotoSdv.setImageURI(Uri.parse(path));
            }
        }
        String userName = memberVo.getUsername();
        if(!TextUtils.isEmpty(userName)){
            viewHolder.contactTv.setText(userName);
        }
        return convertView;
    }

    static class ViewHolder{
        LinearLayout sortLetterLayout;
        TextView sortLetterTv;
        ImageView chooseIv;
        SimpleDraweeView contactPhotoSdv;
        TextView contactTv;
    }

    public int getSectionForPosition(int position) {
        return memberList.get(position).getSortLetter().charAt(0);
    }

    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = memberList.get(i).getSortLetter();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }
}

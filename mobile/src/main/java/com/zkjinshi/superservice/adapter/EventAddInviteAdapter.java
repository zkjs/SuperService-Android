package com.zkjinshi.superservice.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.vo.EventVo;
import com.zkjinshi.superservice.vo.GuestVo;
import com.zkjinshi.superservice.vo.MemberVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 活动管理适配器
 * 开发者：jimmyzhang
 * 日期：16/6/22
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class EventAddInviteAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<GuestVo> guestList;
    private Map<String, Boolean> selectMap;
    private HashMap<String,ArrayList<MemberVo>> chooseMemberMap;

    public void setGuestList(ArrayList<GuestVo> guestList) {
        if(null == guestList){
            this.guestList = new ArrayList<GuestVo>();
        }else {
            this.guestList = guestList;
        }
        notifyDataSetChanged();
    }

    public EventAddInviteAdapter(Context context, ArrayList<GuestVo> guestList){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.setGuestList(guestList);
    }

    public void setSelectMap(Map<String, Boolean> selectMap) {
        this.selectMap = selectMap;
        notifyDataSetChanged();
    }

    public void setChooseMemberMap(HashMap<String, ArrayList<MemberVo>> chooseMemberMap) {
        this.chooseMemberMap = chooseMemberMap;
        notifyDataSetChanged();
    }

    public Map<String, Boolean> getSelectMap() {
        return selectMap;
    }

    public HashMap<String, ArrayList<MemberVo>> getChooseMemberMap() {
        return chooseMemberMap;
    }

    @Override
    public int getCount() {
        return guestList.size();
    }

    @Override
    public Object getItem(int i) {
        return guestList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(null == convertView){
            convertView = inflater.inflate(R.layout.item_event_add_invite,null);
            viewHolder = new ViewHolder();
            viewHolder.roleNameTv = (TextView)convertView.findViewById(R.id.role_name_tv);
            viewHolder.chooseIv = (ImageView)convertView.findViewById(R.id.role_name_choose_cb);
            viewHolder.roleStatTv = (TextView)convertView.findViewById(R.id.role_stat_tv);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final GuestVo guestVo = guestList.get(position);
        String roleId = guestVo.getRoleid();
        if (selectMap != null && selectMap.containsKey(roleId) && selectMap.get(roleId)) {
            viewHolder.chooseIv.setImageResource(R.mipmap.list_checkbox_selected);
        } else {
            viewHolder.chooseIv.setImageResource(R.mipmap.list_checkbox);
        }
        String roleNameStr = guestVo.getRolename();
        if(!TextUtils.isEmpty(roleNameStr)){
            viewHolder.roleNameTv.setText(roleNameStr);
        }
        int count = guestVo.getCount();
        final ArrayList<MemberVo> memberList = guestVo.getMember();
        final int totalCount = memberList == null ? 0 : memberList.size();
        viewHolder.roleStatTv.setText("（"+count+"/"+totalCount+"）");
        viewHolder.chooseIv.setTag(roleId);
        viewHolder.chooseIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String roleId = view.getTag().toString();
                if (selectMap != null
                        && selectMap.containsKey(roleId)
                        && selectMap.get(roleId)) {
                    selectMap.put(roleId, false);
                    guestVo.setCount(0);
                    chooseMemberMap.put(roleId,new ArrayList<MemberVo>());
                } else {
                    selectMap.put(roleId, true);
                    guestVo.setCount(totalCount);
                    chooseMemberMap.put(roleId,memberList);
                }
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    class ViewHolder{
        ImageView chooseIv;
        TextView roleNameTv,roleStatTv;
    }
}

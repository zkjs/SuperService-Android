package com.zkjinshi.superservice.pad.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.superservice.pad.R;
import com.zkjinshi.superservice.pad.utils.ProtocolUtil;
import com.zkjinshi.superservice.pad.view.CircleImageView;
import com.zkjinshi.superservice.pad.vo.EContactVo;

import java.util.ArrayList;

/**
 * 单聊详情页适配器
 * 开发者：JimmyZhang
 * 日期：2015/11/26
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ChatDetailAdapter extends BaseAdapter {

    private DisplayImageOptions options;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<EContactVo> contactList;

    public ChatDetailAdapter(Context context,ArrayList<EContactVo> contactList) {
        this.context = context;
        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_launcher)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        this.inflater = LayoutInflater.from(context);
        this.setContactList(contactList);
    }

    public void setContactList(ArrayList<EContactVo> contactList){
        if(null == contactList){
            this.contactList = new ArrayList<EContactVo>();
        }else{
            this.contactList = contactList;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (contactList == null)
            return 0;
        else if (contactList.size() + 1 > 12)
            return 12;
        else
            return contactList.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_chat_detail_list, null, false);
            vh.iconIv = (CircleImageView) convertView.findViewById(R.id.more_iv_icon);
            vh.nameTv = (TextView) convertView.findViewById(R.id.more_tv_name);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        if (position == getCount() - 1) {
            vh.iconIv.setImageResource(R.mipmap.ic_tianjia_blue_nor);
            vh.nameTv.setText("添加");
        } else {
            EContactVo shopEmployeeVo = contactList.get(position);
            String userName = shopEmployeeVo.getContactName();
            String userId = shopEmployeeVo.getContactId();
            String url = ProtocolUtil.getAvatarUrl(userId);
            ImageLoader.getInstance().displayImage(url, vh.iconIv, options);
            vh.nameTv.setText(userName);
        }

        return convertView;
    }

    static class ViewHolder {
        CircleImageView iconIv;
        TextView nameTv;
    }

}

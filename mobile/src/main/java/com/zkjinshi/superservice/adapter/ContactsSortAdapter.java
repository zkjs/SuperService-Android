package com.zkjinshi.superservice.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.set.ClientDetailActivity;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.sqlite.ClientDBUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.utils.RandomDrawbleUtil;
import com.zkjinshi.superservice.vo.ClientContactVo;
import com.zkjinshi.superservice.vo.ContactType;
import com.zkjinshi.superservice.vo.ContactVo;
import com.zkjinshi.superservice.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * 联系人适配器
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ContactsSortAdapter extends ServiceBaseAdapter<ClientContactVo> implements SectionIndexer {



    public ContactsSortAdapter(Activity activity, List<ClientContactVo> datas) {
        super(activity, datas);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(null == convertView){
            holder      = new ViewHolder();
            convertView = View.inflate(mActivity, R.layout.item_my_cilent, null);
            holder.tvLetter         = (TextView) convertView.findViewById(R.id.catalog);
            holder.civContactAvatar = (SimpleDraweeView) convertView.findViewById(R.id.civ_contact_avatar);
            holder.tvContactAvatar  = (TextView) convertView.findViewById(R.id.tv_contact_avatar);
            holder.tvContactName    = (TextView) convertView.findViewById(R.id.tv_contact_name);
            holder.ivStar           = (ImageView) convertView.findViewById(R.id.iv_star);
            holder.tvContactDes     = (TextView) convertView.findViewById(R.id.tv_contact_des);
            holder.rlContactOnStatus = (RelativeLayout) convertView.findViewById(R.id.rl_contact_on_status);
            holder.tvContactOnLine   = (TextView) convertView.findViewById(R.id.tv_contact_on_line);
            holder.tvContactOnShop   = (TextView) convertView.findViewById(R.id.tv_contact_on_shop);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //根据position获取分类的首字母的Char ascii值
        final ClientContactVo contact = mDatas.get(position);
        int section = getSectionForPosition(position);

        //是否显示首字母
        if (position == getPositionForSection(section)) {
            holder.tvLetter.setVisibility(View.VISIBLE);
            String sortLetter = contact.getFirstLetter();
            if("?".equals(sortLetter)){
                holder.tvLetter.setText(mActivity.getString(R.string.latest_contact));
            }else {
                holder.tvLetter.setText(sortLetter);
            }
        } else {
            holder.tvLetter.setVisibility(View.GONE);
        }

        String clientID = contact.getUserid();

        //根据url显示图片
        String avatarUrl = ProtocolUtil.getHostImgUrl(contact.getUserimage());
        holder.civContactAvatar.setImageURI( Uri.parse(avatarUrl));
        holder.tvContactAvatar.setBackgroundResource(RandomDrawbleUtil.getRandomDrawable());
        holder.tvContactAvatar.setVisibility(View.VISIBLE);

        //显示客户名称
        String clientName = contact.getUsername();
        if(!TextUtils.isEmpty(clientName)){
            holder.tvContactAvatar.setText(clientName.substring(0, 1));
            //去除问号
            if("?".equals(clientName.trim().substring(0, 1))){
                holder.tvContactName.setText(clientName.substring(1));
            } else {
                holder.tvContactName.setText(clientName);
            }
        }

        //显示客户名称
        String phone = contact.getPhone();
        if(!TextUtils.isEmpty(phone)){
            holder.tvContactDes.setText(phone);
        } else {
            holder.tvContactDes.setText("无联系电话");
        }

//        ContactType contactType = contact.getContactType();
//        if(contactType == ContactType.NORMAL){
//            holder.ivStar.setVisibility(View.VISIBLE);
//            holder.ivStar.setBackgroundResource(R.mipmap.ic_star_shouye_pre);
//        }else {
//            holder.ivStar.setVisibility(View.GONE);
//        }
        holder.tvContactOnLine.setVisibility(View.INVISIBLE);
        holder.civContactAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userID  = contact.getUserid();
                if(!TextUtils.isEmpty(userID)) {
                    Intent clientDetail = new Intent(mActivity, ClientDetailActivity.class);
                    clientDetail.putExtra("user_id", userID);
                    mActivity.startActivity(clientDetail);
                } else {
                    DialogUtil.getInstance().showCustomToast(mActivity, "电话号码为空！", Gravity.CENTER);
                }

//                if (contact.getContactType().getValue() == ContactType.NORMAL.getValue()) {
//                    String phoneNumber = contact.getNumber();
//                    Intent clientDetail = new Intent(mActivity, ClientDetailActivity.class);
//                    clientDetail.putExtra("phone_number", phoneNumber);
//                    mActivity.startActivity(clientDetail);
//                } else {
//                    DialogUtil.getInstance().showCustomToast(mActivity, "当前客户为本地联系人，无详细信息。", Gravity.CENTER);
//                }

            }
        });
        return convertView;
    }

    public static class ViewHolder{
        TextView         tvLetter;
        SimpleDraweeView civContactAvatar;
        TextView         tvContactAvatar;
        TextView         tvContactName;
        ImageView        ivStar;
        TextView         tvContactDes;
        RelativeLayout   rlContactOnStatus;
        TextView         tvContactOnLine;
        TextView         tvContactOnShop;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        String firstLetter = mDatas.get(position).getFirstLetter();
        if(!TextUtils.isEmpty(firstLetter)){
           return firstLetter.charAt(0);
        }
        return '#';
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String firstLetter = mDatas.get(i).getFirstLetter();
            if(!TextUtils.isEmpty(firstLetter)){
                int firstChar = firstLetter.charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }
        }
        return -1;
    }

}

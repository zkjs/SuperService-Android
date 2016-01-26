package com.zkjinshi.superservice.adapter;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.bean.ZoneBean;
import com.zkjinshi.superservice.utils.RandomDrawbleUtil;
import com.zkjinshi.superservice.view.CircleImageView;
import com.zkjinshi.superservice.vo.ContactLocalVo;
import com.zkjinshi.superservice.vo.ShopEmployeeVo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 区域列表adapter
 * 开发者：杜健德
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ContactsAdapter extends BaseAdapter{

    private final static String TAG = ContactsAdapter.class.getSimpleName();

    private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局
    private ArrayList<ContactLocalVo> contactLocalList;
    private ContentResolver resolver;

    public ContactsAdapter(Context context,ArrayList<ContactLocalVo> dataList) {
        this.mInflater = LayoutInflater.from(context);
        this.contactLocalList = dataList;
        resolver = context.getContentResolver();
    }

    public ArrayList<ContactLocalVo> getContactLocalList() {
        return contactLocalList;
    }

    public void setContactLocalList(ArrayList<ContactLocalVo> contactLocalList) {
        this.contactLocalList = contactLocalList;
    }

    public void refreshData(ArrayList<ContactLocalVo> dataList){
        this.contactLocalList = dataList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return contactLocalList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_employee_contact, null);
            holder = new ViewHolder();
            holder.avatar = (CircleImageView)convertView.findViewById(R.id.avatar_civ);
            holder.avatarText = (TextView)convertView.findViewById(R.id.avatar_tv);
            holder.name = (TextView)convertView.findViewById(R.id.tv_name);
            holder.check      = (ImageView)convertView.findViewById(R.id.iv_check);
            holder.phone = (TextView)convertView.findViewById(R.id.tv_phone);
            convertView.setTag(holder);//绑定ViewHolder对象
        }else{
            holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
        }

        ContactLocalVo contactLocalVo = contactLocalList.get(position);
        Bitmap contactPhoto = null;
        if(contactLocalVo.getPhotoid() > 0){
            holder.avatarText.setVisibility(View.GONE);
            Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactLocalVo.getContactid());
            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
            contactPhoto = BitmapFactory.decodeStream(input);
            holder.avatar.setImageBitmap(contactPhoto);
        }else{
            holder.avatarText.setVisibility(View.VISIBLE);
            String nameIndex = contactLocalVo.getContactName().substring(0,1);
            if(!nameIndex.equals( holder.avatarText.getText())){
                holder.avatarText.setText(nameIndex);
                holder.avatarText.setBackgroundResource(RandomDrawbleUtil.getRandomDrawable());
            }

        }

        holder.name.setText(contactLocalVo.getContactName());
        holder.phone.setText(contactLocalVo.getPhoneNumber());
        if(contactLocalVo.isHasAdd()){
            holder.check.setImageResource(R.mipmap.ic_jia_pre);
        }else{
            holder.check.setImageResource(R.mipmap.ic_jia_nor);
        }
        return convertView;
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount();
    }

    /*存放控件*/
    public final class ViewHolder{
        public CircleImageView avatar;
        public TextView avatarText;
        public TextView  name;
        public TextView  phone;
        public ImageView check;
    }

}

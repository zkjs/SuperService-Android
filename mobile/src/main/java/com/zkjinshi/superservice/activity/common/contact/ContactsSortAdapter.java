package com.zkjinshi.superservice.activity.common.contact;

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
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.view.CircleImageView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 联系人适配器
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ContactsSortAdapter extends BaseAdapter implements SectionIndexer {

    private List<SortModel> mList;
    private Context         mContext;

    public ContactsSortAdapter(Context mContext, List<SortModel> list) {
        this.mContext = mContext;
        if (list == null) {
            this.mList = new ArrayList<SortModel>();
        } else {
            this.mList = list;
        }
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     * @param list
     */
    public void updateListView(List<SortModel> list) {
        if (list == null) {
            this.mList = new ArrayList<SortModel>();
        } else {
            this.mList = list;
        }
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.mList.size();
    }

    public Object getItem(int position) {
        return mList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        final SortModel mContent = mList.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_contact, null);
            viewHolder.tvLetter        = (TextView) view.findViewById(R.id.catalog);
            viewHolder.tvContactAvatar = (CircleImageView) view.findViewById(R.id.civ_contact_avatar);
            viewHolder.tvContactName   = (TextView) view.findViewById(R.id.tv_contact_name);
            viewHolder.tvContactPhone  = (TextView) view.findViewById(R.id.tv_contact_phone);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(mContent.sortLetters);
        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
        }

        long contactID = this.mList.get(position).getContactID();

        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactID);
        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(
                                                mContext.getContentResolver(), uri);
        Bitmap contactBitmap = BitmapFactory.decodeStream(input);
        if(null != contactBitmap){
            viewHolder.tvContactAvatar.setImageBitmap(contactBitmap);
        } else {
            viewHolder.tvContactAvatar.setImageBitmap(BitmapFactory.decodeResource(
                                                      mContext.getResources(),
                                                      R.mipmap.ic_main_user_default_photo_nor));
        }
        viewHolder.tvContactName.setText(this.mList.get(position).getName());
        viewHolder.tvContactPhone.setText(this.mList.get(position).getNumber());
        return view;
    }

    public static class ViewHolder {
        public TextView         tvLetter;
        public CircleImageView  tvContactAvatar;
        public TextView         tvContactName;
        public TextView         tvContactPhone;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return mList.get(position).sortLetters.charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = mList.get(i).sortLetters;
            char firstChar = sortStr.toUpperCase(Locale.CHINESE).charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

}

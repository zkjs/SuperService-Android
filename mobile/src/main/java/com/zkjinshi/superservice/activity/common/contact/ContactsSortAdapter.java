package com.zkjinshi.superservice.activity.common.contact;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.view.CircleImageView;

import org.w3c.dom.Text;

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

    private DisplayImageOptions options;

    public ContactsSortAdapter(Context mContext, List<SortModel> list) {
        this.mContext = mContext;
        this.mList    = list;

        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)// 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.ic_launcher)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.ic_launcher)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();
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
        final SortModel mContent = mList.get(position);
        if(ContactType.LOCAL.getValue() == getItemViewType(position)){
           //本地联系人
            LocalViewHolder localHolder = null;
            if (view == null) {
                localHolder = new LocalViewHolder();
                view = LayoutInflater.from(mContext).inflate(R.layout.item_contact_local, null);
                localHolder.tvLetter         = (TextView) view.findViewById(R.id.catalog);
                localHolder.civContactAvatar = (CircleImageView) view.findViewById(R.id.civ_contact_avatar);
                localHolder.tvContactName    = (TextView) view.findViewById(R.id.tv_contact_name);
                localHolder.tvContactPhone   = (TextView) view.findViewById(R.id.tv_contact_phone);
                view.setTag(localHolder);
            } else {
                localHolder = (LocalViewHolder) view.getTag();
            }

            //根据position获取分类的首字母的Char ascii值
            int section = getSectionForPosition(position);

            //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
            if (position == getPositionForSection(section)) {
                localHolder.tvLetter.setVisibility(View.VISIBLE);
                localHolder.tvLetter.setText(mContent.getSortLetters());
            } else {
                localHolder.tvLetter.setVisibility(View.GONE);
            }

            long contactID = this.mList.get(position).getContactID();

            Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactID);
            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(
                    mContext.getContentResolver(), uri);
            Bitmap contactBitmap = BitmapFactory.decodeStream(input);
            if(null != contactBitmap){
                localHolder.civContactAvatar.setImageBitmap(contactBitmap);
            } else {
                localHolder.civContactAvatar.setImageBitmap(BitmapFactory.decodeResource(
                        mContext.getResources(),
                        R.mipmap.ic_main_user_default_photo_nor));
            }
            localHolder.tvContactName.setText(this.mList.get(position).getName());
            localHolder.tvContactPhone.setText(this.mList.get(position).getNumber());
        } else {
            //本地联系人
            ServerViewHolder serverHolder = null;
            if (view == null) {
                serverHolder = new ServerViewHolder();
                view = LayoutInflater.from(mContext).inflate(R.layout.item_contact_server, null);
                serverHolder.tvLetter         = (TextView) view.findViewById(R.id.catalog);
                serverHolder.civContactAvatar = (CircleImageView) view.findViewById(R.id.civ_contact_avatar);
                serverHolder.tvContactName    = (TextView) view.findViewById(R.id.tv_contact_name);
                serverHolder.tvContactDes     = (TextView) view.findViewById(R.id.tv_contact_des);
                serverHolder.rlContactStatus  = (RelativeLayout) view.findViewById(R.id.rl_contact_status);
                serverHolder.tvContactStatus    = (TextView) view.findViewById(R.id.tv_contact_status);
                serverHolder.rlContactOnStatus  = (RelativeLayout) view.findViewById(R.id.rl_contact_on_status);
                serverHolder.tvContactOnLine    = (TextView) view.findViewById(R.id.tv_contact_on_shop);
                serverHolder.tvContactOnShop    = (TextView) view.findViewById(R.id.tv_contact_on_shop);
                view.setTag(serverHolder);
            } else {
                serverHolder = (ServerViewHolder) view.getTag();
            }

            //是否显示首字母
            int section = getSectionForPosition(position);
            if (position == getPositionForSection(section)) {
                serverHolder.tvLetter.setVisibility(View.VISIBLE);
                String sortLetter = mContent.getSortLetters();
                if("?".equals(sortLetter)){
                    serverHolder.tvLetter.setText(mContext.getString(R.string.latest_contact));
                }else {
                    serverHolder.tvLetter.setText(mContent.getSortLetters());
                }
            } else {
                serverHolder.tvLetter.setVisibility(View.GONE);
            }

            //根据url显示图片
            String avatarUrl = mContent.getAvatarUrl();
            if(!TextUtils.isEmpty(avatarUrl)){
                ImageLoader.getInstance().displayImage(avatarUrl, serverHolder.civContactAvatar, options);
            }
            //显示客户名称
            String clientName = mContent.getName();
            if(!TextUtils.isEmpty(clientName)){
                //去除wen
                if("?".equals(clientName.trim().substring(0,1))){
                    serverHolder.tvContactName.setText(clientName.substring(1));
                }
            }
            //TODO 1.显示客户订单描述
            //TODO 2.显示客户在线状态
        }
        return view;
    }

    public static class LocalViewHolder {
        public TextView         tvLetter;
        public CircleImageView  civContactAvatar;
        public TextView         tvContactName;
        public TextView         tvContactPhone;
    }

    public static class ServerViewHolder {
        public TextView         tvLetter;
        public CircleImageView  civContactAvatar;
        public TextView         tvContactName;
        public TextView         tvContactDes;
        public RelativeLayout   rlContactStatus;
        public TextView         tvContactStatus;
        public RelativeLayout   rlContactOnStatus;
        public TextView         tvContactOnLine;
        public TextView         tvContactOnShop;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return mList.get(position).getSortLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = mList.get(i).getSortLetters();
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

    @Override
    public int getItemViewType(int position) {
        //获得排序名称列表
        SortModel sortModel = mList.get(position);
        return sortModel.getContactType().getValue();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
}

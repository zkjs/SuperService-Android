package com.zkjinshi.superservice.activity.common.contact;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
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
public class ContactsSortAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
                                  implements SectionIndexer, View.OnClickListener {

    private List<SortModel> mList;
    private Context         mContext;

    private DisplayImageOptions options;

    private OnContactItemClickListener mOnContactItemClickListener;

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
            this.mList = new ArrayList<>();
        } else {
            this.mList = list;
        }
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(ContactType.LOCAL.getValue() == viewType){
            //本地联系人
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_contact_local, null);
            view.setOnClickListener(this);
            LocalViewHolder localHolder = new LocalViewHolder(view);
            return localHolder;
        } else {
            //本地联系人
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_contact_server, null);
            ServerViewHolder serverHolder = new ServerViewHolder(view);
            return serverHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //根据position获取分类的首字母的Char ascii值
        SortModel sortModel = mList.get(position);
        int section = getSectionForPosition(position);
        if(sortModel.getContactType() == ContactType.LOCAL){
            //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
            if (position == getPositionForSection(section)) {
                ((LocalViewHolder)holder).tvLetter.setVisibility(View.VISIBLE);
                ((LocalViewHolder)holder).tvLetter.setText(sortModel.getSortLetters());
            } else {
                ((LocalViewHolder)holder).tvLetter.setVisibility(View.GONE);
            }

            long contactID = sortModel.getContactID();
            Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactID);
            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(
                    mContext.getContentResolver(), uri);
            Bitmap contactBitmap = BitmapFactory.decodeStream(input);
            if(null != contactBitmap){
                ((LocalViewHolder)holder).civContactAvatar.setImageBitmap(contactBitmap);
            } else {
                ((LocalViewHolder)holder).civContactAvatar.setImageBitmap(BitmapFactory.decodeResource(
                        mContext.getResources(),
                        R.mipmap.ic_main_user_default_photo_nor));
            }
            ((LocalViewHolder)holder).tvContactName.setText(this.mList.get(position).getName());
            ((LocalViewHolder)holder).tvContactPhone.setText(this.mList.get(position).getNumber());
        } else {
            //是否显示首字母
            if (position == getPositionForSection(section)) {
                ((ServerViewHolder)holder).tvLetter.setVisibility(View.VISIBLE);
                String sortLetter = sortModel.getSortLetters();
                if("?".equals(sortLetter)){
                    ((ServerViewHolder)holder).tvLetter.setText(mContext.getString(R.string.latest_contact));
                }else {
                    ((ServerViewHolder)holder).tvLetter.setText(sortModel.getSortLetters());
                }
            } else {
                ((ServerViewHolder)holder).tvLetter.setVisibility(View.GONE);
            }

            //根据url显示图片
            String avatarUrl = sortModel.getAvatarUrl();
            if(!TextUtils.isEmpty(avatarUrl)){
                ImageLoader.getInstance().displayImage(avatarUrl, ((ServerViewHolder)holder).civContactAvatar, options);
            }
            //显示客户名称
            String clientName = sortModel.getName();
            if(!TextUtils.isEmpty(clientName)){
                //去除wen
                if("?".equals(clientName.trim().substring(0,1))){
                    ((ServerViewHolder)holder).tvContactName.setText(clientName.substring(1));
                }
            }
            //TODO 1.显示客户订单描述
            //TODO 2.显示客户在线状态
        }
        holder.itemView.setTag(sortModel);
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getContactType().getValue();
    }

    public void onClick(View view) {
        if (mOnContactItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnContactItemClickListener.onItemClick(view, (SortModel)view.getTag());
        }
    }

    public void setOnItemClickListener(OnContactItemClickListener listener) {
        this.mOnContactItemClickListener = listener;
    }

    public interface OnContactItemClickListener {
        void onItemClick(View view , SortModel sortModel);
    }

    public static class LocalViewHolder extends RecyclerView.ViewHolder{

        public TextView         tvLetter;
        public CircleImageView  civContactAvatar;
        public TextView         tvContactName;
        public TextView         tvContactPhone;

        public LocalViewHolder(View view) {
            super(view);
            tvLetter         = (TextView) view.findViewById(R.id.catalog);
            civContactAvatar = (CircleImageView) view.findViewById(R.id.civ_contact_avatar);
            tvContactName    = (TextView) view.findViewById(R.id.tv_contact_name);
            tvContactPhone   = (TextView) view.findViewById(R.id.tv_contact_phone);
        }
    }

    public static class ServerViewHolder extends RecyclerView.ViewHolder{

        public TextView         tvLetter;
        public CircleImageView  civContactAvatar;
        public TextView         tvContactName;
        public TextView         tvContactDes;
        public RelativeLayout   rlContactStatus;
        public TextView         tvContactStatus;
        public RelativeLayout   rlContactOnStatus;
        public TextView         tvContactOnLine;
        public TextView         tvContactOnShop;

        public ServerViewHolder(View view) {
            super(view);
            tvLetter         = (TextView) view.findViewById(R.id.catalog);
            civContactAvatar = (CircleImageView) view.findViewById(R.id.civ_contact_avatar);
            tvContactName    = (TextView) view.findViewById(R.id.tv_contact_name);
            tvContactDes     = (TextView) view.findViewById(R.id.tv_contact_des);
            rlContactStatus  = (RelativeLayout) view.findViewById(R.id.rl_contact_status);
            tvContactStatus    = (TextView) view.findViewById(R.id.tv_contact_status);
            rlContactOnStatus  = (RelativeLayout) view.findViewById(R.id.rl_contact_on_status);
            tvContactOnLine    = (TextView) view.findViewById(R.id.tv_contact_on_shop);
            tvContactOnShop    = (TextView) view.findViewById(R.id.tv_contact_on_shop);
        }
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return mList.get(position).getSortLetters().charAt(0);
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = mList.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase(Locale.CHINESE).charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }


}

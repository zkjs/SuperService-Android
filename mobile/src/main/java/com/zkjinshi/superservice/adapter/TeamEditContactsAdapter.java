package com.zkjinshi.superservice.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.deque.LIFOLinkedBlockingDeque;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.utils.RandomDrawbleUtil;
import com.zkjinshi.superservice.utils.StringUtil;
import com.zkjinshi.superservice.view.CircleImageView;
import com.zkjinshi.superservice.view.CircleTextView;
import com.zkjinshi.superservice.vo.OnlineStatus;
import com.zkjinshi.superservice.vo.EmployeeVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 团队编辑页面的联系人列表
 * 开发者：vincent
 * 日期：2015/10/12
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class TeamEditContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SectionIndexer {

    private List<EmployeeVo>  mList;
    private Context               mContext;
    private Map<Integer, Boolean> mCheckedMap;
    private RecyclerItemClickListener mRecyclerItemClickListener;

    public TeamEditContactsAdapter(Context mContext, List<EmployeeVo> list) {

        this.mContext = mContext;
        this.mList    = list;
        this.mCheckedMap = new HashMap<>();

    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     * @param list
     */
    public void updateListView(List<EmployeeVo> list) {
        if (list == null) {
            this.mList = new ArrayList<>();
        } else {
            this.mList = list;
        }
        if(mCheckedMap != null){
            mCheckedMap.clear();
        }
        this.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_team_edit_contact, null);
        view.setLayoutParams(new LinearLayout.LayoutParams(
                                 LinearLayout.LayoutParams.MATCH_PARENT,
                                 LinearLayout.LayoutParams.WRAP_CONTENT));
        ContactViewHolder holder = new ContactViewHolder(view, mRecyclerItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        EmployeeVo employeeVo = mList.get(position);
        int section = getSectionForPosition(position);

        if (position == getPositionForSection(section)) {
            ((ContactViewHolder)holder).tvLetter.setVisibility(View.VISIBLE);
            String deptName = employeeVo.getRolename();
            if(!TextUtils.isEmpty(deptName)){
                ((ContactViewHolder)holder).tvLetter.setText(deptName);
            }
        } else {
            ((ContactViewHolder)holder).tvLetter.setVisibility(View.GONE);
        }

        //显示客户名称
        final String employeeName = employeeVo.getUsername();
        if(!TextUtils.isEmpty(employeeName)) {
            final String firstName = employeeName.substring(0, 1);
            ((ContactViewHolder) holder).tvContactAvatar.setText(firstName);
            ((ContactViewHolder) holder).tvContactName.setText(employeeName);
        }

        //根据url显示图片
        int bgColorRes = RandomDrawbleUtil.getDrawableByIndex(position);
        String empAvatarUrl = ProtocolUtil.getAvatarUrl(mContext,employeeVo.getUserimage());
        ((ContactViewHolder) holder).civContactAvatar.setImageURI( Uri.parse(empAvatarUrl));
        ((ContactViewHolder) holder).tvContactAvatar.setBackgroundResource(bgColorRes);
        ((ContactViewHolder) holder).tvContactAvatar.setVisibility(View.VISIBLE);


        //set the checkbox
        ((ContactViewHolder) holder).cbCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCheckedMap.put(position, isChecked);
            }
        });

        if(mCheckedMap.containsKey(position) && mCheckedMap.get(position)){
            ((ContactViewHolder) holder).cbCheck.setChecked(true);
        } else {
            ((ContactViewHolder) holder).cbCheck.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setOnItemClickListener(RecyclerItemClickListener listener) {
        this.mRecyclerItemClickListener = listener;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        if(!TextUtils.isEmpty(mList.get(position).getRolename())){
            return mList.get(position).getRolename().charAt(0);
        }
        return -1;
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
            String sortStr = mList.get(i).getRolename();
            if(!TextUtils.isEmpty(sortStr)){
                char firstChar = sortStr.toUpperCase(Locale.CHINESE).charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }
        }
        return -1;
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder{

        public TextView         tvLetter;
        public SimpleDraweeView civContactAvatar;
        public TextView         tvContactAvatar;
        public TextView         tvContactName;
//        public RelativeLayout   rlContactStatus;
//        public TextView         tvContactStatus;
//        public RelativeLayout   rlContactOnStatus;
//        public TextView         tvContactOnLine;
        public CheckBox         cbCheck;

        private RecyclerItemClickListener mItemClickListener;

        public ContactViewHolder(View view, RecyclerItemClickListener itemClickListener) {
            super(view);
            tvLetter         = (TextView) view.findViewById(R.id.catalog);
            civContactAvatar = (SimpleDraweeView) view.findViewById(R.id.civ_contact_avatar);
            tvContactAvatar  = (TextView) view.findViewById(R.id.tv_contact_avatar);
            tvContactName    = (TextView) view.findViewById(R.id.tv_contact_name);
//            rlContactStatus  = (RelativeLayout) view.findViewById(R.id.rl_contact_status);
//            tvContactStatus    = (TextView) view.findViewById(R.id.tv_contact_status);
//            rlContactOnStatus  = (RelativeLayout) view.findViewById(R.id.rl_contact_on_status);
//            tvContactOnLine    = (TextView) view.findViewById(R.id.tv_contact_on_line);
            cbCheck            = (CheckBox) view.findViewById(R.id.cb_check);

            this.mItemClickListener = itemClickListener;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mItemClickListener != null){
                        //设置复选框选中
                        cbCheck.setChecked(!cbCheck.isChecked());
                        mItemClickListener.onItemClick(v, getPosition());
                    }
                }
            });
        }
    }

}

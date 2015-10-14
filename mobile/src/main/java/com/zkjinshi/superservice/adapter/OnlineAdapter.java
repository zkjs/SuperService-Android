package com.zkjinshi.superservice.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.sqlite.ChatRoomDBUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.view.CircleImageView;
import com.zkjinshi.superservice.vo.ChatRoomVo;
import com.zkjinshi.superservice.vo.EmpStatusVo;
import com.zkjinshi.superservice.vo.MessageVo;
import com.zkjinshi.superservice.vo.MimeType;

import java.util.ArrayList;

/**
 * 消息通知适配器
 * 开发者：JimmyZhang
 * 日期：2015/9/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OnlineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<EmpStatusVo> empStatusList;
    private Context context;
    private LayoutInflater inflater;
    private DisplayImageOptions options;
    private RecyclerItemClickListener itemClickListener;

    public void setEmpStatusList(ArrayList<EmpStatusVo> empStatusList) {
        if(null == empStatusList){
            this.empStatusList = new ArrayList<EmpStatusVo>();
        }else{
            this.empStatusList = empStatusList;
        }
        notifyDataSetChanged();
    }

    public OnlineAdapter(Context context, ArrayList<EmpStatusVo> empStatusList){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.setEmpStatusList(empStatusList);
        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.img_hotel_zhanwei)// 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.img_hotel_zhanwei)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.img_hotel_zhanwei)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_online_list, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        EmpStatusVo empStatusVo = empStatusList.get(position);
        long loginTimestamp = empStatusVo.getLoginTimestamp();
        int status = empStatusVo.getOnlineStatus();
        String empName = empStatusVo.getEmpName();
        String empId = empStatusVo.getEmpId();
        if(!TextUtils.isEmpty(empId)){
            String imageUrl = Constants.GET_USER_AVATAR + empId + ".jpg";
            if(!TextUtils.isEmpty(imageUrl)){
                ImageLoader.getInstance().displayImage(imageUrl, ((ViewHolder)holder).photoImageView,options);
            }
        }
        if(!TextUtils.isEmpty(empName)){
            ((ViewHolder)holder).nameTv.setText(empName);
        }
        if(status == 1){// 0:在线 1:不在线
            ((ViewHolder)holder).statusTv.setText("离线");
            ((ViewHolder) holder).timeTv.setVisibility(View.INVISIBLE);
        }else{
            ((ViewHolder)holder).statusTv.setText("在线");
            ((ViewHolder) holder).timeTv.setText(TimeUtil.getChatTime(loginTimestamp));
            ((ViewHolder) holder).timeTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return empStatusList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private CircleImageView photoImageView;
        private TextView nameTv,statusTv,timeTv;

        public ViewHolder(View itemView) {
            super(itemView);
            photoImageView = (CircleImageView) itemView.findViewById(R.id.online_civ_photo);
            nameTv = (TextView) itemView.findViewById(R.id.online_tv_emp_name);
            statusTv = (TextView) itemView.findViewById(R.id.online_tv_online_status);
            timeTv = (TextView) itemView.findViewById(R.id.online_tv_online_time);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(null != itemClickListener){
                itemClickListener.onItemClick(v,getAdapterPosition());
            }
        }
    }

    public void setOnItemClickListener(RecyclerItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}

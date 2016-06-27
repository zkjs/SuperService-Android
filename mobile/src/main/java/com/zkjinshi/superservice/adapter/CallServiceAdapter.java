package com.zkjinshi.superservice.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.view.CircleImageView;
import com.zkjinshi.superservice.vo.ServiceTaskVo;
import com.zkjinshi.superservice.vo.UserVo;

import java.util.ArrayList;

/**
 * 呼叫服务适配器
 * 开发者：jimmyzhang
 * 日期：16/6/22
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class CallServiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ServiceTaskVo> serviceList;
    private Context context;
    private RecyclerItemClickListener itemClickListener;
    private ServiceOptionListener optionListener;

    public void setServiceList(ArrayList<ServiceTaskVo> serviceList) {
        if(null == serviceList){
            this.serviceList = new ArrayList<ServiceTaskVo>();
        }else {
            this.serviceList = serviceList;
        }
        notifyDataSetChanged();
    }

    public CallServiceAdapter(Context context, ArrayList<ServiceTaskVo> serviceList){
        this.context = context;
        this.setServiceList(serviceList);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_call_service,null);
        CallServiceViewHolder callServiceViewHolder = new CallServiceViewHolder(view);
        return callServiceViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        LinearLayout.LayoutParams contentLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        int rightMargin = DisplayUtil.dip2px(context, 8);
        int commonMargin   = DisplayUtil.dip2px(context, 6);
        if(position == 0){
            ((CallServiceViewHolder) holder).topCutLineView.setVisibility(View.INVISIBLE);
            contentLayoutParams.setMargins(0, 2*commonMargin,rightMargin, commonMargin);
        }else{
            contentLayoutParams.setMargins(0, commonMargin, rightMargin, commonMargin);
            ((CallServiceViewHolder) holder).topCutLineView.setVisibility(View.VISIBLE);
        }
        ((CallServiceViewHolder) holder).contentLayout.setLayoutParams(contentLayoutParams);
        final ServiceTaskVo serviceTaskVo = serviceList.get(position);
        if(null != serviceTaskVo){

            String userImage = serviceTaskVo.getUserimage();
            int width = (int)context.getResources().getDimension(R.dimen.user_photo_width);
            String imageUrl = ProtocolUtil.getImageUrlByWidth(context,userImage,width);
            if(!TextUtils.isEmpty(imageUrl)){
                ((CallServiceViewHolder)holder).userPhotoSdv.setImageURI(Uri.parse(imageUrl));
            }
            String userNameStr = serviceTaskVo.getUsername();
            if(!TextUtils.isEmpty(userNameStr)){
                ((CallServiceViewHolder)holder).userNameTv.setText(userNameStr);
            }

            int statuscode = serviceTaskVo.getStatuscode();//1. 发起任务 2. 指派 3. 就绪 4.取消 5 完成 6 评价
            if(statuscode == 1 ){
                ((CallServiceViewHolder)holder).statusIv.setBackgroundResource(R.drawable.bg_service_circle_red);
            }else if(statuscode == 2 || statuscode == 3){
                ((CallServiceViewHolder)holder).statusIv.setBackgroundResource(R.drawable.bg_service_circle_blue);
            }else {
                ((CallServiceViewHolder)holder).statusIv.setBackgroundResource(R.drawable.bg_service_circle_gray);
            }
            String statusDescStr = serviceTaskVo.getStatus();
            if(!TextUtils.isEmpty(statusDescStr)){
                ((CallServiceViewHolder)holder).statusTv.setText(statusDescStr);
            }
            String timeStr = serviceTaskVo.getCreatetime();
            if(!TextUtils.isEmpty(timeStr)){
                ((CallServiceViewHolder)holder).timeTv.setText(timeStr);
            }

            //完成
            ((CallServiceViewHolder)holder).finishLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(null != optionListener){
                        optionListener.executeFinish(serviceTaskVo);
                    }
                }
            });

            //就绪
            ((CallServiceViewHolder)holder).readyLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(null != optionListener){
                        optionListener.executeReady(serviceTaskVo);
                    }
                }
            });
            int ownerStatus = serviceTaskVo.getIsowner();
            ((CallServiceViewHolder)holder).appointLayout.setTag(ownerStatus);
            if(ownerStatus == 0){  //指派
                ((CallServiceViewHolder)holder).appointTv.setText("指派");
            }else {//取消
                ((CallServiceViewHolder)holder).appointTv.setText("取消");
            }
            ((CallServiceViewHolder)holder).appointLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int ownerStatus = (int)view.getTag();
                    if(ownerStatus == 0){  //指派
                        if(null != optionListener){
                            optionListener.executeAppoint(serviceTaskVo);
                        }
                    }else {//取消
                        if(null != optionListener){
                            optionListener.executeCancel(serviceTaskVo);
                        }
                    }
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    class CallServiceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        View topCutLineView;
        SimpleDraweeView userPhotoSdv;
        CircleImageView statusIv;
        TextView userNameTv,projectTv,timeTv,statusTv,appointTv;
        LinearLayout finishLayout,readyLayout,appointLayout;
        LinearLayout contentLayout;

        public CallServiceViewHolder(View itemView) {
            super(itemView);
            topCutLineView = (View) itemView.findViewById(R.id.call_service_time_axis_cut_line_top);
            userPhotoSdv = (SimpleDraweeView)itemView.findViewById(R.id.call_service_user_photo_sdv);
            statusIv = (CircleImageView)itemView.findViewById(R.id.call_service_status_civ);
            userNameTv = (TextView)itemView.findViewById(R.id.call_service_user_name_tv);
            projectTv = (TextView)itemView.findViewById(R.id.call_service_project_tv);
            timeTv = (TextView)itemView.findViewById(R.id.call_service_time_tv);
            statusTv = (TextView)itemView.findViewById(R.id.call_service_status_tv);
            finishLayout = (LinearLayout)itemView.findViewById(R.id.call_service_finish_layout);
            readyLayout = (LinearLayout)itemView.findViewById(R.id.call_service_ready_layout);
            appointLayout = (LinearLayout)itemView.findViewById(R.id.call_service_appoint_layout);
            appointTv = (TextView)itemView.findViewById(R.id.call_service_appoint_tv);
            contentLayout = (LinearLayout)itemView.findViewById(R.id.call_service_content_layout);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(null != itemClickListener){
                itemClickListener.onItemClick(view,getAdapterPosition());
            }
        }
    }

    public void setOnItemClickListener(RecyclerItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setOnOptionListener(ServiceOptionListener optionListener){
        this.optionListener = optionListener;
    }

    /**
     * 呼叫服务操作监听
     */
    public interface ServiceOptionListener{
        void executeCancel(ServiceTaskVo taskVo);//取消
        void executeFinish(ServiceTaskVo taskVo);//完成
        void executeAppoint(ServiceTaskVo taskVo);//指派
        void executeReady(ServiceTaskVo taskVo);//就绪
    }
}

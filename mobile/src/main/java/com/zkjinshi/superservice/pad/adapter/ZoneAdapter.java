package com.zkjinshi.superservice.pad.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zkjinshi.superservice.pad.bean.ZoneBean;
import com.zkjinshi.superservice.pad.R;

import java.util.ArrayList;

/**
 * 区域列表adapter
 * 开发者：杜健德
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ZoneAdapter extends BaseAdapter{
    private final static String TAG = ZoneAdapter.class.getSimpleName();

    private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局
    private ArrayList<ZoneBean> zoneList = new ArrayList<ZoneBean>();

    public ZoneAdapter(Context context,ArrayList<ZoneBean> zoneList) {
        this.mInflater = LayoutInflater.from(context);
        this.zoneList = zoneList;
    }

    public void setCheckedZone(ArrayList<ZoneBean> checkedZone){
        for(int i=0;i<checkedZone.size();i++){
            for(int j=0;j<zoneList.size();j++){
                if(checkedZone.get(i).getLocid().equals(zoneList.get(j).getLocid())){
                    zoneList.get(j).setSubscribed(1);
                }
            }
        }
    }

    /**
     * 获得所有订阅区域
     * @return
     */
    public String[] getAllLocIds(){
        String[] topics = null;
        if(null != zoneList && !zoneList.isEmpty()){
            topics = new String[zoneList.size()];
            for(int i=0;i<zoneList.size();i++){
                ZoneBean zoneBean = zoneList.get(i);
                topics[i] = ""+zoneBean.getLocid();
            }
        }
        return  topics;
    }

    public ArrayList<ZoneBean> getSelectZoneBeanList(){
        ArrayList<ZoneBean> selectZoneList = new ArrayList<ZoneBean>();
        if(null != zoneList && !zoneList.isEmpty()){
            for(int i=0;i<zoneList.size();i++){
                ZoneBean zoneBean = zoneList.get(i);
                if(zoneBean.getSubscribed() == 1){
                    selectZoneList.add(zoneBean);
                }
            }
        }
        return selectZoneList;
    }

    /**
     * 获取选择订阅区域
     * @return
     */
    public String[] getLocIds(){
        String[] topics = null;
        ArrayList<ZoneBean> selectZoneList = getSelectZoneBeanList();
        if(null != selectZoneList && !selectZoneList.isEmpty()){
           topics = new String[selectZoneList.size()];
            for(int i = 0; i < selectZoneList.size(); i++){
                topics[i] = ""+selectZoneList.get(i).getLocid();
            }
        }
        return  topics;
    }

    public String getCheckedIds(){
        String ids = "";
        if(zoneList == null){
            return  ids;
        }
        for(int i=0;i<zoneList.size();i++){
            ZoneBean zoneBean = zoneList.get(i);
            if(zoneBean.getSubscribed() == 1){
                if(TextUtils.isEmpty(ids)){
                    ids = ids+zoneBean.getLocid();
                }else{
                    ids = ids+","+zoneBean.getLocid();
                }
            }

        }
        return  ids;
    }

    public ArrayList<ZoneBean> getZoneList() {
        return zoneList;
    }

    public void setZoneList(ArrayList<ZoneBean> zoneList) {
        this.zoneList = zoneList;
    }

    @Override
    public int getCount() {
        return zoneList.size();
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
            convertView = mInflater.inflate(R.layout.zone_item, null);
            holder = new ViewHolder();
            holder.img = (ImageView)convertView.findViewById(R.id.zone_img_iv);
            holder.name = (TextView)convertView.findViewById(R.id.zone_name_tv);
            holder.check      = (ImageView)convertView.findViewById(R.id.zone_cbx);
            convertView.setTag(holder);//绑定ViewHolder对象
        }else{
            holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
        }
        holder.name.setText(zoneList.get(position).getArea());

        if(zoneList.get(position).getSubscribed() == 1){
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
        public ImageView img;
        public TextView  name;
        public ImageView check;
    }

}

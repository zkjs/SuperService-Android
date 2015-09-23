package com.zkjinshi.superservice.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.vo.ZoneVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 区域列表adapter
 * 开发者：杜健德
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ZoneAdapter extends BaseAdapter {
    private final static String TAG = ZoneAdapter.class.getSimpleName();

    private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局
    private ArrayList<ZoneVo> zoneList = new ArrayList<ZoneVo>();
    private HashMap<Integer,Integer> checkedHashmap = new HashMap<Integer,Integer>();

    public ZoneAdapter(Context context,ArrayList<ZoneVo> zoneList) {
        this.mInflater = LayoutInflater.from(context);
        this.zoneList = zoneList;
    }

    public ArrayList<ZoneVo> getZoneList() {
        return zoneList;
    }

    public void setZoneList(ArrayList<ZoneVo> zoneList) {
        this.zoneList = zoneList;
    }

    // 返回选择的ID 格式是用逗号隔开。
   public String getCheckedIds(){
       Iterator iterator = checkedHashmap.entrySet().iterator();
       String ids = "";
       int i = 0;
       while (iterator.hasNext()){
           Map.Entry entry = (Map.Entry)iterator.next();
           Integer value = (Integer)entry.getValue();
           if(value.intValue() == 1){
               Integer key = (Integer)entry.getKey();
               if(i == 0){
                   ids = ids+key;
               }else{
                   ids = ids+","+key;
               }
               i++;
           }
       }
       return ids;
   }

    private boolean ischecked(int id){
        if(!checkedHashmap.containsKey(new Integer(id))){
            return false;
        }
        Integer value = checkedHashmap.get(new Integer(id));
        if(value.intValue() == 1){
            return true;
        }else{
            return false;
        }
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
            holder.check      = (CheckBox)convertView.findViewById(R.id.zone_cbx);
            convertView.setTag(holder);//绑定ViewHolder对象
        }else{
            holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
        }
        holder.name.setText(zoneList.get(position).getName());
        holder.check.setTag(new Integer(zoneList.get(position).getId()));
        holder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Integer key = (Integer)compoundButton.getTag();
                if(b){
                    checkedHashmap.put(key,new Integer(1));
                }else{
                    checkedHashmap.put(key,new Integer(0));
                }
                Log.d(TAG,"checkedids = "+getCheckedIds());
            }
        });
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
        public CheckBox check;
    }

}

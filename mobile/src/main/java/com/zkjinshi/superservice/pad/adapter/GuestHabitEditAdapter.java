package com.zkjinshi.superservice.pad.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.zkjinshi.superservice.pad.R;
import com.zkjinshi.superservice.pad.view.RoundProgressBar;
import com.zkjinshi.superservice.pad.vo.ItemTagVo;

import java.util.List;
import java.util.Map;

/**
 * Created by qinyejun on 7/26/16.
 */
public class GuestHabitEditAdapter extends ArrayAdapter<ItemTagVo> {
    private List<ItemTagVo> objects;
    private Context mContext;
    private int resource;
    private Map<Integer, Boolean> mSelectMap;

    public GuestHabitEditAdapter(Context context, int resource,
                                 List<ItemTagVo> objects) {
        super(context, resource, objects);
        this.objects = objects;
        this.mContext = context;
        this.resource = resource;
    }

    public void setSelectMap(Map<Integer, Boolean> selectMap) {
        mSelectMap = selectMap;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return super.getCount();
    }

    @Override
    public ItemTagVo getItem(int position) {
        // TODO Auto-generated method stub
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(resource, null);
            holder.roundProgressBar = (RoundProgressBar) convertView.findViewById(R.id.client_label_progress);
            holder.title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.client_label_ck);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ItemTagVo item = this.getItem(position);
        String tagName =  item.getTagname();
        int value = item.getCount();
        holder.roundProgressBar.setAnimDuration(50);
        holder.roundProgressBar.setInterpolator(new AccelerateDecelerateInterpolator());
        holder.roundProgressBar.setSweepValue(value);
        holder.roundProgressBar.anim();
        if(!TextUtils.isEmpty(tagName)){
            //holder.roundProgressBar.setValueText(tagName);
            holder.title.setText(item.getTagname());
        } else {
            holder.title.setText("");
        }
        int isopt = item.getIsopt();
        int tagId = item.getTagid();
        if (isopt == 1) {
            holder.checkBox.setEnabled(false);
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setEnabled(true);
            if (mSelectMap != null
                    && mSelectMap.containsKey(tagId)
                    && mSelectMap.get(tagId)) {
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
            }
        }

        return convertView;
    }

    public void setData(List<ItemTagVo> data) {
        this.objects = data;
    }

    final class ViewHolder {
        TextView title;
        RoundProgressBar roundProgressBar;
        CheckBox checkBox;
    }

}

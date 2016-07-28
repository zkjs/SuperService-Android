package com.zkjinshi.superservice.activity.label;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.GuestArrivingAdapter;
import com.zkjinshi.superservice.adapter.GuestHabitAdapter;
import com.zkjinshi.superservice.adapter.GuestHabitEditAdapter;
import com.zkjinshi.superservice.adapter.GuestPaymentAdapter;
import com.zkjinshi.superservice.base.BaseActivity;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.response.BaseResponse;
import com.zkjinshi.superservice.response.ClientArrivingResponse;
import com.zkjinshi.superservice.response.ClientPaymentResponse;
import com.zkjinshi.superservice.response.ClientTagResponse;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.ClientArrivingVo;
import com.zkjinshi.superservice.vo.ClientPaymentVo;
import com.zkjinshi.superservice.vo.ClientTagVo;
import com.zkjinshi.superservice.vo.ItemTagVo;
import com.zkjinshi.superservice.vo.NoticeVo;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by qinyejun on 7/28/16.
 */
public class GuestLabelEditActivity extends BaseActivity {
    public static String EXTRA_HABITS = "superservice.habits";
    public static String EXTRA_CANOPT_COUNT = "superservice.canoptcnt";
    public static final String TAG = "GuestInfoActivity";

    private NoticeVo noticeVo;
    private String clientId = "";

    private Button backBtn;
    private TextView titleTv;
    private Button confirmBtn;
    private ListView listView;

    private int canoptcnt;
    private ArrayList<ItemTagVo> habitList = new ArrayList<>();
    private GuestHabitEditAdapter habitAdapter;

    private Map<Integer, Boolean> mSelectMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_habit_edit);

        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(null);

        initView();
        initData();
        initListeners();
    }

    private void initView(){
        //labelGridView = (LabelGridView) findViewById(R.id.client_label_gv);
        backBtn = (Button)findViewById(R.id.header_bar_btn_back);
        titleTv = (TextView)findViewById(R.id.header_bar_tv_title);
        confirmBtn = (Button)findViewById(R.id.header_bar_btn_add);
    }

    private void initData(){
        titleTv.setText("喜好标签");

        //mSelectMap = new HashMap<Integer, Boolean>();
        //clientLabelAdapter = new ClientLabelAdapter(this,tagList);
        //labelGridView.setAdapter(clientLabelAdapter);
        //clientLabelAdapter.setSelectMap(mSelectMap);
        //labelGridView.setTag(tagList);
        if(null != getIntent() && null != getIntent().getSerializableExtra("noticeVo")){
            noticeVo = (NoticeVo) getIntent().getSerializableExtra("noticeVo");
            if(null != noticeVo){
                clientId = noticeVo.getUserid();
            }
        }

        if(null != getIntent() && null != getIntent().getSerializableExtra(EXTRA_CANOPT_COUNT)){
            canoptcnt = getIntent().getIntExtra(EXTRA_CANOPT_COUNT, 0);
        }

        if(null != getIntent() && null != getIntent().getSerializableExtra(EXTRA_HABITS)){
            habitList = (ArrayList<ItemTagVo>) getIntent().getSerializableExtra(EXTRA_HABITS);
        }

        habitAdapter = new GuestHabitEditAdapter(GuestLabelEditActivity.this, R.layout.list_row_habit_edit, habitList);
        habitAdapter.setSelectMap(mSelectMap);
        listView.setAdapter(habitAdapter);
    }

    private void initListeners(){

        //返回
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //添加消费记录
        confirmBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DialogUtil.getInstance().showCustomToast(GuestLabelEditActivity.this,"打标签成功", Gravity.CENTER);
            }
        });

        //选择喜好标签
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //tagList = (ArrayList<ItemTagVo>) listView.getTag();
                //canoptcnt = (Integer) labelGridView.getTag(R.id.client_label_gv);
                Log.i(TAG, canoptcnt + "");
                if(isClickableLabel(canoptcnt)){
                    ItemTagVo itemTagVo = habitList.get(position);
                    int count = itemTagVo.getIsopt();
                    if(count == 1){
                        DialogUtil.getInstance().showCustomToast(GuestLabelEditActivity.this,"同个客户相同标签只能贴一次",Gravity.CENTER);
                    }else {
                        int tagId = itemTagVo.getTagid();
                        if(isSelectedMore(tagId,canoptcnt)){
                            Boolean isSelect = true;
                            if (mSelectMap != null
                                    && mSelectMap.containsKey(tagId)){
                                isSelect = !mSelectMap.get(tagId);
                            }
                            mSelectMap.put(tagId, isSelect);
                            habitAdapter.setSelectMap(mSelectMap);
                            if(isSelectedLabel()){
                                confirmBtn.setVisibility(View.VISIBLE);
                            }else {
                                confirmBtn.setVisibility(View.GONE);
                            }
                            habitAdapter.notifyDataSetChanged();
                        }else {
                            DialogUtil.getInstance().showCustomToast(GuestLabelEditActivity.this,"超过当天打标签上限",Gravity.CENTER);
                        }
                    }
                }else {
                    DialogUtil.getInstance().showCustomToast(GuestLabelEditActivity.this,"超过当天打标签上限",Gravity.CENTER);
                }
            }
        });

        //确认标签
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONArray selectedList = getSelectedArr();
                ClientLabelController.getInstance().requestUpdateClientTagsTask(clientId, selectedList, v.getContext(), new ExtNetRequestListener(v.getContext()) {
                    @Override
                    public void onNetworkRequestError(int errorCode, String errorMessage) {
                        super.onNetworkRequestError(errorCode, errorMessage);
                    }

                    @Override
                    public void onNetworkResponseSucceed(NetResponse result) {
                        super.onNetworkResponseSucceed(result);
                        if(null != result && !TextUtils.isEmpty(result.rawResult)){
                            BaseResponse baseResponse = new Gson().fromJson(result.rawResult,BaseResponse.class);
                            Log.d(TAG,result.rawResult);
                            if(null != baseResponse){
                                int resultCode = baseResponse.getRes();
                                if(0 == resultCode){
                                    DialogUtil.getInstance().showCustomToast(GuestLabelEditActivity.this,"打标签成功",Gravity.CENTER);
                                    mSelectMap.clear();
                                    habitAdapter.setSelectMap(mSelectMap);
                                    if(isSelectedLabel()){
                                        confirmBtn.setVisibility(View.VISIBLE);
                                    }else {
                                        confirmBtn.setVisibility(View.GONE);
                                    }
                                    GuestLabelEditActivity.this.setResult(RESULT_OK);
                                    GuestLabelEditActivity.this.finish();
                                }else {
                                    String errorMsg = baseResponse.getResDesc();
                                    if(!TextUtils.isEmpty(errorMsg)){
                                        DialogUtil.getInstance().showCustomToast(GuestLabelEditActivity.this,errorMsg, Gravity.CENTER);
                                    }
                                }
                            }
                        }
                    }
                });
            }
        });
    }


    /**
     * 标签是否可点击
     * @return
     */
    private boolean isClickableLabel(int canoptcnt){
        if(canoptcnt <= 0 ){
            return false;
        }
        return true;
    }

    /**
     * 是否已选择标签
     * @return
     */
    private boolean isSelectedLabel(){
        if(null != mSelectMap){
            Iterator iterator = mSelectMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                boolean selected = (Boolean) entry.getValue();
                if(selected){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否还可以选择
     * @return
     */
    private boolean isSelectedMore(int tagId,int canoptcnt){
        int selectedCount = 0;
        if(null != mSelectMap){
            Iterator iterator = mSelectMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Integer key = (Integer) entry.getKey();
                boolean selected = (Boolean) entry.getValue();
                if(selected){
                    if(tagId == key){
                        return true;
                    }
                    selectedCount ++;
                }
            }
        }
        if(selectedCount >= canoptcnt){
            return false;
        }else {
            return true;
        }
    }

    /**
     * 获取选择标签列表
     * @return
     */
    private JSONArray getSelectedArr(){
        JSONArray jsonArray = new JSONArray();
        if(null != mSelectMap){
            Iterator iterator = mSelectMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Integer key = (Integer) entry.getKey();
                boolean selected = (Boolean) entry.getValue();
                if(selected){
                    jsonArray.put(key);
                }
            }
        }
        return jsonArray;
    }

}

package com.zkjinshi.superservice.activity.label;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.textservice.TextInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.ClientLabelAdapter;
import com.zkjinshi.superservice.base.BaseActivity;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.response.BaseResponse;
import com.zkjinshi.superservice.response.ClientTagResponse;
import com.zkjinshi.superservice.test.TagBiz;
import com.zkjinshi.superservice.view.LabelGridView;
import com.zkjinshi.superservice.vo.ClientTagVo;
import com.zkjinshi.superservice.vo.ItemTagVo;
import com.zkjinshi.superservice.vo.NoticeVo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 用户标签页面
 * 开发者：JimmyZhang
 * 日期：2016/4/27
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientLabelActivity extends BaseActivity {

    private NoticeVo noticeVo;
    private String clientId;
    private int canoptcnt;
    private ArrayList<ItemTagVo> tagList;
    private LabelGridView labelGridView;
    private ClientLabelAdapter clientLabelAdapter;
    private Map<Integer, Boolean> mSelectMap;
    private ImageButton backIBtn;
    private TextView titleTv;
    private SimpleDraweeView clientPhotoView;
    private TextView clientNameTv,clientSexTv;
    private Button sureBtn;

    private void initView(){
        labelGridView = (LabelGridView) findViewById(R.id.client_label_gv);
        backIBtn = (ImageButton)findViewById(R.id.header_bar_btn_back);
        titleTv = (TextView)findViewById(R.id.header_bar_tv_title);
        clientPhotoView = (SimpleDraweeView)findViewById(R.id.client_user_photo_dv);
        clientNameTv = (TextView)findViewById(R.id.client_user_name_tv);
        clientSexTv = (TextView)findViewById(R.id.client_user_sex_tv);
        sureBtn = (Button)findViewById(R.id.sure_select_label_btn);
    }

    private void initData(){
        sureBtn.setVisibility(View.GONE);
        backIBtn.setVisibility(View.VISIBLE);
        titleTv.setText("客户信息");
        labelGridView.setTag(R.id.client_label_gv,canoptcnt);
        mSelectMap = new HashMap<Integer, Boolean>();
        clientLabelAdapter = new ClientLabelAdapter(this,tagList);
        labelGridView.setAdapter(clientLabelAdapter);
        clientLabelAdapter.setSelectMap(mSelectMap);
        labelGridView.setTag(tagList);
        if(null != getIntent() && null != getIntent().getSerializableExtra("noticeVo")){
            noticeVo = (NoticeVo) getIntent().getSerializableExtra("noticeVo");
            if(null != noticeVo){
                clientId = noticeVo.getUserid();
                sureBtn.setTag(clientId);
                String userImg = noticeVo.getUserimage();
                if(!TextUtils.isEmpty(userImg)){
                    Uri userImgUri = Uri.parse(ConfigUtil.getInst().getCdnDomain()+userImg);
                    clientPhotoView.setImageURI(userImgUri);
                }
                String userName = noticeVo.getUsername();
                if(!TextUtils.isEmpty(userName)){
                    clientNameTv.setText(userName);
                }
                int sexId = noticeVo.getSex();
                if(-1 == sexId){
                    clientSexTv.setVisibility(View.GONE);
                }else {
                    clientSexTv.setVisibility(View.VISIBLE);
                    if(0 == sexId){
                        clientSexTv.setText("女");
                    }else {
                        clientSexTv.setText("男");
                    }
                }
            }
        }
    }

    private void initListeners(){

        //返回
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //单选喜好标签
        labelGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tagList = (ArrayList<ItemTagVo>) labelGridView.getTag();
                canoptcnt = (Integer) labelGridView.getTag(R.id.client_label_gv);
                if(isClickableLabel(canoptcnt)){
                    ItemTagVo itemTagVo = tagList.get(position);
                    int count = itemTagVo.getIsopt();
                    if(count == 1){
                        DialogUtil.getInstance().showCustomToast(ClientLabelActivity.this,"同个客户相同标签只能贴一次",Gravity.CENTER);
                    }else {
                        int tagId = itemTagVo.getTagid();
                        if(isSelectedMore(tagId,canoptcnt)){
                            Boolean isSelect = true;
                            if (mSelectMap != null
                                    && mSelectMap.containsKey(tagId)){
                                isSelect = !mSelectMap.get(tagId);
                            }
                            mSelectMap.put(tagId, isSelect);
                            clientLabelAdapter.setSelectMap(mSelectMap);
                            if(isSelectedLabel()){
                                sureBtn.setVisibility(View.VISIBLE);
                            }else {
                                sureBtn.setVisibility(View.GONE);
                            }
                            clientLabelAdapter.notifyDataSetChanged();
                        }else {
                            DialogUtil.getInstance().showCustomToast(ClientLabelActivity.this,"一天只可添加3个标签",Gravity.CENTER);
                        }
                    }
                }else {
                    DialogUtil.getInstance().showCustomToast(ClientLabelActivity.this,"一天只可添加3个标签",Gravity.CENTER);
                }
            }
        });

        //确认标签
        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String clientId = (String) v.getTag();
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
                            if(null != baseResponse){
                                int resultCode = baseResponse.getRes();
                                if(0 == resultCode){
                                    DialogUtil.getInstance().showCustomToast(ClientLabelActivity.this,"打标签成功",Gravity.CENTER);
                                    mSelectMap.clear();
                                    clientLabelAdapter.setSelectMap(mSelectMap);
                                    if(isSelectedLabel()){
                                        sureBtn.setVisibility(View.VISIBLE);
                                    }else {
                                        sureBtn.setVisibility(View.GONE);
                                    }
                                    requestLabelListTask();
                                }else {
                                    String errorMsg = baseResponse.getResDesc();
                                    if(!TextUtils.isEmpty(errorMsg)){
                                        DialogUtil.getInstance().showCustomToast(ClientLabelActivity.this,errorMsg, Gravity.CENTER);
                                    }
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_label);
        initView();
        initData();
        initListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!TextUtils.isEmpty(clientId)){
            requestLabelListTask();
        }
    }

    private void requestLabelListTask(){
        //获取用户标签信息
        ClientLabelController.getInstance().requestGetClientTagsTask(clientId, this, new ExtNetRequestListener(this) {
            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                if(null != result && !TextUtils.isEmpty(result.rawResult)){
                    ClientTagResponse clientTagResponse = new Gson().fromJson(result.rawResult,ClientTagResponse.class);
                    if(null != clientTagResponse){
                        int resultCode = clientTagResponse.getRes();
                        if(0 == resultCode){
                            ClientTagVo clientTagVo = clientTagResponse.getData();
                            if(null != clientTagVo){
                                canoptcnt = clientTagVo.getCanoptcnt();
                                tagList = clientTagVo.getTags();
                                clientLabelAdapter.setTagList(tagList);
                                labelGridView.setTag(tagList);
                                labelGridView.setTag(R.id.client_label_gv,canoptcnt);
                            }
                        }else {
                            String resultMsg = clientTagResponse.getResDesc();
                            if(!TextUtils.isEmpty(resultMsg)){
                                DialogUtil.getInstance().showCustomToast(ClientLabelActivity.this,resultMsg, Gravity.CENTER);
                            }
                        }
                    }
                }
            }

            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                super.onNetworkRequestError(errorCode, errorMessage);
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

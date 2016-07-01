package com.zkjinshi.superservice.activity.set;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.SoftInputUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.common.CutActivity;
import com.zkjinshi.superservice.base.BaseAppCompatActivity;
import com.zkjinshi.superservice.base.BaseApplication;
import com.zkjinshi.superservice.response.BaseResponse;
import com.zkjinshi.superservice.utils.AsyncHttpClientUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.FileUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.EventVo;

import org.json.JSONObject;

import java.io.File;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 编辑活动
 * 开发者：jimmyzhang
 * 日期：16/6/28
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class EditEventActivity extends BaseAppCompatActivity {

    private Toolbar toolbar;
    private TextView titleIv;
    private EditText eventNameEtv,eventContentEtv,carryCountEtv,eventUrlEtv;
    private TextView startDateTv,overDateTv,cutLineTv;
    private SimpleDraweeView eventLogoIv;
    private CheckBox isCarryCb;//是否携带人
    private RelativeLayout carryInputLayout;
    private TimePickerView timePickerView;
    private boolean isEventStartDate = true;
    private Date startDate,endDate;
    private String actImagePath;
    private Button sureBtn;
    private EventVo eventVo;
    private String actName;
    private String actContent;
    private String startDateStr;
    private String endDateStr;
    private int maxTake;
    private int portable;
    private String actUrl;
    private String actId;
    private String actImage;

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleIv = (TextView) findViewById(R.id.tv_center_title);
        eventNameEtv = (EditText)findViewById(R.id.create_event_etv_event_name);
        startDateTv = (TextView) findViewById(R.id.create_event_tv_event_start_date);
        overDateTv = (TextView)findViewById(R.id.create_event_tv_event_over_date);
        eventContentEtv = (EditText)findViewById(R.id.create_event_etv_event_content);
        carryCountEtv = (EditText)findViewById(R.id.create_event_etv_carry_count);
        eventUrlEtv = (EditText)findViewById(R.id.create_event_tv_event_url);
        eventLogoIv = (SimpleDraweeView)findViewById(R.id.create_event_sdv_event_logo);
        isCarryCb = (CheckBox)findViewById(R.id.create_event_cb_carry);
        cutLineTv = (TextView)findViewById(R.id.create_event_tv_cut_line);
        carryInputLayout = (RelativeLayout)findViewById(R.id.create_event_tv_carry_input_layout);
        sureBtn = (Button)findViewById(R.id.sure_btn);
    }

    private void initData(){
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.mipmap.ic_fanhui);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        titleIv.setText("编辑活动");
        timePickerView = new TimePickerView(this, TimePickerView.Type.ALL);
        timePickerView.setTime(new Date());
        timePickerView.setCyclic(false);
        timePickerView.setCancelable(true);
        if(null != getIntent() && null != getIntent().getSerializableExtra("eventVo")){
            eventVo = (EventVo) getIntent().getSerializableExtra("eventVo");
            if(null != eventVo){
                actName = eventVo.getActname();
                actId = eventVo.getActid();
                actContent = eventVo.getActcontent();
                startDateStr = eventVo.getStartdate();
                endDateStr = eventVo.getEnddate();
                maxTake = eventVo.getMaxTake();
                portable = eventVo.getPortable();
                actUrl = eventVo.getActurl();
                actImage = eventVo.getActimage();
                if(!TextUtils.isEmpty(actName)){
                    eventNameEtv.setText(actName);
                }
                if(!TextUtils.isEmpty(actContent)){
                    eventContentEtv.setText(actContent);
                }
                if(!TextUtils.isEmpty(startDateStr)){
                    startDateTv.setText(startDateStr);
                }
                if(!TextUtils.isEmpty(endDateStr)){
                    overDateTv.setText(endDateStr);
                }
                carryCountEtv.setText(""+maxTake);
                if(!TextUtils.isEmpty(actUrl)){
                    eventUrlEtv.setText(actUrl);
                }
                if(!TextUtils.isEmpty(actImage)){
                    int width = (int)getResources().getDimension(R.dimen.contact_logo_height);
                    if(!TextUtils.isEmpty(actImage)){
                        String path = ProtocolUtil.getImageUrlByWidth(this,actImage,width);
                        if(!TextUtils.isEmpty(path)){
                            eventLogoIv.setImageURI(Uri.parse(path));
                        }
                    }
                }
            }
        }
    }

    private void initListeners(){

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });

        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String actNameStr = eventNameEtv.getText().toString();
                String actContentStr = eventContentEtv.getText().toString();
                String maxTakeStr = carryCountEtv.getText().toString();
                String actUrlStr = eventUrlEtv.getText().toString();
                String startDateStr = startDateTv.getText().toString();
                String endDateStr = overDateTv.getText().toString();
                boolean isCarry = isCarryCb.isChecked();
                if(TextUtils.isEmpty(actNameStr)){
                    DialogUtil.getInstance().showCustomToast(EditEventActivity.this,"活动名称不能为空", Gravity.CENTER);
                    return ;
                }
                if(TextUtils.isEmpty(actContentStr)){
                    DialogUtil.getInstance().showCustomToast(EditEventActivity.this,"活动内容不能为空",Gravity.CENTER);
                    return ;
                }
                if(TextUtils.isEmpty(startDateStr)){
                    DialogUtil.getInstance().showCustomToast(EditEventActivity.this,"活动开始时间还没有选",Gravity.CENTER);
                    return ;
                }
                if(TextUtils.isEmpty(endDateStr)){
                    DialogUtil.getInstance().showCustomToast(EditEventActivity.this,"活动结束时间还没有选",Gravity.CENTER);
                    return ;
                }
                if(isCarry){
                    portable = 1;
                }else {
                    portable = 0;
                }
                if(TextUtils.isEmpty(maxTakeStr)){
                    maxTake = 0;
                }else {
                    maxTake = Integer.parseInt(maxTakeStr);
                }
                requestEditEventTask(actNameStr,actContentStr, startDateStr, endDateStr, actImagePath, maxTake, actUrlStr, portable);
            }
        });

        /**
         * 是否携带他人
         */
        isCarryCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked){
                    cutLineTv.setVisibility(View.VISIBLE);
                    carryInputLayout.setVisibility(View.VISIBLE);
                }else {
                    cutLineTv.setVisibility(View.GONE);
                    carryInputLayout.setVisibility(View.GONE);
                }
            }
        });

        //时间选择后回调
        timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                if(isEventStartDate){
                    startDateTv.setText(TimeUtil.getTime(date));
                    startDate = date;
                }else {
                    overDateTv.setText(TimeUtil.getTime(date));
                    endDate = date;
                }
            }
        });

        // 开始时间
        startDateTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SoftInputUtil.hideSoftInputMode(v.getContext(),eventNameEtv);
                isEventStartDate = true;
                timePickerView.show();
            }
        });

        // 结束时间
        overDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoftInputUtil.hideSoftInputMode(view.getContext(),eventNameEtv);
                isEventStartDate = false;
                timePickerView.show();
            }
        });

        //选中活动图片
        eventLogoIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPhotoDialog();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        initView();
        initData();
        initListeners();
    }

    /**
     * 显示选择图片对话框
     *
     */
    public void showPhotoDialog(){

        final Dialog dlg = new Dialog(this, R.style.ActionTheme_DataSheet);
        LinearLayout layout = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.set_actionsheet_dialog, null);
        final int cFullFillWidth = 10000;
        layout.setMinimumWidth(cFullFillWidth);
        Button takeBtn = (Button) layout.findViewById(R.id.dialog_btn_take);
        Button pickBtn = (Button) layout.findViewById(R.id.dialog_btn_pick);
        Button cancelBtn = (Button) layout.findViewById(R.id.dialog_btn_cancel);
        //拍照
        takeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                String photoFileName = System.currentTimeMillis() + ".jpg";
                CacheUtil.getInstance().savePicName(photoFileName);
                i.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(FileUtil.getInstance().getImageTempPath() + photoFileName)));
                startActivityForResult(i, Constants.FLAG_CHOOSE_PHOTO);
                dlg.dismiss();
            }
        });
        //本地选择图片
        pickBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dlg.dismiss();
                Intent intent = new Intent(EditEventActivity.this, MultiImageSelectorActivity.class);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
                startActivityForResult(intent, Constants.FLAG_CHOOSE_IMG);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(layout);
        dlg.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.FLAG_CHOOSE_IMG:// 选择本地图片
                    if (data != null) {
                        List<String> pathList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                        Intent intent = new Intent(EditEventActivity.this,
                                CutActivity.class);
                        intent.putExtra("path", pathList.get(0));
                        startActivityForResult(intent,
                                Constants.FLAG_MODIFY_FINISH);
                    }
                    break;

                case Constants.FLAG_CHOOSE_PHOTO:// 打开照相机
                    Intent intent = new Intent(EditEventActivity.this, CutActivity.class);
                    String photoFileName = CacheUtil.getInstance().getPicName();
                    intent.putExtra("path", FileUtil.getInstance().getImageTempPath()+ photoFileName);
                    startActivityForResult(intent, Constants.FLAG_MODIFY_FINISH);
                    break;

                case Constants.FLAG_MODIFY_FINISH:// 修改完成
                    if (data != null) {
                        actImagePath = data.getStringExtra("path");
                    }else{
                        actImagePath =  FileUtil.getInstance().getImageTempPath() + CacheUtil.getInstance().getPicName();
                    }
                    eventLogoIv.setImageURI(Uri.parse("file://"+actImagePath));
                    break;

                default:
                    break;
            }
        }
    }

    private void requestEditEventTask(String actName,String actContent,String startDate,String endDate,String actImage,int maxTake,String actUrl,int portable){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(50000);
            if(CacheUtil.getInstance().isLogin()){
                client.addHeader("Token",CacheUtil.getInstance().getExtToken());
            }
            String url = null;
            if(TextUtils.isEmpty(actId)){
                url  = ProtocolUtil.getCreateEventUrl();
            }else {
                url = ProtocolUtil.getEditEventUrl(actId);
            }
            RequestParams params = new RequestParams();
            if(!TextUtils.isEmpty(actName)){
                params.put("actname",actName);
            }
            if(!TextUtils.isEmpty(actContent)){
                params.put("actcontent",actContent);
            }
            if(!TextUtils.isEmpty(startDate)){
                params.put("startdate",startDate);
            }
            if(!TextUtils.isEmpty(endDate)){
                params.put("enddate",endDate);
            }
            if(!TextUtils.isEmpty(actImage)){
                params.put("actimage",new File(actImage));
            }
            if(maxTake > 0){
                params.put("maxtake",maxTake);
            }
            if(!TextUtils.isEmpty(actUrl)){
                params.put("acturl",actUrl);
            }
            params.put("portable",portable);
            client.post(url, params, new JsonHttpResponseHandler(){
                public void onStart(){
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(EditEventActivity.this,"");
                }

                public void onFinish(){
                    super.onFinish();
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                    super.onSuccess(statusCode,headers,response);
                    BaseResponse baseResponse = new Gson().fromJson(response.toString(),BaseResponse.class);
                    if(null != baseResponse){
                        int resultCode = baseResponse.getRes();
                        if(0 == resultCode){
                            DialogUtil.getInstance().showCustomToast(EditEventActivity.this,"修改活动成功",Gravity.CENTER);
                            finish();
                        }else {
                            String resultMsg = baseResponse.getResDesc();
                            if(!TextUtils.isEmpty(resultMsg)){
                                DialogUtil.getInstance().showCustomToast(EditEventActivity.this,""+resultMsg,Gravity.CENTER);
                            }
                        }
                    }
                }

                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse){
                    super.onFailure(statusCode,headers,throwable,errorResponse);
                    AsyncHttpClientUtil.onFailure(EditEventActivity.this,statusCode);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

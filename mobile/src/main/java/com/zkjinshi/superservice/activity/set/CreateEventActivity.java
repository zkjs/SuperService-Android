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
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.common.CutActivity;
import com.zkjinshi.superservice.base.BaseAppCompatActivity;
import com.zkjinshi.superservice.response.BaseResponse;
import com.zkjinshi.superservice.utils.AsyncHttpClientUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.FileUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 创建活动
 * 开发者：jimmyzhang
 * 日期：16/6/28
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class CreateEventActivity extends BaseAppCompatActivity {

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
    }

    private void initData(){
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.mipmap.ic_fanhui);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        titleIv.setText("创建活动");
        timePickerView = new TimePickerView(this, TimePickerView.Type.ALL);
        timePickerView.setTime(new Date());
        timePickerView.setCyclic(false);
        timePickerView.setCancelable(true);

    }

    private void initListeners(){

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(android.view.MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_create_event_next://新增事件
                        String actNameStr = eventNameEtv.getText().toString();
                        String actContentStr = eventContentEtv.getText().toString();
                        String maxTakeStr = carryCountEtv.getText().toString();
                        String actUrlStr = eventUrlEtv.getText().toString();
                        boolean isCarry = isCarryCb.isChecked();
                        if(TextUtils.isEmpty(actNameStr)){
                            DialogUtil.getInstance().showCustomToast(CreateEventActivity.this,"活动名称不能为空",Gravity.CENTER);
                            return true;
                        }
                        if(TextUtils.isEmpty(actContentStr)){
                            DialogUtil.getInstance().showCustomToast(CreateEventActivity.this,"活动内容不能为空",Gravity.CENTER);
                            return true;
                        }
                        if(null == startDate){
                            DialogUtil.getInstance().showCustomToast(CreateEventActivity.this,"活动开始时间还没有选",Gravity.CENTER);
                            return true;
                        }
                        if(null == endDate){
                            DialogUtil.getInstance().showCustomToast(CreateEventActivity.this,"活动结束时间还没有选",Gravity.CENTER);
                            return true;
                        }
                        if(TextUtils.isEmpty(actImagePath)){
                            DialogUtil.getInstance().showCustomToast(CreateEventActivity.this,"活动图片没有上传",Gravity.CENTER);
                            return true;
                        }
                        Intent intent = new Intent(CreateEventActivity.this,AddInviteActivity.class);
                        intent.putExtra("actName",actNameStr);
                        intent.putExtra("actContent",actContentStr);
                        intent.putExtra("startDate",TimeUtil.getTime(startDate));
                        intent.putExtra("endDate",TimeUtil.getTime(endDate));
                        if(TextUtils.isEmpty(maxTakeStr)){
                            intent.putExtra("maxTake",0);
                        }else {
                            intent.putExtra("maxTake",Integer.parseInt(maxTakeStr));
                        }
                        if(isCarry){
                            intent.putExtra("portable",1);
                        }else {
                            intent.putExtra("portable",0);
                        }
                        intent.putExtra("actUrl",actUrlStr);
                        intent.putExtra("actImage",actImagePath);
                        startActivity(intent);
                        break;
                }
                return true;
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
                    startDateTv.setText(TimeUtil.getTime(date.getTime()));
                    startDate = date;
                }else {
                    overDateTv.setText(TimeUtil.getTime(date.getTime()));
                    endDate = date;
                }
            }
        });

        // 开始时间
        startDateTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isEventStartDate = true;
                timePickerView.show();
            }
        });

        // 结束时间
        overDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        setContentView(R.layout.activity_create_event);
        initView();
        initData();
        initListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_event, menu);
        return true;
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
                Intent intent = new Intent(CreateEventActivity.this, MultiImageSelectorActivity.class);
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
                        Intent intent = new Intent(CreateEventActivity.this,
                                CutActivity.class);
                        intent.putExtra("path", pathList.get(0));
                        startActivityForResult(intent,
                                Constants.FLAG_MODIFY_FINISH);
                    }
                    break;

                case Constants.FLAG_CHOOSE_PHOTO:// 打开照相机
                    Intent intent = new Intent(CreateEventActivity.this, CutActivity.class);
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
}

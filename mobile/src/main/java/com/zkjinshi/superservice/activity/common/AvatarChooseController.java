package com.zkjinshi.superservice.activity.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zkjinshi.base.util.ImageUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.base.BaseUiController;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.FileUtil;

import java.io.File;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class AvatarChooseController extends BaseUiController{

    private Context context;

    private Bitmap  displayBitmap;
    private String  photoFilePath;

    private static AvatarChooseController instance;

    private AvatarChooseController(){}

    public static synchronized AvatarChooseController getInstance(){
        if(null ==  instance){
            instance = new AvatarChooseController();
        }
        return  instance;
    }

    public void init(Context context){
        this.context = context;
    }

    /** 显示选择图片对话框 */
    public void showChoosePhotoDialog(){

        final Dialog dialog = new Dialog(context, R.style.ActionTheme_DataSheet);
        LinearLayout layout  = (LinearLayout) LayoutInflater.from(context).inflate(
                                              R.layout.set_actionsheet_dialog, null);
        final int cFullFillWidth = 10000;
        layout.setMinimumWidth(cFullFillWidth);
        Button takeBtn   = (Button) layout.findViewById(R.id.dialog_btn_take);
        Button pickBtn   = (Button) layout.findViewById(R.id.dialog_btn_pick);
        Button cancelBtn = (Button) layout.findViewById(R.id.dialog_btn_cancel);

        //拍照
        takeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imageCapture  = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                String photoFileName = System.currentTimeMillis() + ".jpg";
                CacheUtil.getInstance().savePicName(photoFileName);
                imageCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
                        FileUtil.getInstance().getImageTempPath() + photoFileName)));
                ((Activity)context).startActivityForResult(imageCapture, Constants.FLAG_CHOOSE_PHOTO);
                dialog.dismiss();
            }
        });

        //本地选择图片
        pickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(context, MultiImageSelectorActivity.class);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
                ((Activity)context).startActivityForResult(intent, Constants.FLAG_CHOOSE_IMG);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Window w = dialog.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        dialog.onWindowAttributesChanged(lp);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(layout);
        dialog.show();
    }

    /**
     * 选择图片回调
     * @param requestCode
     * @param resultCode
     * @param data
     * @param imageView
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data, ImageView imageView){

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.FLAG_CHOOSE_IMG:// 选择本地图片
                    if (data != null) {
                        List<String> pathList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                        Intent intent = new Intent(context,
                                CutActivity.class);
                        intent.putExtra("path", pathList.get(0));
                        ((Activity)context).startActivityForResult(intent,
                                Constants.FLAG_MODIFY_FINISH);
                    }
                    break;

                case Constants.FLAG_CHOOSE_PHOTO:// 打开照相机
                    Intent intent = new Intent(context, CutActivity.class);
                    String photoFileName = CacheUtil.getInstance().getPicName();
                    intent.putExtra("path", FileUtil.getInstance().getImageTempPath()+ photoFileName);
                    ((Activity)context). startActivityForResult(intent, Constants.FLAG_MODIFY_FINISH);
                    break;

                case Constants.FLAG_MODIFY_FINISH:// 修改完成
                    if (data != null) {
                        photoFilePath = data.getStringExtra("path");
                    }else{
                        photoFilePath =  FileUtil.getInstance().getImageTempPath() + CacheUtil.getInstance().getPicName();
                    }
                    displayBitmap = BitmapFactory.decodeFile(photoFilePath);
                    if (displayBitmap != null) {
                        displayBitmap = ImageUtil.cropThumbBitmap(displayBitmap);
                        displayBitmap = ImageUtil.loadThumbBitmap(context,
                                displayBitmap);
                        imageView.setImageBitmap(displayBitmap);
                    }
                    CacheUtil.getInstance().savePicPath(photoFilePath);
                    break;
            }
        }
    }
}

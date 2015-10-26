package com.zkjinshi.superservice.utils.task;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.zkjinshi.base.util.DeviceUtils;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.ImageUtil;
import com.zkjinshi.superservice.utils.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 压缩图片保存到本地
 * 开发者：dujiande
 * 日期：2015/9/29
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ImgAsyncTask extends AsyncTask<Void,Void,Bitmap> {
    public String photoFilePath;
    public ImageView imageView;
    public Activity activity;
    public CallBack callback;
    private  String savePath = "";

    public interface CallBack{
        public void getNewPath(String path);
    }

    public ImgAsyncTask( Activity activity,String path,ImageView imageView,CallBack callback) {
        this.photoFilePath = path;
        this.imageView = imageView;
        this.activity = activity;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        //第一个执行方法
        DialogUtil.getInstance().showProgressDialog(activity);
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        Bitmap displayBitmap = BitmapFactory.decodeFile(photoFilePath);
        if (displayBitmap != null) {
            int screenHeight = DeviceUtils.getScreenHeight(activity);
            int screanWidth = DeviceUtils.getScreenWidth(activity);
//            int height = (int)(0.29*screenHeight);
//            int width = (int)(0.71*screanWidth);
            int height = 190;
            int width = 270;
            displayBitmap = ImageUtil.cropBitmap(displayBitmap, width, height);

            savePath = FileUtil.getInstance().getImageTempPath() + System.currentTimeMillis() + ".jpg";
            saveBitmap2JPGE(displayBitmap, savePath);
            return displayBitmap;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap displayBitmap) {
        //doInBackground返回时触发，换句话说，就是doInBackground执行完后触发
        //这里的displayBitmap就是上面doInBackground执行后的返回值
        DialogUtil.getInstance().cancelProgressDialog();
        this.imageView.setImageBitmap(displayBitmap);
        callback.getNewPath(savePath);
        super.onPostExecute(displayBitmap);
    }

    public void saveBitmap2JPGE(Bitmap bitmap, String path) {
        File file = new File(path);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

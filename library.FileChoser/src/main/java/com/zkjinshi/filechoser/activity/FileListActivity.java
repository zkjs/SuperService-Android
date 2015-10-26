package com.zkjinshi.filechoser.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zkjinshi.filechoser.R;
import com.zkjinshi.filechoser.adapter.DavAdapter;
import com.zkjinshi.filechoser.adapter.FileAdapter;
import com.zkjinshi.filechoser.listener.RecyclerItemClickListener;

import java.io.File;
import java.util.ArrayList;

/**
 * 文件选择主页面
 * 开发者：JimmyZhang
 * 日期：2015/10/10
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class FileListActivity extends Activity{

    private ImageView backIv;
    private TextView titleTv;
    private ListView fileListView;
    private FileAdapter fileAdapter;
    private DavAdapter davAdapter;
    private File selectedFile;
    private LinearLayout emptyView;
    private RecyclerView navRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<File> dirFileList = new ArrayList<File>();

    public static final String EXTRA_RESULT = "select_result";

    private void initView(){
        backIv = (ImageView)findViewById(R.id.header_bar_btn_back);
        titleTv = (TextView)findViewById(R.id.header_bar_tv_title);
        fileListView = (ListView)findViewById(R.id.file_listview);
        emptyView = (LinearLayout)findViewById(R.id.empty_view);
        navRecyclerView = (RecyclerView)findViewById(R.id.dirHorizonList);
    }

    private void initData(){
        titleTv.setText("浏览目录");
        backIv.setVisibility(View.VISIBLE);
        fileListView.setEmptyView(emptyView);
        selectedFile = Environment.getExternalStorageDirectory();
        fileAdapter = new FileAdapter(this);
        fileAdapter.setCurrentDirectory(selectedFile);
        fileListView.setAdapter(fileAdapter);
        dirFileList.add(selectedFile);
        davAdapter = new DavAdapter(this, dirFileList);
        navRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        navRecyclerView.setLayoutManager(linearLayoutManager);
        navRecyclerView.setAdapter(davAdapter);

    }

    private void initListeners(){

        //返回
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dirFileList.size() > 0) {
                    dirFileList.remove(dirFileList.size() - 1);
                    davAdapter.setFileDirList(dirFileList);
                }
                File dir = fileAdapter.getCurrentDirectory();
                File parent = dir != null ? dir.getParentFile() : null;
                if(null != dir && dir.getAbsoluteFile().equals(Environment.getExternalStorageDirectory())){
                    finish();
                    setResult(RESULT_CANCELED);
                }else{
                    fileAdapter.setCurrentDirectory(parent);
                }
            }
        });

        //文件item
        fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedFile = (File) adapterView.getAdapter().getItem(i);
                fileAdapter.setCurrentDirectory(selectedFile);
                if (null != selectedFile) {
                    if (selectedFile.isDirectory()) {
                        dirFileList.add(selectedFile);
                        davAdapter.setFileDirList(dirFileList);
                    } else {
                        String filePath = selectedFile.getAbsolutePath();
                        Intent intent = getIntent();
                        intent.putExtra(EXTRA_RESULT,filePath);
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                }

            }
        });

        //文件导航
        davAdapter.setOnItemClickListener(new RecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position < dirFileList.size() - 1) {
                    int size = dirFileList.size();
                    selectedFile = (File) dirFileList.get(position);
                    for (int i = position + 1; i < size; i++) {
                        dirFileList.remove(position + 1);
                    }
                    davAdapter.setFileDirList(dirFileList);
                    fileAdapter.setCurrentDirectory(selectedFile);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);
        initView();
        initData();
        initListeners();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (dirFileList.size() > 0) {
                dirFileList.remove(dirFileList.size() - 1);
                davAdapter.setFileDirList(dirFileList);
            }
            File dir = fileAdapter.getCurrentDirectory();
            File parent = dir != null ? dir.getParentFile() : null;
            if(null != dir && dir.getAbsoluteFile().equals(Environment.getExternalStorageDirectory())){
                finish();
                setResult(RESULT_CANCELED);
            }else{
                fileAdapter.setCurrentDirectory(parent);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

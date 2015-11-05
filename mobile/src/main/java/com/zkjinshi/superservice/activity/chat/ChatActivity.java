package com.zkjinshi.superservice.activity.chat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.net.core.WebSocketManager;
import com.zkjinshi.base.net.observer.IMessageObserver;
import com.zkjinshi.base.net.observer.MessageSubject;
import com.zkjinshi.base.net.protocol.ProtocolMSG;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.NetWorkUtil;
import com.zkjinshi.base.util.SoftInputUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.chat.action.FaceViewPagerManager;
import com.zkjinshi.superservice.activity.chat.action.MessageListViewManager;
import com.zkjinshi.superservice.activity.chat.action.MoreViewPagerManager;
import com.zkjinshi.superservice.activity.chat.action.NetCheckManager;
import com.zkjinshi.superservice.activity.chat.action.QuickMenuManager;
import com.zkjinshi.superservice.activity.chat.action.VoiceRecordManager;
import com.zkjinshi.superservice.activity.set.EmployeeAddActivity;
import com.zkjinshi.superservice.activity.set.TeamEditActivity;
import com.zkjinshi.superservice.bean.BookOrderBean;
import com.zkjinshi.superservice.sqlite.ChatRoomDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.FileUtil;
import com.zkjinshi.superservice.utils.MediaPlayerUtil;
import com.zkjinshi.superservice.vo.ChatRoomVo;
import com.zkjinshi.superservice.vo.IdentityType;
import com.zkjinshi.superservice.vo.OnlineStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * 聊天主页面
 * 开发者：JimmyZhang
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ChatActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private final static String TAG = ChatActivity.class.getSimpleName();

    private String        mUserID;
    private String        mShopID;
    private String        mSessionID;
    private String        mClientID;
    private BookOrderBean bookOrder;

    private Toolbar       mToolbar;
    private TextView      mTvCenterTitle;
    private TextView      mTvBottomTitle;

    private EditText mMsgTextInput;
    private Button mBtnMsgSend;

    private MessageListViewManager messageListViewManager;
    private boolean               isShowSoftInput;//是否展示软件盘

    private boolean isVoiceShow = false;//是否显示语音

    private LinearLayout faceLinearLayout, moreLinearLayout;
    private FaceViewPagerManager facePagerManager;
    private MoreViewPagerManager moreViewPagerManager;
    private VoiceRecordManager voiceRecordManager;
    private NetCheckManager netCheckManager;
    private CheckBox faceCb, moreCb;

    //音频操作
    private ImageButton toggleAudioBtn;//切换到录音按钮
    private TextView startAudioBtn;//开始录音
    private RelativeLayout animAreaLayout, cancelAreaLayout; // 录音中View，取消录音View
    private int            flag = 1; // 1：正常 2：语音录音中
    private long           startVoiceT, endVoiceT; // 语音开始时间，结束时间

    private String            mRuleType;
    private String            choosePicName;//选择图片名称
    private ArrayList<String> chooseImageList = new ArrayList<>();
    private String            bookOrderStr;

    private ChatRoomVo mChatRoom;

    private String remoteAction;
    private String remoteMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initView();
        initData();
        initListener();
    }

    private void initView() {

        DialogUtil.getInstance().cancelProgressDialog();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.drawable.ic_fanhui);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTvCenterTitle = (TextView) findViewById(R.id.tv_center_title);
        mTvBottomTitle = (TextView) findViewById(R.id.tv_online_status);

        mMsgTextInput = (EditText) findViewById(R.id.et_msg_text_input);
        mBtnMsgSend   = (Button)   findViewById(R.id.btn_msg_send);
        faceLinearLayout = (LinearLayout) findViewById(R.id.face_ll);
        moreLinearLayout = (LinearLayout) findViewById(R.id.more_ll);
        faceCb = (CheckBox) findViewById(R.id.face_cb);
        moreCb = (CheckBox) findViewById(R.id.more_cb);
        toggleAudioBtn   = (ImageButton) findViewById(R.id.voice_btn);
        startAudioBtn    = (TextView) findViewById(R.id.say_btn);
        animAreaLayout   = (RelativeLayout) findViewById(R.id.voice_rcd_hint_anim_area);
        cancelAreaLayout = (RelativeLayout) findViewById(R.id.voice_rcd_hint_cancel_area);
    }

    private void initData() {
        mSessionID = getIntent().getStringExtra("session_id");
        mShopID    = getIntent().getStringExtra("shop_id");
        String sessionName = getIntent().getStringExtra("session_name");
        int onlineStatus = getIntent().getIntExtra("online_status", 1);

        if(!TextUtils.isEmpty(sessionName)){
            mTvCenterTitle.setText(getString(R.string.with) + sessionName + (getString(R.string.chating)));
        }else{
            ChatRoomVo chatRoomVo = ChatRoomDBUtil.getInstance().queryChatRoomBySessionId(mSessionID);
            if(null != chatRoomVo){
                String title = chatRoomVo.getTitle();
                if(!TextUtils.isEmpty(title)){
                    mTvCenterTitle.setText(getString(R.string.with) + title + (getString(R.string.chating)));
                }
            }
        }

        if(onlineStatus == OnlineStatus.ONLINE.getValue()){
            mTvBottomTitle.setText("对象"+ getString(R.string.online));
        }else {
            mTvBottomTitle.setText("对象"+ getString(R.string.offline));
        }

        mChatRoom = ChatRoomDBUtil.getInstance().queryChatRoomBySessionId(mSessionID);
        if(null != mChatRoom){
            if(TextUtils.isEmpty(mShopID)){
                mShopID    = mChatRoom.getShopId();
            }
            mClientID  = mChatRoom.getCreaterId();
        }
        mUserID    = CacheUtil.getInstance().getUserId();
        bookOrder  = (BookOrderBean) getIntent().getSerializableExtra("bookOrder");
        mRuleType  = getString(R.string.default_rule_type);
        //初始化消息ListView管理器
        messageListViewManager = new MessageListViewManager(this, mShopID, mSessionID, mClientID);
        messageListViewManager.init();
        //初始化表情框
        facePagerManager = new FaceViewPagerManager(this, faceLinearLayout, mMsgTextInput);
        facePagerManager.init();
        //初始化更多框
        moreViewPagerManager = new MoreViewPagerManager(this, moreLinearLayout);
        moreViewPagerManager.init();
        //初始化录音管理器
        voiceRecordManager = new VoiceRecordManager(this, animAreaLayout, cancelAreaLayout);
        voiceRecordManager.init();
        //网络设置
        netCheckManager = new NetCheckManager();
        netCheckManager.init(this);
        netCheckManager.registernetCheckReceiver();
        //初始化快捷菜单
        QuickMenuManager.getInstance().init(this).setMessageListViewManager(messageListViewManager);
        if (null != bookOrder) {
            bookOrderStr = new Gson().toJson(bookOrder);
            if (!TextUtils.isEmpty(bookOrderStr)) {
                messageListViewManager.sendBookTextMessage(bookOrderStr);
            }
        }
        //增加远程手表语音自动回复
        remoteAction = getIntent().getAction();
        if(!TextUtils.isEmpty(remoteAction) && remoteAction.equals(Constants.ACTION_VOICE_RELAY)){
            remoteMessage = getRemoteInputText(getIntent()).toString();
            if(!TextUtils.isEmpty(remoteMessage)){
                messageListViewManager.sendTextMessage(remoteMessage);
                remoteMessage = null;
            }
        }
    }

    private void initListener() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatActivity.this.finish();
            }
        });

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(android.view.MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add_chat_object:
                        break;

                    case R.id.start_group_chat:
                        break;

                    case R.id.transfer_chat_to_others:
                        break;

                    case R.id.offline_to_this_chat:
                        break;

                    case R.id.finish_this_chat:
                        //执行解散回话操作
                        messageListViewManager.sendDisableSession();
                        break;
                }
                return true;
            }
        });

        faceCb.setOnCheckedChangeListener(this);
        moreCb.setOnCheckedChangeListener(this);

        //输入文本控件touch监听
        mMsgTextInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideFaceLayout();
                hideMoreLayout();
                showSoftInput();
                return false;
            }
        });
        //语音键盘切换监听
        toggleAudioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isVoiceShow) {
                    toggleAudioBtn.setImageResource(R.drawable.aio_keyboard);
                    if (moreCb.isChecked()) {
                        Drawable restdrawable = getResources().getDrawable(
                                R.drawable.aio_fold);
                        restdrawable.setBounds(0, 0,
                                restdrawable.getMinimumWidth(),
                                restdrawable.getMinimumHeight());
                        moreCb.setCompoundDrawables(restdrawable, null, null, null);
                    }
                    if (faceCb.isChecked()) {
                        Drawable restdrawable = getResources().getDrawable(
                                R.drawable.aio_favorite);
                        restdrawable.setBounds(0, 0,
                                restdrawable.getMinimumWidth(),
                                restdrawable.getMinimumHeight());
                        faceCb.setCompoundDrawables(restdrawable, null, null, null);
                    }

                    mMsgTextInput.setVisibility(View.GONE);
                    faceCb.setVisibility(View.VISIBLE);
                    startAudioBtn.setVisibility(View.VISIBLE);
                    hideFaceLayout();
                    hideMoreLayout();
                    hideSoftInput();
                    isVoiceShow = true;
                } else {
                    toggleAudioBtn.setImageResource(R.drawable.aio_voice);
                    mMsgTextInput.setVisibility(View.VISIBLE);
                    faceCb.setVisibility(View.VISIBLE);
                    startAudioBtn.setVisibility(View.GONE);
                    showSoftInput();
                    isVoiceShow = false;
                }
            }
        });

        //文本控件输入监听
        mMsgTextInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mBtnMsgSend.setEnabled(true);
                    mBtnMsgSend.setVisibility(VISIBLE);
                    moreCb.setVisibility(GONE);
                } else {
                    mBtnMsgSend.setEnabled(false);
                    mBtnMsgSend.setVisibility(GONE);
                    moreCb.setVisibility(VISIBLE);
                }
            }
        });

        mBtnMsgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = mMsgTextInput.getText().toString();
                if (!TextUtils.isEmpty(msg) && msg.length() < 200) {
                    messageListViewManager.sendTextMessage(mShopID, msg, mRuleType);
                    mMsgTextInput.setText("");
                } else {
                    DialogUtil.getInstance().showCustomToast(ChatActivity.this, "发送消息不能超过200字符!", Gravity.CENTER);
                }
            }
        });

        // 触摸ListView隐藏表情和输入法
        messageListViewManager.getMessageListView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    resetKeyboard();
                }
                return false;
            }
        });

        //录音事件交给父控件处理
        startAudioBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_chat, menu);
        return true;
    }


    public void resetKeyboard(){
        hideFaceLayout();
        hideMoreLayout();
        hideSoftInput();
        Drawable plusawable = getResources().getDrawable(
                R.drawable.aio_fold);
        plusawable.setBounds(0, 0, plusawable.getMinimumWidth(),
                plusawable.getMinimumHeight());
        moreCb.setCompoundDrawables(plusawable, null, null, null);
        Drawable facedrawable = getResources().getDrawable(
                R.drawable.aio_favorite);
        facedrawable.setBounds(0, 0,
                facedrawable.getMinimumWidth(),
                facedrawable.getMinimumHeight());
        faceCb.setCompoundDrawables(facedrawable, null, null, null);
    }

    @Override
    public void onBackPressed() {
        if (!faceCb.isChecked() && !moreCb.isChecked()) {
            finish();
        } else {
            hideFaceLayout();
            hideMoreLayout();
        }
    }

    /**
     * 隐藏表情区域
     */
    private void hideFaceLayout() {
        faceLinearLayout.setVisibility(GONE);
        faceCb.setOnCheckedChangeListener(null);
        faceCb.setChecked(false);
        faceCb.setOnCheckedChangeListener(ChatActivity.this);
    }

    /**
     * 隐藏更多区域
     */
    private void hideMoreLayout() {
        moreLinearLayout.setVisibility(GONE);
        moreCb.setOnCheckedChangeListener(null);
        moreCb.setChecked(false);
        moreCb.setOnCheckedChangeListener(ChatActivity.this);
    }

    /**
     * 隐藏软键盘
     */
    private void hideSoftInput() {
        isShowSoftInput = false;
        SoftInputUtil.hideSoftInputMode(this, mMsgTextInput);
    }

    /**
     * 显示软键盘并显示最底部消息
     */
    private void showSoftInput() {
        isShowSoftInput = true;
        mMsgTextInput.requestFocus();// 强制显示软键盘
        SoftInputUtil.showSoftInputMode(this, mMsgTextInput);
        messageListViewManager.scrollBottom();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.FLAG_CHOOSE_IMG) {//选择图片
                chooseImageList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                if (null != chooseImageList && !chooseImageList.isEmpty()) {
                    for (String imagePath : chooseImageList) {
                        Log.i(TAG, "imagePath:" + imagePath);
                        String imageName = FileUtil.getInstance().getFileName(imagePath);
                        messageListViewManager.sendImageMessage(mShopID, imageName,imagePath, mRuleType);
                    }
                }
            } else if (requestCode == Constants.FLAG_CHOOSE_PHOTO) {//拍照上传
                choosePicName = CacheUtil.getInstance().getPicName();
                String imageTempPath = FileUtil.getInstance().getImageTempPath();
                messageListViewManager.sendImageMessage(mShopID, choosePicName, imageTempPath + choosePicName, mRuleType);
                Log.i(TAG, "imagePath:" + FileUtil.getInstance().getImageTempPath() + choosePicName);
            }
        }
    }

    @Override
    protected void onDestroy() {
        messageListViewManager.destoryMessageListViewManager();
        MediaPlayerUtil.stop();
        if(null != netCheckManager){
            netCheckManager.unregisternetCheckReceiver();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        MediaPlayerUtil.stop();
        super.onResume();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.face_cb) {
            if (moreCb.isChecked()) {
                Drawable restdrawable = getResources().getDrawable(
                        R.drawable.aio_fold);
                restdrawable.setBounds(0, 0, restdrawable.getMinimumWidth(),
                        restdrawable.getMinimumHeight());
                moreCb.setCompoundDrawables(restdrawable, null, null, null);
            }
            if (!isChecked) {
                Drawable drawable = getResources().getDrawable(
                        R.drawable.aio_favorite);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                        drawable.getMinimumHeight());
                faceCb.setCompoundDrawables(drawable, null, null, null);
                faceLinearLayout.setVisibility(GONE);
                moreLinearLayout.setVisibility(GONE);
                showSoftInput();
            } else {
                Drawable drawable = getResources().getDrawable(
                        R.drawable.aio_keyboard);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                        drawable.getMinimumHeight());
                faceCb.setCompoundDrawables(drawable, null, null, null);
                toggleAudioBtn.setImageResource(R.drawable.aio_voice);
                mMsgTextInput.setVisibility(VISIBLE);
                faceCb.setVisibility(VISIBLE);
                startAudioBtn.setVisibility(GONE);
                isVoiceShow = false;
                hideMoreLayout();
                if (isShowSoftInput) {
                    hideSoftInput();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            faceLinearLayout.setVisibility(VISIBLE);
                        }
                    }, 100);
                } else {
                    faceLinearLayout.setVisibility(VISIBLE);
                }
            }
        } else if (id == R.id.more_cb) {
            if (faceCb.isChecked()) {
                Drawable restdrawable = getResources().getDrawable(
                        R.drawable.aio_favorite);
                restdrawable.setBounds(0, 0, restdrawable.getMinimumWidth(),
                        restdrawable.getMinimumHeight());
                faceCb.setCompoundDrawables(restdrawable, null, null, null);
            }
            if (!isChecked) {
                faceLinearLayout.setVisibility(GONE);
                moreLinearLayout.setVisibility(GONE);
                Drawable drawable = getResources().getDrawable(
                        R.drawable.aio_fold);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                        drawable.getMinimumHeight());
                moreCb.setCompoundDrawables(drawable, null, null, null);
                showSoftInput();
            } else {
                toggleAudioBtn.setImageResource(R.drawable.aio_voice);
                mMsgTextInput.setVisibility(VISIBLE);
                faceCb.setVisibility(VISIBLE);
                startAudioBtn.setVisibility(GONE);
                Drawable drawable = getResources().getDrawable(
                        R.drawable.aio_keyboard);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                        drawable.getMinimumHeight());
                moreCb.setCompoundDrawables(drawable, null, null, null);
                isVoiceShow = false;
                hideFaceLayout();
                if (isShowSoftInput) {
                    hideSoftInput();
                    moreViewPagerManager.showMoreViewPager();
                } else {
                    moreLinearLayout.setVisibility(VISIBLE);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!Environment.getExternalStorageDirectory().exists()) {
            Toast.makeText(this, "No SDCard", Toast.LENGTH_LONG).show();
            return false;
        }

        if (isVoiceShow) {
            int[] location = new int[2];
            startAudioBtn.getLocationInWindow(location);
            int btn_rc_Y = location[1];
            int btn_rc_X = location[0];

            // 获得录音空间高度
            float btnHeight = startAudioBtn.getHeight();
            int[] anim_location = new int[2];
            animAreaLayout.getLocationInWindow(anim_location);
            int anim_Y = anim_location[1];
            int anim_X = anim_location[0];
            if (event.getAction() == MotionEvent.ACTION_DOWN && flag == 1) {
                /** 判断手势按下位置是否是语音录制按钮的范围内 开始录音 */
                if (event.getY() > btn_rc_Y &&  event.getY() < (btn_rc_Y + btnHeight) && event.getX() > btn_rc_X) {
                    /** 播放开始录音提示音 */
                    MediaPlayerUtil.playStartRecordVoice(ChatActivity.this);
                    startAudioBtn.setText(R.string.chatfooter_releasetofinish);
                    startAudioBtn.setBackgroundResource(R.drawable.cm_btn_bg_pressed);
                    startAudioBtn.setTextColor(Color.WHITE);
                    startVoiceT = System.currentTimeMillis();
                    voiceRecordManager.start();// 开始录音
                    flag = 2;
                }
            } else {
                /** 松开手势时执行录制完成 */
                if (event.getAction() == MotionEvent.ACTION_UP && flag == 2) {
                    startAudioBtn.setText(R.string.chatfooter_presstorcd);
                    startAudioBtn.setBackgroundResource(R.drawable.cm_btn_bg_normal);
                    startAudioBtn.setTextColor(Color.BLACK);
                    voiceRecordManager.stopRecordCountDown();
                    flag = 1;
                    boolean isCountDown = CacheUtil.getInstance().isCountDown();
                    if (!isCountDown) {
                        if (event.getY() < btn_rc_Y - 100) {
                            voiceRecordManager.stop();
                        } else {
                            voiceRecordManager.stop();
                            endVoiceT = System.currentTimeMillis();
                            int voiceTime = (int) ((endVoiceT - startVoiceT) / 1000);
                            if (voiceTime < 2) {
                                CacheUtil.getInstance().setVoiceTooShort(true);
                                voiceRecordManager.showRecordShortLayout();
                                /** 删除过短音频文件 */
                                String mediaPath = voiceRecordManager.getMediaPath();//音频文件路径
                                File tooShortMedia = new File(mediaPath);
                                if(tooShortMedia.exists()){
                                    tooShortMedia.delete();
                                }
                                return false;
                            }
                            MediaPlayerUtil.playSendOverRecordVoice(ChatActivity.this);// 录音结束提示音
                            //开始文件写入
                            String mediaPath = voiceRecordManager.getMediaPath();//音频文件路径
                            String mediaName = FileUtil.getInstance().getFileName(mediaPath);//音频文件名
                            messageListViewManager.sendAudioMessage( mShopID, mediaName, mediaPath, voiceTime, mRuleType);
                        }
                    } else {
                        CacheUtil.getInstance().setCountDown(false);
                    }
                } else if (event.getAction() == MotionEvent.ACTION_MOVE && flag == 2) {// 取消录音
                    Log.i("rcd", "ACTION_MOVE and rcding");
                    startAudioBtn.setBackgroundResource(R.drawable.cm_btn_bg_pressed);
                    startAudioBtn.setTextColor(Color.WHITE);
                    if (event.getY() < btn_rc_Y - 100) {// 手势按下的位置不在语音录制按钮的范围内
                        startAudioBtn.setText(R.string.chatfooter_cancel_tips);
                        animAreaLayout.setVisibility(View.GONE);
                        cancelAreaLayout.setVisibility(View.VISIBLE);
                        if (event.getY() >= anim_Y
                                && event.getY() <= anim_Y
                                + animAreaLayout.getHeight()
                                && event.getX() >= anim_X
                                && event.getX() <= anim_X
                                + animAreaLayout.getWidth()) {
                        }
                    } else {
                        startAudioBtn.setText(R.string.chatfooter_releasetofinish);
                        animAreaLayout.setVisibility(View.VISIBLE);
                        cancelAreaLayout.setVisibility(View.GONE);
                    }
                }
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 获取远程wear端输入文本
     * @param intent
     * @return
     */
    private CharSequence getRemoteInputText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(Constants.EXTRA_VOICE_REPLY);
        }
        return null;
    }
}

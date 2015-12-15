package com.zkjinshi.superservice.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.utils.Constants;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private final static String TAG = WXEntryActivity.class.getSimpleName();
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        api = WXAPIFactory.createWXAPI(this, Constants.WECHAT_APP_ID, false);
        api.handleIntent(getIntent(), this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onReq(BaseReq req) { }

    @Override
    public void onResp(BaseResp resp) {
        LogUtil.getInstance().info(LogLevel.INFO, "resp.errCode:" + resp.errCode + ",resp.errStr:" + resp.errStr);
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                //分享成功
                DialogUtil.getInstance().showCustomToast(WXEntryActivity.this, TAG+"微信分享成功", Gravity.CENTER);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //分享取消
                DialogUtil.getInstance().showCustomToast(WXEntryActivity.this, TAG+"微信发送被取消", Gravity.CENTER);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                //分享拒绝
                DialogUtil.getInstance().showCustomToast(WXEntryActivity.this, TAG+"微信分享被拒绝", Gravity.CENTER);
                break;
        }
        this.finish();
    }
}
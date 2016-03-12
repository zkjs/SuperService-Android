package com.zkjinshi.superservice.test;

import android.test.AndroidTestCase;
import android.util.Log;
import android.widget.Toast;

import com.zkjinshi.base.util.Constants;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.utils.AESUtil;

import java.security.GeneralSecurityException;

/**
 * 开发者：JimmyZhang
 * 日期：2016/2/26
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class JWTTest extends AndroidTestCase {

    public void testJWT(){
        Log.i("info","JWT");

        try {
            String mobile = "15815507102";
            String encryptData = AESUtil.encrypt(mobile,AESUtil.PAVO_KEY);
            //nrEqYHd3Px7JmFbcI2Aiig==
            Log.i(Constants.ZKJINSHI_BASE_TAG,"encryptData:"+encryptData);

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

    }


}

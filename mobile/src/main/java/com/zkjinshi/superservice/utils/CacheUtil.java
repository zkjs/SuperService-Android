package com.zkjinshi.superservice.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.zkjinshi.superservice.vo.IdentityType;

import java.util.ArrayList;

/**
 * 缓存工具类
 * 开发者：JimmyZhang
 * 日期：2015/7/20
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */

public class CacheUtil {

	private static final String SVIP_CACHE = "super_service_cache";

	private CacheUtil() {
	}

	private static CacheUtil instance;

	public synchronized static CacheUtil getInstance() {
		if (null == instance) {
			instance = new CacheUtil();
		}
		return instance;
	}

	private Context context;

	public void init(Context context) {
		this.context = context;
	}

	/**
	 * 设置用户登录状态
	 * @param isLogin
	 */
	public void setLogin(boolean isLogin) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putBoolean("is_login", isLogin).commit();
	}

	/**
	 * 获取用户登录状态
	 * @return
	 */
	public boolean isLogin() {
		if (null == context) {
			return false;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getBoolean("is_login", false);
	}

	/**
	 * 设置指引状态
	 * @param isGuide
	 */
	public void setGuide(boolean isGuide) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putBoolean("is_guide", isGuide).commit();
	}

	/**
	 * 获取指引状态
	 * @return
	 */
	public boolean isGuide() {
		if (null == context) {
			return false;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getBoolean("is_guide", false);
	}

	/**
	 * 保存登录token
	 * @param token
	 */
	public void setToken(String token) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putString("token", token).commit();
	}

	/**
	 * 获取登录token
	 * @return
	 */
	public String getToken() {
		if (null == context) {
			return null;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getString("token", null);
	}

	/**
	 * 保存用户id
	 * @param userId
	 */
	public void setUserId(String userId) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putString("userId", userId).commit();
	}

	/**
	 * 获取用户id
	 * @return
	 */
	public String getUserId() {
		if (null == context) {
			return null;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getString("userId", null);
	}

	/**
	 * 保存用户角色类型
	 * @param roleID
	 */
	public void setRoleID(String roleID) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putString("roleID", roleID).commit();
	}

	/**
	 * 获取用户角色类型
	 * @return
	 */
	public String getRoleID() {
		if (null == context) {
			return null;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getString("roleID", null);
	}

	/**
	 * 保存用户姓名
	 * @param userName
	 */
	public void setUserName(String userName) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putString("userName", userName).commit();
	}

	/**
	 * 获取用户姓名
	 * @return
	 */
	public String getUserName() {
		if (null == context) {
			return null;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getString("userName","");
	}

	/**
	 * 保存用户手机号
	 * @param mobilePhone
	 */
	public void setUserPhone(String mobilePhone) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putString("mobilePhone", mobilePhone).commit();
	}

	/**
	 * 获取用户手机号
	 * @return
	 */
	public String getUserPhone() {
		if (null == context) {
			return null;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getString("mobilePhone","");
	}

	public void savePicName( String picName) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
				Context.MODE_PRIVATE);
		sp.edit().putString("picName", picName).commit();
	}

	public String getPicName() {
		if (null == context) {
			return null;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
				Context.MODE_PRIVATE);
		return sp.getString("picName", "");
	}

	public void savePicPath( String picPath) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
				Context.MODE_PRIVATE);
		sp.edit().putString("picPath", picPath).commit();
	}

	public String getPicPath() {
		if (null == context) {
			return null;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
				Context.MODE_PRIVATE);
		return sp.getString("picPath", "");
	}

	public void saveAudioPath(String audioPath) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
				Context.MODE_PRIVATE);
		sp.edit().putString("audioPath", audioPath).commit();
	}

	public String getAudioPath() {
		if (null == context) {
			return null;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getString("audioPath", "");
	}

	public void saveUserPhotoUrl( String userPhotoUrl) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
				Context.MODE_PRIVATE);
		sp.edit().putString("user_photo_url", userPhotoUrl).commit();
	}

	public String getUserPhotoUrl() {
		if (null == context) {
			return null;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
				Context.MODE_PRIVATE);
		return sp.getString("user_photo_url", "");
	}

	public void saveTagsOpen(boolean isOpen){
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
				Context.MODE_PRIVATE);
		sp.edit().putBoolean("tagsopen", isOpen).commit();
	}

	public boolean getTagsOpen() {
		if (null == context) {
			return true;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
				Context.MODE_PRIVATE);
		return sp.getBoolean("tagsopen", true);
	}

	public void setOnline(boolean isOnLine){
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
				Context.MODE_PRIVATE);
		sp.edit().putBoolean("online", isOnLine).commit();
	}

	public boolean getOnline() {
		if (null == context) {
			return true;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
				Context.MODE_PRIVATE);
		return sp.getBoolean("online", true);
	}

	/**
	 * 设置开始录音倒计时
	 * @param isCountDown
	 */
	public void setCountDown(boolean isCountDown) {
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putBoolean("count_down", isCountDown).commit();
	}

	/**
	 * 是否处于录音倒计时
	 * @return
	 */
	public boolean isCountDown() {
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getBoolean("count_down", false);
	}

	/**
	 * 是否录音时间过短
	 * @return
	 */
	public boolean isVoiceTooShort() {
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getBoolean("voice_too_short", false);
	}

	/**
	 * 设置录音时间过短
	 * @return
	 */
	public void setVoiceTooShort(boolean voiceTooShort) {
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putBoolean("voice_too_short", voiceTooShort).commit();
	}

	public void setShopID(String shopID) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putString("shop_id", shopID).commit();
	}

	public String getShopID() {
		if (null == context) {
			return null;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getString("shop_id", null);
	}

	public void setCurrentItem(int currentItem) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putInt("currentItem", currentItem).commit();
	}

	public int getCurrentItem() {
		if (null == context) {
			return 0;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getInt("currentItem", 0);
	}


	public void setShopFullName(String shopFullName) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putString("shop_full_name", shopFullName).commit();
	}

	public String getShopFullName() {
		if (null == context) {
			return null;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getString("shop_full_name", null);
	}

	/**
	 *	设置登录身份
	 * @param identityType
	 */
	public void setLoginIdentity(IdentityType identityType) {
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putInt("login_identity", identityType.getVlaue()).commit();
	}

	/**
	 * 获取登录身份
	 * @return
	 */
	public IdentityType getLoginIdentity() {
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		int identityValue =  sp.getInt("login_identity", 0);
		if(identityValue == IdentityType.BUSINESS.getVlaue()){
			return IdentityType.BUSINESS;
		}
		return IdentityType.WAITER;
	}

	/**
	 * 设置区域信息
	 * 多个区域id用,号分割
	 * @param areaInfo
	 */
	public void setAreaInfo(String areaInfo) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putString("area_info", areaInfo).commit();
	}

	/**
	 * 获取区域信息
	 * @return
	 */
	public String getAreaInfo() {
		if (null == context) {
			return null;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getString("area_info", "");
	}

	/**
	 * 设置密码
	 *
	 * @param password
	 */
	public void setPassword(String password) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putString("password", password).commit();
	}

	/**
	 * 获取密码
	 * @return
	 */
	public String getPassword() {
		if (null == context) {
			return null;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getString("password", "");
	}

	/**
	 * 加密存入缓存
	 *
	 * @param cacheObj
	 */
	public void saveObjCache(Object cacheObj) {
		if (null != cacheObj) {
			Gson gson = new Gson();
			String json = gson.toJson(cacheObj);
			String key = cacheObj.getClass().getSimpleName();
			try {
				String encryptedData = Base64Encoder.encode(json);// base 64加密
				SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
						Context.MODE_PRIVATE);
				sp.edit().putString(key, encryptedData).commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 解密取出缓存对象
	 *
	 * @param cacheObj
	 * @return
	 */
	public Object getObjCache(Object cacheObj) {
		if (null == cacheObj) {
			return null;
		}
		if (null != cacheObj) {
			SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
					Context.MODE_PRIVATE);
			String key = cacheObj.getClass().getSimpleName();
			String value = "";
			String encryptedData = sp.getString(key, "");
			if (!TextUtils.isEmpty(encryptedData)) {
				try {
					value = Base64Decoder.decode(encryptedData);
					Gson gson = new Gson();
					cacheObj = gson.fromJson(value, cacheObj.getClass());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return cacheObj;
	}

	/**
	 *  存入集合缓存的通用方法
	 * @param key
	 * @param cacheList
	 * @param <T>
	 */
	public <T> void saveListCache(String key,
								  ArrayList<T> cacheList) {
		if (null != cacheList && cacheList.size() > 0) {
			Gson gson = new Gson();
			String json = gson.toJson(cacheList);
			try {
				String encryptedData = Base64Encoder.encode(json);// base
				// 64加密
				SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
						Context.MODE_PRIVATE);
				sp.edit().putString(key, encryptedData).commit();
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("info", "saveListCache Exception:" + e);
			}
		}
	}

	/**
	 * 取集合缓存的通用方法
	 * @param key
	 * @return
	 */
	public String getListStrCache(String key) {
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
				Context.MODE_PRIVATE);
		String value = "";
		String encryptedData = sp.getString(key, "");
		if (!TextUtils.isEmpty(encryptedData)) {
			try {
				value = Base64Decoder.decode(encryptedData);// base 64解密
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("info", "getListCache Exception:" + e);
			}
		}
		return value;
	}

}

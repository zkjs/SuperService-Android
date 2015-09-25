package com.zkjinshi.superservice.vo;

/**
 * 排序对象
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SortModel extends ContactVo {

	private ContactType contactType;
	private String      avatarUrl;
	private String 		 sortLetters; //显示数据拼音的首字母
	private SortToken   sortToken;

	public SortModel() {
	}

	public ContactType getContactType() {
		return contactType;
	}

	public void setContactType(ContactType contactType) {
		this.contactType = contactType;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public SortToken getSortToken() {
		if(null == sortToken){
			sortToken = new SortToken();
		}
		return sortToken;
	}

	public void setSortToken(SortToken sortToken) {
		this.sortToken = sortToken;
	}


}

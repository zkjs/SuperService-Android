package com.zkjinshi.superservice.activity.common.contact;

/**
 * 排序对象
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SortModel extends Contact {

	public SortModel(long contactID, String name, String number, String sortKey) {
		super(contactID, name, number, sortKey);
	}

	public String sortLetters; //显示数据拼音的首字母

	public SortToken sortToken = new SortToken();
}

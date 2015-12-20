package com.zkjinshi.superservice.utils;

import com.zkjinshi.superservice.vo.ContactVo;

import java.util.Comparator;
/**
 * 拼音字符串比较器
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class PinyinComparator implements Comparator<ContactVo> {

	public int compare(ContactVo o1, ContactVo o2) {

		String letter1 = o1.getFirstLetter();
		String letter2 = o2.getFirstLetter();
		if(!letter1.equals(letter2)){
			return letter1.compareTo(letter2);
		}

		String sortKey1 = o1.getSortKey();
		String sortKey2 = o2.getSortKey();
		if(!letter1.equals(letter2)){
			return sortKey1.compareTo(sortKey2);
		}

		String name1 = o1.getName();
		String name2 = o2.getName();

		if(!name1.equals(name2)){
			return name1.compareTo(name2);
		}

		return o1.getNumber().compareTo(o2.getNumber());
	}

}

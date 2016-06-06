package com.zkjinshi.superservice.pad.utils;

import android.text.TextUtils;

import com.zkjinshi.superservice.pad.vo.ClientContactVo;

import java.util.Comparator;

/**
 * 拼音字符串比较器
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientComparator implements Comparator<ClientContactVo> {

	public int compare(ClientContactVo o1, ClientContactVo o2) {

		String letter1 = o1.getFirstLetter();
		String letter2 = o2.getFirstLetter();

		if(!TextUtils.isEmpty(letter1) && !TextUtils.isEmpty(letter2)){
			if(!letter1.equals(letter2)){
				return letter1.compareTo(letter2);
			}
		}

		String sortKey1 = o1.getSortKey();
		String sortKey2 = o2.getSortKey();
		if(!TextUtils.isEmpty(sortKey1) && !TextUtils.isEmpty(sortKey2)){
			if(!sortKey1.equals(sortKey2)){
				return sortKey1.compareTo(sortKey2);
			}
		}

		String name1 = o1.getUsername();
		String name2 = o2.getUsername();

        if(!TextUtils.isEmpty(name1) && !TextUtils.isEmpty(name2)){
            if(!name1.equals(name2)){
                return name1.compareTo(name2);
            }
        }

		String phone1 = o1.getPhone();
		String phone2 = o2.getPhone();
		if(!TextUtils.isEmpty(phone1) && !TextUtils.isEmpty(phone2)){
			return  phone1.compareTo(phone2);
		}

		return 0;
	}

}

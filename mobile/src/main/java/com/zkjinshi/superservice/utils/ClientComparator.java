package com.zkjinshi.superservice.utils;

import com.zkjinshi.superservice.vo.ClientContactVo;

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

		if(null != letter1 && null != letter2){
			if(!letter1.equals(letter2)){
				return letter1.compareTo(letter2);
			}
		}

		String sortKey1 = o1.getSortKey();
		String sortKey2 = o2.getSortKey();
		if(null != sortKey1 && null != sortKey2){
			if(!sortKey1.equals(sortKey2)){
				return sortKey1.compareTo(sortKey2);
			}
		}

		String name1 = o1.getFname();
		String name2 = o2.getFname();

        if(null != name1 && null != name2){
            if(!name1.equals(name2)){
                return name1.compareTo(name2);
            }
        }

		return o1.getPhone().compareTo(o2.getPhone());
	}

}

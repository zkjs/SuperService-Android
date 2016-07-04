package com.zkjinshi.superservice.view;

import com.zkjinshi.superservice.vo.MemberVo;

import java.util.Comparator;

public class ContactPinyinComparator implements Comparator<MemberVo> {

	public int compare(MemberVo o1, MemberVo o2) {
		if (o1.getSortLetter().equals("@") || o2.getSortLetter().equals("#")) {
			return -1;
		} else if (o1.getSortLetter().equals("#")
				|| o2.getSortLetter().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetter().compareTo(o2.getSortLetter());
		}
	}

}

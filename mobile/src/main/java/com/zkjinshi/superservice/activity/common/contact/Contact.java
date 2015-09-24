package com.zkjinshi.superservice.activity.common.contact;

/**
 * 联系人实体类
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class Contact {

	public String contactID;
	public String name;
	public String number;
	public String simpleNumber;
	public String sortKey;

	public Contact() {}

	public Contact(String contactID, String name, String number, String sortKey) {
		this.contactID = contactID;
		this.name		= name;
		this.number  	= number;
		this.sortKey 	= sortKey;
		if(number!=null){
			this.simpleNumber=number.replaceAll("\\-|\\s", "");
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contactID == null) ? 0 : contactID.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((number == null) ? 0 : number.hashCode());
		result = prime * result + ((sortKey == null) ? 0 : sortKey.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		Contact other = (Contact) obj;
		if (contactID == null) {
			if (other.contactID != null)
				return false;
		} else if (!contactID.equals(other.contactID))
			return false;

		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;

		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
			return false;

		if (sortKey == null) {
			if (other.sortKey != null)
				return false;
		} else if (!sortKey.equals(other.sortKey))
			return false;

		return true;
	}

}

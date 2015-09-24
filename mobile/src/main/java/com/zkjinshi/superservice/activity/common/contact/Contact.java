package com.zkjinshi.superservice.activity.common.contact;

/**
 * 联系人实体类
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class Contact {

	private long   contactID;
	private String name;
	private String number;
	private String simpleNumber;
	private String sortKey;

	public Contact() {}

	public Contact(long contactID, String name, String number, String sortKey) {
		this.contactID = contactID;
		this.name		= name;
		this.number  	= number;
		this.sortKey 	= sortKey;
		if(number!=null){
			this.simpleNumber=number.replaceAll("\\-|\\s", "");
		}
	}

	public long getContactID() {
		return contactID;
	}

	public void setContactID(long contactID) {
		this.contactID = contactID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getSimpleNumber() {
		return simpleNumber;
	}

	public void setSimpleNumber(String simpleNumber) {
		this.simpleNumber = simpleNumber;
	}

	public String getSortKey() {
		return sortKey;
	}

	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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

package com.zkjinshi.superservice.pad.vo;

import com.zkjinshi.superservice.pad.bean.BaseContact;

import java.io.Serializable;

/**
 * 联系人实体类
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ContactVo extends BaseContact implements Serializable{

	private long   contactID;
	private String name;
	private String number;
	private String simpleNumber;
    private String clientID;
	private OnlineStatus isOnLine;
	private int	bgDrawableRes;
	private ContactType contactType;
	private String      avatarUrl;

	public ContactVo() {}

	public ContactVo(long contactID, String name, String number,
					  String simpleNumber, String clientID,
					  OnlineStatus isOnLine, int bgDrawableRes,
					  ContactType contactType, String avatarUrl) {

		this.contactID = contactID;
		this.name = name;
		this.number = number;
		this.simpleNumber = simpleNumber;
		this.clientID = clientID;
		this.isOnLine = isOnLine;
		this.bgDrawableRes = bgDrawableRes;
		this.contactType = contactType;
		this.avatarUrl = avatarUrl;
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

	public String getClientID() {
		return clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	public OnlineStatus getIsOnLine() {
		return isOnLine;
	}

	public void setIsOnLine(OnlineStatus isOnLine) {
		this.isOnLine = isOnLine;
	}

	public int getBgDrawableRes() {
		return bgDrawableRes;
	}

	public void setBgDrawableRes(int bgDrawableRes) {
		this.bgDrawableRes = bgDrawableRes;
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

	public long getContactID() {
		return contactID;
	}

	public void setContactID(long contactID) {
		this.contactID = contactID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clientID == null) ? 0 : clientID.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((number == null) ? 0 : number.hashCode());
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

		ContactVo other = (ContactVo) obj;

		if (clientID == null) {
			if (other.clientID != null)
				return false;
		} else if (!clientID.equals(other.clientID))
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

		return true;
	}

}

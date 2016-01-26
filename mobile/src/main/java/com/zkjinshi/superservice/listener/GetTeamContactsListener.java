package com.zkjinshi.superservice.listener;

import com.zkjinshi.superservice.vo.ShopEmployeeVo;

import java.util.List;

/**
 *
 * 开发者：vincent
 * 日期：2015/10/15
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public interface GetTeamContactsListener {

   public void getContactsDone(List<ShopEmployeeVo> teamContacts);

   public void getContactsFailed();

}

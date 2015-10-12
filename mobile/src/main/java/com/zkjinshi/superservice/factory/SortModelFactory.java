package com.zkjinshi.superservice.factory;

import android.text.TextUtils;

import com.zkjinshi.superservice.bean.TeamContactBean;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.ContactType;
import com.zkjinshi.superservice.vo.SortModel;
import com.zkjinshi.superservice.utils.CharacterParser;
import com.zkjinshi.superservice.utils.SortKeyUtil;
import com.zkjinshi.superservice.vo.ClientVo;

import java.util.ArrayList;
import java.util.List;

/**
 * 开发者：vincent
 * 日期：2015/9/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SortModelFactory {

    private static SortModelFactory instance = null;

    private SortModelFactory(){}

    public synchronized static SortModelFactory getInstance(){
        if(instance == null){
            instance = new SortModelFactory();
        }
        return instance;
    }

//    /**
//     * 将获取客户对象集合转换成排序对象
//     * @param clientVos
//     * @return
//     */
//    public List<SortModel> convertClients2SortModels(List<ClientVo> clientVos) {
//        List <SortModel> sortModels = null;
//        if(!clientVos.isEmpty()){
//            sortModels = new ArrayList<>();
//            for(ClientVo clientVo : clientVos){
//                SortModel sortModel = buildSortModelByClientVo(clientVo);
//                sortModels.add(sortModel);
//            }
//        }
//        return sortModels;
//    }

//    /**
//     * 根据服务器客户Client构建排序对象
//     * @param clientVo
//     */
//    private SortModel buildSortModelByClientVo(ClientVo clientVo) {
//
//        String    avatarUrl   = clientVo.getAvatarUrl();
//        String    clientName  = clientVo.getClientName();
//        String    clientPhone = clientVo.getClientPhone();
//
//        SortModel sortModel = new SortModel();
//        sortModel.setContactType(ContactType.SERVER);
//        sortModel.setName(clientName);
//        sortModel.setAvatarUrl(avatarUrl);
//        sortModel.setNumber(clientPhone);
//        sortModel.setSortKey(clientName);
//        //优先使用系统sortkey取, 取不到再使用工具取
//        CharacterParser characterParser = new CharacterParser();
//        String sortLetters = SortKeyUtil.getSortLetterBySortKey(clientName, characterParser);
//        if (sortLetters == null) {
//            //根据电话号码查找首字母
//            sortLetters = SortKeyUtil.getSortLetter(clientPhone, characterParser);
//        }
//        sortModel.setSortLetters(sortLetters);
//        sortModel.setSortToken(SortKeyUtil.parseSortKey(clientName));
//        return sortModel;
//    }

//    /**
//     * 获得最近联系人的排序对象
//     * @param clientVos
//     */
//    public List <SortModel> getLatestSortModel(List<ClientVo> clientVos) {
//        List <SortModel> sortModels = null;
//        if(!clientVos.isEmpty()){
//            sortModels = new ArrayList<>();
//            for(ClientVo clientVo : clientVos){
//                SortModel sortModel = buildLatestSortModelByClientVo(clientVo);
//                sortModels.add(sortModel);
//            }
//        }
//        return sortModels;
//    }
//
//    /**
//     * 设置最近联系人
//     * @param clientVo
//     * @return
//     */
//    private SortModel buildLatestSortModelByClient(ClientVo clientVo) {
//
//        String    avatarUrl   = clientVo.getAvatarUrl();
//        String    clientName  = clientVo.getClientName();
//        String    clientPhone = clientVo.getClientPhone();
//
//        clientName = "?" + clientName;
//        SortModel sortModel = new SortModel();
//        sortModel.setContactType(ContactType.SERVER);
//        sortModel.setName(clientName);
//        sortModel.setAvatarUrl(avatarUrl);
//        sortModel.setNumber(clientPhone);
//        sortModel.setSortKey(clientName);
//
//        String sortLetters = clientName.trim().substring(0, 1);
//        sortModel.setSortLetters(sortLetters);
//        sortModel.setSortToken(SortKeyUtil.parseSortKey(clientName));
//        return sortModel;
//    }

    /**
     * 将服务端我的客户列表转换为排序列表
     * @param clientVos
     */
    public List <SortModel> convertClientVos2SortModels(List<ClientVo> clientVos) {

        List <SortModel> sortModels = null;
        if(!clientVos.isEmpty()){
            sortModels = new ArrayList<>();
            for(ClientVo clientVo : clientVos){
                SortModel sortModel = buildSortModelByMyClientVo(clientVo);
                sortModels.add(sortModel);
            }
        }

        return sortModels;
    }

    /**
     * 根据我的客户对象生成排序对象
     * @param clientVo
     * @return
     */
    private SortModel buildSortModelByMyClientVo(ClientVo clientVo) {

        String userID   = clientVo.getUserid();
        String userName = clientVo.getUsername();

        SortModel sortModel = new SortModel();
        sortModel.setContactType(clientVo.getContactType());
        sortModel.setName(userName);
        if(!TextUtils.isEmpty(userID)){
            sortModel.setAvatarUrl(ProtocolUtil.getAvatarUrl(clientVo.getUserid()));
        }
        sortModel.setNumber(clientVo.getPhone());
        sortModel.setSortKey(userName);
        //获得首字母
        String sortLetters = SortKeyUtil.getSortLetter(userName, CharacterParser.getInstance());
        sortModel.setSortLetters(sortLetters);
        sortModel.setSortToken(SortKeyUtil.parseSortKey(userName));
        return sortModel;
    }

    /**
     * 将团队客户对象转换成排序对象
     * @param teamContactBeans
     * @return
     */
    public List<SortModel> convertTeamContacts2SortModels(List<TeamContactBean> teamContactBeans) {
        List <SortModel> sortModels = null;

        if(!teamContactBeans.isEmpty()){
            sortModels = new ArrayList<>();
            for(TeamContactBean teamContact : teamContactBeans){
                SortModel sortModel = buildSortModelByTeamContactBean(teamContact);
                sortModels.add(sortModel);
            }
        }
        return sortModels;
    }

    /**
     * 构建排序对象
     * @param teamContact
     * @return
     */
    private SortModel buildSortModelByTeamContactBean(TeamContactBean teamContact) {

        String userID   = teamContact.getSalesid();
        String userName = teamContact.getName();
        String phone    = teamContact.getPhone();
        String roleName = teamContact.getRole_name();

        SortModel sortModel = new SortModel();
        sortModel.setContactType(ContactType.NORMAL);
        sortModel.setName(userName);
        if(!TextUtils.isEmpty(userID)){
            sortModel.setAvatarUrl(ProtocolUtil.getAvatarUrl(teamContact.getSalesid()));
        }
        sortModel.setNumber(phone);
        sortModel.setSortKey(userName);

        sortModel.setSortLetters(roleName); //此处为角色部门全称
        sortModel.setSortToken(SortKeyUtil.parseSortKey(userName));
        return sortModel;
    }
}

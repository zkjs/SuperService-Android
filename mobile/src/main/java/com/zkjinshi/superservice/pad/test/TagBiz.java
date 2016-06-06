package com.zkjinshi.superservice.pad.test;

import com.zkjinshi.superservice.pad.vo.ItemTagVo;

import java.util.ArrayList;

/**
 * 开发者：JimmyZhang
 * 日期：2016/4/27
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class TagBiz {

    public static ArrayList<ItemTagVo> getTagList(){
        ArrayList<ItemTagVo> tagList = new ArrayList<ItemTagVo>();
        ItemTagVo itemTagVo = new ItemTagVo();
        itemTagVo.setCount(15);
        itemTagVo.setTagname("远离电梯");
        tagList.add(itemTagVo);
        itemTagVo = new ItemTagVo();
        itemTagVo.setCount(20);
        itemTagVo.setTagname("睡眠浅");
        tagList.add(itemTagVo);
        itemTagVo = new ItemTagVo();
        itemTagVo.setCount(35);
        itemTagVo.setTagname("怕冷");
        tagList.add(itemTagVo);
        itemTagVo = new ItemTagVo();
        itemTagVo.setCount(75);
        itemTagVo.setTagname("高楼层");
        tagList.add(itemTagVo);
        itemTagVo = new ItemTagVo();
        itemTagVo.setCount(100);
        itemTagVo.setTagname("不喜角房");
        tagList.add(itemTagVo);
        itemTagVo = new ItemTagVo();
        itemTagVo.setCount(38);
        itemTagVo.setTagname("吸烟");
        tagList.add(itemTagVo);
        itemTagVo = new ItemTagVo();
        itemTagVo.setCount(45);
        itemTagVo.setTagname("房间朝北");
        tagList.add(itemTagVo);
        itemTagVo = new ItemTagVo();
        itemTagVo.setCount(80);
        itemTagVo.setTagname("房间朝南");
        tagList.add(itemTagVo);
        itemTagVo = new ItemTagVo();
        itemTagVo.setCount(15);
        itemTagVo.setTagname("素食者");
        tagList.add(itemTagVo);
        itemTagVo = new ItemTagVo();
        itemTagVo.setCount(95);
        itemTagVo.setTagname("爱甜食");
        tagList.add(itemTagVo);
        itemTagVo = new ItemTagVo();
        itemTagVo.setCount(35);
        itemTagVo.setTagname("喜好红茶");
        tagList.add(itemTagVo);
        itemTagVo = new ItemTagVo();
        itemTagVo.setCount(80);
        itemTagVo.setTagname("喜好黑茶");
        tagList.add(itemTagVo);
        itemTagVo = new ItemTagVo();
        itemTagVo.setCount(85);
        itemTagVo.setTagname("喜好绿茶");
        tagList.add(itemTagVo);
        return  tagList;
    }
}

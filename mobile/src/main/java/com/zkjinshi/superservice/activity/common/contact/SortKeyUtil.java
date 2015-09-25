package com.zkjinshi.superservice.activity.common.contact;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 开发者：vincent
 * 日期：2015/9/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SortKeyUtil {

    public final static String chReg = "[\\u4E00-\\u9FA5]+";//中文字符串匹配
    //String chReg="[^\\u4E00-\\u9FA5]";//除中文外的字符匹配
    /**
     * 名字转拼音,取首字母
     * @param name
     * @return
     */
    public static String getSortLetter(String name, CharacterParser characterParser) {
        String letter = "#";
        if (name == null) {
            return letter;
        }

        //汉字转换成拼音
        String pinyin     = characterParser.getSelling(name);
        String sortString = pinyin.substring(0, 1).toUpperCase(Locale.CHINESE);

        // 正则表达式，判断首字母是否是英文字母
        if (sortString.matches("[A-Z]")) {
            letter = sortString.toUpperCase(Locale.CHINESE);
        }
        return letter;
    }

    /**
     * 取sort_key的首字母
     * @param sortKey
     * @return
     */
    public static String getSortLetterBySortKey(String sortKey, CharacterParser characterParser) {

        if (sortKey == null || "".equals(sortKey.trim())) {
            return null;
        }

        String letter = "#";
        //汉字转换成拼音
        String sortString = sortKey.trim().substring(0, 1).toUpperCase(Locale.CHINESE);
        System.out.print("sortString:"+ sortString);
        if (sortString.length() > 0 && isChinese(sortString)) {
            letter = characterParser.getSelling(sortString).trim().substring(0, 1).toUpperCase(Locale.CHINA);
            System.out.print("letter:"+ letter);
        } else if (sortString.matches("[A-Z]")) {
            // 正则表达式，判断首字母是否是英文字母
            letter = sortString.toUpperCase(Locale.CHINESE);
        }
        return letter;
    }

    /**
     * 判断字符串是否为中文
     * @param str
     * @return
     */
    public static boolean isChinese(String str) {
        String regEx = "[\u4e00-\u9fa5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        while (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 解析sort_key,封装简拼,全拼
     * @param sortKey
     * @return
     */
    public static SortToken parseSortKey(String sortKey) {
        SortToken token = new SortToken();
        if (sortKey != null && sortKey.length() > 0) {
            //其中包含的中文字符
            String[] enStrs = sortKey.replace(" ", "").split(chReg);
            for (int i = 0, length = enStrs.length; i < length; i++) {
                if (enStrs[i].length() > 0) {
                    //拼接简拼
                    token.simpleSpell += enStrs[i].charAt(0);
                    token.wholeSpell += enStrs[i];
                }
            }
        }
        return token;
    }

}

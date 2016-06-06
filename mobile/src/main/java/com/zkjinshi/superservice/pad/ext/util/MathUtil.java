package com.zkjinshi.superservice.pad.ext.util;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 开发者：JimmyZhang
 * 日期：2016/3/19
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class MathUtil {

    /**
     * 判断价格是否正确
     * @param priceStr
     * @return
     */
    public static boolean isPriceOk(String priceStr){
        Pattern p = Pattern.compile("[0-9]+\\.?[0-9]{0,2}");
        Matcher m = p.matcher(priceStr);
        return m.matches();
    }

    /**
     * 获取预订价格
     * @param priceStr
     * @return
     */
    public static long parsePrice(String priceStr){
        double priceDouble = Double.parseDouble(priceStr);
        long price = (int)(priceDouble*100);
        return price;
    }

    /**
     * 保留两位小数
     * @param balance
     * @return
     */
    public static String convertStr(double balance){
        balance = balance/100;
        return new DecimalFormat("0.00").format(balance);
    }
}

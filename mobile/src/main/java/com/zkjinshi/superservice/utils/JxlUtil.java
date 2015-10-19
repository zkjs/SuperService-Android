package com.zkjinshi.superservice.utils;

import java.io.File;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

import com.zkjinshi.superservice.vo.ShopEmployeeVo;

/**
 * 解析,xls,xlsx文件 工具类
 * 开发者：dujiande
 * 日期：2015/10/10
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class JxlUtil {

    private static final String TAG = JxlUtil.class.getSimpleName();

    /**开始解析的行数**/
    private static final int START_ROW = 3;


    /**
     * 解析XLS 生成员工列表
     * @param path
     * @return
     */
    public static ArrayList<ShopEmployeeVo> decodeXLS(String path){
        ArrayList<ShopEmployeeVo> employeeVos = new ArrayList<ShopEmployeeVo>();

        try {
            Workbook workbook = null;
            workbook = Workbook.getWorkbook(new File(path));
            Sheet sheet = workbook.getSheet(0);
            Cell cell = null;
            int columnCount = sheet.getColumns();
            int rowCount = sheet.getRows();
            for (int i = START_ROW; i < rowCount; i++) {
                ShopEmployeeVo shopEmployeeVo = new ShopEmployeeVo();
                for (int j = 0; j < columnCount; j++) {
                    cell = sheet.getCell(j, i);
                    String temp2 = "";
                    if (cell.getType() == CellType.NUMBER) {
                        temp2 = ((NumberCell) cell).getContents() + "";
                    } else if (cell.getType() == CellType.DATE) {
                        temp2 = "" + ((DateCell) cell).getDate();
                    } else {
                        temp2 = "" + cell.getContents();
                    }
                    initInfo(shopEmployeeVo,j,temp2);

                }
                employeeVos.add(shopEmployeeVo);
            }
            workbook.close();
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }

        return  employeeVos;
    }



    private static void initInfo(ShopEmployeeVo shopEmployeeVo,int j,String temp2) {
        if(j == 1){
            shopEmployeeVo.setDept_name(temp2);
        }else if(j == 3){
            shopEmployeeVo.setName(temp2);
        }else if(j == 7){
            shopEmployeeVo.setPhone(temp2);
        }else if(j == 8){
            shopEmployeeVo.setEmail(temp2);
        }
    }

    /**
     * 解析XLS
     * @param path
     * @return
     */
    public static String readXLS(String path) {
        String str = "";
        try {
            Workbook workbook = null;
            workbook = Workbook.getWorkbook(new File(path));
            Sheet sheet = workbook.getSheet(0);
            Cell cell = null;
            int columnCount = sheet.getColumns();
            int rowCount = sheet.getRows();
            for (int i = 0; i < rowCount; i++) {
                for (int j = 0; j < columnCount; j++) {
                    cell = sheet.getCell(j, i);
                    String temp2 = "";
                    if (cell.getType() == CellType.NUMBER) {
                        temp2 = ((NumberCell) cell).getValue() + "";
                    } else if (cell.getType() == CellType.DATE) {
                        temp2 = "" + ((DateCell) cell).getDate();
                    } else {
                        temp2 = "" + cell.getContents();
                    }
                    str = str + "  " + temp2;
                }
                str += "\n";
            }
            workbook.close();
        } catch (Exception e) {
        }
        if (str == null) {
            str = "解析文件出现问题";
        }
        return str;
    }



    /**
     * 解析XLSX
     * @param path
     * @return
     */
    public static String readXLSX(String path) {
        String str = "";
        String v = null;
        boolean flat = false;
        List<String> ls = new ArrayList<String>();
        try {
            ZipFile xlsxFile = new ZipFile(new File(path));
            ZipEntry sharedStringXML = xlsxFile
                    .getEntry("xl/sharedStrings.xml");
            InputStream inputStream = xlsxFile.getInputStream(sharedStringXML);
            XmlPullParser xmlParser = Xml.newPullParser();
            xmlParser.setInput(inputStream, "utf-8");
            int evtType = xmlParser.getEventType();
            while (evtType != XmlPullParser.END_DOCUMENT) {
                switch (evtType) {
                    case XmlPullParser.START_TAG:
                        String tag = xmlParser.getName();
                        if (tag.equalsIgnoreCase("t")) {
                            ls.add(xmlParser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                evtType = xmlParser.next();
            }
            ZipEntry sheetXML = xlsxFile.getEntry("xl/worksheets/sheet1.xml");
            InputStream inputStreamsheet = xlsxFile.getInputStream(sheetXML);
            XmlPullParser xmlParsersheet = Xml.newPullParser();
            xmlParsersheet.setInput(inputStreamsheet, "utf-8");
            int evtTypesheet = xmlParsersheet.getEventType();
            while (evtTypesheet != XmlPullParser.END_DOCUMENT) {
                switch (evtTypesheet) {
                    case XmlPullParser.START_TAG:
                        String tag = xmlParsersheet.getName();
                        if (tag.equalsIgnoreCase("row")) {
                        } else if (tag.equalsIgnoreCase("c")) {
                            String t = xmlParsersheet.getAttributeValue(null, "t");
                            if (t != null) {
                                flat = true;
                                System.out.println(flat + "有");
                            } else {
                                System.out.println(flat + "没有");
                                flat = false;
                            }
                        } else if (tag.equalsIgnoreCase("v")) {
                            v = xmlParsersheet.nextText();
                            if (v != null) {
                                if (flat) {
                                    str += ls.get(Integer.parseInt(v)) + "  ";
                                } else {
                                    str += v + "  ";
                                }
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (xmlParsersheet.getName().equalsIgnoreCase("row")
                                && v != null) {
                            str += "\n";
                        }
                        break;
                }
                evtTypesheet = xmlParsersheet.next();
            }
            System.out.println(str);
        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        if (str == null) {
            str = "解析文件出现问题";
        }
        return str;
    }






}

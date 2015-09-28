package com.zkjinshi.superservice.net;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 网络请求帮助类
 * 开发者：JimmyZhang
 * 日期：2015/9/25
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class RequestUtil {

    /**
     * 发送Get请求
     * @param requestUrl
     * @return
     * @throws Exception
     */
    public static String sendGetRequest(String requestUrl) throws Exception{
        StringBuffer buffer = new StringBuffer();
        URL url = new URL(requestUrl);
        HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
        httpUrlConn.setDoOutput(false);
        httpUrlConn.setDoInput(true);
        httpUrlConn.setUseCaches(false);
        httpUrlConn.setRequestMethod("GET");
        httpUrlConn.connect();
        InputStream inputStream = httpUrlConn.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String str = null;
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
        }
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
        inputStream = null;
        httpUrlConn.disconnect();
        return buffer.toString();
    }

    /**
     * 发送post请求
     * @param requestUrl
     * @param bizMap
     * @param fileMap
     * @return
     * @throws Exception
     */
    public static String sendPostRequest(String requestUrl, HashMap<String, String> bizMap, HashMap<String, String> fileMap) throws Exception {
        String resultInfo = null;
        MultipartEntity multipartEntity = new MultipartEntity();
        if (null != bizMap) {
            Iterator<Map.Entry<String, String>> bizIterator = bizMap.entrySet()
                    .iterator();
            while (bizIterator.hasNext()) {
                HashMap.Entry bizEntry = (HashMap.Entry) bizIterator.next();
                StringBody bizStringBody = new StringBody((URLEncoder.encode(bizEntry
                        .getValue().toString(), "UTF-8")));
                multipartEntity.addPart(bizEntry.getKey().toString(), bizStringBody);
            }
        }
        if (null != fileMap) {
            String filePath = null;
            File file = null;
            FileBody fileBody = null;
            Iterator<Map.Entry<String, String>> fileIterator = fileMap.entrySet()
                    .iterator();
            while (fileIterator.hasNext()) {
                HashMap.Entry fileEntry = (HashMap.Entry) fileIterator.next();
                filePath = (String) fileEntry.getValue();
                file = new File(filePath);
                fileBody = new FileBody(file);
                multipartEntity.addPart(URLEncoder.encode((String) fileEntry.getKey(), "UTF-8"), fileBody);
            }
        }
        HttpPost httpPost = new HttpPost(requestUrl);
        HttpClient httpClient = new DefaultHttpClient();
        httpPost.setEntity(multipartEntity);
        HttpResponse response = httpClient.execute(httpPost);
        int respCode = 0;
        if (response != null && null != response.getStatusLine() && ((respCode = response.getStatusLine().getStatusCode()) == HttpStatus.SC_OK )) {
            resultInfo = EntityUtils.toString(response.getEntity());
        }
        return  resultInfo;
    }

}

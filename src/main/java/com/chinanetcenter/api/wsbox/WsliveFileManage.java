package com.chinanetcenter.api.wsbox;

import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.http.HttpClientUtil;
import com.chinanetcenter.api.util.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fuyz on 2016/6/27.
 * Live stream file management
 */
public class WsliveFileManage {

    /**
     * Retrieves a list of files based on the stream name.
     */
    public static HttpClientResult fileList(String bucketName, String channelname, String startTime, String endTime,String start, String limit) throws WsClientException {
        String url = "/wslive/list?" + "channelname=" + channelname + "&bucket=" + bucketName + "&startTime=" + startTime +"&endTime=" + endTime + "&start="+ start + "&limit=" + limit;
        Map<String, String> headMap = new HashMap<String, String>();
        String listToken = TokenUtil.getFileListToken(url);
        url = Config.MGR_URL + url;
        headMap.put("Authorization", listToken);
        return HttpClientUtil.httpGet(url, headMap);
    }

    /**
     * Sets the expiration time for a file.
     * Files will be automatically deleted after the set expiration time.
     */
    public static HttpClientResult setDeadline(String bucketName, String key, int deadline) throws WsClientException {
        String body = "bucket=" + EncodeUtils.urlsafeEncode(bucketName);
        body += "&key=" + EncodeUtils.urlsafeEncode(key) + "&deadline=" + deadline;
        String url = Config.MGR_URL + "/wslive/setdeadline";
        String value = EncodeUtils.urlsafeEncode(EncryptUtil.sha1Hex(("/wslive/setdeadline" + "\n" + body).getBytes(), Config.SK));
        String Authorization = Config.AK + ":" + value;
        Map<String, String> headMap = new HashMap<String, String>();
        headMap.put("Authorization", Authorization);
        return HttpClientUtil.httpPostStringEntity(url, headMap, body);
    }
}

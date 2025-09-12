package com.chinanetcenter.api.wsbox;


import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.entity.PutPolicy;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.http.HttpClientUtil;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.util.DateUtil;
import com.chinanetcenter.api.util.TokenUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Uploads files.
 *
 * @author zouhao
 * @version 1.0
 * @since 2014/02/14
 */
public class FileUploadManage {

    public HttpClientResult upload(String bucketName, String fileKey, String srcFile) throws WsClientException {
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setScope(bucketName + ":" + fileKey);
        putPolicy.setOverwrite(1);
        putPolicy.setDeadline(String.valueOf(DateUtil.nextHours(1, new Date()).getTime()));
        return upload(bucketName, fileKey, srcFile, putPolicy);
    }

    public HttpClientResult upload(String bucketName, String fileKey, InputStream in) throws WsClientException {
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setScope(bucketName + ":" + fileKey);
        putPolicy.setOverwrite(1);
        putPolicy.setDeadline(String.valueOf(DateUtil.nextHours(1, new Date()).getTime()));
        String fileName = fileKey;
        if (fileName.contains("/")) {
            fileName = StringUtils.substringAfterLast(fileName, "/");
        }
        return upload(bucketName, fileKey, fileName, in, putPolicy);
    }

    /**
     * Uploads a file to the specified bucket with a customizable upload policy.
     *
     * @param bucketName The name of the bucket where the file will be stored.
     * @param fileKey The desired file name for the uploaded file within the bucket.
     * @param srcFile The local path of the file to upload.
     * @param putPolicy The upload policy. This policy defines a set of configuration settings for resource uploads.
     *                   Through this configuration, users can customize upload requirements, including specifying resources, target bucket,
     *                   callback notifications or redirects, feedback content, and upload authorization deadlines.
     *                   Refer to the PutPolicy entity for detailed attribute descriptions.
     */
    public HttpClientResult upload(String bucketName, String fileKey, String srcFile, PutPolicy putPolicy) throws WsClientException {
        if (putPolicy.getDeadline() == null) {
            putPolicy.setDeadline(String.valueOf(DateUtil.nextHours(1, new Date()).getTime()));
        }
        if (StringUtils.isEmpty(fileKey)) {
            putPolicy.setScope(bucketName);
        } else {
            putPolicy.setScope(bucketName + ":" + fileKey);
        }
        String uploadToken = TokenUtil.getUploadToken(putPolicy);
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("token", uploadToken);
        return upload(paramMap,srcFile);
    }

    /**
     * Uploads a file to the specified bucket with a customizable upload policy.
     *
     * @param bucketName The name of the bucket where the file will be stored.
     * @param fileKey The desired file name for the uploaded file within the bucket.
     * @param inputStream The input stream of the file.
     * @param putPolicy The upload policy. This policy defines a set of configuration settings for resource uploads.
     *                    Through this configuration, users can customize upload requirements, including specifying resources, target bucket,
     *                    callback notifications or redirects, feedback content, and upload authorization deadlines.
     *                    Refer to the PutPolicy entity for detailed attribute descriptions.
     */
    public HttpClientResult upload(String bucketName, String fileKey, String fileName, InputStream inputStream, PutPolicy putPolicy) throws WsClientException {
        if (putPolicy.getDeadline() == null) {
            putPolicy.setDeadline(String.valueOf(DateUtil.nextHours(1, new Date()).getTime()));
        }
        if (fileKey == null || fileKey.equals("")) {
            putPolicy.setScope(bucketName);
        } else {
            putPolicy.setScope(bucketName + ":" + fileKey);
        }
        String uploadToken = TokenUtil.getUploadToken(putPolicy);
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("token", uploadToken);
        return upload(paramMap,fileName,inputStream);
    }

    public HttpClientResult upload(Map<String, String> paramMap, String fileName, InputStream inputStream) throws WsClientException {
        String url = Config.PUT_URL + "/file/upload";
        return HttpClientUtil.httpPost(url, null, paramMap, fileName, inputStream);
    }

    public HttpClientResult upload(Map<String, String> paramMap, String srcFile) throws WsClientException {
        String url = Config.PUT_URL + "/file/upload";
        File file = new File(srcFile);
        return HttpClientUtil.httpPost(url, paramMap,null, file);
    }
}

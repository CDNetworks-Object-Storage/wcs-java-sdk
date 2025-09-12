package com.chinanetcenter.api.demo;

import com.chinanetcenter.api.entity.PutPolicy;
import com.chinanetcenter.api.entity.SliceUploadHttpResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.sliceUpload.BaseBlockUtil;
import com.chinanetcenter.api.sliceUpload.JSONObjectRet;
import com.chinanetcenter.api.util.*;
import com.chinanetcenter.api.wsbox.SliceUploadResumable;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fuyz on 2016/8/31.
 * Sliced upload
 */
public class SliceUploadDemo {

    public static void main(String[] args) throws FileNotFoundException {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * You can obtain uploadDomain and MgrDomain in the User Management Interface - Security Management - Domain Query.
         */
        Config.PUT_URL = "your uploadDomain";
        String bucketName = "your-bucket";
        String fileKey = "java-sdk/com.toycloud.MeiYe.apk";

        String srcFilePath = "D:\\testfile\\test001\\com.toycloud.MeiYe.apk";
        BaseBlockUtil.CHUNK_SIZE = 4 * 1024 * 1024;  // Each slice is 4MB, default 256KB, reducing upload requests
        SliceUploadDemo demo = new SliceUploadDemo();
        demo.sliceUpload(bucketName,fileKey,srcFilePath);
        /** The second way, key is not written to scope, but specified from head. Used for uploading multiple files with the same token.
        String fileKey2 = "java-sdk/com.toycloud.MeiYe2.apktest";
        String mimeType = "application/vnd.android.package-archive";
        demo.sliceUpload(bucketName,fileKey2,srcFilePath,mimeType);
         */
    }

    public void sliceUpload(final String bucketName, final String fileKey, final String filePath) {
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setScope(bucketName + ":" + fileKey);
        putPolicy.setOverwrite(1);
        putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1, new Date()).getTime()));
        JSONObjectRet jsonObjectRet = getJSONObjectRet(bucketName,fileKey,filePath);
        SliceUploadResumable sliceUploadResumable = new SliceUploadResumable();
        sliceUploadResumable.execUpload(bucketName, fileKey, filePath, putPolicy, null, jsonObjectRet);
    }

    public void sliceUpload(final String bucketName, final String fileKey, final String filePath,String mimeType) {
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setScope(bucketName);
        putPolicy.setOverwrite(1);
        putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1, new Date()).getTime()));
        JSONObjectRet jsonObjectRet = getJSONObjectRet(bucketName,fileKey,filePath);
        SliceUploadResumable sliceUploadResumable = new SliceUploadResumable();
        Map<String,String> headMap = new HashMap<String, String>();
        headMap.put("mimeType",mimeType);
        headMap.put("key", EncodeUtils.urlsafeEncode(fileKey));
        sliceUploadResumable.execUpload(bucketName, fileKey, filePath, putPolicy, null, jsonObjectRet,headMap);
    }

    public JSONObjectRet getJSONObjectRet(final String bucketName,final String fileKey,final String filePath){
        return new JSONObjectRet() {
            /**
             * This method will be called back after the file is successfully uploaded
             * Check if the hash of the uploaded file is consistent with the hash of the local file. Inconsistency may indicate that the local file has been modified.
             */
            @Override
            public void onSuccess(JsonNode obj) {
                File fileHash = new File(filePath);
                String eTagHash = WetagUtil.getEtagHash(fileHash.getParent(), fileHash.getName());// 根据文件内容计算hash
                SliceUploadHttpResult result = new SliceUploadHttpResult(obj);
                if (eTagHash.equals(result.getHash())) {
                    System.out.println("Upload successful");
                } else {
                    System.out.println("hash not equal,eTagHash:" + eTagHash + " ,hash:" + result.getHash());
                }
            }

            @Override
            public void onSuccess(byte[] body) {
                System.out.println(new String(body));
            }

            // This method is called back when file upload fails
            @Override
            public void onFailure(Exception ex) {
                if (ex instanceof WsClientException) {
                    WsClientException wsClientException = (WsClientException) ex;
                    System.out.println(wsClientException.code + ":" + wsClientException.getMessage());
                }else {
                    ex.printStackTrace();
                }
                System.out.println("Upload error, " + ex.getMessage());
            }

            // Progress bar display, this method is called back after each block is successfully uploaded
            @Override
            public void onProcess(long current, long total) {
                System.out.printf("%s\r", current * 100 / total + " %");
            }

            /**
             * Persistence, save progress information during breakpoint resume, assign JSONObject to PutExtra next time uploading
             * SDK saves information to disk file by default, please save to DB if needed
             * Assign the value to PutExtra parameter when resuming upload next time
             */
            @Override
            public void onPersist(JsonNode obj) {
                BaseBlockUtil.savePutExtra(bucketName, fileKey, obj);
            }
        };
    }

    public void sliceUpload(final String bucketName, final String fileKey, InputStream inputStream) {
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setScope(bucketName + ":" + fileKey);
        putPolicy.setOverwrite(1);
        putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1, new Date()).getTime()));
        JSONObjectRet jsonObjectRet = new JSONObjectRet() {
            @Override
            public void onSuccess(JsonNode obj) {
                System.out.println("Upload successful");
            }

            @Override
            public void onSuccess(byte[] body) {
            }

            @Override
            public void onFailure(Exception ex) {
                if (ex instanceof WsClientException) {
                    WsClientException wsClientException = (WsClientException) ex;
                    System.out.println(wsClientException.code + ":" + wsClientException.getMessage());
                } else {
                    ex.printStackTrace();
                }
                System.out.println("Upload error, " + ex.getMessage());
            }

            @Override
            public void onProcess(long current, long total) {
            }

            @Override
            public void onPersist(JsonNode obj) {
            }
        };
        SliceUploadResumable sliceUploadResumable = new SliceUploadResumable();
        sliceUploadResumable.execUpload(bucketName, fileKey, inputStream, putPolicy, jsonObjectRet);
    }
}

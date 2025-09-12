package com.chinanetcenter.api.demo;

import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.entity.PutPolicy;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.util.DateUtil;
import com.chinanetcenter.api.util.TokenUtil;
import com.chinanetcenter.api.wsbox.FileUploadManage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fuyz on 2016/8/30.
 * Upload demo
 */
public class UploadDemo {
    FileUploadManage fileUploadManage = new FileUploadManage();

    public static void main(String[] args) throws FileNotFoundException {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * You can obtain uploadDomain and MgrDomain in the User Management Interface - Security Management - Domain Query.
         */
        Config.PUT_URL = "your uploadDomain";
        String bucketName = "your-bucket";
        String fileKey = "test.JPG";
        String fileKeyMp4 = "folder/test.JPG";
        String srcFilePath = "D:\\testfile\\1m.JPG";
        UploadDemo demo = new UploadDemo();
        demo.uploadFile(bucketName, fileKey, srcFilePath);
        FileInputStream in = new FileInputStream(new File(srcFilePath));
        demo.uploadFile(bucketName, fileKey, in);
        demo.uploadReturnBody(bucketName, fileKeyMp4, srcFilePath);
        demo.uploadMimeType(bucketName, fileKey, srcFilePath);
        demo.uploadPersistent(bucketName, fileKey, srcFilePath);
    }

    /**
     * Upload file via local file path
     * Default overwrite upload
     */
    public void uploadFile(String bucketName,String fileKey,String srcFilePath){
        try {
            HttpClientResult result = fileUploadManage.upload(bucketName,fileKey,srcFilePath);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * Upload file via file stream, InputStream will be closed in the method
     * Default overwrite upload
     */
    public void uploadFile(String bucketName,String fileKey,InputStream in){
        try {
            HttpClientResult result = fileUploadManage.upload(bucketName,fileKey,in);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * After uploading, if callback, return information, etc. are needed, upload policy can be specified through PutPolicy
     * callbackurl, callbackbody, returnurl are similar to this method
     */
    public void uploadReturnBody(String bucketName,String fileKey,String srcFilePath){
        String returnBody = "key=$(key)&fname=$(fname)&fsize=$(fsize)&url=$(url)&hash=$(hash)&mimeType=$(mimeType)";
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setOverwrite(1); // Overwrite upload
        putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1,new Date()).getTime()));
        putPolicy.setReturnBody(returnBody);
        putPolicy.setScope(bucketName + ":" + fileKey);
        try {
            HttpClientResult result = fileUploadManage.upload(bucketName,fileKey,srcFilePath,putPolicy);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * Upload specified file type, server defaults to file extension or file content
     * If mimeType is specified, Content-type will be specified as this type when downloading
     */
    public void uploadMimeType(String bucketName,String fileKey,String srcFilePath){
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setOverwrite(1);
        putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1, new Date()).getTime()));
        putPolicy.setScope(bucketName + ":" + fileKey);
        try {
            String uploadToken = TokenUtil.getUploadToken(putPolicy);
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("token", uploadToken);
            paramMap.put("mimeType", "application/UQ");
            HttpClientResult result = fileUploadManage.upload(paramMap,srcFilePath);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * Transcode the file after uploading
     * After successful upload, persistentId response is returned, which can be used to query transcoding status
     */
    public void uploadPersistent(String bucketName,String fileKey,String srcFilePath){
        PutPolicy putPolicy = new PutPolicy();
        String returnBody = "key=$(key)&persistentId=$(persistentId)&fsize=$(fsize)";
        putPolicy.setOverwrite(1);
        putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1, new Date()).getTime()));
        putPolicy.setScope(bucketName + ":" + fileKey);
        putPolicy.setPersistentOps("imageMogr2/jpg/crop/500x500/gravity/CENTER/lowpoly/1|saveas/ZnV5enRlc3Q4Mi0wMDE6ZG9fY3J5c3RhbGxpemVfZ3Jhdml0eV9jZW50ZXJfMTQ2NTkwMDg0Mi5qcGc="); // Set video transcoding operation
        putPolicy.setPersistentNotifyUrl("http://demo1/notifyUrl"); // Set callback interface after transcoding
        putPolicy.setReturnBody(returnBody);
        try {
            HttpClientResult result = fileUploadManage.upload(bucketName,fileKey,srcFilePath,putPolicy);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
}

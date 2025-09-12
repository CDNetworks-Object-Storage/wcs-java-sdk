package com.chinanetcenter.api.demo;

import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.wsbox.WsliveFileManage;

/**
 * Purpose: Resource Management - Set File Retention Period
 * Created by chenql on 2018/4/3.
 */
public class SetdeadlineDemo {

    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * You can obtain uploadDomain and MgrDomain in the User Management Interface - Security Management - Domain Query.
         */
        Config.MGR_URL = "your MgrDomain";
        String bucketName = "your-bucket";
        String fileKey = "java-sdk/testfile.jpg";
        int deadline = 30;// Represents expiration after 30 days
        WsliveFileManage manageCommand = new WsliveFileManage();
        try {
            HttpClientResult result = manageCommand.setDeadline(bucketName, fileKey, deadline);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
}

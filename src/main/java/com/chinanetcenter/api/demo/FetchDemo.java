package com.chinanetcenter.api.demo;

import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.wsbox.OperationManager;

import java.util.ArrayList;

/**
 * Created by fuyz on 2016/9/1.
 * Stat file information
 */
public class FetchDemo {
    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * You can obtain uploadDomain and MgrDomain in the User Management Interface - Security Management - Domain Query.
         */
        Config.MGR_URL = "your MgrDomain";
        String bucketName = "your-bucket";
        new FetchDemo().prefetch(bucketName);
    }

    public void prefetch(String bucketName) {
        OperationManager fileManageCommand = new OperationManager();
        String fileName1 = "testPreFetch1.png"; // File Name
        String fileName2 = "testPreFetch2.png"; // File Name
        ArrayList<String> fileKeys = new ArrayList<String>();
        fileKeys.add(fileName1);
        fileKeys.add(fileName2);

        try {
            HttpClientResult result = fileManageCommand.prefetch(bucketName, fileKeys);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
}

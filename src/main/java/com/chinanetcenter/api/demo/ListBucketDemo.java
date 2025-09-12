package com.chinanetcenter.api.demo;

import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.wsbox.OperationManager;

public class ListBucketDemo {

    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * You can obtain uploadDomain and MgrDomain in the User Management Interface - Security Management - Domain Query.
         */
        Config.MGR_URL = "your MgrDomain";
        OperationManager fileManageCommand = new OperationManager();
        try {
            HttpClientResult result = fileManageCommand.bucketList();
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
}

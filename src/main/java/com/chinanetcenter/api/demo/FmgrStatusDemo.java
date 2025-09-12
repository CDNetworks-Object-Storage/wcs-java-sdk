package com.chinanetcenter.api.demo;

import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.wsbox.FmgrFileManage;

/**
 * Purpose: Advanced Resource Management - Task Query
 * Created by chenql on 2018/4/3.
 */
public class FmgrStatusDemo {
    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * You can obtain uploadDomain and MgrDomain in the User Management Interface - Security Management - Domain Query.
         */
        Config.MGR_URL = "your MgrDomain";
        String persistentId = "your-persistentId";
        FmgrFileManage fileManageCommand = new FmgrFileManage();
        try {
            HttpClientResult result = fileManageCommand.fmgrStatus(persistentId);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
}

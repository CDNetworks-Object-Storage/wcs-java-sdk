package com.chinanetcenter.api.demo;

import com.chinanetcenter.api.entity.FmgrParam;
import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.wsbox.FmgrFileManage;

import java.util.ArrayList;
import java.util.List;

/**
 * Purpose: Advanced Resource Management - Batch Modify File Retention Period
 * Created by chenql on 2018/4/3.
 */
public class FmgrSetdeadlineDemo {
    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * You can obtain uploadDomain and MgrDomain in the User Management Interface - Security Management - Domain Query.
         */
        Config.MGR_URL = "your MgrDomain";
        String bucketName = "your-bucket";
        FmgrFileManage fileManageCommand = new FmgrFileManage();
        try {
            List<FmgrParam> list = new ArrayList<FmgrParam>();
            FmgrParam fmgrParam = new FmgrParam();
            fmgrParam.setBucket(bucketName);
            fmgrParam.setPrefix("aaa");
            fmgrParam.setDeadline(10);
            list.add(fmgrParam);
            FmgrParam fmgrParam2 = new FmgrParam();
            fmgrParam2.setBucket(bucketName);
            fmgrParam2.setPrefix("bbb");
            fmgrParam2.setDeadline(5);
            list.add(fmgrParam2);
            String notifyURL = "http://demo1/notifyUrl";  // Notification address, this address will be called back after successful transcoding
            HttpClientResult result = fileManageCommand.fmgrSetdeadline(list, notifyURL);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
}

package com.chinanetcenter.api.util;

import com.chinanetcenter.api.entity.PutPolicy;

/**
 * Created by zouhao on 14-5-16.
 */
public class TokenUtil {


    public static void main(String[] args) {
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setScope("viptest:moteltest001.mp4");
        Long time = DateUtil.parseDate("2050-01-01 12:00:00", DateUtil.COMMON_PATTERN).getTime();
        putPolicy.setDeadline(String.valueOf(time));
        String uploadToken = TokenUtil.getUploadToken(putPolicy);
        System.out.println(uploadToken);
    }

    /**
     * Get upload token
     *
     * @param putPolicy
     * @return
     */
    public static String getUploadToken(PutPolicy putPolicy) {
        JsonMapper jsonMapper = JsonMapper.nonEmptyMapper();
        String putPolicyStr = jsonMapper.toJson(putPolicy);
        String encodePutPolicy = EncodeUtils.urlsafeEncode(putPolicyStr);
        String singSk = EncryptUtil.sha1Hex(encodePutPolicy.getBytes(), Config.SK);//signature
        String skValue = EncodeUtils.urlsafeEncode(singSk);//Base64 encoding
        String uploadToken = Config.AK + ":" + skValue + ":" + encodePutPolicy;
        return uploadToken;
    }


    /**
     * Get delete token
     *
     * @param bucketName Bucket name
     * @param fileName   File name
     * @return
     */
    public static String getDeleteToken(String bucketName, String fileName) {
        String encodedEntryURI = EncodeUtils.urlsafeEncodeString((bucketName + ":" + fileName).getBytes());
        String encodeDeletePath = "/delete/" + encodedEntryURI + "\n";
        String signSk = EncryptUtil.sha1Hex(encodeDeletePath.getBytes(), Config.SK);//signature
        String encodedSign = EncodeUtils.urlsafeEncode(signSk);//Base64 encoding
        String deleteToken = Config.AK + ":" + encodedSign;
        return deleteToken;
    }

    /**
     * Get token for prefix fuzzy deletion
     *
     * @param bucketName Bucket name
     * @param fileName   File name
     * @return
     */
    public static String getDeletePrefixToken(String bucketName, String fileName) {
        String encodedEntryURI = EncodeUtils.urlsafeEncodeString((bucketName + ":" + fileName).getBytes());
                String encodeDeletePath = "/deletePrefix/" + encodedEntryURI + "\n";
        String signSk = EncryptUtil.sha1Hex(encodeDeletePath.getBytes(), Config.SK);//signature
        String encodedSign = EncodeUtils.urlsafeEncode(signSk);//Base64 encoding
        String deleteToken = Config.AK + ":" + encodedSign;
        return deleteToken;
    }

    /**
     * Get token for file information
     *
     * @param bucketName Bucket Name
     * @param fileName File name
     * @return
     */
    public static String getStatToken(String bucketName, String fileName) {
        String encodedEntryURI = EncodeUtils.urlsafeEncodeString((bucketName + ":" + fileName).getBytes());
        String encodeDeletePath = "/stat/" + encodedEntryURI + "\n";
        String signSk = EncryptUtil.sha1Hex(encodeDeletePath.getBytes(), Config.SK);//signature
        String encodedSign = EncodeUtils.urlsafeEncode(signSk);//Base64 encoding
        String deleteToken = Config.AK + ":" + encodedSign;
        return deleteToken;
    }

    public static String getFileListToken(String listUrl) {
        listUrl += "\n";
        String encodeDownloadUrl = EncryptUtil.sha1Hex(listUrl.getBytes(), Config.SK);// signature
        String skValues = EncodeUtils.urlsafeEncode(encodeDownloadUrl);// Base64 encoding
        String listToken = Config.AK + ":" + skValues;
        return listToken;
    }

}

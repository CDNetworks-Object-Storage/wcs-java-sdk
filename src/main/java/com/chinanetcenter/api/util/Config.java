package com.chinanetcenter.api.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Configuration class, configuring user's AK and SK information<br>
 *
 * @author zouhao
 * @version 1.0
 * @since 2014/03/02
 */
public class Config {
    public final static String VERSION_NO = "wcs-java-sdk-2.0.7";
    /**
     * For specific AK SK information, please obtain it from the Wangsu Cloud Storage Web application (Account Management - Key Management).
     */
    public static String AK = "your-ak";
    public static String SK = "your-sk";
    /**
     * You can obtain `uploadDomain` and `MgrDomain` in the User Management Interface - Security Management - Domain Query.
     */
    public static String PUT_URL = "your uploadDomain";
    public static String MGR_URL = "your MgrDomain";
    /**
     * Download GET_URL using bound domain
     */
    public static String GET_URL = "your downloadDomain";
    public static String LOCAL_IP = "127.0.0.1";
    public static String LOG_FILE_PATH = "";

    /**
     * Prevents direct external instantiation.<br>
     */
    private Config() {
        try {
            LOCAL_IP = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            LOCAL_IP = "127.0.0.1";
        }
    }

    /**
     * Initializes the key.<br>
     *
     * @param ak The AK information for the space.
     * @param sk The SK information for the space.
     */
    public static void init(String ak, String sk) {
        AK = ak;
        SK = sk;
    }

    /**
     * Initializes the key.<br>
     *
     * @param ak The AK information for the space.
     * @param sk The SK information for the space.
     * @param logFilePath The path for HTTP request logs.
     */
    public static void init(String ak, String sk, String logFilePath) {
        AK = ak;
        SK = sk;
        LOG_FILE_PATH = logFilePath;
    }

    /**
     * Initializes the key.<br>
     *
     * @param ak The AK information for the space.
     * @param sk The SK information for the space.
     */
    public static void init(String ak, String sk, String putUrl, String getUrl) {
        AK = ak;
        SK = sk;
        PUT_URL = putUrl;
        GET_URL = getUrl;
    }

    /**
     * Initializes the key.<br>
     *
     * @param ak The AK information for the space.
     * @param sk The SK information for the space.
     */
    public static void init(String ak, String sk, String putUrl, String getUrl, String mgrUrl) {
        AK = ak;
        SK = sk;
        PUT_URL = putUrl;
        GET_URL = getUrl;
        MGR_URL = mgrUrl;
    }

    /**
     * Initializes the key.<br>
     *
     * @param ak          Accesskey
     * @param sk          Accesskey Secret
     * @param logFilePath The path for HTTP request logs.
     */
    public static void init(String ak, String sk, String putUrl, String getUrl, String mgrUrl, String logFilePath) {
        AK = ak;
        SK = sk;
        PUT_URL = putUrl;
        GET_URL = getUrl;
        MGR_URL = mgrUrl;
        LOG_FILE_PATH = logFilePath;
    }
}

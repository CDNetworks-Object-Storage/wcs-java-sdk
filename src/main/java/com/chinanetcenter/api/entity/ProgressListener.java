package com.chinanetcenter.api.entity;

/**
 * Created by lidl on 15-3-20.
 */
public class ProgressListener {

    /**
     * @param bytesWritten Progress of uploaded or downloaded bytes
     * @param totalSize    Total file size
     */
    public void onProgress(long bytesWritten, long totalSize) {
        System.out.println(bytesWritten + " ," + totalSize);
    }

}

package com.chinanetcenter.api.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

/**
 * File hash/etag utility
 * Created by xiexb on 2014/5/30.
 */
public class WetagUtil {
    private static final int BLOCK_BITS = 22;
    private static final int BLOCK_SIZE = 1 << BLOCK_BITS;// 2^22 = 4MB
    private static final byte BYTE_LOW_4 = 0x16;// For files less than or equal to 4MB, a single byte with value 0x16 is prepended.
    private static final byte BYTE_OVER_4 = (byte) 0x96;// For files greater than 4MB, a single byte with value 0x96 is prepended.

    /**
     * Calculates the number of file blocks, with 4MB chunking.
     *
     * @param fileLength
     * @return
     */
    private static long blockCount(long fileLength) {
        return ((fileLength + (BLOCK_SIZE - 1)) >> BLOCK_BITS);
    }

    /**
     * Reads SHA-1 data for a specified file block.
     *
     * @param fis
     * @return
     */
    private static MessageDigest calSha1(BufferedInputStream fis) {
        MessageDigest sha1 = null;
        try {
            byte[] buffer = new byte[1024];
            int numRead = 0;
            int total = 0;
            sha1 = MessageDigest.getInstance("SHA-1");
            while ((numRead = fis.read(buffer)) > 0) {
                sha1.update(buffer, 0, numRead);
                total += numRead;
                if (total >= BLOCK_SIZE) {// Reads up to 4MB at a time.
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sha1;
    }

    /**
     * Retrieves hash/etag, calculates hash value based on the File object.
     *
    public static String getEtagHash(File file) {
        String etagHash = null;
        BufferedInputStream fis = null;
        try {
            if (file.exists()) {
                byte[] ret = new byte[21];
                long blockCount = blockCount(file.length());
                fis = new BufferedInputStream(new FileInputStream(file));
                if (blockCount <= 1) { // The number of file blocks is less than or equal to 1 block.
                    MessageDigest sha1 = calSha1(fis);
                    if (null != sha1) {
                        byte[] input = sha1.digest();
                        ret[0] = BYTE_LOW_4;
                        for (int i = 0; i < 20; ++i) {// The SHA1 algorithm is 20 bytes.
                            ret[i + 1] = input[i];
                        }
                    }
                } else {// Concatenate all SHA1 values in chunk order.
                    byte[] rec = new byte[(int) blockCount * 20];
                    ret[0] = BYTE_OVER_4;
                    int i, cnt = 0;
                    for (i = 0; i < blockCount; i++) {// Calculate SHA1 for each file block separately.
                        MessageDigest sha1 = calSha1(fis);
                        if (null != sha1) {
                            byte[] tmp = sha1.digest();
                            for (int j = 0; j < 20; j++) {
                                rec[cnt++] = tmp[j];
                            }
                        }
                    }
                    MessageDigest sha1 = MessageDigest.getInstance("SHA-1");// Then perform SHA-1 calculation on the concatenated data.
                    sha1.update(rec, 0, (int) blockCount * 20);
                    byte[] tmp = sha1.digest();
                    for (i = 0; i < 20; ++i) {// Prepend a single byte with the value 0x96.
                        ret[i + 1] = tmp[i];
                    }
                }
                etagHash = EncodeUtils.urlsafeEncodeString(ret);
            } else {
                System.out.println("File[" + file.getAbsolutePath() + "] Not Exist,Cannot Calculate Hash!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return etagHash;
    }

    /**
     * Get hash/etag, used to calculate hash value for unchunked local files.
     *
     * @param filePath file physical path
     * @param fileName file name
     * @return
     */
    public static String getEtagHash(String filePath, String fileName) {
        File f = new File(filePath, fileName);
        return getEtagHash(f);
    }

    /**
     * Get hash/etag, used to calculate hash value for unchunked files.
     *
     * @param fileInputStream file input stream
     * @param fileLength      file size
     * @return
     */
    public static String getEtagHash(InputStream fileInputStream, long fileLength) {
        String etagHash = null;
        BufferedInputStream fis = null;
        try {
            byte[] ret = new byte[21];
            long blockCount = blockCount(fileLength);
            fis = new BufferedInputStream(fileInputStream);
            if (blockCount <= 1) { // The number of file blocks is less than or equal to 1 block.
                MessageDigest sha1 = calSha1(fis);
                if (null != sha1) {
                    byte[] input = sha1.digest();
                    ret[0] = BYTE_LOW_4;
                    for (int i = 0; i < 20; ++i) {// The SHA1 algorithm is 20 bytes.
                        ret[i + 1] = input[i];
                    }
                }
            } else {// Concatenate all SHA1 values in chunk order.
                byte[] rec = new byte[(int) blockCount * 20];
                ret[0] = BYTE_OVER_4;
                int i, cnt = 0;
                for (i = 0; i < blockCount; i++) {// Calculate SHA1 for each file block separately.
                    MessageDigest sha1 = calSha1(fis);
                    if (null != sha1) {
                        byte[] tmp = sha1.digest();
                        for (int j = 0; j < 20; j++) {
                            rec[cnt++] = tmp[j];
                        }
                    }
                }
                MessageDigest sha1 = MessageDigest.getInstance("SHA-1");// Re-calculate SHA1 for the concatenated data.
                sha1.update(rec, 0, (int) blockCount * 20);
                byte[] tmp = sha1.digest();
                for (i = 0; i < 20; ++i) {// Prepend a single byte with the value 0x96.
                    ret[i + 1] = tmp[i];
                }
            }
            etagHash = EncodeUtils.urlsafeEncodeString(ret);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                    fis = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return etagHash;
    }

    /**
     * Calculate the SHA1 of the specified data.
     *
     * @param data byte array
     * @return
     */
    private static MessageDigest calSha1(byte[] data) {
        MessageDigest sha1 = null;
        try {
            sha1 = MessageDigest.getInstance("SHA-1");
            sha1.update(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sha1;
    }

    /**
     * Obtain hash/etag, used to calculate the hash value for each block of a file that has already been chunked.
     *
     * @param data file's byte array
     * @return
     */
    public static String getEtagHash(byte[] data) {
        String etagHash = null;
        try {
            byte[] ret = new byte[21];
            MessageDigest sha1 = calSha1(data);
            if (null != sha1) {
                byte[] input = sha1.digest();
                ret[0] = BYTE_LOW_4;
                for (int i = 0; i < 20; ++i) {// The SHA1 algorithm is 20 bytes.
                    ret[i + 1] = input[i];
                }
            }
            etagHash = EncodeUtils.urlsafeEncodeString(ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return etagHash;
    }

    public static long crc32(byte[] data) {
        CRC32 crc32 = new CRC32();
        crc32.update(data);
        return crc32.getValue();
    }

    public static String getFileMD5String(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data);
            return toHex(md.digest());
        } catch (IllegalStateException e) {
            return null;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    private static String toHex(byte buffer[]) {
        StringBuilder sb = new StringBuilder();
        String s;
        for (byte aBuffer : buffer) {
            s = Integer.toHexString((int) aBuffer & 0xff);
            if (s.length() < 2) {
                sb.append('0');
            }
            sb.append(s);
        }
        return sb.toString();
    }
}

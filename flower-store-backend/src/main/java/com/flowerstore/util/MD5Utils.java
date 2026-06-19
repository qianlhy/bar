package com.flowerstore.util;

import java.security.MessageDigest;

/**
 * MD5加密工具类
 */
public class MD5Utils {

    /**
     * MD5加密
     */
    public static String encrypt(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] byteDigest = md.digest();
            StringBuilder buf = new StringBuilder();
            for (byte b : byteDigest) {
                buf.append(String.format("%02x", b & 0xff));
            }
            return buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}


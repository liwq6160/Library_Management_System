package com.liwq.bookmanager.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * 密码加密工具类
 */
public class PasswordUtil {

    /**
     * MD5加密密码
     */
    public static String encryptPassword(String password) {
        return DigestUtils.md5Hex(password);
    }

    /**
     * 验证密码是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return encryptPassword(rawPassword).equals(encodedPassword);
    }
}

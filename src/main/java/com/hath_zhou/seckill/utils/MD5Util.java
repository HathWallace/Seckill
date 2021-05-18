package com.hath_zhou.seckill.utils;

import org.springframework.stereotype.Component;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * MD5工具类
 *
 * @author HathZhou on 2021/5/9 13:07
 */
@Component
public class MD5Util {
    public static String addSalt(String src, String salt) {
        String str = String.format(
                "%s%s%s%s%s",
                salt.charAt(0),
                salt.charAt(2),
                src,
                salt.charAt(5),
                salt.charAt(4)
        );
        return str;
    }

    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    private static final String salt = "1a2b3c4d";

    public static String inputPassToFromPass(String inputPass) {
        return md5(addSalt(inputPass, salt));
    }

    public static String formPassToDBPass(String formPass, String salt) {
        return md5(addSalt(formPass, salt));
    }

    public static String inputPassToDBPass(String inputPass, String salt) {
        String fromPass = inputPassToFromPass(inputPass);
        String dbPass = formPassToDBPass(fromPass, salt);
        return dbPass;
    }

    public static void main(String[] args) {
        System.out.println(inputPassToFromPass("123456"));
        System.out.println(formPassToDBPass("d3b1294a61a07da9b49b6e22b2cbd7f9", "1a2b3c4d"));
        System.out.println(inputPassToDBPass("123456", "1a2b3c4d"));
        System.out.println("b7797cce01b4b131b433b6acf4add449");
    }
}
package com.hath_zhou.seckill.utils;

import java.util.UUID;

/**
 * UUID工具类
 *
 * @author HathZhou on 2021/5/9 21:37
 */
public class UUIDUtil {
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
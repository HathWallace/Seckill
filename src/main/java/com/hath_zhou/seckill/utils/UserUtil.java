package com.hath_zhou.seckill.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hath_zhou.seckill.mapper.UserMapper;
import com.hath_zhou.seckill.pojo.User;
import com.hath_zhou.seckill.vo.RespBean;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 生成用户工具类
 *
 * @author HathZhou on 2021/5/16 16:15
 */
public class UserUtil {
    /**
     * 生成若干用户
     *
     * @param count
     * @param userMapper
     */
    public static void createUser(int count, UserMapper userMapper) {
        String format = "%0" + Integer.valueOf(count).toString().length() + "d";
        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setId(15800000000L + i);
            user.setNickname("user" + String.format(format, i));
            user.setSalt("1a2b3c4d");
            user.setPassword(MD5Util.inputPassToDBPass("123456", user.getSalt()));
            userMapper.insert(user);
        }
    }

    // /**
    //  * 写入所有cookies
    //  *
    //  * @param userMapper
    //  */
    // public static void writeCookies(UserMapper userMapper) {
    //     try {
    //         //登录，生成userTicket
    //         File file = new File("D:\\2Program\\apache-jmeter-5.3\\windows-start\\test-exaple\\cookie.txt");
    //         if (file.exists()) {
    //             file.delete();
    //         }
    //         RandomAccessFile raf = new RandomAccessFile(file, "rw");
    //         file.createNewFile();
    //         raf.seek(0);
    //
    //         String urlString = "http://localhost:8080/login/doLogin";
    //         List<User> users = userMapper.selectList(null);
    //         for (int i = 0; i < users.size(); i++) {
    //             User user = users.get(i);
    //             URL url = new URL(urlString);
    //             HttpURLConnection co = (HttpURLConnection) url.openConnection();
    //             co.setRequestMethod("POST");
    //             co.setDoOutput(true);
    //
    //             OutputStream out = co.getOutputStream();
    //             String params = "mobile=" + user.getId() + "&password=" + MD5Util.inputPassToFromPass("123456");
    //             out.write(params.getBytes());
    //             out.flush();
    //
    //             InputStream inputStream = co.getInputStream();
    //             ByteArrayOutputStream bout = new ByteArrayOutputStream();
    //             byte buff[] = new byte[1024];
    //             int len = 0;
    //             while ((len = inputStream.read(buff)) >= 0) {
    //                 bout.write(buff, 0, len);
    //             }
    //             inputStream.close();
    //             bout.close();
    //
    //             String response = new String(bout.toByteArray());
    //             ObjectMapper mapper = new ObjectMapper();
    //             RespBean respBean = mapper.readValue(response, RespBean.class);
    //             String userTicket = ((String) respBean.getObj());
    //             System.out.println("create userTicket : " + user.getId());
    //
    //             String row = user.getId() + "," + userTicket;
    //             raf.seek(raf.length());
    //             raf.write(row.getBytes());
    //             raf.write("\r\n".getBytes());
    //             System.out.println("write to file : " + user.getId());
    //         }
    //         raf.close();
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }

    /**
     * 写入所有cookies
     *
     * @param idList
     * @param host
     */
    public static void writeCookies(List<Long> idList, String host) {
        try {
            //登录，生成userTicket
            File file = new File("D:\\2Program\\apache-jmeter-5.3\\windows-start\\test-exaple\\cookie.txt");
            if (file.exists()) {
                file.delete();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            file.createNewFile();
            raf.seek(0);

            String urlString = String.format("http://%s:8080/login/doLogin", host);
            for (int i = 0; i < idList.size(); i++) {
                Long userId = idList.get(i);
                URL url = new URL(urlString);
                HttpURLConnection co = (HttpURLConnection) url.openConnection();
                co.setRequestMethod("POST");
                co.setDoOutput(true);

                OutputStream out = co.getOutputStream();
                String params = String.format(
                        "mobile=%s&password=%s",
                        userId,
                        MD5Util.inputPassToFromPass("123456")
                );
                out.write(params.getBytes());
                out.flush();

                InputStream inputStream = co.getInputStream();
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                byte buff[] = new byte[1024];
                int len = 0;
                while ((len = inputStream.read(buff)) >= 0) {
                    bout.write(buff, 0, len);
                }
                inputStream.close();
                bout.close();

                String response = bout.toString();
                ObjectMapper mapper = new ObjectMapper();
                RespBean respBean = mapper.readValue(response, RespBean.class);
                String userTicket = ((String) respBean.getObj());
                System.out.println("create userTicket : " + userId);

                String row = userId + "," + userTicket;
                raf.seek(raf.length());
                raf.write(row.getBytes());
                raf.write("\r\n".getBytes());
                System.out.println("write to file : " + userId);
            }
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        List<Long> idList = new ArrayList<>();
        for (long i = 0; i < 2000; i++) {
            idList.add(15800000000L + i);
        }
        idList.add(19912345678L);
        writeCookies(idList, "192.168.109.88");
    }
}
package com.hath_zhou.seckill.controller;


import com.hath_zhou.seckill.mapper.UserMapper;
import com.hath_zhou.seckill.pojo.User;
import com.hath_zhou.seckill.utils.UserUtil;
import com.hath_zhou.seckill.vo.RespBean;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hath_zhou
 * @since 2021-05-07
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserMapper userMapper;

    /**
     * 生成用户测试用例
     *
     * @param model
     */
    @RequestMapping("/examples")//http://localhost:8080/user/examples
    @ResponseBody
    public RespBean createUsers(Model model) {
        // UserUtil.createUser(2000, userMapper);
        return RespBean.success();
    }

    // /**
    //  * 获取所有用户的cookie
    //  *
    //  * @param model
    //  * @return
    //  */
    // @RequestMapping("/examples/cookie")//http://localhost:8080/user/examples/cookie
    // @ResponseBody
    // public RespBean getCookies(Model model) {
    //     UserUtil.writeCookies(userMapper);
    //     return RespBean.success();
    // }

    /**
     * 用户信息(测试)
     *
     * @param user
     * @return
     */
    @RequestMapping("/info")//http://localhost:8080/user/info
    @ResponseBody
    public RespBean info(User user) {
        return RespBean.success(user);
    }
}

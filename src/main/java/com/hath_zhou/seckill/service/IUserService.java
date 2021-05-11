package com.hath_zhou.seckill.service;

import com.hath_zhou.seckill.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hath_zhou.seckill.vo.LoginVo;
import com.hath_zhou.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hath_zhou
 * @since 2021-05-07
 */
public interface IUserService extends IService<User> {
    /**
     * 登录
     *
     * @param loginVo
     * @param request
     * @param response
     * @return
     */
    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    /**
     * 根据cookie获取用户
     *
     * @param userTicket
     * @return
     */
    User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);
}

package com.hath_zhou.seckill.service.impl;

import com.hath_zhou.seckill.exception.GlobalException;
import com.hath_zhou.seckill.pojo.User;
import com.hath_zhou.seckill.mapper.UserMapper;
import com.hath_zhou.seckill.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hath_zhou.seckill.utils.CookieUtil;
import com.hath_zhou.seckill.utils.MD5Util;
import com.hath_zhou.seckill.utils.UUIDUtil;
import com.hath_zhou.seckill.vo.LoginVo;
import com.hath_zhou.seckill.vo.RespBean;
import com.hath_zhou.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hath_zhou
 * @since 2021-05-07
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 登录
     *
     * @param loginVo
     * @param request
     * @param response
     * @return
     */
    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();

        //region 通过添加注解省略健壮性判断
        // if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)) {
        //     return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        // }
        // if (!ValidatorUtil.isMobile(mobile)) {
        //     return RespBean.error(RespBeanEnum.MOBILE_ERROR);
        // }
        //endregion

        //根据手机号获取用户
        User user = userMapper.selectById(mobile);
        //判断密码是否正确
        if (user == null || !MD5Util.formPassToDBPass(password, user.getSalt()).equals(user.getPassword())) {
            // return RespBean.error(RespBeanEnum.LOGIN_ERROR);
            // 通过抛出全局异常来输出错误
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }

        //生成cookie
        String ticket = UUIDUtil.uuid();
        // request.getSession().setAttribute(ticket, user);
        // 改用redis存session
        // 将用户信息存入redis中
        redisTemplate.opsForValue().set("user:" + ticket, user);
        CookieUtil.setCookie(request, response, "userTicket", ticket);

        return RespBean.success();
    }

    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isEmpty(userTicket)) {
            return null;
        }
        User user = (User) redisTemplate.opsForValue().get("user:" + userTicket);
        if (user != null) {
            CookieUtil.setCookie(request, response, "userTicket", userTicket);
        }
        return user;
    }
}

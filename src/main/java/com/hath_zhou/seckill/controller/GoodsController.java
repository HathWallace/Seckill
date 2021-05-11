package com.hath_zhou.seckill.controller;

import com.hath_zhou.seckill.pojo.User;
import com.hath_zhou.seckill.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 商品
 *
 * @author HathZhou on 2021/5/9 21:47
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private IUserService userService;

    //region 直接获取User参数，无需写冗余的方法获取request和response
    // /**
    //  * 跳转到商品页面
    //  *
    //  * @param session
    //  * @param model
    //  * @return
    //  */
    // @RequestMapping("/toList")//http://localhost:8080/goods/toList
    // public String toList(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model, @CookieValue("userTicket") String ticket) {
    //     if (StringUtils.isEmpty(ticket)) {
    //         return "login";
    //     }
    //
    //     // User user = (User) session.getAttribute(ticket);
    //     // 改用通过cookie获取user
    //     User user = userService.getUserByCookie(ticket, request, response);
    //     if (user == null) {
    //         return "login";
    //     }
    //
    //     model.addAttribute("user", user);
    //     return "goodsList";
    // }
    //endregion

    /**
     * 跳转到商品页面
     *
     * @param model
     * @param user
     * @return
     */
    @RequestMapping("/toList")//http://localhost:8080/goods/toList
    public String toList(Model model, User user) {
        model.addAttribute("user", user);
        return "goodsList";
    }

}
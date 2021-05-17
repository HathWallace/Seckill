package com.hath_zhou.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hath_zhou.seckill.pojo.Order;
import com.hath_zhou.seckill.pojo.SeckillOrder;
import com.hath_zhou.seckill.pojo.User;
import com.hath_zhou.seckill.service.IGoodsService;
import com.hath_zhou.seckill.service.IOrderService;
import com.hath_zhou.seckill.service.ISeckillOrderService;
import com.hath_zhou.seckill.vo.GoodsVo;
import com.hath_zhou.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.PortUnreachableException;

/**
 * 秒杀
 *
 * @author HathZhou on 2021/5/12 10:16
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController {
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 秒杀
     *
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping("/doSeckill")
    public String doSeckill(Model model, User user, Long goodsId) {
        if (user == null) {
            return "login";
        }
        model.addAttribute("user", user);

        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存
        if (goodsVo.getStockCount() < 1) {
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "seckillFail";
        }
        //判断是否重复抢购
        //region 数据库判断
        // SeckillOrder seckillOrder = seckillOrderService.getOne(
        //         new QueryWrapper<SeckillOrder>()
        //                 .eq("user_id", user.getId())
        //                 .eq("goods_id", goodsId)
        // );
        //endregion
        //redis 判断
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get(
                String.format("order:%d:%d", user.getId(), goodsVo.getId())
        );
        if (seckillOrder != null) {
            model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
            return "seckillFail";
        }

        Order order = orderService.seckill(user, goodsVo);
        model.addAttribute("order", order);
        model.addAttribute("goods", goodsVo);
        return "orderDetail";
    }
}
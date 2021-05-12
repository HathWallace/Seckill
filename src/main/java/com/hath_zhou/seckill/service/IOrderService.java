package com.hath_zhou.seckill.service;

import com.hath_zhou.seckill.pojo.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hath_zhou.seckill.pojo.User;
import com.hath_zhou.seckill.vo.GoodsVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hath_zhou
 * @since 2021-05-11
 */
public interface IOrderService extends IService<Order> {
    /**
     * 秒杀
     * @param user
     * @param goodsVo
     * @return
     */
    Order seckill(User user, GoodsVo goodsVo);
}

package com.hath_zhou.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hath_zhou.seckill.pojo.Order;
import com.hath_zhou.seckill.mapper.OrderMapper;
import com.hath_zhou.seckill.pojo.SeckillGoods;
import com.hath_zhou.seckill.pojo.SeckillOrder;
import com.hath_zhou.seckill.pojo.User;
import com.hath_zhou.seckill.service.IOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hath_zhou.seckill.service.ISeckillGoodsService;
import com.hath_zhou.seckill.service.ISeckillOrderService;
import com.hath_zhou.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hath_zhou
 * @since 2021-05-11
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {
    @Autowired
    private ISeckillGoodsService seckillGoodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 秒杀商品
     *
     * @param user
     * @param goodsVo
     * @return
     */
    @Transactional
    @Override
    public Order seckill(User user, GoodsVo goodsVo) {
        //region 超卖！
        // SeckillGoods seckillGoods = seckillGoodsService.getOne(
        //         new QueryWrapper<SeckillGoods>()
        //                 .eq("goods_id", goodsVo.getId())
        // );
        // seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        // seckillGoodsService.updateById(seckillGoods);
        //endregion

        boolean result = seckillGoodsService.update(
                new UpdateWrapper<SeckillGoods>()
                        .eq("goods_id", goodsVo.getId())
                        .gt("stock_count", 0)
                        .setSql("stock_count = stock_count-1")
        );
        if (!result) return null;

        //region 生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goodsVo.getId());
        order.setDeliverAddrId(0L);
        order.setGoodsName(goodsVo.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(goodsVo.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);
        //endregion


        //region 生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goodsVo.getId());
        seckillOrderService.save(seckillOrder);
        //endregion

        //加入redis，防止同一用户抢同种多个商品
        redisTemplate.opsForValue().set("order:" + user.getId() + ":" + goodsVo.getId(), seckillOrder);

        return order;
    }
}

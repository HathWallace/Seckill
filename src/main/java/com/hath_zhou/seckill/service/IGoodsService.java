package com.hath_zhou.seckill.service;

import com.hath_zhou.seckill.pojo.Goods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hath_zhou.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hath_zhou
 * @since 2021-05-11
 */
public interface IGoodsService extends IService<Goods> {
    /**
     * 获取商品列表
     * @return
     */
    List<GoodsVo> findGoodsVo();

    /**
     * 获取商品详情
     * @param goodsId
     * @return
     */
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}

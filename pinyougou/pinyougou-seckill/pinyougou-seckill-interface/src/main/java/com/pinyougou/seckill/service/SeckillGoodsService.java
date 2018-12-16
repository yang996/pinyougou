package com.pinyougou.seckill.service;

import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

import java.util.List;

public interface SeckillGoodsService extends BaseService<TbSeckillGoods> {

    PageResult search(Integer page, Integer rows, TbSeckillGoods seckillGoods);

    /**
     * 查询全部秒杀商品,首页展示
     * @return
     */
    List<TbSeckillGoods> findList();

    /**
     * 根据id从redis中查询秒杀商品
     * @param id
     * @return
     */
    TbSeckillGoods findOneFromRedis(Long id);
}
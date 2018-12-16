package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillGoodsService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service(interfaceClass = SeckillGoodsService.class)
public class SeckillGoodsServiceImpl extends BaseServiceImpl<TbSeckillGoods> implements SeckillGoodsService {


    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;
    private final static String REDIS_SECKILLGOODS_LIST = "REDIS_SECKILLGOODS_LIST";

    @Override
    public PageResult search(Integer page, Integer rows, TbSeckillGoods seckillGoods) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbSeckillGoods.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(seckillGoods.get***())){
            criteria.andLike("***", "%" + seckillGoods.get***() + "%");
        }*/

        List<TbSeckillGoods> list = seckillGoodsMapper.selectByExample(example);
        PageInfo<TbSeckillGoods> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public List<TbSeckillGoods> findList() {
        List<TbSeckillGoods> seckillGoodsList = null;
        //从redis中查询秒杀商品
        seckillGoodsList = redisTemplate.boundHashOps(REDIS_SECKILLGOODS_LIST).values();
        //redis中没有就到mysql中查找,然后存入redis中
        if (seckillGoodsList == null || seckillGoodsList.size() < 1) {
            Example example = new Example(TbSeckillGoods.class);
            example.createCriteria().andEqualTo("status", "1").
                    andGreaterThan("stockCount", 0).
                    andLessThanOrEqualTo("startTime", new Date()).
                    andGreaterThan("endTime", new Date());
            example.orderBy("startTime");
            seckillGoodsList = seckillGoodsMapper.selectByExample(example);
            //将查找到的商品存入redis中
            if (seckillGoodsList.size() > 0) {
                for (TbSeckillGoods seckillGood : seckillGoodsList) {
                    redisTemplate.boundHashOps(REDIS_SECKILLGOODS_LIST).put(seckillGood.getId(), seckillGood);
                }
            }
        }
        //返回秒杀商品
        return seckillGoodsList;
    }

    @Override
    public TbSeckillGoods findOneFromRedis(Long id) {
        return (TbSeckillGoods) redisTemplate.boundHashOps(REDIS_SECKILLGOODS_LIST).get(id);
    }
}

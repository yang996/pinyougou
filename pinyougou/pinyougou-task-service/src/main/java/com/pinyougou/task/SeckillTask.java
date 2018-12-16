package com.pinyougou.task;

import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName SeckillTask
 * @Author WuYeYang
 * @Description
 * @Date 2018/11/21 20:43
 * @Version 1.0
 **/
@Component
public class SeckillTask {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;


    /**
     * 每分钟执行查询秒杀商品数据库表,
     * 将审核通过的,库存大于0的,开始
     * 时间小于等于当前时间的,结束时间
     * 大于当前时间并且缓存中不存在的
     * 秒杀商品加入缓存中
     */
    @Scheduled(cron = "0 * * * * ?")
    public void refreshSeckillGoods(){

        //查询当前redis中的秒杀商品id集合
        ArrayList ids =
                new ArrayList<>(redisTemplate.boundHashOps("REDIS_SECKILLGOODS_LIST").keys());
        Example example=new Example(TbSeckillGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status","1");
        criteria.andGreaterThan("stockCount",0);
        criteria.andLessThanOrEqualTo("startTime",new Date());
        criteria.andGreaterThan("endTime",new Date());
        if (ids.size()>0){
            criteria.andNotIn("id",ids);
        }
        List<TbSeckillGoods> seckillGoods = seckillGoodsMapper.selectByExample(example);
        if (seckillGoods!=null && seckillGoods.size()>0){
            for (TbSeckillGoods seckillGood : seckillGoods) {
                redisTemplate.boundHashOps("REDIS_SECKILLGOODS_LIST").put(seckillGood.getId(),seckillGood);
            }
            System.out.println("已将"+seckillGoods.size()+"条秒杀商品加入缓存中");
        }
    }


    /**
     * 每分钟都去检查redis中的商品是否过期
     * 如果过期则从redis中移除秒杀商品并且
     * 将该商品更新到数据库中
     */
    @Scheduled(cron = "0 * * * * ?")
    public void removeSeckillGoods(){
        List<TbSeckillGoods> seckillgoodsList =
                redisTemplate.boundHashOps("REDIS_SECKILLGOODS_LIST").values();
        if (seckillgoodsList!=null && seckillgoodsList.size()>0){
            for (TbSeckillGoods seckillGood : seckillgoodsList) {
                if (seckillGood.getEndTime().getTime()<new Date().getTime()){
                    //保存到数据库
                    seckillGoodsMapper.updateByPrimaryKeySelective(seckillGood);
                    //将该商品从redis中删除
                    redisTemplate.boundHashOps("REDIS_SECKILLGOODS_LIST").delete(seckillGood.getId());
                    System.out.println("移除秒杀商品"+seckillGood.getId());
                }
            }
        }
    }
}

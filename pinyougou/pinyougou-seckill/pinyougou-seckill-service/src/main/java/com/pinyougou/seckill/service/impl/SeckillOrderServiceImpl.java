package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.common.util.RedisLock;
import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.mapper.SeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service(interfaceClass = SeckillOrderService.class, timeout = 30000)
public class SeckillOrderServiceImpl extends BaseServiceImpl<TbSeckillOrder> implements SeckillOrderService {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private IdWorker idWorker;


    private final static String REDIS_SECKILLGOODS_LIST = "REDIS_SECKILLGOODS_LIST";
    private final static String REDIS_SECKILLORDERS = "REDIS_SECKILLORDERS";


    @Override
    public PageResult search(Integer page, Integer rows, TbSeckillOrder seckillOrder) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbSeckillOrder.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(seckillOrder.get***())){
            criteria.andLike("***", "%" + seckillOrder.get***() + "%");
        }*/

        List<TbSeckillOrder> list = seckillOrderMapper.selectByExample(example);
        PageInfo<TbSeckillOrder> pageInfo = new PageInfo<>(list);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public Long submitOrder(String username, Long seckillId) throws InterruptedException {
        RedisLock redisLock = new RedisLock(redisTemplate);
        //获得分布式锁
        if (redisLock.lock(seckillId.toString())) {
            //1、从redis中获取秒杀商品；判断商品是否存在，库存是否大于0
            TbSeckillGoods seckillGoods
                    = (TbSeckillGoods) redisTemplate.boundHashOps(REDIS_SECKILLGOODS_LIST).get(seckillId);
            if (seckillGoods != null && seckillGoods.getStockCount() > 0) {
                // 2、秒杀商品的库存减1；
                seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
                // 2.1、如果库存大于0，则需要更新秒杀商品到redis
                if (seckillGoods.getStockCount() > 0) {
                    redisTemplate.boundHashOps(REDIS_SECKILLGOODS_LIST).put(seckillId, seckillGoods);
                } else {
                    // 2.2、如果库存等于0，则需要将redis中的秒杀商品更新回mysql；并删除在redis中的秒杀商品
                    seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
                    redisTemplate.boundHashOps(REDIS_SECKILLGOODS_LIST).delete(seckillGoods);
                }
            } else {
                throw new RuntimeException("库存为0或商品不存在");
            }
            //释放分布式锁
            redisLock.unlock(seckillId.toString());
            // 3、生成秒杀订单，并存入redis
            TbSeckillOrder seckillOrder = new TbSeckillOrder();
            Long orderId = idWorker.nextId();
            seckillOrder.setId(orderId);
            seckillOrder.setCreateTime(new Date());
            seckillOrder.setMoney(seckillGoods.getCostPrice());
            seckillOrder.setSeckillId(seckillGoods.getId());
            seckillOrder.setSellerId(seckillGoods.getSellerId());
            seckillOrder.setUserId(username);
            seckillOrder.setStatus("0");
            redisTemplate.boundHashOps(REDIS_SECKILLORDERS).put(orderId.toString(), seckillOrder);
            // 4、返回订单id
            return orderId;
        }
        return null;
    }

    @Override
    public TbSeckillOrder getSeckillOrderInRedisByOrderId(String outTradeNo) {
        return (TbSeckillOrder) redisTemplate.boundHashOps(REDIS_SECKILLORDERS).get(outTradeNo);
    }

    @Override
    public void updateOrderStatus(String outTradeNo, String transaction_id) {
        //将订单存入mysql
        TbSeckillOrder seckillOrder = getSeckillOrderInRedisByOrderId(outTradeNo);
        if (seckillOrder==null){
            throw new RuntimeException("订单不存在");
        }
        if (!outTradeNo.equals(seckillOrder.getId().toString())){
            throw new RuntimeException("订单不相符");
        }
        seckillOrder.setStatus("1");
        seckillOrder.setPayTime(new Date());
        seckillOrder.setTransactionId(transaction_id);
        seckillOrderMapper.insertSelective(seckillOrder);
        //删除redis中的订单
        redisTemplate.boundHashOps(REDIS_SECKILLORDERS).delete(outTradeNo);
    }

    @Override
    public void deleteRedisOrder(String outTradeNo) throws  Exception{
        TbSeckillOrder seckillOrder = getSeckillOrderInRedisByOrderId(outTradeNo);
        if (seckillOrder!=null && outTradeNo.equals(seckillOrder.getId().toString())){
            //删除redis中的秒杀订单
            redisTemplate.boundHashOps(REDIS_SECKILLORDERS).delete(outTradeNo);
            //更新redis中对应商品的库存
            RedisLock redisLock = new RedisLock(redisTemplate);
            if (redisLock.lock(seckillOrder.getSeckillId().toString())){
                TbSeckillGoods seckillGoods
                        = (TbSeckillGoods) redisTemplate.boundHashOps(REDIS_SECKILLGOODS_LIST).get(seckillOrder.getSellerId());
                if (seckillGoods==null){
                    //如果redis中不存在,则从数据库中找出来
                    seckillGoods=seckillGoodsMapper.selectByPrimaryKey(seckillOrder.getSeckillId());
                }
                //对应秒杀商品的库存+1
                seckillGoods.setStockCount(seckillGoods.getStockCount()+1);
                //更新redis中的秒杀商品
                redisTemplate.boundHashOps(REDIS_SECKILLGOODS_LIST).put(seckillGoods.getId(),seckillGoods);
                //释放分布式锁
                redisLock.unlock(seckillOrder.getSeckillId().toString());
            }
        }
    }
}

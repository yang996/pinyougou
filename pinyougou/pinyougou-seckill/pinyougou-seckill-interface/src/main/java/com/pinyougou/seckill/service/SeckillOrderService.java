package com.pinyougou.seckill.service;

import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

public interface SeckillOrderService extends BaseService<TbSeckillOrder> {

    PageResult search(Integer page, Integer rows, TbSeckillOrder seckillOrder);

    /**
     * 提交订单
     * @param username 用户名
     * @param seckillId 秒杀商品id
     * @return
     */
    Long submitOrder(String username, Long seckillId) throws InterruptedException;

    /**
     * 根据订单id从redis中查询订单
     * @param outTradeNo
     * @return
     */
    TbSeckillOrder getSeckillOrderInRedisByOrderId(String outTradeNo);

    /**
     * 用户支付成功,更新订单状态
     * @param outTradeNo
     */
    void updateOrderStatus(String outTradeNo,String transaction_id);

    /**
     * 如果关闭了订单,则需要将redis中的订单删除
     * @param outTradeNo
     */
    void deleteRedisOrder(String outTradeNo)throws  Exception;
}
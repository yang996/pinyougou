package com.pinyougou.order.service;

import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

import java.util.Map;

public interface OrderService extends BaseService<TbOrder> {

    PageResult search(Integer page, Integer rows, TbOrder order);

    /**
     * 用户提交订单
     * @param order 订单信息
     * @return
     */
    String addOrder(TbOrder order);

    /**
     * 查询支付日志
     * @param outTradeNo
     * @return
     */
    TbPayLog findPayLogByOutTradeNO(String outTradeNo);

    /**
     * 用户支付成功,更新订单和支付日志状态
     * @param outTradeNo
     */
    void updateOrderStatus(String outTradeNo,String transaction_id);

    /**
     * 根据商家id分页查询订单信息
     * @return
     */
    PageResult findOrderList(Integer page,Integer rows,TbOrder orders);

    /**
     * 根据用户id查询订单
     * @param userId
     * @return
     */
    Map<String,Object> getOrderList(String userId,Map<String,Object> searchMap);

    /**
     * 取消订单
     * @param orderId
     */
    void cancelOrder(Long orderId);
}
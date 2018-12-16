package com.pinyougou.pay.service;

import java.util.Map;

public interface WeixinPayService {
    /**
     * 生成二维码等信息
     * @param outTradeNo 支付日志id
     * @param totalFee 订单金额
     * @return
     */
    Map<String,Object> createNative(String outTradeNo, String totalFee);

    /**
     * 查询用户支付成功或者失败
     * @param outTradeNo 支付日志id
     * @return 支付结果
     */
    Map<String,String> queryPayStatus(String outTradeNo);

    /**
     * 超过一分钟未支付,关闭订单
     * @param outTradeNo 订单编号
     * @return
     */
    Map<String,String> closeOrder(String outTradeNo);
}
